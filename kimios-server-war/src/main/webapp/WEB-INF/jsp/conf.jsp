<%@ page import="org.kimios.deployer.core.ConfElement" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%--
~ Kimios - Document Management System Software
~ Copyright (C) 2008-2012  DevLib'
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
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link href="<%=request.getContextPath()%>/web/bootstrap/css/bootstrap.css"
          media="screen" rel="stylesheet" type="text/css">
    <link href="<%=request.getContextPath()%>/web/bootstrap/css/bootstrap-grid.css"
          media="screen" rel="stylesheet" type="text/css">
    <link href="<%=request.getContextPath()%>/web/bootstrap/css/bootstrap-reboot.css"
          media="screen" rel="stylesheet" type="text/css">

    <link href="<%=request.getContextPath()%>/web/css/style.css" media="screen" rel="stylesheet"
          type="text/css">

    <script type="text/javascript" src="<%=request.getContextPath()%>/web/js/jquery-3.3.1.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/web/bootstrap/js/bootstrap.bundle.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/web/js/bootbox.min.js"></script>

    <script type="text/javascript">
        //<![CDATA[

        function testDatabase() {
            $.ajax({
                url: "<%=request.getContextPath()%>/installer?checkdb=1",
                dataType: 'json',
                type: 'post',
                data: {
                    'dbHost': $('#dbHost').val(),
                    'dbPort': $('#dbPort').val(),
                    'dbName': $('#dbName').val(),
                    'jdbc.user': $('#jdbc-user').val(),
                    'jdbc.password': $('#jdbc-password').val(),
                    'jdbc.databasetype': $('#jdbc-databasetype').val(),
                    'dbCreate': $('input[name=dbCreate]:checked').val()
                },
                async: true,
                success: function (json) {
                    if (json.success == false) {
                        bootbox.alert('<span style="color: red; font-weight: bolder;">Database connection error</span></br><br/>' + json.message + '</br><br/><pre style="font-size: .8em;">' + json.stacktrace + '</pre>');
                    } else {
                        bootbox.alert('<span style="color: green; font-weight: bolder;">Database connection success</span><br/><br/>You are now ready to install Kimios.');
                    }
                }
            });
        }

        function launchInstall(event) {
            if (!validForm(Array.from(document.getElementsByClassName('needs-validation'))[0], event)) {
                return;
            }
            document.getElementById('launchButton').style.display = 'none';
            document.getElementById('loader').style.display = 'block';
            $.ajax({
                url: "<%=request.getContextPath()%>/installer?installgo=1",
                dataType: 'json',
                type: 'post',
                data: {
                    'dbHost': $('#dbHost').val(),
                    'dbPort': $('#dbPort').val(),
                    'dbName': $('#dbName').val(),
                    'jdbc.user': $('#jdbc-user').val(),
                    'jdbc.password': $('#jdbc-password').val(),
                    'jdbc.databasetype': $('#jdbc-databasetype').val(),
                    'dms.repository.default.path': $('#dms-repository-default-path').val(),
                    'dms.repository.index.path': $('#dms-repository-index-path').val(),
                    'dms.index.solr.home': $('#dms-index-solr-home').val(),
                    'dbCreate': $('input[name=dbCreate]:checked').val()
                },
                async: true,
                success: function (json) {

                    if (json.success == false) {
                        bootbox.alert('<span style="color: red; font-weight: bolder;">DMS installation error</span><br/><br/>' + json.message + '</br><br/><pre style="font-size: .8em;">' + json.stacktrace + '</pre>');
                    } else {
                        document.getElementById('loader').style.display = 'none';
                        Array.from(document.getElementsByClassName('installation_success')).forEach(function (elem) {
                            elem.style.display = 'block';
                        });
                        setTimeout(function () {
                            window.location = 'http://<%=request.getServerName()%>:<%=(System.getenv("KIMIOS_SERVICE_PORT")!=null ? System.getenv("KIMIOS_SERVICE_PORT") : request.getServerPort())%>/';
                        }, 5000);
                    }
                }
            });
        }

        function validForm(form, event) {
            var valid = form.checkValidity();
            event.preventDefault();
            event.stopPropagation();
            form.classList.add('was-validated');
            return valid;
        }

        $(document).ready(function (event) {
            $("#kimiosInstallForm").submit(function () {
                if (validForm(this, event)) {
                } else {
                    event.preventDefault();
                    event.stopPropagation();
                }
            });
        });

    </script>

    <title>Kimios DMS - Setup</title>
    <link rel="shortcut icon" type="image/png" href="<%=request.getContextPath()%>/images/kimios-favico.ico"/>

</head>
<body style="font-size: 12px">
<%
    List<String> elementKeys = (java.util.List) request.getAttribute("elementKeys");
    Map<String, ConfElement> elementsMap = (Map) request.getAttribute("elementsMap");
    List<String> cstElement = new ArrayList<String>();
    cstElement.add("jdbc.url");
    cstElement.add("jdbc.user");
    cstElement.add("jdbc.password");
    cstElement.add("jdbc.databasetype");
