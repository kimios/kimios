package org.kimios.utils.configuration;

import org.kimios.exceptions.ConfigException;
import org.kimios.utils.spring.PropertiesHolderPropertyPlaceholderConfiguer;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: farf
 * Date: 9/6/13
 * Time: 2:41 PM
 * To change this template use File | Settings | File Templates.
 */
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
