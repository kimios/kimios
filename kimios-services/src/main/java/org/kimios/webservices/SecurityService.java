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
package org.kimios.webservices;

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.kimios.kernel.ws.pojo.AuthenticationSource;
import org.kimios.kernel.ws.pojo.DMEntitySecurity;
import org.kimios.kernel.ws.pojo.Group;
import org.kimios.kernel.ws.pojo.User;
import org.kimios.webservices.exceptions.DMServiceException;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.*;

/**
 *
 *
 */
@Path("/security")
@WebService(targetNamespace = "http://kimios.org", serviceName = "SecurityService")
@CrossOriginResourceSharing(allowAllOrigins = true)
public interface SecurityService
{
    @GET
    @Path("/getDMEntitySecurities")
    @Produces("application/json")
    public DMEntitySecurity[] getDMEntitySecurities(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityId)
            throws DMServiceException;

    @GET
    @Path("/updateDMEntitySecurities")
    @Produces("application/json")
    public void updateDMEntitySecurities(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityId,
            @QueryParam(value = "xmlStream") @WebParam(name = "xmlStream") String xmlStream,
            @QueryParam(value = "isRecursive") @WebParam(name = "isRecursive") boolean isRecursive)
            throws DMServiceException;

    @GET
    @Path("/canRead")
    @Produces("application/json")
    public boolean canRead(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityId)
            throws DMServiceException;

    @GET
    @Path("/canWrite")
    @Produces("application/json")
    public boolean canWrite(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityId)
            throws DMServiceException;

    @GET
    @Path("/hasFullAccess")
    @Produces("application/json")
    public boolean hasFullAccess(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityId)
            throws DMServiceException;

    @GET
    @Path("/getAuthenticationSources")
    @Produces("application/json")
    public AuthenticationSource[] getAuthenticationSources() throws DMServiceException;

    @POST
    @Path("/startSession")
    @Produces("application/json")
    public String startSession(@FormParam(value = "userName") @WebParam(name = "userName") String userName,
            @FormParam(value = "userSource") @WebParam(name = "userSource") String userSource,
            @FormParam(value = "password") @WebParam(name = "password") String password) throws DMServiceException;

    @POST
    @Path("/startSessionWithToken")
    @Produces("application/json")
    public String startSessionWithToken(@FormParam(value = "externalToken") @WebParam(name = "externalToken") String externalToken)
                               throws DMServiceException;

    @GET
    @Path("/isSessionAlive")
    @Produces("application/json")
    public boolean isSessionAlive(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET
    @Path("/getUser")
    @Produces("application/json")
    public User getUser(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET
    @Path("/getUsers")
    @Produces("application/json")
    public User[] getUsers(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "userSource") @WebParam(name = "userSource") String userSource)
            throws DMServiceException;

    @GET
    @Path("/getGroup")
    @Produces("application/json")
    public Group getGroup(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "groupId") @WebParam(name = "groupId") String groupId,
            @QueryParam(value = "userSource") @WebParam(name = "userSource") String userSource)
            throws DMServiceException;

    @GET
    @Path("/getGroups")
    @Produces("application/json")
    public Group[] getGroups(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "userSource") @WebParam(name = "userSource") String userSource)
            throws DMServiceException;

    @GET
    @Path("/canCreateWorkspace")
    @Produces("application/json")
    public boolean canCreateWorkspace(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET
    @Path("/hasStudioAccess")
    @Produces("application/json")
    public boolean hasStudioAccess(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET
    @Path("/hasReportingAccess")
    @Produces("application/json")
    public boolean hasReportingAccess(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET
    @Path("/isAdmin")
    @Produces("application/json")
    public boolean isAdmin(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;
}
