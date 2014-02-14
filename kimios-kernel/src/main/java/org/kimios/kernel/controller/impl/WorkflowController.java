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
package org.kimios.kernel.controller.impl;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.configuration.Config;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.IWorkflowController;
import org.kimios.kernel.dms.*;
import org.kimios.kernel.events.EventContext;
import org.kimios.kernel.events.annotations.DmsEvent;
import org.kimios.kernel.events.annotations.DmsEventName;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.WorkflowException;
import org.kimios.kernel.security.SecurityEntityType;
import org.kimios.kernel.security.Session;
import org.kimios.kernel.user.Group;
import org.kimios.utils.configuration.ConfigurationManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Vector;

@Transactional
public class WorkflowController extends AKimiosController implements IWorkflowController
{
    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IWorkflowController#createWorkflowRequest(org.kimios.kernel.security.Session, long, long)
    */
    @DmsEvent(eventName = { DmsEventName.WORKFLOW_STATUS_REQUEST_CREATE })
    public void createWorkflowRequest(Session session, long documentUid, long workflowStatusUid)
            throws AccessDeniedException,
            WorkflowException, ConfigException, DataSourceException
    {
        Document d = dmsFactoryInstantiator.getDocumentFactory().getDocument(documentUid);
        if (!getSecurityAgent().isWritable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }

        DocumentWorkflowStatusRequest lastRequest =
                dmsFactoryInstantiator.getDocumentWorkflowStatusRequestFactory().getLastPendingRequest(d);
        if (lastRequest != null) {
            throw new WorkflowException("Pending request already exists!");
        }

        DocumentWorkflowStatusRequest request = new DocumentWorkflowStatusRequest();
        request.setDocumentUid(documentUid);
        request.setUserName(session.getUserName());
        request.setUserSource(session.getUserSource());
        request.setDate(new Date());
        request.setStatus(RequestStatus.PENDING);
        request.setWorkflowStatusUid(workflowStatusUid);
        dmsFactoryInstantiator.getDocumentWorkflowStatusRequestFactory().saveRequest(request);

        EventContext.addParameter("workflowRequest", request);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IWorkflowController#cancelWorkflow(org.kimios.kernel.security.Session, long)
    */
    @DmsEvent(eventName = { DmsEventName.WORKFLOW_CANCEL })
    public void cancelWorkflow(Session session, long documentUid)
            throws AccessDeniedException, ConfigException, DataSourceException
    {

        Document d = dmsFactoryInstantiator.getDocumentFactory().getDocument(documentUid);
        if (!getSecurityAgent()
                .canCancelWorkFlow(d, session.getUserName(), session.getUserSource(), session.getGroups()))
        {
            throw new AccessDeniedException();
        }
        Vector<DocumentWorkflowStatus> dwss =
                dmsFactoryInstantiator.getDocumentWorkflowStatusFactory().getDocumentWorkflowStatuses(d.getUid());
        for (DocumentWorkflowStatus dws : dwss) {
            dmsFactoryInstantiator.getDocumentWorkflowStatusFactory().deleteDocumentWorkflowStatus(dws);
        }
        Vector<DocumentWorkflowStatusRequest> dwsrs =
                dmsFactoryInstantiator.getDocumentWorkflowStatusRequestFactory().getRequests(d.getUid());
        for (DocumentWorkflowStatusRequest dwsr : dwsrs) {
            dmsFactoryInstantiator.getDocumentWorkflowStatusRequestFactory().deleteRequest(dwsr);
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IWorkflowController#getLastWorkflowStatus(org.kimios.kernel.security.Session, long)
    */
    public DocumentWorkflowStatus getLastWorkflowStatus(Session session, long documentUid)
            throws AccessDeniedException, ConfigException, DataSourceException
    {
        Document d = dmsFactoryInstantiator.getDocumentFactory().getDocument(documentUid);
        if (!getSecurityAgent().isReadable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }
        return dmsFactoryInstantiator.getDocumentWorkflowStatusFactory().getLastDocumentWorkflowStatus(d.getUid());
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IWorkflowController#getDocumentWorkflowStatusRequest(org.kimios.kernel.security.Session, long, long, java.lang.String, java.lang.String, java.util.Date)
    */
    public DocumentWorkflowStatusRequest getDocumentWorkflowStatusRequest(Session session, long documentUid,
            long workflowStatusUid, String userName, String userSource, Date requestDate)
            throws AccessDeniedException, ConfigException, DataSourceException
    {
        Document d = dmsFactoryInstantiator.getDocumentFactory().getDocument(documentUid);
        if (!getSecurityAgent().isReadable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }
        return dmsFactoryInstantiator.getDocumentWorkflowStatusRequestFactory()
                .getDocumentWorkflowStatusRequest(documentUid, workflowStatusUid,
                        userName, userSource, requestDate);
    }

    /**
     * Create a new status request on a given document uid
     */
    private void createDocumentWorkflowStatus(Session session, long documentUid, long workflowStatusUid)
            throws WorkflowException, AccessDeniedException, ConfigException, DataSourceException
    {
        Document d = dmsFactoryInstantiator.getDocumentFactory().getDocument(documentUid);
        if (!getSecurityAgent().isWritable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }
        WorkflowStatusManagerFactory wsmf = dmsFactoryInstantiator.getWorkflowStatusManagerFactory();
        boolean canAddStatus =
                (wsmf.getWorkflowStatusManager(session.getUserName(), session.getUserSource(), SecurityEntityType.USER,
                        workflowStatusUid) != null);
        if (!canAddStatus) {
            Vector<Group> vGroups = session.getGroups();
            for (Group g : vGroups) {
                if (wsmf.getWorkflowStatusManager(g.getID(), session.getUserSource(), SecurityEntityType.GROUP,
                        workflowStatusUid) != null)
                {
                    canAddStatus = true;
                }
            }
        }
        if (canAddStatus) {
            dmsFactoryInstantiator.getDocumentWorkflowStatusFactory().saveDocumentWorkflowStatus(
                    new DocumentWorkflowStatus(
                            d.getUid(),
                            workflowStatusUid,
                            new Date(),
                            session.getUserName(),
                            session.getUserSource()));
        } else {
            throw new AccessDeniedException();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IWorkflowController#acceptWorkflowRequest(org.kimios.kernel.security.Session, long, java.lang.String, java.lang.String, java.lang.String, java.lang.String, long, java.util.Date, java.lang.String)
    */
    @DmsEvent(eventName = { DmsEventName.WORKFLOW_STATUS_REQUEST_ACCEPT})
    public void acceptWorkflowRequest(Session session, long documentUid, String userName, String userSource,
            String validatorUserName, String validatorUserSource, long workflowStatusUid, Date requestDate,
            String comment)
            throws AccessDeniedException, ConfigException, DataSourceException, WorkflowException
    {
        Document d = dmsFactoryInstantiator.getDocumentFactory().getDocument(documentUid);
        if (!getSecurityAgent().isWritable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }
        DocumentWorkflowStatusRequest newRequest = dmsFactoryInstantiator.getDocumentWorkflowStatusRequestFactory()
                .getDocumentWorkflowStatusRequest(documentUid, workflowStatusUid, userName, userSource, requestDate);
        newRequest.setValidationDate(new Date());
        newRequest.setComment(comment);
        newRequest.setValidatorUserName(validatorUserName);
        newRequest.setValidatorUserSource(validatorUserSource);
        newRequest.setStatus(RequestStatus.ACCEPTED);
        dmsFactoryInstantiator.getDocumentWorkflowStatusRequestFactory().updateRequest(newRequest);
        EventContext.addParameter("acceptedWorkflowRequest", newRequest);
        this.createDocumentWorkflowStatus(session, documentUid, workflowStatusUid);
        /*
        *
        * If automated workflow activated, create the next status request in chain
        *
        */
        WorkflowStatus wfs =
                dmsFactoryInstantiator.getWorkflowStatusFactory().getWorkflowStatus(newRequest.getWorkflowStatusUid())
                        .getSuccessor();
        if (ConfigurationManager.getValue(Config.WORFKLOW_AUTOMATED).equalsIgnoreCase("true") &&
                wfs != null)
        {
            this.createWorkflowRequest(session, documentUid, wfs.getUid());
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IWorkflowController#rejectWorkflowRequest(org.kimios.kernel.security.Session, long, java.lang.String, java.lang.String, java.lang.String, java.lang.String, long, java.util.Date, java.lang.String)
    */
    @DmsEvent(eventName = { DmsEventName.WORKFLOW_STATUS_REQUEST_REJECT })
    public void rejectWorkflowRequest(Session session, long documentUid, String userName, String userSource,
            String validatorUserName, String validatorUserSource, long workflowStatusUid, Date requestDate,
            String comment)
            throws AccessDeniedException, ConfigException, DataSourceException
    {

        Document d = dmsFactoryInstantiator.getDocumentFactory().getDocument(documentUid);
        if (!getSecurityAgent().isWritable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }
        DocumentWorkflowStatusRequest newRequest = dmsFactoryInstantiator.getDocumentWorkflowStatusRequestFactory()
                .getDocumentWorkflowStatusRequest(documentUid, workflowStatusUid, userName, userSource, requestDate);
        newRequest.setValidationDate(new Date());
        newRequest.setComment(comment);
        newRequest.setValidatorUserName(validatorUserName);
        newRequest.setValidatorUserSource(validatorUserSource);
        newRequest.setStatus(RequestStatus.REJECTED);
        dmsFactoryInstantiator.getDocumentWorkflowStatusRequestFactory().updateRequest(newRequest);
        EventContext.addParameter("workflowRequest", newRequest);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IWorkflowController#updateDocumentWorkflowRequestComment(org.kimios.kernel.security.Session, long, java.lang.String, java.lang.String, java.util.Date, long, java.lang.String)
    */
    @DmsEvent(eventName = { DmsEventName.WORKFLOW_STATUS_REQUEST_COMMENT })
    public void updateDocumentWorkflowRequestComment(Session session, long documentUid, String userName,
            String userSource, Date requestDate,
            long workflowStatusUid, String comment) throws AccessDeniedException, ConfigException, DataSourceException
    {

        Document d = dmsFactoryInstantiator.getDocumentFactory().getDocument(documentUid);
        if (!getSecurityAgent().isWritable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }

        DocumentWorkflowStatusRequest toUpdate =
                dmsFactoryInstantiator.getDocumentWorkflowStatusRequestFactory().getDocumentWorkflowStatusRequest(
                        documentUid, workflowStatusUid, userName, userSource, requestDate);

        if (session.getUserName().equalsIgnoreCase(toUpdate.getValidatorUserName()) &&
                session.getUserSource().equalsIgnoreCase(toUpdate.getValidatorUserSource()))
        {
            toUpdate.setComment(comment);
            dmsFactoryInstantiator.getDocumentWorkflowStatusRequestFactory().updateRequest(toUpdate);
        } else {
            throw new AccessDeniedException();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IWorkflowController#getWorkflowRequests(org.kimios.kernel.security.Session, long)
    */
    public Vector<DocumentWorkflowStatusRequest> getWorkflowRequests(Session session, long documentUid)
            throws AccessDeniedException, ConfigException, DataSourceException
    {
        Document d = dmsFactoryInstantiator.getDocumentFactory().getDocument(documentUid);
        if (!getSecurityAgent().isReadable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }
        return dmsFactoryInstantiator.getDocumentWorkflowStatusRequestFactory().getRequests(documentUid);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IWorkflowController#getPendingWorkflowRequests(org.kimios.kernel.security.Session)
    */
    public Vector<DocumentWorkflowStatusRequest> getPendingWorkflowRequests(Session session)
            throws ConfigException, DataSourceException
    {
        Vector<DocumentWorkflowStatusRequest> vDWSR = dmsFactoryInstantiator.getDocumentWorkflowStatusRequestFactory()
                .getPendingRequests(authFactoryInstantiator.getAuthenticationSourceFactory()
                        .getAuthenticationSource(session.getUserSource()).getUserFactory()
                        .getUser(session.getUserName()));
        Vector<Group> vGroups = session.getGroups();
        for (Group g : vGroups) {
            Vector<DocumentWorkflowStatusRequest> vDWSR2 =
                    dmsFactoryInstantiator.getDocumentWorkflowStatusRequestFactory().getPendingRequests(g);
            for (DocumentWorkflowStatusRequest dwsr : vDWSR2) {
                if (!vDWSR.contains(dwsr)) {
                    vDWSR.add(dwsr);
                }
            }
        }
        for (int i = vDWSR.size() - 1; i >= 0; i--) {
            if (!getSecurityAgent().isReadable(
                    dmsFactoryInstantiator.getDocumentFactory().getDocument(vDWSR.elementAt(i).getDocumentUid()),
                    session.getUserName(), session.getUserSource(), session.getGroups()))
            {
                vDWSR.remove(i);
            }
        }
        return vDWSR;
    }
}


