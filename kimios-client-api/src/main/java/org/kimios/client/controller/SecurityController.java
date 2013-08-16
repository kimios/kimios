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

import org.kimios.client.controller.helpers.StringTools;
import org.kimios.client.exception.AccessDeniedException;
import org.kimios.client.exception.ExceptionHelper;
import org.kimios.client.exception.DMSException;
import org.kimios.kernel.ws.pojo.AuthenticationSource;
import org.kimios.kernel.ws.pojo.DMEntitySecurity;
import org.kimios.kernel.ws.pojo.Group;
import org.kimios.kernel.ws.pojo.User;
import org.kimios.webservices.SecurityService;

import java.util.Vector;

/**
 * SecurityController is used to get information about security and to check
 * rights about DMEntities
 */
public class SecurityController
{

    private SecurityService client;

    public SecurityService getClient()
    {
        return client;
    }

    public void setClient( SecurityService client )
    {
        this.client = client;
    }

    /**
     * Start a session
     */
    public String startSession( String userName, String password, String source )
        throws Exception, DMSException
    {
        try
        {
            return client.startSession( userName, source, password );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }

    }

    /**
     * Is the given session still alive ?
     */
    public boolean isSessionAlive( String sessionId )
        throws Exception, DMSException
    {
        try
        {
            return client.isSessionAlive( sessionId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Get the user linked to a given session
     */
    public User getUser( String sessionId )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            return client.getUser( sessionId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Get users list for a given authentication source
     */
    public User[] getUsers( String sessionId, String source )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            return client.getUsers( sessionId, source );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Get group for a given authentication source and a given gid
     */
    public Group[] getGroups( String sessionId, String source )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            return client.getGroups( sessionId, source );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Get groups list for a given authentication source
     */
    public Group getGroup( String sessionId, String groupId, String source )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            return client.getGroup( sessionId, groupId, source );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Can given user read the given entity ?
     */
    public boolean canRead( String sessionId, long dmEntityId, int dmEntityType )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            return client.canRead( sessionId, dmEntityId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Can the given user write on the given entity ?
     */
    public boolean canWrite( String sessionId, long dmEntityId, int dmEntityType )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            return client.canWrite( sessionId, dmEntityId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Can the given user update rights of the given entity ?
     */
    public boolean hasFullAccess( String sessionId, long dmEntityId, int dmEntityType )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            return client.hasFullAccess( sessionId, dmEntityId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Return registered authentication sources list
     */
    public AuthenticationSource[] getAuthenticationSources()
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            return client.getAuthenticationSources();
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Has given user workspace creation role ?
     */
    public boolean canCreateWorkspace( String sessionId )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            return client.canCreateWorkspace( sessionId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Has given user studio access role ?
     */
    public boolean hasStudioAccess( String sessionId )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            return client.hasStudioAccess( sessionId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Has given user reporting access role ?
     */
    public boolean hasReportingAccess( String sessionId )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            return client.hasReportingAccess( sessionId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Has given user administrator role ?
     */
    public boolean isAdmin( String sessionId )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            return client.isAdmin( sessionId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Get the matrix of rights for a given entity (workspace, folder, or document)
     */
    public DMEntitySecurity[] getDMEntitySecurities( String sessionId, long dmEntityId, int dmEntityType )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            return client.getDMEntitySecurities( sessionId, dmEntityId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Update rights for a given entity from an xml descriptor
     */
    public void updateDMEntitySecurities( String sessionId, long dmEntityId, int dmEntityType, boolean isRecursive,
                                          Vector<DMEntitySecurity> des )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            String xmlStream = "<security-rules dmEntityId=\"" + dmEntityId + "\"" +
                " dmEntityTye=\"" + dmEntityType + "\">\r\n";
            for ( int i = 0; i < des.size(); i++ )
            {
                xmlStream += "\t<rule " +
                    "security-entity-type=\"" + des.elementAt( i ).getType() + "\" " +
                    "security-entity-uid=\"" + StringTools.magicDoubleQuotes( des.elementAt( i ).getName() ) + "\" " +
                    "security-entity-source=\"" + StringTools.magicDoubleQuotes( des.elementAt( i ).getSource() )
                    + "\" " +
                    "read=\"" + des.elementAt( i ).isRead() + "\" " +
                    "write=\"" + des.elementAt( i ).isWrite() + "\" " +
                    "full=\"" + des.elementAt( i ).isFullAccess() + "\" />\r\n";
            }
            xmlStream += "</security-rules>";
            des = null;
            client.updateDMEntitySecurities( sessionId, dmEntityId, xmlStream, isRecursive );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }
}

