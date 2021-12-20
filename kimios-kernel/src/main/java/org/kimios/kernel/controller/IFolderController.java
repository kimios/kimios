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
package org.kimios.kernel.controller;

import org.kimios.api.events.annotations.DmsEvent;
import org.kimios.api.events.annotations.DmsEventName;
import org.kimios.exceptions.AccessDeniedException;
import org.kimios.exceptions.ConfigException;
import org.kimios.exceptions.DataSourceException;
import org.kimios.exceptions.NamingException;
import org.kimios.exceptions.TreeException;
import org.kimios.kernel.dms.model.Folder;
import org.kimios.kernel.dms.model.MetaValue;
import org.kimios.kernel.log.model.DMEntityLog;
import org.kimios.kernel.security.model.Session;

import java.util.List;
import java.util.Map;
import java.util.Vector;

public interface IFolderController {
    /**
     * Get folder for a given id
     */
    public Folder getFolder(Session session, long folderUid)
            throws ConfigException, DataSourceException, AccessDeniedException;

    /**
     * Get folder for a given name and parent (workspace or folder)
     */
    public Folder getFolder(Session session, String name, long parentUid, int parentType)
            throws ConfigException, DataSourceException, AccessDeniedException;

    /**
     * Get folder for a given parent (workspace or folder)
     */
    public List<Folder> getFolders(Session session, long parentUid)
            throws ConfigException, DataSourceException, AccessDeniedException;

    /**
     * Create a new folder in a given parent (workspace or folder)
     */
    @DmsEvent(eventName = {DmsEventName.FOLDER_CREATE})
    public long createFolder(Session session, String name, long parentUid, boolean isSecurityInherited)
            throws NamingException, ConfigException, DataSourceException,
            AccessDeniedException;

    /**
     * Update folder (for name and/or parent change) for a given id
     */
    @DmsEvent(eventName = {DmsEventName.FOLDER_UPDATE})
    public void updateFolder(Session session, long folderUid, String name, long parentUid)
            throws NamingException, TreeException, AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Remove folder of given id
     */
    @DmsEvent(eventName = {DmsEventName.FOLDER_DELETE})
    public boolean deleteFolder(Session session, long folderUid)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Get Dms Logs for a given folder id
     */
    public Vector<DMEntityLog<Folder>> getLogs(Session session, long folderUid)
            throws AccessDeniedException, ConfigException, DataSourceException;

    @DmsEvent(eventName = {DmsEventName.FOLDER_CREATE})
    public long createVirtualFolder(Session session, Long id, String name, Long parentId, List<MetaValue> metaValues)
            throws NamingException, ConfigException, DataSourceException, AccessDeniedException;

    public List<MetaValue> listMetaValues(Session session, long folderId)
            throws ConfigException, DataSourceException, AccessDeniedException;

    public Map<org.kimios.kernel.ws.pojo.Folder, List<org.kimios.kernel.ws.pojo.MetaValue>>
    getFolderWithMetaDatas(Session session, List<Long> folders)
            throws ConfigException, DataSourceException, AccessDeniedException;

    public boolean canBeMoved(Session session, long folderId) throws ConfigException, DataSourceException;

    public boolean isWriteable(Session session, long folderId) throws ConfigException, DataSourceException;
}
