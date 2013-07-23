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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kimios.controller;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

import org.kimios.client.controller.helpers.WorkflowStatusDefinition;
import org.kimios.client.controller.helpers.XMLGenerators;
import org.kimios.kernel.ws.pojo.*;

import java.io.Writer;
import java.util.*;

/**
 * @author Fabien Alin
 */
public class StudioControllerWeb extends Controller {

    public StudioControllerWeb(Map<String, String> parameters) {
        super(parameters);
    }

    public String execute() throws Exception {

        if (action.equals("documentTypes")) {
            return documentTypesList();
        }
        if (action.equals("workflows")) {
            return workflowsList();
        }
        if (action.equals("metaFeeds")) {
            return metaFeedsList();
        }
        if (action.equals("statusList")) {
            return statusList(parameters);
        }
        if (action.equals("UpdateDocumentType")) {
            return updateDocumentType(parameters);
        }
        if (action.equals("AddDocumentType")) {
            return addDocumentType(parameters);
        }
        if (action.equals("RemoveDocumentType")) {
            return deleteDocumentType(parameters);
        }
        if (action.equals("getUnheritedMetas")) {
            return getUnheritedMetas(parameters);
        }
        if (action.equals("UpdateMetaFeed")) {
            return updateMetaFeed(parameters);
        }
        if (action.equals("AddMetaFeed")) {
            return addMetaFeed(parameters);
        }
        if (action.equals("RemoveMetaFeed")) {
            return removeMetaFeed(parameters);
        }
        if (action.equals("AvailableMetaFeeds")) {
            return availableMetaFeeds();
        }
        if (action.equals("SearchMetaFeedValues")) {
            return searchMetaFeedValues(parameters);
        }
        if (action.equals("GetMetaFeedValues")) {
            return getMetaFeedValues(parameters);
        }
        if (action.equals("UpdateEnumerationValues")) {
            return updateEnumerationValues(parameters);
        }
        if (action.equals("CancelWorkflow")) {
            return cancelWorkflow(parameters);
        }
        if (action.equals("UpdateWorkflow")) {
            return updateWorflow(parameters);
        }
        if (action.equals("CreateWorkflow")) {
            return createWorflow(parameters);
        }
        if (action.equals("RemoveWorkflow")) {
            return deleteWorkflow(parameters);
        }
        if (action.equals("CreateWorkflowStatus")) {
            return createWorkflowStatus(parameters);
        }
        if (action.equals("CreateWorkflowStatusManager")) {
            return createWorkflowStatusManager(parameters);
        }
        if (action.equals("RemoveWorkflowStatusManager")) {
            return removeWorkflowStatusManager(parameters);
        }
        if (action.equals("GetWorkflowStatus")) {
            return getWorkflowStatus();
        }
        if (action.equals("GetWorkflowStatusManagers")) {
            return getWorkflowStatusManagers();
        }
        return "";
    }

    private String documentTypesList() throws Exception {
        DocumentType[] list = studioController.getDocumentTypes(sessionUid);
        String jsonResp = new JSONSerializer().exclude("class").serialize(list);
        return jsonResp;
    }

    private String getUnheritedMetas(Map<String, String> parameters) throws Exception {
        Meta[] list = documentVersionController.getUnheritedMetas(sessionUid, Long.parseLong(parameters.get("documentTypeUid")));
        String jsonResp = new JSONSerializer().exclude("class").serialize(list);
        return jsonResp;
    }

    private String metaFeedsList() throws Exception {
        MetaFeed[] list = studioController.getMetaFeeds(sessionUid);
        String jsonResp = new JSONSerializer().exclude("class").serialize(list);
        return jsonResp;
    }

    private String workflowsList() throws Exception {
        Workflow[] list = studioController.getWorkflows(sessionUid);
        String jsonResp = new JSONSerializer().exclude("class").serialize(list);
        return jsonResp;
    }

