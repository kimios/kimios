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
package org.kimios.kernel.hibernate;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.proxy.HibernateProxy;

public abstract class HFactory
{
    private IDBFactory provider;

    public Session getSession()
    {
        return provider.getSession(autoCommit.get());
    }

    public SessionFactory getSessionFactory()
    {
        return provider.getSessionFactory();
    }

    public void setProvider(IDBFactory _provider)
    {
        this.provider = _provider;
    }

    public static void setAutoCommit(boolean _ac)
    {
        autoCommit.set(_ac);
    }

    protected static ThreadLocal<Boolean> autoCommit = new ThreadLocal<Boolean>()
    {
        @Override
        public Boolean get()
        {
            return super.get();
        }

        @Override
        protected Boolean initialValue()
        {
            return false;
        }

        @Override
        public void set(Boolean value)
        {
            super.set(value);
        }
    };

    public static <T> T initializeAndUnproxy(T entity)
    {
        if (entity == null) {
            throw new
                    NullPointerException("Entity passed for initialization is null");
        }

        Hibernate.initialize(entity);
        if (entity instanceof HibernateProxy) {
            entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer()
                    .getImplementation();
        }
        return entity;
    }
}

