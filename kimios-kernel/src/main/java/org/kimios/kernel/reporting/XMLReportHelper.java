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
package org.kimios.kernel.reporting;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.DMEntityImpl;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.ReportingException;
import org.kimios.kernel.user.User;
import org.kimios.kernel.utils.ClassFinder;
import org.kimios.kernel.xml.XSDException;
import org.kimios.kernel.xml.XSDUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class XMLReportHelper
{
    public XMLReportHelper()
    {

    }

    /**
     * Get a report XML stream from a given class name and XML stream parameters
     */
    public String getReport(String sessionUid, String className, String xmlParameters) throws ReportingException,
            ConfigException, DataSourceException
    {
        try {
            new XSDUtil().validateXmlStream(xmlParameters, "report-parameters.xsd");

            Class c = Class.forName(className);
            ReportImpl reportImpl = (ReportImpl) c.newInstance();
            reportImpl.setSessionUid(sessionUid);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                    new ByteArrayInputStream(xmlParameters.getBytes()));
            Element root = doc.getDocumentElement();
            NodeList params = root.getElementsByTagName("parameter");

            // Check unknown parameter names
            for (int i = 0; i < params.getLength(); i++) {
                NamedNodeMap map = params.item(i).getAttributes();
                if (!checkIfUnknownParam(map.getNamedItem("name").getNodeValue(), c)) {
                    throw new ReportingException("Parameter name: " + map.getNamedItem("name").getNodeValue()
                            + " is invalid");
                }
            }

            // Check already set parameter names
            List<String> savedParams = new ArrayList<String>();
            for (int i = 0; i < params.getLength(); i++) {
                NamedNodeMap map = params.item(i).getAttributes();
                String paramName = map.getNamedItem("name").getNodeValue();
                if (savedParams.contains(paramName)) {
                    throw new ReportingException("Parameter name: " + map.getNamedItem("name").getNodeValue()
                            + " is already set");
                }
                savedParams.add(paramName);
            }

            for (int i = 0; i < params.getLength(); i++) {
                NamedNodeMap map = params.item(i).getAttributes();
                for (Field field : c.getDeclaredFields()) {
                    String paramName = map.getNamedItem("name").getNodeValue();
                    String paramType = map.getNamedItem("type").getNodeValue();
                    String paramValue = map.getNamedItem("value").getNodeValue();
                    if (paramName.equals(field.getName())) {
                        field.setAccessible(true);
                        Class cc = Class.forName(paramType);
                        if (cc.equals(Boolean.class)) {
                            field.set(reportImpl, Boolean.parseBoolean(paramValue));
                        } else if (cc.equals(Integer.class)) {
                            field.set(reportImpl, Integer.parseInt(paramValue));
                        } else if (cc.equals(Long.class)) {
                            field.set(reportImpl, Long.parseLong(paramValue));
                        } else if (cc.equals(Date.class)) {
                            field.set(reportImpl, new Date(Long.parseLong(paramValue)));
                        } else if (cc.equals(User.class)) {
                            User user = new User();
                            String value[] = paramValue.split("@");
                            if (value.length == 2) {
                                user.setUid(value[0]);
                                user.setAuthenticationSourceName(value[1]);
                            }
                            field.set(reportImpl, user);
                        } else if (cc.equals(DMEntityImpl.class)) {
                            DMEntityImpl dm = new DMEntityImpl();
                            dm.setUid(Long.parseLong(paramValue));
                            field.set(reportImpl, dm);
                        } else {
                            field.set(reportImpl, paramValue);
                        }
                    }
                }
            }
            return reportImpl.getData();
        } catch (XSDException e) {
            throw new ReportingException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new ReportingException(e.getMessage());
        } catch (InstantiationException e) {
            throw new ReportingException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new ReportingException(e.getMessage());
        } catch (SAXException e) {
            throw new ReportingException(e.getMessage());
        } catch (IOException e) {
            throw new ReportingException(e.getMessage());
        } catch (ParserConfigurationException e) {
            throw new ReportingException(e.getMessage());
        }
    }

    private boolean checkIfUnknownParam(String paramName, Class c)
    {
        for (Field field : c.getDeclaredFields()) {
            if (paramName.equals(field.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return report attributes from a given class name
     */
    public String getReportAttributes(String className) throws ReportingException
    {
        try {
            Class c = Class.forName(className);
            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
            xml += "<report name=\"" + c.getName() + "\">\n";
            for (Field field : c.getDeclaredFields()) {
                xml += "<parameter name=\"" + field.getName() + "\" ";
                xml += "type=\"" + field.getType().getName() + "\" ";
                xml += "value=\"\"/>\n";
            }
            xml += "</report>\n";
            return xml;
        } catch (ClassNotFoundException e) {
            throw new ReportingException(e);
        }
    }

    /**
     * Return a list containing all reports
     */
    public String getReportsList()
    {
        Collection<Class<? extends ReportImpl>> classes = ClassFinder.findImplement("org.kimios", ReportImpl.class);
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        xml += "<reportsList>\n";
        for (Class c : classes) {
            xml += "<report name=\"" + c.getSimpleName() + "\" className=\"" + c.getName() + "\"/>\n";
        }
        xml += "</reportsList>\n";
        return xml;
    }
}