    private String getWorkflowStatus() throws Exception {
        long workflowUid = Long.parseLong(parameters.get("workflowUid"));
        WorkflowStatus[] list = studioController.getWorkflowStatuses(sessionUid, workflowUid);
        return new JSONSerializer().exclude("class").serialize(list);
    }

    private String getWorkflowStatusManagers() throws Exception {
        long statusUid = Long.parseLong(parameters.get("statusUid"));
        WorkflowStatusManager[] list = studioController.getWorkflowStatusManagers(sessionUid, statusUid);
        return new JSONSerializer().exclude("class").serialize(list);
    }

    private String statusList(Map<String, String> parameters) throws Exception {
        WorkflowStatus[] list = studioController.getWorkflowStatuses(sessionUid, Long.parseLong(parameters.get("uid")));
        WorkflowStatusDefinition[] results = new WorkflowStatusDefinition[list.length];
        int ind = 0;
        for (WorkflowStatus it : list) {
            WorkflowStatusDefinition n = new WorkflowStatusDefinition();
            n.setPosition(ind);
            n.setWorkflowStatus(it);
            WorkflowStatusManager[] mn = studioController.getWorkflowStatusManagers(sessionUid, it.getUid());
            n.setWorkflowStatusManagers(mn);
            results[ind] = n;
            ind++;
        }
        XStream xml = new XStream(new DomDriver());
        XStream xstream = new XStream(new JettisonMappedXmlDriver() {

            public HierarchicalStreamWriter createWriter(Writer writer) {
                return new JsonWriter(writer, "".toCharArray(), "", JsonWriter.DROP_ROOT_MODE);
            }
        });
        xstream.aliasField("securityEntityName", WorkflowStatusManager.class, "localSecurityEntityName");
        xstream.aliasField("securityEntitySource", WorkflowStatusManager.class, "localSecurityEntitySource");
        xstream.aliasField("securityEntityType", WorkflowStatusManager.class, "localSecurityEntityType");
        xstream.aliasField("workflowStatusUid", WorkflowStatusManager.class, "localWorkflowStatusUid");
        xstream.setMode(XStream.NO_REFERENCES);
        String jsonResp = xstream.toXML(results);
        return jsonResp;
    }

    private String searchMetaFeedValues(Map<String, String> parameters) throws Exception {
        String[] st = studioController.searchMetaFeedValues(sessionUid, Long.parseLong(parameters.get("uid")), parameters.get("criteria"));
        String jsonResp = "[";
        for (int u = 0; u < st.length; u++) {
            jsonResp += "{\"value\":\"" + st[u] + "\"}";
            if (u < st.length - 1) {
                jsonResp += ",";
            }
        }
        jsonResp += "]";
        return jsonResp;
    }

    /**
     * @see #searchMetaFeedValues(java.util.Map)
     * @deprecated
     */
    private String getMetaFeedValues(Map<String, String> parameters) throws Exception {
        String[] st = studioController.getMetaFeedValues(sessionUid, Long.parseLong(parameters.get("uid")));
        String jsonResp = "[";
        for (int u = 0; u < st.length; u++) {
            jsonResp += "{\"value\":\"" + st[u] + "\"}";
            if (u < st.length - 1) {
                jsonResp += ",";
            }
        }
        jsonResp += "]";
        return jsonResp;
    }

    private String availableMetaFeeds() throws Exception {
        String[] list = studioController.getAvailableMetaFeedTypes(sessionUid);
        String jsonResp = "[";
        for (int u = 0; u < list.length; u++) {
            jsonResp += "{\"className\":\"" + list[u] + "\"}";
            if (u < list.length - 1) {
                jsonResp += ",";
            }
        }
        jsonResp += "]";

        return jsonResp;
    }

