/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2012  DevLib'
 *
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.utils.spring;

import javax.transaction.UserTransaction;

import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class TransactionHelper
{
    public void startNew(Integer timeout) throws Exception
    {
        JtaTransactionManager txMngr = ApplicationContextProvider.loadBean(JtaTransactionManager.class);
        UserTransaction userTransaction = txMngr.getUserTransaction();
        if (timeout != null) {
            userTransaction.setTransactionTimeout(timeout);
        }
        userTransaction.begin();
    }

    public void commit() throws Exception
    {
        JtaTransactionManager txMngr = ApplicationContextProvider.loadBean(JtaTransactionManager.class);
        UserTransaction userTransaction = txMngr.getUserTransaction();
        userTransaction.commit();
    }

    public void rollback() throws Exception
    {
        JtaTransactionManager txMngr = ApplicationContextProvider.loadBean(JtaTransactionManager.class);
        UserTransaction userTransaction = txMngr.getUserTransaction();
        userTransaction.rollback();
    }

    public static int displayCurrentTxTimeout()
    {
        JtaTransactionManager txMngr = ApplicationContextProvider.loadBean(JtaTransactionManager.class);
        return txMngr.getDefaultTimeout();
    }
}

