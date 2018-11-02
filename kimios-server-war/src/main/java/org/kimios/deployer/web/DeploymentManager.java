/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2018  DevLib'
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
package org.kimios.deployer.web;

import org.springframework.web.context.ContextLoader;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.Map;

/**
 *
 */
public class DeploymentManager
{
    private ContextLoader loader;

    private static DeploymentManager manager;

    public static String DM_DEPLOYED = "kimios-install";

    public static Boolean isKimiosDeployed(ServletContext ctx)
    {
        return (((Boolean) (ctx.getAttribute(DM_DEPLOYED) != null ? ctx.getAttribute(DM_DEPLOYED) : false)));
    }

    synchronized public static void init(ContextLoader loader, ServletContext servletContext)
    {

        /*
            Check deployment status
         */
        if (manager == null) {
            manager = new DeploymentManager();
        }
        manager.setLoader(loader);


        //server
        String kimiosAppPath = System.getProperty("kimios.home");
        if(kimiosAppPath != null){
            File kimiosHome = new File(kimiosAppPath);

            //server
            File fileConf = new File(kimiosHome,servletContext.getInitParameter("kimios.app.name"));
            File serverConfDir = new File(fileConf, "conf");
            File serverConfFile = new File(serverConfDir, "kimios.properties");
            if(fileConf.exists() && fileConf.isDirectory()
                    && serverConfDir.exists() && serverConfDir.isDirectory()
                    && serverConfFile.exists()){
                servletContext.setAttribute(DM_DEPLOYED, Boolean.TRUE);
            } else {
                servletContext.setAttribute(DM_DEPLOYED, Boolean.FALSE);
            }
        }
        //client
    }

    protected void setLoader(ContextLoader loader)
    {
        this.loader = loader;
    }

    public static ContextLoader getContextLoader()
    {
        if (manager != null) {
            return manager.loader;
        }

        return null;
    }

    synchronized public static void endInstall(ContextLoader loader, ServletContext servletContext)
    {
        if (manager == null) {
            manager = new DeploymentManager();
        }
        manager.setLoader(loader);
        servletContext.setAttribute(DM_DEPLOYED, Boolean.TRUE);
    }

    synchronized public void handleInstall(Map<String, String> parameters)
    {
    }
}
