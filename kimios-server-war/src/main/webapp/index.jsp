<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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

<html>

<head>
    <title>Kimios - Open Source Document Management System</title>
    <link rel="icon" type="image/png" href="<%=request.getContextPath()%>/images/kimios-favico.png"/>
    <style type="text/css">
        * {
            margin: 0;
            padding: 0;
        }

        html, body {
            height: 100%;
        }

        iframe {
            width: 100%;
            height: 100%;
            border: none;
            background-color: red;
            font-family: arial !important;
        }
    </style>
</head>

<body>

<img alt="Kimios - Open Source Document Management System"
     src="<%=request.getContextPath()%>/images/logo.png"/>

<br/>

<iframe id="iframe" class="iframe"
        src="<%=request.getContextPath()%>/services"></iframe>

</body>
</html>
