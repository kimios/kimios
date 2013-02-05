/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
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
package org.kimios.kernel.mail;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mailer extends Thread
{
    private static Logger log = LoggerFactory.getLogger(Mailer.class);

    private MailTemplate mesg;

    public Mailer(MailTemplate mt)
    {
        this.mesg = mt;
        this.setName("Mail ID " + mesg.hashCode());
    }

    @Override
    public void run()
    {
        try {
            Session session = MailSession.getSession();
            Message msg = mesg.getCompiledMessage(session);
            Transport.send(msg);
        } catch (Exception ex) {
            log.error("Mailer error on " + this.getName(), ex);
        }
    }
}

