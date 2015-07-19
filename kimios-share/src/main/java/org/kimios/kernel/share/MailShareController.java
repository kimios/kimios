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

package org.kimios.kernel.share;

import org.apache.commons.mail.Email;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DmsKernelException;
import org.kimios.kernel.security.Session;
import org.kimios.kernel.share.mail.EmailFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by farf on 10/07/15.
 */
public class MailShareController {




    public void sendDocumentByEmail(Session session, List<Long> documentIds, Map<String, String> recipients)
        throws DmsKernelException {


        Email email = new EmailFactory().getMultipartEmailObject();

        for(String emailAddress: recipients.keySet()){
            email.addTo(emailAddress, recipients.get(emailAddress));
        }

        if(senderAddress != null){
            if(senderName != null){
                email.setFrom(senderAddress, senderName);
            } else {
                email.setFrom(senderAddress);
            }
        } else
            email.setFrom(mailerSenderMail, mailerSender);

        email.setSubject("The logo");
        email.setMsg("Here is Apache's logo");


    }



}
