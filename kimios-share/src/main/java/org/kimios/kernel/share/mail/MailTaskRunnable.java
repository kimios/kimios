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

package org.kimios.kernel.share.mail;

import org.apache.commons.mail.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by farf on 19/07/15.
 */
public class MailTaskRunnable implements Runnable {


    private static Logger logger = LoggerFactory.getLogger(MailTaskRunnable.class);

    private Email email;

    public MailTaskRunnable(Email email) {
        this.email = email;
    }

    @Override
    public void run() {
        try {
            email.send();
        } catch (Exception ex) {
            logger.error("error while sending email", ex);
        }
    }
}
