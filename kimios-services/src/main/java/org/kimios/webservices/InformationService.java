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

import java.util.Date;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * Created by IntelliJ IDEA. User: farf Date: 4/1/12 Time: 5:05 PM
 */
@Path("/information")
@WebService(targetNamespace = "http://kimios.org", serviceName = "InformationService")
@CrossOriginResourceSharing(allowAllOrigins = true)
public interface InformationService
{
    @GET
    @Path("/getServerVersionNumber")
    @Produces("application/json")
    public String getServerVersionNumber() throws DMServiceException;

    @GET
    @Path("/getServerOnlineTime")
    @Produces("application/json")
    public Date getServerOnlineTime(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET
    @Path("/getServerName")
    @Produces("application/json")
    public String getServerName() throws DMServiceException;
}
