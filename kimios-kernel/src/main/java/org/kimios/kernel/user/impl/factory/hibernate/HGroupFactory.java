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
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.user.impl.HAuthenticationSource;
import org.kimios.kernel.user.model.AuthenticationSource;
import org.kimios.kernel.user.model.Group;
import org.kimios.kernel.user.model.GroupFactory;

import java.util.List;
import java.util.Vector;

public class HGroupFactory implements GroupFactory
{
    public AuthenticationSource auth;

    private HInternalGroupFactory internalGroupFactory;

    private HInternalUserFactory internalUserFactory;

    public HGroupFactory(HInternalGroupFactory internalGroupFactory,
                         HInternalUserFactory internalUserFactory,
                         HAuthenticationSource hAuthenticationSource){
        //get internal user and group database factory
        this.internalGroupFactory = internalGroupFactory;
        this.internalUserFactory = internalUserFactory;
        this.auth = hAuthenticationSource;
    }

    public void deleteGroup(Group group) throws DataSourceException,
            ConfigException
    {
        internalGroupFactory.deleteGroup(group, internalUserFactory);
    }

    public List<Group> searchGroups(String searchText) throws DataSourceException, ConfigException {
        return internalGroupFactory.searchGroups(searchText);
    }

    public Group getGroup(String gid) throws DataSourceException,
            ConfigException
    {
        return internalGroupFactory.getGroup(gid, this.auth.getName());
    }

    public Vector<Group> getGroups() throws DataSourceException,
            ConfigException
    {
        return internalGroupFactory.getGroups(this.auth.getName());
    }

    public Vector<Group> getGroups(String userUid) throws DataSourceException,
            ConfigException
    {
        return internalGroupFactory.getGroupsForUser(userUid, this.auth.getName());
    }

    public void saveGroup(Group group) throws DataSourceException,
            ConfigException
    {
        internalGroupFactory.saveGroup(group);
    }

    public void updateGroup(Group group) throws DataSourceException,
            ConfigException
    {
        internalGroupFactory.updateGroup(group);
    }
}

