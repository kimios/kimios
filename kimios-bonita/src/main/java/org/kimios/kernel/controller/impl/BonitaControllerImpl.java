package org.kimios.kernel.controller.impl;

import org.bonitasoft.engine.api.LoginAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.bpm.flownode.ActivityInstanceCriterion;
import org.bonitasoft.engine.bpm.flownode.HumanTaskInstance;
import org.bonitasoft.engine.bpm.process.ProcessDeploymentInfo;
import org.bonitasoft.engine.bpm.process.ProcessDeploymentInfoCriterion;
import org.bonitasoft.engine.exception.BonitaHomeNotSetException;
import org.bonitasoft.engine.exception.ServerAPIException;
import org.bonitasoft.engine.exception.UnknownAPITypeException;
import org.bonitasoft.engine.platform.LoginException;
import org.bonitasoft.engine.platform.LogoutException;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.session.SessionNotFoundException;
import org.kimios.kernel.bonita.BonitaSettings;
import org.kimios.webservices.pojo.ProcessWrapper;
import org.kimios.webservices.pojo.TaskWrapper;
import org.kimios.kernel.controller.BonitaController;
import org.kimios.kernel.security.Session;
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

        log.info(processes.size() + " processes found");

        Set<Long> actorsId = new HashSet<Long>();
        actorsId.add(apiSession.getUserId());

        List<ProcessWrapper> wrappers = new ArrayList<ProcessWrapper>();

        for (ProcessDeploymentInfo p : processes) {

            if (processAPI.isAllowedToStartProcess(p.getProcessId(), actorsId)) {
                ProcessWrapper wrapper = new ProcessWrapper(p);
                log.info(wrapper.toString());
                wrappers.add(wrapper);
            }
        }

        logout(apiSession);
        return wrappers;
    }

    public List<TaskWrapper> getPendingTasks(Session session) throws BonitaHomeNotSetException, ServerAPIException,
            UnknownAPITypeException, IOException, LoginException, LogoutException, SessionNotFoundException {

        APISession apiSession = login(session);
        ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(apiSession);

        List<HumanTaskInstance> pendingTasks = processAPI.getPendingHumanTaskInstances(
                apiSession.getUserId(), Integer.MIN_VALUE, Integer.MAX_VALUE, ActivityInstanceCriterion.PRIORITY_ASC);

        log.info(pendingTasks.size() + " tasks found");

        List<TaskWrapper> wrappers = new ArrayList<TaskWrapper>();

        for (HumanTaskInstance t : pendingTasks) {

//            processAPI.getTask


            TaskWrapper wrapper = new TaskWrapper(t);
            log.info(wrapper.toString());
            wrappers.add(wrapper);
        }

        logout(apiSession);
        return wrappers;

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
