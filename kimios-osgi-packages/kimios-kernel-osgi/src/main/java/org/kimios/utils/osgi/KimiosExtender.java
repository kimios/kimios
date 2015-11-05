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

package org.kimios.utils.osgi;

import org.kimios.utils.extension.ExtensionRegistryManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;

/**
 * Created by farf on 6/20/14.
 */
public class KimiosExtender extends BundleTracker {


    private static Logger logger = LoggerFactory.getLogger(KimiosExtender.class);

    public KimiosExtender(BundleContext context, int stateMask, BundleTrackerCustomizer customizer) {
        super(context, stateMask, customizer);
        //load registry
        ExtensionRegistryManager.init();
    }

    @Override
    public Object addingBundle(Bundle bundle, BundleEvent event) {
        //eturn super.addingBundle(bundle, event);
        Enumeration<String> items = bundle.getHeaders().keys();
        while (items.hasMoreElements()) {
            String k = items.nextElement();
            if (k.startsWith("Kimios-")) {
                // put in registry
                logger.info("found header {} ==> {}", k, bundle.getHeaders().get(k));
                String className = (String) bundle.getHeaders().get(k);
                if (className != null) {
                    Class<?> clazz;
                    try {
                        clazz = bundle.loadClass(className);

                        Class<?> serviceClass = null;

                        Class<?> toCheckClass = clazz;
                        while(toCheckClass != null){
                            if(toCheckClass.getInterfaces().length == 0)
                                toCheckClass = toCheckClass.getSuperclass();
                            else {
                                //check if
                                Class<?> interfaceClass = toCheckClass.getInterfaces()[0];
                                /*if(interfaceClass.equals(MetaFeed.class)
                                        || interfaceClass.equals(Converter.class)) {
                                    serviceClass = interfaceClass;
                                    break;
                                } else {
                                    toCheckClass = toCheckClass.getSuperclass();
                                }*/
                            }
                        }

                        if(serviceClass != null) {
                            logger.info("registering {} as service for type {}", clazz, serviceClass, bundle);
                            bundle.getBundleContext().registerService(serviceClass.getName(), clazz.newInstance(), null);
                        }

                        logger.info("Kimios Extender Found item : "
                                    + clazz.getName() + ". will be put in registry");

                        ExtensionRegistryManager.addClass(clazz);


                    } catch (ClassNotFoundException e) {
                        logger.error("Could not find class " + className, e);
                    }
                    catch (Exception e) {
                        logger.error("extender exception fro " + className, e);
                    }
                }
            }
        }
        return bundle;
    }

    @Override
    public void modifiedBundle(Bundle bundle, BundleEvent event, Object object) {
        super.modifiedBundle(bundle, event, object);
    }

    @Override
    public void removedBundle(Bundle bundle, BundleEvent event, Object object) {
        super.removedBundle(bundle, event, object);
    }
}
