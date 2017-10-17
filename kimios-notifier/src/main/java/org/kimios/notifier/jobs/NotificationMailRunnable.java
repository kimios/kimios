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

package org.kimios.notifier.jobs;

import org.apache.commons.mail.Email;
import org.kimios.kernel.notification.model.NotificationStatus;
import org.kimios.kernel.share.mail.MailTaskRunnable;
import org.kimios.notifier.factory.NotificationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by farf on 19/07/15.
 */
public class NotificationMailRunnable extends MailTaskRunnable {


    private static Logger logger = LoggerFactory.getLogger(NotificationMailRunnable.class);

    private Email email;
    private NotificationFactory notificationFactory;
    private long notificationId;

    public NotificationMailRunnable(Email email, NotificationFactory factory, long notificationId) {
        super(email);
        this.notificationFactory = factory;
        this.notificationId = notificationId;
    }

    @Override
    public void run() {
        try {
            super.run();
        } catch (Exception e) {
            logger.error("error while sending email", e);
            return;
        }
        this.notificationFactory.changeNotificationStatus(notificationId, NotificationStatus.SENT);
    }
}
