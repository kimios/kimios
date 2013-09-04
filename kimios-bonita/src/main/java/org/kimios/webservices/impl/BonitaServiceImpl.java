package org.kimios.webservices.impl;


import org.kimios.kernel.controller.BonitaController;
import org.kimios.webservices.BonitaService;
import org.kimios.webservices.DMServiceException;
import org.kimios.webservices.ServiceHelper;
import org.kimios.webservices.pojo.ProcessWrapper;
import org.kimios.webservices.pojo.TaskWrapper;

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

    public List<TaskWrapper> getPendingTasks(String sessionId, int min, int max) throws DMServiceException {
        try {
            return controller.getPendingTasks(helper.getSession(sessionId), min, max);
        } catch (Exception e) {
            throw new DMServiceException(e.getMessage(), e);
        }
    }

    public List<TaskWrapper> getAssignedTasks(String sessionId, int min, int max) throws DMServiceException {
        try {
            return controller.getAssignedTasks(helper.getSession(sessionId), min, max);
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
}
