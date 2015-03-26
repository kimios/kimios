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
package org.kimios.webservices;

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.kimios.kernel.ws.pojo.*;
import org.kimios.webservices.exceptions.DMServiceException;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA. User: farf Date: 4/1/12 Time: 5:00 PM
 */
@Path("/administration")
@WebService(targetNamespace = "http://kimios.org", serviceName = "AdministrationService")
@CrossOriginResourceSharing(allowAllOrigins = true)
public interface AdministrationService
{
    @GET
    @Path("/getRoles")
    @Produces("application/json")
    public Role[] getRoles(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "role") @WebParam(name = "role") int role) throws DMServiceException;

    @GET
    @Path("/getUserRoles")
    @Produces("application/json")
    public Role[] getUserRoles(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "userName") @WebParam(name = "userName") String userName,
            @QueryParam(value = "userSource") @WebParam(name = "userSource") String userSource)
            throws DMServiceException;

    @GET
    @Path("/createRole")
    @Produces("application/json")
    public void createRole(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "role") @WebParam(name = "role") int role,
            @QueryParam(value = "userName") @WebParam(name = "userName") String userName,
            @QueryParam(value = "userSource") @WebParam(name = "userSource") String userSource)
            throws DMServiceException;

    @GET
    @Path("/deleteRole")
    @Produces("application/json")
    public void deleteRole(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "role") @WebParam(name = "role") int role,
            @QueryParam(value = "userName") @WebParam(name = "userName") String userName,
            @QueryParam(value = "userSource") @WebParam(name = "userSource") String userSource)
            throws DMServiceException;

    @GET
    @Path("/getAuthenticationSource")
    @Produces("application/json")
    public AuthenticationSource getAuthenticationSource(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "name") @WebParam(name = "name") String name) throws DMServiceException;

    @GET
    @Path("/getAuthenticationSourceParams")
    @Produces("application/json")
    public String getAuthenticationSourceParams(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "name") @WebParam(name = "name") String name,
            @QueryParam(value = "className") @WebParam(name = "className") String className) throws DMServiceException;

    @GET
    @Path("/createAuthenticationSource")
    @Produces("application/json")
    public void createAuthenticationSource(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "name") @WebParam(name = "name") String name,
            @QueryParam(value = "className") @WebParam(name = "className") String className,
            @QueryParam(value = "enableSso") @WebParam(name = "enableSso") boolean enableSso,
            @QueryParam(value = "enableMailCheck") @WebParam(name = "enableMailCheck") boolean enableMailCheck,
            @QueryParam(value = "xmlParameters") @WebParam(name = "xmlParameters") String xmlParameters)
            throws DMServiceException;

    @GET
    @Path("/updateAuthenticationSource")
    @Produces("application/json")
    public void updateAuthenticationSource(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "currentName") @WebParam(name = "currentName") String authenticationSourceName,
            @QueryParam(value = "className") @WebParam(name = "className") String className,
            @QueryParam(value = "enableSso") @WebParam(name = "enableSso") boolean enableSso,
            @QueryParam(value = "enableMailCheck") @WebParam(name = "enableMailCheck") boolean enableMailCheck,
            @QueryParam(value = "xmlParameters") @WebParam(name = "xmlParameters") String xmlParameters)
            throws DMServiceException;

    @GET
    @Path("/deleteAuthenticationSource")
    @Produces("application/json")
    public void deleteAuthenticationSource(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "className") @WebParam(name = "className") String name) throws DMServiceException;

    @GET
    @Path("/getAvailableAuthenticationSource")
    @Produces("application/json")
    public String getAvailableAuthenticationSource(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId) throws DMServiceException;

    @GET
    @Path("/getAvailableAuthenticationSourceParams")
    @Produces("application/json")
    public String getAvailableAuthenticationSourceParams(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "className") @WebParam(name = "className") String className) throws DMServiceException;

    @GET
    @Path("/createUser")
    @Produces("application/json")
    public void createUser(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "uid") @WebParam(name = "uid") String uid,
            @QueryParam(value = "firstName") @WebParam(name = "firstName") String firstName,
            @QueryParam(value = "lastName") @WebParam(name = "lastName") String lastName,
            @QueryParam(value = "phoneNumber") @WebParam(name = "phoneNumber") String phoneNumber,
            @QueryParam(value = "mail") @WebParam(name = "mail") String mail,
            @QueryParam(value = "password") @WebParam(name = "password") String password,
            @QueryParam(value = "authenticationSourceName") @WebParam(name = "authenticationSourceName")
            String authenticationSourceName,
            @QueryParam(value = "enabled") @WebParam(name = "enabled") boolean enabled)
            throws DMServiceException;

    @GET
    @Path("/updateUser")
    @Produces("application/json")
    public void updateUser(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "uid") @WebParam(name = "uid") String uid,
            @QueryParam(value = "firstName") @WebParam(name = "firstName") String firstName,
            @QueryParam(value = "lastName") @WebParam(name = "lastName") String lastName,
            @QueryParam(value = "phoneNumber") @WebParam(name = "phoneNumber") String phoneNumber,
            @QueryParam(value = "mail") @WebParam(name = "mail") String mail,
            @QueryParam(value = "password") @WebParam(name = "password") String password,
            @QueryParam(value = "authenticationSourceName") @WebParam(name = "authenticationSourceName")
            String authenticationSourceName,
            @QueryParam(value = "enabled") @WebParam(name = "enabled") boolean enabled)
            throws DMServiceException;

    @GET
    @Path("/deleteUser")
    @Produces("application/json")
    public void deleteUser(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "uid") @WebParam(name = "uid") String uid,
            @QueryParam(value = "authenticationSourceName") @WebParam(name = "authenticationSourceName")
            String authenticationSourceName) throws DMServiceException;

    @GET
    @Path("/createGroup")
    @Produces("application/json")
    public void createGroup(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "gid") @WebParam(name = "gid") String gid,
            @QueryParam(value = "name") @WebParam(name = "name") String name,
            @QueryParam(value = "authenticationSourceName") @WebParam(name = "authenticationSourceName")
            String authenticationSourceName) throws DMServiceException;

    @GET
    @Path("/updateGroup")
    @Produces("application/json")
    public void updateGroup(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "gid") @WebParam(name = "gid") String gid,
            @QueryParam(value = "name") @WebParam(name = "name") String name,
            @QueryParam(value = "authenticationSourceName") @WebParam(name = "authenticationSourceName")
            String authenticationSourceName) throws DMServiceException;

    @GET
    @Path("/deleteGroup")
    @Produces("application/json")
    public void deleteGroup(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "gid") @WebParam(name = "gid") String gid,
            @QueryParam(value = "authenticationSourceName") @WebParam(name = "authenticationSourceName")
            String authenticationSourceName) throws DMServiceException;

    @GET
    @Path("/addUserToGroup")
    @Produces("application/json")
    public void addUserToGroup(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "uid") @WebParam(name = "uid") String uid,
            @QueryParam(value = "gid") @WebParam(name = "gid") String gid,
            @QueryParam(value = "authenticationSourceName") @WebParam(name = "authenticationSourceName")
            String authenticationSourceName) throws DMServiceException;

    @GET
    @Path("/removeUserFromGroup")
    @Produces("application/json")
    public void removeUserFromGroup(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "uid") @WebParam(name = "uid") String uid,
            @QueryParam(value = "gid") @WebParam(name = "gid") String gid,
            @QueryParam(value = "authenticationSourceName") @WebParam(name = "authenticationSourceName")
            String authenticationSourceName) throws DMServiceException;

    @GET
    @Path("/getManageableUser")
    @Produces("application/json")
    public User getManageableUser(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            String uid,
            String authenticationSourceName) throws DMServiceException;

    @GET
    @Path("/getManageableUsers")
    @Produces("application/json")
    public User[] getManageableUsers(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "gid") @WebParam(name = "gid") String gid,
            @QueryParam(value = "authenticationSourceName") @WebParam(name = "authenticationSourceName")
            String authenticationSourceName) throws DMServiceException;

    @GET
    @Path("/getManageableGroup")
    @Produces("application/json")
    public Group getManageableGroup(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "gid") @WebParam(name = "gid") String gid,
            @QueryParam(value = "authenticationSourceName") @WebParam(name = "authenticationSourceName")
            String authenticationSourceName) throws DMServiceException;

    @GET
    @Path("/getManageableGroups")
    @Produces("application/json")
    public Group[] getManageableGroups(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "userId") @WebParam(name = "userId") String userId,
            @QueryParam(value = "authenticationSourceName") @WebParam(name = "authenticationSourceName")
            String authenticationSourceName) throws DMServiceException;

    @GET
    @Path("/reindex")
    @Produces("application/json")
    public void reindex(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "path") @WebParam(name = "path") String path) throws DMServiceException;

    @GET
    @Path("/getReindexProgress")
    @Produces("application/json")
    public int getReindexProgress(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET
    @Path("/getCheckedOutDocuments")
    @Produces("application/json")
    public Document[] getCheckedOutDocuments(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET
    @Path("/clearLock")
    @Produces("application/json")
    public void clearLock(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId) throws DMServiceException;

    @GET
    @Path("/changeOwnership")
    @Produces("application/json")
    public void changeOwnership(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityId,
            @QueryParam(value = "userName") @WebParam(name = "userName") String userName,
            @QueryParam(value = "userSource") @WebParam(name = "userSource") String userSource)
            throws DMServiceException;

    @GET
    @Path("/getConnectedUsers")
    @Produces("application/json")
    public User[] getConnectedUsers(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET
    @Path("/getAllEnabledSessions")
    @Produces("application/json")
    public Session[] getAllEnabledSessions(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET
    @Path("/getEnabledSessions")
    @Produces("application/json")
    public Session[] getEnabledSessions(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "userName") @WebParam(name = "userName") String userName,
            @QueryParam(value = "userSource") @WebParam(name = "userSource") String userSource)
            throws DMServiceException;

    @GET
    @Path("/removeEnabledSession")
    @Produces("application/json")
    public void removeEnabledSession(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "sessionIdToRemove") @WebParam(name = "sessionIdToRemove") String sessionIdToRemove)
            throws DMServiceException;

    @GET
    @Path("/removeEnabledSessions")
    @Produces("application/json")
    public void removeEnabledSessions(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "userName") @WebParam(name = "userName") String userName,
            @QueryParam(value = "userSource") @WebParam(name = "userSource") String userSource)
            throws DMServiceException;

    @GET
    @Path("/setUserAttribute")
    @Produces("application/json")
    public void setUserAttribute(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "userId") @WebParam(name = "userId") String userId,
            @QueryParam(value = "userSource") @WebParam(name = "userSource") String userSource,
            @QueryParam(value = "attributeName") @WebParam(name = "attributeName") String attributeName,
            @QueryParam(value = "attributeValue") @WebParam(name = "attributeValue") String attributeValue)
            throws DMServiceException;

    @GET
    @Path("/getUserAttribute")
    @Produces("application/json")
    public String getUserAttribute(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "userId") @WebParam(name = "userId") String userId,
            @QueryParam(value = "userSource") @WebParam(name = "userSource") String userSource,
            @QueryParam(value = "attributeName") @WebParam(name = "attributeName") String attributeName)
            throws DMServiceException;

    @GET
    @Path("/getUserByAttribute")
    @Produces("application/json")
    public User getUserByAttribute(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "userSource") @WebParam(name = "userSource") String userSource,
            @QueryParam(value = "attributeName") @WebParam(name = "attributeName") String attributeName,
            @QueryParam(value = "attributeValue") @WebParam(name = "attributeValue") String attributeValue)
            throws DMServiceException;


    @GET
    @Path("/disableServiceLogging")
    public void disableServiceLogging()
            throws DMServiceException;

    @GET
    @Path("/enableServiceLogging")
    public void enableServiceLogging()
            throws DMServiceException;

    @GET
    @Path("/listLoggers")
    @Produces("application/json")
    public HashMap<String,String> listLoggers() throws DMServiceException;

    @GET
    @Path("/setLoggerLevel")
    @Produces("application/json")
    public void setLoggerLevel(@QueryParam(value = "loggerName") @WebParam(name = "loggerName") String loggerName,
                                   @QueryParam(value = "loggerLevel") @WebParam(name = "loggerLevel") String loggerLevel)
        throws DMServiceException;


}
