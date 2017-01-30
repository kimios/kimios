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
package org.kimios.kernel.user.impl.factory.hibernate;

import org.kimios.exceptions.ConfigException;
import org.kimios.exceptions.DataSourceException;
import org.kimios.kernel.user.impl.HAuthenticationSource;
import org.kimios.kernel.user.model.AuthenticationSource;
import org.kimios.kernel.user.model.Group;
import org.kimios.kernel.user.model.User;
import org.kimios.kernel.user.model.UserFactory;

import java.util.List;
import java.util.Map;
import java.util.Vector;

public class HUserFactory implements UserFactory
{
    private AuthenticationSource auth;

    private HInternalUserFactory internalUserFactory;

    public HUserFactory(HInternalUserFactory internalUserFactory, HAuthenticationSource source){
        //get internal user database factory
        this.internalUserFactory = internalUserFactory;
        this.auth = source;
    }

    public void addUserToGroup(User user, Group group)
            throws DataSourceException, ConfigException
    {
        internalUserFactory.addUserToGroup(user, group, this.auth.getName());
    }

    public boolean authenticate(String uid, String password)
            throws DataSourceException, ConfigException
    {

        return internalUserFactory.authenticate(uid, password, this.auth.getName());
    }

    public void deleteUser(User user) throws DataSourceException,
            ConfigException
    {
        internalUserFactory.deleteUser(user);
    }

    public User getUser(String uid) throws DataSourceException, ConfigException
    {
        return internalUserFactory.getUser(uid, this.auth.getName());
    }

    public Vector<User> getUsers(Group group) throws DataSourceException,
            ConfigException
    {
        return internalUserFactory.getUsers(group);
    }

    public Vector<User> getUsers() throws DataSourceException, ConfigException
    {
        return internalUserFactory.getUsers(this.auth.getName());
    }

    public void removeUserFromGroup(User user, Group group)
            throws DataSourceException, ConfigException
    {
        internalUserFactory.removeUserFromGroup(user, group);
    }

    public void saveUser(User user, String password)
            throws DataSourceException, ConfigException
    {
        internalUserFactory.saveUser(user, password);
    }

    public void updateUser(User user, String password)
            throws DataSourceException, ConfigException
    {

        internalUserFactory.updateUser(user, password);
    }

    public Object getAttribute(User user, String attributeName)
            throws DataSourceException, ConfigException
    {
        return internalUserFactory.getAttribute(user, attributeName);
    }

    public void setAttribute(User user, String attributeName,
            Object attributeValue) throws DataSourceException, ConfigException
    {
        internalUserFactory.setAttribute(user, attributeName, attributeValue);
    }

    public Map<String, String> getAttributes(User user)
            throws DataSourceException, ConfigException
    {
        return internalUserFactory.getAttributes(user);
    }

    public User getUserByAttributeValue(String attributeName, String attributeValue)
            throws DataSourceException, ConfigException
    {
        return internalUserFactory.getUserByAttributeValue(attributeName, attributeValue);
    }

    public User getUserByEmail(String emailAddress) throws DataSourceException, ConfigException {

        return internalUserFactory.getUserByEmail(emailAddress);
    }


    @Override
    public void addUserEmails(String uid, List<String> emails) {
        internalUserFactory.addUserEmails(uid, this.auth.getName(), emails);
    }

    public List<User> searchUsers(String searchText, String sourceName) {
        return internalUserFactory.searchUsers(searchText, sourceName);
    }
}


