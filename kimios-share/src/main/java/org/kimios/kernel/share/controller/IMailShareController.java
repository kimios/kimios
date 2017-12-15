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

package org.kimios.kernel.share.controller;

import org.kimios.exceptions.DmsKernelException;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.share.mail.MailDescriptor;
import org.kimios.kernel.share.model.MailContact;
import org.kimios.kernel.share.model.Share;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by farf on 19/07/15.
 */
public interface IMailShareController {
    void sendDocumentByEmail(Session session,
                             List<Share> shares,
                             Map<String, String> recipients,
                             String subject, String content,
                             String senderAddress, String senderName,
                             boolean defaultSender, String password)
        throws DmsKernelException;

    List<MailContact> searchContact(Session session, String searchQuery);

    String loadDefaultMailTemplate(Session session) throws Exception;

    void scheduleMailSend(MailDescriptor mailDescriptor) throws Exception;

    Share createShare(Session session, long entityId, Date expirationDate) throws Exception;

}
