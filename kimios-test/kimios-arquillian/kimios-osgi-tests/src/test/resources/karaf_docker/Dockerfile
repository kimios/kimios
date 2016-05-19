### KIMIOS Server Docker File (Karaf)
### Could be replaced by STI Builder Container, copying artefact directly from maven ?
FROM centos:7.1.1503
# Install Java.
RUN \
  yum -y  install java-1.7.0-openjdk && \
  yum -y install tar && \
  yum -y install wget && yum install -y libxml2 && \
  yum clean all


ENV KIMIOS_PG_LINK jdbc:postgresql://127.0.0.1/kimios
ENV KIMIOS_PGUSER kimios
ENV KIMIOS_PGPASS kimios
COPY kimios-karaf-distribution-1.1-SNAPSHOT.tar.gz /
RUN mkdir /opt/karaf; \
    mkdir -p /home/kimios/repository; \
    mkdir /opt/karaf/deploy; \
    tar --strip-components=1 -C /opt/karaf -xzf kimios-karaf-distribution-1.1-SNAPSHOT.tar.gz; \
    rm /kimios-karaf-distribution-1.1-SNAPSHOT.tar.gz;
COPY setenv /opt/karaf/bin/
COPY custom.properties /opt/karaf/etc/
RUN chmod +x /opt/karaf/bin/setenv
VOLUME ["/home/kimios/repository"]
EXPOSE 1099 8101 44444 8181
ENTRYPOINT ["/opt/karaf/bin/karaf"]
# Define default command.
CMD ["bash"]