@echo off

set JAVA_HOME=C:\Program Files\Java\jdk1.8.0
rem Tomcat Connector Listening Port
set KIMIOS_SERVICE_PORT=9999

rem Kimios SSL Settings
set KIMIOS_SERVICE_SSL_PORT=10000
set KIMIOS_JKS=kimios.jks
set KIMIOS_JKS_PWD=kimios

rem Other Tomcat Setting
set KIMIOS_AJP_PORT=8009
set KIMIOS_SERVER_SHTPORT=8005


rem Kimios Server WebApp Name
set KIMIOS_SERVER_APPNAME=kimios



set KIMIOS_HOME=%CD%/kimios_home

echo %KIMIOS_HOME%

set KIMIOS_SSL_OPTS=-Dtc.ajp=%KIMIOS_AJP_PORT% -Dtc.shtport=%KIMIOS_SERVER_SHTPORT% -Dkimios.service.ssl=%KIMIOS_SERVICE_SSL_PORT%  -Dkimios.keystore.file=%KIMIOS_JKS% -Dkimios.keystore.pass=%KIMIOS_JKS_PWD%
set KIMIOS_APP_OPTS=-Dkimios.service.port=%KIMIOS_SERVICE_PORT% -Dkimios.service.appname=%KIMIOS_SERVER_APPNAME% -Dkimios.home=%KIMIOS_HOME%


echo %KIMIOS_SSL_OPTS%
echo %KIMIOS_APP_OPTS%

set JAVA_OPTS=-Xms256m -Xmx1024m %KIMIOS_SSL_OPTS% %KIMIOS_APP_OPTS%
set JAVA_OPTS=%$JAVA_OPTS% -Dlogback.configurationFile=kimios_home/server/conf/logback.xml

echo %JAVA_OPTS%
set KIMIOS_PID_FILE=kimios.pid


if ""%1"" == ""start"" goto doStart
if ""%1"" == ""stop"" goto doStop

echo Usage:  kimios ( commands ... )
echo commands:
echo   start             Start Kimios App Server
echo   stop		 Stop Kimios App Server
goto end


:doStart
echo Starting Kimios ...
bin/startup.bat
goto end

:doStop
echo Stopping Kimios
rem Here we can't use tomcat pid file (not available under windows operating system)
bin/shutdown.bat

:end