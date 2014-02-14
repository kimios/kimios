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
package org.kimios.kernel.controller;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.Workspace;
import org.kimios.kernel.events.annotations.DmsEvent;
import org.kimios.kernel.events.annotations.DmsEventName;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.NamingException;
import org.kimios.kernel.log.DMEntityLog;
import org.kimios.kernel.security.Session;

import java.util.List;
import java.util.Vector;

public interface IWorkspaceController
{
    /**
     * Return workspace for a given id
     *
     * @return workspace
     */
    public Workspace getWorkspace(Session session, long workspaceUid)
            throws ConfigException, DataSourceException, AccessDeniedException;

    /**
     * Return workspace for a given name
     *
     * @return workspace
     */
    public Workspace getWorkspace(Session session, String workspaceName)
            throws ConfigException, DataSourceException, AccessDeniedException;

    /**
     * Get workspace readable by a given user
     *
     * @return workspace list
     */
    public List<Workspace> getWorkspaces(Session session)
            throws ConfigException, DataSourceException;

    /**
     * Create new workspace and return id
     *
     * @return workspace id
     */
    @DmsEvent(eventName = { DmsEventName.WORKSPACE_CREATE })
    public long createWorkspace(Session session, String name)
            throws NamingException, ConfigException, DataSourceException,
            AccessDeniedException;

    /**
     * Update workspace
     */
    @DmsEvent(eventName = { DmsEventName.WORKSPACE_UPDATE })
    public void updateWorkspace(Session session, long workspaceUid, String name)
            throws NamingException, ConfigException, DataSourceException,
            AccessDeniedException;

    /**
     * Delete workspace (and children)
     */
    @DmsEvent(eventName = { DmsEventName.WORKSPACE_DELETE })
    public void deleteWorkspace(Session session, long workspaceUid)
            throws ConfigException, DataSourceException, AccessDeniedException;

    /**
     * Get Dms Logs for a given workspace
     */
    public Vector<DMEntityLog<Workspace>> getLogs(Session session,
            long workspaceUid) throws AccessDeniedException, ConfigException,
            DataSourceException;
}
