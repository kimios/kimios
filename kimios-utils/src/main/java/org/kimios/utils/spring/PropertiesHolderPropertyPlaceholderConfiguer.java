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
