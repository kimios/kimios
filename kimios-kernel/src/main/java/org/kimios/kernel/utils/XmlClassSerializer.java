/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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
package org.kimios.kernel.utils;

import org.kimios.kernel.exception.XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XmlClassSerializer
{
    public static String getXmlDescriptor(String className)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        Class c = Class.forName(className);
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        xml += "<xmldesc name=\"" + c.getName() + "\">\n";
        xml += "<parameters>";
        for (Field field : c.getDeclaredFields()) {
            xml += "<parameter name=\"" + field.getName() + "\" ";
            xml += "type=\"" + field.getType().getName() + "\" ";
            ClassGeneric cg = field.getAnnotation(ClassGeneric.class);
            if (cg != null) {
                xml += "list-type=\"" + cg.classType().getName() + "\" ";
            }
            xml += "value=\"\"/>\n";
        }
        xml += "</parameters>";
        xml += "</xmldesc>\n";
        return xml;
    }

    private static boolean checkIfUnknownParam(String paramName, Class c)
    {
        try {
            c.getDeclaredField(paramName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Map<String, Serializable> getValuesFromXml(String xmlStream, String className)
            throws Exception, ClassNotFoundException, InstantiationException, IllegalAccessException
    {

        Class c = Class.forName(className);

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                new ByteArrayInputStream(xmlStream.getBytes()));
        Element root = doc.getDocumentElement();
        NodeList params = root.getElementsByTagName("parameter");

        // Check unknown parameter names
        for (int i = 0; i < params.getLength(); i++) {
            NamedNodeMap map = params.item(i).getAttributes();
            if (!checkIfUnknownParam(map.getNamedItem("name").getNodeValue(), c)) {
                throw new XMLException();
            }
        }

        // Check already set parameter names
        List<String> savedParams = new ArrayList<String>();
        for (int i = 0; i < params.getLength(); i++) {
            NamedNodeMap map = params.item(i).getAttributes();
            String paramName = map.getNamedItem("name").getNodeValue();
            if (savedParams.contains(paramName)) {
                throw new XMLException();
            }
            savedParams.add(paramName);
        }

        for (int i = 0; i < params.getLength(); i++) {
            NamedNodeMap map = params.item(i).getAttributes();
            for (Field field : c.getDeclaredFields()) {
                String paramName = map.getNamedItem("name").getNodeValue();
                String paramType = map.getNamedItem("type").getNodeValue();
                String paramValue = map.getNamedItem("value").getNodeValue();
                String paramListType = map.getNamedItem("list-type").getNodeValue();
                if (paramName.equals(field.getName())) {
                    field.setAccessible(true);
                    Class cc = Class.forName(paramType);
                }
            }
        }
        return null;
    }
}

