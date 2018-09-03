package org.kimios.telemetry.controller;

import org.kimios.kernel.security.model.Session;

import java.util.HashMap;

public interface ITelemetryController {

    HashMap<String, String> retrieveKarafInstanceNameAndVersion() throws Exception;

    void sendToTelemetryPHP(Session session) throws Exception;
}

