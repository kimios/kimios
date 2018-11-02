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

import org.kimios.utils.spring.SpringWebContextLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.net.URL;

/**
 * Custom Context Loader Listener
 */
public class AppContextLoaderListener extends ContextLoader implements ServletContextListener
{
    private static Logger log = LoggerFactory.getLogger(AppContextLoaderListener.class);

    private ContextLoader contextLoader;

    public void contextInitialized(ServletContextEvent event)
    {
        /*
           Look for app settings
        */
        this.contextLoader = this;
        DeploymentManager.init(this, event.getServletContext());
        if (DeploymentManager.isKimiosDeployed( event.getServletContext() )) {
            /*
               Launch app
             */
            log.info("starting Kimios Spring Context");
            SpringWebContextLauncher.launchApp(event.getServletContext(), this.contextLoader);
            log.info("Kimios started");
        } else {
            /*
                Load installer context
             */
            String installerUrl = "";
            log.info("Kimios isn't deployed. please go to {}", installerUrl);
        }
    }

    public void contextDestroyed(ServletContextEvent event)
    {

        if (DeploymentManager.isKimiosDeployed(event.getServletContext())) {
            SpringWebContextLauncher.shutdownApp(event.getServletContext(), this.contextLoader);
        }
    }
}
