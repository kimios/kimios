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
package org.kimios.kernel.dms;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.model.DMEntity;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.user.model.Group;

import java.util.List;

public interface BookmarkFactory
{
    public void addBookmark(String userName, String userSource, int securityEntityType, long dmentityUid, int dmentityType)
            throws ConfigException,
            DataSourceException;

    public void removeBookmark(String userName, String userSource, int securityEntityType, long dmentityUid, int dmentityType)
            throws ConfigException, DataSourceException;

    public List<DMEntity> getUserBookmarks(String userName, String userSource)
            throws ConfigException, DataSourceException;

    public List<DMEntity> getBookmarks(String userName, String userSource, List<Group> groups)
            throws ConfigException, DataSourceException;

    public List<DMEntity> getBookmarks(String userName, String userSource, List<Group> groups, String path)
            throws ConfigException, DataSourceException;
}

