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

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * Created by IntelliJ IDEA. User: farf Date: 4/1/12 Time: 5:06 PM To change this template use File | Settings | File
 * Templates.
 */
@Path("/reporting")
@WebService(targetNamespace = "http://kimios.org", serviceName = "ReportingService")
@CrossOriginResourceSharing(allowAllOrigins = true)
public interface ReportingService
{
    @GET
    @Path("/getReport")
    @Produces("application/json")
    public String getReport(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @WebParam(name = "className") String className,
            @QueryParam(value = "xmlParameters") @WebParam(name = "xmlParameters") String xmlParameters)
            throws DMServiceException;

    @GET
    @Path("/getReportsList")
    @Produces("application/json")
    public String getReportsList(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET
    @Path("/getReportAttributes")
    @Produces("application/json")
    public String getReportAttributes(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @WebParam(name = "className") String className) throws DMServiceException;

    @GET
    @Path("/removeGhostTransaction")
    @Produces("application/json")
    public void removeGhostTransaction(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "transactionId") @WebParam(name = "transactionId") long transactionId)
            throws DMServiceException;
}
