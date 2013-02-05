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
package org.kimios.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kimios.client.controller.helpers.AuthenticationSourceUtil;
import org.kimios.core.DateHelper;
import org.kimios.core.GroupUserTransformer;
import org.kimios.kernel.ws.pojo.AuthenticationSource;
import org.kimios.kernel.ws.pojo.Document;
import org.kimios.kernel.ws.pojo.Group;
import org.kimios.kernel.ws.pojo.Role;
import org.kimios.kernel.ws.pojo.Session;
import org.kimios.kernel.ws.pojo.User;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * @author Fabien Alin
 */
public class AdminControllerWeb extends Controller
{
    public AdminControllerWeb(Map<String, String> parameters)
    {
        super(parameters);
    }

    public String execute() throws Exception
    {
        if (action.equalsIgnoreCase("domains")) {
            return getDomains(parameters);
        }
        if (action.equalsIgnoreCase("domainDetails")) {
            return getDomainDetails(parameters);
        }
        if (action.equalsIgnoreCase("updateDomain")) {
            return updateDetails(parameters);
        }
        if (action.equalsIgnoreCase("createDomainDetails")) {
            return createDomainDetails(parameters);
        }
        if (action.equalsIgnoreCase("domainType")) {
            return getDomainTypeList(parameters);
        }
        if (action.equalsIgnoreCase("domainTypeFields")) {
            return getDomainFields(parameters);
        }
        if (action.equalsIgnoreCase("domainTypeParam")) {
            return getDomainFieldsDesc(parameters);
        }
        if (action.equals("CreateDmsUser")) {
            return createUser(parameters);
        }
        if (action.equals("UpdateDmsUser")) {
            return updateUser(parameters);
        }
        if (action.equals("DeleteDmsUser")) {
            return deleteUser(parameters);
        }
        if (action.equals("CreateDmsGroup")) {
            return createGroup(parameters);
        }
        if (action.equals("UpdateDmsGroup")) {
            return updateGroup(parameters);
        }
        if (action.equals("DeleteDmsGroup")) {
            return deleteGroup(parameters);
        }
        if (action.equals("AddDmsUserToGroup")) {
            return addUserToGroup(parameters);
        }
        if (action.equals("RemoveDmsUserFromGroup")) {
            return removeUserFromGroup(parameters);
        }
        if (action.equals("deleteAuthenticationSource")) {
            return deleteAuthenticationSource(parameters);
        }
        if (action.equals("getUsersGroups")) {
            return usersGroups(parameters);
        }
        if (action.equals("getGroupsUsers")) {
            return groupsUsers(parameters);
        }
        if (action.equals("AddDmsUserToGroup")) {
            return addUserToGroup(parameters);
        }
        if (action.equals("RemoveDmsUserFromGroup")) {
            return removeUserFromGroup(parameters);
        }
        if (action.equals("CreateRole")) {
            return createRole(parameters);
        }
        if (action.equals("DeleteRole")) {
            return deleteRole(parameters);
        }
        if (action.equals("roles")) {
            return roles(parameters);
        }
        if (action.equals("userRoles")) {
            return userRoles(parameters);
        }
        if (action.equals("getDeadLock")) {
            return getDeadLock();
        }
        if (action.equals("getConnectedUsers")) {
            return getConnectedUsers();
        }
        if (action.equals("getSessions")) {
            return getSessions(parameters);
        }
        if (action.equals("disconnectUser")) {
            return disconnectUser(parameters);
        }
        if (action.equals("eraseSessions")) {
            return eraseSessions(parameters);
        }
        if (action.equals("clearDeadLock")) {
            return clearDeadLock(parameters);
        }
        if (action.equals("reindex")) {
            return reindex();
        }
        if (action.equals("changeOwner")) {
            return changeOwner(parameters);
        }
        if (action.equals("getReindexProgress")) {
            return getReindexProgress();
        }
        return "{\"success\":false,\"exception\":\"Unknown action\"}";
    }

    private String roles(Map<String, String> parameters) throws Exception
    {
        Role[] items = administrationController.getRoles(sessionUid,
                Integer.parseInt(parameters.get("roleId")));
        String resp = "{list:" + new JSONSerializer().serialize(items) + "}";
        return resp;
    }

