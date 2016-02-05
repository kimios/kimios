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
package org.kimios.controller;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.kimios.client.controller.helpers.ReportGenerator;
import org.kimios.client.controller.helpers.report.Column;
import org.kimios.client.controller.helpers.report.Row;
import org.kimios.kernel.reporting.model.Report;
import org.kimios.kernel.reporting.model.ReportParam;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.*;

/**
 *
 * @author jludmann
 */
public class ReportingControllerWeb extends Controller {

    public ReportingControllerWeb(Map<String, String> parameters) {
        super(parameters);
    }

    public String execute() throws Exception {
        if (action.equals("getAttributes")) {
            return getAttributes(parameters);
        }
        if (action.equals("getReport")) {
            return getReport(parameters);
        }
        if (action.equals("getReportsList")) {
            return getReportsList();
        }
        if (action.equals("removeTransaction")) {
            removeTransactions(parameters);
        }
        return "";
    }


    public static String join(Collection<?> values, String delimiter)
    {
        if (values == null)
        {
            return new String();
        }

        StringBuffer strbuf = new StringBuffer();

        boolean first = true;

        for (Object value : values)
        {
            if (!first) { strbuf.append(delimiter); } else { first = false; }
            strbuf.append(value.toString());
        }

        return strbuf.toString();
    }

    private String getAttributes(Map<String, String> parameters) throws Exception {
        List<ReportParam> reportParameters =
                reportingController.getReportAttributes(getSessionUid(), parameters.get("impl"));
        List<Map<String, String>> attributes = new ArrayList<Map<String, String>>();
        for (ReportParam param: reportParameters) {
            Map<String, String> attribute = new HashMap<String, String>();
            if (!"order".equals(param.getName()) && !"asc".equals(param.getName())) {
                attribute.put("name", param.getName());
                attribute.put("type", param.getType());
                attribute.put("listType", param.getListType());
                attribute.put("availableValues", join(param.getAvailableValues(), ","));
                attributes.add(attribute);
            }
        }
        return new JSONSerializer().exclude("class").serialize(attributes);
    }

    private String getReport(Map<String, String> parameters) throws Exception {
        ArrayList<Map<String, String>> paramsList =
                (ArrayList<Map<String, String>>) new JSONDeserializer().deserialize(parameters.get("jsonParameters"));
        ReportGenerator generator = new ReportGenerator(sessionUid, parameters.get("impl"), reportingController);

        for (Map<String, String> param : paramsList) {
            generator.addParameter(param.get("name"), String.valueOf( param.get("value") ));
        }
        org.kimios.client.controller.helpers.report.Report report = generator.generate();
        List<Map<String, String>> columns = new ArrayList<Map<String, String>>();
        for (Column col : report.getHeader().getColumns()) {
            Map<String, String> values = new HashMap<String, String>();
            values.put("header", col.getName());
            values.put("dataIndex", col.getName());
            columns.add(values);
        }

        List<Map<String, String>> body = new ArrayList<Map<String, String>>();
        if (report.getBody().getRows() != null) {
            for (Row row : report.getBody().getRows()) {
                Map<String, String> values = new HashMap<String, String>();
                for (Map column : columns) {
                    String c = String.valueOf(column.get("dataIndex"));
                    values.put(c, String.valueOf(row.getCell(c).getValue()));
                }
                body.add(values);
            }
        }
        StringBuffer fields = new StringBuffer();
        for (Map map : columns) {
            fields.append("{");
            fields.append("\"name\":\"");
            fields.append(map.get("dataIndex"));
            fields.append("\"},");
        }
        String serializedColumns = new JSONSerializer().exclude("class").serialize(columns);
        String serializedBody = new JSONSerializer().exclude("class").serialize(body);

        String jsonMetaData = "\"metaData\":{\"root\":\"records\",\"id\":\"id\",\"fields\":[" + fields.substring(0, fields.length()-1) + "]}";
        String jsonRecords = "\"success\":true,\"records\":" + serializedBody;
        String jsonColumns = "\"columns\":" + serializedColumns;
        return "{" + jsonMetaData + "," + jsonRecords + "," + jsonColumns + "}";
    }

    private String getReportsList() throws Exception {
        List<Report> reports = reportingController.getReportsList(sessionUid);
        /*ByteArrayInputStream in = new ByteArrayInputStream(xmlReportsList.getBytes());
        Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
        NodeList nl = d.getDocumentElement().getElementsByTagName("report");

        List<Map<String, String>> reportsList = new ArrayList<Map<String, String>>();
        for (int i = 0; i < nl.getLength(); i++) {
            Map<String, String> attribute = new HashMap<String, String>();
            String name = nl.item(i).getAttributes().getNamedItem("name").getNodeValue();
            String className = nl.item(i).getAttributes().getNamedItem("className").getNodeValue();
            attribute.put("name", name);
            attribute.put("className", className);
            reportsList.add(attribute);
        } */
        List<Map<String, String>> reportsList = new ArrayList<Map<String, String>>();
        for (Report r: reports) {
            Map<String, String> attribute = new HashMap<String, String>();
            attribute.put("name", r.getName());
            attribute.put("className", r.getClassName());
            reportsList.add(attribute);
        }

        return new JSONSerializer().exclude("class").serialize(reportsList);
    }

    private void removeTransactions(Map<String, String> parameters) throws Exception {
        reportingController.removeGhostTransaction(sessionUid, Long.parseLong(parameters.get("transactionUid")));
    }
}


