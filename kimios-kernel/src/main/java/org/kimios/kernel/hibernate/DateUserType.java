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
package org.kimios.kernel.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

/**
 *
 *
 */
public class DateUserType implements UserType
{
    public int[] sqlTypes()
    {
        return new int[]{ Types.DATE };
    }

    @SuppressWarnings("rawtypes")
    public Class returnedClass()
    {
        return Date.class;
    }

    public boolean equals(Object x, Object y) throws HibernateException
    {
        return x == y || !(x == null || y == null) && x.equals(y);
    }

    public int hashCode(Object x) throws HibernateException
    {
        return x.hashCode();
    }

    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
            throws HibernateException, SQLException
    {
        java.sql.Date sqlDate = rs.getDate(names[0]);
        if (rs.wasNull()) {
            return null;
        }
        return new Date(sqlDate.getTime());
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
            throws HibernateException, SQLException
    {
        if (value == null) {
            st.setNull(index, Types.DATE);
        } else {
            Date date = (Date) value;
            java.sql.Date dateSql = new java.sql.Date(date.getTime());
            st.setDate(index, dateSql);
        }
    }

    public Object deepCopy(Object value) throws HibernateException
    {
        return value;
    }

    public boolean isMutable()
    {
        return false;
    }

    public Serializable disassemble(Object value) throws HibernateException
    {
        return (Serializable) value;
    }

    public Object assemble(Serializable cached, Object owner)
            throws HibernateException
    {
        return cached;
    }

    public Object replace(Object original, Object target, Object owner)
            throws HibernateException
    {
        return original;
    }
}