    private String userRoles(Map<String, String> parameters) throws Exception
    {
        Role[] items = administrationController.getRoles(sessionUid,
                parameters.get("userName"), parameters.get("userSource"));
        String resp = "{list:" + new JSONSerializer().serialize(items) + "}";
        return resp;
    }

    private String createRole(Map<String, String> parameters) throws Exception
    {
        administrationController.createRole(sessionUid,
                Integer.parseInt(parameters.get("roleId")),
                parameters.get("uid"), parameters.get("source"));
        return "{\"success\":true}";
    }

    private String deleteRole(Map<String, String> parameters) throws Exception
    {
        administrationController.deleteRole(sessionUid,
                Integer.parseInt(parameters.get("roleId")),
                parameters.get("uid"), parameters.get("source"));
        return "";
    }

    private String addUserToGroup(Map<String, String> parameters)
            throws Exception
    {
        administrationController.addUserToGroup(sessionUid,
                parameters.get("uid"), parameters.get("gid"),
                parameters.get("authenticationSourceName"));
        return "";
    }

    private String removeUserFromGroup(Map<String, String> parameters)
            throws Exception
    {
        administrationController.removeUserFromGroup(sessionUid,
                parameters.get("uid"), parameters.get("gid"),
                parameters.get("authenticationSourceName"));
        return "";
    }

    private String usersGroups(Map<String, String> parameters) throws Exception
    {
        Group[] grps = administrationController.getGroups(sessionUid,
                parameters.get("uid"),
                parameters.get("authenticationSourceName"));
        return new JSONSerializer().transform(new GroupUserTransformer(),
                User.class, Group.class).serialize(grps);
    }

    private String groupsUsers(Map<String, String> parameters) throws Exception
    {
        User[] users = administrationController.getUsers(sessionUid,
                parameters.get("gid"),
                parameters.get("authenticationSourceName"));
        return new JSONSerializer().transform(new GroupUserTransformer(),
                User.class, Group.class).serialize(users);
    }

    private String deleteAuthenticationSource(Map<String, String> parameters)
            throws Exception
    {
        String name = parameters.get("authenticationSourceName");
        administrationController.deleteAuthenticationSource(sessionUid,
                name);
        return "";
    }

    private String getDomainTypeList(Map<String, String> parameters)
            throws Exception
    {
        String xml = administrationController.getAvailableAuthenticationSource(sessionUid);
        List<String> list = AuthenticationSourceUtil.getAvailable(xml);
        String json = new String("{impl:[");
        for (String impl : list) {
            json += "{className:'" + impl + "'},";
        }
        if (list.size() > 0) {
            json = json.substring(0, json.lastIndexOf(","));
        }
        json += "]}";
        return json;
    }

    private String getDomainFields(Map<String, String> parameters)
            throws Exception
    {
        String typeName = parameters.get("className");
        String xmlParams = administrationController.getAvailableAuthenticationSourceParams(sessionUid, typeName);
        List<String> fields = AuthenticationSourceUtil.getAvailableParams(xmlParams);
        String metaDatas = "{fields:[";
        for (String field : fields) {
            metaDatas += "{name:'" + field + "'},";
        }
        if (fields.size() > 0) {
            metaDatas = metaDatas.substring(0, metaDatas.lastIndexOf(","));
        }
        metaDatas += "]}";
        return metaDatas;
    }

    private String getDomainFieldsDesc(Map<String, String> parameters)
            throws Exception
    {
        String typeName = parameters.get("className");
        String authName = parameters.get("name");

        String xmlParams = administrationController.getAuthenticationSourceParams(sessionUid, authName, typeName);
        Map<String, String> fields = AuthenticationSourceUtil.getFields(xmlParams);

        String metaDatas = "{fields:[";

        for (String key : fields.keySet()) {
            metaDatas += "{name:'" + key + "'},";
        }

        if (fields.size() > 0) {
            metaDatas = metaDatas.substring(0, metaDatas.lastIndexOf(","));
        }
        metaDatas += "]}";

        return metaDatas;
    }

    private String getDomains(Map<String, String> parameters) throws Exception
    {
        AuthenticationSource[] items = securityController.getAuthenticationSources();
        String jsonResp = new JSONSerializer().serialize(items);
        return jsonResp;
    }

