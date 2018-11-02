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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class ConfTemplateParser
{

    private static Logger log = LoggerFactory.getLogger(ConfTemplateParser.class);

    public Map<String, ConfElement> parse(InputStream stream, Properties properties) throws Exception
    {

        BufferedReader br = new BufferedReader(
                new InputStreamReader(stream));
        String line = null;
        StringBuffer buffer = new StringBuffer();
        while ((line = br.readLine()) != null) {
            buffer.append(line + "\n");
        }
        br.close();

        /*
           Parse line
        */
        Pattern pattern = Pattern.compile(
                "#\\s?Setting\\sitem\\s?:\\s?(.+)\\n#\\s?Description\\s?:(.+)?\\n#\\s?Name\\s?:\\s?(.+)");
        Matcher m = pattern.matcher(buffer);

        Map<String, ConfElement> elementMap = new HashMap<String, ConfElement>();
        while (m.find()) {
            log.info("Parsing " + m.group(3) + " ==> " + m.group(1));
            ConfElement element = new ConfElement();
            element.setDescription(m.group(2));
            element.setTechnicalName(m.group(3));
            element.setLabel(m.group(1));
            try{
                element.setDefaultValue(properties.getProperty(element.getTechnicalName()).toString());
                elementMap.put(element.getTechnicalName(), element);
            } catch (Exception e){
                log.error("No default value found");
            }
        }

        return elementMap;
    }
}
