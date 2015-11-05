/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2015  DevLib'
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.kernel.events;

import org.kimios.kernel.events.impl.ActionLogger;
import org.kimios.kernel.events.impl.WorkflowMailer;
import org.kimios.kernel.events.model.EventContext;
import org.kimios.utils.configuration.ConfigurationManager;
import org.kimios.utils.extension.ExtensionRegistry;
import org.kimios.utils.spring.ApplicationContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventHandlerManager extends ExtensionRegistry<GenericEventHandler> implements IEventHandlerManager {
    private static Logger log = LoggerFactory.getLogger(EventHandlerManager.class);

    private static EventHandlerManager instance;

    private ConfigurationManager configurationManager;

    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    public void setConfigurationManager(ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }

    private EventHandlerManager()
    {
    }

    synchronized public static IEventHandlerManager getInstance()
    {
        if (instance == null) {
            instance = new EventHandlerManager();
        }
        return instance;
    }

    @Override
    synchronized public void addHandler(GenericEventHandler handler)
    {
        instance.handlers.add(handler);
    }

    private List<GenericEventHandler> handlers;

    @Override
    public List<GenericEventHandler> handlers(){
        List<GenericEventHandler> handlerList = new ArrayList<GenericEventHandler>(handlers);
        return handlerList;
    }

    @Override
    public List<GenericEventHandler> updatableHandlers(){
        return handlers;
    }

    @Override
    protected void handleAdd(Class<? extends GenericEventHandler> classz) {
        try {
            log.debug("creating event handler " + classz);
            GenericEventHandler handler = classz.newInstance();
            if(handlers == null || handlers.isEmpty()){
                handlers = new ArrayList<GenericEventHandler>();
                handlers.add(new ActionLogger());
                handlers.add(new WorkflowMailer());
            }
            handlers.add(handler);
            log.info("instantiating event handler", classz);
        } catch (Exception e) {
            log.error("error while adding event handler " + classz, e);
        }
    }

    @Override
    protected void handleRemove(Class<? extends GenericEventHandler> classz) {
        GenericEventHandler _z = null;
        for(GenericEventHandler e: handlers){
            if(e.getClass().isAssignableFrom(classz)){
                log.info("removing event handler {}", e);
                _z = e;
            }
        }
        if(_z != null){
            log.info("removing event handler {}", _z);
            handlers.remove(_z);
        }

    }

    public void init()
    {
        EventContext.init();
        if(handlers == null || handlers.isEmpty()){
            handlers = new ArrayList<GenericEventHandler>();
            handlers.add(new ActionLogger());
            handlers.add(new WorkflowMailer());
        }

        try{
             /*
            Load handlers from spring context
            */

            Map<String, GenericEventHandler> springInstantiatedHandlers =
                    ApplicationContextProvider.loadBeans(GenericEventHandler.class);
            for (GenericEventHandler manager : springInstantiatedHandlers.values()) {
                log.info("Adding event handler " + manager.getClass().getName());
                handlers.add(manager);
            }
        } catch (Exception ex){
            log.info("not spring enabled");
        }

        try {
            ClassLoader cLoader = Thread.currentThread().getContextClassLoader();
            List<String> eventClasses = configurationManager.getListValue("dms.events");
            if (eventClasses != null && eventClasses.size() > 0) {
                for (String handlerClass : eventClasses) {
                    try {
                        log.debug("Creating event handler " + handlerClass);
                        Class<?> cHandler = Class.forName(handlerClass, true, cLoader);
                        GenericEventHandler handler = (GenericEventHandler) cHandler.newInstance();
                        handlers.add(handler);
                    }
                    catch (ClassNotFoundException cnf){
                        log.error("{} can't be loaded due to classlaoder error", handlerClass);
                    }
                    catch (Exception e) {
                        log.error("Error while loading event handler " + handlerClass, e);
                    }
                }
            } else {
                log.info("No custom event handlers defined");
            }
        } catch (Exception e) {
            log.error("Error while Loading event handlers", e);
        }
    }
}

