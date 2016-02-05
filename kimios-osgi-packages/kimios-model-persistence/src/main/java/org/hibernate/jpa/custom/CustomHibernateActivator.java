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

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.internal.util.ClassLoaderHelper;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.osgi.*;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import javax.persistence.spi.PersistenceProvider;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Created by farf on 07/02/16.
 */
public class CustomHibernateActivator implements BundleActivator {

        private OsgiClassLoader osgiClassLoader;
        private OsgiServiceUtil osgiServiceUtil;

        private ServiceRegistration persistenceProviderService;
        private ServiceRegistration sessionFactoryService;

        @Override
        @SuppressWarnings("unchecked")
        public void start(BundleContext context) throws Exception {
            // build a ClassLoader that uses all the necessary OSGi bundles, and place it into
            // a well-known location so internals can access it
            osgiClassLoader = new OsgiClassLoader();
            osgiClassLoader.addBundle( FrameworkUtil.getBundle( Session.class ) );
            osgiClassLoader.addBundle( FrameworkUtil.getBundle( CustomPersistenceProvider.class ) );
            ClassLoaderHelper.overridenClassLoader = osgiClassLoader;

            osgiServiceUtil = new OsgiServiceUtil( context );

            // Build a JtaPlatform specific for this OSGi context
            final OsgiJtaPlatform osgiJtaPlatform = new OsgiJtaPlatform( osgiServiceUtil );

            final Dictionary properties = new Hashtable();
            // In order to support existing persistence.xml files, register using the legacy provider name.
            properties.put( "javax.persistence.provider", CustomPersistenceProvider.class.getName() );
            persistenceProviderService = context.registerService(
                    PersistenceProvider.class.getName(),
                    new CustomOsgiPersistenceProviderService( osgiClassLoader, osgiJtaPlatform, osgiServiceUtil ),
                    properties
            );
            sessionFactoryService = context.registerService(
                    SessionFactory.class.getName(),
                    new OsgiSessionFactoryService( osgiClassLoader, osgiJtaPlatform, osgiServiceUtil ),
                    new Hashtable()
            );
        }

        @Override
        public void stop(BundleContext context) throws Exception {
            osgiClassLoader.stop();
            osgiClassLoader = null;
            osgiServiceUtil.stop();
            osgiServiceUtil = null;

            persistenceProviderService.unregister();
            persistenceProviderService = null;
            sessionFactoryService.unregister();
            sessionFactoryService = null;

            ClassLoaderHelper.overridenClassLoader = null;
        }
}
