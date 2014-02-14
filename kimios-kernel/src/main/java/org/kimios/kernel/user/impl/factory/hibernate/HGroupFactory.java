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
package org.kimios.kernel.user.impl.factory.hibernate;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactoryImpl;
import org.kimios.kernel.user.AuthenticationSource;
import org.kimios.kernel.user.Group;
import org.kimios.kernel.user.GroupFactory;
import org.kimios.kernel.user.User;

import java.util.List;
import java.util.Set;
import java.util.Vector;

public class HGroupFactory implements GroupFactory
{
    public AuthenticationSource auth;

    public AuthenticationSource getAuth()
    {
        return auth;
    }

    public void setAuth(AuthenticationSource auth)
    {
        this.auth = auth;
    }

    public void deleteGroup(Group group) throws DataSourceException,
            ConfigException
    {
        try {

            group = (Group) HFactoryImpl.getInstance().getSession().merge(group);
            for (User u : group.getUsers()) {
                auth.getUserFactory().removeUserFromGroup(u, group);
            }
            HFactoryImpl.getInstance().getSession().delete(group);
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public Group getGroup(String gid) throws DataSourceException,
            ConfigException
    {
        try {
            Criteria c = HFactoryImpl.getInstance().getSession().createCriteria(Group.class)
                    .add(Restrictions.eq("gid", gid))
                    .add(Restrictions.eq("authenticationSourceName", this.getAuth().getName()));
            Group g = (Group) (c.uniqueResult());
            return g;
        } catch (HibernateException he) {
            throw new DataSourceException(he);
        }
    }

    public Vector<Group> getGroups() throws DataSourceException,
            ConfigException
    {
        try {
            Criteria c = HFactoryImpl.getInstance().getSession().createCriteria(Group.class)
                    .add(Restrictions.eq("authenticationSourceName", this.getAuth().getName()))
                    .addOrder(Order.asc("name"));
            List<Group> lGroups = (List<Group>) (c.list());
            Vector<Group> vGroup = new Vector<Group>();
            for (Group g : lGroups) {
                vGroup.add(g);
            }
            return vGroup;
        } catch (HibernateException he) {
            throw new DataSourceException(he);
        }
    }

    public Vector<Group> getGroups(String userUid) throws DataSourceException,
            ConfigException
    {
        try {
            User u = (User) HFactoryImpl.getInstance().getSession().createCriteria(User.class)
                    .add(Restrictions.eq("uid", userUid))
                    .add(Restrictions.eq("authenticationSourceName", this.auth.getName()))
                    .uniqueResult();
            Set<Group> sGroups = u.getGroups();
            Vector<Group> groups = new Vector<Group>();
            for (Group g : sGroups) {
                groups.add(g);
            }
            return groups;
        } catch (HibernateException he) {
            throw new DataSourceException(he);
        }
    }

    public void saveGroup(Group group) throws DataSourceException,
            ConfigException
    {
        try {
            HFactoryImpl.getInstance().getSession().save(group);
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public void updateGroup(Group group) throws DataSourceException,
            ConfigException
    {
        try {
            HFactoryImpl.getInstance().getSession().update(group);
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }
}

