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
import org.kimios.utils.spring.PropertiesHolderPropertyPlaceholderConfiguer;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

public class SpringPropertiesConfigurationHolder implements ConfigurationHolder {


    private PropertiesHolderPropertyPlaceholderConfiguer holder;


    public SpringPropertiesConfigurationHolder(PropertiesHolderPropertyPlaceholderConfiguer holder){
        this.holder = holder;
    }

    public boolean exists(String keyOrPrefix) {
        return holder.getResolvedProperties().getProperty(keyOrPrefix) != null;
    }

    public Object getValue(String key) {
        return holder.getResolvedProperties().get(key);
    }

    public String getStringValue(String key) {
        return holder.getResolvedProperties().getProperty(key);
    }

    public List<String> getValues(String prefix) {
        String valuesItems = holder.getResolvedProperties().getProperty(prefix);
        List<String> valuesItemList = Arrays.asList(StringUtils.tokenizeToStringArray(valuesItems, ","));
        return valuesItemList;
    }

    public void refresh() throws ConfigException {

    }
}
