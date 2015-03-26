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

import org.osgi.framework.BundleContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

public class ApplicationContextProvider implements ApplicationContextAware, BeanFactoryPostProcessor
{
    private static ApplicationContext ctx;

    private static ConfigurableListableBeanFactory factory;

    public static Object loadBean(String beanName)
    {
        return ctx.getBean(beanName);
    }

    public static ApplicationContext context(){
        return ctx;
    }


    public static <T> T loadBean(Class<T> _class){
        if(ctx != null){
            return ctx.getBean(_class);
        }
        return null;
    }


    public static <T> Map<String, T> loadBeans(Class<T> _class){
        return ctx.getBeansOfType(_class);
    }

    public static Object instantiate(String beanName, Class<?> _class, String parentName)
    {
        BeanDefinitionRegistry registry = ((BeanDefinitionRegistry) factory);
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(_class);
        beanDefinition.setLazyInit(false);
        beanDefinition.setAbstract(false);
        beanDefinition.setParentName(parentName);
        beanDefinition.setAutowireCandidate(true);
        beanDefinition.setScope("singleton");
        registry.registerBeanDefinition(beanName, beanDefinition);
        return ctx.getBean(beanName);
    }

    public static <T> T prototypeBean(String beanName, Class<T> _class, String parentName)
    {

        T bean = null;
        try {
            bean = ctx.getBean(beanName, _class);
        } catch (Exception e) {
            BeanDefinitionRegistry registry = ((BeanDefinitionRegistry) factory);
            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            beanDefinition.setBeanClass(_class);
            beanDefinition.setLazyInit(false);
            beanDefinition.setAbstract(false);
            beanDefinition.setParentName(parentName);
            beanDefinition.setAutowireCandidate(true);
            beanDefinition.setScope("prototype");
            registry.registerBeanDefinition(beanName, beanDefinition);

            bean = ctx.getBean(beanName, _class);
        }
        return bean;
    }

    public void setApplicationContext(ApplicationContext applicationContext)
    {
        ctx = applicationContext;
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory _factory)
            throws BeansException
    {
        factory = _factory;
    }
}

