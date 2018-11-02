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

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.File;

/**
 *
 *
 */
public class ResourceProvider {
    private static Logger log = LoggerFactory.getLogger(ResourceProvider.class);

    private static String[] jsp = new String[]{"index.jsp", "ctrl.jsp", "conf.jsp"};

    public static boolean init(ServletContext servletContext) {

        try {
            /*
               Copy resources
            */
            String destinationDirectory = "/WEB-INF/jsp";
            File webResDir = new File(
                    servletContext.getRealPath(destinationDirectory)
            );

            File rootDir = new File(
                    servletContext.getRealPath("/")
            );

            if (!webResDir.exists()) {
                webResDir.mkdirs();
            }
            for (String t : jsp) {
                log.info("Copying JSP file for installer: " + t);
                if (t.equals("index.jsp") || t.equals("ctrl.jsp")) {
                    if (new File(rootDir, t).exists()) {
                        new File(rootDir, t).delete();
                    }
                    FileUtils.copyInputStreamToFile(ResourceProvider.class.getResourceAsStream("/jsp/" + t),
                            new File(rootDir, t));
                } else {
                    FileUtils.copyInputStreamToFile(ResourceProvider.class.getResourceAsStream("/jsp/" + t),
                            new File(webResDir, t));
                }
            }
            return true;
        } catch (Exception e) {
            log.error("Copying resources error", e);
            return false;
        }
    }

    public static void cleanResources(ServletContext ctx) {
        String destinationDirectory = "/WEB-INF/jsp";
        File webResDir = new File(
                ctx.getRealPath(destinationDirectory)
        );

        try {
            FileUtils.deleteDirectory(webResDir);
        } catch (Exception e) {

        }
    }
}
