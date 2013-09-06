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
package org.kimios.controller;

import flexjson.JSONSerializer;
import flexjson.transformer.IterableTransformer;
import org.kimios.kernel.ws.pojo.Document;
import org.kimios.kernel.ws.pojo.DocumentWorkflowStatusRequest;
import org.kimios.kernel.ws.pojo.WorkflowStatus;
import org.kimios.webservices.pojo.CommentWrapper;
import org.kimios.webservices.pojo.TaskWrapper;

import java.util.*;

/**
 * @author jludmann
 */
public class WorkflowControllerWeb extends Controller {

    public WorkflowControllerWeb(Map<String, String> parameters) {
        super(parameters);
    }

    @Override
    public String execute() throws Exception {
        if (action.equals("getMyTasks")) {
            return getMyTasks();
        }
        if (action.equals("getBonitaPendingTasks")) {
            return getBonitaPendingTasks();
        }
        if (action.equals("getBonitaAssignedTasks")) {
            return getBonitaAssignedTasks();
        }
        if (action.equals("takeTask")) {
            return takeTask();
        }
        if (action.equals("releaseTask")) {
            return releaseTask();
        }
        if (action.equals("hideTask")) {
            return hideTask();
        }
        if (action.equals("addComment")) {
            return addComment();
        }
        if (action.equals("getComments")) {
            return getComments();
        }

        if (action.equals("getWorkflowStatusRequests")) {
            return getWorkflowStatusRequests(parameters);
        }
        if (action.equals("getWorkflowStatus")) {
            return getWorkflowStatus(parameters);
        }
        if (action.equals("acceptWorkflowRequest")) {
            return acceptWorkflowRequest(parameters);
        }
        if (action.equals("rejectWorkflowRequest")) {
            return rejectWorkflowRequest(parameters);
        }
        if (action.equals("startWorkflowRequest")) {
            return startWorkflowRequest(parameters);
        }
//        if (action.equals("startProcessRequest")) {
//            return startProcessRequest(parameters);
//        }
        if (action.equals("getLastDocumentWorkflowStatus")) {
            return getLastDocumentWorkflowStatus(parameters);
        }
        if (action.equals("cancelWorkflow")) {
            cancelWorkflow(parameters);
        }
        return "";
    }

    private void cancelWorkflow(Map<String, String> parameters)
            throws Exception {
        long documentUid = Long.parseLong(parameters.get("documentUid"));
        workflowController
                .cancelWorkflow(sessionUid, documentUid);
    }

    private String getLastDocumentWorkflowStatus(Map<String, String> parameters)
            throws Exception {
        long documentUid = Long.parseLong(parameters.get("documentUid"));
        WorkflowStatus ws = workflowController
                .getLastDocumentWorkflowStatus(sessionUid, documentUid);
        return new JSONSerializer().serialize(ws);

    }

    private String getWorkflowStatusRequests(Map<String, String> parameters)
            throws Exception {
        long documentUid = Long.parseLong(parameters.get("documentUid"));
        List<Map<String, Object>> requests = new ArrayList<Map<String, Object>>();
        for (DocumentWorkflowStatusRequest r : workflowController
                .getDocumentWorkflowStatusRequests(sessionUid, documentUid)) {
            Map<String, Object> request = new HashMap<String, Object>();
            request.put("comment", r.getComment());
            request.put("date", r.getDate().getTime());
            request.put("documentUid", r.getDocumentUid());
            request.put("userName", r.getUserName());
            request.put("userSource", r.getUserSource());
            request.put("validationDate", r.getValidationDate() != null ? r
                    .getValidationDate().getTime() : null);
            request.put("validatorUserName", r.getValidatorUserName());
            request.put("validatorUserSource", r.getValidatorUserSource());
            request.put("workflowStatusUid", r.getWorkflowStatusUid());
            request.put("status", getStatusStr(r.getStatus()));
            requests.add(request);
        }

        return new JSONSerializer().serialize(requests);
    }

    private String getWorkflowStatus(Map<String, String> parameters)
            throws Exception {
        long workflowStatusUid = Long.parseLong(parameters
                .get("workflowStatusUid"));
        WorkflowStatus wst = studioController
                .getWorkflowStatus(sessionUid, workflowStatusUid);
        return "[" + new JSONSerializer().serialize(wst) + "]";
    }

