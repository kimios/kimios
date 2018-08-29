package org.kimios.telemetry.controller;

import org.kimios.kernel.security.model.Session;

public interface ITelemetryController {

    String[] retrieveKarafInstanceNameAndVersion() throws Exception;

    void sendToTelemetryPHP(Session session) throws Exception;
}

