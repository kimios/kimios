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
package org.kimios.kernel.configuration;

import org.kimios.kernel.system.DmsModule;
import org.kimios.kernel.system.RepositoryCleaner;
import org.kimios.utils.configuration.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Deprecated
public class Starter
{
    private static Logger log = LoggerFactory.getLogger(Starter.class);

    private static RepositoryCleaner rc;

    private static Date startedAt;

    private static Map<String, DmsModule> modules;

    public static Date getStartedAt()
    {
        return startedAt;
    }

    public static synchronized void stop()
    {
        try {
            //Stopping modules
            if (modules != null) {
                for (String mName : modules.keySet()) {
                    DmsModule module = modules.get(mName);
                    try {
                        log.info("Stopping " + mName + " " + module.getClass().getName());
                        module.stop();
                    } catch (Exception e) {
                        log.error("Error while stopping module " + mName, e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Kimios Closing Error ", e);
        }
    }

    public static synchronized void start(String configPath)
    {
        try {
            log.info(" **** Kimios **** Initializing context ...");
            startedAt = new Date();
            //Modules
            modules = new HashMap<String, DmsModule>();
            try {
                URLClassLoader cLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
                List<String> modulesClassList = ConfigurationManager.getListValue("dms.modules");
                for (String moduleClass : modulesClassList) {
                    try {
                        log.info("Loading Module " + moduleClass);
                        Class<?> cModule = Class.forName(moduleClass, true, cLoader);
                        Method mConfig = cModule.getMethod("setConfigPath", String.class);
                        DmsModule module = (DmsModule) cModule.newInstance();
                        mConfig.invoke(module, configPath);
                        module.start();
                        modules.put(moduleClass, module);
                    } catch (Exception e) {
                        log.error("Error while loading module", e);
                    }
                }
            } catch (Exception e) {
                log.error("Error while Loading Modules", e);
            }
        } catch (Exception e) {
            log.error("Kimios Context initialization error \n", e);
        }
    }
}


