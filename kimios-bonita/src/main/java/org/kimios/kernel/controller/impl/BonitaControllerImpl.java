/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2015  DevLib'
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
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kimios.kernel.controller.impl;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.LoginAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.bpm.comment.Comment;
import org.bonitasoft.engine.bpm.data.DataDefinition;
import org.bonitasoft.engine.bpm.flownode.ActivityInstance;
import org.bonitasoft.engine.bpm.flownode.ActivityInstanceCriterion;
import org.bonitasoft.engine.bpm.flownode.HumanTaskInstance;
import org.bonitasoft.engine.bpm.flownode.MultiInstanceActivityInstance;
import org.bonitasoft.engine.bpm.process.ProcessDefinitionNotFoundException;
import org.bonitasoft.engine.bpm.process.ProcessDeploymentInfo;
import org.bonitasoft.engine.bpm.process.ProcessDeploymentInfoCriterion;
import org.bonitasoft.engine.platform.LoginException;
import org.bonitasoft.engine.session.APISession;
import org.kimios.kernel.bonita.BonitaSettings;
import org.kimios.kernel.controller.BonitaController;
import org.kimios.kernel.exception.DmsKernelException;
import org.kimios.kernel.security.Session;
import org.kimios.webservices.impl.factory.CommentWrapperFactory;
import org.kimios.webservices.impl.factory.ProcessWrapperFactory;
import org.kimios.webservices.impl.factory.TaskWrapperFactory;
import org.kimios.webservices.pojo.CommentWrapper;
import org.kimios.webservices.pojo.ProcessWrapper;
import org.kimios.webservices.pojo.TaskWrapper;
import org.kimios.webservices.pojo.TasksResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Transactional
public class BonitaControllerImpl implements BonitaController {

    private static Logger log = LoggerFactory.getLogger(BonitaControllerImpl.class);
    private static final String KIMIOS_PREFIX_VARIABLE = "kimiosData";

    private BonitaSettings bonitaCfg;

    public List<ProcessWrapper> getProcesses(Session session) throws DmsKernelException {

        try {

            if (!bonitaCfg.isBonitaEnabled()) {
                log.debug("disabled bonita link. returning empty data");
                return new ArrayList<ProcessWrapper>();
            }

            APISession apiSession = login(session);
            ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(apiSession);

            Set<Long> actorsId = new HashSet<Long>();
            actorsId.add(apiSession.getUserId());

            List<ProcessDeploymentInfo> processes = processAPI.getProcessDeploymentInfos(0, Integer.MAX_VALUE,
                    ProcessDeploymentInfoCriterion.DEFAULT);

            log.info(processes.size() + " processes found");

            List<ProcessWrapper> wrappers = new ArrayList<ProcessWrapper>();

            for (ProcessDeploymentInfo p : processes) {
                if (isKimiosProcessDataDefinitions(processAPI, p.getProcessId())) {

                    ProcessWrapper wrapper = ProcessWrapperFactory.createProcessWrapper(p);

                    wrapper.setUrl(bonitaCfg.getBonitaPublicServerUrl() + (bonitaCfg.getBonitaApplicationName() != null
                            && bonitaCfg.getBonitaApplicationName().length() > 0 ? "/" + bonitaCfg.getBonitaApplicationName() : "") + "/portal/" +
                            "homepage?__kb=" + session.getUid() + "&ui=form&locale=en#form=" + p.getName() + "--" +
                            p.getVersion() + "$entry&process=" + p.getProcessId() +
                            "&autoInstantiate=false&user=" + apiSession.getUserId() + "&mode=app");

                    log.info(wrapper.toString());
                    wrappers.add(wrapper);
                }
            }

            logout(apiSession);
            return wrappers;

        } catch (Exception e) {
            log.error("Exception while getting process", e);
            throw new DmsKernelException(e);
        }
    }


