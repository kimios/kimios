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

import org.hibernate.HibernateException;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.*;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;

import java.text.SimpleDateFormat;
import java.util.List;

public class HMetaValueFactory extends HFactory implements MetaValueFactory {
    public void deleteMetaValue(MetaValue metaValue) throws ConfigException,
            DataSourceException {
        try {
            getSession().delete(metaValue);
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public MetaValue getMetaValue(DocumentVersion documentVersion, Meta meta)
            throws ConfigException, DataSourceException {
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
            throws ConfigException, DataSourceException {
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
            throws ConfigException, DataSourceException {
        try {

            String query = "select mvb from MetaValueBean mvb join mvb.meta mt " +
                    "where mvb.documentVersionUid = :vuid order by mt.position,mt.name,mt.id";


            /*List<MetaValue> lValues = (List<MetaValue>) getSession().createCriteria(MetaValueBean.class)
                    .add(Restrictions.eq("documentVersionUid", documentVersion.getUid()))
                    .addOrder(Order.asc("meta.position"))
                    .addOrder(Order.asc("meta.id"))
                    .addOrder(Order.asc("meta.name"))
                    .list();*/
            return getSession().createQuery(query)
                    .setLong("vuid", documentVersion.getUid())
                    .list();
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public void saveMetaValue(MetaValue metaValue) throws ConfigException,
            DataSourceException {
        try {
            getSession().save(metaValue);
            getSession().flush();
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public void saveMetaValueOverride(MetaValue metaValue) throws ConfigException,
            DataSourceException {
        try {
            getSession().save(metaValue);
        } catch (HibernateException e) {
            if (e instanceof NonUniqueObjectException) {
                //merge and update
                try {
                    getSession().merge(metaValue);
                } catch (HibernateException ex) {
                    throw new DataSourceException(e);
                }
            } else {
                throw new DataSourceException(e);
            }
        }
    }

    public void updateMetaValue(MetaValue metaValue) throws ConfigException,
            DataSourceException {
        try {
            getSession().update(metaValue);
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public List<MetaValue> getMetaByValue(String value, int type) throws ConfigException, DataSourceException {
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
                case MetaType.LIST:
                    query = "SELECT m FROM MetaListValue m JOIN m.value v WHERE v = :value";
                    q = getSession().createQuery(query)
                            .setString("value", value);
                    break;
            }
            return q.list();
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public void cleanMetaValues() throws ConfigException, DataSourceException {
        String query =
                "delete from %s mvb where mvb.documentVersionUid " +
                        "in (select uid from DocumentVersion where documentUid is null)";

        getSession().createQuery(String.format(query, "MetaStringValue")).executeUpdate();
        getSession().createQuery(String.format(query, "MetaDateValue")).executeUpdate();
        getSession().createQuery(String.format(query, "MetaNumberValue")).executeUpdate();
        getSession().createQuery(String.format(query, "MetaBooleanValue")).executeUpdate();
        getSession().createQuery(String.format(query, "MetaListValue")).executeUpdate();
    }

    public void cleanDocumentMetaValues(Document document) throws ConfigException, DataSourceException {
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


        //TODO: check JPA / Hibernate feature enhancement, for sub collection with @ElementCollection
        //on delete cascade.
        // For now, have to manual delete item

        String item = "select v from MetaListValue v where v.documentVersionUid in "
                + "(select uid from DocumentVersion where document = :document)";
        List<MetaListValue> metaListValues =
                getSession().createQuery(item)
                        .setEntity("document", document)
                        .list();
        for (MetaListValue metaListValue : metaListValues) {
            getSession().delete(metaListValue);
        }
    }
}

