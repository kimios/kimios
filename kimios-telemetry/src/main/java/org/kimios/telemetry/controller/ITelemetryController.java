package org.kimios.telemetry.controller;

import org.kimios.kernel.index.query.model.SearchResponse;
import org.kimios.kernel.security.model.Session;

public interface ITelemetryController {

    SearchResponse searchDocuments(Session session) throws Exception;

    Integer createNotifications(Session session) throws Exception;

    void sendNotifications(Session session) throws Exception;
}
