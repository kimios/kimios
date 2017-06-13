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
package org.kimios.kernel.mail;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.configuration.Config;
import org.kimios.utils.configuration.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;

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
            String smtpPassword = "";
            String smtpUser = "";
            boolean smtpAuth = true;
            try {
                smtpUser = ConfigurationManager.getValue(Config.DEFAULT_SMTP_USER);
                smtpPassword = ConfigurationManager.getValue(Config.DEFAULT_SMTP_PASSWORD);
                smtpAuth = smtpUser != null && smtpPassword != null;
            } catch (ConfigException e) {
                smtpAuth = false;
            }
            if(smtpAuth){
                Transport.send(msg, smtpUser, smtpPassword);
            } else {
                Transport.send(msg);
            }
        } catch (Exception ex) {
            log.error("Mailer error on " + this.getName(), ex);
        }
    }
}

