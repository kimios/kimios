/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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
package org.kimios.kernel.controller;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.DocumentWorkflowStatus;
import org.kimios.kernel.dms.DocumentWorkflowStatusRequest;
import org.kimios.kernel.events.annotations.DmsEvent;
import org.kimios.kernel.events.annotations.DmsEventName;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.WorkflowException;
import org.kimios.kernel.security.Session;

import java.util.Date;
import java.util.Vector;

public interface IWorkflowController
{
    /**
     * Create new workflow request for a given document and a given workflow status
     */
    @DmsEvent(eventName = { DmsEventName.WORKFLOW_STATUS_REQUEST_CREATE })
    public void createWorkflowRequest(Session session, long documentUid,
            long workflowStatusUid) throws AccessDeniedException,
            WorkflowException, ConfigException, DataSourceException;

    /**
     * Cancel workflow for a given document
     */
    @DmsEvent(eventName = { DmsEventName.WORKFLOW_CANCEL })
    public void cancelWorkflow(Session session, long documentUid)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Return the last worfklow status for a given document (method duplicated in DocumentController)
     */
    public DocumentWorkflowStatus getLastWorkflowStatus(Session session,
            long documentUid) throws AccessDeniedException, ConfigException,
            DataSourceException;

    /**
     * Get Status Request details for a givent document uid, status uid, and user
     */
    public DocumentWorkflowStatusRequest getDocumentWorkflowStatusRequest(
            Session session, long documentUid, long workflowStatusUid,
            String userName, String userSource, Date requestDate)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Validate Status request, and create workflow status
     */
    @DmsEvent(eventName = { DmsEventName.WORKFLOW_STATUS_REQUEST_ACCEPT })
    public void acceptWorkflowRequest(Session session, long documentUid,
            String userName, String userSource, String validatorUserName,
            String validatorUserSource, long workflowStatusUid,
            Date requestDate, String comment) throws AccessDeniedException,
            ConfigException, DataSourceException, WorkflowException;

    /**
     * Reject Status request for a given document, status, and user
     */
    @DmsEvent(eventName = { DmsEventName.WORKFLOW_STATUS_REQUEST_REJECT })
    public void rejectWorkflowRequest(Session session, long documentUid,
            String userName, String userSource, String validatorUserName,
            String validatorUserSource, long workflowStatusUid,
            Date requestDate, String comment) throws AccessDeniedException,
            ConfigException, DataSourceException;

    /**
     * Update Status Request Comment
     */
    @DmsEvent(eventName = { DmsEventName.WORKFLOW_STATUS_REQUEST_COMMENT })
    public void updateDocumentWorkflowRequestComment(Session session,
            long documentUid, String userName, String userSource,
            Date requestDate, long workflowStatusUid, String comment)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Return workflow status requests list for a given document
     */
    public Vector<DocumentWorkflowStatusRequest> getWorkflowRequests(
            Session session, long documentUid) throws AccessDeniedException,
            ConfigException, DataSourceException;

    /**
     * Return Available Pending Status Requests for a given user
     */
    public Vector<DocumentWorkflowStatusRequest> getPendingWorkflowRequests(
            Session session) throws ConfigException, DataSourceException;
}
