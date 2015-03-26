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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.Document;
import org.kimios.kernel.dms.DocumentType;
import org.kimios.kernel.dms.DocumentVersion;
import org.kimios.kernel.dms.DocumentVersionFactory;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class HDocumentVersionFactory extends HFactory implements DocumentVersionFactory
{
    public void deleteDocumentVersion(DocumentVersion v) throws ConfigException, DataSourceException
    {
        try {
            getSession().delete(v);
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public DocumentVersion getDocumentVersion(long uid) throws ConfigException, DataSourceException
    {
        try {
            DocumentVersion v = (DocumentVersion) getSession().get(DocumentVersion.class, uid);
            return v;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public DocumentVersion getLastDocumentVersion(Document d) throws ConfigException, DataSourceException
    {
        try {
            Criteria c =
                    getSession().createCriteria(DocumentVersion.class).add(Restrictions.eq("documentUid", d.getUid()))
                            .addOrder(Order.desc("creationDate"))
                            .setMaxResults(1);
            DocumentVersion v = (DocumentVersion) c.uniqueResult();
            return v;
        } catch (HibernateException e) {
            e.printStackTrace();
            throw new DataSourceException(e);
        }
    }

    public DocumentVersion getLastDocumentVersionById(long documentUid) throws ConfigException, DataSourceException
    {
        try {
            Criteria c =
                    getSession().createCriteria(DocumentVersion.class).add(Restrictions.eq("documentUid", documentUid))
                            .addOrder(Order.desc("creationDate"))
                            .setMaxResults(1);
            DocumentVersion v = (DocumentVersion) c.uniqueResult();
            return v;
        } catch (HibernateException e) {
            e.printStackTrace();
            throw new DataSourceException(e);
        }
    }

    public Vector<DocumentVersion> getTwoLastDocumentVersion(Document d) throws ConfigException, DataSourceException
    {
        try {
            List<DocumentVersion> lVersion =
                    getSession().createCriteria(DocumentVersion.class).add(Restrictions.eq("documentUid", d.getUid()))
                            .addOrder(
                                    Order.desc("creationDate")).setMaxResults(2).list();
            Vector<DocumentVersion> vVersions = new Vector<DocumentVersion>();
            for (DocumentVersion dv : lVersion) {
                vVersions.add(dv);
            }
            return vVersions;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public DocumentVersion getVersionByHashes(String md5, String sha1) throws ConfigException,
            DataSourceException
    {
        try {
            org.hibernate.Query q =
                    getSession().createQuery("from DocumentVersion where hashMD5 like :md5 and hashSHA1 like :sha")
                            .setString("md5", md5)
                            .setString("sha", sha1);

            return (DocumentVersion) q.uniqueResult();
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public Vector<DocumentVersion> getVersions(Document d) throws ConfigException, DataSourceException
    {
        try {
            Criteria c =
                    getSession().createCriteria(DocumentVersion.class).add(Restrictions.eq("documentUid", d.getUid()))
                            .addOrder(Order.desc("creationDate"));
            List<DocumentVersion> fList = c.list();
            DocumentVersion fp = null;
            Vector<DocumentVersion> vDocumentVersions = new Vector<DocumentVersion>();
            for (Iterator<DocumentVersion> it = fList.iterator(); it.hasNext(); ) {
                fp = it.next();
                vDocumentVersions.add(fp);
            }
            return vDocumentVersions;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public Vector<DocumentVersion> getVersionsToDelete() throws ConfigException, DataSourceException
    {
        try {
            Criteria c = getSession().createCriteria(DocumentVersion.class).add(Restrictions.isNull("documentUid"))
                    .addOrder(Order.desc("creationDate"));
            List<DocumentVersion> fList = c.list();
            DocumentVersion fp = null;
            Vector<DocumentVersion> vDocumentVersions = new Vector<DocumentVersion>();
            for (Iterator<DocumentVersion> it = fList.iterator(); it.hasNext(); ) {
                fp = it.next();
                vDocumentVersions.add(fp);
            }
            return vDocumentVersions;
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public void saveDocumentVersion(DocumentVersion v) throws ConfigException, DataSourceException
    {
        try {
            long uid = -1;
            Date d = new Date();
            v.setCreationDate(d);
            v.setModificationDate(d);
            if (v.getStoragePath() == null || v.getStoragePath().equals("")) {
                v.setStoragePath(
                        new SimpleDateFormat("/yyyy/MM/dd/HH/mm/").format(d) + v.getDocumentUid() + "_" + d.getTime() +
                                ".bin");
            }
            uid = (Long) getSession().save(v);
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }


    public void saveDocumentVersionBulk(DocumentVersion v) throws ConfigException, DataSourceException
    {
        try {
            long uid = -1;
            if (v.getStoragePath() == null || v.getStoragePath().equals("")) {
                v.setStoragePath(
                        new SimpleDateFormat("/yyyy/MM/dd/HH/mm/").format(v.getCreationDate()) + v.getDocumentUid() + "_" + v.getCreationDate().getTime() +
                                ".bin");
            }
            uid = (Long) getSession().save(v);
            getSession().flush();
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public void updateDocumentVersion(DocumentVersion v) throws ConfigException, DataSourceException
    {
        try {
            v.setModificationDate(new Date());
            getSession().update(v);
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public void updateDocumentVersionBulk(DocumentVersion v) throws ConfigException, DataSourceException
    {
        try {
            getSession().update(v);
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public void removeDocumentType(DocumentType dt) throws ConfigException, DataSourceException
    {
        try {
            List<DocumentVersion> lDv =
                    getSession().createCriteria(DocumentVersion.class).add(Restrictions.eq("documentType", dt)).list();
            for (DocumentVersion dv : lDv) {
                dv.setDocumentType(null);
                getSession().save(dv);
            }
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }
}

