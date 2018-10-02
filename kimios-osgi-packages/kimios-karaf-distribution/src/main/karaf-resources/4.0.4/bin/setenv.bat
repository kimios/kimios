rem    Licensed to the Apache Software Foundation (ASF) under one or more
rem    contributor license agreements.  See the NOTICE file distributed with
rem    this work for additional information regarding copyright ownership.
rem    The ASF licenses this file to You under the Apache License, Version 2.0
rem    (the "License"); you may not use this file except in compliance with
rem    the License.  You may obtain a copy of the License at
rem
rem       http://www.apache.org/licenses/LICENSE-2.0
rem
rem    Unless required by applicable law or agreed to in writing, software
rem    distributed under the License is distributed on an "AS IS" BASIS,
rem    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem    See the License for the specific language governing permissions and
rem    limitations under the License.
rem

rem
rem general settings which should be applied for all scripts go here; please keep
rem in mind that it is possible that scripts might be executed more than once, e.g.
rem in example of the start script where the start script is executed first and the
rem karaf script afterwards.
rem

rem
rem The following section shows the possible configuration options for the default
rem karaf scripts
rem
rem Location of Java installation
rem SET JAVA_HOME
rem Minimum memory for the JVM
rem SET JAVA_MIN_MEM
SET JAVA_MAX_MEM=2048m
IF "%KMS_MAX_MEMORY%"=="" (SET JAVA_MAX_MEM=%KMS_MAX_MEMORY%)
IF NOT "%FABRIC8_CM_BRIDGE_ENABLED%" == "" (SET FABRIC8_CM_BRIDGE_ENABLED=true)
IF "%FABRIC8_CM_BRIDGE_ENABLED%" == "" (SET FABRIC8_CM_BRIDGE_ENABLED=false)

rem Maximum perm memory for the JVM
SET JAVA_MAX_PERM_MEM=512m

rem SET EXTRA_JAVA_OPTS rem Additional JVM options
rem SET KARAF_HOME rem Karaf home folder
rem SET KARAF_DATA rem Karaf data folder
rem SET KARAF_BASE rem Karaf base folder
rem SET KARAF_ETC  rem Karaf etc  folder

rem SET KIMIOS_JDBC_DRIVER=
rem SET KIMIOS_JDBC_URL=
rem SET KIMIOS_JDBC_USER=
rem SET KIMIOS_JDBC_PASSWORD=
rem SET KIMIOS_REPO_PATH=
rem SET KIMIOS_REPO_TMP_PATH=
rem SET KIMIOS_SOLR_HOME=
rem SET KIMIOS_SOLR_SERVER_URL=

SET KARAF_OPTS=-Dkimios.db.url="%KIMIOS_JDBC_URL%" -Dkimios.db.user="%KIMIOS_JDBC_USER%" -Dkimios.db.password="%KIMIOS_JDBC_PASSWORD%" -Dkimios.repo.default.path="%KIMIOS_REPO_PATH%" -Dkimios.repo.tmp.path="%KIMIOS_REPO_TMP_PATH%" -Dkimios.index.solr.home="%KIMIOS_SOLR_HOME%" -Dkimios.index.solr.server.url="%KIMIOS_SOLR_SERVER_URL%" -Djavax.xml.transform.TransformerFactory="org.apache.xalan.processor.TransformerFactoryImpl"
rem SET KARAF_DEBUG rem Enable debug mode
rem SET KARAF_REDIRECT rem Enable/set the std/err redirection when using bin/start


