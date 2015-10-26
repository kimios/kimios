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

package org.kimios.client.controller;

import org.kimios.client.exception.ExceptionHelper;
import org.kimios.kernel.share.model.MailContact;
import org.kimios.kernel.ws.pojo.DMEntity;
import org.kimios.kernel.ws.pojo.DMEntityAttribute;
import org.kimios.webservices.ExtensionService;
import org.kimios.webservices.exceptions.DMServiceException;
import org.kimios.webservices.share.ShareService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShareController {

    private ShareService client;

    public ShareService getClient() {
        return client;
    }

    public void setClient(ShareService client) {
        this.client = client;
    }

    public List<MailContact> listContacts(String sessionId, String query) throws
            Exception {
        try {
            return client.searchContact(sessionId, query);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }


    public void sendDocuments(String sessionId, List<Long> documentIds,
                              Map<String, String> recipients,
                              String subject, String content,
                              String senderAddress, String senderName, Boolean defaultSender) throws Exception {
        try{

            List<MailContact> mc = new ArrayList<MailContact>();
            for(String u: recipients.keySet()){
                MailContact m = new MailContact();
                m.setFullName(recipients.get(u));
                m.setEmailAddress(u);
                mc.add(m);
            }

            client.shareByEmailFullContact(sessionId, documentIds, mc, subject,
                    content, senderAddress, senderName, defaultSender );
        }   catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }
}
