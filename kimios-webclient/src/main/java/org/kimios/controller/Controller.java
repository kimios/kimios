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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kimios.controller;

import org.kimios.client.controller.*;
import org.kimios.core.ApplicationContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

/**
 * @author Fabien Alin
 */
public class Controller {


    Logger log = LoggerFactory.getLogger(this.getClass().getName());

    protected static org.kimios.core.ApplicationContextProvider applicationContextProvider;

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
    protected static BonitaController bonitaController;
    protected static ShareController shareController;


    public Controller(){}

    public synchronized static void init(ApplicationContextProvider appContext) {

        applicationContextProvider = appContext;

        if (administrationController == null) {
            administrationController = applicationContextProvider.getBean(AdministrationController.class);
        }
        if (studioController == null) {
            studioController = applicationContextProvider.getBean(StudioController.class);
        }
        if (reportingController == null) {
            reportingController = applicationContextProvider.getBean(ReportingController.class);
        }
        if (logController == null) {
            logController = applicationContextProvider.getBean(LogController.class);
        }
        if (securityController == null) {
            securityController = applicationContextProvider.getBean(SecurityController.class);
        }
        if (searchController == null) {
            searchController = applicationContextProvider.getBean(SearchController.class);
        }
        if (documentController == null) {
            documentController = applicationContextProvider.getBean(DocumentController.class);
        }
        if (documentVersionController == null) {
            documentVersionController = applicationContextProvider.getBean(DocumentVersionController.class);
        }
        if (folderController == null) {
            folderController = applicationContextProvider.getBean(FolderController.class);
        }
        if (workflowController == null) {
            workflowController = applicationContextProvider.getBean(WorkflowController.class);
        }
        if (workspaceController == null) {
            workspaceController = applicationContextProvider.getBean(WorkspaceController.class);
        }
        if (serverInformationController == null) {
            serverInformationController = applicationContextProvider.getBean(ServerInformationController.class);
        }
        if (fileTransferController == null) {
            fileTransferController = applicationContextProvider.getBean(FileTransferController.class);
        }
        if (extensionController == null) {
            extensionController = applicationContextProvider.getBean(ExtensionController.class);
        }
        if (ruleController == null) {
            ruleController = applicationContextProvider.getBean(RuleController.class);
        }
        if (bonitaController == null) {
            bonitaController = applicationContextProvider.getBean(BonitaController.class);
        }
        if (shareController == null) {
            shareController = applicationContextProvider.getBean(ShareController.class);
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

    public String execute() throws Exception {
        return null;
    }

    public Controller(Map<String, String> parameters) {
        this.parameters = parameters;
        this.action = parameters.get("action");
    }


    public static AdministrationController getAdministrationController() {
        return administrationController;
    }

    public void setAdministrationController(AdministrationController administrationController) {
        Controller.administrationController = administrationController;
    }

    public static StudioController getStudioController() {
        return studioController;
    }

    public void setStudioController(StudioController studioController) {
        Controller.studioController = studioController;
    }

    public static ReportingController getReportingController() {
        return reportingController;
    }

    public void setReportingController(ReportingController reportingController) {
        Controller.reportingController = reportingController;
    }

    public static LogController getLogController() {
        return logController;
    }

    public void setLogController(LogController logController) {
        Controller.logController = logController;
    }

    public static SecurityController getSecurityController() {
        return securityController;
    }

    public void setSecurityController(SecurityController securityController) {
        Controller.securityController = securityController;
    }

    public static SearchController getSearchController() {
        return searchController;
    }

    public void setSearchController(SearchController searchController) {
        Controller.searchController = searchController;
    }

    public static DocumentController getDocumentController() {
        return documentController;
    }

    public void setDocumentController(DocumentController documentController) {
        Controller.documentController = documentController;
    }

    public static DocumentVersionController getDocumentVersionController() {
        return documentVersionController;
    }

    public void setDocumentVersionController(DocumentVersionController documentVersionController) {
        Controller.documentVersionController = documentVersionController;
    }

    public static FolderController getFolderController() {
        return folderController;
    }

    public void setFolderController(FolderController folderController) {
        Controller.folderController = folderController;
    }

    public static WorkflowController getWorkflowController() {
        return workflowController;
    }

    public void setWorkflowController(WorkflowController workflowController) {
        Controller.workflowController = workflowController;
    }

    public static WorkspaceController getWorkspaceController() {
        return workspaceController;
    }

    public void setWorkspaceController(WorkspaceController workspaceController) {
        Controller.workspaceController = workspaceController;
    }

    public static ServerInformationController getServerInformationController() {
        return serverInformationController;
    }

    public void setServerInformationController(ServerInformationController serverInformationController) {
        Controller.serverInformationController = serverInformationController;
    }

    public static FileTransferController getFileTransferController() {
        return fileTransferController;
    }

    public void setFileTransferController(FileTransferController fileTransferController) {
        Controller.fileTransferController = fileTransferController;
    }

    public static ExtensionController getExtensionController() {
        return extensionController;
    }

    public void setExtensionController(ExtensionController extensionController) {
        Controller.extensionController = extensionController;
    }

    public static RuleController getRuleController() {
        return ruleController;
    }

    public void setRuleController(RuleController ruleController) {
        Controller.ruleController = ruleController;
    }

    public static ShareController getShareController() {
        return shareController;
    }

    public void setShareController(ShareController shareController) {
        Controller.shareController = shareController;
    }

    public static BonitaController getBonitaController() {
        return bonitaController;
    }

    public void setBonitaController(BonitaController bonitaController) {
        Controller.bonitaController = bonitaController;
    }




}

