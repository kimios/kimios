/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2018  DevLib'
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

package org.kimios.deployer.core;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CommandLineInstaller
{
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {

        // * load custom conf file *//
        if (args.length < 1) {
            System.err.println("No parameters file provided.");
            return;
        }

        String deploymentType = args[0];
        URI baseUri = CommandLineInstaller.class.getResource(
                "/custom-conf.properties").toURI();
        String baseUriStr = baseUri.toString();
        String trunkBaseUriStr = baseUriStr.substring(0,
                baseUriStr.lastIndexOf("/"));
        System.out.println("Base URI " + baseUriStr + " | " + trunkBaseUriStr);
        InstallerCore installer = new InstallerCore(trunkBaseUriStr,
                "D:/tmpinst", true, null, null);

        // load default parameters
        Properties parameters = new Properties();
        parameters.load(CommandLineInstaller.class
                .getResourceAsStream("/custom-conf.properties"));

        Map<String, String> params = new HashMap<String, String>();
        for (Object item : parameters.keySet()) {
            String param = item.toString();
            String value = parameters.getProperty(item.toString());
            params.put(param, value);
        }

        String[] pr = params.keySet().toArray(new String[]{ });
        if (deploymentType.equalsIgnoreCase("env")) {
            String prefix = "KIMIOS_";
            prefix = args[1] != null && args[1].startsWith("-p") ? args[1].substring(args[1].indexOf("p") + 1) : prefix;
            System.out.println("Loading from env with prefix " + prefix);
            Map<String, String> envParam = System.getenv();
            for (String it : pr) {
                String value = envParam.get(prefix + it);
                if (value != null) {
                    params.put(it, value);
                }
            }
        } else if (deploymentType.equalsIgnoreCase("file")) {
            //file load
            String fileName = args[1];
            System.out.println("Loading from file " + fileName);
            if (fileName == null) {
                System.out.println("No file provided.");
                return;
            }
            try {
                FileInputStream f = new FileInputStream(new File(fileName));
                parameters = new Properties();
                parameters.load(f);
            } catch (Exception e) {
                System.out.println("Error While loading conf file "
                        + e.getMessage());
                return;
            }

            for (Object item : parameters.keySet()) {
                String param = item.toString();
                String value = parameters.getProperty(item.toString());
                params.put(param, value);
            }
        } else {
            System.out.println("No deployment type given. Shoudl be 'env' or 'file'");
            return;
        }

        for (String item : params.keySet()) {
            String param = item.toString();
            String value = params.get(item);

            Field f = InstallerCore.class.getDeclaredField(param);
            f.setAccessible(true);
            if (f.getType().equals(Boolean.class)
                    || f.getType().equals(boolean.class))
            {
                f.setBoolean(installer, Boolean.parseBoolean(value));
            } else {
                f.set(installer, value);
            }
            System.out.println(" >>> " + param + " " + value);
        }
        System.out.println("Starting deployment ...");
        installer.checkDatabase();
        installer.checkRepository();
        installer.defineIndexLanguage();
        installer.installDMS();
        System.out.println("Done.");
    }
}
