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

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.kimios.kernel.ws.pojo.DocumentType;
import org.kimios.kernel.ws.pojo.MetaFeed;
import org.kimios.kernel.ws.pojo.Workflow;
import org.kimios.kernel.ws.pojo.WorkflowStatus;
import org.kimios.kernel.ws.pojo.WorkflowStatusManager;

/**
 * Created by IntelliJ IDEA. User: farf Date: 4/1/12 Time: 5:07 PM To change this template use File | Settings | File
 * Templates.
 */
@Path("/studio")
@WebService(targetNamespace = "http://kimios.org", serviceName = "StudioService")
@CrossOriginResourceSharing(allowAllOrigins = true)
public interface StudioService
{
    @GET
    @Path("/getDocumentType")
    @Produces("application/json")
    public DocumentType getDocumentType(@WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentTypeId") @WebParam(name = "documentTypeId") long documentTypeId)
            throws DMServiceException;

    @GET
    @Path("/getDocumentTypes")
    @Produces("application/json")
    public DocumentType[] getDocumentTypes(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET
    @Path("/createDocumentType")
    @Produces("application/json")
    public void createDocumentType(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "xmlStream") @WebParam(name = "xmlStream") String xmlStream) throws DMServiceException;

    @GET
    @Path("/updateDocumentType")
    @Produces("application/json")
    public void updateDocumentType(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "xmlStream") @WebParam(name = "xmlStream") String xmlStream) throws DMServiceException;

    @GET
    @Path("/deleteDocumentType")
    @Produces("application/json")
    public void deleteDocumentType(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentTypeId") @WebParam(name = "documentTypeId") long documentTypeId)
            throws DMServiceException;

    @GET
    @Path("/getAvailableMetaFeeds")
    @Produces("application/json")
    public String[] getAvailableMetaFeeds(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET
    @Path("/getMetaFeed")
    @Produces("application/json")
    public MetaFeed getMetaFeed(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "metaFeedId") @WebParam(name = "metaFeedId") long metaFeedId) throws DMServiceException;

    @GET
    @Path("/getMetaFeeds")
    @Produces("application/json")
    public MetaFeed[] getMetaFeeds(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET
    @Path("/searchMetaFeedValues")
    @Produces("application/json")
    public String[] searchMetaFeedValues(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "metaFeedId") @WebParam(name = "metaFeedId") long metaFeedId,
            @QueryParam(value = "criteria") @WebParam(name = "criteria") String criteria) throws DMServiceException;

    @GET
    @Path("/createMetaFeed")
    @Produces("application/json")
    public long createMetaFeed(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "name") @WebParam(name = "name") String name,
            @QueryParam(value = "className") @WebParam(name = "className") String className) throws DMServiceException;

