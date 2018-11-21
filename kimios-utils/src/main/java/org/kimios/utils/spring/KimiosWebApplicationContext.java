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
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.XmlWebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;

public class KimiosWebApplicationContext extends XmlWebApplicationContext {

    private static Logger logger = LoggerFactory.getLogger(KimiosWebApplicationContext.class);

    public static String KIMIOS_HOME = "kimios.home";

    public static String KIMIOS_APP_ATTRIBUTE_NAME = "kimios.app.name";

    @Override
    protected String[] getDefaultConfigLocations() {
        String kimiosHomeDirectory = System.getProperty(KimiosWebApplicationContext.KIMIOS_HOME);
        if (kimiosHomeDirectory != null) {
            /*
                Load servlet context attribute (for client or server)
             */
            String kimiosAppConfDirectory = this.getServletContext().getInitParameter(KIMIOS_APP_ATTRIBUTE_NAME);
            File kimiosHome = new File(kimiosHomeDirectory + "/" + kimiosAppConfDirectory);

            /*
                Start Spring loading
             */
            String springConfigLocation = kimiosHome.getAbsolutePath() + "/conf/ctx-kimios.xml";
            String kimiosConfigLocation = kimiosHome.getAbsolutePath() + "/conf/kimios.properties";
            if (! new File(springConfigLocation).exists() || ! new File(kimiosConfigLocation).exists()) {
                logger.info("kimios Spring configuration isn't available");
                return new String[]{};
            } else {
                logger.info("starting Kimios DMS with settings directory {}", kimiosHomeDirectory + "/conf");
                return new String[]{springConfigLocation};
            }


        }
        throw new RuntimeException("Kimios Home Not found. Please check kimios.home value, and target directory");
    }

    @Override
    protected Resource getResourceByPath(String path) {
        return new FileSystemResource(path);
    }
}