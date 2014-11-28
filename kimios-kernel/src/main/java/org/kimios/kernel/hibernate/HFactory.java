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

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.proxy.HibernateProxy;
import org.kimios.exceptions.ConfigException;

import javax.persistence.EntityManager;

public abstract class HFactory
{
    private IDBFactory provider;

    public void setEntityManager(EntityManager entityManager){
        if(provider instanceof JpaHibernateFactory){
            ((JpaHibernateFactory) provider).setEntityManager(entityManager);
        }
        throw new ConfigException();
    }

    public EntityManager getEntityManager(){
        if(provider instanceof JpaHibernateFactory){
            return ((JpaHibernateFactory) provider).getEntityManager();
        }
        throw new ConfigException();
    }


    public Session getSession()
    {
        return provider.getSession(autoCommit.get());
    }


    public void save(Object o){
        if(provider instanceof JpaHibernateFactory){
            getEntityManager()
                .unwrap(HibernateEntityManager.class).getSession().saveOrUpdate(o);
        } else {
            getSession().save(o);
        }
    }

    public SessionFactory getSessionFactory()
    {
        return provider.getSessionFactory();
    }


    public void flush(){
        if(provider instanceof JpaHibernateFactory){
            getEntityManager()
            .unwrap(HibernateEntityManager.class)
            .getSession().flush();
        }else {
            getSession().flush();
        }


    }

    public IDBFactory getProvider(){
        return provider;
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

