<%@page import="org.kimios.client.controller.DocumentController" %>
<%@page import="org.kimios.client.controller.FolderController" %>
<%@page import="org.kimios.client.controller.SecurityController" %>
<%@page import="org.kimios.client.controller.WorkspaceController" %>
<%@page import="org.kimios.kernel.ws.pojo.Document" %>
<%@page import="org.kimios.kernel.ws.pojo.Folder" %>
<%@ page import="org.kimios.kernel.ws.pojo.Workspace" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
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

<%!
    private String getNormalizedExtension(String extension) {
        extension = extension.toLowerCase();
        if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("png")
                || extension.equalsIgnoreCase("gif") || extension.equalsIgnoreCase("psd")
                || extension.equalsIgnoreCase("tif") || extension.equalsIgnoreCase("tiff")
                || extension.equalsIgnoreCase("bmp") || extension.equalsIgnoreCase("ico"))
            return "img";

        if (extension.equalsIgnoreCase("odt"))
            return "ooo";
        if (extension.equalsIgnoreCase("docx"))
            return "doc";
        if (extension.equalsIgnoreCase("xlsx"))
            return "xls";
        if (extension.equalsIgnoreCase("pptx"))
            return "ppt";

        if (extension.equalsIgnoreCase("zip") || extension.equalsIgnoreCase("rar") || extension.equalsIgnoreCase("ace")
                || extension.equalsIgnoreCase("gz") || extension.equalsIgnoreCase("war")
                || extension.equalsIgnoreCase("tgz") || extension.equalsIgnoreCase("bz2"))
            return "zip";

        //if (extension.equalsIgnoreCase("mp3" || extension.equalsIgnoreCase("ogg"){
        //  return "music";
        //}

        //if (extension.equalsIgnoreCase("mpg" || extension.equalsIgnoreCase("mpeg" || extension.equalsIgnoreCase("avi" || extension.equalsIgnoreCase("mov"){
        //  return "video";
        //}

        if (extension.equalsIgnoreCase("pdf")
                || extension.equalsIgnoreCase("doc")
                || extension.equalsIgnoreCase("xls")
                || extension.equalsIgnoreCase("vsd")
                || extension.equalsIgnoreCase("mpp")
                || extension.equalsIgnoreCase("ppt")
                || extension.equalsIgnoreCase("html")
                || extension.equalsIgnoreCase("htm")
                || extension.equalsIgnoreCase("txt"))
            return extension;


        return "unknown";
    }
%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%
    SecurityController securityController = null;
    WorkspaceController workspaceController = null;
    FolderController folderController = null;
    DocumentController documentController = null;
    WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(this.getServletConfig().getServletContext());
    if (securityController == null) {
        securityController = (SecurityController) wac.getBean("securityController");
        workspaceController = (WorkspaceController) wac.getBean("workspaceController");
        folderController = (FolderController) wac.getBean("folderController");
        documentController = (DocumentController) wac.getBean("documentController");
    }
    String sessionUid = (String) request.getSession().getAttribute("sessionUid");
    if (sessionUid == null || !securityController.isSessionAlive(sessionUid)) {
        response.sendRedirect(request.getContextPath() + "/mobile/index.jsp");
        return;
    }
    long uid = -1;
    Object dm = null;
    try {
        uid = Long.parseLong(request.getParameter("uid"));
        dm = null;
        if (uid > 0) {
            try {
                dm = workspaceController.getWorkspace(sessionUid, uid);
            } catch (Exception ex) {
                System.out.println(" >>> workspace not found " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        if (dm == null)
            try {
                dm = folderController.getFolder(sessionUid, uid);
            } catch (Exception ex) {
                System.out.println(" >>> folder not found " + ex.getMessage());
                ex.printStackTrace();
            }
    } catch (Exception e) {
        e.printStackTrace();
    }
    String path = null;
    long parentUid = -1;
    if (dm instanceof Workspace) {
        path = ((Workspace) dm).getPath();
        parentUid = -1;
    } else if (dm instanceof Folder) {
        path = ((Folder) dm).getPath();
        parentUid = ((Folder) dm).getParentUid();
    } else
        path = "/";
%>
<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" href="http://code.jquery.com/mobile/1.0a2/jquery.mobile-1.0a2.min.css"/>
    <script src="http://code.jquery.com/jquery-1.4.4.min.js"></script>
    <script src="http://code.jquery.com/mobile/1.0a2/jquery.mobile-1.0a2.min.js"></script>
    <title>kimios Mobile</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <style type="text/css">
        div.error-div {
            color: red;
            padding: 1em;
        }
    </style>
    <script type="text/javascript">
        $(document).ready(function() {


        });

    </script>
</head>
<body>
<div data-role="page">
    <div data-role="header">
        <h1><%=path %>
        </h1>
        <a href="<%=request.getContextPath() + "/mobile/logout.jsp" %>" data-icon="delete" class="ui-btn-right"
           data-theme="c">Logout</a>

    </div>

    <div data-role="content">
        <ul data-role="listview">
            <%
                try {

                    int count = 0;
                    if (dm == null) {
                        Workspace[] ww = workspaceController.getWorkspaces(sessionUid);
                        for (Workspace w : ww) {
                            out.write("<li><img src=\"" + request.getContextPath() + "/images/icons/16x16/database.png\" alt=\"" + w.getName() + "\" class=\"ui-li-icon\" /><a href=\"" + request.getContextPath() + "/mobile/logged.jsp?uid=" + w.getUid() + "\">" + w.getName() + "</a></li>");
                            count++;
                        }
                    } else if (dm instanceof Workspace) {
                        Folder[] ff = folderController.getFolders(sessionUid, ((Workspace) dm).getUid(), 1);
                        for (Folder f : ff) {
                            out.write("<li><img src=\"" + request.getContextPath() + "/images/icons/16x16/folder.png\" alt=\"" + f.getName() + "\" class=\"ui-li-icon\" /><a href=\"" + request.getContextPath() + "/mobile/logged.jsp?uid=" + f.getUid() + "\">" + f.getName() + "</a></li>");
                            count++;
                        }
                    } else if (dm instanceof Folder) {
                        Folder[] ff = folderController.getFolders(sessionUid, ((Folder) dm).getUid(), 2);
                        for (Folder f : ff) {
                            out.write("<li><img src=\"" + request.getContextPath() + "/images/icons/16x16/folder.png\" alt=\"" + f.getName() + "\" class=\"ui-li-icon\" /><a href=\"" + request.getContextPath() + "/mobile/logged.jsp?uid=" + f.getUid() + "\">" + f.getName() + "</a></li>");
                            count++;
                        }
                        Document[] dd = documentController.getDocuments(sessionUid, ((Folder) dm).getUid());
                        for (Document d : dd) {
                            out.write("<li><img src=\"" + request.getContextPath() + "/images/icons/16x16/" + getNormalizedExtension(d.getExtension()) + ".png\" alt=\"" + d.getName() + "." + d.getExtension() + "\" class=\"ui-li-icon\" /><a href=\"" + request.getContextPath() + "/mobile/download.jsp?uid=" + d.getUid() + "\" rel=\"external\" target=\"_blank\">" + d.getName() + "." + d.getExtension() + "</a></li>");
                            count++;
                        }
                    }
                    if(count == 0){
                        out.write("<li>No Items</li>");
                    }
            %>
            <%
                } catch (Exception ex) {

                }
            %>
        </ul>
    </div>
</div>
</body>
</html>