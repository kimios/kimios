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

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.events.annotations.DmsEvent;
import org.kimios.kernel.events.annotations.DmsEventName;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.XMLException;
import org.kimios.kernel.security.DMEntitySecurity;
import org.kimios.kernel.security.Session;
import org.kimios.kernel.user.AuthenticationSource;
import org.kimios.kernel.user.Group;
import org.kimios.kernel.user.User;

import java.util.List;

public interface ISecurityController
{
    /**
     * Get the matrix of rights for a given entity (workspace, folder, or document)
     */
    public List<DMEntitySecurity> getDMEntitySecurityies(Session session, long dmEntityUid)
            throws ConfigException, DataSourceException;

    /**
     * Update rights for a given entity from an xml descriptor
     */
    @DmsEvent(eventName = { DmsEventName.ENTITY_ACL_UPDATE })
    public void updateDMEntitySecurities(Session session, long dmEntityUid, String xmlStream, boolean isRecursive)
            throws AccessDeniedException, ConfigException, DataSourceException,
            XMLException;


    @DmsEvent(eventName = { DmsEventName.ENTITY_ACL_UPDATE })
    public void updateDMEntitySecurities(Session session, long dmEntityUid, List<DMEntitySecurity> items,
                             boolean isRecursive) throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Can given user read the given entity ?
     */
    public boolean canRead(Session session, long dmEntityUid)
            throws ConfigException, DataSourceException;

    /**
     * Can the given user write on the given entity ?
     */
    public boolean canWrite(Session session, long dmEntityUid)
            throws ConfigException, DataSourceException;

    /**
     * Can the given user update rights of the given entity ?
     */
    public boolean hasFullAccess(Session session, long dmEntityUid)
            throws ConfigException, DataSourceException;

    /**
     * Return registered authentication sources list
     */
    public List<AuthenticationSource> getAuthenticationSources()
            throws ConfigException, DataSourceException;

    /**
     * Start a kernel session
     */
    public Session startSession(String userName, String userSource,
            String password) throws ConfigException, DataSourceException, AccessDeniedException;


    /**
     * End a kernel session
     */
    public void endSession(String sessionId) throws ConfigException, DataSourceException, AccessDeniedException;

    /**
     * Start a kernel session using external Token (by example: SSO System like CAS)
     */
    public Session startSession(String externalToken) throws ConfigException,AccessDeniedException;



    /**
     * Is the given session still alive ?
     */
    public boolean isSessionAlive(String sessionUid) throws ConfigException,
            DataSourceException;

    /**
     * Get the user linked to a given session
     */
    public User getUser(Session session) throws ConfigException,
            DataSourceException;

    /**
     * Get users list for a given authentication source
     */
    public List<User> getUsers(String userSource) throws ConfigException,
            DataSourceException;

    /**
     * Get group for a given authentication source and a given gid
     */
    public Group getGroup(String groupUid, String userSource)
            throws ConfigException, DataSourceException;

    /**
     * Get groups list for a given authentication source
     */
    public List<Group> getGroups(String userSource) throws ConfigException,
            DataSourceException;

    /**
     * Has given user workspace creation role ?
     */
    public boolean canCreateWorkspace(Session session) throws ConfigException,
            DataSourceException;

    /**
     * Has given user studio access role ?
     */
    public boolean hasStudioAccess(Session session) throws ConfigException,
            DataSourceException;

    /**
     * Has given user reporting access role ?
     */
    public boolean hasReportingAccess(Session session) throws ConfigException,
            DataSourceException;

    /**
     * Has given user administrator role ?
     */
    public boolean isAdmin(Session session) throws ConfigException,
            DataSourceException;

    /**
     * Has given user administrator role ?
     */
    public boolean isAdmin(String userName, String userSource) throws ConfigException,
            DataSourceException;

    public User getUser(String userName, String userSource) throws ConfigException,
            DataSourceException;

    public Session impersonnate(Session session, String userName, String userSource)
            throws AccessDeniedException, ConfigException, DataSourceException;
}
