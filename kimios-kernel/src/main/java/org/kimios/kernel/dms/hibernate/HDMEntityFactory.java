/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2015  DevLib'
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
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.kernel.dms.hibernate;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.*;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.*;
import org.kimios.kernel.dms.model.*;
import org.kimios.exceptions.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

;import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class HDMEntityFactory extends HFactory implements DMEntityFactory {
    private static Logger log = LoggerFactory.getLogger(DMEntityFactory.class);

    public DMEntity getEntity(long dmEntityUid) throws ConfigException, DataSourceException {
        try {
            return (DMEntity) getSession().createCriteria(DMEntityImpl.class)
                    .add(Restrictions.eq("uid", new Long(dmEntityUid)))
                    .uniqueResult();
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public DMEntity getEntity(long dmEntityUid, int dmEntityType) throws ConfigException, DataSourceException {
        try {
            return (DMEntity) getSession().createCriteria(DMEntityImpl.class)
                    .add(Restrictions.eq("uid", dmEntityUid))
                    .add(Restrictions.eq("type", dmEntityType))
                    .uniqueResult();
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public DMEntity getEntity(String path) throws ConfigException, DataSourceException {
        try {
            Criteria c = getSession().createCriteria(DMEntityImpl.class)
                    .add(Restrictions.like("path", path, MatchMode.EXACT));
            return (DMEntity) c.uniqueResult();
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public List<DMEntity> getEntities(String path) throws ConfigException, DataSourceException {
        try {
            String finalPath = path != null ? path : "";
            if (finalPath.endsWith("/")) {
                finalPath += "%";
            } else {
                finalPath += "/%";
            }

            List<Long> items = getSession().createCriteria(DMEntityImpl.class)
                    .add(Restrictions.like("path", finalPath, MatchMode.START))
                    .setProjection(Projections.distinct(Projections.id()))
                    .list();


            if (items.size() > 0) {
                return getSession().createCriteria(DMEntityImpl.class)
                        .add(Property.forName("uid").in(items))
                        .list();
            } else {
                return new ArrayList<DMEntity>();
            }


        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public List<DMEntityImpl> getEntitiesImpl(String path) throws ConfigException, DataSourceException {
        try {
            String finalPath = path != null ? path : "";
            if (finalPath.endsWith("/")) {
                finalPath += "%";
            } else {
                finalPath += "/%";
            }

            List<Long> items = getSession().createCriteria(DMEntityImpl.class)
                    .add(Restrictions.like("path", finalPath, MatchMode.START))
                    .setProjection(Projections.distinct(Projections.id()))
                    .list();

            if (items.size() > 0) {
                return getSession().createCriteria(DMEntityImpl.class)
                        .add(Property.forName("uid").in(items))
                        .list();
            } else {
                return new ArrayList<DMEntityImpl>();
            }

        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public List<DMEntity> getEntitiesByPathAndType(String path, int dmEntityType)
            throws ConfigException, DataSourceException {
        try {
            String finalPath = path != null ? path : "";
            if (finalPath.endsWith("/")) {
                finalPath += "%";
            } else {
                finalPath += "/%";
            }


            /**/


            List<Long> items = getSession().createCriteria(DMEntityImpl.class)
                    .add(Restrictions.like("path", finalPath, MatchMode.START))
                    .add(Restrictions.eq("type", dmEntityType))
                    .setProjection(Projections.distinct(Projections.id()))
                    .list();

            if (items.size() > 0) {
                return getSession().createCriteria(DMEntityImpl.class)
                        .add(Property.forName("uid").in(items))
                        .list();
            } else {
                return new ArrayList<DMEntity>();
            }
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }


    public Long getEntitiesByPathAndTypeCount(String path, int dmEntityType)
            throws ConfigException, DataSourceException {
        try {
            String finalPath = path != null ? path : "";
            if (finalPath.endsWith("/")) {
                finalPath += "%";
            } else {
                finalPath += "/%";
            }

            return (Long) getSession().createCriteria(DMEntityImpl.class)
                    .add(Restrictions.like("path", finalPath, MatchMode.START))
                    .add(Restrictions.eq("type", dmEntityType))
                    .setProjection(Projections.rowCount()).uniqueResult();
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }


    public Long getEntitiesByPathAndTypeCount(String path, int dmEntityType, List<Long> excludedIds, List<String> excludedExtensions)
            throws ConfigException, DataSourceException {
        try {
            String finalPath = path != null ? path : "";
            if (finalPath.endsWith("/")) {
                finalPath += "%";
            } else {
                finalPath += "/%";
            }

            Criteria criteria = getSession().createCriteria(DMEntityImpl.class)
                    .add(Restrictions.like("path", finalPath, MatchMode.START))
                    .add(Restrictions.eq("type", dmEntityType));

            if (excludedIds != null && excludedIds.size() > 0) {
                criteria.add(Restrictions.not(Restrictions.in("uid", excludedIds)));
            }
            if (excludedExtensions != null && excludedExtensions.size() > 0) {
                criteria.add(Restrictions.not(Restrictions.in("extension", excludedExtensions)));
            }
            criteria.setProjection(Projections.rowCount()).uniqueResult();
            return (Long)criteria.uniqueResult();
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }


    public List<DMEntity> getEntitiesByPathAndType(String path, int dmEntityType, int start, int count,
                                                   List<Long> excludedIds, List<String> excludedExtensions)
            throws ConfigException, DataSourceException {
        try {
            String finalPath = path != null ? path : "";
            if (finalPath.endsWith("/")) {
                finalPath += "%";
            } else {
                finalPath += "/%";
            }


            /*String sql = "select distinct d.dm_entity_id as uid, {d.*} " +
                    "from dm_entity d where d.dm_entity_type = :dmtype and d.dm_entity_path like :fpath order by d.creation_date offset " + start + " limit " + count;*/

            Criteria criteria = getSession().createCriteria(DMEntityImpl.class);
            criteria.setProjection(Projections.distinct(Projections.id()))
                    .add(Restrictions.like("path", finalPath, MatchMode.START))
                    .add(Restrictions.eq("type", dmEntityType))
                    .addOrder(Order.asc("uid"));


            if (excludedIds != null && excludedIds.size() > 0) {
                criteria.add(Restrictions.not(Restrictions.in("uid", excludedIds)));
            }
            if (excludedExtensions != null && excludedExtensions.size() > 0) {
                criteria.add(Restrictions.not(Restrictions.in("extension", excludedExtensions)));
            }


            criteria.setFirstResult(start)
                    .setMaxResults(count);
            List uniqueSubList = criteria.list();


            if (uniqueSubList.size() > 0) {
                criteria.setProjection(null);
                criteria.setFirstResult(0);
                criteria.setMaxResults(Integer.MAX_VALUE);
                criteria.add(Restrictions.in("uid", uniqueSubList));
                criteria.addOrder(Order.asc("uid"));
                criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

                List finalItems =  criteria.list();


                if(finalItems.size() != uniqueSubList.size()){
                    log.info("WARNING !!! Difference betweeeeen ids Count and entities : {} against {}",uniqueSubList.size(), finalItems.size());
                }

                return finalItems;


            } else {
                return new ArrayList<DMEntity>();
            }

            /*getSession().createSQLQuery(sql)
                        .addEntity("d", Document.class)
                        .addJoin()
                        .setString("fpath", finalPath)
                        .setInteger("type", dmEntityType)
                        .*/

        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public List<DMEntity> getEntitiesByPathAndType(String path, int dmEntityType, int start, int count)
            throws ConfigException, DataSourceException {
        try {
            String finalPath = path != null ? path : "";
            if (finalPath.endsWith("/")) {
                finalPath += "%";
            } else {
                finalPath += "/%";
            }


            /*String sql = "select distinct d.dm_entity_id as uid, {d.*} " +
                    "from dm_entity d where d.dm_entity_type = :dmtype and d.dm_entity_path like :fpath order by d.creation_date offset " + start + " limit " + count;*/

            Criteria criteria = getSession().createCriteria(DMEntityImpl.class);
            criteria.setProjection(Projections.distinct(Projections.id()))
                    .add(Restrictions.like("path", finalPath, MatchMode.START))
                    .add(Restrictions.eq("type", dmEntityType))
                    .addOrder(Order.asc("uid"))
                    .setFirstResult(start)
                    .setMaxResults(count);
            List uniqueSubList = criteria.list();


            if (uniqueSubList.size() > 0) {
                criteria.setProjection(null);
                criteria.setFirstResult(0);
                criteria.setMaxResults(Integer.MAX_VALUE);
                criteria.add(Restrictions.in("uid", uniqueSubList));
                criteria.addOrder(Order.asc("uid"));
                criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

                List finalItems =  criteria.list();


                if(finalItems.size() != uniqueSubList.size()){
                    log.info("WARNING !!! Difference betweeeeen ids Count and entities : {} against {}",uniqueSubList.size(), finalItems.size());
                }


                return finalItems;


            } else {
                return new ArrayList<DMEntity>();
            }

            /*getSession().createSQLQuery(sql)
                        .addEntity("d", Document.class)
                        .addJoin()
                        .setString("fpath", finalPath)
                        .setInteger("type", dmEntityType)
                        .*/

        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }


    public void deleteEntities(String path) throws ConfigException, DataSourceException {
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
                    "DELETE FROM related_documents WHERE document_id IN (SELECT dm_entity_id FROM dm_entity " +
                            "WHERE dm_entity_path LIKE :pathExact OR dm_entity_path LIKE :pathExt AND dm_entity_type = 3) " +
                            "OR related_document_id IN (SELECT dm_entity_id FROM dm_entity " +
                            "WHERE dm_entity_path LIKE :pathExact OR dm_entity_path LIKE :pathExt AND dm_entity_type = 3)";

            getSession().createSQLQuery(cleanRelatedDocuments)
                    .setString("pathExact", path)
                    .setString("pathExt", path + "/%")
                    .executeUpdate();

            String cleanAttributes =
                    "DELETE FROM dm_entity_attributes " +
                            "WHERE dm_entity_id IN " +
                            "(SELECT dm_entity_id FROM dm_entity WHERE dm_entity_path LIKE :pathExact OR dm_entity_path LIKE :pathExt)";

            getSession().createSQLQuery(cleanAttributes)
                    .setString("pathExact", path)
                    .setString("pathExt", path + "/%")
                    .executeUpdate();

            String cleanSharesRules =
                    "DELETE FROM dm_security_rules"
                            + " WHERE dm_entity_share_id IN "
                            + "(SELECT id FROM dm_entity_share INNER JOIN dm_entity ON dm_entity_share.dm_entity_id = dm_entity.dm_entity_id "
                            + " WHERE dm_entity.dm_entity_path LIKE :pathExact OR dm_entity_path LIKE :pathExt)";
            getSession().createSQLQuery(cleanSharesRules)
                    .setString("pathExact", path)
                    .setString("pathExt", path + "/%")
                    .executeUpdate();

            String cleanShares =
                    "DELETE FROM dm_entity_share"
                    + " WHERE dm_entity_id IN "
                    + "(SELECT dm_entity_id FROM dm_entity WHERE dm_entity_path LIKE :pathExact OR dm_entity_path LIKE :pathExt)";
            getSession().createSQLQuery(cleanShares)
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
            DataSourceException {
        try {
            getSession().update(entity);
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public void generatePath(DMEntity entity) throws ConfigException, DataSourceException {

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

                if(((Folder)p).getParent() == null){
                    p = this.getEntity(((Folder)entity).getParentUid());
                } else {
                    p = ((Folder) p).getParent();
                }
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

    public void updatePath(DMEntity entity, String newName) throws ConfigException, DataSourceException {
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

    public static String TRASH_PREFIX = "__TRASHED_ENTITY__";


    public List<DMEntity> listTrashedEntities(Integer start, Integer count) throws ConfigException, DataSourceException {

        Criteria criteria = getSession().createCriteria(DMEntityImpl.class);
        criteria.add(Restrictions.like("path", TRASH_PREFIX + "%", MatchMode.START))
                .add(Restrictions.eq("trashed", true))
                .add(Restrictions.in("type", new Object[]{DMEntityType.WORKSPACE, DMEntityType.FOLDER}));

        List<DMEntityImpl> trashedContainers = criteria.list();

        Criteria fCriteria = getSession().createCriteria(DMEntityImpl.class);
        fCriteria.setProjection(Projections.distinct(Projections.id()))
                .add(Restrictions.like("path", TRASH_PREFIX + "%", MatchMode.START))
                .add(Restrictions.eq("trashed", true))
                .add(Restrictions.in("type", new Object[]{DMEntityType.WORKSPACE, DMEntityType.FOLDER, DMEntityType.DOCUMENT}));

        //exclude parent path
        for(DMEntityImpl tr: trashedContainers){
            fCriteria = fCriteria.add(Restrictions.not(Restrictions.like("path", tr.getPath() + "/%", MatchMode.START)));
            log.info("excluding path {}", tr.getPath() + "/%");
        }


        if (start != null && count != null) {
            fCriteria.setFirstResult(start);
            fCriteria.setMaxResults(count);
        }


        List uniqueSubList = fCriteria.list();
        log.info("found items: {}", uniqueSubList);
        if (uniqueSubList.size() > 0) {
            fCriteria.setProjection(null);
            fCriteria.setFirstResult(0);
            fCriteria.setMaxResults(Integer.MAX_VALUE);
            fCriteria.add(Restrictions.in("uid", uniqueSubList));
            fCriteria.addOrder(Order.desc("updateDate"));
            fCriteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
            List<DMEntity> items =  fCriteria.list();
            Collections.sort(items, new Comparator<DMEntity>() {
                @Override
                public int compare(DMEntity o1, DMEntity o2) {
                    return ((DMEntityImpl)o2).getUpdateDate().compareTo(((DMEntityImpl)o1).getUpdateDate());
                }
            });
            log.info("retuning trashed items: {}", items);
            return items;
        } else {
            return new ArrayList<DMEntity>();
        }
    }

    public void trash(DMEntityImpl entity) throws ConfigException, DataSourceException {
        if (entity.getType() == DMEntityType.DOCUMENT) {
            DMEntity p = entity;
            String oldPath = p.getPath();
            entity.setPath(TRASH_PREFIX + oldPath);
            entity.setUpdateDate(new Date());
            entity.setTrashed(true);
            getSession().saveOrUpdate(entity);
            getSession().flush();
        }  else if( entity.getType() == DMEntityType.FOLDER || entity.getType() == DMEntityType.WORKSPACE){
            Date updateDate = new Date();
            getSession().createQuery("update DMEntityImpl set trashed = true, updateDate = :upDate, path = " +
                    "concat('" + TRASH_PREFIX + "', path) where path like :pathExact or path like :pathExt ")
                    .setDate("upDate", updateDate)
                    .setString("pathExact", entity.getPath())
                    .setString("pathExt", entity.getPath() + "/%")
                    .executeUpdate();

            getSession().flush();
        }
    }

    public void untrash(DMEntityImpl entity) throws ConfigException, DataSourceException {
        if (entity.getType() == DMEntityType.DOCUMENT) {
            entity.setPath(entity.getPath().replaceAll(TRASH_PREFIX, ""));
            entity.setUpdateDate(new Date());
            entity.setTrashed(false);
            getSession().saveOrUpdate(entity);
            getSession().flush();
        }  else if( entity.getType() == DMEntityType.FOLDER || entity.getType() == DMEntityType.WORKSPACE){

            Date updateDate = new Date();
            getSession().createQuery("update DMEntityImpl set trashed = false , updateDate = :upDate, path = " +
                    "substring(path," + (TRASH_PREFIX.length()  + 1) + ", length(path) - " + TRASH_PREFIX.length() + ") " +
                    "where path like :pathExact or path like :pathExt ")
                    .setDate("upDate", updateDate)
                    .setString("pathExact", entity.getPath())
                    .setString("pathExt", entity.getPath() + "/%")
                    .executeUpdate();

            getSession().flush();
        }
    }


    public List<DMEntity> getEntitiesFromIds(List<Long> listIds, int dmEntityType) throws ConfigException, DataSourceException
    {
        List<DMEntity> lists = null;
        if (listIds.size() > 0) {
            try {

                Criteria criteria = getSession().createCriteria(DMEntityImpl.class);
                criteria.setProjection(Projections.distinct(Projections.id()))
                        .add(Restrictions.in("uid", listIds))
                        .add(Restrictions.eq("type", dmEntityType))
                        .addOrder(Order.asc("uid"));
                List uniqueSubList = criteria.list();


                if (uniqueSubList.size() > 0) {
                    criteria.setProjection(null);
                    criteria.setFirstResult(0);
                    criteria.setMaxResults(Integer.MAX_VALUE);
                    criteria.add(Restrictions.in("uid", uniqueSubList));
                    criteria.addOrder(Order.asc("uid"));
                    criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

                    List finalItems =  criteria.list();


                    if(finalItems.size() != uniqueSubList.size()){
                        log.info("WARNING !!! Difference betweeeeen ids Count and entities : {} against {}",uniqueSubList.size(), finalItems.size());
                    }


                    return finalItems;


                } else {
                    return new ArrayList<DMEntity>();
                }
            } catch (HibernateException he) {
                throw new DataSourceException(he, he.getMessage());
            }
        } else {
            return new ArrayList<DMEntity>();
        }
    }
}

