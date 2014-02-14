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

import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactoryImpl;
import org.kimios.kernel.security.FactoryInstantiator;
import org.kimios.kernel.security.pwdgen.md5.MD5Generator;
import org.kimios.kernel.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class HUserFactory implements UserFactory
{
    private static Logger log = LoggerFactory.getLogger(HUserFactory.class);

    private AuthenticationSource auth;

    public AuthenticationSource getAuth()
    {
        return auth;
    }

    public void setAuth(AuthenticationSource auth)
    {
        this.auth = auth;
    }

    public void addUserToGroup(User user, Group group)
            throws DataSourceException, ConfigException
    {
        try {

            User u = (User) HFactoryImpl.getInstance()
                    .getSession().get(User.class, user);
            Group g = (Group) HFactoryImpl.getInstance()
                    .getSession().get(Group.class, group);
            AuthenticationSourceBean l = new AuthenticationSourceBean();
            l.setJavaClass(this.auth.getClass().getName());
            l.setName(this.auth.getName());
            u.getGroups().add(g);
            HFactoryImpl.getInstance()
                    .getSession().save(u);
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public boolean authenticate(String uid, String password)
            throws DataSourceException, ConfigException
    {

        String md5Password = new MD5Generator().generatePassword(password);
        String rq = "from User where uid = :uid AND password= :cryptpwd AND authenticationSourceName=:authname";

        try {
            Object u = HFactoryImpl.getInstance()
                    .getSession().createQuery(rq)
                    .setString("uid", uid)
                    .setString("cryptpwd", md5Password)
                    .setString("authname", this.getAuth().getName())
                    .uniqueResult();

            if (u == null) {
                log.debug("Unable to authenticate user: \"" + uid + "\" is unknown");
                return false;
            }

            if (!((User) u).isEnabled()) {
                log.debug("Unable to authenticate user: \"" + uid + "\" is disabled");
                return false;
            }

            Date lDate = new Date();
            String query = "update User set lastLogin=:ldate where uid=:uid AND authenticationSourceName=:authname";
            HFactoryImpl.getInstance()
                    .getSession().createQuery(query)
                    .setString("uid", uid)
                    .setString("authname", this.getAuth().getName())
                    .setDate("ldate", lDate)
                    .executeUpdate();
            return true;

        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public void deleteUser(User user) throws DataSourceException,
            ConfigException
    {
        try {
            HFactoryImpl.getInstance().getSession().delete(user);
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public User getUser(String uid) throws DataSourceException, ConfigException
    {
        try {
            User u = (User) HFactoryImpl.getInstance()
                    .getSession()
                    .createCriteria(User.class).add(Restrictions.eq("uid", uid))
                    .add(Restrictions.eq("authenticationSourceName", this.getAuth().getName())).uniqueResult();

            return u;
        } catch (HibernateException he) {
            throw new DataSourceException(he);
        }
    }

    public Vector<User> getUsers(Group group) throws DataSourceException,
            ConfigException
    {
        try {
            Set<User> sUsers = group.getUsers();
            Vector<User> users = new Vector<User>();
            for (User u : sUsers) {
                users.add(u);
            }
            return users;
        } catch (HibernateException he) {
            throw new DataSourceException(he);
        }
    }

    public Vector<User> getUsers() throws DataSourceException, ConfigException
    {
        try {
            Vector<User> vUsers = new Vector<User>();
            List<User> lUsers = (List<User>) HFactoryImpl.getInstance().getSession().createCriteria(User.class)
                    .add(Restrictions.eq("authenticationSourceName", this.getAuth().getName()))
                    .addOrder(Order.asc("uid"))
                    .list();
            for (User u : lUsers) {
                vUsers.add(u);
            }
            return vUsers;
        } catch (HibernateException he) {
            throw new DataSourceException(he);
        }
    }

    public void removeUserFromGroup(User user, Group group)
            throws DataSourceException, ConfigException
    {
        try {
            User u = (User) HFactoryImpl.getInstance().getSession().load(User.class, user);
            Group g = (Group) HFactoryImpl.getInstance().getSession().load(Group.class, group);
            u.getGroups().remove(g);
            HFactoryImpl.getInstance().getSession().update(u);
        } catch (HibernateException he) {
            throw new DataSourceException(he, he.getMessage());
        }
    }

    public void saveUser(User user, String password)
            throws DataSourceException, ConfigException
    {
        try {
            HFactoryImpl.getInstance()
                    .getSession().save(user);
            HFactoryImpl.getInstance()
                    .getSession().flush();

            String query =
                    "update User set password = :cryptwd WHERE uid= :uid AND authenticationSourceName like :authname";

            HFactoryImpl.getInstance()
                    .getSession()
                    .createQuery(query)
                    .setString("cryptwd",
                            FactoryInstantiator.getInstance().getCredentialsGenerator().generatePassword(password))
                    .setString("uid", user.getID())
                    .setString("authname", this.getAuth().getName())
                    .executeUpdate();
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public void updateUser(User user, String password)
            throws DataSourceException, ConfigException
    {

        try {
            HFactoryImpl.getInstance().getSession().update(user);
            HFactoryImpl.getInstance().getSession().flush();
            if (password != null && !password.equals("")) {
                String query =
                        "update User SET password = :cryptwd WHERE uid= :uid AND authenticationSourceName like :authname";
                HFactoryImpl.getInstance().getSession().createQuery(query)
                        .setString("cryptwd",
                                FactoryInstantiator.getInstance().getCredentialsGenerator().generatePassword(password))
                        .setString("uid", user.getID())
                        .setString("authname", this.getAuth().getName())
                        .executeUpdate();
            }
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public Object getAttribute(User user, String attributeName)
            throws DataSourceException, ConfigException
    {
        return user.getAttributes().get(attributeName);
    }

    public void setAttribute(User user, String attributeName,
            Object attributeValue) throws DataSourceException, ConfigException
    {
        user.getAttributes().put(attributeName, attributeValue.toString());
        log.debug(
                "attribute set for " + user.getID() + " " + user.getAuthenticationSourceName() + ": " + attributeName +
                        " --> " + attributeValue);
        HFactoryImpl.getInstance().getSession().saveOrUpdate(user);
    }

    public Map<String, String> getAttributes(User user)
            throws DataSourceException, ConfigException
    {
        return user.getAttributes();
    }

    public User getUserByAttributeValue(String attributeName, String attributeValue)
            throws DataSourceException, ConfigException
    {
        String query = "from User where attributes['" + attributeName + "'] = :attributeValue";
        return (User) HFactoryImpl.getInstance().getSession().createQuery(query)
                .setString("attributeValue", attributeValue)
                .uniqueResult();
    }

    public User getUserByEmail(String emailAddress) throws DataSourceException, ConfigException {
        String query = "from User where mail = :email";
        List<User> users = HFactoryImpl.getInstance().getSession().createQuery(query)
                .setString("email", emailAddress)
                .list();

        if(users.size() == 1){
            return users.get(0);
        } else {
            return null;
        }

    }
}


