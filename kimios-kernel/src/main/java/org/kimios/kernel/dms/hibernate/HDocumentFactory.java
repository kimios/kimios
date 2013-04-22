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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.Document;
import org.kimios.kernel.dms.DocumentFactory;
import org.kimios.kernel.dms.FactoryInstantiator;
import org.kimios.kernel.dms.Folder;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;

public class HDocumentFactory extends HFactory implements DocumentFactory
{
    public void deleteDocument(Document d) throws ConfigException,
            DataSourceException
    {
        try {
            FactoryInstantiator.getInstance().getMetaValueFactory().cleanDocumentMetaValues(d);
            cleanRelatedDocument(d);
            getSession().delete(d);
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public Document getDocument(long uid) throws ConfigException,
            DataSourceException
    {
        try {
            Document d = (Document) getSession().get(Document.class, new Long(uid));
            return d;
        } catch (ObjectNotFoundException e) {
            return null;
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public Document getDocument(String name, String extension, Folder f) throws ConfigException,
            DataSourceException
    {
        try {
            Query q = null;
            if (extension != null) {
                q = getSession().createQuery(
                        "from Document d where d.name=:name and UPPER(d.extension)=:extension and folderUid=:folderUid")
                        .setString("name", name)
                        .setString("extension", extension.toUpperCase())
                        .setLong("folderUid", f.getUid());
            } else {
                q = getSession().createQuery(
                        "from Document d where d.name=:name and d.extension is null and folderUid=:folderUid")
                        .setString("name", name)
                        .setLong("folderUid", f.getUid());
            }
            List<Document> list = q.list();
            if (list.size() >= 1) {
                return list.get(0);
            } else {
                return null;
            }
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public List<Document> getDocuments() throws ConfigException,
            DataSourceException
    {
        try {
            Criteria c = getSession().createCriteria(Document.class).addOrder(
                    Order.asc("name").ignoreCase());
            List<Document> fList = c.list();

            return fList;
        } catch (ObjectNotFoundException e) {
            return null;
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public List<Document> getLockedDocuments(String owner, String ownerSource) throws ConfigException,
        DataSourceException
    {
        try {

            String query = "select d from Document d join d.lock lc where lc.user = :owner "
                + "and lc.userSource = :ownerSource order by d.name asc";

            List<Document> fList = getSession().createQuery( query )
                .setString( "owner", owner )
                .setString( "ownerSource", ownerSource )
                .list();

            return fList;
        } catch (ObjectNotFoundException e) {
            return null;
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public List<Document> getDocuments(Folder f) throws ConfigException,
            DataSourceException
    {
        try {

            Criteria c = getSession().createCriteria(Document.class)
                    .add(Restrictions.eq("folderUid", f.getUid()))
                    .addOrder(Order.asc("name").ignoreCase());
            List<Document> fList = c.list();

            return fList;
        } catch (ObjectNotFoundException e) {
            return null;
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public void saveDocument(Document d) throws ConfigException,
            DataSourceException
    {
        try {
            getSession().save(d);
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public void updateDocument(Document d) throws ConfigException,
            DataSourceException
    {
        try {
            getSession().update(d);
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public List<Document> getRelatedDocuments(Document document)
            throws ConfigException, DataSourceException
    {
        try {
            List<Document> relatedDocs = new ArrayList<Document>(document.getRelatedDocuments());
            relatedDocs.addAll(document.getParentsRelatedDocuments());
            return relatedDocs;
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public List<Document> getExpiredDocuments(String sourceWorkspace, Date expirationDate)
    {
        String sql = "from Document fetch all properties " +
                "where updateDate < :expirationDate " +
                "and path like :sourceWorkspace";
        Query query = getSession().createQuery(sql);
        query.setDate("expirationDate", expirationDate);
        query.setString("sourceWorkspace", "/" + sourceWorkspace + "/%");
        return query.list();
    }

    public void addRelatedDocument(Document document, Document relatedDocument)
            throws ConfigException, DataSourceException
    {
        try {
            document.getRelatedDocuments().add(relatedDocument);
            getSession().save(document);
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public void removeRelatedDocument(Document document, Document toRemove) throws ConfigException, DataSourceException
    {
        try {
            if (document.getRelatedDocuments().contains(toRemove)) {
                document.getRelatedDocuments().remove(toRemove);
                getSession().save(document);
            }
            if (toRemove.getRelatedDocuments().contains(document)) {
                toRemove.getRelatedDocuments().remove(document);
                getSession().save(toRemove);
            }
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public List<org.kimios.kernel.ws.pojo.Document> getDocumentsPojos(List<Document> docs)
            throws ConfigException, DataSourceException
    {
        List<org.kimios.kernel.ws.pojo.Document> lists = null;
        if (docs.size() > 0) {
            try {
                String query = "from DocumentPojo where uid in (";
                for (int j = 0; j < docs.size(); j++) {
                    if (j == (docs.size() - 1)) {
                        query += docs.get(j).getUid() + ")";
                    } else {
                        query += docs.get(j).getUid() + ",";
                    }
                }
                query += " ORDER by lower(name)";
                lists = getSession().createQuery(query).list();
                return lists;
            } catch (HibernateException he) {
                throw new DataSourceException(he, he.getMessage());
            }
        } else {
            return new Vector<org.kimios.kernel.ws.pojo.Document>();
        }
    }

    public List<Document> getDocumentsFromIds(List<Long> listIds) throws ConfigException, DataSourceException
    {
        List<Document> lists = null;
        if (listIds.size() > 0) {
            try {
                String query = "from Document where uid in (:listIds)";
                query += " ORDER by lower(name)";
                lists = getSession()
                        .createQuery(query)
                        .setParameterList("listIds", listIds)
                        .list();
                return lists;
            } catch (HibernateException he) {
                throw new DataSourceException(he, he.getMessage());
            }
        } else {
            return new ArrayList<Document>();
        }
    }


    public List<org.kimios.kernel.ws.pojo.Document> getDocumentsPojosFromIds(List<Long> listIds)
            throws ConfigException, DataSourceException
    {
        List<org.kimios.kernel.ws.pojo.Document> lists = null;
        if (listIds.size() > 0) {
            try {
                String query = "from DocumentPojo where uid in (:listIds)";
                query += " ORDER by lower(name)";
                lists = getSession().createQuery(query)
                        .setParameterList("listIds", listIds)
                        .list();
                return lists;
            } catch (HibernateException he) {
                throw new DataSourceException(he, he.getMessage());
            }
        } else {
            return new ArrayList<org.kimios.kernel.ws.pojo.Document>();
        }
    }

    public List<Document> getDocumentSince(Calendar since, String excludePath)
    {
        StringBuilder query = new StringBuilder();
        query.append("from Document  where creationDate >= :since ");
        if (excludePath != null && excludePath.length() > 0) {
            query.append(" and path not like :path");
        }
        Query hQuery = getSession()
                .createQuery(query.toString())
                .setCalendar("since", since);
        if (excludePath != null && excludePath.length() > 0) {
            hQuery.setString("path", excludePath + "%");
        }
        return hQuery.list();
    }

    public Number getDocumentCountSince(Calendar since, String excludePath)
    {
        StringBuilder query = new StringBuilder();
        query.append("select count(*) from Document  where creationDate >= :since ");
        if (excludePath != null && excludePath.length() > 0) {
            query.append(" and path not like :path");
        }
        Query hQuery = getSession()
                .createQuery(query.toString())
                .setCalendar("since", since);
        if (excludePath != null && excludePath.length() > 0) {
            hQuery.setString("path", excludePath + "%");
        }
        Number count = (Number) hQuery.uniqueResult();
        return count;
    }

    public void cleanRelatedDocument(Document document)
    {
        String query =
                "delete from related_documents where document_id = :documentId or related_document_id = :documentId";
        getSession().createSQLQuery(query)
                .setLong("documentId", document.getUid())
                .executeUpdate();
    }
}

