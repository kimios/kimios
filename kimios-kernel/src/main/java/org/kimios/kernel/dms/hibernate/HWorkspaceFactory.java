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

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.exception.ConstraintViolationException;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.Workspace;
import org.kimios.kernel.dms.WorkspaceFactory;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;

import java.util.List;

public class HWorkspaceFactory extends HFactory implements WorkspaceFactory
{
    public void deleteWorkspace(Workspace w) throws ConfigException,
            DataSourceException
    {
        try {
            getSession().delete(w);
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public Workspace getWorkspace(long uid) throws ConfigException,
            DataSourceException
    {
        try {
            Workspace w = (Workspace) getSession().get(Workspace.class, new Long(uid));
            return w;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public Workspace getWorkspace(String name) throws ConfigException,
            DataSourceException
    {
        try {
            Query q = getSession().createQuery("from Workspace w where w.name=:name").setString("name", name);
            List<Workspace> list = q.list();
            if (list.size() >= 1) {
                return list.get(0);
            } else {
                return null;
            }
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public List<Workspace> getWorkspaces() throws ConfigException,
            DataSourceException
    {
        try {
            //Criteria c = getSession().createCriteria(Workspace.class).addOrder(Order.asc("name").ignoreCase());
            Query q = getSession().createQuery("from Workspace w order by w.name");
            List<Workspace> wList = q.setReadOnly(true).setFetchSize(100).list();
            return wList;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public void saveWorkspace(Workspace w) throws ConfigException,
            DataSourceException
    {
        try {
            long uid = ((Long) getSession().save(w)).longValue();
            w.setUid(uid);
            getSession().flush();
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public void updateWorkspace(Workspace w) throws ConfigException,
            DataSourceException
    {
        try {
            getSession().update(w);
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }
}

