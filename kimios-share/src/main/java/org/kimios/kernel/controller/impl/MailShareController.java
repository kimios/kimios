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

package org.kimios.kernel.controller.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.IMailShareController;
import org.kimios.kernel.dms.Document;
import org.kimios.kernel.dms.DocumentVersion;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DmsKernelException;
import org.kimios.kernel.security.Session;
import org.kimios.kernel.share.factory.MailContactFactory;
import org.kimios.kernel.share.mail.EmailFactory;
import org.kimios.kernel.share.mail.MailTaskRunnable;
import org.kimios.kernel.share.model.MailContact;
import org.kimios.kernel.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Transactional
public class MailShareController extends AKimiosController implements IMailShareController {

    private static Logger logger = LoggerFactory.getLogger(MailShareController.class);

    private String mailerSender = "Kimios'";

    private String mailerSenderMail = "kimios@kimios.org";

    public String getMailerSenderMail() {
        return mailerSenderMail;
    }

    public void setMailerSenderMail(String mailerSenderMail) {
        this.mailerSenderMail = mailerSenderMail;
    }

    public String getMailerSender() {
        return mailerSender;
    }

    public void setMailerSender(String mailerSender) {
        this.mailerSender = mailerSender;
    }

    private EmailFactory emailFactory;

    public EmailFactory getEmailFactory() {
        return emailFactory;
    }

    public void setEmailFactory(EmailFactory emailFactory) {
        this.emailFactory = emailFactory;
    }

    private ScheduledExecutorService scheduledExecutorService;

    private MailContactFactory mailContactFactory;

    public MailContactFactory getMailContactFactory() {
        return mailContactFactory;
    }

    public void setMailContactFactory(MailContactFactory mailContactFactory) {
        this.mailContactFactory = mailContactFactory;
    }

    public MailShareController(){
        scheduledExecutorService = Executors.newScheduledThreadPool(8);
    }

    @Override
    public void sendDocumentByEmail(Session session,
                                    List<Long> documentIds,
                                    Map<String, String> recipients,
                                    String subject, String content,
                                    String senderAddress, String senderName,
                                    boolean defaultSender)
        throws DmsKernelException {

        try {
            logger.info("Submitted recipients: {}", recipients);
            logger.info("Submitted doc ids: {}", documentIds);
            MultiPartEmail email = emailFactory.getMultipartEmailObject();

            for (String emailAddress : recipients.keySet()) {
                email.addBcc(emailAddress, recipients.get(emailAddress));
                mailContactFactory.addContact(emailAddress.toLowerCase(), recipients.get(emailAddress));
            }
            if (StringUtils.isNotBlank(senderAddress)) {
                if (StringUtils.isNotBlank(senderName)) {
                    email.setFrom(senderAddress, senderName);
                } else {
                    email.setFrom(senderAddress);
                }
            } else if( defaultSender && StringUtils.isNotBlank(mailerSenderMail)) {
                email.setFrom(mailerSenderMail, mailerSender);
            } else {
                //send with user's mail address
                User u = authFactoryInstantiator.getAuthenticationSourceFactory().getAuthenticationSource(session.getUserSource())
                            .getUserFactory().getUser(session.getUserName());
                if(StringUtils.isNotBlank(u.getMail())){
                    email.setFrom(u.getMail());
                    email.setBounceAddress(u.getMail());
                }
            }
            email.setSubject(subject);
            email.setMsg(content);
            for(Long documentId: documentIds){
                Document d = dmsFactoryInstantiator.getDocumentFactory()
                        .getDocument(documentId);
                if(getSecurityAgent().isReadable(d, session.getUserName(), session.getUserSource(), session.getGroups())){
                    DocumentVersion dv = dmsFactoryInstantiator.getDocumentVersionFactory()
                            .getLastDocumentVersion(d);
                    emailFactory.addDocumentVersionAttachment(email, d, dv);
                    logger.debug("added document to mail:  {}", d);
                } else {
                    throw new AccessDeniedException();
                }
            }

            scheduledExecutorService.schedule(new MailTaskRunnable(email), 1000, TimeUnit.MILLISECONDS);


        }catch (Exception ex){
            logger.error("error while sharing documen(s)", ex);
        }
    }

    @Override
    public List<MailContact> searchContact(Session session, String searchQuery){
        return mailContactFactory.searchContact(searchQuery);
    }
}
