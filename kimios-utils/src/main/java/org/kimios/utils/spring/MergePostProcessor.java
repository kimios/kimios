package org.kimios.utils.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.ManagedList;

/**
 * Created with IntelliJ IDEA.
 * User: farf
 * Date: 9/4/13
 * Time: 8:23 PM
 * To change this template use File | Settings | File Templates.
 */
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
