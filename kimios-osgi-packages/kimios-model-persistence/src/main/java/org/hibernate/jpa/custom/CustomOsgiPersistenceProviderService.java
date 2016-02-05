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
import org.hibernate.osgi.OsgiPersistenceProviderService;
import org.hibernate.osgi.OsgiServiceUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;

/**
 * Created by farf on 07/02/16.
 */
public class CustomOsgiPersistenceProviderService extends OsgiPersistenceProviderService {

    private OsgiClassLoader osgiClassLoader;
    private OsgiJtaPlatform osgiJtaPlatform;
    private OsgiServiceUtil osgiServiceUtil;



    public CustomOsgiPersistenceProviderService(OsgiClassLoader osgiClassLoader, OsgiJtaPlatform osgiJtaPlatform, OsgiServiceUtil osgiServiceUtil) {
        super(osgiClassLoader, osgiJtaPlatform, osgiServiceUtil);

        this.osgiClassLoader = osgiClassLoader;
        this.osgiJtaPlatform = osgiJtaPlatform;
        this.osgiServiceUtil = osgiServiceUtil;
    }


    @Override
    public Object getService(Bundle requestingBundle, ServiceRegistration registration) {
        return new CustomPersistenceProvider(osgiClassLoader, osgiJtaPlatform, osgiServiceUtil, requestingBundle);
    }

    @Override
    public void ungetService(Bundle requestingBundle, ServiceRegistration registration, Object service) {
        // ?
    }
}
