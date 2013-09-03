package org.kimios.client.controller;

import org.kimios.client.exception.AccessDeniedException;
import org.kimios.client.exception.ConfigException;
import org.kimios.client.exception.DMSException;
import org.kimios.client.exception.ExceptionHelper;
import org.kimios.webservices.BonitaService;
import org.kimios.webservices.pojo.ProcessInstanceWrapper;
import org.kimios.webservices.pojo.ProcessWrapper;
import org.kimios.webservices.pojo.TaskWrapper;

import java.util.List;

public class BonitaController {

    private BonitaService client;

    public BonitaService getClient() {
        return client;
    }

    public void setClient(BonitaService client) {
        this.client = client;
    }

    public List<ProcessWrapper> getProcesses(String sessionId)
            throws Exception, DMSException, ConfigException, AccessDeniedException {
        try {
            return client.getProcesses(sessionId);

        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    public List<TaskWrapper> getPendingTasks(String sessionId) throws Exception {
        try {
            return client.getPendingTasks(sessionId);

        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

//    public ProcessInstanceWrapper startProcess(String sessionId, Long documentId, Long processId) throws Exception {
//        try {
//            return client.startProcess(sessionId, documentId, processId);
//
//        } catch (Exception e) {
//            throw new ExceptionHelper().convertException(e);
//        }
//    }

}
