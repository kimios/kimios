<%@page import="org.kimios.client.controller.SecurityController" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
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
    WebApplicationContext wac =
            WebApplicationContextUtils.getRequiredWebApplicationContext(this.getServletConfig().getServletContext());
    if (securityController == null) {
        securityController = (SecurityController) wac.getBean("securityController");
    }
    // check and redirect to main page if session is alive
    String sessionUid = (String) request.getSession().getAttribute("sessionUid");
    if (sessionUid != null && securityController.isSessionAlive(sessionUid)) {
        response.sendRedirect(request.getContextPath() + "/logged.jsp");
        return;
    }
%>
<%
    /*
    // SSL
    System.setProperty("javax.net.ssl.keyStore", "/path/to/client.ks");
    System.setProperty("javax.net.ssl.keyStorePassword", "secret");
    System.setProperty("javax.net.ssl.keyStoreType", "JKS");
    System.setProperty("javax.net.ssl.trustStore", "/path/to/client.ts");
    System.setProperty("javax.net.ssl.trustStorePassword", "secret");
    System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
    */
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="icon" type="image/png" href="<%=request.getContextPath()%>/images/favico.png"/>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/ext/resources/css/ext-all.css"/>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/ext/resources/css/xtheme-gray.css"/>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/client-dm-viewer.css"/>
    <title>kimios Web Client</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/ext/ext-all.js"></script>
    <%@include file="/WEB-INF/jsp/core/init.jsp" %>
    <script type="text/javascript" language="javascript"
            src="<%=request.getContextPath()%>/js/client/main/kimios.js"></script>
    <script type="text/javascript" language="javascript"
            src="<%=request.getContextPath()%>/js/client/main/Cookies.js"></script>
    <script type="text/javascript" language="javascript"
            src="<%=request.getContextPath()%>/js/client/main/record.js"></script>
    <script type="text/javascript" language="javascript"
            src="<%=request.getContextPath()%>/js/client/main/store.js"></script>
    <script type="text/javascript" language="javascript"
            src="<%=request.getContextPath()%>/js/client/i18n/Internationalization.js"></script>
    <script type="text/javascript" language="javascript"
            src="<%=request.getContextPath()%>/js/client/main/MessageBox.js"></script>
    <script type="text/javascript" language="javascript"
            src="<%=request.getContextPath()%>/js/client/form/AuthenticationSourceField.js"></script>
    <script type="text/javascript" language="javascript"
            src="<%=request.getContextPath()%>/js/client/main/FormPanel.js"></script>
    <script type="text/javascript" language="javascript"
            src="<%=request.getContextPath()%>/js/client/security/login.js"></script>
    <style rel="stylesheet" type="text/css">
        body {
            background-image: url(<%=request.getContextPath()%>/images/explorer_background.png);
            background-repeat: no-repeat;
            background-attachment: fixed;
            background-position: right bottom;
        }
    </style>
</head>
<body>
<div align="center"><br/><br/><img src="images/logo.png" style="height: 50px"/></div>
</body>
</html>


