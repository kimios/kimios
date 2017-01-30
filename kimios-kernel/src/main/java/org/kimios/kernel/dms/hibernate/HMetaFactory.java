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
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.model.DocumentType;
import org.kimios.kernel.dms.model.Meta;
import org.kimios.kernel.dms.MetaFactory;
import org.kimios.exceptions.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;

import java.util.List;

public class HMetaFactory extends HFactory implements MetaFactory
{
    public void deleteMeta(Meta m) throws ConfigException, DataSourceException
    {
        try {
            getSession().delete(m);
            getSession().flush();
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public Meta getMeta(DocumentType t, String name) throws ConfigException,
            DataSourceException
    {
        try {
            Criteria c = getSession().createCriteria(Meta.class)
                    .add(Restrictions.eq("documentTypeUid", t.getUid()))
                    .add(Restrictions.eq("name", name));
            c.setMaxResults(1);
            Meta m = (Meta) c.uniqueResult();
            return m;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public Meta getMeta(long uid) throws ConfigException, DataSourceException
    {
        try {
            Meta m = (Meta) getSession().get(Meta.class, new Long(uid));
            return m;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public List<Meta> getMetas(DocumentType t) throws ConfigException,
            DataSourceException
    {
        try {
            Criteria c = getSession().createCriteria(Meta.class)
                    .add(Restrictions.eq("documentTypeUid", t.getUid()))
                    .addOrder(Order.asc("position"))
                    .addOrder(Order.asc("name").ignoreCase());
            List<Meta> lMetas = (List<Meta>) c.list();
            if (t.getDocumentType() != null) {
                lMetas.addAll(this.getMetas(t.getDocumentType()));
            }
            return lMetas;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public List<Meta> getMetas() throws ConfigException,
            DataSourceException
    {
        try {
            Criteria c = getSession().createCriteria(Meta.class)
                    .addOrder(Order.asc("position"))
                    .addOrder(Order.asc("name").ignoreCase());
            List<Meta> lMetas = (List<Meta>) c.list();
            return lMetas;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public List<Meta> getUnheritedMetas(DocumentType t)
            throws ConfigException, DataSourceException
    {
        try {
            Criteria c = getSession().createCriteria(Meta.class)
                    .add(Restrictions.eq("documentTypeUid", t.getUid()))
                    .addOrder(Order.asc("position"))
                    .addOrder(Order.asc("name").ignoreCase());
            List<Meta> lMetas = (List<Meta>) c.list();
            return lMetas;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public void saveMeta(Meta m) throws ConfigException, DataSourceException
    {
        try {
            getSession().save(m);
            getSession().flush();
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public void updateMeta(Meta m) throws ConfigException, DataSourceException
    {
        try {
            m = (Meta) getSession().merge(m);
            getSession().update(m);
            getSession().flush();
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }
}

