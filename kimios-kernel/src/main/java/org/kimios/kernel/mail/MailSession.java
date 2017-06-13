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

import javax.mail.Session;
import java.util.Properties;

public class MailSession
{
    private static String smtpHost;

    private static String smtpPassword;

    private static String smtpUser;

    private static String smtpPort;

    private static boolean smtpAuth;

    private static boolean smtpTLSAuth;

    public static Session getSession() throws ConfigException
    {
        //loading parameters
        smtpHost = ConfigurationManager.getValue(Config.DEFAULT_SMTP_HOST);
        try {
            smtpPort = ConfigurationManager.getValue(Config.DEFAULT_SMTP_PORT);
        } catch (ConfigException e) {
            smtpPort = "25";
        }
        try {
            smtpUser = ConfigurationManager.getValue(Config.DEFAULT_SMTP_USER);
            smtpPassword = ConfigurationManager.getValue(Config.DEFAULT_SMTP_PASSWORD);
            smtpAuth = smtpUser != null && smtpPassword != null;
        } catch (ConfigException e) {
            smtpAuth = false;
        }
        try {
            smtpTLSAuth = Boolean.parseBoolean(ConfigurationManager.getValue(Config.DEFAULT_SMTP_TLSAUTH));
        } catch (ConfigException e) {
            smtpTLSAuth = false;
        }

        //Creating session
        Authenticator authenticator = null;
        Properties properties = System.getProperties();
        String protocol = (smtpTLSAuth ? "smtps" : "smtp");
        if (smtpAuth) {
            authenticator = new Authenticator(smtpUser, smtpPassword);
            properties.setProperty("mail.smtp.submitter", authenticator.getPasswordAuthentication().getUserName());
        }
        if (smtpTLSAuth) {
            properties.put("mail.smtp.socketFactory.port", smtpPort);
            properties.put("mail.smtp.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.socketFactory.fallback", "true");
            properties.put("mail.smtp.quitwait", "false");
        }
        properties.put("mail." + protocol + ".starttls.enable", Boolean.toString(smtpTLSAuth));
        properties.put("mail.transport.protocol", protocol);
        properties.put("mail." + protocol + ".port", smtpPort);
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.auth", Boolean.toString(smtpAuth));

        Session session = null;

        if (authenticator != null) {
            session = Session.getInstance(properties, authenticator);
        } else {
            session = Session.getDefaultInstance(properties);
        }
        try {
            session.setDebug(Boolean.parseBoolean(ConfigurationManager.getValue(Config.DEFAULT_SMTP_DEBUG)));
        } catch (Exception e) {
            session.setDebug(false);
        }
        return session;
    }
}

