package org.kimios.telemetry.system.service.impl;

import org.kimios.telemetry.system.service.KimiosSystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.SpringVersion;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class SpringModeSystemService implements KimiosSystemService {

    private static Logger logger = LoggerFactory.getLogger(SpringModeSystemService.class);

    private static String CATALINA_SERVER_MBEAN_NAME = "Catalina:type=Server";

    private MBeanServerConnection mBeanServerConnection;

    public SpringModeSystemService() {
        this.setMBeanServerConnection(ManagementFactory.getPlatformMBeanServer());
    }

    public MBeanServerConnection getMBeanServerConnection() {
        return mBeanServerConnection;
    }

    public void setMBeanServerConnection(MBeanServerConnection mBeanServerConnection) {
        this.mBeanServerConnection = mBeanServerConnection;
    }

    @Override
    public String getName() {

        // server is CATALINA
        // add others if needed
        String name = DATA_UNKNOWN;
        try {
            name = this.getMBeanServerConnection()
                    .getAttribute(new ObjectName(CATALINA_SERVER_MBEAN_NAME), "serverInfo")
                    .toString();
        } catch (Exception e) {
            logger.error("Error while getting server name from MBean");
            logger.error(e.getMessage());
        }

        return name;
    }

    @Override
    public String getVersion() {

        // server is CATALINA
        // add others if needed
        String version = DATA_UNKNOWN;
        try {
            version = this.getMBeanServerConnection()
                    .getAttribute(new ObjectName(CATALINA_SERVER_MBEAN_NAME), "serverNumber")
                    .toString();
        } catch (Exception e) {
            logger.error("Error while getting server version from MBean");
            logger.error(e.getMessage());
        }

        return version;
    }

    @Override
    public String getFrameworkName() {
        return "Spring " + SpringVersion.getVersion();
    }

    @Override
    public String getKimiosDistribution() {
        return "WAR";
    }
}
