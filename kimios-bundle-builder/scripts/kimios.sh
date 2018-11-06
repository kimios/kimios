#!/bin/sh

# Tomcat Connector Listening Port
KIMIOS_SERVICE_PORT=9999


#Kimios SSL Settings
KIMIOS_SERVICE_SSL_PORT=10000
KIMIOS_JKS=kimios.jks
KIMIOS_JKS_PWD=kimios

#Other Tomcat Setting
KIMIOS_AJP_PORT=8009
KIMIOS_SERVER_SHTPORT=8005


# Kimios Server WebApp Name
KIMIOS_SERVER_APPNAME=kimios

export KIMIOS_SERVICE_PORT
export KIMIOS_SERVER_APPNAME
export KIMIOS_AJP_PORT
export KIMIOS_SERVER_SHTPORT

KIMIOS_HOME=`pwd`/kimios_home




KIMIOS_SSL_OPTS=" -Dtc.ajp=$KIMIOS_AJP_PORT -Dtc.shtport=$KIMIOS_SERVER_SHTPORT -Dkimios.service.ssl=$KIMIOS_SERVICE_SSL_PORT  -Dkimios.keystore.file=$KIMIOS_JKS -Dkimios.keystore.pass=$KIMIOS_JKS_PWD"
KIMIOS_APP_OPTS="-Dkimios.service.port=$KIMIOS_SERVICE_PORT -Dkimios.service.appname=$KIMIOS_SERVER_APPNAME -Dkimios.home=$KIMIOS_HOME"


JAVA_OPTS="-Xms256m -Xmx1024m $KIMIOS_APP_OPTS $KIMIOS_SSL_OPTS"
JAVA_OPTS="$JAVA_OPTS -Dlogback.configurationFile=kimios_home/server/conf/logback.xml"

KIMIOS_PID_FILE=kimios.pid

export JAVA_OPTS
export KIMIOS_PID_FILE

if [ "$1" = "start" ] ; then
	echo "Starting Kimios Apps Container ..."
	bin/startup.sh

elif [ "$1" = "stop" ] ; then
	echo "Stopping Kimios Apps Container ..."
	bin/shutdown.sh -force
else
  echo "Usage: kimios.sh ( commands ... )"
  echo "commands:"
  echo "  start             Start Kimios App Server"
  echo "  stop              Stop  Kimios App Server"
  exit 1
fi