    private String updateDocumentType(Map<String, String> parameters) throws Exception {

        DocumentType docType = new DocumentType();
        docType.setUid(Long.parseLong(parameters.get("uid")));
        docType.setName(parameters.get("name"));
        if (parameters.get("heritedfrom").isEmpty()) {
            docType.setDocumentTypeUid(-1);
        } else {
            docType.setDocumentTypeUid(Long.parseLong(parameters.get("heritedfrom")));
        }

        ArrayList<Map<String, String>> l = (ArrayList<Map<String, String>>) new JSONDeserializer().deserialize(parameters.get("jsonParameters"));
        List<Meta> metas = new ArrayList<Meta>();
        for (Map metaDatas : l) {
            Meta meta = new Meta();
            int metaUid = -1;
            if (metaDatas.get("uid") != null) {
                metaUid = (Integer) metaDatas.get("uid");
            }
            meta.setUid(metaUid);
            meta.setName(String.valueOf(metaDatas.get("name")));
            meta.setMetaType((Integer) metaDatas.get("metaType"));
            if (String.valueOf(metaDatas.get("metaFeedUid")).isEmpty()) {
                meta.setMetaFeedUid(-1L);
            } else {
                meta.setMetaFeedUid(((Integer)metaDatas.get("metaFeedUid")).longValue());
            }
            meta.setDocumentTypeUid(docType.getUid());
            meta.setMandatory((Boolean) metaDatas.get("mandatory"));
            metas.add(meta);
        }
        String xmlStream = XMLGenerators.getDocumentTypeXMLDescriptor(docType, metas);
        studioController.updateDocumentType(sessionUid, xmlStream);
        return "";
    }

    private String addDocumentType(Map<String, String> parameters) throws Exception {
        DocumentType docType = new DocumentType();
        docType.setUid(Long.parseLong(parameters.get("uid")));
        docType.setName(parameters.get("name"));
        if (parameters.get("heritedfrom").isEmpty()) {
            docType.setDocumentTypeUid(-1);
        } else {
            docType.setDocumentTypeUid(Long.parseLong(parameters.get("heritedfrom")));
        }

        ArrayList<Map<String, String>> l = (ArrayList<Map<String, String>>) new JSONDeserializer().deserialize(parameters.get("jsonParameters"));
        List<Meta> metas = new ArrayList<Meta>();
        for (Map metaDatas : l) {
            Meta meta = new Meta();
            meta.setUid(-1);
            meta.setName(String.valueOf(metaDatas.get("name")));
            meta.setMetaType((Integer) metaDatas.get("metaType"));
            if (String.valueOf(metaDatas.get("metaFeedUid")).isEmpty()) {
                meta.setMetaFeedUid(-1L);
            } else {
                meta.setMetaFeedUid(((Integer) metaDatas.get("metaFeedUid")).longValue());
            }
            meta.setDocumentTypeUid(docType.getUid());
            meta.setMandatory((Boolean) metaDatas.get("mandatory"));
            metas.add(meta);
        }
        String xmlStream = XMLGenerators.getDocumentTypeXMLDescriptor(docType, metas);
        studioController.addDocumentType(sessionUid, xmlStream);
        return "";
    }

    private String deleteWorkflow(Map<String, String> parameters) throws Exception {
        studioController.deleteWorkflow(sessionUid, Long.parseLong(parameters.get("uid")));
        return "";
    }

    private String deleteDocumentType(Map<String, String> parameters) throws Exception {
        studioController.deleteDocumentType(sessionUid, Long.parseLong(parameters.get("uid")));
        return "";
    }

    private String updateMetaFeed(Map<String, String> parameters) throws Exception {
        studioController.updateMetaFeed(sessionUid, Long.parseLong(parameters.get("uid")), parameters.get("name"));
        return "";
    }

    private String addMetaFeed(Map<String, String> parameters) throws Exception {
        studioController.addMetaFeed(sessionUid, parameters.get("name"), parameters.get("className"));
        return "";
    }

    private String removeMetaFeed(Map<String, String> parameters) throws Exception {
        studioController.removeMetaFeed(sessionUid, Long.parseLong(parameters.get("uid")));
        return "";
    }

