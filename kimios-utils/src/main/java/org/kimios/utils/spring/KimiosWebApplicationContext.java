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

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.XmlWebApplicationContext;

import java.io.File;

public class KimiosWebApplicationContext extends XmlWebApplicationContext {



    public static String KIMIOS_HOME = "kimios.home";


    private static String KIMIOS_APP_ATTRIBUTE_NAME = "kimios.app.name";

    @Override
    protected String[] getDefaultConfigLocations() {
        String kimiosHomeDirectory = System.getProperty(KimiosWebApplicationContext.KIMIOS_HOME);
        if (kimiosHomeDirectory != null) {

            /*
                Load servlet context attribute (for client or server)
             */

            String kimiosAppConfDirectory = this.getServletContext().getInitParameter(KIMIOS_APP_ATTRIBUTE_NAME);


            File kimiosHome = new File(kimiosHomeDirectory + "/" + kimiosAppConfDirectory);
            if (kimiosHome.exists() && kimiosHome.isDirectory()) {
                /*
                    Start Spring loading
                 */
                File springConf = new File(kimiosHome, "conf");
                if (springConf.exists()) {
                    return new String[]{kimiosHome.getAbsolutePath() + "/conf/ctx-kimios.xml"};
                }
            }
        }
        throw new RuntimeException("Kimios Home Not found. Please check kimios.home value, and target directory");
    }

    @Override
    protected Resource getResourceByPath(String path) {
        return new FileSystemResource(path);
    }
}
