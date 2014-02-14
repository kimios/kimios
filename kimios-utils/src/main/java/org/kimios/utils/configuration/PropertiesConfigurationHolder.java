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
package org.kimios.utils.configuration;

import org.kimios.exceptions.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author Fabien Alin (Farf) <fabien.alin@gmail.com>
 *
 *         Configuration Holder with Properties format
 */
public class PropertiesConfigurationHolder implements ConfigurationHolder
{
    private static Logger log = LoggerFactory.getLogger(ConfigurationHolder.class);

    private String CONFIG_FILE_NAME = "/kimios.properties";

    private String configFilePath;

    private Properties values;

    public Properties getValues()
    {
        return values;
    }

    public void setValues(Properties values)
    {
        this.values = values;
    }

    public void init(String _configFilePath) throws ConfigException
    {
        if (_configFilePath == null) {
            throw new ConfigException("Config file has not been initialized");
        }
        configFilePath = _configFilePath;
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(_configFilePath + CONFIG_FILE_NAME));
            if (log.isDebugEnabled()) {
                for (Object k : values.keySet()) {
                    log.debug("Key:" + k + " Value:" + values.get(k));
                }
            }
            values = prop;
        } catch (IOException io) {
            throw new ConfigException("Properties Config file was unparsable : " + io.getMessage());
        }
    }

    public void init(InputStream configStream) throws ConfigException
    {
        try {
            Properties prop = new Properties();
            prop.load(configStream);
            if (log.isDebugEnabled()) {
                for (Object k : values.keySet()) {
                    log.debug("Key:" + k + " Value:" + values.get(k));
                }
            }
            values = prop;
        } catch (IOException io) {
            throw new ConfigException("Properties Config file was unparsable : " + io.getMessage());
        }
    }

    public boolean exists(String keyOrPrefix)
    {
        return values.containsKey(keyOrPrefix) || System.getProperty(keyOrPrefix) != null || System.getenv(keyOrPrefix) != null;
    }

    public Object getValue(String key)
    {
        Object retValues = null;
        try {
            String value = System.getProperty(key);
            if (value == null) {
                value = System.getenv(key);
            }
            retValues = value;
        }
        catch (Throwable ex) {
            retValues = null;
        }
        if(retValues == null){
            return values.get(key);
        } else
            return retValues;
    }

    public String getStringValue(String key)
    {
        Object val = this.getValue( key );
        if(val != null)
            return val.toString();

        return null;
    }

    public List<String> getValues(String prefix)
    {
        String valuesItems = values.getProperty(prefix);
        List<String> valuesItemList = Arrays.asList(StringUtils.tokenizeToStringArray(valuesItems, ","));
        return valuesItemList;
    }

    public void refresh() throws ConfigException
    {
        init(configFilePath);
    }
}
