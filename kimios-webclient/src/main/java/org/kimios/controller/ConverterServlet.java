/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
 *
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.controller;

import org.kimios.core.configuration.Config;
import org.kimios.utils.configuration.ConfigurationManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ConverterServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String serverUrl = ConfigurationManager.getValue(Config.DM_SERVER_URL);
        String serviceContext = ConfigurationManager.getValue(Config.DM_SERVICE_CONTEXT) + "/rest/converter/convertDocuments";
        String query = req.getQueryString();
        String rebuiltUrl = serverUrl + serviceContext + "?" + query;

        System.out.println("open connection with rebuilt url: " + rebuiltUrl);
        URLConnection connection = new URL(rebuiltUrl).openConnection();

        resp.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        resp.setHeader("Content-Disposition", connection.getHeaderField("Content-Disposition"));

        int read = -1;
        byte[] data = new byte[4096];
        InputStream input = connection.getInputStream();
        while ((read = input.read(data, 0, data.length)) > 0) {
            resp.getOutputStream().write(data, 0, read);
        }
    }
}
