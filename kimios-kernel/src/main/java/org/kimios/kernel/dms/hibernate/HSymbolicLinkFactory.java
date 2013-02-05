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

import java.util.List;
import java.util.Vector;

import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.dms.SymbolicLink;
import org.kimios.kernel.dms.SymbolicLinkFactory;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;

public class HSymbolicLinkFactory extends HFactory implements SymbolicLinkFactory
{
    /* (non-Javadoc)
    * @see org.kimios.kernel.dms.SymbolicLinkFactory#getChildSymbolicLinks(org.kimios.kernel.dms.DMEntity)
    *
    */
    public Vector<SymbolicLink> getChildSymbolicLinks(DMEntity dme)
            throws ConfigException, DataSourceException
    {

        try {
            List<SymbolicLink> lSymLinks =
                    getSession().createQuery("from SymbolicLink sl where sl.parentType=:dmeType" +
                            " AND parentUid=:dmeUid")
                            .setParameter("dmeType", dme.getType())
                            .setParameter("dmeUid", dme.getUid())
                            .list();

            Vector<SymbolicLink> vSymLinks = new Vector<SymbolicLink>();
            for (SymbolicLink sl : lSymLinks) {
                vSymLinks.add(sl);
            }

            return vSymLinks;
        } catch (HibernateException he) {
            throw new DataSourceException(he);
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.dms.SymbolicLinkFactory#getSymbolicLinks(org.kimios.kernel.dms.DMEntity)
    */
    public Vector<SymbolicLink> getSymbolicLinks(DMEntity dme)
            throws ConfigException, DataSourceException
    {
        try {
            List<SymbolicLink> lSymLinks =
                    getSession().createQuery("from SymbolicLink sl where sl.dmEntityType=:dmeType" +
                            " AND dmEntityUid=:dmeUid")
                            .setParameter("dmeType", dme.getType())
                            .setParameter("dmeUid", dme.getUid())
                            .list();

            Vector<SymbolicLink> vSymLinks = new Vector<SymbolicLink>();
            for (SymbolicLink sl : lSymLinks) {
                vSymLinks.add(sl);
            }
            return vSymLinks;
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
            getSession().delete(sl);
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

