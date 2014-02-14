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
package org.kimios.kernel.security.factory;

import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.security.Role;
import org.kimios.kernel.security.RoleFactory;
import org.kimios.kernel.security.RolePK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Vector;

public class HRoleFactory extends HFactory implements RoleFactory
{
    private static Logger log = LoggerFactory.getLogger(RoleFactory.class);

    public void deleteRole(Role r) throws ConfigException, DataSourceException
    {
        try {
            getSession().delete(r);
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public Role getRole(int role, String userName, String userSource)
            throws ConfigException, DataSourceException
    {
        try {
            Role r = (Role) getSession().get(Role.class, new RolePK(role, userName, userSource));
            return r;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public Vector<Role> getRoles(String userName, String userSource)
            throws ConfigException, DataSourceException
    {
        try {
            Vector<Role> vRoles = new Vector<Role>();
            List<Role> lRoles = (List<Role>) getSession().createCriteria(Role.class)
                    .add(Restrictions.eq("userName", userName))
                    .add(Restrictions.eq("userSource", userSource))
                    .list();
            for (Role r : lRoles) {
                vRoles.add(r);
            }
            return vRoles;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public Vector<Role> getRoles(int role) throws ConfigException,
            DataSourceException
    {
        try {
            Vector<Role> vRoles = new Vector<Role>();
            List<Role> lRoles = (List<Role>) getSession().createCriteria(Role.class)
                    .add(Restrictions.eq("role", role))
                    .list();
            for (Role r : lRoles) {
                vRoles.add(r);
            }
            return vRoles;
        } catch (HibernateException e) {
            e.printStackTrace();
            throw new DataSourceException(e);
        }
    }

    public void saveRole(Role r) throws ConfigException, DataSourceException
    {
        try {
            getSession().save(r);
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }
}

