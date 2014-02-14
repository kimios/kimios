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

package org.kimios.utils.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: farf
 * Date: 9/6/13
 * Time: 2:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class PropertiesHolderPropertyPlaceholderConfiguer extends PropertyPlaceholderConfigurer {


    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {

        /*
            Execute standard bean definition replacement
         */
        super.processProperties(beanFactoryToProcess, props);
        /*
            Add support to maintain reference to resolved properties
         */
        resolvedProperties = props;
    }



    private Properties resolvedProperties = null;


    public Properties getResolvedProperties() {
        return resolvedProperties;
    }
}