%>
<div class="container">

    <div class="row">
        <div class="offset-2 col-3">
            <img style="height: 100px; width: auto" src="<%=request.getContextPath()%>/images/logo.png"
                 alt="Kimios Setup">
        </div>
        <div class="col-3 offset-1">
            <h3>Kimios DMS - Setup</h3>
        </div>
    </div>

    <div class="row mt-lg-4">
            <form class="needs-validation form col-lg-7" id="kimiosInstallForm" style="float: none; margin: 0 auto;">
                <div class="form-group row" id="databaseType">
                    <label class="col-4 col-form-label-sm" for="jdbc-databasetype">Database type
                    </label>
                    <div class="col-8">
                        <select class="form-control form-control-sm" id="jdbc-databasetype" name="jdbc-databasetype">
                            <option value="postgresql">PostgreSQL</option>
                            <option value="mysql">MySQL</option>
                            <option value="oracle">Oracle</option>
                            <option value="sqlserver">MS SQL Server</option>
                        </select>
                    </div>

                </div>

                <div class="form-group row" id="databaseHost">
                    <label class="col-4 col-form-label-sm" for="dbHost">Database hostname</label>
                    <div class="col-8">
                        <input class="input-xlarge form-control form-control-sm" type="text" id="dbHost" name="dbHost"
                               value="127.0.0.1"
                               placeholder="Database hostname" required>
                        <div class="invalid-feedback">
                            Please provide a valid database hostname.
                        </div>
                    </div>
                </div>

                <div class="form-group row" id="databasePort">
                    <label class="col-4 col-form-label-sm" for="dbPort">Database port</label>
                    <div class="col-8">
                        <div class="row">
                            <div class="col-3">
                                <input class="input-small form-control form-control-sm" type="text" id="dbPort"
                                       name="dbPort"
                                       value="5432" placeholder="Database port" required>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-12">
                                <div class="invalid-feedback">
                                    Please provide a valid database port.
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="form-group row" id="databaseLogin">
                    <label class="col-4 col-form-label-sm" for="jdbc-user">Database username
                    </label>
                    <div class="col-8">
                        <input class="input-xlarge form-control form-control-sm" type="text" id="jdbc-user"
                               name="jdbc-user"
                               value="postgres" placeholder="Database username" required>
                        <div class="invalid-feedback">
                            Please provide a valid database login.
                        </div>
                    </div>
                </div>


                <div class="form-group row" id="databasePassword">
                    <label class="col-4 col-form-label-sm" for="jdbc-password">Database password
                    </label>
                    <div class="col-8">
                        <input class="input-xlarge form-control form-control-sm" type="password" id="jdbc-password"
                               name="jdbc-password"
                               value="postgres" placeholder="Database password" required>
                        <div class="invalid-feedback">
                            Please provide a valid database password.
                        </div>
                    </div>
                </div>

                <div class="form-group row" id="databaseName">
                    <label class="col-4 col-form-label-sm" for="dbName">Database name</label>
                    <div class="col-8">
                        <input class="input-xlarge form-control form-control-sm" type="text" id="dbName" name="dbName"
                               value="kimios"
                               placeholder="Database name" required>
                        <div class="invalid-feedback">
                            Please provide a valid database name.
                        </div>
                    </div>
                </div>

                <div class="form-group row" id="databaseCreation">
                    <label class="col-4 col-form-label-sm">Database creation</label>
                    <div class="col-8">
                        <div class="row">
                            <div class="col-2">
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input" type="radio" id="dbCreate1" name="dbCreate"
                                           value="yes" required>
                                    <label class="form-check-label" for="dbCreate1">Yes</label>
                                </div>
                            </div>
                            <div class="col-2">
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input" type="radio" id="dbCreate2" name="dbCreate"
                                           value="no" required>
                                    <label class="form-check-label" for="dbCreate2">No</label>
                                </div>
                            </div>

                            <div class="col-6">
                                <button type="button" class="btn btn-danger btn-mini"
                                        onclick="testDatabase(); return false;">
                                    Check database settings
                                </button>
                            </div>

                            <div class="invalid-feedback">
                                Please choose if you want Kimios database to be created.
                            </div>
                        </div>
                    </div>
                </div>

                <div class="form-group row" id="repositoryPath">
                    <label class="col-4 col-form-label-sm" for="dms-repository-default-path">Data
                        repository path</label>

                    <div class="col-8">
                        <input class="input-xlarge form-control form-control-sm" type="text"
                               id="dms-repository-default-path"
                               name="dms-repository-default-path"
                               value="${elementsMap['dms.repository.default.path'].defaultValue}"
                               placeholder="Repository path"
                               required>
                        <div class="invalid-feedback">
                            Please provide a valid path for the Kimios repository.
                        </div>
                    </div>
                </div>

                <div class="form-group row" id="solrHome">
                    <label class="col-4 col-form-label-sm" for="dms-index-solr-home">Solr home path</label>
                    <div class="col-8">
                        <input class="input-xlarge form-control form-control-sm" type="text" id="dms-index-solr-home"
                               name="dms-index-solr-home"
                               value="${elementsMap['dms.index.solr.home'].defaultValue}"
                               placeholder="Solr home path" required>
                        <div class="invalid-feedback">
                            Please provide a valid path for SolR home.
                        </div>
                    </div>
                </div>
            </form>
    </div>

    <div class="row text-center">
        <div class="col-3 offset-1">
            <button id="launchButton" type="button" class="btn btn-large btn-primary"
                    onclick="launchInstall(event); return false;" style="width: 100%;">
                <i class="icon-circle-arrow-right icon-white"></i> Install
            </button>
            <div id="loader" class="loader" style="display: none; margin: auto;"></div>
            <div class="installation_success" style="display: none; margin: auto;">
                <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" viewBox="0 0 8 8" fill="#5cd65c">
                    <path d="M6.41 0l-.69.72-2.78 2.78-.81-.78-.72-.72-1.41 1.41.72.72 1.5 1.5.69.72.72-.72 3.5-3.5.72-.72-1.44-1.41z"
                          transform="translate(0 1)"/>
                </svg>
            </div>
        </div>
        <div class="col-8">
            <span class="installation_success" style="display: none;">Installation successful ! You will be redirected in 5 seconds…</span>
        </div>
    </div>
</div>
</div>

</body>
</html>
