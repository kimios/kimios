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
package org.kimios.client.controller;

import org.kimios.client.exception.AccessDeniedException;
import org.kimios.client.exception.ConfigException;
import org.kimios.client.exception.DMSException;
import org.kimios.client.exception.ExceptionHelper;
import org.kimios.kernel.ws.pojo.Workspace;
import org.kimios.webservices.WorkspaceService;

/**
 * WorkspaceController is used to create, update, delete and get workspaces
 */
public class WorkspaceController
{

    private WorkspaceService client;

    public WorkspaceService getClient()
    {
        return client;
    }

    public void setClient( WorkspaceService client )
    {
        this.client = client;
    }

    /**
     * Get workspace readable by a given user
     */
    public Workspace[] getWorkspaces( String sessionId )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            return client.getWorkspaces( sessionId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Return workspace for a given id
     */
    public Workspace getWorkspace( String sessionId, long workspaceId )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            return client.getWorkspace( sessionId, workspaceId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Create new workspace and return id
     */
    public long createWorkspace( String sessionId, Workspace w )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            return client.createWorkspace( sessionId, w.getName() );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Update workspace
     */
    public void updateWorkspace( String sessionId, Workspace w )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            client.updateWorkspace( sessionId, w.getUid(), w.getName() );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Delete workspace (and children)
     */
    public void deleteWorkspace( String sessionId, long workspaceId )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            client.deleteWorkspace( sessionId, workspaceId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }
}

