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
package org.kimios.utils.configuration;

import org.kimios.exceptions.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Properties;

public class ConfigurationManager implements FactoryBean<ConfigurationManager>
{
    private static Logger log = LoggerFactory.getLogger(ConfigurationManager.class);

    private ConfigurationHolder holder;

    public ConfigurationHolder getHolder()
    {
        return holder;
    }

    public void setHolder(ConfigurationHolder holder)
    {
        this.holder = holder;
    }

    private static ConfigurationManager instance;

    protected ConfigurationManager()
    {
        instance = this;
        log.info("Creating Kimios Configuration Manager");
    }

    @Override
    public ConfigurationManager getObject() throws Exception {
        ConfigurationManager cfg = new ConfigurationManager();
        cfg.holder = getHolder();
        ConfigurationManager.instance = cfg;
        log.debug("While building Configuration Manager: " + getHolder() + " ==> " + cfg.holder);
        return cfg;
    }

    @Override
    public Class<?> getObjectType() {
        return ConfigurationManager.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public static void init(ApplicationContext springContext) throws ConfigException
    {
        /*
            Look for spring instantiated ConfigHolder
         */
        if (instance.holder == null) {
            log.info("Configuration not defined. Looking for default implementation");
            instance.holder = springContext.getBean(ConfigurationHolder.class);
            if (instance.holder == null) {
                log.error("No config holder found");
                throw new RuntimeException("No config holder found");
            }
        }
    }

    public static String getValue(String key) throws ConfigException
    {

        log.debug("called configuration manager get value on instance " + instance);
        if (instance.holder.exists(key)) {
            return instance.holder.getStringValue(key);
        } else {
            log.warn("[Kimios Configuration] Key " + key + " cannot be found in global configuration");
            return null;
        }
    }

    public static List<String> getListValue(String key) throws ConfigException
    {
        log.info("Instance " + instance + ". " + (instance != null ? instance.holder : " No instance"));
        if (instance != null && instance.holder != null && instance.holder.exists(key)) {
            return instance.holder.getValues(key);
        } else {
            log.warn("[Kimios  Configuration Key " + key + " cannot be found in global configuration");
            return null;
        }
    }

    public static Properties allValues(){
        return instance.holder.getAllProperties();
    }
}

