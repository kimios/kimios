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
import org.kimios.kernel.ws.pojo.DocumentWorkflowStatusRequest;
import org.kimios.kernel.ws.pojo.WorkflowStatus;
import org.kimios.webservices.exceptions.DMServiceException;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.Date;

/**
 * Created by IntelliJ IDEA. User: farf Date: 4/1/12 Time: 5:08 PM To change this template use File | Settings | File
 * Templates.
 */
@Path("/notification")
@WebService(targetNamespace = "http://kimios.org", serviceName = "NotificationService")
@CrossOriginResourceSharing(allowAllOrigins = true)
public interface NotificationService
{
    @GET
    @Path("/createRequest")
    @Produces("application/json")
    public void createRequest(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId,
            @QueryParam(value = "workflowStatusId") @WebParam(name = "workflowStatusId") long workflowStatusId)
            throws DMServiceException;

    @GET
    @Path("/getLastWorkflowStatus")
    @Produces("application/json")
    public WorkflowStatus getLastWorkflowStatus(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId) throws DMServiceException;

    @GET
    @Path("/getPendingRequests")
    @Produces("application/json")
    public DocumentWorkflowStatusRequest[] getPendingRequests(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET
    @Path("/getRequests")
    @Produces("application/json")
    public DocumentWorkflowStatusRequest[] getRequests(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId) throws DMServiceException;

    @GET
    @Path("/acceptRequest")
    @Produces("application/json")
    public void acceptRequest(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId,
            @QueryParam(value = "workflowStatusId") @WebParam(name = "workflowStatusId") long workflowStatusId,
            @QueryParam(value = "userName") @WebParam(name = "userName") String userName,
            @QueryParam(value = "userSource") @WebParam(name = "userSource") String userSource,
            @QueryParam(value = "statusDate") @WebParam(name = "statusDate") Date statusDate,
            @QueryParam(value = "comment") @WebParam(name = "comment") String comment) throws DMServiceException;

    @GET
    @Path("/rejectRequest")
    @Produces("application/json")
    public void rejectRequest(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId,
            @QueryParam(value = "workflowStatusId") @WebParam(name = "workflowStatusId") long workflowStatusId,
            @QueryParam(value = "userName") @WebParam(name = "userName") String userName,
            @QueryParam(value = "userSource") @WebParam(name = "userSource") String userSource,
            @QueryParam(value = "statusDate") @WebParam(name = "statusDate") Date statusDate,
            @QueryParam(value = "comment") @WebParam(name = "comment") String comment) throws DMServiceException;

    @GET
    @Path("/cancelWorkflow")
    @Produces("application/json")
    public void cancelWorkflow(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId) throws DMServiceException;

    @GET
    @Path("/updateDocumentWorkflowStatusRequestComment")
    @Produces("application/json")
    public void updateDocumentWorkflowStatusRequestComment(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId,
            @QueryParam(value = "workflowStatusId") @WebParam(name = "workflowStatusId") long workflowStatusId,
            @QueryParam(value = "userName") @WebParam(name = "userName") String userName,
            @QueryParam(value = "userSource") @WebParam(name = "userSource") String userSource,
            @QueryParam(value = "requestDate") @WebParam(name = "requestDate") Date requestDate,
            @QueryParam(value = "newComment") @WebParam(name = "newComment") String newComment)
            throws DMServiceException;

    @GET
    @Path("/getDocumentWorkflowStatusRequest")
    @Produces("application/json")
    public DocumentWorkflowStatusRequest getDocumentWorkflowStatusRequest(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId,
            @QueryParam(value = "workflowStatusId") @WebParam(name = "workflowStatusId") long workflowStatusId,
            @QueryParam(value = "userName") @WebParam(name = "userName") String userName,
            @QueryParam(value = "userSource") @WebParam(name = "userSource") String userSource,
            @QueryParam(value = "requestDate") @WebParam(name = "requestDate") Date requestDate)
            throws DMServiceException;
}
