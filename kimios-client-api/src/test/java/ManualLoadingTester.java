/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
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

import org.kimios.client.controller.SecurityController;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Properties;

/**
 *
 *
 *
 *
 *
 */
public class ManualLoadingTester {


    public static void main(String[] args) throws Exception {
        Properties prop = new Properties();
        prop.setProperty("server.url", "http://localhost:9595/kimios");
        prop.setProperty("temporaryFilesPath", "");
        prop.setProperty("transfer.chunksize", "10240");
        PropertyPlaceholderConfigurer cfgHolder = new PropertyPlaceholderConfigurer();
        cfgHolder.setIgnoreUnresolvablePlaceholders(true);
        cfgHolder.setBeanName("propResolver");
        cfgHolder.setProperties(prop);
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext();
        ctx.addBeanFactoryPostProcessor(cfgHolder);
        ctx.setConfigLocation("kimios-ctx-client.xml");
        ctx.refresh();



        SecurityController secCtrl = ctx.getBean(SecurityController.class);

        System.out.println(secCtrl.startSession("admin", "kimios", "kimios"));

    }
}
