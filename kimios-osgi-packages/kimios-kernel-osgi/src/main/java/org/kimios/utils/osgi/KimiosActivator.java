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

import org.osgi.framework.*;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;

/**
 * Created by farf on 6/20/14.
 */
public class KimiosActivator implements BundleActivator {


    private static Logger logger = LoggerFactory.getLogger(KimiosActivator.class);


    private KimiosExtender kimiosExtender;


    private static String CONFIG_PID = "org.kimios.server.app";


    private ServiceRegistration configUpdateServiceRegistration;

    public void start(BundleContext context) throws Exception {

        logger.info("Kimios starting");


        Hashtable<String, Object> properties = new Hashtable<String, Object>();
        properties.put(Constants.SERVICE_PID, CONFIG_PID);
        configUpdateServiceRegistration = context.registerService(ManagedService.class.getName(),
                new KimiosConfigUpdater(), properties);



        int trackStates = Bundle.STARTING | Bundle.STOPPING | Bundle.RESOLVED | Bundle.INSTALLED | Bundle.UNINSTALLED;
        kimiosExtender = new KimiosExtender(context, trackStates, null);
        kimiosExtender.open();
        logger.info("Kimios Extender Loaded");
    }

    public void stop(BundleContext context) throws Exception {

        kimiosExtender.close();
        logger.info("Kimios Extender Closed");

        if(configUpdateServiceRegistration != null){
            configUpdateServiceRegistration.unregister();
        }
    }
}
