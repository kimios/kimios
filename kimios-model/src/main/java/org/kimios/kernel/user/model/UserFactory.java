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
package org.kimios.kernel.user.model;

import org.kimios.exceptions.ConfigException;
import org.kimios.exceptions.DataSourceException;

import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * UserFactory interface designs the objects that will instantiate user objects
 *
 * @author Louis Sicard
 */
public interface UserFactory
{
    public boolean authenticate(String uid, String password) throws DataSourceException, ConfigException;

    public User getUser(String uid) throws DataSourceException, ConfigException;

    public User getUserByEmail(String emailAddress) throws DataSourceException, ConfigException;

    public Vector<User> getUsers(Group group) throws DataSourceException, ConfigException;

    public Vector<User> getUsers() throws DataSourceException, ConfigException;

    public void saveUser(User user, String password) throws DataSourceException, ConfigException;

    public void updateUser(User user, String password) throws DataSourceException, ConfigException;

    public void deleteUser(User user) throws DataSourceException, ConfigException;

    public void addUserToGroup(User user, Group group) throws DataSourceException, ConfigException;

    public void removeUserFromGroup(User user, Group group) throws DataSourceException, ConfigException;

    public Object getAttribute(User user, String attributeName) throws DataSourceException, ConfigException;

    public void setAttribute(User user, String attributeName, Object attributeValue)
            throws DataSourceException, ConfigException;

    public Map<String, String> getAttributes(User user) throws DataSourceException, ConfigException;

    public User getUserByAttributeValue(String attributeName, String attributeValue)
            throws DataSourceException, ConfigException;

    public void addUserEmails(String uid, List<String> emails);

    public List<User> searchUsers(String searchText, String sourceName);
}

