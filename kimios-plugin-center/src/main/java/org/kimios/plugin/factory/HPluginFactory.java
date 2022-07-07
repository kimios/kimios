package org.kimios.plugin.factory;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.kimios.exceptions.ConfigException;
import org.kimios.exceptions.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.plugin.model.Plugin;
import org.kimios.kernel.plugin.model.PluginFactory;
import org.kimios.kernel.plugin.model.PluginStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HPluginFactory extends HFactory implements PluginFactory {

    private static Logger logger = LoggerFactory.getLogger(HPluginFactory.class);

    @Override
    public List<Plugin> getAll() throws DataSourceException, ConfigException {

        List<Plugin> all = getSession()
                .createCriteria(Plugin.class)
                .addOrder( Order.asc("name") )
                .list();

        return all;
    }

    @Override
    public Plugin get(long id) throws DataSourceException, ConfigException {
        List<Plugin> pluginList = getSession()
                .createCriteria(Plugin.class)
                .add(Restrictions.eq("id", id))
                .list();

        return pluginList.size() == 1 ? pluginList.get(0) : null;
    }

    @Override
    public Plugin get(String codeName) throws DataSourceException, ConfigException {
        List<Plugin> pluginList = getSession()
                .createCriteria(Plugin.class)
                .add(Restrictions.eq("codeName", codeName))
                .list();

        return pluginList.size() == 1 ? pluginList.get(0) : null;
    }

    @Override
    public void deactivate(long id) throws DataSourceException, ConfigException {
        this.updateStatus(id, PluginStatus.DISABLED);
    }

    @Override
    public void activate(long id) throws DataSourceException, ConfigException {
        this.updateStatus(id, PluginStatus.ACTIVE);
    }

    public void updateStatus(long id, PluginStatus pluginStatus) throws DataSourceException, ConfigException {
        String query = "UPDATE plugin SET status = :pluginStatus WHERE id = :id";

        getSession().createQuery(query)
                .setString("status", pluginStatus.toString())
                .setLong("id", id)
                .executeUpdate();
    }

    @Override
    public void delete(Plugin plugin) throws DataSourceException, ConfigException {
        getSession().delete(plugin);
        getSession().flush();
    }

    @Override
    public Plugin saveOrUpdate(Plugin plugin) throws DataSourceException, ConfigException {
        getSession().saveOrUpdate(plugin);
        getSession().flush();
        return plugin;
    }
}
