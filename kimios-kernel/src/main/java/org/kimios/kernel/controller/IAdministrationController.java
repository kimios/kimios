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
import org.kimios.kernel.dms.model.Document;
import org.kimios.api.events.annotations.DmsEvent;
import org.kimios.api.events.annotations.DmsEventName;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.security.model.Role;
import org.kimios.kernel.security.model.SecurityEntity;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.user.model.AuthenticationSource;
import org.kimios.kernel.user.model.Group;
import org.kimios.kernel.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.Vector;

public interface IAdministrationController
{
    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IAdministrationController#getRoles(org.kimios.kernel.security.Session, int)
    */
    public Vector<Role> getRoles(Session session, int role)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Return users from a role
     */
    public Vector<Role> getRoles(Session session, String userName, String userSource)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IAdministrationController#createRole(org.kimios.kernel.security.Session, int, java.lang.String, java.lang.String)
    */
    public void createRole(Session session, int role, String userName,
            String userSource) throws AccessDeniedException, ConfigException,
            DataSourceException;

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IAdministrationController#deleteRole(org.kimios.kernel.security.Session, int, java.lang.String, java.lang.String)
    */
    public void deleteRole(Session session, int role, String userName,
            String userSource) throws AccessDeniedException, ConfigException,
            DataSourceException;

    /**
     * Return the authentication source specified by name
     */
    public AuthenticationSource getAuthenticationSource(Session session,
            String name) throws AccessDeniedException, ConfigException,
            DataSourceException;


    /**
     * Return connection parameters XML stream for the authentication sources specified by name
     */
    @Deprecated
    public String getAuthenticationSourceParamsXml(Session session, String name,
                                                             String className) throws ConfigException, DataSourceException,
            AccessDeniedException;

    /**
     * Return connection parameters for the authentication sources specified by name
     */
    public Map<String, String> getAuthenticationSourceParams(Session session, String name,
                                             String className) throws ConfigException, DataSourceException,
            AccessDeniedException;

    /**
     * Create new authentication source
     */
    public void createAuthenticationSource(Session session, String name,
            String className, boolean enableSso, boolean enableMailCheck, String xmlParameters)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Create new authentication source
     */
    public void createAuthenticationSource(Session session, String name,
                                           String className, boolean enableSso, boolean enableMailCheck, Map<String, String> parameters)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Update authentication source (xml parameters)
     */
    @Deprecated
    public void updateAuthenticationSource(Session session, String name, String className,
                                           boolean enableSso, boolean enableMailCheck, String xmlParameters)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Update authentication source
     */
    public void updateAuthenticationSource(Session session, String name, String className,
                                           boolean enableSso, boolean enableMailCheck, Map<String, String> params)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Remove a DMS Authentication Source
     */
    public void deleteAuthenticationSource(Session session, String name)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Get a class names list of all implemented authentication sources
     */
    public String getAvailableAuthenticationSourceXml(Session session)
            throws ConfigException, DataSourceException, AccessDeniedException;


    public List<String> getAvailableAuthenticationSource(Session session) throws ConfigException, DataSourceException,
            AccessDeniedException;

    public String getAvailableAuthenticationSourceParamsXml(Session session,
            String className) throws ConfigException, DataSourceException,
            AccessDeniedException, ClassNotFoundException;

    public List<String> getAvailableAuthenticationSourceParams(Session session,
                                                         String className) throws ConfigException, DataSourceException,
            AccessDeniedException, ClassNotFoundException;

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IAdministrationController#updateAuthenticationSource(org.kimios.kernel.security.Session, java.lang.String, java.lang.String)
    */
    public void updateAuthenticationSource(Session session, String name, String newName)
            throws AccessDeniedException, ConfigException,
            DataSourceException;

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IAdministrationController#createUser(org.kimios.kernel.security.Session, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
    */
    @DmsEvent(eventName = { DmsEventName.USER_CREATE })
    public void createUser(Session session, String uid, String firstName, String lastName,
            String phoneNumber, String mail, String password, String authenticationSourceName, boolean enabled)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IAdministrationController#updateUser(org.kimios.kernel.security.Session, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
    */

    public void updateUser(Session session, String uid, String firstName, String lastName,
            String phoneNumber, String mail, String password, String authenticationSourceName, boolean enabled)
            throws AccessDeniedException, ConfigException, DataSourceException;