    @GET
    @Path("/updateMetaFeed")
    @Produces("application/json")
    public void updateMetaFeed(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "metaFeedId") @WebParam(name = "metaFeedId") long metaFeedId,
            @QueryParam(value = "name") @WebParam(name = "name") String name) throws DMServiceException;

    @GET
    @Path("/deleteMetaFeed")
    @Produces("application/json")
    public void deleteMetaFeed(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "metaFeedId") @WebParam(name = "metaFeedId") long metaFeedId) throws DMServiceException;

    @GET
    @Path("/getMetaFeedValues")
    @Produces("application/json")
    public String[] getMetaFeedValues(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "metaFeedId") @WebParam(name = "metaFeedId") long metaFeedId) throws DMServiceException;

    @GET
    @Path("/updateEnumerationValues")
    @Produces("application/json")
    public void updateEnumerationValues(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "xmlStream") @WebParam(name = "xmlStream") String xmlStream) throws DMServiceException;

    @GET
    @Path("/getWorkflows")
    @Produces("application/json")
    public Workflow[] getWorkflows(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET
    @Path("/getWorkflow")
    @Produces("application/json")
    public Workflow getWorkflow(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "workflowId") @WebParam(name = "workflowId") long workflowId) throws DMServiceException;

    @GET
    @Path("/getWorkflowStatuses")
    @Produces("application/json")
    public WorkflowStatus[] getWorkflowStatuses(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "workflowId") @WebParam(name = "workflowId") long workflowId) throws DMServiceException;

    @GET
    @Path("/getWorkflowStatus")
    @Produces("application/json")
    public WorkflowStatus getWorkflowStatus(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "workflowStatusId") @WebParam(name = "workflowStatusId") long workflowStatusId)
            throws DMServiceException;

    @GET
    @Path("/createWorkflowStatusManager")
    @Produces("application/json")
    public void createWorkflowStatusManager(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "workflowStatusId") @WebParam(name = "workflowStatusId") long workflowStatusId,
            @QueryParam(value = "securityEntityName") @WebParam(name = "securityEntityName") String securityEntityName,
            @QueryParam(value = "securityEntitySource") @WebParam(name = "securityEntitySource")
            String securityEntitySource,
            @QueryParam(value = "securityEntityType") @WebParam(name = "securityEntityType") int securityEntityType)
            throws DMServiceException;

    @GET
    @Path("/deleteWorkflowStatusManager")
    @Produces("application/json")
    public void deleteWorkflowStatusManager(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "workflowStatusId") @WebParam(name = "workflowStatusId") long workflowStatusId,
            @QueryParam(value = "securityEntityName") @WebParam(name = "securityEntityName") String securityEntityName,
            @QueryParam(value = "securityEntitySource") @WebParam(name = "securityEntitySource")
            String securityEntitySource,
            @QueryParam(value = "securityEntityType") @WebParam(name = "securityEntityType") int securityEntityType)
            throws DMServiceException;

    @GET
    @Path("/getWorkflowStatusManagers")
    @Produces("application/json")
    public WorkflowStatusManager[] getWorkflowStatusManagers(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "workflowStatusId") @WebParam(name = "workflowStatusId") long workflowStatusId)
            throws DMServiceException;

    @GET
    @Path("/createWorkflow")
    @Produces("application/json")
    public long createWorkflow(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "name") @WebParam(name = "name") String name,
            @QueryParam(value = "description") @WebParam(name = "description") String description,
            @QueryParam(value = "xmlStream") @WebParam(name = "xmlStream") String xmlStream) throws DMServiceException;

    @GET
    @Path("/updateWorkflow")
    @Produces("application/json")
    public void updateWorkflow(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "workflowId") @WebParam(name = "workflowId") long workflowId,
            @QueryParam(value = "name") @WebParam(name = "name") String name,
            @QueryParam(value = "description") @WebParam(name = "description") String description,
            @QueryParam(value = "xmlStream") @WebParam(name = "xmlStream") String xmlStream) throws DMServiceException;

    @GET
    @Path("/deleteWorkflow")
    @Produces("application/json")
    public void deleteWorkflow(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "workflowId") @WebParam(name = "workflowId") long workflowId) throws DMServiceException;

    @GET
    @Path("/createWorkflowStatus")
    @Produces("application/json")
    public long createWorkflowStatus(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "workflowId") @WebParam(name = "workflowId") long workflowId,
            @QueryParam(value = "name") @WebParam(name = "name") String name,
            @QueryParam(value = "successorId") @WebParam(name = "successorId") long successorId)
            throws DMServiceException;

    @GET
    @Path("/updateWorkflowStatus")
    @Produces("application/json")
    public void updateWorkflowStatus(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "workflowStatusId") @WebParam(name = "workflowStatusId") long workflowStatusId,
            @QueryParam(value = "workflowId") @WebParam(name = "workflowId") long workflowUid,
            @QueryParam(value = "name") @WebParam(name = "name") String name,
            @QueryParam(value = "successorId") @WebParam(name = "successorId") long successorId)
            throws DMServiceException;

    @GET
    @Path("/deleteWorkflowStatus")
    @Produces("application/json")
    public void deleteWorkflowStatus(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "workflowStatusId") @WebParam(name = "workflowStatusId") long workflowStatusId)
            throws DMServiceException;
}
