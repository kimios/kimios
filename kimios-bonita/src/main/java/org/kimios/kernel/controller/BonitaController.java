package org.kimios.kernel.controller;

import org.bonitasoft.engine.bpm.flownode.ActivityInstanceNotFoundException;
import org.bonitasoft.engine.bpm.process.ProcessDefinitionNotFoundException;
import org.bonitasoft.engine.exception.BonitaHomeNotSetException;
import org.bonitasoft.engine.exception.ServerAPIException;
import org.bonitasoft.engine.exception.UnknownAPITypeException;
import org.bonitasoft.engine.exception.UpdateException;
import org.bonitasoft.engine.platform.LoginException;
import org.bonitasoft.engine.platform.LogoutException;
import org.bonitasoft.engine.session.SessionNotFoundException;
import org.kimios.kernel.security.Session;
import org.kimios.webservices.pojo.ProcessWrapper;
import org.kimios.webservices.pojo.TaskWrapper;

import java.io.IOException;
import java.util.List;

public interface BonitaController {

    List<ProcessWrapper> getProcesses(Session session) throws LoginException, ServerAPIException,
            BonitaHomeNotSetException, UnknownAPITypeException, IOException, LogoutException, SessionNotFoundException;

    List<TaskWrapper> getPendingTasks(Session session, int min, int max) throws IOException, LoginException,
            BonitaHomeNotSetException, ServerAPIException, UnknownAPITypeException, IOException, LoginException,
            LogoutException, SessionNotFoundException, ProcessDefinitionNotFoundException;

    List<TaskWrapper> getAssignedTasks(Session session, int min, int max) throws IOException, LoginException,
            BonitaHomeNotSetException, ServerAPIException, UnknownAPITypeException, IOException, LoginException,
            LogoutException, SessionNotFoundException, ProcessDefinitionNotFoundException;

    void takeTask(Session session, Long taskId) throws LoginException, ServerAPIException, BonitaHomeNotSetException,
            UnknownAPITypeException, IOException, LogoutException, SessionNotFoundException, UpdateException;

    void releaseTask(Session session, Long taskId) throws LoginException, ServerAPIException, BonitaHomeNotSetException,
            UnknownAPITypeException, IOException, ActivityInstanceNotFoundException, UpdateException, LogoutException,
            SessionNotFoundException;

    void hideTask(Session session, Long taskId) throws Exception;
}
