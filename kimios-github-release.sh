#!/bin/bash


GITHUB_ORGANIZATION=kimios
GITHUB_REPO=kimios
VERSION_NAME=$GITTAG
PROJECT_NAME=kimios

github-release delete --user ${GITHUB_ORGANIZATION} --repo ${GITHUB_REPO} --tag ${VERSION_NAME}
github-release release --user ${GITHUB_ORGANIZATION} --pre-release --draft --repo ${GITHUB_REPO} --tag ${VERSION_NAME} --name "${VERSION_NAME}"


declare -a arr=("kimios-bundle-builder/" "kimios-osgi-packages/kimios-karaf-distribution/")
targzArtifacts=(`find . -type f -name *.tar.gz`)
zipArtifacts=(`find . -type f -name *.zip`)

for file in ${targzArtifacts[@]} 
do
BINARTIFACT=`basename $file`
echo "uploading $BINARTIFACT to github for releag $VERSION_NAME"
github-release upload --user ${GITHUB_ORGANIZATION} --repo ${GITHUB_REPO} -R --tag ${VERSION_NAME} --name "${BINARTIFACT}" --file "${file}" 
done

for file in ${zipArtifacts[@]} 
do
BINARTIFACT=`basename $file`
echo "uploading $BINARTIFACT to github for releag $VERSION_NAME"
github-release upload --user ${GITHUB_ORGANIZATION} --repo ${GITHUB_REPO} -R --tag ${VERSION_NAME} --name "${BINARTIFACT}" --file "${file}"
done
