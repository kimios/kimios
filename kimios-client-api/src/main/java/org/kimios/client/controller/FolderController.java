/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
 *
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.client.controller;

import org.kimios.client.exception.AccessDeniedException;
import org.kimios.client.exception.ConfigException;
import org.kimios.client.exception.ExceptionHelper;
import org.kimios.client.exception.DMSException;
import org.kimios.kernel.ws.pojo.Folder;
import org.kimios.kernel.ws.pojo.Workspace;
import org.kimios.webservices.FolderService;

/**
 * FolderController is used to create, update, delete and get folders
 */
public class FolderController
{

    private FolderService client;

    public FolderService getClient()
    {
        return client;
    }

    public void setClient( FolderService client )
    {
        this.client = client;
    }

    /**
     * Get folder for a given parent workspace
     */
    public Folder[] getFolders( String sessionId, Workspace w )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            return client.getFolders( sessionId, w.getUid(), 1 );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Get folder for a given parent folder
     */
    public Folder[] getFolders( String sessionId, Folder f )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            return client.getFolders( sessionId, f.getUid(), 2 );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Get folder for a given parent (workspace or folder)
     */
    public Folder[] getFolders( String sessionId, long parentId, int parentType )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            return client.getFolders( sessionId, parentId, parentType );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Get folder
     */
    public Folder getFolder( String sessionId, long folderId )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            return client.getFolder( sessionId, folderId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Create a new folder in a given parent folder
     */
    public long createFolder( String sessionId, Folder f, boolean isSecurityInherited )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            return client.createFolder( sessionId, f.getName(), f.getParentUid(), f.getParentType(),
                                        isSecurityInherited );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Update folder (for name and/or parent change) for a given folder
     */
    public void updateFolder( String sessionId, Folder f )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            client.updateFolder( sessionId, f.getUid(), f.getName(), f.getParentUid(), f.getParentType() );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Remove folder of given id
     */
    public void deleteFolder( String sessionId, long folderId )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            client.deleteFolder( sessionId, folderId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }
}

