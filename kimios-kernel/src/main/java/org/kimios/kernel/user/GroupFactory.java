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
package org.kimios.kernel.user;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;

import java.util.Vector;

/**
 * UserFactory interface designs the objects that will instantiate user objects
 *
 * @author Louis Sicard
 */
public interface GroupFactory
{
    public Group getGroup(String gid) throws DataSourceException, ConfigException;

    public Vector<Group> getGroups() throws DataSourceException, ConfigException;

    public Vector<Group> getGroups(String userUid) throws DataSourceException, ConfigException;

    public void saveGroup(Group group) throws DataSourceException, ConfigException;

    public void updateGroup(Group group) throws DataSourceException, ConfigException;

    public void deleteGroup(Group group) throws DataSourceException, ConfigException;
}

