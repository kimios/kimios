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
import org.kimios.webservices.AdministrationService;
import org.kimios.kernel.ws.pojo.AuthenticationSource;
import org.kimios.kernel.ws.pojo.Document;
import org.kimios.kernel.ws.pojo.Group;
import org.kimios.kernel.ws.pojo.Role;
import org.kimios.kernel.ws.pojo.Session;
import org.kimios.kernel.ws.pojo.User;

/**
 * Here are all of the administration functionalities (domain management, user
 * and group, roles)
 */
public class AdministrationController
{

    private AdministrationService client;

    public AdministrationService getClient()
    {
        return client;
    }

    public void setClient( AdministrationService client )
    {
        this.client = client;
    }

    /**
     * Return users from specified roles
     */
    public Role[] getRoles( String sessionId, int role )
        throws Exception
    {
        try
        {
            return client.getRoles( sessionId, role );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Return roles from specified user
     */
    public Role[] getRoles( String sessionId, String userName, String userSource )
        throws Exception
    {
        try
        {
            return client.getUserRoles( sessionId, userName, userSource );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Add a role for the user specified by user name and source
     */
    public void createRole( String sessionId, int role, String userName, String userSource )
        throws Exception, AccessDeniedException, ConfigException, DMSException
    {
        try
        {
            client.createRole( sessionId, role, userName, userSource );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }

    }

    /**
     * Remove the user specified by user name and source for the role
     */
    public void deleteRole( String sessionId, int role, String userName, String userSource )
        throws Exception, AccessDeniedException, ConfigException, DMSException
    {
        try
        {
            client.deleteRole( sessionId, role, userName, userSource );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Get authentication source for a specified authentication source name
     */
    public AuthenticationSource getAuthenticationSource( String sessionId, String name )
        throws Exception, AccessDeniedException, ConfigException, DMSException
    {
        try
        {
            return client.getAuthenticationSource( sessionId, name );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Get authentication source parameters XML stream from authentication source name
     */
    public String getAuthenticationSourceParams( String sessionId, String name, String className )
        throws Exception
    {
        try
        {
            return client.getAuthenticationSourceParams( sessionId, name, className );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Create a new generic Authentication Source
     */
    public void createAuthenticationSource( String sessionId, String authenticationSourceName, String className,
                                            String xmlParameters )
        throws Exception
    {
        try
        {
            client.createAuthenticationSource( sessionId, authenticationSourceName, className, xmlParameters );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Update authentication source
     */
    public void updateAuthenticationSource( String sessionId, String authenticationSourceName,
                                            String newAuthenticationSourceName, String className, String xmlParameters )
        throws Exception
    {
        try
        {
            client.updateAuthenticationSource( sessionId, authenticationSourceName, newAuthenticationSourceName,
                                               className, xmlParameters );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Delete authentication source
     */
    public void deleteAuthenticationSource( String sessionId, String name )
        throws Exception
    {
        try
        {
            client.deleteAuthenticationSource( sessionId, name );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Get a class names list of all implemented authentication sources
     */
    public String getAvailableAuthenticationSource( String sessionId )
        throws Exception
    {
        try
        {
            return client.getAvailableAuthenticationSource( sessionId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Get a field names list for a given implemented authentication source
     */
    public String getAvailableAuthenticationSourceParams( String sessionId, String className )
        throws Exception
    {
        try
        {
            return client.getAvailableAuthenticationSourceParams( sessionId, className );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Add an user to a DMS Authentication Source
     */
    public void createUser( String sessionId, String uid, String userName, String mail, String password,
                            String authenticationSourceName )
            throws Exception
    {
       createUser(sessionId, uid, userName, mail, password, authenticationSourceName, true);
    }

    /**
     * Add an user to a DMS Authentication Source
     */
    public void createUser( String sessionId, String uid, String userName, String mail, String password,
                            String authenticationSourceName, boolean enabled )
        throws Exception
    {
        try
        {
            client.createUser( sessionId, uid, userName, mail, password, authenticationSourceName, enabled );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Update user information in a DMS Authentication source
     */
    public void updateUser( String sessionId, String uid, String userName, String mail, String password,
                            String authenticationSourceName )
            throws Exception
    {
        updateUser(sessionId, uid, userName, mail, password, authenticationSourceName, true);
    }

    /**
     * Update user information in a DMS Authentication source
     */
    public void updateUser( String sessionId, String uid, String userName, String mail, String password,
                            String authenticationSourceName, boolean enabled )
        throws Exception
    {
        try
        {
            client.updateUser( sessionId, uid, userName, mail, password, authenticationSourceName, enabled );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Delete a DMS Authentication Source
     */
    public void deleteUser( String sessionId, String uid, String authenticationSourceName )
        throws Exception
    {
        try
        {
            client.deleteUser( sessionId, uid, authenticationSourceName );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Create a  Group in a standard Authentication Source
     */
    public void createGroup( String sessionId, String gid, String name, String authenticationSourceName )
        throws Exception
    {
        try
        {
            client.createGroup( sessionId, gid, name, authenticationSourceName );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Update Group information in a standard DMS Authentication Source
     */
    public void updateGroup( String sessionId, String gid, String name, String authenticationSourceName )
        throws Exception
    {
        try
        {
            client.updateGroup( sessionId, gid, name, authenticationSourceName );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Remove a group from a DMS Authentication Source
     */
    public void deleteGroup( String sessionId, String gid, String authenticationSourceName )
        throws Exception
    {
        try
        {
            client.deleteGroup( sessionId, gid, authenticationSourceName );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Add a user to a group in a  Authentication Source
     */
    public void addUserToGroup( String sessionId, String uid, String gid, String authenticationSourceName )
        throws Exception, AccessDeniedException, ConfigException, DMSException
    {
        try
        {
            client.addUserToGroup( sessionId, uid, gid, authenticationSourceName );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Remove a DMS User from a DMS  group
     */
    public void removeUserFromGroup( String sessionId, String uid, String gid, String authenticationSourceName )
        throws Exception, AccessDeniedException, ConfigException, DMSException
    {
        try
        {
            client.removeUserFromGroup( sessionId, uid, gid, authenticationSourceName );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Get user from an Authentication Source
     */
    public User getUser( String sessionId, String uid, String authenticationSourceName )
        throws Exception, AccessDeniedException, ConfigException, DMSException
    {
        try
        {
            return client.getManageableUser( sessionId, uid, authenticationSourceName );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Get users from a gid and authentication source
     */
    public User[] getUsers( String sessionId, String gid, String authenticationSourceName )
        throws Exception
    {
        try
        {
            return client.getManageableUsers( sessionId, gid, authenticationSourceName );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Get group from an Authentication Source
     */
    public Group getGroup( String sessionId, String gid, String authenticationSourceName )
        throws Exception, AccessDeniedException, ConfigException, DMSException
    {
        try
        {
            return client.getManageableGroup( sessionId, gid, authenticationSourceName );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Get groups from an authentication source
     */
    public Group[] getGroups( String sessionId, String userId, String authenticationSourceName )
        throws Exception, AccessDeniedException, ConfigException, DMSException
    {
        try
        {
            return client.getManageableGroups( sessionId, userId, authenticationSourceName );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Reindex the entire repository
     */
    public void reIndex( String sessionId, String path )
        throws Exception, AccessDeniedException, ConfigException, DMSException
    {
        try
        {
            client.reindex( sessionId, path );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Get the reindex progression %
     */
    public int getReIndexProgress( String sessionId )
        throws Exception
    {
        try
        {
            return client.getReindexProgress( sessionId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Get all the documents dead locked
     */
    public Document[] getCheckedOutDocuments( String sessionId )
        throws Exception
    {
        try
        {
            return client.getCheckedOutDocuments( sessionId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Clears dead lock on the given document
     */
    public void clearLock( String sessionId, long documentId )
        throws Exception
    {
        try
        {
            client.clearLock( sessionId, documentId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Changes the owner of a dmentity
     */
    public void changeOwnerShip( String sessionId, long dmEntityId, int dmEntityType, String userName,
                                 String userSource )
        throws Exception
    {
        try
        {
            client.changeOwnership( sessionId, dmEntityId, userName, userSource );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Return the connected users list
     */
    public User[] getConnectedUsers( String sessionId )
        throws Exception
    {
        try
        {
            return client.getConnectedUsers( sessionId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Return the enabled sessions list
     */
    public Session[] getAllEnabledSessions( String sessionId )
        throws Exception
    {
        try
        {
            return client.getAllEnabledSessions( sessionId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Return the enabled sessions list for a given user
     */
    public Session[] getEnabledSessions( String sessionId, String userName, String userSource )
        throws Exception
    {
        try
        {
            return client.getEnabledSessions( sessionId, userName, userSource );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Remove an enabled session
     */
    public void removeEnabledSession( String sessionId, String sessionIdToRemove )
        throws Exception
    {
        try
        {
            client.removeEnabledSession( sessionId, sessionIdToRemove );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Remove enabled sessions for a given user
     */
    public void removeEnabledSessions( String sessionId, String userName, String userSource )
        throws Exception
    {
        try
        {
            client.removeEnabledSessions( sessionId, userName, userSource );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }
}

