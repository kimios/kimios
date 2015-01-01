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
package org.kimios.webservices.impl;


import org.kimios.kernel.dms.MetaFeedImpl;
import org.kimios.kernel.security.Session;
import org.kimios.kernel.ws.pojo.*;
import org.kimios.webservices.CoreService;
import org.kimios.webservices.exceptions.DMServiceException;
import org.kimios.webservices.StudioService;

import javax.jws.WebService;
import java.util.List;
import java.util.Vector;

@WebService(targetNamespace = "http://kimios.org", serviceName = "StudioService", name = "StudioService")
public class StudioServiceImpl extends CoreService implements StudioService {


    public DocumentType getDocumentType(String sessionId, long uid) throws DMServiceException {

        try {

            getHelper().getSession(sessionId);

            org.kimios.kernel.dms.DocumentType type = studioController.getDocumentType(uid);

            if (type == null)
                return null;
            else
                return type.toPojo();
        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public DocumentType[] getDocumentTypes(String sessionId) throws DMServiceException {

        try {

            getHelper().getSession(sessionId);

            Vector<org.kimios.kernel.dms.DocumentType> v = studioController.getDocumentTypes();
            DocumentType[] r = new DocumentType[v.size()];
            for (int i = 0; i < v.size(); i++)
                r[i] = v.elementAt(i).toPojo();

            return r;
        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public void createDocumentType(String sessionId, String xmlStream) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            studioController.createDocumentType(session, xmlStream);

        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public void updateDocumentType(String sessionId, String xmlStream) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            studioController.updateDocumentType(session, xmlStream);

        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public void deleteDocumentType(String sessionId, long uid) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            studioController.deleteDocumentType(session, uid);

        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public String[] getAvailableMetaFeeds(String sessionId) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            List<String> list = studioController.getAvailableMetaFeedTypes(session);
            String[] array = new String[list.size()];
            for (String item : list) {
                array[list.indexOf(item)] = item;
            }

            return array;
        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public MetaFeed getMetaFeed(String sessionId, long uid) throws DMServiceException {

        try {

            getHelper().getSession(sessionId);

            MetaFeedImpl metaFeed = studioController.getMetaFeed(uid);

            if (metaFeed == null)
                return null;
            else
                return metaFeed.toPojo();
        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public MetaFeed[] getMetaFeeds(String sessionId) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            List<MetaFeedImpl> v = studioController.getMetaFeeds(session);
            MetaFeed[] r = new MetaFeed[v.size()];
            int i = 0;
            for(MetaFeedImpl m: v)
                r[i++] = m.toPojo();

            return r;
        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public String[] searchMetaFeedValues(String sessionId, long metaFeedId, String criteria) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            String[] results = studioController.searchMetaFeedValues(session, metaFeedId, criteria);

            return results;
        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public long createMetaFeed(String sessionId, String name, String className) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            return studioController.createMetaFeed(session, name, className);

        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public void updateMetaFeed(String sessionId, long uid, String name) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            studioController.updateMetaFeed(session, uid, name);

        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public void deleteMetaFeed(String sessionId, long uid) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            studioController.deleteMetaFeed(session, uid);

        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public String[] getMetaFeedValues(String sessionId, long uid) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            List<String> v = studioController.getMetaFeedValues(session, uid);
            String[] r = new String[v.size()];
            for (int i = 0; i < v.size(); i++)
                r[i] = v.get(i);

            return r;
        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public void updateEnumerationValues(String sessionId, String xmlStream) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            studioController.updateEnumerationValues(session, xmlStream);

        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public Workflow[] getWorkflows(String sessionId) throws DMServiceException {

        try {

            getHelper().getSession(sessionId);

            Vector<org.kimios.kernel.dms.Workflow> v = studioController.getWorkflows();
            Workflow[] r = new Workflow[v.size()];
            for (int i = 0; i < v.size(); i++)
                r[i] = v.elementAt(i).toPojo();

            return r;
        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public Workflow getWorkflow(String sessionId, long uid) throws DMServiceException {

        try {

            getHelper().getSession(sessionId);

            Workflow wf = studioController.getWorkflow(uid).toPojo();

            return wf;
        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public WorkflowStatus[] getWorkflowStatuses(String sessionId, long workflowId) throws DMServiceException {

        try {

            getHelper().getSession(sessionId);

            Vector<org.kimios.kernel.dms.WorkflowStatus> v = studioController.getWorkflowStatuses(workflowId);
            WorkflowStatus[] r = new WorkflowStatus[v.size()];
            for (int i = 0; i < v.size(); i++)
                r[i] = v.elementAt(i).toPojo();

            return r;
        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public WorkflowStatus getWorkflowStatus(String sessionId, long workflowStatusId) throws DMServiceException {

        try {

            getHelper().getSession(sessionId);

            WorkflowStatus wfs = studioController.getWorkflowStatus(workflowStatusId).toPojo();

            return wfs;
        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public void createWorkflowStatusManager(String sessionId, long workflowStatusId, String securityEntityName, String securityEntitySource,
                                            int securityEntityType) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            studioController.createWorkflowStatusManager(session, workflowStatusId, securityEntityName, securityEntitySource, securityEntityType);

        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public void deleteWorkflowStatusManager(String sessionId, long workflowStatusId, String securityEntityName, String securityEntitySource,
                                            int securityEntityType) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            studioController.deleteWorkflowStatusManager(session, workflowStatusId, securityEntityName, securityEntitySource, securityEntityType);

        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public WorkflowStatusManager[] getWorkflowStatusManagers(String sessionId, long workflowStatusId) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            Vector<org.kimios.kernel.dms.WorkflowStatusManager> v = studioController.getWorkflowStatusManagers(session, workflowStatusId);
            WorkflowStatusManager[] r = new WorkflowStatusManager[v.size()];
            for (int i = 0; i < v.size(); i++)
                r[i] = v.elementAt(i).toPojo();

            return r;
        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public long createWorkflow(String sessionId, String name, String description, String xmlStream) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            long uid = studioController.createWorkflow(session, name, description, xmlStream);

            return uid;
        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public void updateWorkflow(String sessionId, long workflowId, String name, String description, String xmlStream) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            studioController.updateWorkflow(session, workflowId, name, description, xmlStream);

        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public void deleteWorkflow(String sessionId, long workflowId) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            studioController.deleteWorkflow(session, workflowId);

        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public long createWorkflowStatus(String sessionId, long workflowId, String name, long successorId) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            long uid = studioController.createWorkflowStatus(session, workflowId, name, successorId);

            return uid;
        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public void updateWorkflowStatus(String sessionId, long workflowStatusId, long workflowId, String name, long successorId) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            studioController.updateWorkflowStatus(session, workflowStatusId, workflowId, name, successorId);

        } catch (Exception e) {
            e.printStackTrace();
            throw getHelper().convertException(e);
        }
    }

    public void deleteWorkflowStatus(String sessionId, long workflowStatusId) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);
            studioController.deleteWorkflowStatus(session, workflowStatusId);

        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }
}

