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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ConfigurationManager  {

    private static Logger log = LoggerFactory.getLogger(ConfigurationManager.class);

    private ConfigurationHolder holder;

    private static Map<String, ConfigurationManager> instances;

    static {
        synchronized (ConfigurationManager.class){
            instances = new HashMap<>();
        }
    }

    protected ConfigurationManager(String context, ConfigurationHolder holder){
        if(context == null ||context.trim().length() == 0){
            context = "server";
        }
        log.info("creating with context {}", context);
        this.holder = holder;
        synchronized (this){
            instances.put(context, this);
        }
    }

    public static String getValue(String key) throws ConfigException{
        return getValue("server", key);
    }

    public static String getValue(String context, String key) throws ConfigException {
        if (instances.get(context).holder != null && instances.get(context).holder.exists(key)) {
            return instances.get(context).holder.getStringValue(key);
        } else {
            log.warn("[Kimios Configuration] Key {} cannot be found in context {} configuration", key, context);
            return null;
        }
    }

    public static List<String> getListValue(String key) throws ConfigException {
        return getListValue("server", key);
    }

    public static List<String> getListValue(String context, String key) throws ConfigException {
        ConfigurationHolder cHolder = instances.get(context) != null ? instances.get(context).holder : null;
        if (cHolder != null && cHolder.exists(key)) {
            return cHolder.getValues(key);
        } else {
            log.warn("[Kimios Configuration] Key {} cannot be found in context {} configuration", key, context);
            return null;
        }
    }



    public static Properties allValues() {
        return allValues("server");
    }

    public static Properties allValues(String context) {
        return instances.get(context).holder.getAllProperties();
    }
}

