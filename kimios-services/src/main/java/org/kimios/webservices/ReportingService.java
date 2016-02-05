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
import org.kimios.kernel.reporting.model.Report;
import org.kimios.kernel.reporting.model.ReportParam;
import org.kimios.webservices.exceptions.DMServiceException;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: farf Date: 4/1/12 Time: 5:06 PM To change this template use File | Settings | File
 * Templates.
 */
@Path("/reporting")
@WebService(targetNamespace = "http://kimios.org", serviceName = "ReportingService")
@CrossOriginResourceSharing(allowAllOrigins = true)
@Api(value = "/reporting", description = "Reporting Service")
public interface ReportingService
{
    @GET @ApiOperation(value ="")
    @Path("/getReport")
    @WebMethod(operationName = "getReportXml")
    @Produces("application/json")
    public String getReport(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "className") @WebParam(name = "className") String className,
            @QueryParam(value = "xmlParameters") @WebParam(name = "xmlParameters") String xmlParameters)
            throws DMServiceException;

    @POST @ApiOperation(value ="")
    @Path("/report/{className}")
    @Produces("application/json")
    @Consumes("application/json")
    public String getReport(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                            @PathParam(value = "className") @WebParam(name = "className") String className,
                            @WebParam(name = "parameters") List<ReportParam> parameters)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getReportsList")
    @Produces("application/json")
    public String getReportsListXml(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/reports")
    @Produces("application/json")
    public List<Report> getReportsList(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getReportAttributes")
    @Produces("application/json")
    public String getReportAttributesXml(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                                      @QueryParam(value = "className") @WebParam(name = "className") String className) throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/report/{className}/attributes")
    @Produces("application/json")
    public List<ReportParam> getReportAttributes(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                                                 @PathParam(value = "className") @WebParam(name = "className") String className) throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/removeGhostTransaction")
    @Produces("application/json")
    public void removeGhostTransaction(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "transactionId") @WebParam(name = "transactionId") long transactionId)
            throws DMServiceException;
}
