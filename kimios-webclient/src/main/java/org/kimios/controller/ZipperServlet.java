/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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
package org.kimios.controller;

import org.kimios.core.configuration.Config;
import org.kimios.utils.configuration.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ZipperServlet extends HttpServlet {

    private static Logger logger = LoggerFactory.getLogger(ZipperServlet.class);


    private static String SERVICE_URL = "/rest/zip/make";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String serverUrl = ConfigurationManager.getValue("client", Config.DM_SERVER_URL);

            String serviceContext = ConfigurationManager.getValue("client", Config.DM_SERVICE_CONTEXT) + SERVICE_URL;
            String query = req.getQueryString();

            String rebuiltUrl = serverUrl;
            rebuiltUrl += serviceContext + "?" + query;

            logger.debug("open connection with rebuilt url: " + rebuiltUrl);
            URLConnection connection = null;
            try {
                connection = new URL(rebuiltUrl).openConnection();
            } catch (Exception e) {
                logger.debug(e.getLocalizedMessage());
                logger.debug(e.getCause().getMessage());
            }
            String contentDisposition = connection.getHeaderField("Content-Disposition");
            resp.setContentType(connection.getContentType());
        /*if(inline){
            resp.setHeader("Content-Disposition", contentDisposition.replace("attachment;", "inline;"));
        }else*/
            resp.setHeader("Content-Disposition", contentDisposition);

            int read = -1;
            byte[] data = new byte[4096];
            InputStream input = connection.getInputStream();
            while ((read = input.read(data, 0, data.length)) > 0) {
                resp.getOutputStream().write(data, 0, read);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
