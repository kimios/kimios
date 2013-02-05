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
package org.kimios.client.controller.helpers;

import org.kimios.client.controller.ReportingController;
import org.kimios.client.controller.helpers.report.Report;
import org.kimios.client.exception.ReportingException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to generate any Report from ReportGenerator class name
 *
 * @author jludmann
 */
public class ReportGenerator {

    private String sessionUid;
    private String className;
    private Map<String, String> parameters;
    private ReportingController reportingController;

    public ReportGenerator(String sessionUid, String className, ReportingController reportingController) {
        this.sessionUid = sessionUid;
        this.className = className;
        this.parameters = new HashMap<String, String>();
        this.reportingController = reportingController;
    }

    /**
     * Get the class name of report generator
     */
    public String getClassName() {
        return this.className;
    }

    /**
     * Add parameter for the related report
     */
    public void addParameter(String name, String value) throws ReportingException
    {
        if (name == null) {
            throw new ReportingException("Parameter name can't be null");
        }
        if (parameters.containsKey(name)) {
            throw new ReportingException("Value for \"" + name + "\" is already set");
        }
        parameters.put(name, value);
    }

    private boolean checkParameter(String name, NodeList nodeList) {

        for (int i = 0; i < nodeList.getLength(); i++) {
            if (name.equals(nodeList.item(i).getAttributes().getNamedItem("name").getNodeValue())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get a report object from added parameters
     */
    public Report generate() throws ReportingException {
        try {
            ReportingController controller = reportingController;
            Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(controller.getReportAttributes(sessionUid, className).getBytes()));
            NodeList nl = d.getDocumentElement().getElementsByTagName("parameter");
            if (parameters.size() > nl.getLength()) {
                throw new ReportingException("Number of arguments for this report generator \"" + className + "\" must not exceed " + nl.getLength());
            }
            for (String name : parameters.keySet()) {
                if (!checkParameter(name, nl)) {
                    throw new ReportingException("Named parameter \"" + name + "\" is invalid (see getReportAttributes)");
                }
            }
            for (int i = 0; i < nl.getLength(); i++) {
                String value = parameters.get(nl.item(i).getAttributes().getNamedItem("name").getNodeValue());
                nl.item(i).getAttributes().getNamedItem("value").setNodeValue(value);
            }
            StringWriter sw = new StringWriter();
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(d), new StreamResult(sw));
            String xml = controller.getReport(sessionUid, className, sw.toString());
            return XMLGenerators.unserializeReport(xml);
        } catch (Exception ex) {
            throw new ReportingException(ex.getMessage());
        }
    }
}

