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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by farf on 6/20/14.
 */
public class KimiosActivator implements BundleActivator {


    private static Logger logger = LoggerFactory.getLogger(KimiosActivator.class);


    private KimiosExtender kimiosExtender;

    public void start(BundleContext context) throws Exception {

        logger.info("Kimios starting");


        int trackStates = Bundle.STARTING | Bundle.STOPPING | Bundle.RESOLVED | Bundle.INSTALLED | Bundle.UNINSTALLED;
        kimiosExtender = new KimiosExtender(context, trackStates, null);
        kimiosExtender.open();

        logger.info("Kimios Extender Loaded");
    }

    public void stop(BundleContext context) throws Exception {

        kimiosExtender.close();
        logger.info("Kimios Extender Closed");
    }
}
