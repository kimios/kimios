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

import org.kimios.utils.spring.ApplicationContextProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.transaction.TransactionManager;

public class TransactionHelper
{

    private static Logger logger = LoggerFactory.getLogger(TransactionHelper.class);

    private TransactionManager tm = null;
    private PlatformTransactionManager ptm = null;

    public TransactionHelper(){
        //check if osgi mode
        try {
            Bundle b = FrameworkUtil.getBundle(TransactionManager.class);
            ServiceTracker serviceTracker = new
                    ServiceTracker(b.getBundleContext(),
                    b.getBundleContext().createFilter("(objectClass=javax.transaction.TransactionManager)"),
                    null);
            serviceTracker.open();
            tm = (TransactionManager)
                    serviceTracker.getService();
        } catch (Throwable ex){
            // not osgi mode, instead use spring context
            ptm =  ApplicationContextProvider.loadBean(PlatformTransactionManager.class);
        }
    }

    public boolean isRunningInTransaction(){
        try {
            int txStatus = tm.getStatus();
            return txStatus == 0;
        }catch (Exception ex){
            return TransactionSynchronizationManager.isActualTransactionActive();
        }
    }



    public Object startNew(Integer timeout) throws Exception
    {
        if(tm != null){
            tm.begin();
            return null;
        } else {
            return ptm.getTransaction(null);
        }
    }

    public void commit(Object o) throws Exception
    {
        if(tm != null){
            tm.commit();
        } else {
            ptm.commit((TransactionStatus)o);
        }
    }

    public void rollback(Object o) throws Exception
    {
        if(tm != null){
            tm.rollback();
        } else {
            ptm.rollback((TransactionStatus)o);
        }
    }

    public static int displayCurrentTxTimeout()
    {
        return -1;
    }
}

