<%@page import="org.kimios.client.controller.SecurityController" %>
<%@page import="org.kimios.controller.DocumentVersionActionHandler" %>
<%@page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.kimios.controller.Controller" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%--
  ~ Kimios - Document Management System Software
  ~ Copyright (C) 2012-2013  DevLib'
  ~
  ~ This program is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU Affero General Public License as
  ~ published by the Free Software Foundation, either version 2 of the
  ~ License, or (at your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU Affero General Public License for more details.
  ~ You should have received a copy of the GNU Affero General Public License
  ~ along with this program.  If not, see <http://www.gnu.org/licenses/>.
  --%>

<%
    SecurityController securityController = null;
    if (securityController == null) {
        securityController = Controller.getSecurityController();
    }


    String sessionUid = (String) request.getSession().getAttribute("sessionUid");
    if (sessionUid == null || !securityController.isSessionAlive(sessionUid)) {
        response.sendRedirect(request.getContextPath() + "/mobile/index.jsp");
        return;
    }
    long uid = -1;
    try {
        uid = Long.parseLong(request.getParameter("uid"));
    } catch (Exception ex) {
    }
    if (uid <= 0)
        return;
    else {
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "GetLastVersion");
        params.put("uid", uid + "");
        response.reset();
        DocumentVersionActionHandler dvah = new DocumentVersionActionHandler(params, request, response);
        dvah.setSessionUid(sessionUid);
        dvah.execute();
    }
%>
