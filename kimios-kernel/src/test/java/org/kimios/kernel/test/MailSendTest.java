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

import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.mail.MailTemplate;
import org.kimios.kernel.mail.Mailer;
import org.kimios.kernel.utils.TemplateUtil;
import org.kimios.utils.configuration.ConfigurationManager;
import org.kimios.utils.configuration.PropertiesConfigurationHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;

/**
 */
public class MailSendTest
{
    public static void main(String[] args) throws Exception
    {


        ClassPathXmlApplicationContext cl = new ClassPathXmlApplicationContext("/org/kimios/kernel/test/config-spring.xml");
        cl.start();



        Toto hop = cl.getBean(Toto.class);


        System.out.println(hop);







    }
}
