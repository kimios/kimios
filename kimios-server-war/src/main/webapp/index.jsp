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
    <link rel="shortcut icon" type="image/png" href="<%=request.getContextPath()%>/images/kimios-favico.ico"/>
    <style type="text/css">
        #servicesList {
            padding: 16px 16px 16px 16px;
            border: 1px solid #dddddd;
            background-color: #eeeeee;
        }

        #servicesList * {
            font-size: 12px;
            font-family: arial, helvetica;
            border: none;
        }

        #servicesList .heading {
            font-size: 14px;
            font-weight: bolder;
            color: #333;
            text-decoration: underline;
        }

        #servicesList .porttypename {
            font-size: 12px;
            font-weight: bold;
        }
    </style>
    <script type="text/javascript">
        var http = false;
        if (navigator.appName == "Microsoft Internet Explorer") {
            http = new ActiveXObject("Microsoft.XMLHTTP");
        } else {
            http = new XMLHttpRequest();
        }
        function loadServicesList() {
            http.abort();
            http.open("GET", "<%=request.getContextPath()%>/services", true);
            http.onreadystatechange = function () {
                if (http.readyState == 4) {
                    document.getElementById('servicesList').innerHTML = http.responseText;
                }
            }
            http.send(null);
        }
        //        var applyStyle = function () {
        //            var iframe = document.getElementById('iframe');
        //            var doc = iframe.contentWindow || iframe.contentDocument;
        //            if (doc.document) {
        //                doc = doc.document;
        //            }
        //            doc.body.style.fontSize = "12px";
        //            doc.body.style.fontFamily = "Verdana, Tahoma, Arial";
        //
        //        }
    </script>
</head>

<body onload="loadServicesList()">

<div align="center">

    <img alt="Kimios - Open Source Document Management System"
         src="images/logo.png" style="margin: 10px 0 20px 0; height: 40px;border:none;"/>

</div>

<div id="servicesList"></div>

</body>
</html>
