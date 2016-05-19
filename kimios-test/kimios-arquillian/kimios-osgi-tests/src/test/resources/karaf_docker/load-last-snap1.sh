#!/bin/bash

repo=http://repositories.k.devlib.fr/repository/kimios-private
groupId=org/kimios
artifactId=kimios-karaf-distribution

httpUser="kimios-accessor"
httpPassword="kimios2015*$"
wget --http-user=${httpUser} --http-password=${httpPassword} \
        $repo/$groupId/$artifactId/maven-metadata.xml \
        -O maven-metadata.xml

latest=`xmllint --noout -xpath "/metadata/versioning/latest/text()" maven-metadata.xml`
tmstp=`xmllint --noout -xpath "/metadata/versioning/lastUpdated/text()" maven-metadata.xml`

lastpart=${tmstp:8}
firstpart=${tmstp:0:8}
baseVersion=$(echo $latest | cut -d "-" -f 1)
baseurl=$repo/$groupId/$artifactId
metabuildurl=$baseurl/$latest/maven-metadata.xml

wget --http-user=${httpUser} --http-password=${httpPassword} \
        $metabuildurl \
        -O maven-metadata.xml

buildNumber=`xmllint --noout -xpath "/metadata/versioning/snapshot/buildNumber/text()" maven-metadata.xml`
tmstp=`xmllint --noout -xpath "/metadata/versioning/snapshot/timestamp/text()" maven-metadata.xml`

fullurl=$baseurl/$latest/$artifactId-$baseVersion-$tmstp-$buildNumber.tar.gz

echo $fullurl
wget --http-user=${httpUser} --http-password=${httpPassword} $fullurl -O $artifactId-$latest-$buildNumber.tar.gz