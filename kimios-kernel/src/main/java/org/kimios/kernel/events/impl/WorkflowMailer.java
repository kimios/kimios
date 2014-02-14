/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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
package org.kimios.kernel.events.impl;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.configuration.Config;
import org.kimios.kernel.dms.*;
import org.kimios.kernel.events.EventContext;
import org.kimios.kernel.events.GenericEventHandler;
import org.kimios.kernel.events.annotations.DmsEvent;
import org.kimios.kernel.events.annotations.DmsEventName;
import org.kimios.kernel.events.annotations.DmsEventOccur;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.mail.MailTemplate;
import org.kimios.kernel.mail.Mailer;
import org.kimios.kernel.security.SecurityEntityType;
import org.kimios.kernel.user.Group;
import org.kimios.kernel.user.User;
import org.kimios.kernel.utils.TemplateUtil;
import org.kimios.utils.configuration.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class WorkflowMailer extends GenericEventHandler
{
    private static Logger logger = LoggerFactory
            .getLogger(WorkflowMailer.class);

    @DmsEvent(eventName = { DmsEventName.WORKFLOW_STATUS_REQUEST_CREATE }, when = DmsEventOccur.AFTER)
    public void onWorflowRequestCreated(Object[] paramsObj, Object returnObj,
            EventContext ctx)
    {
        try {

            DocumentWorkflowStatusRequest req = (DocumentWorkflowStatusRequest) ctx
                    .getParameters().get("workflowRequest");
            List<User> users = this.getManagerUsers(req);

            String templatePath = "/org/kimios/kernel/templates/workflow-status-create.html";
            Document doc = FactoryInstantiator.getInstance()
                    .getDocumentFactory().getDocument(req.getDocumentUid());
            WorkflowStatus wfs = FactoryInstantiator.getInstance()
                    .getWorkflowStatusFactory()
                    .getWorkflowStatus(req.getWorkflowStatusUid());
            HashMap<String, Object> datas = new HashMap<String, Object>();
            datas.put("requestDate", new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").format(req
                    .getDate()));
            datas.put("documentId", doc.getUid());
            datas.put("doc", doc);
            datas.put("wfRequest", req);
            datas.put("wfStatus", wfs);
            datas.put("sender", ConfigurationManager.getValue(Config.SERVER_NAME));
            datas.put("documentName", doc.getName());
            datas.put("requsername", req.getUserName());
            datas.put("requsersource", req.getUserSource());
            String mailSubject = "["
                    + ConfigurationManager.getValue(Config.SERVER_NAME) + "] - "
                    + " Your approval is required";
            for (User user : users) {
                try {
                    if (user.getMail() != null && !user.getMail().equals("")) {

                        datas.put("username",
                                user.getName() != null && user.getName().length() > 0 ? user.getName() : user.getUid());
                        datas.put("user", user);

                        String template = TemplateUtil.generateContent(datas,
                                templatePath, "UTF-8");

                        MailTemplate mt = new MailTemplate(
                                ConfigurationManager
                                        .getValue(Config.WORKFLOW_MAILER_ADDRESS),
                                (ConfigurationManager
                                        .getValue(Config.WORKFLOW_MAILER_NAME) != null ? ConfigurationManager
                                        .getValue(Config.WORKFLOW_MAILER_NAME) :
                                        "Dms Notification"),
                                user.getMail(),
                                mailSubject,
                                template,
                                "text/html");

                        new Mailer(mt).start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DmsEvent(eventName = { DmsEventName.WORKFLOW_STATUS_REQUEST_ACCEPT }, when = DmsEventOccur.AFTER)
    public void onWorflowRequestAcceptedObject(Object[] paramsObj,
            Object returnObj, EventContext ctx)
    {
        try {

            DocumentWorkflowStatusRequest req = (DocumentWorkflowStatusRequest) ctx
                    .getParameters().get("acceptedWorkflowRequest");
            User user = this.getUser(req);
            if (user.getMail() != null && !user.getMail().equals("")) {
                String templatePath = "/org/kimios/kernel/templates/workflow-status-accept.html";
                Document doc = FactoryInstantiator.getInstance()
                        .getDocumentFactory().getDocument(req.getDocumentUid());
                WorkflowStatus wfs = FactoryInstantiator.getInstance()
                        .getWorkflowStatusFactory()
                        .getWorkflowStatus(req.getWorkflowStatusUid());
                HashMap<String, Object> datas = new HashMap<String, Object>();
                datas.put("requestDate", new SimpleDateFormat(
                        "MM/dd/yyyy hh:mm:ss").format(req.getDate()));
                datas.put("validationDate", new SimpleDateFormat(
                        "MM/dd/yyyy hh:mm:ss").format(req.getValidationDate()));
                datas.put("documentId", doc.getUid());
                datas.put("doc", doc);
                datas.put("wfRequest", req);
                datas.put("wfStatus", wfs);
                datas.put("sender", ConfigurationManager.getValue(Config.SERVER_NAME));
                datas.put("documentName", doc.getName());
                datas.put("username",
                        user.getName() != null && user.getName().length() > 0 ? user.getName() : user.getUid());
                String mailSubject = "["
                        + ConfigurationManager.getValue(Config.SERVER_NAME) + "] - "
                        + " Your request as been approved";
                String body = TemplateUtil.generateContent(datas, templatePath,
                        "UTF-8");

                MailTemplate mt = new MailTemplate(
                        ConfigurationManager.getValue(Config.WORKFLOW_MAILER_ADDRESS),
                        (ConfigurationManager
                                .getValue(Config.WORKFLOW_MAILER_NAME) != null ? ConfigurationManager
                                .getValue(Config.WORKFLOW_MAILER_NAME) :
                                "Dms Notification"),
                        user.getMail(),
                        mailSubject,
                        body,
                        "text/html");
                new Mailer(mt).start();
            }
            if (ctx.getParameters().get("workflowRequest") != null) {
                this.onWorflowRequestCreated(paramsObj, returnObj, ctx);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DmsEvent(eventName = { DmsEventName.WORKFLOW_STATUS_REQUEST_REJECT }, when = DmsEventOccur.AFTER)
    public void onWorflowRequestRejected(Object[] paramsObj, Object returnObj,
            EventContext ctx)
    {
        try {
            DocumentWorkflowStatusRequest req = (DocumentWorkflowStatusRequest) ctx
                    .getParameters().get("workflowRequest");
            logger.debug("Worflow status request: " + req);
            User user = this.getUser(req);
            if (user.getMail() != null && !user.getMail().equals("")) {
                String templatePath = "/org/kimios/kernel/templates/workflow-status-reject.html";
                Document doc = FactoryInstantiator.getInstance()
                        .getDocumentFactory().getDocument(req.getDocumentUid());
                WorkflowStatus wfs = FactoryInstantiator.getInstance()
                        .getWorkflowStatusFactory()
                        .getWorkflowStatus(req.getWorkflowStatusUid());
                HashMap<String, Object> datas = new HashMap<String, Object>();
                datas.put("requestDate", new SimpleDateFormat(
                        "MM/dd/yyyy hh:mm:ss").format(req.getDate()));
                datas.put("validationDate", new SimpleDateFormat(
                        "MM/dd/yyyy hh:mm:ss").format(req.getValidationDate()));
                datas.put("documentId", doc.getUid());
                datas.put("doc", doc);
                datas.put("wfRequest", req);
                datas.put("wfStatus", wfs);
                datas.put("sender", ConfigurationManager.getValue(Config.SERVER_NAME));
                datas.put("username",
                        user.getName() != null && user.getName().length() > 0 ? user.getName() : user.getUid());
                datas.put("documentName", doc.getName());

                String mailSubject = "["
                        + ConfigurationManager.getValue(Config.SERVER_NAME) + "] - "
                        + " Your request as been rejected";
                String body = TemplateUtil.generateContent(datas, templatePath,
                        "UTF-8");
                MailTemplate mt = new MailTemplate(
                        ConfigurationManager.getValue(Config.WORKFLOW_MAILER_ADDRESS),
                        (ConfigurationManager
                                .getValue(Config.WORKFLOW_MAILER_NAME) != null ? ConfigurationManager
                                .getValue(Config.WORKFLOW_MAILER_NAME) :
                                "Dms Notification"),
                        user.getMail(),
                        mailSubject,
                        body,
                        "text/html");
                new Mailer(mt).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private User getUser(DocumentWorkflowStatusRequest request)
            throws ConfigException, DataSourceException
    {
        return org.kimios.kernel.user.FactoryInstantiator.getInstance()
                .getAuthenticationSourceFactory()
                .getAuthenticationSource(request.getUserSource())
                .getUserFactory().getUser(request.getUserName());
    }

    private List<User> getManagerUsers(DocumentWorkflowStatusRequest request)
            throws ConfigException, DataSourceException
    {
        List<User> v = new ArrayList<User>();
        Vector<WorkflowStatusManager> m = FactoryInstantiator.getInstance()
                .getWorkflowStatusManagerFactory()
                .getWorkflowStatusManagers(request.getWorkflowStatusUid());
        for (WorkflowStatusManager wm : m) {
            if (wm.getSecurityEntityType() == SecurityEntityType.USER) {
                this.addToUsers(org.kimios.kernel.user.FactoryInstantiator
                        .getInstance().getAuthenticationSourceFactory()
                        .getAuthenticationSource(wm.getSecurityEntitySource())
                        .getUserFactory().getUser(wm.getSecurityEntityName()),
                        v);
            }
            if (wm.getSecurityEntityType() == SecurityEntityType.GROUP) {
                Group group = org.kimios.kernel.user.FactoryInstantiator
                        .getInstance().getAuthenticationSourceFactory()
                        .getAuthenticationSource(wm.getSecurityEntitySource())
                        .getGroupFactory().getGroup(wm.getSecurityEntityName());
                Vector<User> gu = org.kimios.kernel.user.FactoryInstantiator
                        .getInstance().getAuthenticationSourceFactory()
                        .getAuthenticationSource(wm.getSecurityEntitySource())
                        .getUserFactory().getUsers(group);
                for (User u : gu) {
                    this.addToUsers(u, v);
                }
            }
        }
        return v;
    }

    private void addToUsers(User user, List<User> v)
    {
        boolean add = true;
        for (User u : v) {
            if (u.getUid().equals(user.getUid())
                    && u.getAuthenticationSourceName().equals(
                    user.getAuthenticationSourceName()))
            {
                add = false;
            }
        }
        if (add) {
            v.add(user);
        }
    }
}
