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

import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.kimios.exceptions.ConfigException;
import org.kimios.exceptions.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.security.FactoryInstantiator;
import org.kimios.kernel.security.pwdgen.md5.MD5Generator;
import org.kimios.kernel.user.model.AuthenticationSourceBean;
import org.kimios.kernel.user.model.Group;
import org.kimios.kernel.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class HInternalUserFactory extends HFactory {


    private static Logger log = LoggerFactory.getLogger(HInternalUserFactory.class);
    
    public void addUserToGroup(User user, Group group, String sourceName)
            throws DataSourceException, ConfigException {
        try {

            User u = (User) 
                    getSession().get(User.class, user);
            Group g = (Group) 
                    getSession().get(Group.class, group);
            AuthenticationSourceBean l = new AuthenticationSourceBean();
            l.setJavaClass(sourceName);
            l.setName(sourceName);
            u.getGroups().add(g);
            
                    getSession().save(u);
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public boolean authenticate(String uid, String password, String sourceName)
            throws DataSourceException, ConfigException {



        if(log.isDebugEnabled())
            log.debug("authenticating {} {} ", uid, sourceName);
        String md5Password = new MD5Generator().generatePassword(password);
        String rq = "from User where lower(uid) = :uid AND password= :cryptpwd AND authenticationSourceName=:authname";

        try {
            Object u = 
                    getSession().createQuery(rq)
                    .setString("uid", uid.toLowerCase())
                    .setString("cryptpwd", md5Password)
                    .setString("authname", sourceName)
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
            
                    getSession().createQuery(query)
                    .setString("uid", uid)
                    .setString("authname", sourceName)
                    .setDate("ldate", lDate)
                    .executeUpdate();
            return true;

        } catch (HibernateException e) {
            log.error("error while auth", e);
            throw new DataSourceException(e, e.getMessage());
        }
        catch (Exception e) {
            log.error("error while auth", e);
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public void deleteUser(User user) throws DataSourceException,
            ConfigException {
        try {
            User u = (User) getSession()
                    .createCriteria(User.class)
                    .add(Restrictions.eq("uid", user.getUid()).ignoreCase())
                    .add(Restrictions.eq("authenticationSourceName", user.getAuthenticationSourceName()))
                    .uniqueResult();
            getSession().delete(u);
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public User getUser(String uid, String sourceName) throws DataSourceException, ConfigException {
        try {
            User u = (User) 
                    getSession()
                    .createCriteria(User.class)
                    .add(Restrictions.eq("uid", uid).ignoreCase())
                    .add(Restrictions.eq("authenticationSourceName", sourceName)).uniqueResult();

            return u;
        } catch (HibernateException he) {
            throw new DataSourceException(he);
        }
    }

    public Vector<User> getUsers(Group group) throws DataSourceException,
            ConfigException {
        try {
            Set<User> sUsers = group.getUsers();
            initializeAndUnproxy(group.getUsers());
            Vector<User> users = new Vector<User>();
            for (User u : sUsers) {
                User loadedUser = getUser(u.getID(), u.getAuthenticationSourceName());
                initializeAndUnproxy(loadedUser.getEmails());
                users.add(loadedUser);
            }
            return users;
        } catch (HibernateException he) {
            throw new DataSourceException(he);
        }
    }

    public Vector<User> getUsers(String sourceName) throws DataSourceException, ConfigException {
        try {
            Vector<User> vUsers = new Vector<User>();
            List<User> lUsers = (List<User>) getSession().createCriteria(User.class)
                    .add(Restrictions.eq("authenticationSourceName", sourceName))
                    .addOrder(Order.asc("uid"))
                    .list();
            for (User u : lUsers) {
                initializeAndUnproxy(u.getEmails());
                vUsers.add(u);
            }
            return vUsers;
        } catch (HibernateException he) {
            throw new DataSourceException(he);
        }
    }

    public void removeUserFromGroup(User user, Group group)
            throws DataSourceException, ConfigException {
        try {
            User u = (User) getSession().load(User.class, user);
            Group g = (Group) getSession().load(Group.class, group);
            u.getGroups().remove(g);
            getSession().update(u);
        } catch (HibernateException he) {
            throw new DataSourceException(he, he.getMessage());
        }
    }

    public void saveUser(User user, String password)
            throws DataSourceException, ConfigException {
        try {
            
                    getSession().save(user);
            
                    getSession().flush();

            String query =
                    "update User set password = :cryptwd WHERE lower(uid)= :uid AND authenticationSourceName like :authname";

            
                    getSession()
                    .createQuery(query)
                    .setString("cryptwd",
                            FactoryInstantiator.getInstance().getCredentialsGenerator().generatePassword(password))
                    .setString("uid", user.getID().toLowerCase())
                    .setString("authname", user.getAuthenticationSourceName())
                    .executeUpdate();
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public void updateUser(User user, String password)
            throws DataSourceException, ConfigException {

        try {
            getSession().update(user);
            getSession().flush();
            if (password != null && !password.equals("")) {
                String query =
                        "update User SET password = :cryptwd WHERE lower(uid) = :uid AND authenticationSourceName like :authname";
                getSession().createQuery(query)
                        .setString("cryptwd",
                                FactoryInstantiator.getInstance().getCredentialsGenerator().generatePassword(password))
                        .setString("uid", user.getID().toLowerCase())
                        .setString("authname", user.getAuthenticationSourceName())
                        .executeUpdate();
            }
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public Object getAttribute(User user, String attributeName)
            throws DataSourceException, ConfigException {
        return user.getAttributes().get(attributeName);
    }

    public void setAttribute(User user, String attributeName,
                             Object attributeValue) throws DataSourceException, ConfigException {
        user.getAttributes().put(attributeName, attributeValue.toString());
        log.debug(
                "attribute set for " + user.getID() + " " + user.getAuthenticationSourceName() + ": " + attributeName +
                        " --> " + attributeValue);
        getSession().saveOrUpdate(user);
    }

    public Map<String, String> getAttributes(User user)
            throws DataSourceException, ConfigException {
        return user.getAttributes();
    }

    public User getUserByAttributeValue(String attributeName, String attributeValue)
            throws DataSourceException, ConfigException {
        String query = "from User where attributes['" + attributeName + "'] = :attributeValue";
        return (User) getSession().createQuery(query)
                .setString("attributeValue", attributeValue)
                .uniqueResult();
    }

    public User getUserByEmail(String emailAddress) throws DataSourceException, ConfigException {
        String query = "select distinct u from User as u where lower(u.mail) = :email or '" + emailAddress + "' in elements(u.emails)";
        List<User> users = getSession().createQuery(query)
                .setString("email", emailAddress)
                .list();
        if (users.size() > 0) {
            return users.get(0);
        } else {
            return null;
        }

    }

    public void addUserEmails(String uid, String sourceName, List<String> emails) {
        try {
            User u = (User) 
                    getSession()
                    .createCriteria(User.class).add(Restrictions.eq("uid", uid).ignoreCase())
                    .add(Restrictions.eq("authenticationSourceName", sourceName)).uniqueResult();

            u.getEmails().clear();
            u.getEmails().addAll(emails);
            getSession().saveOrUpdate(u);
        } catch (HibernateException he) {
            throw new DataSourceException(he);
        }
    }

    public List<User> searchUsers(String searchText, String sourceName)
            throws DataSourceException, ConfigException {
        List<User> initializedUsers = new ArrayList<User>();
        String searchPattern = "%" + searchText + "%";
        try {
            List<User> list = (List<User>) getSession()
                    .createCriteria(User.class)
                    .add(Restrictions.eq("authenticationSourceName", sourceName))
                    .add(Restrictions.disjunction()
                            .add(Restrictions.like("uid", searchPattern).ignoreCase())
                            .add(Restrictions.like("name", searchPattern).ignoreCase())
                            .add(Restrictions.like("firstName", searchPattern).ignoreCase())
                            .add(Restrictions.like("mail", searchPattern).ignoreCase()))
                    .list();

            for (User u : list) {
                initializeAndUnproxy(u.getEmails());
                initializedUsers.add(u);
            }

            return initializedUsers;
        } catch (HibernateException he) {
            throw new DataSourceException(he);
        }
    }
}