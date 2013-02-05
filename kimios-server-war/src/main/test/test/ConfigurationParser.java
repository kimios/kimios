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

package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA. User: farf Date: 10/7/12 Time: 4:24 PM To change this template use File | Settings | File
 * Templates.
 */
public class ConfigurationParser
{
    public static void main(String[] items) throws Exception
    {

        BufferedReader br = new BufferedReader(
                new InputStreamReader(ConfigurationParser.class.getResourceAsStream("/kimios.properties")));

        String line = null;

        StringBuffer buffer = new StringBuffer();
        while ((line = br.readLine()) != null) {
            buffer.append(line + "\n");
        }

        br.close();

        /*
            Parse line
         */
        System.out.println(buffer);       //(#\sSetting\sitem\s:\s)(.+)\r(   (#\sName\s:\s)(.+)
        Pattern pattern = Pattern.compile("#\\s?Setting\\sitem\\s?:\\s?(.+)\\n#\\s?Description\\s?:(.+)?\\n#\\s?Name\\s?:\\s?(.+)");
        Matcher m = pattern.matcher(buffer);
        MatchResult result = m.toMatchResult();
        System.out.println(m.matches() + " " + result.groupCount());

        Properties p = new Properties();

        p.load(ConfigurationParser.class.getResourceAsStream("/kimios.properties"));

        while (m.find()) {

            System.out.println("Property " + m.group(1) + " has value: " +
                            p.get(m.group(3)));
        }
    }
}
