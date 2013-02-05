/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.kernel.dms.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.dms.DMEntityFactory;
import org.kimios.kernel.dms.DMEntityImpl;
import org.kimios.kernel.dms.DMEntityType;
import org.kimios.kernel.dms.Document;
import org.kimios.kernel.dms.FactoryInstantiator;
import org.kimios.kernel.dms.Folder;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HDMEntityFactory extends HFactory implements DMEntityFactory
{
    private static Logger log = LoggerFactory.getLogger(DMEntityFactory.class);

    public DMEntity getEntity(long dmEntityUid) throws ConfigException, DataSourceException
    {
        try {
            return (DMEntity) getSession().createCriteria(DMEntityImpl.class)
                    .add(Restrictions.eq("uid", new Long(dmEntityUid)))
                    .uniqueResult();
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public DMEntity getEntity(long dmEntityUid, int dmEntityType) throws ConfigException, DataSourceException
    {
        try {
            return (DMEntity) getSession().createCriteria(DMEntityImpl.class)
                    .add(Restrictions.eq("uid", dmEntityUid))
                    .add(Restrictions.eq("type", dmEntityType))
                    .uniqueResult();
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public DMEntity getEntity(String path) throws ConfigException, DataSourceException
    {
        try {
            Criteria c = getSession().createCriteria(DMEntityImpl.class)
                    .add(Restrictions.like("path", path, MatchMode.EXACT));
            return (DMEntity) c.uniqueResult();
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public List<DMEntity> getEntities(String path) throws ConfigException, DataSourceException
    {
        try {
            String finalPath = path != null ? path : "";
            if (finalPath.endsWith("/")) {
                finalPath += "%";
            } else {
                finalPath += "/%";
            }

            return getSession().createCriteria(DMEntityImpl.class)
                    .add(Restrictions.like("path", finalPath, MatchMode.START))
                    .list();
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public List<DMEntityImpl> getEntitiesImpl(String path) throws ConfigException, DataSourceException
    {
        try {
            String finalPath = path != null ? path : "";
            if (finalPath.endsWith("/")) {
                finalPath += "%";
            } else {
                finalPath += "/%";
            }

            return getSession().createCriteria(DMEntityImpl.class)
                    .add(Restrictions.like("path", finalPath, MatchMode.START))
                    .list();
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public List<DMEntity> getEntitiesByPathAndType(String path, int dmEntityType)
            throws ConfigException, DataSourceException
    {
        try {
            String finalPath = path != null ? path : "";
            if (finalPath.endsWith("/")) {
                finalPath += "%";
            } else {
                finalPath += "/%";
            }

            return getSession().createCriteria(DMEntityImpl.class)
                    .add(Restrictions.like("path", finalPath, MatchMode.START))
                    .add(Restrictions.eq("type", dmEntityType))
                    .list();
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public void deteteEntities(String path) throws ConfigException, DataSourceException
    {
        try {
            /*
                Update versions
             */
            String updateVersions =
                    "update DocumentVersion set documentUid = null where documentUid in (select uid from DMEntityImpl where " +
                            "path like :pathExact or path like :pathExt and type = 3)";

            getSession().createQuery(updateVersions)
                    .setString("pathExact", path)
                    .setString("pathExt", path + "/%")
                    .executeUpdate();

            FactoryInstantiator.getInstance().getMetaValueFactory().cleanMetaValues();

            String cleanRelatedDocuments =
                    "delete from related_documents where document_id in (select dm_entity_id from dm_entity " +
                            "where dm_entity_path like :pathExact or dm_entity_path like :pathExt and dm_entity_type = 3) " +
                            "or related_document_id in (select dm_entity_id from dm_entity " +
                            "where dm_entity_path like :pathExact or dm_entity_path like :pathExt and dm_entity_type = 3)";

            getSession().createSQLQuery(cleanRelatedDocuments)
                    .setString("pathExact", path)
                    .setString("pathExt", path + "/%")
                    .executeUpdate();

            getSession().createQuery("delete from DMEntityImpl where path like :pathExact or path like :pathExt")
                    .setString("pathExact", path)
                    .setString("pathExt", path + "/%")
                    .executeUpdate();
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public void updateEntity(DMEntityImpl entity) throws ConfigException,
            DataSourceException
    {
        try {
            getSession().update(entity);
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public void generatePath(DMEntity entity) throws ConfigException, DataSourceException
    {

        DMEntity p = entity;
        StringBuilder path = new StringBuilder();
        switch (p.getType()) {
            case DMEntityType.WORKSPACE:
                path.append(DMEntityImpl.DM_PATH_SEPARATOR);
                path.append(p.getName());
                break;

            case DMEntityType.FOLDER:
                path.append(DMEntityImpl.DM_PATH_SEPARATOR);
                path.append(p.getName());
                p = initializeAndUnproxy(p);
                p = ((Folder) p).getParent();
                while (p != null) {
                    path.insert(0, DMEntityImpl.DM_PATH_SEPARATOR);
                    path.insert(1, p.getName());
                    if (p.getType() == DMEntityType.FOLDER) {
                        p = initializeAndUnproxy(p);
                        p = ((Folder) p).getParent();
                    } else {
                        p = null;
                    }
                }
                break;
            case DMEntityType.DOCUMENT:
                path.append(DMEntityImpl.DM_PATH_SEPARATOR);
                path.append(p.getName());
                p = initializeAndUnproxy(p);
                if (((Document) p).getExtension() != null) {
                    path.append(DMEntityImpl.DM_EXTENSION_SEPARATOR);
                    path.append(((Document) p).getExtension().toLowerCase());
                }
                p = ((Document) p).getFolder();
                while (p != null) {
                    path.insert(0, DMEntityImpl.DM_PATH_SEPARATOR);
                    path.insert(1, p.getName());
                    if (p.getType() == DMEntityType.FOLDER) {
                        p = initializeAndUnproxy(p);
                        p = ((Folder) p).getParent();
                    } else {
                        p = null;
                    }
                }
                break;
        }
        log.debug("Entity " + entity.getUid() + " generated path " + path);
        entity.setPath(path.toString());
    }

    public void updatePath(DMEntity entity, String newName) throws ConfigException, DataSourceException
    {
        DMEntity p = entity;
        String oldPath = p.getPath();
        /*
        * Setting name
        */
        p.setName(newName);
        /*
        * Generate new path
        */
        StringBuilder path = new StringBuilder();
        switch (p.getType()) {
            case DMEntityType.WORKSPACE:
                path.append(DMEntityImpl.DM_PATH_SEPARATOR);
                path.append(p.getName());
                break;

            case DMEntityType.FOLDER:
                path.append(DMEntityImpl.DM_PATH_SEPARATOR);
                path.append(p.getName());
                p = initializeAndUnproxy(p);
                p = ((Folder) p).getParent();
                while (p != null) {
                    path.insert(0, DMEntityImpl.DM_PATH_SEPARATOR);
                    path.insert(1, p.getName());
                    if (p.getType() == DMEntityType.FOLDER) {
                        p = initializeAndUnproxy(p);
                        p = ((Folder) p).getParent();
                    } else {
                        p = null;
                    }
                }
                break;
            case DMEntityType.DOCUMENT:
                path.append(DMEntityImpl.DM_PATH_SEPARATOR);
                path.append(p.getName());
                p = initializeAndUnproxy(p);
                if (((Document) p).getExtension() != null) {
                    path.append(DMEntityImpl.DM_EXTENSION_SEPARATOR);
                    path.append(((Document) p).getExtension().toLowerCase());
                }
                p = ((Document) p).getFolder();
                while (p != null) {
                    path.insert(0, DMEntityImpl.DM_PATH_SEPARATOR);
                    path.insert(1, p.getName());
                    if (p.getType() == DMEntityType.FOLDER) {
                        p = initializeAndUnproxy(p);
                        p = ((Folder) p).getParent();
                    } else {
                        p = null;
                    }
                }
                break;
        }
        log.debug("oldPath: " + oldPath + " / newPath: " + path);
        entity.setPath(path.toString());
        /*
        * Update entity path
        */
        String query = "update DMEntityImpl set path = " +
                ":newPath where path = :oldPath";

        getSession()
                .createQuery(query)
                .setString("oldPath", oldPath)
                .setString("newPath", entity.getPath())
                .executeUpdate();

        /*
        * Update children path
        */
        if (entity.getType() != 3) {
            query = "update DMEntityImpl set path = " +
                    "concat(:newPath,substring(path, :oldPathLength + 1, length(path))) " +
                    "where substring(path,1, :oldPathLength) = :oldPath";
            getSession()
                    .createQuery(query)
                    .setInteger("oldPathLength", oldPath.length() + 1)
                    .setString("oldPath", oldPath + "/")
                    .setString("newPath", entity.getPath() + "/")
                    .executeUpdate();
        }
    }
}