    private String getDomainDetails(Map<String, String> parameters)
            throws Exception
    {
        String typeName = parameters.get("className");
        String authName = parameters.get("name");

        String xmlParams = administrationController.getAuthenticationSourceParams(sessionUid, authName, typeName);
        Map<String, String> fields = AuthenticationSourceUtil.getFields(xmlParams);

        String metaDatas = "metaData:{" + "totalProperty:'results',"
                + "root:'rows'," + "id:'nanouninana'," + "fields:[";

        String values = "";
        JSONSerializer ser = new JSONSerializer();
        for (String key : fields.keySet()) {
            metaDatas += "{name:'" + key + "'},";
            values += key + ":" + ser.serialize(fields.get(key)) + ",";
        }
        if (values.length() > 0 && values.indexOf(",") != -1) {
            values = values.substring(0, values.lastIndexOf(","));
        }

        values = "{" + values + "}";
        metaDatas = metaDatas.substring(0, metaDatas.lastIndexOf(","));
        metaDatas += "]},results:1,rows:";

        String jsonResp = "{" + metaDatas + "[" + values + "]}";
        return jsonResp;
    }

    private String createDomainDetails(Map<String, String> parameters)
            throws Exception
    {
        AuthenticationSourceUtil util = new AuthenticationSourceUtil(
                parameters.get("newName"));
        ArrayList<Map<String, String>> l =
                (ArrayList<Map<String, String>>) new JSONDeserializer().deserialize(parameters.get("jsonParameters"));
        for (Map<String, String> m : l) {
            util.addField((String) m.get("name"), (String) m.get("value"));
        }
        administrationController.createAuthenticationSource(sessionUid,
                parameters.get("newName"), parameters.get("className"),
                util.generateXml());
        return "{\"success\":true}";
    }

    private String updateDetails(Map<String, String> parameters)
            throws Exception
    {
        AuthenticationSourceUtil util = new AuthenticationSourceUtil(
                parameters.get("name"));
        util.changeName(parameters.get("newName"));
        ArrayList<Map<String, String>> l =
                (ArrayList<Map<String, String>>) new JSONDeserializer().deserialize(parameters.get("jsonParameters"));
        for (Map<String, String> m : l) {
            util.addField((String) m.get("name"), (String) m.get("value"));
        }
        administrationController.updateAuthenticationSource(sessionUid,
                parameters.get("name"), parameters.get("newName"),
                parameters.get("className"), util.generateXml());
        return "{\"success\":true}";
    }

    private String createUser(Map<String, String> parameters)
            throws Exception
    {
        administrationController.createUser(sessionUid,
                parameters.get("uid"), parameters.get("name"),
                parameters.get("mail"), parameters.get("password"),
                parameters.get("authenticationSourceName"));
        return "{\"success\":true}";
    }

    private String updateUser(Map<String, String> parameters)
            throws Exception
    {
        try {
            administrationController.updateUser(sessionUid,
                    parameters.get("uid"), parameters.get("name"),
                    parameters.get("mail"), parameters.get("password"),
                    parameters.get("authenticationSourceName"));
            return "{\"success\":true}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"success\":false,\"exception\":\"" + e.getMessage() + "\"}";
        }
    }

    private String deleteUser(Map<String, String> parameters)
            throws Exception
    {
        administrationController.deleteUser(sessionUid,
                parameters.get("uid"),
                parameters.get("authenticationSourceName"));
        return "";
    }

    private String createGroup(Map<String, String> parameters)
            throws Exception
    {
        administrationController.createGroup(sessionUid,
                parameters.get("gid"), parameters.get("name"),
                parameters.get("authenticationSourceName"));
        return "{\"success\":true}";
    }

    private String updateGroup(Map<String, String> parameters)
            throws Exception
    {
        administrationController.updateGroup(sessionUid,
                parameters.get("gid"), parameters.get("name"),
                parameters.get("authenticationSourceName"));
        return "{\"success\":true}";
    }

    private String deleteGroup(Map<String, String> parameters)
            throws Exception
    {
        administrationController.deleteGroup(sessionUid,
                parameters.get("gid"),
                parameters.get("authenticationSourceName"));
        return "";
    }

    private String getConnectedUsers() throws Exception
    {
        return new JSONSerializer().serialize(administrationController.getConnectedUsers(sessionUid));
    }

