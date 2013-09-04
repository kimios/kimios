package org.kimios.kernel.controller.impl;

import org.bonitasoft.engine.api.LoginAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.bpm.flownode.ActivityInstanceCriterion;
import org.bonitasoft.engine.bpm.flownode.ActivityInstanceNotFoundException;
import org.bonitasoft.engine.bpm.flownode.HumanTaskInstance;
import org.bonitasoft.engine.bpm.process.ProcessDefinitionNotFoundException;
import org.bonitasoft.engine.bpm.process.ProcessDeploymentInfo;
import org.bonitasoft.engine.bpm.process.ProcessDeploymentInfoCriterion;
import org.bonitasoft.engine.exception.BonitaHomeNotSetException;
import org.bonitasoft.engine.exception.ServerAPIException;
import org.bonitasoft.engine.exception.UnknownAPITypeException;
import org.bonitasoft.engine.exception.UpdateException;
import org.bonitasoft.engine.platform.LoginException;
import org.bonitasoft.engine.platform.LogoutException;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.session.SessionNotFoundException;
import org.kimios.kernel.bonita.BonitaSettings;
import org.kimios.kernel.controller.BonitaController;
import org.kimios.kernel.security.Session;
import org.kimios.webservices.factory.ProcessWrapperFactory;
import org.kimios.webservices.factory.TaskWrapperFactory;
import org.kimios.webservices.pojo.ProcessWrapper;
import org.kimios.webservices.pojo.TaskWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BonitaControllerImpl implements BonitaController {

    private static Logger log = LoggerFactory.getLogger(BonitaControllerImpl.class);

    private BonitaSettings bonitaCfg;


    public List<ProcessWrapper> getProcesses(Session session) throws LoginException, ServerAPIException,
            BonitaHomeNotSetException, UnknownAPITypeException, IOException, LogoutException, SessionNotFoundException {

        APISession apiSession = login(session);
        ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(apiSession);

        List<ProcessDeploymentInfo> processes = processAPI.getProcessDeploymentInfos(
                Integer.MIN_VALUE, Integer.MAX_VALUE, ProcessDeploymentInfoCriterion.DEFAULT);

        Set<Long> actorsId = new HashSet<Long>();
        actorsId.add(apiSession.getUserId());

        List<ProcessWrapper> wrappers = new ArrayList<ProcessWrapper>();

        for (ProcessDeploymentInfo p : processes) {
            if (processAPI.isAllowedToStartProcess(p.getProcessId(), actorsId)) {
                ProcessWrapper wrapper = ProcessWrapperFactory.createProcessWrapper(p);

                wrapper.setUrl(bonitaCfg.getBonitaServerUrl() + "/" + bonitaCfg.getBonitaApplicationName() + "/console/" +
                        "homepage?__kb=" + session.getUid() + "&ui=form&locale=en#form=" + p.getName() + "--" +
                        p.getVersion() + "$entry&process=" + p.getProcessId() +
                        "&autoInstantiate=false&user=" + apiSession.getUserId() + "&mode=form");

                // TODO add document ID to url

                log.info(wrapper.toString());

                wrappers.add(wrapper);
            }
        }

        logout(apiSession);
        return wrappers;
    }

    public List<TaskWrapper> getPendingTasks(Session session, int min, int max) throws BonitaHomeNotSetException, ServerAPIException,
            UnknownAPITypeException, IOException, LoginException, LogoutException, SessionNotFoundException, ProcessDefinitionNotFoundException {

        APISession apiSession = login(session);
        ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(apiSession);

        List<HumanTaskInstance> pendingTasks = processAPI.getPendingHumanTaskInstances(
                apiSession.getUserId(), min, max, ActivityInstanceCriterion.PRIORITY_ASC);

        List<TaskWrapper> wrappers = new ArrayList<TaskWrapper>();

        for (HumanTaskInstance t : pendingTasks) {

            TaskWrapper wrapper = TaskWrapperFactory.createTaskWrapper(t);
            ProcessDeploymentInfo p = processAPI.getProcessDeploymentInfo(t.getProcessDefinitionId());

            wrapper.setUrl(bonitaCfg.getBonitaServerUrl() + "/" + bonitaCfg.getBonitaApplicationName() + "/console/" +
                    "homepage?__kb=" + session.getUid() + "&ui=form&locale=en#form=" + p.getName() + "--" + p.getVersion() +
                    "--" + t.getName() + "$entry&task=" +
                    t.getId() + "&mode=form");

            wrapper.setProcessWrapper(ProcessWrapperFactory.createProcessWrapper(p));

            log.info(wrapper.toString());
            wrappers.add(wrapper);
        }

        logout(apiSession);
        return wrappers;
    }

    public List<TaskWrapper> getAssignedTasks(Session session, int min, int max) throws BonitaHomeNotSetException, ServerAPIException,
            UnknownAPITypeException, IOException, LoginException, LogoutException, SessionNotFoundException, ProcessDefinitionNotFoundException {

        APISession apiSession = login(session);
        ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(apiSession);

        List<HumanTaskInstance> pendingTasks = processAPI.getAssignedHumanTaskInstances(
                apiSession.getUserId(), min, max, ActivityInstanceCriterion.PRIORITY_ASC);

        List<TaskWrapper> wrappers = new ArrayList<TaskWrapper>();

        for (HumanTaskInstance t : pendingTasks) {

            TaskWrapper wrapper = TaskWrapperFactory.createTaskWrapper(t);
            ProcessDeploymentInfo p = processAPI.getProcessDeploymentInfo(t.getProcessDefinitionId());

            wrapper.setUrl(bonitaCfg.getBonitaServerUrl() + "/" + bonitaCfg.getBonitaApplicationName() + "/console/" +
                    "homepage?__kb=" + session.getUid() + "&ui=form&locale=en#form=" + p.getName() + "--" + p.getVersion() +
                    "--" + t.getName() + "$entry&task=" +
                    t.getId() + "&mode=form");

            wrapper.setProcessWrapper(ProcessWrapperFactory.createProcessWrapper(p));

            log.info(wrapper.toString());
            wrappers.add(wrapper);
        }

        logout(apiSession);
        return wrappers;
    }

    public void takeTask(Session session, Long taskId) throws LoginException, ServerAPIException,
            BonitaHomeNotSetException, UnknownAPITypeException, IOException, LogoutException, SessionNotFoundException,
            UpdateException {

        APISession apiSession = login(session);
        ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(apiSession);

        processAPI.assignUserTask(taskId, apiSession.getUserId());

        logout(apiSession);
    }

    public void releaseTask(Session session, Long taskId) throws LoginException, ServerAPIException,
            BonitaHomeNotSetException, UnknownAPITypeException, IOException, ActivityInstanceNotFoundException,
            UpdateException, LogoutException, SessionNotFoundException {

        APISession apiSession = login(session);
        ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(apiSession);

        processAPI.releaseUserTask(taskId);

        logout(apiSession);
    }

    public void hideTask(Session session, Long taskId) throws Exception {

        APISession apiSession = login(session);
        ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(apiSession);

        processAPI.hideTasks(apiSession.getUserId(), taskId);

        logout(apiSession);
    }

    private APISession login(Session session) throws IOException, BonitaHomeNotSetException, ServerAPIException, UnknownAPITypeException, LoginException {
        try {
            bonitaCfg.init();

            LoginAPI loginAPI = TenantAPIAccessor.getLoginAPI();
            return loginAPI.login(session.getUserName() + "@" + session.getUserSource(), session.getUid());

        } catch (LoginException e) {
            log.info("Error while authenticating to Bonita: " + e.getMessage());
            throw e;
        }
    }

    private void logout(APISession session) throws BonitaHomeNotSetException, ServerAPIException, UnknownAPITypeException, LogoutException, SessionNotFoundException {
        LoginAPI loginAPI = TenantAPIAccessor.getLoginAPI();
        loginAPI.logout(session);
    }

    public BonitaSettings getBonitaCfg() {
        return bonitaCfg;
    }

    public void setBonitaCfg(BonitaSettings bonitaCfg) {
        this.bonitaCfg = bonitaCfg;
    }
}
