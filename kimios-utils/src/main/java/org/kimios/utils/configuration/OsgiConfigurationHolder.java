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

import org.apache.commons.lang.StringUtils;
import org.kimios.exceptions.ConfigException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
;

/**
 * Created by farf on 6/14/14.
 */
public class OsgiConfigurationHolder implements ConfigurationHolder {


    private static Logger logger = LoggerFactory.getLogger(OsgiConfigurationHolder.class);

    private BundleContext bundleContext;

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }


    private Properties prop;

    public Properties getProp() {
        return prop;
    }

    public void setProp(Properties prop) {
        this.prop = prop;
    }

    public OsgiConfigurationHolder(BundleContext bundleContext, String persistentId) {
        this.bundleContext = bundleContext;
        prop = new Properties();
        ServiceReference ref = bundleContext.getServiceReference(ConfigurationAdmin.class.getName());
        ConfigurationAdmin cm = (ConfigurationAdmin) bundleContext.getService(ref);
        if (cm != null) {

            try {
                Dictionary dict = cm.getConfiguration(persistentId).getProperties();
                if (dict != null) {
                    // copy properties into dictionary
                    for (Enumeration enm = dict.keys(); enm.hasMoreElements();) {
                        Object key = enm.nextElement();
                        Object value = dict.get(key);
                        prop.put(key, value);
                        logger.debug("loaded property {} ==> {}", key, value);
                    }
                } else {
                    logger.warn("no dictionnary found for persistent-id", persistentId);
                }
            }
            catch (IOException ioe) {
                // FIXME: consider adding a custom/different exception
                throw new RuntimeException("Cannot retrieve configuration for pid=" + persistentId, ioe);
            }
        } else {
            logger.warn("no configuration admin service found. no kimios properties will be available");
        }

    }


    public boolean exists(String s) {
        return prop.containsKey(s);
    }

    public Object getValue(String s) {
        return prop.get(s);
    }

    public String getStringValue(String s) {
        return prop.getProperty(s);
    }

    public List<String> getValues(String s) {
        if(StringUtils.isNotBlank((prop.get(s).toString()))){
            return Arrays.asList(prop.getProperty(s).split(","));
        } else {
            return new ArrayList<String>();
        }

    }

    public void refresh() throws ConfigException {

    }

    public Properties getAllProperties() {
        return prop;
    }
}
