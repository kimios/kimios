/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
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

package org.hibernate.jpa.custom;


import org.hibernate.osgi.OsgiClassLoader;
import org.hibernate.osgi.OsgiJtaPlatform;
import org.hibernate.osgi.OsgiPersistenceProvider;
import org.hibernate.osgi.OsgiServiceUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import java.util.Map;
import java.util.Properties;

/**
 * Created by farf on 07/02/16.
 */
public class CustomPersistenceProvider extends OsgiPersistenceProvider {

    private static final Logger logger = LoggerFactory.getLogger(CustomPersistenceProvider.class);

    public CustomPersistenceProvider(OsgiClassLoader osgiClassLoader, OsgiJtaPlatform osgiJtaPlatform, OsgiServiceUtil osgiServiceUtil, Bundle requestingBundle) {
        super(osgiClassLoader, osgiJtaPlatform, osgiServiceUtil, requestingBundle);
    }

    @Override
    public EntityManagerFactory createEntityManagerFactory(String persistenceUnitName, Map properties) {
        if(properties == null){
            properties = new Properties();
        }
        if(properties != null) {

            try {
                BundleContext bundleContext = FrameworkUtil.getBundle(CustomPersistenceProvider.class).getBundleContext();
                ServiceReference configurationAdminReference =
                        bundleContext.getServiceReference(ConfigurationAdmin.class.getName());
                if (configurationAdminReference != null) {
                    ConfigurationAdmin confAdmin = (ConfigurationAdmin) bundleContext.getService(configurationAdminReference);

                    org.osgi.service.cm.Configuration kmsConfig = confAdmin.getConfiguration("org.kimios.server.app");
                    String databaseType = kmsConfig.getProperties().get("jdbc.databasetype").toString();
                    String dialect = kmsConfig.getProperties().get("jdbc.dialect").toString();
                    properties.put("hibernate.dialect", dialect);
                    properties.put("hibernate.ejb.cfgfile", "hibernate-" + databaseType + ".cfg.xml");


                }
            }catch (Exception ex) {
                logger.error("error while loading kimios properties");
            }
        }

        return super.createEntityManagerFactory(persistenceUnitName, properties);
    }

    @Override
    public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map properties) {
        if(properties == null){
            properties = new Properties();
        }
        if(properties != null) {

            try {
                BundleContext bundleContext = FrameworkUtil.getBundle(CustomPersistenceProvider.class).getBundleContext();
                ServiceReference configurationAdminReference =
                        bundleContext.getServiceReference(ConfigurationAdmin.class.getName());
                if (configurationAdminReference != null) {
                    ConfigurationAdmin confAdmin = (ConfigurationAdmin) bundleContext.getService(configurationAdminReference);

                    org.osgi.service.cm.Configuration kmsConfig = confAdmin.getConfiguration("org.kimios.server.app");
                    String databaseType = kmsConfig.getProperties().get("jdbc.databasetype").toString();
                    String dialect = kmsConfig.getProperties().get("jdbc.dialect").toString();
                    properties.put("hibernate.dialect", dialect);
                    properties.put("hibernate.ejb.cfgfile", "hibernate-" + databaseType + ".cfg.xml");


                }
            }catch (Exception ex) {
                logger.error("error while loading kimios properties");
            }
        }
        return super.createContainerEntityManagerFactory(info, properties);
    }

}