    public TasksResponse getPendingTasks(Session session, int start, int limit) throws DmsKernelException {
        try {


            if (!bonitaCfg.isBonitaEnabled()) {
                log.debug("disabled bonita link. returning empty data");
                return new TasksResponse(new ArrayList<TaskWrapper>(), 0);
            }

            APISession apiSession = login(session);
            ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(apiSession);
            IdentityAPI identityAPI = TenantAPIAccessor.getIdentityAPI(apiSession);

            List<HumanTaskInstance> pendingTasks = processAPI.getPendingHumanTaskInstances(
                    apiSession.getUserId(), start, limit, ActivityInstanceCriterion.PRIORITY_ASC);

            Number count = processAPI.getNumberOfPendingHumanTaskInstances(apiSession.getUserId());

            List<TaskWrapper> taskWrappers = getTaskWrappers(session, processAPI, identityAPI, pendingTasks);

            logout(apiSession);

            return new TasksResponse(taskWrappers, count);

        } catch (Exception e) {
            log.error("Exception while listing pending tasks", e);
            throw new DmsKernelException(e);
        }
    }

    public TasksResponse getAssignedTasks(Session session, int start, int limit) throws DmsKernelException {
        try {

            if (!bonitaCfg.isBonitaEnabled()) {
                log.debug("disabled bonita link. returning empty data");
                return new TasksResponse(new ArrayList<TaskWrapper>(), 0);
            }

            APISession apiSession = login(session);
            ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(apiSession);
            IdentityAPI identityAPI = TenantAPIAccessor.getIdentityAPI(apiSession);


            System.out.println(" Just before getting whole human tasks " + start + " " + limit);
            ;
            List<HumanTaskInstance> assignedTasks = processAPI.getAssignedHumanTaskInstances(
                    apiSession.getUserId(), start, limit, ActivityInstanceCriterion.PRIORITY_ASC);


            Number count = processAPI.getNumberOfAssignedHumanTaskInstances(apiSession.getUserId());

            List<TaskWrapper> taskWrappers = getTaskWrappers(session, processAPI, identityAPI, assignedTasks);

            logout(apiSession);

            return new TasksResponse(taskWrappers, count);

        } catch (Exception e) {
            log.error("Exception while getting assigned tasks", e);
            throw new DmsKernelException(e);
        }
    }

    public TasksResponse getTasksByInstance(Session session, long processInstanceId, int start, int limit)
            throws DmsKernelException {
        try {


            if (!bonitaCfg.isBonitaEnabled()) {
                log.debug("disabled bonita link. returning empty data");
                return new TasksResponse(new ArrayList<TaskWrapper>(), 0);
            }

            APISession apiSession = login(session);
            ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(apiSession);
            IdentityAPI identityAPI = TenantAPIAccessor.getIdentityAPI(apiSession);

            List<ActivityInstance> tasks = processAPI.getActivities(processInstanceId, start, limit);

            Number count = processAPI.getNumberOfOpenedActivityInstances(processInstanceId);

            List<TaskWrapper> taskWrappers = getTaskWrappers(session, processAPI, identityAPI, tasks);

            logout(apiSession);

            return new TasksResponse(taskWrappers, count);

        } catch (Exception e) {
            log.error("Exception while getting task by instance", e);
            throw new DmsKernelException(e);
        }
    }

    public void takeTask(Session session, Long taskId) throws DmsKernelException {

        try {
            APISession apiSession = login(session);
            ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(apiSession);

            processAPI.assignUserTask(taskId, apiSession.getUserId());

            logout(apiSession);
        } catch (Exception e) {
            log.error("Exception while taking task", e);
            throw new DmsKernelException(e);
        }
    }

    public void releaseTask(Session session, Long taskId) throws DmsKernelException {

        try {
            APISession apiSession = login(session);
            ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(apiSession);

            processAPI.releaseUserTask(taskId);

            logout(apiSession);
        } catch (Exception e) {
            log.error("Exception while releasing task", e);
            throw new DmsKernelException(e);
        }
    }

    public void hideTask(Session session, Long taskId) throws DmsKernelException {
        try {
            APISession apiSession = login(session);
            ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(apiSession);

            processAPI.hideTasks(apiSession.getUserId(), taskId);

            logout(apiSession);

        } catch (Exception e) {
            log.error("Exception while hiding task", e);
            throw new DmsKernelException(e);
        }
    }

