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

import java.text.SimpleDateFormat;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.Document;
import org.kimios.kernel.dms.DocumentVersion;
import org.kimios.kernel.dms.Meta;
import org.kimios.kernel.dms.MetaBooleanValue;
import org.kimios.kernel.dms.MetaDateValue;
import org.kimios.kernel.dms.MetaNumberValue;
import org.kimios.kernel.dms.MetaStringValue;
import org.kimios.kernel.dms.MetaType;
import org.kimios.kernel.dms.MetaValue;
import org.kimios.kernel.dms.MetaValueBean;
import org.kimios.kernel.dms.MetaValueFactory;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;

public class HMetaValueFactory extends HFactory implements MetaValueFactory
{
    public void deleteMetaValue(MetaValue metaValue) throws ConfigException,
            DataSourceException
    {
        try {
            getSession().delete(metaValue);
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public MetaValue getMetaValue(DocumentVersion documentVersion, Meta meta)
            throws ConfigException, DataSourceException
    {
        try {
            meta.getMetaType();
            MetaValue mv = (MetaValue) getSession().createCriteria(MetaValueBean.class)
                    .add(Restrictions.eq("documentVersionUid", documentVersion.getUid()))
                    .add(Restrictions.eq("metaUid", meta.getUid()))
                    .uniqueResult();
            return mv;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public boolean hasValue(Meta meta)
            throws ConfigException, DataSourceException
    {
        try {
            boolean isEmpty = getSession().createCriteria(MetaValueBean.class)
                    .add(Restrictions.eq("metaUid", meta.getUid()))
                    .setMaxResults(1)
                    .list().isEmpty();
            return !isEmpty;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public List<MetaValue> getMetaValues(DocumentVersion documentVersion)
            throws ConfigException, DataSourceException
    {
        try {
            List<MetaValue> lValues = (List<MetaValue>) getSession().createCriteria(MetaValue.class)
                    .add(Restrictions.eq("documentVersionUid", documentVersion.getUid()))
                    .addOrder(Order.asc("meta.id"))
                    .list();
            return lValues;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public void saveMetaValue(MetaValue metaValue) throws ConfigException,
            DataSourceException
    {
        try {
            switch (metaValue.getMeta().getMetaType()) {
                case MetaType.STRING:
                    getSession().save((MetaStringValue) metaValue);
                    break;
                case MetaType.NUMBER:
                    getSession().save((MetaNumberValue) metaValue);
                    break;
                case MetaType.DATE:
                    getSession().save((MetaDateValue) metaValue);
                    break;
                case MetaType.BOOLEAN:
                    getSession().save((MetaBooleanValue) metaValue);
                    break;
                default:
                    break;
            }
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public void saveMetaValueOverride(MetaValue metaValue) throws ConfigException,
            DataSourceException
    {
        try {
            switch (metaValue.getMeta().getMetaType()) {
                case MetaType.STRING:
                    getSession().save((MetaStringValue) metaValue);
                    break;
                case MetaType.NUMBER:
                    getSession().save((MetaNumberValue) metaValue);
                    break;
                case MetaType.DATE:
                    getSession().save((MetaDateValue) metaValue);
                    break;
                case MetaType.BOOLEAN:
                    getSession().save((MetaBooleanValue) metaValue);
                    break;
                default:
                    break;
            }
        } catch (HibernateException e) {
            if (e instanceof NonUniqueObjectException) {
                //merge and update
                try {
                    switch (metaValue.getMeta().getMetaType()) {
                        case MetaType.STRING:
                            getSession().merge((MetaStringValue) metaValue);
                            break;
                        case MetaType.NUMBER:
                            getSession().merge((MetaNumberValue) metaValue);
                            break;
                        case MetaType.DATE:
                            getSession().merge((MetaDateValue) metaValue);
                            break;
                        case MetaType.BOOLEAN:
                            getSession().merge((MetaBooleanValue) metaValue);
                            break;
                        default:
                            break;
                    }
                } catch (HibernateException ex) {
                    throw new DataSourceException(e);
                }
            } else {
                throw new DataSourceException(e);
            }
        }
    }

    public void updateMetaValue(MetaValue metaValue) throws ConfigException,
            DataSourceException
    {
        try {
            getSession().update(metaValue);
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public List<MetaValue> getMetaByValue(String value, int type) throws ConfigException, DataSourceException
    {
        try {
            String query = "";
            Query q = null;
            switch (type) {
                case MetaType.STRING:
                    query = "from MetaStringValue where value like :val";
                    q = getSession().createQuery(query).setString("val", value);
                    break;
                case MetaType.BOOLEAN:
                    query = "from MetaBooleanValue where value = :val";
                    q = getSession().createQuery(query).setBoolean("val", Boolean.parseBoolean(value));
                    break;
                case MetaType.NUMBER:
                    query = "from MetaNumberValue where value = :val";
                    q = getSession().createQuery(query).setDouble("val", Double.parseDouble(value));
                    break;
                case MetaType.DATE:
                    query = "from MetaDateValue where value = :val";
                    try {
                        q = getSession().createQuery(query)
                                .setDate("val", new SimpleDateFormat("dd/MM/yyy").parse(value));
                    } catch (Exception e) {

                    }
                    break;
            }
            return q.list();
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public void cleanMetaValues() throws ConfigException, DataSourceException
    {
        String query =
                "delete from %s mvb where mvb.documentVersionUid " +
                        "in (select uid from DocumentVersion where documentUid is null)";

        getSession().createQuery(String.format(query, "MetaStringValue")).executeUpdate();
        getSession().createQuery(String.format(query, "MetaDateValue")).executeUpdate();
        getSession().createQuery(String.format(query, "MetaNumberValue")).executeUpdate();
        getSession().createQuery(String.format(query, "MetaBooleanValue")).executeUpdate();
    }

    public void cleanDocumentMetaValues(Document document) throws ConfigException, DataSourceException
    {
        String query = "delete from %s mvb where mvb.documentVersionUid in " +
                "(select uid from DocumentVersion where document = :document)";

        getSession().createQuery(String.format(query, "MetaStringValue"))
                .setEntity("document", document)
                .executeUpdate();
        getSession().createQuery(String.format(query, "MetaDateValue"))
                .setEntity("document", document)
                .executeUpdate();
        getSession().createQuery(String.format(query, "MetaNumberValue"))
                .setEntity("document", document)
                .executeUpdate();
        getSession().createQuery(String.format(query, "MetaBooleanValue"))
                .setEntity("document", document)
                .executeUpdate();
    }
}

