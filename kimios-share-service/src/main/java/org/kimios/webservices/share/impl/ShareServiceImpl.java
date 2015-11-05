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

package org.kimios.webservices.share.impl;

import org.kimios.kernel.share.controller.IMailShareController;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.share.model.MailContact;
import org.kimios.webservices.IServiceHelper;
import org.kimios.webservices.exceptions.DMServiceException;
import org.kimios.webservices.share.ShareService;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by farf on 19/07/15.
 */

@WebService(targetNamespace = "http://kimios.org", serviceName = "ShareService")
public class ShareServiceImpl implements ShareService {


    private IMailShareController mailShareController;
    private IServiceHelper helper;

    public ShareServiceImpl(IMailShareController mailShareController, IServiceHelper serviceHelper){
        this.helper = serviceHelper;
        this.mailShareController = mailShareController;
    }

    @Override
    public void shareByEmail(String sessionId,
                             List<Long> documentIds,
                             Map<String, String> recipients,
                             String subject, String content,
                             String senderAddress, String senderName,
                             Boolean defaultSender) throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);
            mailShareController.sendDocumentByEmail(session, documentIds, recipients, subject,
                    content, senderAddress, senderName, defaultSender);

        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }

    @Override
    @WebMethod(exclude = true)
    public void shareByEmailFullContact(String sessionId, List<Long> documentIds, List<MailContact> recipients, String subject,
                             String content, String senderAddress, String senderName, Boolean defaultSender)
            throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);

            Map<String, String> recipientsData = new HashMap<String, String>();
            for(MailContact mc: recipients){
                recipientsData.put(mc.getEmailAddress(), mc.getFullName());
            }
            mailShareController.sendDocumentByEmail(session, documentIds, recipientsData, subject,
                    content, senderAddress, senderName, defaultSender);

        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }

    @Override
    public List<MailContact> searchContact(String sessionId, String query) throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);
            return mailShareController.searchContact(session, query);
        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }
}
