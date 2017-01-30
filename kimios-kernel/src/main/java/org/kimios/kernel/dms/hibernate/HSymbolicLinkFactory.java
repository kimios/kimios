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
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.model.DMEntity;
import org.kimios.kernel.dms.model.SymbolicLink;
import org.kimios.kernel.dms.SymbolicLinkFactory;
import org.kimios.exceptions.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;

import java.util.List;

public class HSymbolicLinkFactory extends HFactory implements SymbolicLinkFactory
{
    /* (non-Javadoc)
    * @see org.kimios.kernel.dms.SymbolicLinkFactory#getChildSymbolicLinks(org.kimios.kernel.dms.DMEntity)
    *
    */
    public List<SymbolicLink> getChildSymbolicLinks(DMEntity dme)
            throws ConfigException, DataSourceException
    {

        try {
            List<SymbolicLink> lSymLinks =
                    getSession().createQuery("from SymbolicLink sl where sl.parentType=:dmeType" +
                            " AND parentUid=:dmeUid")
                            .setParameter("dmeType", dme.getType())
                            .setParameter("dmeUid", dme.getUid())
                            .list();

            return lSymLinks;
        } catch (HibernateException he) {
            throw new DataSourceException(he);
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.dms.SymbolicLinkFactory#getSymbolicLinks(org.kimios.kernel.dms.DMEntity)
    */
    public List<SymbolicLink> getSymbolicLinks(DMEntity dme)
            throws ConfigException, DataSourceException
    {
        try {
            List<SymbolicLink> lSymLinks =
                    getSession().createQuery("from SymbolicLink sl where sl.dmEntityType=:dmeType" +
                            " AND dmEntityUid=:dmeUid")
                            .setParameter("dmeType", dme.getType())
                            .setParameter("dmeUid", dme.getUid())
                            .list();

            return lSymLinks;
        } catch (HibernateException he) {
            throw new DataSourceException(he);
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.dms.SymbolicLinkFactory#getSymbolicLink(long, int, long, int)
    */
    public SymbolicLink getSymbolicLink(long dmEntityUid, int dmEntityType, long parentUid, int parentType)
            throws DataSourceException
    {
        try {
            SymbolicLink key = new SymbolicLink();
            key.setDmEntityType(dmEntityType);
            key.setDmEntityUid(dmEntityUid);
            key.setParentType(parentType);
            key.setParentUid(parentUid);
            SymbolicLink sl = (SymbolicLink) getSession().get(SymbolicLink.class, key);
            return sl;
        } catch (HibernateException he) {
            throw new DataSourceException(he);
        }
    }


    /* (non-Javadoc)
   * @see org.kimios.kernel.dms.SymbolicLinkFactory#getSymbolicLink(long, int, long, int)
   */
    public SymbolicLink getSymbolicLink(long dmEntityUid)
            throws DataSourceException
    {
        try {
            SymbolicLink sl = (SymbolicLink) getSession().get(SymbolicLink.class, dmEntityUid);
            return sl;
        } catch (HibernateException he) {
            throw new DataSourceException(he);
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.dms.SymbolicLinkFactory#addSymbolicLink(org.kimios.kernel.dms.SymbolicLink)
    */
    public void addSymbolicLink(SymbolicLink sl) throws ConfigException, DataSourceException
    {
        try {
            getSession().save(sl);
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.dms.SymbolicLinkFactory#removeSymbolicLink(org.kimios.kernel.dms.SymbolicLink)
    */
    public void removeSymbolicLink(SymbolicLink sl) throws ConfigException, DataSourceException
    {
        try {
            String delQuery = "delete SymbolicLink sl where sl.dmEntityUid = :targetUid and sl.parentUid =  :parentUid";
            getSession().createQuery(delQuery)
                        .setParameter("targetUid", sl.getDmEntityUid())
                        .setLong("parentUid", sl.getParentUid())
                        .executeUpdate();
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.dms.SymbolicLinkFactory#removeSymbolicLink(org.kimios.kernel.dms.SymbolicLink)
    */
    public void removeSymbolicLink(long symbolicLinkId) throws ConfigException, DataSourceException
    {
        try {
            getSession().delete(getSession().get(SymbolicLink.class, symbolicLinkId));
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public void updateSymbolicLink(SymbolicLink sl) throws ConfigException, DataSourceException
    {
        try {
            getSession().update(sl);
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }
}

