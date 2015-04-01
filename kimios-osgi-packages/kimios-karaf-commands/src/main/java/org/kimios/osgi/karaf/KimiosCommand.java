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

import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.kimios.kernel.bonita.interfaces.IBonitaUsersSynchronizer;
import org.kimios.kernel.controller.*;
import org.kimios.kernel.index.ISolrIndexManager;
import org.kimios.kernel.security.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Created with IntelliJ IDEA. User: farf Date: 1/10/13 Time: 3:19 PM To change this template use File | Settings | File
 * Templates.
 */
public abstract class KimiosCommand extends OsgiCommandSupport
{
    protected static String KIMIOS_SESSION = "kimios-session";

    protected ISecurityController securityController;

    protected IAdministrationController administrationController;

    protected ISearchController searchController;

    protected ISearchManagementController searchManagementController;

    protected IPathController pathController;

    protected ISolrIndexManager indexManager;

    protected IBonitaUsersSynchronizer bonitaUsersSynchronizer;


    private static Logger logger = LoggerFactory.getLogger(KimiosCommand.class);

    @Override protected Object doExecute() throws Exception
    {
        Class<IAdministrationController> cAdmin = IAdministrationController.class;
        administrationController = getService(cAdmin);

        Class<ISecurityController> cSec = ISecurityController.class;
        securityController = getService(cSec);

        Class<ISearchController> cSearch = ISearchController.class;
        searchController = getService(cSearch);


        Class<ISearchManagementController> cSearchMng = ISearchManagementController.class;
        searchManagementController = getService(cSearchMng);


        Class<IPathController> cPath = IPathController.class;
        pathController = getService(cPath);



        Class<IBonitaUsersSynchronizer> cBonitaSync =
                IBonitaUsersSynchronizer.class;

        bonitaUsersSynchronizer = getService(cBonitaSync);


        Class<ISolrIndexManager> cSolrIdx = ISolrIndexManager.class;
        indexManager = getService(cSolrIdx);

        if (securityController == null) {
            logger.error("Kimios Security service is unavailable.");
            return null;
        }

        if (administrationController == null) {
            logger.error("Kimios Administration service is unavailable.");
            return null;
        }





        doExecuteKimiosCommand();
        return null;
    }

    protected abstract void doExecuteKimiosCommand() throws Exception;

    protected Session getCurrentSession()
    {
        if (this.session != null) {
            return (Session) this.session.get(KIMIOS_SESSION);
        }
        return null;
    }

    protected boolean isConnected()
    {
        boolean connected = false;
        if (this.session != null && this.session.get(KIMIOS_SESSION) != null) {

            connected = this.securityController.isSessionAlive(((Session) this.session.get(KIMIOS_SESSION)).getUid());
        } else {
            connected = false;
        }
        if(!connected){
            logger.error("Kimios Session unavailable. You should connect with kimios:admin");
        }
        return connected;
    }
}
