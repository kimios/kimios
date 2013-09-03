package org.kimios.kernel.controller;

import org.bonitasoft.engine.bpm.process.ProcessActivationException;
import org.bonitasoft.engine.bpm.process.ProcessDefinitionNotFoundException;
import org.bonitasoft.engine.bpm.process.ProcessExecutionException;
import org.bonitasoft.engine.bpm.process.ProcessInstance;
import org.bonitasoft.engine.exception.BonitaHomeNotSetException;
import org.bonitasoft.engine.exception.ServerAPIException;
import org.bonitasoft.engine.exception.UnknownAPITypeException;
import org.bonitasoft.engine.platform.LoginException;
import org.bonitasoft.engine.platform.LogoutException;
import org.bonitasoft.engine.session.SessionNotFoundException;
import org.kimios.kernel.security.Session;
import org.kimios.webservices.pojo.ProcessInstanceWrapper;
import org.kimios.webservices.pojo.ProcessWrapper;
import org.kimios.webservices.pojo.TaskWrapper;

import java.io.IOException;
import java.util.List;

public interface BonitaController {

    List<ProcessWrapper> getProcesses(Session session) throws LoginException, ServerAPIException,
            BonitaHomeNotSetException, UnknownAPITypeException, IOException, LogoutException, SessionNotFoundException;

    List<TaskWrapper> getPendingTasks(Session session) throws IOException, LoginException, BonitaHomeNotSetException,
            ServerAPIException, UnknownAPITypeException, IOException, LoginException, LogoutException, SessionNotFoundException, ProcessDefinitionNotFoundException;

//    ProcessInstanceWrapper startProcess(Session session, Long documentId, Long processId)
//            throws LoginException, ServerAPIException, BonitaHomeNotSetException, UnknownAPITypeException, IOException,
//            ProcessDefinitionNotFoundException, ProcessExecutionException, ProcessActivationException, LogoutException, SessionNotFoundException;
}
