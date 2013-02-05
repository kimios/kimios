/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
 *
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kimios.controller;

import org.kimios.client.controller.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.util.Map;

/**
 * @author Fabien Alin
 */
public abstract class Controller {


    Logger log = LoggerFactory.getLogger(this.getClass().getName());


    protected static WebApplicationContext webApplicationContext;


    protected static AdministrationController administrationController;
    protected static StudioController studioController;
    protected static ReportingController reportingController;
    protected static LogController logController;
    protected static SecurityController securityController;
    protected static SearchController searchController;
    protected static DocumentController documentController;
    protected static DocumentVersionController documentVersionController;
    protected static FolderController folderController;
    protected static WorkflowController workflowController;
    protected static WorkspaceController workspaceController;
    protected static ServerInformationController serverInformationController;
    protected static FileTransferController fileTransferController;
    protected static ExtensionController extensionController;
    protected static RuleController ruleController;

    public static void init(ServletContext servletContext) {
        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        if (administrationController == null) {
            administrationController = (AdministrationController) wac.getBean("administrationController");
        }
        if (studioController == null) {
            studioController = (StudioController) wac.getBean("studioController");
        }
        if (reportingController == null) {
            reportingController = (ReportingController) wac.getBean("reportingController");
        }
        if (logController == null) {
            logController = (LogController) wac.getBean("logController");
        }
        if (securityController == null) {
            securityController = (SecurityController) wac.getBean("securityController");
        }
        if (searchController == null) {
            searchController = (SearchController) wac.getBean("searchController");
        }
        if (documentController == null) {
            documentController = (DocumentController) wac.getBean("documentController");
        }
        if (documentVersionController == null) {
            documentVersionController = (DocumentVersionController) wac.getBean("documentVersionController");
        }
        if (folderController == null) {
            folderController = (FolderController) wac.getBean("folderController");
        }
        if (workflowController == null) {
            workflowController = (WorkflowController) wac.getBean("workflowController");
        }
        if (workspaceController == null) {
            workspaceController = (WorkspaceController) wac.getBean("workspaceController");
        }
        if (serverInformationController == null) {
            serverInformationController = (ServerInformationController) wac.getBean("serverInformationController");
        }
        if (fileTransferController == null) {
            fileTransferController = (FileTransferController) wac.getBean("fileTransferController");
        }
        if (extensionController == null) {
            extensionController = (ExtensionController) wac.getBean("extensionController");
        }
        if (ruleController == null) {
            ruleController = (RuleController) wac.getBean("ruleController");
        }


    }


    protected String action;
    protected String sessionUid;
    protected Map<String, String> parameters;


    public String getSessionUid() {
        return sessionUid;
    }

    public void setSessionUid(String sessionUid) {
        this.sessionUid = sessionUid;
    }

    public abstract String execute() throws Exception;

    public Controller(Map<String, String> parameters) {
        this.parameters = parameters;
        this.action = parameters.get("action");
    }
}

