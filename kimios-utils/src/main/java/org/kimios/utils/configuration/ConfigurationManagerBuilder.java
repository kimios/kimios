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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by farf on 6/17/14.
 */
public class ConfigurationManagerBuilder {

    private static Logger log = LoggerFactory.getLogger(ConfigurationManagerBuilder.class);

    private ConfigurationHolder holder;

    public ConfigurationHolder getHolder() {
        return holder;
    }

    public void setHolder(ConfigurationHolder holder) {
        this.holder = holder;
    }

    public ConfigurationManager createInstance() {
        ConfigurationManager cfg = new ConfigurationManager();
        cfg.setHolder(holder);
        log.debug("While building Configuration Manager: " + getHolder() + " ==> " + holder);
        /*
            Set properties as system properties to handle custom SolR properties
         */

        Properties properties = holder.getAllProperties();
        log.debug("loaded properties: {} (keyset: {})", properties.size(), properties.keySet().size());
        for (Object o : properties.keySet()) {
            System.setProperty("kimios." + o.toString(), properties.getProperty(o.toString()));
        }
        for (Object u : System.getProperties().keySet()) {
            if (u.toString().startsWith("kimios.")) {
                log.debug("java system properties set: {} => {}", u.toString(), System.getProperty(u.toString()
                ));
            }
        }
        return cfg;
    }
}