    private String updateEnumerationValues(Map<String, String> parameters) throws Exception {
        long enumerationUid = Long.parseLong(parameters.get("uid"));
        ArrayList<HashMap<String, String>> l = (ArrayList<HashMap<String, String>>) new JSONDeserializer().deserialize(parameters.get("json"));
        Vector<String> values = new Vector<String>();

        for (HashMap<String, String> map : l) {
            for (String key : map.keySet()) {
                values.add(map.get(key));
            }
        }
        String stream = XMLGenerators.getEnumerationValuesXMLDescriptor(enumerationUid, values);
        studioController.updateEnumerationValues(sessionUid, stream);
        return "";
    }

    /**
     * @deprecated
     */
    private String createWorkflowStatus(Map<String, String> parameters) throws Exception {
        long workflowUid = Long.parseLong(parameters.get("workflowUid"));
        String workflowName = parameters.get("workflowName");
        String workflowDescription = parameters.get("workflowDescription");
        String statusName = parameters.get("statusName");

//        Vector<WorkflowStatusDefinition> workflowStatus = new Vector<WorkflowStatusDefinition>();
//        XMLGenerators.getWorkflowXMLDescriptor(workflowUid, workflowStatus);

        String xmlStream = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        xmlStream += "<workflow uid=\"" + workflowUid + "\">\n";
        for (WorkflowStatus ws : studioController.getWorkflowStatuses(sessionUid, workflowUid)) {
            xmlStream += "<status uid=\"" + ws.getUid() + "\" successor-uid=\"" + ws.getSuccessorUid() + "\">";
            xmlStream += "<name>" + ws.getName() + "</name>\n";
            for (WorkflowStatusManager wsm : studioController.getWorkflowStatusManagers(sessionUid, ws.getUid())) {
                xmlStream += "<manager type=\"" + 1 + "\" uid=\"" + wsm.getSecurityEntityName() + "\" source=\"" + wsm.getSecurityEntitySource() + "\" />";
            }
            xmlStream += "</status>\n";
        }
        xmlStream += "<status uid=\"-1\" successor-uid=\"-1\">";
        xmlStream += "<name>" + statusName + "</name>";
        xmlStream += "</status>\n";
        xmlStream += "</workflow>\n";

        studioController.updateWorkflow(sessionUid, workflowUid, workflowName, workflowDescription, xmlStream);
        return "";
    }

    /**
     * @deprecated
     */
    private String createWorkflowStatusManager(Map<String, String> parameters) throws Exception {
        long workflowStatusUid = Long.parseLong(parameters.get("workflowStatusUid"));
        String securityEntityName = parameters.get("securityEntityName");
        String securityEntitySource = parameters.get("securityEntitySource");
        int securityEntityType = Integer.parseInt(parameters.get("securityEntityType"));
        studioController.createWorkflowStatusManager(sessionUid, workflowStatusUid, securityEntityName, securityEntitySource, securityEntityType);
        return "";
    }

    /**
     * @deprecated
     */
    private String removeWorkflowStatusManager(Map<String, String> parameters) throws Exception {
        long workflowStatusUid = Long.parseLong(parameters.get("workflowStatusUid"));
        String securityEntityName = parameters.get("securityEntityName");
        String securityEntitySource = parameters.get("securityEntitySource");
        int securityEntityType = Integer.parseInt(parameters.get("securityEntityType"));
        studioController.deleteWorkflowStatusManager(sessionUid, workflowStatusUid, securityEntityName, securityEntitySource, securityEntityType);
        return "";
    }

    private String cancelWorkflow(Map<String, String> parameters) throws Exception {
//      long documentUid = Long.parseLong(request.getParameter("docUid"));
//      new WorkflowControllerWeb().cancelWorkflow(sessionUid, documentUid);
//      response.getWriter().write(new MessageViewer().getMessagePopup("","Done", request));

        return "";
    }

