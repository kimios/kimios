#!/bin/sh
export FABRIC8_CONSOLE=http://172.28.128.4
#export DOCKER_REGISTRY=172.30.17.90:5000
export KUBERNETES_TRUST_CERT=true
export DOCKER_IP=172.28.128.4
export DOCKER_HOST=tcp://172.28.128.4:2375

mvn -Ddocker.useOpenShiftAuth -P f8-build,f8-local-deploy 

