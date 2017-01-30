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
package org.kimios.kernel.log.factory;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.model.DMEntity;
import org.kimios.exceptions.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.log.model.DMEntityLog;
import org.kimios.kernel.log.model.Log;
import org.kimios.kernel.log.LogFactory;
import org.kimios.kernel.user.model.User;

import java.util.Date;
import java.util.List;
import java.util.Vector;

public class HLogFactory extends HFactory implements LogFactory
{
    public void cleanLogs(DMEntity e) throws ConfigException,
            DataSourceException
    {
        try {
            Criteria c = getSession().createCriteria(Log.class)
                    .add(Restrictions.eq("dmEntityType", e.getType()))
                    .add(Restrictions.eq("dmEntityUid", e.getUid()));
            List<Log> lLogs = (List<Log>) c.list();
            for (Log g : lLogs) {
                getSession().delete(g);
            }
        } catch (HibernateException he) {
            throw he;
        }
    }

    public void cleanLogs(User user) throws ConfigException,
            DataSourceException
    {

    }

    public void cleanLogs(DMEntity e, Date from, Date to)
            throws ConfigException, DataSourceException
    {
        try {
            Criteria c = getSession().createCriteria(DMEntityLog.class)
                    .add(Restrictions.eq("dmEntityUid", e.getUid()))
                    .add(Restrictions.eq("dmEntityType", e.getType()))
                    .add(Restrictions.ge("date", from))
                    .add(Restrictions.le("date", to));
            List<Log> lLogs = (List<Log>) c.list();
            for (Log g : lLogs) {
                getSession().delete(g);
            }
        } catch (HibernateException he) {
            throw he;
        }
    }

    public void cleanLogs(User user, Date from, Date to)
            throws ConfigException, DataSourceException
    {
        try {
            Criteria c = getSession().createCriteria(DMEntityLog.class)
                    .add(Restrictions.eq("user", user.getName()))
                    .add(Restrictions.eq("userSource", user.getUid()))
                    .add(Restrictions.ge("date", from))
                    .add(Restrictions.le("date", to));
            List<Log> lLogs = (List<Log>) c.list();
            for (Log g : lLogs) {
                getSession().delete(g);
            }
        } catch (HibernateException he) {
            throw he;
        }
    }

    public Log getLog(long uid) throws ConfigException, DataSourceException
    {
        try {
            Log l = (Log) getSession().load(Log.class, new Long(uid));
            return l;
        } catch (HibernateException e) {
            throw e;
        }
    }

    public Vector<Log> getLogs(DMEntity e) throws ConfigException,
            DataSourceException
    {
        try {
            Vector<Log> logs = new Vector<Log>();
            Criteria c = getSession().createCriteria(DMEntityLog.class)
                    .add(Restrictions.eq("dmEntityUid", e.getUid()))
                    .add(Restrictions.eq("dmEntityType", e.getType()))
                    .addOrder(Order.desc("date"));
            List<Log> lLogs = (List<Log>) c.list();
            for (Log g : lLogs) {
                logs.add(g);
            }
            return logs;
        } catch (HibernateException he) {
            throw he;
        }
    }

    public Vector<Log> getLogs(User user) throws ConfigException,
            DataSourceException
    {
        try {
            Vector<Log> logs = new Vector<Log>();
            Criteria c = getSession().createCriteria(DMEntityLog.class)
                    .add(Restrictions.eq("user", user.getUid()))
                    .add(Restrictions.eq("userSource", user.getAuthenticationSourceName()))
                    .addOrder(Order.desc("date"));
            List<Log> lLogs = (List<Log>) c.list();
            for (Log g : lLogs) {
                logs.add(g);
            }
            return logs;
        } catch (HibernateException he) {
            throw he;
        }
    }

    public Vector<Log> getLogs(DMEntity e, Date from, Date to)
            throws ConfigException, DataSourceException
    {
        try {
            Vector<Log> logs = new Vector<Log>();
            Criteria c = getSession().createCriteria(DMEntityLog.class)
                    .add(Restrictions.eq("dmEntityType", e.getType()))
                    .add(Restrictions.eq("dmEntityUid", e.getUid()))
                    .add(Restrictions.ge("date", from))
                    .add(Restrictions.le("date", to))
                    .addOrder(Order.desc("date"));
            List<Log> lLogs = (List<Log>) c.list();
            for (Log g : lLogs) {
                logs.add(g);
            }
            return logs;
        } catch (HibernateException he) {
            throw he;
        }
    }

    public Vector<Log> getLogs(User user, Date from, Date to)
            throws ConfigException, DataSourceException
    {
        try {
            Vector<Log> logs = new Vector<Log>();
            Criteria c = getSession().createCriteria(Log.class)
                    .add(Restrictions.eq("user", user.getType()))
                    .add(Restrictions.eq("userSource", user.getUid()))
                    .add(Restrictions.ge("date", from))
                    .add(Restrictions.le("date", to))
                    .addOrder(Order.desc("date"));
            List<Log> lLogs = (List<Log>) c.list();
            for (Log g : lLogs) {
                logs.add(g);
            }
            return logs;
        } catch (HibernateException he) {
            throw he;
        }
    }

    public void saveLog(Log log) throws ConfigException, DataSourceException
    {
        try {
            log.setDate(new Date());
            getSession().save(log);
        } catch (HibernateException he) {
            throw he;
        }
    }
}

