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

import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.Http11AprProtocol;
import org.apache.coyote.http11.Http11NioProtocol;
import org.kimios.utils.spring.SpringWebContextLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.catalina.Server;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

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
        SpringWebContextLauncher.launchApp(event.getServletContext(), this.contextLoader);
        List<String> urls = generateTomcatServerUrl();

        if (!DeploymentManager.isKimiosDeployed( event.getServletContext() )) {
            if(urls.size() > 0){
                log.info("Kimios isn't deployed. please go to one of: {}", urls);
            }
        }
    }

    private List<String> generateTomcatServerUrl() {
        try {


            MBeanServer mBeanServer = MBeanServerFactory.findMBeanServer(null).get(0);
            ObjectName name = new ObjectName("Catalina", "type", "Server");
            Server server = (Server) mBeanServer.getAttribute(name, "managedResource");


            List<Integer> ports = new ArrayList<Integer>();
            List<Integer> sslPorts = new ArrayList<Integer>();
            Service[] services = server.findServices();
            for (Service service : services) {
                for (Connector connector : service.findConnectors()) {
                    ProtocolHandler protocolHandler = connector.getProtocolHandler();
                    if (protocolHandler instanceof Http11AprProtocol
                            || protocolHandler instanceof Http11AprProtocol
                            || protocolHandler instanceof Http11NioProtocol) {

                        if(protocolHandler.findSslHostConfigs() != null && protocolHandler.findSslHostConfigs().length > 0){
                            sslPorts.add(connector.getPort());
                        } else {
                            ports.add(connector.getPort());
                        }
                    }
                }
            }

            String host = InetAddress.getLocalHost().getHostAddress();
            List<String> items = new ArrayList<String>();
            if(ports.size() == 1)
                items.add("http://" + host + ":" + ports.get(0) + "/kimios");

            if(sslPorts.size() == 1)
                items.add("https://" + host + ":" + sslPorts.get(0) + "/kimios");


            return items;
        }catch (Exception ex){
            log.error("unable to get tomcat informations", ex);
            return new ArrayList<String>();
        }
    }

    public void contextDestroyed(ServletContextEvent event)
    {

        if (DeploymentManager.isKimiosDeployed(event.getServletContext())) {
            SpringWebContextLauncher.shutdownApp(event.getServletContext(), this.contextLoader);
        }
    }
}
