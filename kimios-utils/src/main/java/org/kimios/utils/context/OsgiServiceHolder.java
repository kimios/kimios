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

package org.kimios.utils.context;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by farf on 23/05/16.
 */
public class OsgiServiceHolder implements ContextHolder {

    private static Logger logger = LoggerFactory.getLogger(OsgiServiceHolder.class);

    public <T> T getService(Class<T> clazz) {
        BundleContext bundleContext = FrameworkUtil.getBundle(ContextHolder.class).getBundleContext();
        //get service access
        ServiceReference<T> reference = bundleContext.getServiceReference(clazz);
        return bundleContext.getService(reference);
    }
}