    private String getSessions(Map<String, String> parameters) throws Exception
    {
        String userName = parameters.get("userName");
        String userSource = parameters.get("userSource");
        List<Map<String, Object>> sessions = new ArrayList<Map<String, Object>>();

        for (Session s : administrationController.getEnabledSessions(
                sessionUid, userName, userSource))
        {
            Map<String, Object> session = new HashMap<String, Object>();
            session.put("lastUse", DateHelper.convert(s.getLastUse()));
            session.put("metaDatas", s.getMetaDatas());
            session.put("sessionUid", s.getSessionUid());
            session.put("userName", s.getUserName());
            session.put("userSource", s.getUserSource());
            sessions.add(session);
        }

        return new JSONSerializer().serialize(sessions);
    }

    private String disconnectUser(Map<String, String> parameters)
            throws Exception
    {
        Session[] sessions = administrationController.getEnabledSessions(
                sessionUid, parameters.get("userName"),
                parameters.get("userSource"));
        for (Session s : sessions) {
            if (!s.getSessionUid().equals(sessionUid)) {
                administrationController.removeEnabledSession(sessionUid,
                        s.getSessionUid());
            }
        }
        return "{\"success\":true}";
    }

    private String eraseSessions(Map<String, String> parameters)
            throws Exception
    {
        administrationController.removeEnabledSession(sessionUid,
                parameters.get("sessionUidToRemove"));
        return "{\"success\":true}";
    }

    private String getDeadLock() throws Exception
    {
        List<Map<String, Object>> documents = new ArrayList<Map<String, Object>>();
        for (Document doc : administrationController.getCheckedOutDocuments(sessionUid)) {
            Map<String, Object> document = new HashMap<String, Object>();
            document.put("checkedOut", doc.getCheckedOut());
            document.put("checkoutDate",
                    DateHelper.convert(doc.getCheckoutDate()));
            document.put("checkoutUser", doc.getCheckoutUser());
            document.put("checkoutUserSource", doc.getCheckoutUserSource());
            document.put("creationDate",
                    DateHelper.convert(doc.getCreationDate()));
            document.put("documentTypeName", doc.getDocumentTypeName());
            document.put("documentTypeUid", doc.getDocumentTypeUid());
            document.put("extension", doc.getExtension());
            document.put("folderUid", doc.getFolderUid());
            document.put("length", doc.getLength());
            document.put("mimeType", doc.getMimeType());
            document.put("name", doc.getName());
            document.put("outOfWorkflow", doc.getOutOfWorkflow());
            document.put("owner", doc.getOwner());
            document.put("ownerSource", doc.getOwnerSource());
            document.put("path", doc.getPath());
            document.put("uid", doc.getUid());
            document.put("updateDate", DateHelper.convert(doc.getUpdateDate()));
            document.put("versionCreationDate",
                    DateHelper.convert(doc.getVersionCreationDate()));
            document.put("versionUpdateDate",
                    DateHelper.convert(doc.getVersionUpdateDate()));
            document.put("workflowStatusName", doc.getWorkflowStatusName());
            document.put("workflowStatusUid", doc.getWorkflowStatusUid());
            documents.add(document);
        }

        return new JSONSerializer().serialize(documents);
    }

    private String clearDeadLock(Map<String, String> parameters)
            throws Exception
    {
        administrationController.clearLock(sessionUid,
                Long.parseLong(String.valueOf(parameters.get("documentUid"))));
        return "{\"success\":true}";
    }

    private String reindex() throws Exception
    {
        administrationController.reIndex(sessionUid, "/");
        return "{\"success\":true}";
    }

    private String getReindexProgress() throws Exception
    {
        return "[{\"percent\":\""
                + administrationController.getReIndexProgress(sessionUid)
                + "\"}]";
    }

    private String changeOwner(Map<String, String> parameters) throws Exception
    {
        long dmEntityUid = Long.parseLong(parameters.get("dmEntityUid"));
        int dmEntityType = Integer.parseInt(parameters.get("dmEntityType"));
        String userName = parameters.get("userName");
        String userSource = parameters.get("userSource");
        administrationController.changeOwnerShip(sessionUid, dmEntityUid,
                dmEntityType, userName, userSource);
        return "{\"success\":true}";
    }
}
