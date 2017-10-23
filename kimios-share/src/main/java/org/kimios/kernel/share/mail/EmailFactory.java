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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.*;
import org.kimios.api.templates.ITemplate;
import org.kimios.api.templates.ITemplateProcessor;
import org.kimios.api.templates.ITemplateProvider;
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.dms.model.DocumentVersion;
import org.kimios.kernel.repositories.impl.RepositoryManager;

import javax.activation.FileDataSource;
import java.util.HashMap;
import java.util.Map;

public class EmailFactory implements IEmailFactory {



    private ITemplateProcessor templateProcessor;

    private ITemplateProvider templateProvider;

    private String mailServer = "smtp.googlemail.com";

    private String mailAccount = "my.email";

    private String mailAccountPassword = "password";

    private int mailServerPort = 465;

    private boolean mailServerTls = true;

    private boolean mailServerSsl = true;

    private boolean mailDebug = false;

    private AttachmentNameGenerator attachmentNameGenerator;

    public boolean isMailDebug() {
        return mailDebug;
    }

    public void setMailDebug(boolean mailDebug) {
        this.mailDebug = mailDebug;
    }

    public String getMailServer() {
        return mailServer;
    }

    public void setMailServer(String mailServer) {
        this.mailServer = mailServer;
    }

    public String getMailAccount() {
        return mailAccount;
    }

    public void setMailAccount(String mailAccount) {
        this.mailAccount = mailAccount;
    }

    public String getMailAccountPassword() {
        return mailAccountPassword;
    }

    public void setMailAccountPassword(String mailAccountPassword) {
        this.mailAccountPassword = mailAccountPassword;
    }

    public int getMailServerPort() {
        return mailServerPort;
    }

    public void setMailServerPort(int mailServerPort) {
        this.mailServerPort = mailServerPort;
    }

    public boolean isMailServerSsl() {
        return mailServerSsl;
    }

    public boolean isMailServerTls() {
        return mailServerTls;
    }

    public void setMailServerTls(boolean mailServerTls) {
        this.mailServerTls = mailServerTls;
    }

    public void setMailServerSsl(boolean mailServerSsl) {
        this.mailServerSsl = mailServerSsl;
    }

    public AttachmentNameGenerator getAttachmentNameGenerator() {
        return attachmentNameGenerator;
    }

    public void setAttachmentNameGenerator(AttachmentNameGenerator attachmentNameGenerator) {
        this.attachmentNameGenerator = attachmentNameGenerator;
    }

    public ITemplateProcessor getTemplateProcessor() {
        return templateProcessor;
    }

    public void setTemplateProcessor(ITemplateProcessor templateProcessor) {
        this.templateProcessor = templateProcessor;
    }

    public MultiPartEmail getMultipartEmailObject() throws EmailException {
        MultiPartEmail email = new HtmlEmail();
        email.setHostName(mailServer);
        email.setSmtpPort(mailServerPort);
        if(StringUtils.isNotBlank(mailAccount)) {
            email.setAuthenticator(new DefaultAuthenticator(mailAccount, mailAccountPassword));
        }


        email.setStartTLSEnabled(mailServerTls);
        email.setSSLOnConnect(mailServerSsl);
        email.setDebug(mailDebug);
        return email;
    }


    public MultiPartEmail getEmailObjectFromDescriptor(MailDescriptor descriptor) throws Exception {
        MultiPartEmail email = new HtmlEmail();
        email.setHostName(mailServer);
        email.setSmtpPort(mailServerPort);
        if(StringUtils.isNotBlank(mailAccount)) {
            email.setAuthenticator(new DefaultAuthenticator(mailAccount, mailAccountPassword));
        }

        email.setStartTLSEnabled(mailServerTls);
        email.setSSLOnConnect(mailServerSsl);
        email.setDebug(mailDebug);

        email.setSubject(descriptor.getSubject());
        if(descriptor.getFromName() != null){
            email.setFrom(descriptor.getFrom(), descriptor.getFromName());
        } else {
            email.setFrom(descriptor.getFrom());
        }
        if(descriptor.getMailContent() != null){
            email.setMsg(descriptor.getMailContent());
        } else if(descriptor.getTemplateContent() != null){
            //process
            email.setMsg(templateProcessor.processStringTemplateToString(descriptor.getTemplateContent(), descriptor.getDatas()));
        } else if(descriptor.getTemplateName() != null){
            ITemplate template = templateProvider.loadTemplate(descriptor.getTemplateName());
            email.setMsg(templateProcessor.processTemplateToString(template, descriptor.getDatas()));
        }
        for(String u: descriptor.getRecipients()){
            email.addTo(u);
        }

        return email;
    }



    public void addDocumentVersionAttachment(MultiPartEmail email, Document document, DocumentVersion documentVersion)
        throws  Exception {
        FileDataSource fileDataSource = new FileDataSource(RepositoryManager.directFileAccess(documentVersion));
        String attachmentName = null;
        if(attachmentNameGenerator != null){
            attachmentName = attachmentNameGenerator.generate(documentVersion);
            if(attachmentName == null){
                attachmentName =  document.getPath().substring(document.getPath().lastIndexOf("/") + 1);
            } else {
                //append extension
                if(StringUtils.isNotBlank(document.getExtension())){
                    attachmentName += "." + document.getExtension();
                }
            }
        }
        email.attach(fileDataSource, attachmentName,
                "", EmailAttachment.ATTACHMENT);
    }


}
