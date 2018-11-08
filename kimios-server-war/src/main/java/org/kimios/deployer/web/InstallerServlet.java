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

import org.kimios.deployer.core.InstallProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InstallerServlet extends HttpServlet {


    private static Logger logger = LoggerFactory.getLogger(InstallerServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        if (!DeploymentManager.isKimiosDeployed(this.getServletConfig().getServletContext())) {


            boolean createDb = false;
            if (request.getParameter("checkdb") != null) {
                String dbHost = request.getParameter("dbHost");
                String dbPort = request.getParameter("dbPort");
                String dbName = request.getParameter("dbName");
                String dbLogin = request.getParameter("jdbc.user");
                String dbPassword = request.getParameter("jdbc.password");

                createDb = request.getParameter("dbCreate") != null &&
                        request.getParameter("dbCreate").equals("yes");

                String dbType = request.getParameter("jdbc.databasetype");
                InstallProcessor proc = new InstallProcessor();
                proc.init();
                try {
                    proc.checkDatabase(dbType, dbHost, dbPort, dbLogin, dbPassword, dbName, createDb);
                    request.setAttribute("success", true);
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("success", false);
                    request.setAttribute("message", e.getMessage());
                    StringBuilder stacktrace = new StringBuilder();
                    for (StackTraceElement stack : e.getStackTrace()) {
                        stacktrace.append(stack.toString()).append("<br/>");
                    }
                    request.setAttribute("stacktrace", stacktrace.toString());
                }
                request.getRequestDispatcher("ctrl.jsp").forward(request, response);
            }

            if (request.getParameter("installer") != null) {
                try {
                    new WebDeployerViewGenerator().generate(request, response);
                } catch (Exception ex) {
                    throw new IOException(ex);
                }
                return;
            }

            if (request.getParameter("installgo") != null) {
                InstallProcessor proc = new InstallProcessor();
                proc.init();

                String dbHost = request.getParameter("dbHost");
                String dbPort = request.getParameter("dbPort");
                String dbName = request.getParameter("dbName");
                String dbLogin = request.getParameter("jdbc.user");
                String dbPassword = request.getParameter("jdbc.password");
                createDb = request.getParameter("dbCreate") != null &&
                        request.getParameter("dbCreate").equals("yes");
                String dbType = request.getParameter("jdbc.databasetype");

                try {
                    if (createDb)
                        proc.createDatabase(dbType, dbHost, dbPort, dbLogin, dbPassword, dbName, createDb);

                    proc.checkDatabase(dbType, dbHost, dbPort, dbLogin, dbPassword, dbName, createDb);

                    proc.createPath(request.getParameter("dms.repository.default.path"));

                    Map<String, String> mParam = new HashMap<String, String>();

                    mParam.putAll(request.getParameterMap());

                    proc.createPath("kimios-tmp");

                    mParam.put("dms.repository.tmp.path", "kimios-tmp");

                    // if Kimios Home does not exist, let's create it
                    File kimiosServerConfFile = new File(proc.getKimiosHome() + "/server/conf");
                    if (! kimiosServerConfFile.exists()) {
                        if (! kimiosServerConfFile.mkdirs()) {
                            throw new Exception("Kimios Home server conf directory does not exist and cannot be created");
                        }
                    }

                    proc.generateServerPropertiesFile(mParam,
                            proc.getKimiosHome() + "/server/conf/kimios.properties");
                    proc.loadSpringContext(this.getServletConfig().getServletContext());
                    //global restart after first init
                    /*proc.reloadConfiguration(
                            this.getServletConfig().getServletContext().getRealPath("/WEB-INF/web.xml")
                    );*/
                    proc.tomcatRestart();
                    request.setAttribute("success", true);
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("success", false);
                    request.setAttribute("message", e.getMessage());
                    StringBuilder stacktrace = new StringBuilder();
                    for (StackTraceElement stack : e.getStackTrace()) {
                        stacktrace.append(stack.toString()).append("<br/>");
                    }
                    request.setAttribute("stacktrace", stacktrace.toString());
                }
                request.getRequestDispatcher("ctrl.jsp").forward(request, response);
            }
        } else {
            //redirect to web client, or webservices listing...
            request.getRequestDispatcher("/").forward(request, response);
        }

    }
}
