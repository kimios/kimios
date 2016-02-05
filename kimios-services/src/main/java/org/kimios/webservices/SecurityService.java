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

import io.swagger.annotations.*;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.kimios.kernel.jobs.model.TaskInfo;
import org.kimios.kernel.ws.pojo.*;
import org.kimios.webservices.exceptions.DMServiceException;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 *
 *
 */
@Path("/security")
@WebService(targetNamespace = "http://kimios.org", serviceName = "SecurityService")
@CrossOriginResourceSharing(allowAllOrigins = true)
@Api(value="/security", description = "Security Operations")
public interface SecurityService
{
    @GET @ApiOperation(value ="")
    @Path("/getDMEntitySecurities")
    @Produces("application/json")
    public DMEntitySecurity[] getDMEntitySecurities(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityId)
            throws DMServiceException;

    @POST @ApiOperation(value ="")
    @Path("/updateDMEntitySecurities")
    @Consumes("application/json")
    @Produces("application/json")
    public TaskInfo updateDMEntitySecurities(UpdateSecurityWithXmlCommand securityCommand)
            throws DMServiceException;


    @POST @ApiOperation(value ="")
    @Path("/update-security")
    @Produces("application/json")
    @Consumes("application/json")
    @WebMethod(operationName = "updateSecurities")
    public TaskInfo updateDMEntitySecurities(@WebParam(name = "updateSecurityCommand") UpdateSecurityCommand securityCommand)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getDefaultDMEntitySecurities")
    @Produces("application/json")
    public DMEntitySecurity[] getDefaultDMEntitySecurities(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "objectType") @WebParam(name = "objectType") String objectType)
            throws DMServiceException;

    @POST @ApiOperation(value ="")
    @Path("/updateDefaultDMEntitySecurities")
    @Produces("application/json")
    public void updateDefaultDMEntitySecurities(
            @FormParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @FormParam(value = "xmlStream") @WebParam(name = "xmlStream") String xmlStream,
            @FormParam(value = "objectType") @WebParam(name = "objectType") String objectType)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/canRead")
    @Produces("application/json")
    public boolean canRead(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityId)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/canWrite")
    @Produces("application/json")
    public boolean canWrite(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityId)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/hasFullAccess")
    @Produces("application/json")
    public boolean hasFullAccess(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityId)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getAuthenticationSources")
    @Produces("application/json")
    public AuthenticationSource[] getAuthenticationSources() throws DMServiceException;

    @POST
    @Path("/startSession")
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @ApiOperation(value = "Start Kimios Session",
            httpMethod = "POST",
            notes = "Start Kimios Session",
            response = String.class,
            consumes = "application/x-www-form-urlencoded")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "", response = String.class), @ApiResponse(code = 500, response = Exception.class, message = "Invalid Session") })
    public String startSession(
            @ApiParam(required = true, name = "userName")
            @FormParam(value = "userName") @WebParam(name = "userName") String userName,
            @ApiParam(required = true, name = "userSource")
            @FormParam(value = "userSource") @WebParam(name = "userSource") String userSource,
            @ApiParam(required = true, name = "password")
            @FormParam(value = "password") @WebParam(name = "password") String password) throws DMServiceException;

    @POST
    @Path("/endSession")
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @ApiOperation(value = "End Kimios Session",
            httpMethod = "POST",
            notes = "Start Kimios Session",
            response = String.class,
            consumes = "application/x-www-form-urlencoded")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "") })
    public void endSession(@ApiParam(required = true, name = "sessionId") @FormParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @POST
    @Path("/startSessionWithToken")
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @ApiOperation(value = "Start Kimios Session from an external Token",
            httpMethod = "POST",
            notes = "Start Kimios Session from an external Token",
            response = String.class,
            consumes = "application/x-www-form-urlencoded")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "") })
    public String startSessionWithToken(@ApiParam(required = true, name = "externalToken")
                                         @FormParam(value = "externalToken") @WebParam(name = "externalToken") String externalToken)
                               throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/isSessionAlive")
    @Produces("application/json")
    public boolean isSessionAlive(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getUser")
    @Produces("application/json")
    public User getUser(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getUsers")
    @Produces("application/json")
    public User[] getUsers(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "userSource") @WebParam(name = "userSource") String userSource)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getGroup")
    @Produces("application/json")
    public Group getGroup(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "groupId") @WebParam(name = "groupId") String groupId,
            @QueryParam(value = "userSource") @WebParam(name = "userSource") String userSource)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getGroups")
    @Produces("application/json")
    public Group[] getGroups(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "userSource") @WebParam(name = "userSource") String userSource)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/canCreateWorkspace")
    @Produces("application/json")
    public boolean canCreateWorkspace(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/hasStudioAccess")
    @Produces("application/json")
    public boolean hasStudioAccess(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/hasReportingAccess")
    @Produces("application/json")
    public boolean hasReportingAccess(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/isAdmin")
    @Produces("application/json")
    public boolean isAdmin(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;
}
