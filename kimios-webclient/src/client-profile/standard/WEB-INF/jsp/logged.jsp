<%@ page import="org.kimios.client.controller.SecurityController" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
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
    String sessionUid = null;
    org.kimios.kernel.ws.pojo.User u = null;
    SecurityController securityController = null;
    WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(this.getServletConfig().getServletContext());
    if (securityController == null) {
        securityController = (SecurityController) wac.getBean("securityController");
    }

    try {
        sessionUid = (String) request.getSession().getAttribute("sessionUid");
        if (sessionUid == null)
            throw new java.lang.Exception("sessionUid == null");
        u = securityController.getUser(sessionUid);
    } catch (Exception e) {
        response.sendRedirect(request.getContextPath() + "/");
        return;
    }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Kimios Web Explorer</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="icon" type="image/png" href="<%=request.getContextPath()%>/images/kimios-favico.png"/>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/ext/resources/css/ext-all.css"/>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/ext/resources/css/kimios-theme.css"/>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/client-dm-viewer.css"/>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/StatusBar.css"/>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/Spinner.css"/>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/notifier.css"/>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/GroupTab.css"/>
    <link rel="stylesheet" type="text/css"
          href="<%=request.getContextPath()%>/js/ext/resources/css/kimios-theme-grouptab.css"/>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/icons"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/ext/ext-all.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/ux/FileUploadField.js"></script>
    <%@include file="/WEB-INF/jsp/core/init.jsp" %>
    <script type="text/javascript" language="javascript">
        var sessionUid = '<%=sessionUid%>';
        RightsRecord = Ext.data.Record.create([
            {name: 'canCreateWorkspace', type: 'boolean'},
            {name: 'isAdmin', type: 'boolean'},
            {name: 'isStudioUser', type: 'boolean'}
        ]);
        var currentName = '<%=u.getName() != null ? u.getName().replaceAll("'", "\\\\'") : ""%>';
        var currentUser = '<%=u.getUid().replaceAll("'", "\\\\'")%>';
        var currentSource = '<%=u.getSource().replaceAll("'", "\\\\'")%>';
        var currentMail = '<%=(u.getMail() != null ? u.getMail().replaceAll("'", "\\\\'") : "")%>';
    </script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/ux/RowExpander.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/ux/ColumnTree.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/ux/StatusBar.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/ux/CheckColumn.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/ux/Spinner.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/ux/SpinnerField.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/ux/GroupTabPanel.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/ux/GroupTab.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/ux/SearchField.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/main/kimios.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/main/Cookies.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/main/record.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/main/store.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/main/request.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/main/LoadMask.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/main/ContextMenu.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/main/MessageBox.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/main/DmsSimpleUpload.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/main/DMEntityPojo.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/main/Notifier.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/main/UploaderWindow.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/main/FormPanel.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/main/MyAccountPanel.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/menu/ToolsMenu.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/menu/LanguageMenu.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/security/Rights.js"></script>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/js/client/picker/SecurityEntityPicker.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/picker/DMEntityPicker.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/picker/WorkflowPicker.js"></script>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/js/client/form/AuthenticationSourceField.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/form/ActionTypeField.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/form/SecurityEntityField.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/form/DocumentTypeField.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/form/DMEntityField.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/form/MetaFeedField.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/form/WorkflowField.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/form/WorkflowStatusField.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/util/DMEntityTree.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/util/IconHelper.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/util/ImageViewer.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/explorer/Viewport.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/explorer/Toolbar.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/explorer/ExplorerPanel.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/explorer/DMEntityGridPanel.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/explorer/CommentsPanel.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/explorer/BreadcrumbToolbar.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/explorer/BookmarksPanel.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/explorer/SearchQueryPanel.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/explorer/RecentItemsPanel.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/search/SearchToolbar.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/search/SearchButton.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/search/SearchField.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/search/AdvancedSearchPanel.js"></script>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/js/client/properties/PropertiesWindow.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/properties/PropertiesPanel.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/properties/DMEntityPanel.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/properties/MetaDataPanel.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/properties/VersionsPanel.js"></script>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/js/client/properties/RelatedDocumentsPanel.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/properties/HistoryPanel.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/properties/WorkflowPanel.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/properties/CommentsPanel.js"></script>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/js/client/properties/SecurityEntityPanel.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/tasks/TasksPanel.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/studio/studio.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/studio/document-types.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/studio/meta-feeds.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/studio/workflows.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/admin/admin.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/admin/domains.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/admin/special-roles.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/admin/special-tasks.js"></script>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/js/client/reporting/GenericReportPanel.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/reporting/reporting.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/reporting/generic-report.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/i18n/Internationalization.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/client/main/run.js"></script>
    <script type="text/javascript">
        var defaultTheme = 'minimalist-green';
        new kimios.util.IconHelper.iconThemeSwitcher(defaultTheme);
    </script>
</head>
<body>
<div id="panel"></div>
</body>
</html>

