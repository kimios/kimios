package org.kimios.telemetry.system.service.impl;

import org.apache.karaf.system.SystemService;
import org.kimios.telemetry.system.service.KimiosSystemService;

public class KarafSystemService implements KimiosSystemService {

    public SystemService systemService;

    public KarafSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    public SystemService getSystemService() {
        return systemService;
    }

    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    @Override
    public String getName() {
        return "Apache Karaf";
    }

    @Override
    public String getVersion() {
        return this.getSystemService().getVersion();
    }

    @Override
    public String getFrameworkName() {
        return this.getSystemService().getFramework().name();
    }

    @Override
    public String getKimiosDistribution() {
        return "KARAF Distribution";
    }

}
