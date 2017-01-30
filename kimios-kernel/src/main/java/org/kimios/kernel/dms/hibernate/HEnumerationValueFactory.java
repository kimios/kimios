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
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.type.StringType;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.EnumerationValueFactory;
import org.kimios.exceptions.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;

import java.util.List;
import java.util.Vector;

public class HEnumerationValueFactory extends HFactory implements EnumerationValueFactory
{
    public Vector<String> getValues(long uid) throws ConfigException,
            DataSourceException
    {
        try {
            Vector<String> vValues = new Vector<String>();
            List<String> lValues = getSession().createSQLQuery(
                    "SELECT v.enumeration_value as val FROM enumeration_value v WHERE enumeration_id=:uid ORDER by lower(v.enumeration_value)")
                    .addScalar("val", StringType.INSTANCE)
                    .setParameter("uid", uid)
                    .list();
            for (String st : lValues) {
                vValues.add(st);
            }

            return vValues;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public void updateValues(long uid, Vector<String> values)
            throws ConfigException, DataSourceException
    {
        try {
            getSession().createSQLQuery("DELETE FROM enumeration_value WHERE enumeration_id=:uid")
                    .setLong("uid", uid)
                    .executeUpdate();
            for (String b : values) {
                getSession().createSQLQuery(
                        "INSERT INTO enumeration_value (enumeration_id, enumeration_value) VALUES(:uid,:value)")
                        .setLong("uid", uid)
                        .setString("value", b)
                        .executeUpdate();
            }
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }
}

