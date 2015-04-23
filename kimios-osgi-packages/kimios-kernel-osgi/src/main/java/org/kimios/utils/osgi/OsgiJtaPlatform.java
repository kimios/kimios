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

//import org.hibernate.service.jta.platform.internal.AbstractJtaPlatform;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

/**
 */
public class OsgiJtaPlatform /*extends AbstractJtaPlatform*/
{


    private static Logger logger = LoggerFactory.getLogger(OsgiJtaPlatform.class);

    protected TransactionManager locateTransactionManager()
    {

        try {



            Bundle b = FrameworkUtil.getBundle(TransactionManager.class);
            TransactionManager tm = null;

            ServiceTracker serviceTracker = new
                    ServiceTracker(b.getBundleContext(),
                    b.getBundleContext().createFilter("(objectClass=javax.transaction.TransactionManager)"),
                    null);

            serviceTracker.open();

            tm = (TransactionManager)
                    serviceTracker.getService();

            logger.info("Loaded Transaction Manager from " + this.getClass().getName() + ". " + tm);
            return tm;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected UserTransaction locateUserTransaction()
    {
        try {

            Bundle b = FrameworkUtil.getBundle(UserTransaction.class);
            UserTransaction tm = null;
            ServiceTracker serviceTracker = new
                    ServiceTracker(b.getBundleContext(),
                    b.getBundleContext().createFilter("(objectClass=javax.transaction.UserTransaction)"),
                    null);

            serviceTracker.open();

            tm = (UserTransaction)
                    serviceTracker.getService();

            logger.info("Loaded UserTransaction from " + this.getClass().getName() + ". " + tm);
            return tm;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
