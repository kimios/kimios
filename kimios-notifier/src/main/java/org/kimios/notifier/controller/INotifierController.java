package org.kimios.notifier.controller;

import org.kimios.kernel.index.query.model.Criteria;
import org.kimios.kernel.index.query.model.SearchResponse;
import org.kimios.kernel.security.model.Session;

import java.util.List;

public interface INotifierController {

    SearchResponse searchDocuments(Session session) throws Exception;

    Integer createNotifications(Session session) throws Exception;

    void sendNotifications(Session session) throws Exception;
}
