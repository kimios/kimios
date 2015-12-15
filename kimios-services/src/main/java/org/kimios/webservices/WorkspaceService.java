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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.kimios.kernel.ws.pojo.Workspace;
import org.kimios.webservices.exceptions.DMServiceException;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * Created by IntelliJ IDEA. User: farf Date: 4/1/12 Time: 4:57 PM
 */
@Path("/workspace")
@WebService(targetNamespace = "http://kimios.org", serviceName = "WorkspaceService")
@CrossOriginResourceSharing(allowAllOrigins = true)
@Api(value = "/workspace", description = "Workspace Operations")
public interface WorkspaceService
{
    @GET @ApiOperation(value="")
    @Path("/getWorkspace")
    @Produces("application/json")
    public Workspace getWorkspace(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "workspaceId") @WebParam(name = "workspaceId") long workspaceId)
            throws DMServiceException;

    @GET @ApiOperation(value="")
    @Path("/getWorkspaces")
    @Produces("application/json")
    public Workspace[] getWorkspaces(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET @ApiOperation(value="")
    @Path("/createWorkspace")
    @Produces("application/json")
    public long createWorkspace(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "name") @WebParam(name = "name") String name) throws DMServiceException;

    @GET @ApiOperation(value="")
    @Path("/updateWorkspace")
    @Produces("application/json")
    public void updateWorkspace(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "workspaceId") @WebParam(name = "workspaceId") long workspaceId,
            @QueryParam(value = "name") @WebParam(name = "name") String name) throws DMServiceException;

    @GET @ApiOperation(value="")
    @Path("/deleteWorkspace")
    @Produces("application/json")
    public void deleteWorkspace(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "workspaceId") @WebParam(name = "workspaceId") long workspaceId)
            throws DMServiceException;
}
