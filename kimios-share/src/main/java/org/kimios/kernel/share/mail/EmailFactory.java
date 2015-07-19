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
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import java.util.List;
import java.util.Map;

/**
 * Created by farf on 10/07/15.
 */
public class EmailFactory {


    private String publicUrl;

    private String mailerSender = "Sendlib'";

    private String mailerSenderMail = "fabien.alin@gmail.com";

    private String mailServer = "smtp.googlemail.com";

    private String mailAccount = "fabien.alin";

    private String mailAccountPassword = "150385farfou75018";

    private int mailServerPort = 465;

    private boolean mailServerSsl = true;

    private boolean sendlibDebugMode = false;

    private String sendlibDebugTestAddress = "test@sendlib.com";





    public void getEmailObject(){

    }


    public void getMultipartEmailObject() throws EmailException {
        MultiPartEmail email = new MultiPartEmail();
        email.setHostName(mailServer);
        email.setSmtpPort(mailServerPort);
        email.setAuthenticator(new DefaultAuthenticator(mailAccount, mailAccountPassword));
        email.setSSLOnConnect(mailServerSsl);
    }


}
