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
import org.kimios.utils.configuration.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MailTemplate
{
    private String mailFrom;

    private String mailSubject;

    private String mailBody;

    private String mimeType;

    private String senderName;

    private List<File> attachments = new ArrayList<File>();

    private Logger log = LoggerFactory.getLogger(MailTemplate.class);

    private Vector<String> to = new Vector<String>();

    private Vector<String> bcc = new Vector<String>();

    private Vector<String> cc = new Vector<String>();

    public void addTo(String to)
    {
        this.to.add(to);
    }

    public void addBCC(String bcc)
    {
        this.bcc.add(bcc);
    }

    public void cc(String bcc)
    {
        this.cc.add(bcc);
    }

    public void addAttachment(File file) throws FileNotFoundException
    {
        if (file.exists() && file.isFile()) {
            this.attachments.add(file);
        } else {
            throw new FileNotFoundException();
        }
    }

    public MailTemplate(String mailFrom, String mailTo, String mailSubject,
            String mailBody, String mimeType)
    {
        this.mailBody = mailBody;
        this.to.add(mailTo);
        this.mailFrom = mailFrom;
        this.mailSubject = mailSubject;
        this.mimeType = mimeType;
    }

    public MailTemplate(String mailFrom, String senderName, String mailTo,
            String mailSubject, String mailBody, String mimeType)
    {
        this.mailBody = mailBody;
        this.to.add(mailTo);
        this.mailFrom = mailFrom;
        this.mailSubject = mailSubject;
        this.mimeType = mimeType;
        this.senderName = senderName;
    }

    public Message getCompiledMessage(Session session)
    {
        try {
            String testEmail = ConfigurationManager.getValue("dms.mail.test");
            if (testEmail != null && testEmail.length() > 0) {
                this.to.clear();
                this.to.add(testEmail);
                bcc = new Vector<String>();
                cc = new Vector<String>();
            }
        } catch (ConfigException e) {
            log.error("No config defined");
        }

        try {
            MimeMessage mesg = null;
            Multipart multipart = null;

            mesg = new MimeMessage(session);
            //sender
            InternetAddress sender = new InternetAddress(this.mailFrom);
            if (senderName != null) {
                sender.setPersonal(this.senderName);
            }
            mesg.setFrom(sender);
            mesg.setSubject(MimeUtility.encodeText(this.mailSubject, "UTF-8", "Q"));
            for (String t : this.to) {
                mesg.addRecipient(Message.RecipientType.TO, new InternetAddress(t));
            }

            for (String bc : this.bcc) {
                mesg.addRecipient(Message.RecipientType.BCC,
                        new InternetAddress(bc));
            }
            for (String cc : this.cc) {
                mesg.addRecipient(Message.RecipientType.CC,
                        new InternetAddress(cc));
            }

            if (attachments.size() > 0) {
                //attachment
                multipart = new MimeMultipart();
                //process the body
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setContent(this.mailBody.getBytes("UTF-8"), this.mimeType);
                multipart.addBodyPart(messageBodyPart);
                for (File attach : attachments) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(attach);
                    attachmentPart.setDataHandler(new DataHandler(source));
                    attachmentPart.setFileName(attach.getName());
                    multipart.addBodyPart(attachmentPart);
                }
            }

            if (attachments.size() == 0 || multipart == null) {
                mesg.setDataHandler(
                        new DataHandler(new ByteArrayDataSource(this.mailBody.getBytes("UTF-8"), this.mimeType)));
            } else {
                mesg.setContent(multipart);
            }
            return mesg;
        } catch (IOException e) {
            log.error("MailTemplate IO Exception", e);
            return null;
        } catch (AddressException e) {
            log.error("MailTemplate Address Excepttion", e);
            return null;
        } catch (MessagingException e) {
            log.error("MailTemplate Message Excepttion", e);
            return null;
        }
    }
}

