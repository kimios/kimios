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

package org.kimios.utils.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.ManagedList;

public class MergePostProcessor implements BeanFactoryPostProcessor {

    private String beanToMerge;

    private String beanPropertyToMerge;

    private String beanTypeToLoad;

    public String getBeanToMerge() {
        return beanToMerge;
    }

    public void setBeanToMerge(String beanToMerge) {
        this.beanToMerge = beanToMerge;
    }

    public String getBeanPropertyToMerge() {
        return beanPropertyToMerge;
    }

    public void setBeanPropertyToMerge(String beanPropertyToMerge) {
        this.beanPropertyToMerge = beanPropertyToMerge;
    }

    public String getBeanTypeToLoad() {
        return beanTypeToLoad;
    }

    public void setBeanTypeToLoad(String beanTypeToLoad) {
        this.beanTypeToLoad = beanTypeToLoad;
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        BeanDefinition u = beanFactory.getBeanDefinition(beanToMerge);
        PropertyValue value = u.getPropertyValues().getPropertyValue(beanPropertyToMerge);
        if (value.getValue() instanceof ManagedList) {
            ManagedList beanList = (ManagedList) value.getValue();
            try {
                Class<?> z = Class.forName(beanTypeToLoad);
                String[] list = beanFactory.getBeanNamesForType(z);
                /*
                    Add bean to list
                 */
                for (String found : list) {
                    beanList.add(beanList.size() - 1, beanFactory.getBeanDefinition(found));
                }
            } catch (ClassNotFoundException e) {
                throw new FatalBeanException("Class not found " + beanTypeToLoad, e);
            }
        }
    }
}
