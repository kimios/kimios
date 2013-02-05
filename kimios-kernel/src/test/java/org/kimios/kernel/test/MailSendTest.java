package org.kimios.kernel.test;/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2013  DevLib'
 *
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.HashMap;

import org.kimios.kernel.dms.Document;
import org.kimios.kernel.mail.MailTemplate;
import org.kimios.kernel.mail.Mailer;
import org.kimios.kernel.user.User;
import org.kimios.kernel.utils.TemplateUtil;
import org.kimios.utils.configuration.ConfigurationManager;
import org.kimios.utils.configuration.PropertiesConfigurationHolder;

/**
 */
public class MailSendTest
{
    public static void main(String[] args) throws Exception
    {

        PropertiesConfigurationHolder holder = new PropertiesConfigurationHolder();
        holder.init(MailSendTest.class.getResourceAsStream("kimios.properties"));

        ConfigurationManager mng = new ConfigurationManager();
        mng.setHolder(holder);


        String templatePath = "/org/kimios/kernel/test/incoming-document-notifier.html";
        HashMap<String, Object> datas = new HashMap<String, Object>();
        Document doc = new Document();
        doc.setName("PDF DOC");
        datas.put("document", doc);
        datas.put("link", "http://kimios.fr");


        MailTemplate mt = new MailTemplate(
                "f@devlib.fr",
                "Sender",
                "fabien.alin@gmail.com", // mailTo: first email
                ConfigurationManager.getValue("TestSubject"),
                TemplateUtil.generateContent(datas, templatePath, "UTF-8"),
                "text/html; charset=utf-8");


        new Mailer(mt).start();

    }
}