    public CommentWrapper addComment(Session session, Long taskId, String comment) throws DmsKernelException {

        try {


            if (!bonitaCfg.isBonitaEnabled()) {
                log.debug("disabled bonita link. returning empty data");
                return new CommentWrapper();
            }

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
        } catch (Exception e) {
            log.error("Exception while adding comment", e);
            throw new DmsKernelException(e);
        }
    }

    public List<CommentWrapper> getComments(Session session, Long taskId) throws DmsKernelException {
        try {

            if (!bonitaCfg.isBonitaEnabled()) {
                log.debug("disabled bonita link. returning empty data");
                return new ArrayList<CommentWrapper>();
            }

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

        } catch (Exception e) {
            log.error("Exception while getting comment", e);
            throw new DmsKernelException(e);
        }
    }

    private List<TaskWrapper> getTaskWrappers(Session session, ProcessAPI processAPI, IdentityAPI identityAPI, List<? extends ActivityInstance> tasks) throws Exception {

        List<TaskWrapper> wrappers = new ArrayList<TaskWrapper>();

        log.info(tasks.size() + " tasks found");

        for (ActivityInstance t : tasks) {

            TaskWrapper wrapper = null;
            if (t instanceof HumanTaskInstance)
                wrapper = TaskWrapperFactory.createTaskWrapper((HumanTaskInstance) t, identityAPI);
            else if (t instanceof MultiInstanceActivityInstance)
                wrapper = TaskWrapperFactory.createTaskWrapper((MultiInstanceActivityInstance) t, identityAPI);


            // Add process to current task
            ProcessDeploymentInfo p = processAPI.getProcessDeploymentInfo(t.getProcessDefinitionId());
            wrapper.setProcessWrapper(ProcessWrapperFactory.createProcessWrapper(p));
            // Set direct url to task
            wrapper.setUrl(bonitaCfg.getBonitaPublicServerUrl() + (bonitaCfg.getBonitaApplicationName() != null
                    && bonitaCfg.getBonitaApplicationName().length() > 0 ? "/" + bonitaCfg.getBonitaApplicationName() : "") + "/portal/" +
                    "homepage?__kb=" + session.getUid() + "&ui=form&locale=en#form=" + p.getName() + "--" + p.getVersion() +
                    "--" + t.getName() + "$entry&task=" + t.getId() + "&mode=app");
            log.info(wrapper.toString());
            wrappers.add(wrapper);
        }

        return wrappers;

    }


    private APISession login(Session session) throws LoginException {
        try {
            bonitaCfg.init();

            LoginAPI loginAPI = TenantAPIAccessor.getLoginAPI();
            return loginAPI.login(session.getUserName() + "@" + session.getUserSource(), session.getUid());

        } catch (LoginException e) {
            log.error("error while authenticating to Bonita", e);
            throw new DmsKernelException(e, "error while authenticating to Bonita");
        } catch (Exception e) {
            log.error("error while attempting bonita connexion", e);
            throw new DmsKernelException(e, "error while attempting bonita connexion");
        }
    }

    private boolean isKimiosProcessDataDefinitions(ProcessAPI processAPI, long processId)
            throws ProcessDefinitionNotFoundException {

        List<DataDefinition> dataDefinitions = processAPI.getProcessDataDefinitions(processId, 0, Integer.MAX_VALUE);
        for (DataDefinition dataDefinition : dataDefinitions) {
            if (dataDefinition.getName().startsWith(KIMIOS_PREFIX_VARIABLE)) {
                return true;
            }
        }

        return false;

    }

    private void logout(APISession session) throws DmsKernelException {
        try {
            LoginAPI loginAPI = TenantAPIAccessor.getLoginAPI();
            loginAPI.logout(session);
        } catch (Exception e) {
            log.error("error while attempting bonita connexion for logout", e);
            throw new DmsKernelException(e, "error while attempting bonita connexion");
        }
    }

    public BonitaSettings getBonitaCfg() {
        return bonitaCfg;
    }

    public void setBonitaCfg(BonitaSettings bonitaCfg) {
        this.bonitaCfg = bonitaCfg;
    }
}
