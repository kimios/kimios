package org.kimios.kernel.controller.impl;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.LoginAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.bpm.comment.Comment;
import org.bonitasoft.engine.bpm.flownode.ActivityInstanceCriterion;
import org.bonitasoft.engine.bpm.flownode.ActivityInstanceNotFoundException;
import org.bonitasoft.engine.bpm.flownode.HumanTaskInstance;
import org.bonitasoft.engine.bpm.process.ProcessDeploymentInfo;
import org.bonitasoft.engine.bpm.process.ProcessDeploymentInfoCriterion;
import org.bonitasoft.engine.exception.BonitaHomeNotSetException;
import org.bonitasoft.engine.exception.ServerAPIException;
import org.bonitasoft.engine.exception.UnknownAPITypeException;
import org.bonitasoft.engine.exception.UpdateException;
import org.bonitasoft.engine.identity.UserNotFoundException;
import org.bonitasoft.engine.platform.LoginException;
import org.bonitasoft.engine.platform.LogoutException;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.session.SessionNotFoundException;
import org.kimios.kernel.bonita.BonitaSettings;
import org.kimios.kernel.controller.BonitaController;
import org.kimios.kernel.security.Session;
import org.kimios.webservices.impl.factory.CommentWrapperFactory;
import org.kimios.webservices.impl.factory.ProcessWrapperFactory;
import org.kimios.webservices.impl.factory.TaskWrapperFactory;
import org.kimios.webservices.pojo.CommentWrapper;
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

        Set<Long> actorsId = new HashSet<Long>();
        actorsId.add(apiSession.getUserId());

        List<ProcessDeploymentInfo> processes = processAPI.getStartableProcessDeploymentInfosForActors(actorsId,
                Integer.MIN_VALUE, Integer.MAX_VALUE, ProcessDeploymentInfoCriterion.DEFAULT);

        log.info(processes.size() + " processes found");

        List<ProcessWrapper> wrappers = new ArrayList<ProcessWrapper>();

        for (ProcessDeploymentInfo p : processes) {
            ProcessWrapper wrapper = ProcessWrapperFactory.createProcessWrapper(p);

            wrapper.setUrl(bonitaCfg.getBonitaServerUrl() + "/" + bonitaCfg.getBonitaApplicationName() + "/console/" +
                    "homepage?__kb=" + session.getUid() + "&ui=form&locale=en#form=" + p.getName() + "--" +
                    p.getVersion() + "$entry&process=" + p.getProcessId() +
                    "&autoInstantiate=false&user=" + apiSession.getUserId() + "&mode=form");

            // TODO add document ID to url

            log.info(wrapper.toString());

            wrappers.add(wrapper);
        }

        logout(apiSession);
        return wrappers;
    }

    public List<TaskWrapper> getPendingTasks(Session session, int min, int max) throws Exception {

        APISession apiSession = login(session);
        ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(apiSession);
        IdentityAPI identityAPI = TenantAPIAccessor.getIdentityAPI(apiSession);

        List<HumanTaskInstance> pendingTasks = processAPI.getPendingHumanTaskInstances(
                apiSession.getUserId(), min, max, ActivityInstanceCriterion.PRIORITY_ASC);

        List<TaskWrapper> taskWrappers = getTaskWrappers(session, processAPI, identityAPI, pendingTasks);

        logout(apiSession);
        return taskWrappers;
    }

    public List<TaskWrapper> getAssignedTasks(Session session, int min, int max) throws Exception {

        APISession apiSession = login(session);
        ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(apiSession);
        IdentityAPI identityAPI = TenantAPIAccessor.getIdentityAPI(apiSession);

        List<HumanTaskInstance> assignedTasks = processAPI.getAssignedHumanTaskInstances(
                apiSession.getUserId(), min, max, ActivityInstanceCriterion.PRIORITY_ASC);

        List<TaskWrapper> taskWrappers = getTaskWrappers(session, processAPI, identityAPI, assignedTasks);

        logout(apiSession);
        return taskWrappers;
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

    public CommentWrapper addComment(Session session, Long taskId, String comment) throws LoginException, ServerAPIException, BonitaHomeNotSetException, UnknownAPITypeException, IOException, LogoutException, SessionNotFoundException, ActivityInstanceNotFoundException, UserNotFoundException {

        APISession apiSession = login(session);
        ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(apiSession);
        IdentityAPI identityAPI = TenantAPIAccessor.getIdentityAPI(apiSession);

        log.info("taskId: " + taskId);
        HumanTaskInstance task = processAPI.getHumanTaskInstance(taskId);

        log.info("Adding comment: " + comment);
        Comment submitted = processAPI.addComment(task.getParentProcessInstanceId(), comment);

        CommentWrapper c = CommentWrapperFactory.createCommentWrapper(submitted, identityAPI);

        logout(apiSession);

        return c;
    }

    public List<CommentWrapper> getComments(Session session, Long taskId) throws LoginException, ServerAPIException, BonitaHomeNotSetException, UnknownAPITypeException, IOException, ActivityInstanceNotFoundException, LogoutException, SessionNotFoundException, UserNotFoundException {
        APISession apiSession = login(session);
        ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(apiSession);
        IdentityAPI identityAPI = TenantAPIAccessor.getIdentityAPI(apiSession);

        log.info("taskId: " + taskId);
        HumanTaskInstance task = processAPI.getHumanTaskInstance(taskId);

        List<Comment> comments = processAPI.getComments(task.getParentProcessInstanceId());

        log.info(comments.size() + " comments found");

        List<CommentWrapper> wrappers = new ArrayList<CommentWrapper>();
        for (Comment c : comments) {
            wrappers.add(CommentWrapperFactory.createCommentWrapper(c, identityAPI));
        }

        logout(apiSession);

        return wrappers;
    }

    private List<TaskWrapper> getTaskWrappers(Session session, ProcessAPI processAPI, IdentityAPI identityAPI, List<HumanTaskInstance> tasks) throws Exception {

        List<TaskWrapper> wrappers = new ArrayList<TaskWrapper>();

        log.info(tasks.size() + " tasks found");

        for (HumanTaskInstance t : tasks) {

            TaskWrapper wrapper = TaskWrapperFactory.createTaskWrapper(t, identityAPI);

            // Add process to current task
            ProcessDeploymentInfo p = processAPI.getProcessDeploymentInfo(t.getProcessDefinitionId());
            wrapper.setProcessWrapper(ProcessWrapperFactory.createProcessWrapper(p));

            // Set direct url to task
            wrapper.setUrl(bonitaCfg.getBonitaServerUrl() + "/" + bonitaCfg.getBonitaApplicationName() + "/console/" +
                    "homepage?__kb=" + session.getUid() + "&ui=form&locale=en#form=" + p.getName() + "--" + p.getVersion() +
                    "--" + t.getName() + "$entry&task=" + t.getId() + "&mode=form");

            // Add comments to current task
            List<CommentWrapper> commentWrappers = new ArrayList<CommentWrapper>();
            List<Comment> comments = processAPI.getComments(t.getParentProcessInstanceId());
            log.info(comments.size() + " comments found");
            for (Comment c : comments) {
                commentWrappers.add(CommentWrapperFactory.createCommentWrapper(c, identityAPI));
            }
            wrapper.setCommentWrappers(commentWrappers);

            log.info(wrapper.toString());
            wrappers.add(wrapper);
        }

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
