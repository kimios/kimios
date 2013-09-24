package org.kimios.kernel.controller;

import org.bonitasoft.engine.bpm.flownode.ActivityInstanceNotFoundException;
import org.bonitasoft.engine.bpm.process.ProcessDefinitionNotFoundException;
import org.bonitasoft.engine.exception.BonitaHomeNotSetException;
import org.bonitasoft.engine.exception.ServerAPIException;
import org.bonitasoft.engine.exception.UnknownAPITypeException;
import org.bonitasoft.engine.exception.UpdateException;
import org.bonitasoft.engine.identity.UserNotFoundException;
import org.bonitasoft.engine.platform.LoginException;
import org.bonitasoft.engine.platform.LogoutException;
import org.bonitasoft.engine.session.SessionNotFoundException;
import org.kimios.kernel.security.Session;
import org.kimios.webservices.pojo.CommentWrapper;
import org.kimios.webservices.pojo.ProcessWrapper;
import org.kimios.webservices.pojo.TaskWrapper;
import org.kimios.webservices.pojo.TasksResponse;

import java.io.IOException;
import java.util.List;

public interface BonitaController {

    List<ProcessWrapper> getProcesses(Session session) throws LoginException, ServerAPIException,
            BonitaHomeNotSetException, UnknownAPITypeException, IOException, LogoutException, SessionNotFoundException, ProcessDefinitionNotFoundException;

    TasksResponse getPendingTasks(Session session, int start, int limit) throws Exception;

    TasksResponse getAssignedTasks(Session session, int start, int limit) throws Exception;

    TasksResponse getTasksByInstance(Session session, long processInstanceId, int start, int limit) throws Exception;

    void takeTask(Session session, Long taskId) throws LoginException, ServerAPIException, BonitaHomeNotSetException,
            UnknownAPITypeException, IOException, LogoutException, SessionNotFoundException, UpdateException;

    void releaseTask(Session session, Long taskId) throws LoginException, ServerAPIException, BonitaHomeNotSetException,
            UnknownAPITypeException, IOException, ActivityInstanceNotFoundException, UpdateException, LogoutException,
            SessionNotFoundException;

    void hideTask(Session session, Long taskId) throws Exception;

    CommentWrapper addComment(Session session, Long taskId, String comment)
            throws LoginException, ServerAPIException, BonitaHomeNotSetException, UnknownAPITypeException, IOException,
            LogoutException, SessionNotFoundException, ActivityInstanceNotFoundException, UserNotFoundException;

    List<CommentWrapper> getComments(Session session, Long taskId)
            throws LoginException, ServerAPIException, BonitaHomeNotSetException, UnknownAPITypeException, IOException,
            ActivityInstanceNotFoundException, LogoutException, SessionNotFoundException, UserNotFoundException;
}
