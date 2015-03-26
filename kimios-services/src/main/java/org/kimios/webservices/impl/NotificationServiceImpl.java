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
package org.kimios.webservices.impl;

import org.kimios.kernel.dms.DocumentWorkflowStatus;
import org.kimios.kernel.dms.DocumentWorkflowStatusRequest;
import org.kimios.kernel.security.Session;
import org.kimios.kernel.ws.pojo.WorkflowStatus;
import org.kimios.webservices.CoreService;
import org.kimios.webservices.exceptions.DMServiceException;
import org.kimios.webservices.NotificationService;

import javax.jws.WebService;
import java.util.Date;
import java.util.Vector;

@WebService(targetNamespace = "http://kimios.org", serviceName = "NotificationService", name = "NotificationService")
public class NotificationServiceImpl extends CoreService implements NotificationService
{
    /**
     * @param sessionId
     * @param documentId
     * @param workflowStatusId
     * @throws DMServiceException
     */
    public void createRequest(String sessionId, long documentId, long workflowStatusId) throws DMServiceException
    {
        try {
            Session session = getHelper().getSession(sessionId);
            workflowController.createWorkflowRequest(session, documentId, workflowStatusId);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param documentId
     * @return
     * @throws DMServiceException
     */
    public WorkflowStatus getLastWorkflowStatus(String sessionId, long documentId) throws DMServiceException
    {
        try {
            Session session = getHelper().getSession(sessionId);
            DocumentWorkflowStatus item = workflowController.getLastWorkflowStatus(session, documentId);
            WorkflowStatus wst = (item != null ? item.toPojo() : null);
            return wst;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @return
     * @throws DMServiceException
     */
    public org.kimios.kernel.ws.pojo.DocumentWorkflowStatusRequest[] getPendingRequests(String sessionId)
            throws DMServiceException
    {
        try {
            Session session = getHelper().getSession(sessionId);
            Vector<DocumentWorkflowStatusRequest> dwsr = workflowController.getPendingWorkflowRequests(session);
            int i = 0;
            org.kimios.kernel.ws.pojo.DocumentWorkflowStatusRequest[] pojos =
                    new org.kimios.kernel.ws.pojo.DocumentWorkflowStatusRequest[dwsr.size()];
            for (DocumentWorkflowStatusRequest dr : dwsr) {
                pojos[i] = dr.toPojo();
                i++;
            }
            return pojos;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param documentId
     * @return
     * @throws DMServiceException
     */
    public org.kimios.kernel.ws.pojo.DocumentWorkflowStatusRequest[] getRequests(String sessionId, long documentId)
            throws DMServiceException
    {
        try {
            Session session = getHelper().getSession(sessionId);
            Vector<DocumentWorkflowStatusRequest> dwsr = workflowController.getWorkflowRequests(session, documentId);
            int i = 0;
            org.kimios.kernel.ws.pojo.DocumentWorkflowStatusRequest[] pojos =
                    new org.kimios.kernel.ws.pojo.DocumentWorkflowStatusRequest[dwsr.size()];
            for (DocumentWorkflowStatusRequest dr : dwsr) {
                pojos[i] = dr.toPojo();
                i++;
            }
            return pojos;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param documentId
     * @param workflowStatusId
     * @param userName
     * @param userSource
     * @param statusDate
     * @param comment
     * @throws DMServiceException
     */
    public void acceptRequest(String sessionId, long documentId, long workflowStatusId, String userName,
            String userSource, Date statusDate, String comment) throws DMServiceException
    {
        try {
            Session session = getHelper().getSession(sessionId);
            workflowController.acceptWorkflowRequest(session, documentId, userName, userSource, session.getUserName(),
                    session.getUserSource(), workflowStatusId, statusDate, comment);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param documentId
     * @param workflowStatusId
     * @param userName
     * @param userSource
     * @param statusDate
     * @param comment
     * @throws DMServiceException
     */
    public void rejectRequest(String sessionId, long documentId, long workflowStatusId, String userName,
            String userSource, Date statusDate, String comment) throws DMServiceException
    {
        try {
            Session session = getHelper().getSession(sessionId);
            workflowController.rejectWorkflowRequest(session, documentId, userName, userSource, session.getUserName(),
                    session.getUserSource(), workflowStatusId, statusDate, comment);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param documentId
     * @throws DMServiceException
     */
    public void cancelWorkflow(String sessionId, long documentId) throws DMServiceException
    {
        try {
            Session session = getHelper().getSession(sessionId);
            workflowController.cancelWorkflow(session, documentId);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param documentId
     * @param workflowStatusId
     * @param userName
     * @param userSource
     * @param requestDate
     * @param newComment
     * @throws DMServiceException
     */
    public void updateDocumentWorkflowStatusRequestComment(String sessionId, long documentId, long workflowStatusId,
            String userName, String userSource, Date requestDate, String newComment) throws DMServiceException
    {
        try {
            Session session = getHelper().getSession(sessionId);
            workflowController
                    .updateDocumentWorkflowRequestComment(session, documentId, userName, userSource, requestDate,
                            workflowStatusId, newComment);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param documentId
     * @param workflowStatusId
     * @param userName
     * @param userSource
     * @param requestDate
     * @return
     * @throws DMServiceException
     */
    public org.kimios.kernel.ws.pojo.DocumentWorkflowStatusRequest getDocumentWorkflowStatusRequest(String sessionId,
            long documentId, long workflowStatusId,
            String userName, String userSource, Date requestDate) throws DMServiceException
    {
        try {
            Session session = getHelper().getSession(sessionId);
            org.kimios.kernel.ws.pojo.DocumentWorkflowStatusRequest dwsr = workflowController
                    .getDocumentWorkflowStatusRequest(session, documentId, workflowStatusId, userName, userSource,
                            requestDate).toPojo();
            return dwsr;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }
}

