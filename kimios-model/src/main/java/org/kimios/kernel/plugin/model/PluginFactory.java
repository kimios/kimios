package org.kimios.kernel.plugin.model;

import org.kimios.exceptions.ConfigException;
import org.kimios.exceptions.DataSourceException;

import java.util.List;

public interface PluginFactory {

    List<Plugin> getAll() throws DataSourceException, ConfigException, DataSourceException;
    Plugin get(long id) throws DataSourceException, ConfigException;
    Plugin get(String codeName) throws DataSourceException, ConfigException;
    void deactivate(long id) throws DataSourceException, ConfigException;
    void activate(long id) throws DataSourceException, ConfigException;
    void delete(Plugin plugin) throws DataSourceException, ConfigException;

    Plugin saveOrUpdate(Plugin plugin) throws DataSourceException, ConfigException;
}