    public void updateUserEmails(Session session, String uid, String authenticationSource, List<String> emails)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IAdministrationController#deleteUser(org.kimios.kernel.security.Session, java.lang.String, java.lang.String)
    */
    @DmsEvent(eventName = { DmsEventName.USER_DELETE })
    public void deleteUser(Session session, String uid,
            String authenticationSourceName) throws AccessDeniedException,
            ConfigException, DataSourceException;

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IAdministrationController#createGroup(org.kimios.kernel.security.Session, java.lang.String, java.lang.String, java.lang.String)
    */
    @DmsEvent(eventName = { DmsEventName.GROUP_CREATE })
    public void createGroup(Session session, String gid, String name,
            String authenticationSourceName) throws AccessDeniedException,
            ConfigException, DataSourceException;

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IAdministrationController#updateGroup(org.kimios.kernel.security.Session, java.lang.String, java.lang.String, java.lang.String)
    */
    public void updateGroup(Session session, String gid, String name,
            String authenticationSourceName) throws AccessDeniedException,
            ConfigException, DataSourceException;

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IAdministrationController#deleteGroup(org.kimios.kernel.security.Session, java.lang.String, java.lang.String)
    */
    @DmsEvent(eventName = { DmsEventName.GROUP_DELETE })
    public void deleteGroup(Session session, String gid,
            String authenticationSourceName) throws AccessDeniedException,
            ConfigException, DataSourceException;

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IAdministrationController#addUserToGroup(org.kimios.kernel.security.Session, java.lang.String, java.lang.String, java.lang.String)
    */
    @DmsEvent(eventName = { DmsEventName.USER_GROUP_ADD })
    public void addUserToGroup(Session session, String uid, String gid,
            String authenticationSourceName) throws AccessDeniedException,
            ConfigException, DataSourceException;

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IAdministrationController#removeUserFromGroup(org.kimios.kernel.security.Session, java.lang.String, java.lang.String, java.lang.String)
    */
    public void removeUserFromGroup(Session session, String uid,
            String gid, String authenticationSourceName)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IAdministrationController#getUser(org.kimios.kernel.security.Session, java.lang.String, java.lang.String)
    */
    public User getUser(Session session, String uid,
            String authenticationSourceName) throws AccessDeniedException,
            ConfigException, DataSourceException;

    /**
     * Get users from a gid and authentication source
     */
    public Vector<User> getUsers(Session session, String gid,
            String authenticationSourceName) throws ConfigException,
            DataSourceException, AccessDeniedException;

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IAdministrationController#getGroup(org.kimios.kernel.security.Session, java.lang.String, java.lang.String)
    */
    public Group getGroup(Session session, String gid,
            String authenticationSourceName) throws AccessDeniedException,
            ConfigException, DataSourceException;

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IAdministrationController#getGroups(org.kimios.kernel.security.Session, java.lang.String, java.lang.String)
    */
    public Vector<Group> getGroups(Session session, String userUid,
            String authenticationSourceName) throws AccessDeniedException,
            ConfigException, DataSourceException;

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IAdministrationController#getCheckedOutDocuments(org.kimios.kernel.security.Session)
    */
    public List<Document> getCheckedOutDocuments(Session session)
            throws ConfigException, DataSourceException, AccessDeniedException;

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IAdministrationController#clearLock(org.kimios.kernel.security.Session, long)
    */
    public void clearLock(Session session, long documentUid)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IAdministrationController#changeOwnership(org.kimios.kernel.security.Session, long, int, java.lang.String, java.lang.String)
    */
    public void changeOwnership(Session session, long dmEntityUid, String userName, String userSource)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IAdministrationController#getConnectedUsers(org.kimios.kernel.security.Session)
    */
    public org.kimios.kernel.ws.pojo.User[] getConnectedUsers(
            Session session) throws ConfigException, DataSourceException,
            AccessDeniedException;

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IAdministrationController#getEnabledSessions(org.kimios.kernel.security.Session)
    */
    public org.kimios.kernel.ws.pojo.Session[] getEnabledSessions(
            Session session) throws DataSourceException, ConfigException,
            AccessDeniedException;

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IAdministrationController#getEnabledSessions(org.kimios.kernel.security.Session, java.lang.String, java.lang.String)
    */
    public org.kimios.kernel.ws.pojo.Session[] getEnabledSessions(
            Session session, String userName, String userSource)
            throws DataSourceException, ConfigException, AccessDeniedException;

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IAdministrationController#removeEnabledSession(org.kimios.kernel.security.Session, java.lang.String)
    */
    public void removeEnabledSession(Session session, String sessionUidToRemove)
            throws DataSourceException, ConfigException, AccessDeniedException;

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IAdministrationController#removeEnabledSessions(org.kimios.kernel.security.Session, java.lang.String, java.lang.String)
    */
    public void removeEnabledSessions(Session session, String userName,
            String userSource) throws ConfigException, DataSourceException,
            AccessDeniedException;

    @DmsEvent(eventName = { DmsEventName.USER_ATTRIBUTE_SET })
    public void setUserAttribute(Session session, String userId, String userSource, String attributeName,
            String attributeValue)
            throws ConfigException, DataSourceException,
            AccessDeniedException;

    public String getUserAttribute(Session session, String userId, String userSource, String attributeName)
            throws ConfigException, DataSourceException,
            AccessDeniedException;

    public User getUserByAttributeValue(Session session, String userSource, String attributeName,
            String attributeValue)
            throws ConfigException, DataSourceException,
            AccessDeniedException;

    public void deleteUserPermissions(Session session, String userId, String authenticationSourceName) throws AccessDeniedException,
            ConfigException, DataSourceException;

    public List<SecurityEntity> searchSecurityEntities(String searchText, String sourceName, int securityEntityType) throws AccessDeniedException, ConfigException, DataSourceException;
}
