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
package org.kimios.kernel.log.factory;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.DMEntityImpl;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.log.DMEntityLog;
import org.kimios.kernel.log.DMEntityLogFactory;
import org.kimios.kernel.user.User;

import java.util.Date;
import java.util.List;
import java.util.Vector;

public class HDMEntityLogFactory extends HFactory implements DMEntityLogFactory
{
    public void cleanLogs(User user) throws ConfigException,
            DataSourceException
    {

    }

    public void cleanLogs(User user, Date from, Date to)
            throws ConfigException, DataSourceException
    {

    }

    public <T extends DMEntityImpl> DMEntityLog<T> getLog(long uid) throws ConfigException,
            DataSourceException
    {
        return null;
    }

    public <T extends DMEntityImpl> Vector<DMEntityLog<T>> getLogs(T e) throws ConfigException,
            DataSourceException
    {
        try {
            Vector<DMEntityLog<T>> logs = new Vector<DMEntityLog<T>>();
            Query q = getSession().createQuery("from DMEntityLog where dmEntityUid=:uid and dmEntityType=:type")
                    .setLong("uid", e.getUid())
                    .setInteger("type", e.getType());
            List<DMEntityLog<T>> lLogs = (List<DMEntityLog<T>>) q.list();
            for (DMEntityLog<T> g : lLogs) {
                logs.add(g);
            }
            return logs;
        } catch (HibernateException he) {
            throw he;
        }
    }

    public <T extends DMEntityImpl> Vector<DMEntityLog<T>> getLogs(User user) throws ConfigException,
            DataSourceException
    {
        return null;
    }

    public <T extends DMEntityImpl> Vector<DMEntityLog<T>> getLogs(T e, Date from, Date to)
            throws ConfigException, DataSourceException
    {
        return null;
    }

    public <T extends DMEntityImpl> Vector<DMEntityLog<T>> getLogs(User user, Date from, Date to)
            throws ConfigException, DataSourceException
    {
        return null;
    }

    public <T extends DMEntityImpl> void saveLog(DMEntityLog<T> log) throws ConfigException,
            DataSourceException
    {
        try {
            log.setDate(new Date());
            getSession().save(log);
        } catch (HibernateException he) {
            throw he;
        }
    }
}

