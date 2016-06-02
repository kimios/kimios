#!/bin/sh
export MAVEN_OPT="-Xmx6000m -Xms1024m -XX:MaxPermSize=1524m"
mvn  -DskipTests=true -DautoVersionSubmodules=true release:prepare
