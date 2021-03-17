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
package org.kimios.client.controller.helpers;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.kimios.client.controller.helpers.report.*;
import org.kimios.client.controller.helpers.rules.Rule;
import org.kimios.client.controller.helpers.rules.RuleParameter;
import org.kimios.kernel.ws.pojo.DocumentType;
import org.kimios.kernel.ws.pojo.Meta;
import org.kimios.kernel.ws.pojo.WorkflowStatus;
import org.kimios.kernel.ws.pojo.WorkflowStatusManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;


/**
 * @author Fabien Alin
 */
public class XMLGenerators {

    /**
     * Return document type XML description from document type and metas list
     */
    public static String getDocumentTypeXMLDescriptor(DocumentType dt, List<Meta> lMetas) {
        String xmlStream = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
        xmlStream += "<document-type uid=\"" + dt.getUid() + "\" name=\"" + cleanString(dt.getName())
                + "\" document-type-uid=\"" + dt.getDocumentTypeUid() + "\">\r\n";
        for (Meta m : lMetas) {
            xmlStream += "\t<meta meta_type=\"" + m.getMetaType() + "\" uid=\"" +
                    m.getUid() + "\" name=\"" + cleanString(m.getName()) + "\" meta_feed=\"" + m.getMetaFeedUid()
                    + "\" mandatory=\"" + m.isMandatory() + "\" position=\"" + m.getPosition() + "\"/>\r\n";
        }
        xmlStream += "</document-type>";
        return xmlStream;
    }

    /**
     * Return workflow XML description from workflow UID and workflow status definition list
     */
    public static String getWorkflowXMLDescriptor(long wfUid, Vector<WorkflowStatusDefinition> vStatus) {
        String xmlStream = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        xmlStream += "<workflow " + (wfUid > 0 ? "uid=\"" + wfUid + "\"" : "") + ">";
        for (WorkflowStatusDefinition statusDef : vStatus) {
            WorkflowStatus status = statusDef.getWorkflowStatus();
            WorkflowStatusManager[] lWfStatusManagers = statusDef.getWorkflowStatusManagers();
            xmlStream +=
                    "<status uid=\"" + status.getUid() + "\" successor-uid=\"" + status.getSuccessorUid() + "\"><name>"
                            + cleanString(status.getName()) + "</name>";
            for (WorkflowStatusManager wfm : lWfStatusManagers) {
                xmlStream += "<manager type=\"" + wfm.getSecurityEntityType() + "\" uid=\"" +
                        wfm.getSecurityEntityName() +
                        "\" source=\"" +
                        wfm.getSecurityEntitySource() + "\" />";

            }
            xmlStream += "</status>";
        }
        xmlStream += "</workflow>";
        return xmlStream;
    }

    /**
     * Return enumeration values XML description from meta feed UID and values list
     */
    public static String getEnumerationValuesXMLDescriptor(long metaFeedUid, Vector<String> values) {
        String xmlStream = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
        xmlStream += "<enumeration uid=\"" + metaFeedUid + "\">\r\n";
        for (String val : values) {
            xmlStream += "\t<entry value=\"" + cleanString(val) + "\" />\r\n";
        }
        xmlStream += "</enumeration>";

        return xmlStream;
    }

    /**
     * Return meta datas document XML description from document version UID, meta values map and date
     */
    public static String getMetaDatasDocumentXMLDescriptor(Map<Meta, String> metaValues, String dateFormat) {

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String xmlStream = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<document-meta>\r\n";
        for (Meta m : metaValues.keySet()) {
            xmlStream += "\t<meta uid=\"" + m.getUid() + "\">";
            switch (m.getMetaType()) {
                case 1:
                    xmlStream += "" + cleanString((String) metaValues.get(m)) + "</meta>\r\n";
                    break;
                case 2:
                    try {
                        xmlStream +=
                                "<![CDATA[" + Double.parseDouble((String) metaValues.get(m)) + "]]></meta>\r\n";
                    } catch (Exception pe) {
                        xmlStream += "<![CDATA[0]]></meta>\r\n";
                    }
                    break;
                case 3:
                    try {
                        Date date = sdf.parse((String) metaValues.get(m));
                        System.out.println(" >> Parsed Content From update " + date);
                        xmlStream += "<![CDATA[" + date.getTime() + "]]></meta>\r\n";
                    } catch (ParseException pe) {
                        xmlStream += "<![CDATA[-1]]></meta>\r\n";
                    }
                    break;
                case 4:
                    try {
                        xmlStream +=
                                "<![CDATA[" + Boolean.parseBoolean((String) metaValues.get(m)) + "]]></meta>\r\n";
                    } catch (Exception pe) {
                        xmlStream += "<![CDATA[false]]></meta>\r\n";
                    }
                    break;
                case 5:
                    try {
                        xmlStream +=
                                "<![CDATA[" + metaValues.get(m) + "]]></meta>\r\n";
                    } catch (Exception pe) {
                        xmlStream += "<![CDATA[false]]></meta>\r\n";
                    }
                    break;
            }
        }
        xmlStream += "</document-meta>";
        return xmlStream;
    }

    public static String getRulesXMLDescriptor(List<Map<String, String>> events, List<Map<String, String>> parameters) {
        StringBuffer xml = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        xml.append("<rule>\r\n");
    
    /* events */
        if (events != null) {
            //    xml.append("<events>\r\n");
            for (Map<String, String> map : events) {
                for (String name : map.keySet()) {
                    xml.append("<event name=\"" + name + "\" status=\"" + map.get(name) + "\" />\r\n");
                }
            }
            //    xml.append("</events>\r\n");
        }
    
    /* parameters */
        if (parameters != null) {
            //    xml.append("<parameters>\r\n");
            for (Map<String, String> map : parameters) {
                for (String key : map.keySet()) {
                    xml.append("<parameter key=\"" + key + "\" value=\"" + map.get(key) + "\" />\r\n");
                }
            }
            //    xml.append("</parameters>\r\n");
        }

        xml.append("</rule>\r\n");
        return xml.toString();
    }

    /**
     * Clean string and return it
     */
    public static String cleanString(String str) {
        String r = "";
        for (int g = 0; g < str.length(); g++) {
            int i = (int) str.charAt(g);
            if (i >= 48 && i <= 57 || i >= 65 && i <= 90 || i >= 97 && i <= 122) {
                r += str.charAt(g);
            } else {
                r += "&#" + i + ";";
            }
        }
        return r;
    }

    /**
     * Get report object from XML stream report
     */
    public static Report unserializeReport(String xmlReport) {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("report", Report.class);
        xstream.alias("column", Column.class);
        xstream.alias("row", Row.class);
        xstream.alias("cell", Cell.class);
        xstream.addImplicitCollection(Header.class, "columns");
        xstream.addImplicitCollection(Body.class, "rows");
        xstream.addImplicitCollection(Row.class, "cells");
        Report report = (Report) xstream.fromXML(xmlReport);
        return report;
    }

    /**
     * Get report object from XML stream report
     */
    public static Rule unserializeRule(String xmlReport) {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("xmldesc", Rule.class);
        xstream.alias("parameter", RuleParameter.class);
        xstream.addImplicitCollection(RuleParameter.class, "parameters");
        Rule rule = (Rule) xstream.fromXML(xmlReport);
        return rule;
    }
}

