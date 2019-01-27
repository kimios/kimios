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

package org.kimios.osgi;

import org.kimios.core.CoreInitializer;
import org.kimios.core.FileCleaner;
import org.kimios.core.configuration.Config;
import org.kimios.i18n.InternationalizationManager;
import org.kimios.utils.configuration.ConfigurationManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.util.Properties;

/**
 * Created by farf on 3/30/15.
 */
public class OsgiCoreListener implements ServletContextListener {


    private static Logger log = LoggerFactory.getLogger(OsgiCoreListener.class);

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            Bundle webClientBundle = FrameworkUtil.getBundle(getClass());
            log.info("running kimios web client in osgi mode. Bundle Id " + webClientBundle.getBundleId() + " Version " + webClientBundle.getVersion().toString());
            InternationalizationManager.getInstance("EN");
            CoreInitializer.contextInitialized(event.getServletContext().getRealPath("/"));
            new FileCleaner().cleanTemporaryFiles(new File(
                   ConfigurationManager.getValue("client",Config.DM_TMP_FILES_PATH).toString()));
        } catch (Exception ex) {
            log.error("error while checking osgi mode, fallback to default", ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
