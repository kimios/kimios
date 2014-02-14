/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.DocumentType;
import org.kimios.kernel.dms.DocumentTypeFactory;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class HDocumentTypeFactory extends HFactory implements DocumentTypeFactory
{
    public void deleteDocumentType(DocumentType t) throws ConfigException,
            DataSourceException
    {
        try {
            getSession().delete(t);
            getSession().flush();
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public DocumentType getDocumentType(long uid) throws ConfigException,
            DataSourceException
    {
        try {
            DocumentType t = (DocumentType) getSession().get(DocumentType.class, new Long(uid));
            return t;
        } catch (ObjectNotFoundException e) {
            return null;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public Vector<DocumentType> getDocumentTypes() throws ConfigException,
            DataSourceException
    {
        try {
            Criteria c = getSession().createCriteria(DocumentType.class).addOrder(Order.asc("name").ignoreCase());
            List fList = c.list();
            DocumentType t = null;
            Vector<DocumentType> vDocumentsType = new Vector<DocumentType>();
            for (Iterator it = fList.iterator(); it.hasNext(); ) {
                t = (DocumentType) it.next();
                vDocumentsType.add(t);
            }
            return vDocumentsType;
        } catch (ObjectNotFoundException e) {
            return null;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public DocumentType getDocumentTypeByName(String typeName) throws ConfigException, DataSourceException
    {
        try {
            String query = "from DocumentType where name like :name";
            Query q = getSession().createQuery(query).setString("name", typeName);
            return (DocumentType) q.uniqueResult();
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public List<DocumentType> getChildrenDocumentType(long documentTypeId) throws ConfigException, DataSourceException
    {
        try {
            String query = "from DocumentType where documentType.uid = :id";
            Query q = getSession().createQuery(query).setLong("id", documentTypeId);
            return q.list();
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public void saveDocumentType(DocumentType t) throws ConfigException,
            DataSourceException
    {
        try {
            getSession().save(t);
            getSession().flush();
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public void updateDocumentType(DocumentType t) throws ConfigException,
            DataSourceException
    {
        try {
            getSession().update(t);
            getSession().flush();
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }
}

