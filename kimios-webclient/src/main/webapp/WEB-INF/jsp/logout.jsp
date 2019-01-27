<%@ page import="org.kimios.client.controller.SecurityController" %>
<%@ page import="org.kimios.controller.Controller" %>
<%@ page import="org.kimios.utils.configuration.ConfigurationManager" %>
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
  --%> qq

<%
    String sessionUid = (String)request.getSession().getAttribute("sessionUid");
    if(sessionUid != null){
        SecurityController controller = Controller.getSecurityController();
        controller.endSession(sessionUid);
    }
    request.getSession(false).removeAttribute("session_uid");
    request.getSession(false).removeAttribute("sessionUid");
    request.getSession(false).setMaxInactiveInterval(-1);
    request.getSession(false).invalidate();

    //check CAS !
    if(ConfigurationManager.getValue("client", "sso.cas.enabled") != null &&
            ConfigurationManager.getValue("client", "sso.cas.enabled").equals("true")){
        // logout
        String casLogoutUrl = ConfigurationManager.getValue("client", "sso.cas.url") + "/logout";
        response.sendRedirect(casLogoutUrl);
    } else {
        response.sendRedirect(request.getContextPath() + "/");
    }


%>