    private String getMyTasks() throws Exception {
        List<Map<String, Object>> myTasksMapList = new ArrayList<Map<String, Object>>();
        DocumentWorkflowStatusRequest[] tasks = workflowController
                .getDocumentWorkflowStatusRequests(sessionUid);
        for (int i = 0; i < tasks.length; i++) {
            Map<String, Object> myTaskMap = new HashMap<String, Object>();
            Document d = documentController.getDocument(sessionUid,
                    tasks[i].getDocumentUid());
            WorkflowStatus wfs = studioController
                    .getWorkflowStatus(sessionUid,
                            tasks[i].getWorkflowStatusUid());
            myTaskMap.put("type", new Integer(3));
            myTaskMap.put("uid", d.getUid());
            myTaskMap.put("name", d.getName());
            myTaskMap.put("length", d.getLength());
            myTaskMap.put("path", d.getPath());
            myTaskMap.put("outOfWorkflow", d.getOutOfWorkflow());
            myTaskMap.put("workflowStatusUid", wfs.getUid());
            myTaskMap.put("workflowStatusName", wfs.getName());
            myTaskMap.put("statusUserName", tasks[i].getUserName());
            myTaskMap.put("statusUserSource", tasks[i].getUserSource());
            myTaskMap.put("statusDate", tasks[i].getDate().getTime().getTime());
            myTaskMap.put("status", getStatusStr(tasks[i].getStatus()));
            myTaskMap.put("owner", d.getOwner());
            myTaskMap.put("ownerSource", d.getOwnerSource());
            myTaskMap.put("creationDate", d.getCreationDate().getTime());
            myTaskMap.put("extension", d.getExtension());
            myTaskMap.put("checkedOut", d.getCheckedOut());
            myTaskMap.put("checkoutDate", d.getCheckoutDate().getTime());
            myTaskMap.put("checkoutUser", d.getCheckoutUser());
            myTaskMap.put("checkoutUserSource", d.getCheckoutUserSource());
            myTaskMap.put("documentTypeUid", d.getDocumentTypeUid());
            myTaskMap.put("documentTypeName", d.getDocumentTypeName());
            myTasksMapList.add(myTaskMap);
        }
        return new JSONSerializer()
                .transform(new IterableTransformer(), Collection.class)
                .exclude("*.class").serialize(myTasksMapList);
    }

    private String getStatusStr(int statusType) {
        switch (statusType) {
            case 1:
                return "RequestStatus1";
            case 2:
                return "RequestStatus2";
            case 3:
                return "RequestStatus3";
            default:
                return "Unknown";
        }
    }

    private String acceptWorkflowRequest(Map<String, String> parameters)
            throws Exception, Exception {
        workflowController
                .acceptWorkflowRequest(sessionUid,
                        Long.parseLong(parameters.get("documentUid")),
                        Long.parseLong(parameters.get("workflowStatusUid")),
                        parameters.get("userName"),
                        parameters.get("userSource"),
                        new Date(Long.parseLong(parameters.get("statusDate"))),
                        parameters.get("comment"));
        return "";
    }

    private String rejectWorkflowRequest(Map<String, String> parameters)
            throws Exception, Exception {
        workflowController
                .rejectWorkflowRequest(sessionUid,
                        Long.parseLong(parameters.get("documentUid")),
                        Long.parseLong(parameters.get("workflowStatusUid")),
                        parameters.get("userName"),
                        parameters.get("userSource"),
                        new Date(Long.parseLong(parameters.get("statusDate"))),
                        parameters.get("comment"));
        return "";
    }

    private String startWorkflowRequest(Map<String, String> parameters)
            throws Exception {
        workflowController
                .createWorkflowRequest(sessionUid,
                        Long.parseLong(parameters.get("documentUid")),
                        Long.parseLong(parameters.get("workflowStatusUid")));
        return "";
    }

    private String getBonitaPendingTasks() throws Exception {

        List<TaskWrapper> tasks = bonitaController.getPendingTasks(sessionUid,
                parameters.get("start") != null ? Integer.parseInt(parameters.get("start")) : Integer.MIN_VALUE,
                parameters.get("limit") != null ? Integer.parseInt(parameters.get("limit")) : Integer.MAX_VALUE);
        return new JSONSerializer().deepSerialize(tasks);
    }

    private String getBonitaAssignedTasks() throws Exception {

        List<TaskWrapper> tasks = bonitaController.getAssignedTasks(sessionUid,
                parameters.get("start") != null ? Integer.parseInt(parameters.get("start")) : Integer.MIN_VALUE,
                parameters.get("limit") != null ? Integer.parseInt(parameters.get("limit")) : Integer.MAX_VALUE);
        return new JSONSerializer().deepSerialize(tasks);
    }

    private String takeTask() throws Exception {
        bonitaController.takeTask(sessionUid, Long.parseLong(parameters.get("taskId")));
        return "";
    }

    private String releaseTask() throws Exception {
        bonitaController.releaseTask(sessionUid, Long.parseLong(parameters.get("taskId")));
        return "";
    }

    private String hideTask() throws Exception {
        bonitaController.hideTask(sessionUid, Long.parseLong(parameters.get("taskId")));
        return "";
    }

    private String addComment() throws Exception {
        return new JSONSerializer().serialize(
                bonitaController.addComment(
                        sessionUid,
                        Long.parseLong(parameters.get("taskId")),
                        parameters.get("comment")));
    }

    private String getComments() throws Exception {
        return new JSONSerializer().serialize(
                bonitaController.getComments(
                        sessionUid,
                        Long.parseLong(parameters.get("taskId")))
        );
    }
}
