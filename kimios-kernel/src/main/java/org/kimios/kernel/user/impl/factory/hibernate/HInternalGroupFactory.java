/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
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
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.user.model.Group;
import org.kimios.kernel.user.model.GroupFactory;
import org.kimios.kernel.user.model.User;

import java.util.List;
import java.util.Set;
import java.util.Vector;

public class HInternalGroupFactory extends HFactory {

    public void deleteGroup(Group group, HInternalUserFactory internalUserFactory) throws DataSourceException,
            ConfigException {
        try {

            group = (Group) getSession().merge(group);
            for (User u : group.getUsers()) {
                internalUserFactory.removeUserFromGroup(u, group);
            }
            getSession().delete(group);
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public Group getGroup(String gid, String sourceName) throws DataSourceException,
            ConfigException {
        try {
            Criteria c = getSession().createCriteria(Group.class)
                    .add(Restrictions.eq("gid", gid))
                    .add(Restrictions.eq("authenticationSourceName", sourceName));
            Group g = (Group) (c.uniqueResult());
            return g;
        } catch (HibernateException he) {
            throw new DataSourceException(he);
        }
    }

    public Vector<Group> getGroups(String sourceName) throws DataSourceException,
            ConfigException {
        try {
            Criteria c = getSession().createCriteria(Group.class)
                    .add(Restrictions.eq("authenticationSourceName", sourceName))
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

    public Vector<Group> getGroupsForUser(String userUid, String sourceName) throws DataSourceException,
            ConfigException {
        try {
            User u = (User) getSession().createCriteria(User.class)
                    .add(Restrictions.eq("uid", userUid))
                    .add(Restrictions.eq("authenticationSourceName", sourceName))
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
            ConfigException {
        try {
            getSession().save(group);
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public void updateGroup(Group group) throws DataSourceException,
            ConfigException {
        try {
            getSession().update(group);
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }
}