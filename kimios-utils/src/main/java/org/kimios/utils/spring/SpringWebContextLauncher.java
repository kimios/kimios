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
package org.kimios.utils.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.util.Enumeration;

/**
 */
public class SpringWebContextLauncher
{
    private static Logger logger = LoggerFactory.getLogger(SpringWebContextLauncher.class);

    private SpringWebContextLauncher()
    {
    }

    private static SpringWebContextLauncher instance = null;

    synchronized public static ApplicationContext launchApp(ServletContext ctx, ContextLoader loader)
    {

        if (instance == null) {
            instance = new SpringWebContextLauncher();
        }
        
        ConfigurableApplicationContext springContext =
                (ConfigurableApplicationContext) WebApplicationContextUtils.getWebApplicationContext(ctx);

        if(springContext == null){
            return loader.initWebApplicationContext(ctx);
        } else {
            springContext.refresh();
            return springContext;
        }
    }

    synchronized public static void shutdownApp(ServletContext servletContext, ContextLoader contextLoader)
    {
        if (contextLoader != null) {
            contextLoader.closeWebApplicationContext(servletContext);
        }
        /***
         Clean up context
         ***/
        Enumeration attrNames = servletContext.getAttributeNames();
        while (attrNames.hasMoreElements()) {
            String attrName = (String) attrNames.nextElement();
            if (attrName.startsWith("org.springframework.")) {
                Object attrValue = servletContext.getAttribute(attrName);
                if (attrValue instanceof DisposableBean) {
                    try {
                        ((DisposableBean) attrValue).destroy();
                    } catch (Throwable ex) {
                        logger.error("Couldn't invoke destroy method of attribute with name '" + attrName + "'", ex);
                    }
                }
            }
        }
    }
}