    private String updateWorflow(Map<String, String> parameters) throws Exception {
        ArrayList<Map<String, String>> statusList = (ArrayList<Map<String, String>>) new JSONDeserializer().deserialize(parameters.get("jsonParameters"));
        Vector<WorkflowStatusDefinition> statusDefinitionList = new Vector<WorkflowStatusDefinition>();

        for (Map statusMap : statusList) {
            long statusUid = statusMap.get("uid") != null ? Long.parseLong(String.valueOf(statusMap.get("uid"))) : -1;
            long successorUid = statusMap.get("successorUid") != null ? Long.parseLong(String.valueOf(statusMap.get("successorUid"))) : -1;

            //WorkflowStatus

            WorkflowStatus status = new WorkflowStatus();
            status.setUid(statusUid);
            status.setName(String.valueOf(statusMap.get("name")));
            status.setSuccessorUid(successorUid);

            //WorkflowStatusManagers

            ArrayList<Map<String, String>> managersList = (ArrayList<Map<String, String>>) statusMap.get("managers");
            WorkflowStatusManager[] wsmTab = new WorkflowStatusManager[managersList.size()];
            for (int i = 0; i < managersList.size(); ++i) {
                Map managersMap = managersList.get(i);
                WorkflowStatusManager wsm = new WorkflowStatusManager();
                wsm.setSecurityEntityName(String.valueOf(managersMap.get("uid")));
                wsm.setSecurityEntitySource(String.valueOf(managersMap.get("source")));
                wsm.setSecurityEntityType(Integer.parseInt(String.valueOf(managersMap.get("type"))));
                wsmTab[i] = wsm;
            }

            WorkflowStatusDefinition statusDefinition = new WorkflowStatusDefinition();
            statusDefinition.setWorkflowStatus(status);
            statusDefinition.setWorkflowStatusManagers(wsmTab);

            statusDefinitionList.add(statusDefinition);
        }

        studioController.updateWorkflow(sessionUid, Long.parseLong(parameters.get("uid")), parameters.get("name"), parameters.get("description"), XMLGenerators.getWorkflowXMLDescriptor(-1, statusDefinitionList));
        return "";
    }

    private String createWorflow(Map<String, String> parameters) throws Exception {
        ArrayList<Map<String, String>> statusList = (ArrayList<Map<String, String>>) new JSONDeserializer().deserialize(parameters.get("jsonParameters"));
        Vector<WorkflowStatusDefinition> statusDefinitionList = new Vector<WorkflowStatusDefinition>();

        for (Map statusMap : statusList) {
//            long statusUid = statusMap.get("uid") != null ? Long.parseLong(String.valueOf(statusMap.get("uid"))) : -1;
            long successorUid = statusMap.get("successorUid") != null ? Long.parseLong(String.valueOf(statusMap.get("successorUid"))) : -1;

            //WorkflowStatus

            WorkflowStatus status = new WorkflowStatus();
//            status.setUid(statusUid);
            status.setName(String.valueOf(statusMap.get("name")));
            status.setSuccessorUid(successorUid);

            //WorkflowStatusManagers

            ArrayList<Map<String, String>> managersList = (ArrayList<Map<String, String>>) statusMap.get("managers");
            WorkflowStatusManager[] wsmTab = new WorkflowStatusManager[managersList.size()];
            for (int i = 0; i < managersList.size(); ++i) {
                Map managersMap = managersList.get(i);
                WorkflowStatusManager wsm = new WorkflowStatusManager();
                wsm.setSecurityEntityName(String.valueOf(managersMap.get("uid")));
                wsm.setSecurityEntitySource(String.valueOf(managersMap.get("source")));
                wsm.setSecurityEntityType(Integer.parseInt(String.valueOf(managersMap.get("type"))));
                wsmTab[i] = wsm;
            }

            WorkflowStatusDefinition statusDefinition = new WorkflowStatusDefinition();
            statusDefinition.setWorkflowStatus(status);
            statusDefinition.setWorkflowStatusManagers(wsmTab);

            statusDefinitionList.add(statusDefinition);
        }

        studioController.createWorkflow(sessionUid, parameters.get("name"), parameters.get("description"), XMLGenerators.getWorkflowXMLDescriptor(-1, statusDefinitionList));
        return "";
    }

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
}

