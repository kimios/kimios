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
package org.kimios.kernel.index;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

import javax.transaction.TransactionManager;

public class OsgiTransactionHelper
{


    public TransactionManager loadTxManager() throws Exception {
        Bundle b = FrameworkUtil.getBundle(TransactionManager.class);
        TransactionManager tm = null;

        ServiceTracker serviceTracker = new
                ServiceTracker(b.getBundleContext(),
                b.getBundleContext().createFilter("(objectClass=javax.transaction.TransactionManager)"),
                null);

        serviceTracker.open();

        tm = (TransactionManager)
                serviceTracker.getService();

        return tm;
    }

    public void startNew(Integer timeout) throws Exception
    {


        TransactionManager txManager = loadTxManager();
        txManager.begin();
        return;

    }

    public void commit() throws Exception
    {
        loadTxManager().commit();


    }

    public void rollback() throws Exception
    {
        loadTxManager().rollback();
    }

    public static int displayCurrentTxTimeout()
    {
        return -1;
    }
}

