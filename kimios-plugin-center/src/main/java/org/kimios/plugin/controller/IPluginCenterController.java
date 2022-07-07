package org.kimios.plugin.controller;

import org.kimios.kernel.plugin.model.Plugin;
import org.kimios.kernel.security.model.Session;

import java.util.List;

public interface IPluginCenterController {

    List<Plugin> getAll(Session session) throws Exception;
    Plugin get(Session session, long id) throws Exception;
    org.kimios.kernel.ws.pojo.Plugin getPojo(Session session, long id) throws Exception;
    void activate(Session session, long id) throws Exception;
    void deactivate(Session session, long id) throws Exception;
    void delete(Session session, long id) throws Exception;
}
