package org.kimios.webservices.plugin.impl;

import org.kimios.plugin.controller.IPluginCenterController;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.ws.pojo.Plugin;
import org.kimios.webservices.IServiceHelper;
import org.kimios.webservices.exceptions.DMServiceException;
import org.kimios.webservices.plugin.PluginService;

import java.util.List;
import java.util.stream.Collectors;

public class PluginServiceImpl implements PluginService {
    private IPluginCenterController pluginCenterController;

    private IServiceHelper helper;

    public PluginServiceImpl() {
    }

    public IPluginCenterController getPluginCenterController() {
        return pluginCenterController;
    }

    public void setPluginCenterController(IPluginCenterController pluginCenterController) {
        this.pluginCenterController = pluginCenterController;
    }

    public IServiceHelper getHelper() {
        return helper;
    }

    public void setHelper(IServiceHelper helper) {
        this.helper = helper;
    }

    @Override
    public List<Plugin> getAll(String sessionId) throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);
            return this.pluginCenterController.getAll(session).stream()
                    .map(plugin -> plugin.toPojo())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }

    @Override
    public Plugin get(String sessionId, long id) throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);

            return this.pluginCenterController.getPojo(session, id);
        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }

    @Override
    public void deactivate(String sessionId, long id) throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);
            this.pluginCenterController.deactivate(session, id);
        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }

    @Override
    public void activate(String sessionId, long id) throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);
            this.pluginCenterController.activate(session, id);
        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }
}
