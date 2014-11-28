<%@ page import="java.util.Properties" %>
<%@ page import="java.io.FileInputStream" %>
<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
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
    String protocol = request.getProtocol().toLowerCase().startsWith("https") ? "https" : "http";
    String fullServerUrl = protocol + "://" + request.getServerName() + (request.getServerPort() != 80 ? ":" + request.getServerPort() : "");
%>

<script type="text/javascript" language="javascript">
    var srcContextPath = '<%=request.getContextPath()%>';
    var fullServerUrl = '<%=fullServerUrl%>';
    Ext.BLANK_IMAGE_URL = '<%=request.getContextPath()%>/css/ext/images/default/s.gif';
    var contextPath = '<%=request.getContextPath()%>/Main';
    var appNameCtx = '<%=request.getContextPath().length() > 1 ? request.getContextPath().substring(1) : ""%>';
    function getLoginUrl() {
        return contextPath + '?servlet=Security&action=login';
    }
    function getBackEndUrl(servlet) {
        return contextPath + '?servlet=' + servlet;
    }




    /*
        Load configuration
     */
    var clientConfig = {}

    <%
        Properties properties = new Properties();
        properties.load(new FileInputStream(System.getProperty("kimios.home") + "/" + this.getServletConfig().getServletContext().getInitParameter("kimios.app.name") + "/conf/kimios.properties"));

        for(String p: properties.stringPropertyNames()){
            String pName = p.replaceAll("\\.", "");
            %>
                clientConfig.<%=pName%> = '<%=properties.get(p).toString()%>';
            <%
        }
    %>
    var bonitaEnabled = clientConfig.bonitaenabled === 'true';

</script>
