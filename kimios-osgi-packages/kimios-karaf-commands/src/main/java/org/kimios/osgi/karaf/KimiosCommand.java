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

package org.kimios.osgi.karaf;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.kimios.kernel.bonita.interfaces.IBonitaUsersSynchronizer;
import org.kimios.kernel.controller.*;
import org.kimios.kernel.index.ISolrIndexManager;
import org.kimios.kernel.index.controller.ISearchController;
import org.kimios.kernel.index.controller.ISearchManagementController;
import org.kimios.kernel.security.model.Session;
import org.kimios.utils.extension.IExtensionRegistryManager;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.TransactionManager;


/**
 * Created with IntelliJ IDEA. User: farf Date: 1/10/13 Time: 3:19 PM To change this template use File | Settings | File
 * Templates.
 */
public abstract class KimiosCommand implements Action
{
    private static Logger logger = LoggerFactory.getLogger(KimiosCommand.class);

    protected static String KIMIOS_SESSION = "kimios-session";

    @Reference
    protected ISecurityController securityController;

    @Reference
    protected IAdministrationController administrationController;

    @Reference
    protected ISearchController searchController;

    @Reference
    protected ISearchManagementController searchManagementController;

    @Reference
    protected IPathController pathController;

    @Reference
    protected ISolrIndexManager indexManager;

    @Reference
    protected IBonitaUsersSynchronizer bonitaUsersSynchronizer;

    @Reference
    protected IWorkspaceController workspaceController;

    @Reference
    protected IFileTransferController fileTransferController;

    @Reference
    protected IExtensionRegistryManager extensionRegistryManager;

    @Reference
    protected TransactionManager transactionManager;

    @Reference
    protected org.apache.karaf.shell.api.console.Session karafSession;

    @Override
    public Object execute() throws Exception
    {
        doExecuteKimiosCommand();
        return null;
    }

    protected abstract void doExecuteKimiosCommand() throws Exception;

    protected Session getCurrentSession()
    {

        if (this.karafSession != null) {
            return (Session) this.karafSession.get(KIMIOS_SESSION);
        }
        return null;
    }

    protected boolean isConnected()
    {
        boolean connected = false;
        if (this.karafSession != null && this.karafSession.get(KIMIOS_SESSION) != null) {

            connected = this.securityController.isSessionAlive(((Session) this.karafSession.get(KIMIOS_SESSION)).getUid());
        } else {
            connected = false;
        }
        if(!connected){
            String message = "Kimios Session unavailable. You should connect with kimios:admin";
            logger.error(message);
            this.karafSession.getConsole().println(message);
        }
        return connected;
    }

    public <T> T getService(Class<T> contract) {
        ServiceReference<T> refCurr = FrameworkUtil.getBundle(this.getClass())
                .getBundleContext().getServiceReference(contract);
        return FrameworkUtil.getBundle(contract)
                .getBundleContext().getService(refCurr);
    }
}
