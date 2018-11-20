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
import org.kimios.webservices.exceptions.DMServiceException;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.Date;

/**
 * Created by IntelliJ IDEA. User: farf Date: 4/1/12 Time: 5:05 PM
 */
@Path("/information")
@WebService(targetNamespace = "http://kimios.org", serviceName = "InformationService")
@CrossOriginResourceSharing(allowAllOrigins = true)
@Api(value="/information", description = "Information Operations")
public interface InformationService
{
    @GET @ApiOperation(value="")
    @Path("/getServerVersionNumber")
    @Produces("application/json")
    public String getServerVersionNumber() throws DMServiceException;

    @GET @ApiOperation(value="")
    @Path("/getServerOnlineTime")
    @Produces("application/json")
    public Date getServerOnlineTime(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET @ApiOperation(value="")
    @Path("/getServerName")
    @Produces("application/json")
    public String getServerName() throws DMServiceException;


    @GET @ApiOperation(value="")
    @Path("/getTelemetryUUID")
    @Produces("application/json")
    public String getTelemetryUUID() throws DMServiceException;
}
