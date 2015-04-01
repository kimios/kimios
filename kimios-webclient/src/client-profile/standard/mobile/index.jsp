<%@page import="org.kimios.client.controller.SecurityController" %>
<%@page import="org.kimios.kernel.ws.pojo.AuthenticationSource" %>
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
    SecurityController securityController = Controller.getSecurityController();
    String sessionUid = (String) request.getSession().getAttribute("sessionUid");
    if (sessionUid != null && securityController.isSessionAlive(sessionUid)) {
        response.sendRedirect(request.getContextPath() + "/mobile/logged.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
  <link rel="icon" type="image/png" href="<%=request.getContextPath()%>/images/kimios-favico.png"/>
  <link rel="stylesheet" href="http://code.jquery.com/mobile/1.0a2/jquery.mobile-1.0a2.min.css"/>
    <script src="http://code.jquery.com/jquery-1.4.4.min.js"></script>
    <script src="http://code.jquery.com/mobile/1.0a2/jquery.mobile-1.0a2.min.js"></script>
    <title>Kimios - Mobile Browser</title>
    <style type="text/css">
        div.error-div {
            color: red;
            padding: 1em;
        }
    </style>
    <script type="text/javascript">
        $(document).ready(function() {
            $('#form1').submit(function() {

                var msg = "";
                if ($("#login").val() == "")
                    msg += "You must provide a login.<br />";
                if ($("#password").val() == "")
                    msg += "You must provide a password.<br />";
                if (msg != "") {
                    $("div.error-div").html(msg).show().fadeOut(3000);
                    return false;
                }
                else
                    return true;
            });
        });

    </script>
</head>

<body>
<div data-role="page">
    <div data-role="header">
        <h1>kimios Mobile</h1>
    </div>
    <div class="error-div"><%
        if (request.getParameter("msg") != null && request.getParameter("msg").equals("loginfailed"))
            out.write("Login failed!");
    %></div>
    <form id="form1" method="post" action="<%=request.getContextPath()%>/mobile/login.jsp">
        <fieldset>
            <div data-role="fieldcontain" style="padding:1em">
                <label for="login">Login:</label>
                <input type="text" name="login" id="login" value=""/>
                <label for="password">Password:</label>
                <input type="password" name="password" id="password" value=""/>
                <label for="source" class="select">Domain:</label>

                <select name="source" id="source"><%
                    AuthenticationSource[] sources = securityController.getAuthenticationSources();
                    for (AuthenticationSource source : sources) {
                %>
                    <option value="<%=source.getName()%>"><%=source.getName()%>
                    </option>
                    <%
                        } %>
                </select>
            </div>
        </fieldset>

        <button type="submit" data-theme="b">Submit</button>

    </form>
</div>
</body>
</html>
