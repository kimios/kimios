package org.kimios.webservices.impl;


import org.kimios.kernel.controller.BonitaController;
import org.kimios.webservices.BonitaService;
import org.kimios.webservices.DMServiceException;
import org.kimios.webservices.ServiceHelper;
import org.kimios.webservices.pojo.CommentWrapper;
import org.kimios.webservices.pojo.ProcessWrapper;
import org.kimios.webservices.pojo.TasksResponse;

import javax.jws.WebService;
import java.util.List;

@WebService(targetNamespace = "http://kimios.org", serviceName = "BonitaService", name = "BonitaService")
public class BonitaServiceImpl implements BonitaService {

    private BonitaController controller;
    private ServiceHelper helper;

    public BonitaServiceImpl(BonitaController controller, ServiceHelper helper) {
        this.controller = controller;
        this.helper = helper;
    }

    public List<ProcessWrapper> getProcesses(String sessionId) throws DMServiceException {
        try {
            return controller.getProcesses(helper.getSession(sessionId));
        } catch (Exception e) {
            throw new DMServiceException(e.getMessage(), e);
        }
    }

    public TasksResponse getPendingTasks(String sessionId, int start, int limit) throws DMServiceException {
        try {
            return controller.getPendingTasks(helper.getSession(sessionId), start, limit);
        } catch (Exception e) {
            throw new DMServiceException(e.getMessage(), e);
        }
    }

    public TasksResponse getAssignedTasks(String sessionId, int start, int limit) throws DMServiceException {
        try {
            return controller.getAssignedTasks(helper.getSession(sessionId), start, limit);
        } catch (Exception e) {
            throw new DMServiceException(e.getMessage(), e);
        }
    }

    public TasksResponse getTasksByInstance(String sessionId, long processInstanceId, int start, int limit) throws DMServiceException {
        try {
            return controller.getTasksByInstance(helper.getSession(sessionId), processInstanceId, start, limit);
        } catch (Exception e) {
            throw new DMServiceException(e.getMessage(), e);
        }
    }

    public void takeTask(String sessionId, Long taskId) throws DMServiceException {
        try {
            controller.takeTask(helper.getSession(sessionId), taskId);
        } catch (Exception e) {
            throw new DMServiceException(e.getMessage(), e);
        }
    }

    public void releaseTask(String sessionId, Long taskId) throws DMServiceException {
        try {
            controller.releaseTask(helper.getSession(sessionId), taskId);
        } catch (Exception e) {
            throw new DMServiceException(e.getMessage(), e);
        }
    }

    public void hideTask(String sessionId, Long taskId) throws DMServiceException {
        try {
            controller.hideTask(helper.getSession(sessionId), taskId);
        } catch (Exception e) {
            throw new DMServiceException(e.getMessage(), e);
        }
    }

    public CommentWrapper addComment(String sessionId, Long taskId, String comment) throws DMServiceException {
        try {
            return controller.addComment(helper.getSession(sessionId), taskId, comment);
        } catch (Exception e) {
            throw new DMServiceException(e.getMessage(), e);
        }
    }

    public List<CommentWrapper> getComments(String sessionId, Long taskId) throws DMServiceException {
        try {
            return controller.getComments(helper.getSession(sessionId), taskId);
        } catch (Exception e) {
            throw new DMServiceException(e.getMessage(), e);
        }
    }
}
