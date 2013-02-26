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
package org.kimios.webservices;

import org.kimios.kernel.controller.*;

public abstract class CoreService
{
    private ServiceHelper helper;

    public ServiceHelper getHelper()
    {
        return helper;
    }

    public void setHelper(ServiceHelper helper)
    {
        this.helper = helper;
    }

    protected ISecurityController securityController;

    protected IWorkspaceController workspaceController;

    protected IDocumentController documentController;

    protected IDocumentVersionController documentVersionController;

    protected IFolderController folderController;

    protected IWorkflowController workflowController;

    protected IStudioController studioController;

    protected ISearchController searchController;

    protected IAdministrationController administrationController;

    protected IPathController pathController;

    protected IFileTransferController transferController;

    protected IRuleManagementController ruleController;

    protected IReportingController reportingController;

    protected IServerInformationController informationController;

    protected IExtensionController extensionController;

    protected IConvertController convertController;

    public IExtensionController getExtensionController()
    {
        return extensionController;
    }

    public void setExtensionController(IExtensionController extensionController)
    {
        this.extensionController = extensionController;
    }

    public IServerInformationController getInformationController()
    {
        return informationController;
    }

    public void setInformationController(
            IServerInformationController informationController)
    {
        this.informationController = informationController;
    }

    public IReportingController getReportingController()
    {
        return reportingController;
    }

    public void setReportingController(IReportingController reportingController)
    {
        this.reportingController = reportingController;
    }

    public ISecurityController getSecurityController()
    {
        return securityController;
    }

    public void setSecurityController(ISecurityController securityController)
    {
        this.securityController = securityController;
    }

    public IWorkspaceController getWorkspaceController()
    {
        return workspaceController;
    }

    public void setWorkspaceController(IWorkspaceController workspaceController)
    {
        this.workspaceController = workspaceController;
    }

    public IDocumentController getDocumentController()
    {
        return documentController;
    }

    public void setDocumentController(IDocumentController documentController)
    {
        this.documentController = documentController;
    }

    public IDocumentVersionController getDocumentVersionController()
    {
        return documentVersionController;
    }

    public void setDocumentVersionController(
            IDocumentVersionController documentVersionController)
    {
        this.documentVersionController = documentVersionController;
    }

    public IFolderController getFolderController()
    {
        return folderController;
    }

    public void setFolderController(IFolderController folderController)
    {
        this.folderController = folderController;
    }

    public IWorkflowController getWorkflowController()
    {
        return workflowController;
    }

    public void setWorkflowController(IWorkflowController workflowController)
    {
        this.workflowController = workflowController;
    }

    public IStudioController getStudioController()
    {
        return studioController;
    }

    public void setStudioController(IStudioController studioController)
    {
        this.studioController = studioController;
    }

    public ISearchController getSearchController()
    {
        return searchController;
    }

    public void setSearchController(ISearchController searchController)
    {
        this.searchController = searchController;
    }

    public IAdministrationController getAdministrationController()
    {
        return administrationController;
    }

    public void setAdministrationController(
            IAdministrationController administrationController)
    {
        this.administrationController = administrationController;
    }

    public IPathController getPathController()
    {
        return pathController;
    }

    public void setPathController(IPathController pathController)
    {
        this.pathController = pathController;
    }

    public IFileTransferController getTransferController()
    {
        return transferController;
    }

    public void setTransferController(IFileTransferController transferController)
    {
        this.transferController = transferController;
    }

    public IRuleManagementController getRuleController()
    {
        return ruleController;
    }

    public void setRuleController(IRuleManagementController ruleController)
    {
        this.ruleController = ruleController;
    }

    public IConvertController getConvertController()
    {
        return convertController;
    }

    public void setConvertController(IConvertController convertController)
    {
        this.convertController = convertController;
    }
}

