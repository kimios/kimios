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

package org.kimios.kernel.share.controller.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.MultiPartEmail;
import org.kimios.api.events.annotations.DmsEvent;
import org.kimios.api.events.annotations.DmsEventName;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.configuration.Config;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.IFileTransferController;
import org.kimios.kernel.filetransfer.model.DataTransfer;
import org.kimios.kernel.share.controller.IMailShareController;
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.dms.model.DocumentVersion;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.exceptions.DmsKernelException;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.share.factory.MailContactFactory;
import org.kimios.kernel.share.mail.EmailFactory;
import org.kimios.kernel.share.mail.MailTaskRunnable;
import org.kimios.kernel.share.model.MailContact;
import org.kimios.kernel.user.FactoryInstantiator;
import org.kimios.kernel.user.model.User;
import org.kimios.api.templates.ITemplateProcessor;
import org.kimios.api.templates.ITemplateProvider;
import org.kimios.api.templates.ITemplate;
import org.kimios.api.templates.TemplateType;
import org.kimios.utils.configuration.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


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

    private ITemplateProvider templateProvider;

    private ITemplateProcessor templateProcessor;

    private IFileTransferController fileTransferController;

    public ITemplateProvider getTemplateProvider() {
        return templateProvider;
    }

    public void setTemplateProvider(ITemplateProvider templateProvider) {
        this.templateProvider = templateProvider;
    }

    public ITemplateProcessor getTemplateProcessor() {
        return templateProcessor;
    }

    public void setTemplateProcessor(ITemplateProcessor templateProcessor) {
        this.templateProcessor = templateProcessor;
    }

    public IFileTransferController getFileTransferController() {
        return fileTransferController;
    }

    public void setFileTransferController(IFileTransferController fileTransferController) {
        this.fileTransferController = fileTransferController;
    }

    @Override
    @DmsEvent(eventName = DmsEventName.DOCUMENT_SHARED)
    public void sendDocumentByEmail(Session session,
                                    List<Long> documentIds,
                                    Map<String, String> recipients,
                                    String subject, String content,
                                    String senderAddress, String senderName,
                                    boolean defaultSender)
        throws DmsKernelException {

        try {
            if(logger.isDebugEnabled()) {
                logger.debug("Submitted recipients: {}", recipients);
                logger.debug("Submitted doc ids: {}", documentIds);
                logger.debug("Submitted contents is: {}", content);
            }
            MultiPartEmail email = emailFactory.getMultipartEmailObject();

            for (String emailAddress : recipients.keySet()) {
                email.addTo(emailAddress, recipients.get(emailAddress));
                mailContactFactory.addContact(emailAddress.toLowerCase(), recipients.get(emailAddress));
            }

            String copyToMailAddress = ConfigurationManager.getValue("dms.share.mail.copy.to");
            if(StringUtils.isNotBlank(copyToMailAddress)){
                email.addCc(copyToMailAddress);
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

            /*
                Build Link List only if attachment shouldn't be built inside !
             */
            String finalContent = content;

            if(StringUtils.isBlank(content)){
                content = loadDefaultMailTemplate(session);
            }
            if(content.contains("__DOCUMENTSLINKS__")){
                User user = FactoryInstantiator.getInstance()
                        .getAuthenticationSourceFactory().getAuthenticationSource(session.getUserSource())
                        .getUserFactory().getUser(session.getUid());
                Map<String, Object> items = new HashMap<String, Object>();
                //Generate Document Links
                for(Long docId: documentIds){
                    Document doc = dmsFactoryInstantiator.getDocumentFactory()
                            .getDocument(docId);
                    if(getSecurityAgent().isReadable(doc, session.getUserName(), session.getUserSource(), session.getGroups())) {
                        DocumentVersion lastVersion = dmsFactoryInstantiator.getDocumentVersionFactory()
                                .getLastDocumentVersion(doc);
                        DataTransfer transfer = fileTransferController.startDownloadTransactionToken(session,
                                lastVersion.getUid());
                        items.put(ConfigurationManager.getValue(Config.PUBLIC_URL)
                                + "services/rest/filetransfer/downloadDocumentByToken?token="
                                + transfer.getDownloadToken(),
                                doc);
                    }else
                        throw new AccessDeniedException();
                }
                finalContent = processShareNotify(finalContent, user, new ArrayList<User>(), items);
            } else {
                long countSize = 0;
                long maxAttachmentSize = 10000 * 1024;;
                String val = ConfigurationManager.getValue("dms.share.max.attachment.size");
                if(StringUtils.isNotBlank(val)){
                    try{
                        maxAttachmentSize = Long.parseLong(val);
                    }   catch (Exception ex){
                        logger.error("max attachment size not defined as integer. will use default {}", 10);
                    }
                }
                for(Long documentId: documentIds){
                    Document d = dmsFactoryInstantiator.getDocumentFactory()
                            .getDocument(documentId);

                    if(getSecurityAgent().isReadable(d, session.getUserName(), session.getUserSource(), session.getGroups())){
                        DocumentVersion dv = dmsFactoryInstantiator.getDocumentVersionFactory()
                                .getLastDocumentVersion(d);
                        emailFactory.addDocumentVersionAttachment(email, d, dv);
                        logger.debug("added document to mail:  {}", d);
                        countSize += dv.getLength();
                        if(countSize > maxAttachmentSize){
                            throw new ConfigException("MaxAttachmentSizeReached");
                        }
                    } else {
                        throw new AccessDeniedException();
                    }
                }
            }

            email.setMsg(finalContent);

            scheduledExecutorService.schedule(new MailTaskRunnable(email), 1000, TimeUnit.MILLISECONDS);


        }catch (Exception ex){
            logger.error("error while sharing documen(s)", ex);
            if(ex instanceof DmsKernelException){
                throw (DmsKernelException)ex;
            } else {
                throw new DmsKernelException(ex);
            }
        }
    }

    @Override
    public List<MailContact> searchContact(Session session, String searchQuery){
        return mailContactFactory.searchContact(searchQuery);
    }


    private String processShareNotify(String template, User user,
                                      List<User> recipients,
                                      Map<String, Object> links) throws Exception {


        String tplContent = null;

        if(template != null){
            tplContent = template;
        } else {
            ITemplate mailTemplate = templateProvider.getDefaultTemplate(TemplateType.SHARE_MAIL);
            tplContent = mailTemplate.getContent();
        }

        //Generate final mail content
        InputStream inputStream = MailShareController.class.getClassLoader().getResourceAsStream("templates/default-documents-links.html");
        String forEachTemplate = Pattern.quote(IOUtils.toString(inputStream));
        String finalContent = tplContent.replaceAll("__DOCUMENTSLINKS__", forEachTemplate);

        Map<String, Object> items = new HashMap<String, Object>();
        items.put("sender", user);
        items.put("recipients", recipients);
        items.put("links", links);

        return templateProcessor.processStringTemplateToString(finalContent, items);
    }



    public String loadDefaultMailTemplate(Session session) throws Exception {
        ITemplate mailTemplate = templateProvider.getDefaultTemplate(TemplateType.SHARE_MAIL);
        if(mailTemplate != null){
            return mailTemplate.getContent();
        } else {
            return "<html><body>__DOCUMENTSLINKS__</body></html>";
        }
    }


}
