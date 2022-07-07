package org.kimios.plugin.controller.impl;

import org.kimios.api.controller.IKimiosPluginController;
import org.kimios.exceptions.AccessDeniedException;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.plugin.controller.IPluginCenterController;
import org.kimios.kernel.plugin.model.Plugin;
import org.kimios.kernel.plugin.model.PluginFactory;
import org.kimios.kernel.plugin.model.PluginStatus;
import org.kimios.kernel.security.model.Session;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

public class PluginCenterController extends AKimiosController implements IPluginCenterController {

    private static Logger logger = LoggerFactory.getLogger(PluginCenterController.class);

    private PluginFactory pluginFactory;

    private List<IKimiosPluginController> pluginControllerList = new ArrayList<>();

    List<Plugin> retrievePlugins(Session session) {
        return this.pluginFactory.getAll();
    }

    public PluginCenterController() {}

    public PluginFactory getPluginFactory() {
        return pluginFactory;
    }

    public void setPluginFactory(PluginFactory pluginFactory) {
        this.pluginFactory = pluginFactory;
    }

    @Override
    public List<Plugin> getAll(Session session) throws Exception {
        if (! this.getSecurityAgent().isAdmin(session.getUserName(), session.getUserSource())) {
            throw new AccessDeniedException();
        }

        return this.pluginFactory.getAll();
    }

    @Override
    public Plugin get(Session session, long id) throws Exception {
        if (! this.getSecurityAgent().isAdmin(session.getUserName(), session.getUserSource())) {
            throw new AccessDeniedException();
        }

        return this.pluginFactory.get(id);
    }

    @Override
    public org.kimios.kernel.ws.pojo.Plugin getPojo(Session session, long id) throws Exception {
        if (! this.getSecurityAgent().isAdmin(session.getUserName(), session.getUserSource())) {
            throw new AccessDeniedException();
        }

        Plugin plugin = this.pluginFactory.get(id);
        return plugin != null ? plugin.toPojo() : null;
    }

    @Override
    public void activate(Session session, long id) throws Exception {
        if (! this.getSecurityAgent().isAdmin(session.getUserName(), session.getUserSource())) {
            throw new AccessDeniedException();
        }

        this.pluginFactory.activate(id);
    }

    @Override
    public void deactivate(Session session, long id) throws Exception {
        if (! this.getSecurityAgent().isAdmin(session.getUserName(), session.getUserSource())) {
            throw new AccessDeniedException();
        }

        this.pluginFactory.deactivate(id);
    }

    @Override
    public void delete(Session session, long id) throws Exception {
        if (! this.getSecurityAgent().isAdmin(session.getUserName(), session.getUserSource())) {
            throw new AccessDeniedException();
        }

        Plugin plugin = this.pluginFactory.get(id);
        if (plugin != null) {
            this.pluginFactory.delete(plugin);
        }
    }

    public List<IKimiosPluginController> getPluginControllerList() {
        return pluginControllerList;
    }

    public void setPluginControllerList(List<IKimiosPluginController> pluginControllerList) {
        this.pluginControllerList = pluginControllerList;
    }

    public void bind(ServiceReference reference) {
        logger.info(">>> bind service");
        Arrays.asList(reference.getPropertyKeys()).forEach(key ->
                logger.info("service property: " + key + " : " + reference.getProperty(key))
        );

        IKimiosPluginController pluginController = ((IKimiosPluginController) reference.getBundle().getBundleContext().getService(reference));
        String serviceCodeName = pluginController.getCodeName();
        Plugin plugin = this.pluginFactory.get(serviceCodeName);

        // the plugin does not exist in DB
        if (plugin == null) {
            // let's create it
            Plugin newPlugin = new Plugin(
                    serviceCodeName,
                    pluginController.getName(),
                    pluginController.getVersion(),
                    PluginStatus.DISABLED,
                    true
            );
            plugin = this.pluginFactory.saveOrUpdate(newPlugin);
        } else {
            plugin.setStarted(true);
        }
        this.pluginFactory.saveOrUpdate(plugin);

        logger.info("<<<");
    }

    public void bind(Serializable service) {
    }

    public void unbind(ServiceReference reference) {
        logger.info(">>> unbind service");
        if (reference != null) {
            Arrays.asList(reference.getPropertyKeys()).forEach(key ->
                    logger.info("service property: " + key + " : " + reference.getProperty(key))
            );

            IKimiosPluginController pluginController = ((IKimiosPluginController) reference.getBundle().getBundleContext().getService(reference));
            String serviceCodeName = pluginController.getCodeName();
            Plugin plugin = this.pluginFactory.get(serviceCodeName);

            if (plugin != null) {
                plugin.setStarted(false);
                this.pluginFactory.saveOrUpdate(plugin);
            }
        } else {
            logger.info("reference is null");
        }
        logger.info("<<<");
    }
}
