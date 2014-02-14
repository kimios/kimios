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

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class HFactoryImpl implements IDBFactory
{
    private SessionFactory sessionFactory;

    private SessionFactory acSessionFactory;

    public Session getSession(boolean autoCommit)
    {
        if (autoCommit) {
            return acSessionFactory.openSession();
        } else {
            return sessionFactory.getCurrentSession();
        }
    }

    public SessionFactory getSessionFactory()
    {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    public SessionFactory getAcSessionFactory()
    {
        return acSessionFactory;
    }

    public void setAcSessionFactory(SessionFactory sessionFactory)
    {
        this.acSessionFactory = sessionFactory;
    }

    private static IDBFactory instance;

    private HFactoryImpl()
    {

    }

    synchronized static public IDBFactory getInstance()
    {
        if (instance == null) {
            instance = new HFactoryImpl();
        }

        return instance;
    }

    public Session getSession()
    {
        return sessionFactory.getCurrentSession();
    }
}

