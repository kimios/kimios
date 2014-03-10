/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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
import org.kimios.utils.configuration.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventHandlerManager implements ApplicationContextAware
{
    private static Logger log = LoggerFactory.getLogger(EventHandlerManager.class);

    private static EventHandlerManager instance;

    private ConfigurationManager configurationManager;

    private ApplicationContext springContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.springContext = applicationContext;
    }

    private EventHandlerManager()
    { }

    synchronized public static EventHandlerManager getInstance()
    {
        if (instance == null) {
            instance = new EventHandlerManager();
        }
        return instance;
    }

    public List<GenericEventHandler> handlers;

    private void init()
    {

        configurationManager = springContext.getBean(ConfigurationManager.class);
        EventContext.init();
        handlers = new ArrayList<GenericEventHandler>();
        handlers.add(new ActionLogger());
        handlers.add(new WorkflowMailer());
        /*
            Load handlers from spring context
         */
        Map<String, GenericEventHandler> springInstantiatedHandlers =
                springContext.getBeansOfType(GenericEventHandler.class);

        for (GenericEventHandler manager : springInstantiatedHandlers.values()) {
            log.info("Adding event handler " + manager.getClass().getName());
            handlers.add(manager);
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
                    } catch (Exception e) {
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

