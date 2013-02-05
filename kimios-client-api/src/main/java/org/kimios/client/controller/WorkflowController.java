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
package org.kimios.client.controller;

import org.kimios.client.exception.AccessDeniedException;
import org.kimios.client.exception.ConfigException;
import org.kimios.client.exception.DMSException;
import org.kimios.client.exception.ExceptionHelper;
import org.kimios.client.exception.WorkflowException;
import org.kimios.kernel.ws.pojo.DocumentWorkflowStatusRequest;
import org.kimios.kernel.ws.pojo.WorkflowStatus;
import org.kimios.webservices.NotificationService;

import java.util.Date;

/**
 * WorkflowController is used to create, update, accept, cancel and to manage
 * workflows over documents,
 */
public class WorkflowController {

    private NotificationService client;

    public NotificationService getClient() {
        return client;
    }

    public void setClient(NotificationService client) {
        this.client = client;
    }

    /**
     * Return the last worfklow status for a given document
     */
    public WorkflowStatus getLastDocumentWorkflowStatus(String sessionId, long documentId) throws Exception,
            DMSException, ConfigException, AccessDeniedException
    {
        try {
            return client.getLastWorkflowStatus(sessionId,
                    documentId);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    /**
     * Create new workflow request for a given document and a given workflow status
     */
    public void createWorkflowRequest(String sessionId, long documentId, long workflowStatusId) throws Exception, DMSException, ConfigException, AccessDeniedException,
            WorkflowException
    {
        try {
          client.createRequest(sessionId, documentId, workflowStatusId);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    /**
     * Validate Status request, and create workflow status
     */
    public void acceptWorkflowRequest(String sessionId, long documentId, long workflowStatusId, String userName, String userSource, Date statusDate, String comment) throws Exception, DMSException, ConfigException, AccessDeniedException {
        try {
            client.acceptRequest(sessionId, documentId, workflowStatusId, userName, userSource, statusDate, comment);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    /**
     * Reject Status request for a given document, status, and user
     */
    public void rejectWorkflowRequest(String sessionId, long documentId, long workflowStatusId, String userName, String userSource, Date statusDate, String comment) throws Exception, DMSException, ConfigException, AccessDeniedException {
        try {
            client.rejectRequest(sessionId, documentId, workflowStatusId, userName, userSource, statusDate, comment);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    /**
     * Update Status Request Comment
     */
    public void updateWorkflowRequestComment(String sessionId, long documentId, long workflowStatusId, String userName, String userSource, Date statusDate, String comment) throws Exception, DMSException, ConfigException, AccessDeniedException {
        try {
            client.updateDocumentWorkflowStatusRequestComment(sessionId, documentId, workflowStatusId, userName, userSource, statusDate, comment);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    /**
     * Get Status Request details for a given document uid
     */
    public DocumentWorkflowStatusRequest[] getDocumentWorkflowStatusRequests(String sessionId, long documentId) throws Exception, DMSException, ConfigException, AccessDeniedException {
        try {
            return client.getRequests(sessionId, documentId);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    /**
     * Get Status Request details
     */
    public DocumentWorkflowStatusRequest[] getDocumentWorkflowStatusRequests(String sessionId) throws Exception, DMSException, ConfigException, AccessDeniedException {
        try {
           return client.getPendingRequests(sessionId);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    /**
     * Get Status Request details for a givent document uid, status uid, and user
     */
    public DocumentWorkflowStatusRequest getDocumentWorkflowStatusRequest(String sessionId, long documentId, long workflowStatusId, String userName, String userSource, Date requestDate) throws Exception, DMSException, ConfigException, AccessDeniedException {
        try {
            return client.getDocumentWorkflowStatusRequest(
                    sessionId,
                    documentId,
                    workflowStatusId,
                    userName,
                    userSource,
                    requestDate
            );
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    /**
     * Cancel workflow for a given document
     */
    public void cancelWorkflow(String sessionId, long documentId) throws Exception, DMSException, ConfigException, AccessDeniedException {
        try {
            client.cancelWorkflow(sessionId, documentId);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }
}

