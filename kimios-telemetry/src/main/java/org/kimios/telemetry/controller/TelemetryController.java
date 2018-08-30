package org.kimios.telemetry.controller;

import org.kimios.kernel.controller.*;
import org.apache.karaf.system.SystemService;
import org.kimios.kernel.security.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class TelemetryController extends AKimiosController implements ITelemetryController {

    private static Logger logger = LoggerFactory.getLogger(TelemetryController.class);

    private SystemService systemService;

    public SystemService getSystemService() {
        return systemService;
    }

    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    public TelemetryController(SystemService systemService){
        this.systemService = systemService;
    }

    public String[] retrieveKarafInstanceNameAndVersion() throws Exception {
        String[] data = {this.systemService.getName(), this.systemService.getVersion(), this.systemService.getFramework().name()};

        return data;
    }

    @Override
    public void sendToTelemetryPHP(Session session) throws Exception {
        String[] karafInfo = this.retrieveKarafInstanceNameAndVersion();

        logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        logger.debug("Karaf info:");
        Arrays.asList(karafInfo).forEach(v -> {
            logger.debug(v);
        });
        logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }
}
