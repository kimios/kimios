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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kimios.controller;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.apache.commons.io.IOUtils;
import org.kimios.client.controller.DocumentController;
import org.kimios.client.controller.FolderController;
import org.kimios.client.controller.SearchController;
import org.kimios.client.controller.WorkspaceController;
import org.kimios.client.controller.helpers.XMLGenerators;
import org.kimios.client.exception.DeleteDocumentWithActiveShareException;
import org.kimios.core.configuration.Config;
import org.kimios.core.exceptions.DeletingDocumentsWithActiveShareException;
import org.kimios.core.exceptions.DocumentDeletedWithActiveShareException;
import org.kimios.core.exceptions.DocumentsDeletedWithActiveShareException;
import org.kimios.core.wrappers.DMEntity;
import org.kimios.kernel.ws.pojo.*;
import org.kimios.utils.configuration.ConfigurationManager;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Fabien Alin
 */
public class DmEntityControllerWeb extends Controller
{
    private boolean forceDeletion;

    public DmEntityControllerWeb(Map<String, String> parameters)
    {
        super(parameters);
        forceDeletion = Boolean.valueOf(parameters.get("force"));
    }

    public String execute() throws Exception
    {
        if (action.equalsIgnoreCase("getEntities")) {
            return getEntities();
        }
        if (action.equalsIgnoreCase("getEntity")) {
            return getEntity();
        }
        if (action.equalsIgnoreCase("deleteEntity")) {
            deleteEntity();
        }
        if (action.equalsIgnoreCase("getPath")) {
            return getPath();
        }
        if (action.equalsIgnoreCase("moveEntity")) {
            moveEntity();
        }
        if (action.equalsIgnoreCase("moveEntities")) {
            moveEntities();
        }
        if (action.equalsIgnoreCase("deleteEntities")) {
            deleteEntities();
        }
        if (action.equalsIgnoreCase("updateEntities")) {
            updateEntities();
        }
        if(action.equalsIgnoreCase("createSymbolicLink")){
            createSymbolikLink();
        }
        if(action.equalsIgnoreCase("permanentDelete")){
            permanentDocumentDelete();
        }
        if(action.equalsIgnoreCase("exportCsv")){
            return exportCsv();
        }
        return "";
    }

    private String exportCsv() throws Exception {

        long folderId = Long.parseLong(parameters.get("folderId"));
        InputStream io =
                documentController.exportToCsv(sessionUid, folderId);
        //copy to file

        String fileName = "Kimios_Export_"
                + new SimpleDateFormat("yyyy_MM_dd_HH_mm").format(new Date()) + ".csv";
        IOUtils.copyLarge(io, new FileOutputStream(ConfigurationManager.getValue("client","temp.directory") + "/" + fileName));
        String fullResp =
                "[{\"fileExport\":\"" + fileName + "\"}]";

        return fullResp;
    }

    private String getEntity() throws Exception
    {
        int dmEntityType = Integer.parseInt(parameters.get("dmEntityType"));
        long dmEntityUid = Long.parseLong(parameters.get("dmEntityUid"));
        org.kimios.core.wrappers.DMEntity it = null;
        switch (dmEntityType) {
            case 1:
                it = new DMEntity(workspaceController.getWorkspace(sessionUid, dmEntityUid));
                break;
            case 2:
                it = new DMEntity(folderController.getFolder(sessionUid, dmEntityUid));
                break;
            case 3:
                it = new DMEntity(documentController.getDocument(sessionUid, dmEntityUid));
                break;
        }
        if (it != null) {
            String jsonResp = "";
            jsonResp = new JSONSerializer().serialize(it);
            return "[" + jsonResp + "]";
        } else {
            throw new Exception();
        }
    }

    private void deleteEntity() throws Exception
    {
        int dmEntityType = Integer.parseInt(parameters.get("dmEntityType"));
        long dmEntityUid = Long.parseLong(parameters.get("dmEntityUid"));
        switch (dmEntityType) {
            case 1:
                if(ConfigurationManager.getValue("client",Config.TRASH_FEATURE_ENABLED) != null
                        &&ConfigurationManager.getValue("client",Config.TRASH_FEATURE_ENABLED).toLowerCase().equals("true")) {
                    extensionController.addEntityToTrash(sessionUid, dmEntityUid, false);
                } else {
                    workspaceController.deleteWorkspace(sessionUid, dmEntityUid);
                }
                break;
            case 2:
                if(ConfigurationManager.getValue("client",Config.TRASH_FEATURE_ENABLED) != null
                        &&ConfigurationManager.getValue("client",Config.TRASH_FEATURE_ENABLED).toLowerCase().equals("true")) {
                    extensionController.addEntityToTrash(sessionUid, dmEntityUid, false);
                } else {
                    folderController.deleteFolder(sessionUid, dmEntityUid);
                }
                break;
            case 3:
                //mode to trash ?
                boolean toTrash = (ConfigurationManager.getValue("client",Config.TRASH_FEATURE_ENABLED) != null
                        &&ConfigurationManager.getValue("client",Config.TRASH_FEATURE_ENABLED).equals("true"));
                this.deleteDocument(dmEntityUid, toTrash);
                break;
            case 7:
                long parentId = Long.parseLong(parameters.get("parentId"));
                documentController.removeSymbolicLink(sessionUid, dmEntityUid, parentId);
                break;
        }
    }

    private String getEntities() throws Exception
    {
        int dmEntityType = 0;
        long dmEntityUid = 0;
        List<DMEntity> qNodes = new ArrayList<DMEntity>();
        try {
            try {
                dmEntityType = Integer.parseInt(parameters.get("dmEntityType"));
                dmEntityUid = Long.parseLong(parameters.get("dmEntityUid"));
            } catch (Exception e) {

            }

            if (dmEntityType > 0 && dmEntityUid > 0) {
                Folder[] fNodes = folderController.getFolders(sessionUid, dmEntityUid, dmEntityType);
                for (Folder f : fNodes) {
                    qNodes.add(new DMEntity(f));
                }
                if (dmEntityType == 2) {
                    Document[] dNodes = documentController.getDocuments(sessionUid, dmEntityUid);
                    for (Document d : dNodes) {
                        try {
                            qNodes.add(new DMEntity(d));
                        } catch (Exception e) {
                            log.error( "Error on pojo convert", e);
                        }
                    }
                    /*
                        Add Symlinks
                     */
                    SymbolicLink[] symbolicLinks = documentController
                            .getChildSymbolicLinks(sessionUid, dmEntityUid, dmEntityType);
                    for(SymbolicLink sl: symbolicLinks){
                        try {
                            qNodes.add(new DMEntity(sl));
                        } catch (Exception e) {
                            log.error( "Error on pojo convert", e);
                        }
                    }
                }
            } else {
                Workspace[] wNodes = workspaceController.getWorkspaces(sessionUid);
                for (Workspace w : wNodes) {
                    qNodes.add(new DMEntity(w));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String jsonResp = new JSONSerializer().exclude("class").serialize(qNodes);
        jsonResp = "{list:" + jsonResp + "}";

        return jsonResp;
    }

    private String getPath() throws Exception
    {
        try {
            int dmEntityType = Integer.parseInt(parameters.get("dmEntityType"));
            long dmEntityUid = Long.parseLong(parameters.get("dmEntityUid"));
            String path = new SearchController().getPathFromDMEntity(sessionUid, dmEntityUid, dmEntityType);
            return "[{'dmEntityUid':" + dmEntityUid + ",'dmEntityType':" + dmEntityType + ",'path':'" + path + "'}]";
        } catch (NumberFormatException nfe) {
            return "[{'dmEntityUid':0,'dmEntityType':0,'path':'/'}]";
        }
    }

    private void moveEntity() throws Exception
    {
        long dmEntityUid = Long.parseLong(parameters.get("uid"));
        int dmType = Integer.parseInt(parameters.get("type"));
        long targetUid = Long.parseLong(parameters.get("targetUid"));
        int targetType = Integer.parseInt(parameters.get("targetType"));
        if (dmType == 2) {
            Folder f = folderController.getFolder(sessionUid, dmEntityUid);
            if (f != null) {
                if (!(f.getUid() == targetUid && targetType == 2)) {
                    f.setParentUid(targetUid);
                    f.setParentType(targetType);
                    folderController.updateFolder(sessionUid, f);
                }
            }
        }
        if (dmType == 3) {
            Document doc = documentController.getDocument(sessionUid, dmEntityUid);
            if (doc != null && targetType == 2) {
                doc.setFolderUid(targetUid);
                documentController.updateDocument(sessionUid, doc);
            }
        }
    }


    private void createSymbolikLink() throws Exception
    {
        long dmEntityUid = Long.parseLong(parameters.get("uid"));
        int dmType = Integer.parseInt(parameters.get("type"));
        long targetUid = Long.parseLong(parameters.get("targetUid"));
        int targetType = Integer.parseInt(parameters.get("targetType"));
        if (dmType == 3) {
            Document doc = documentController.getDocument(sessionUid, dmEntityUid);
            if (doc != null && targetType == 2) {
                documentController.createSymbolicLink(sessionUid, dmEntityUid, targetUid, doc.getName());
            }
        }
    }

    private void moveEntities() throws Exception
    {
        long targetUid = Long.parseLong(parameters.get("targetUid"));
        int targetType = Integer.parseInt(parameters.get("targetType"));

        List<Map<String, Object>> dmEntities = (ArrayList<Map<String, Object>>) new JSONDeserializer()
                .deserialize(parameters.get("dmEntityPojosJson"));

        for (Map<String, Object> dmEntity : dmEntities) {
            long dmEntityUid = Long.parseLong(String.valueOf(dmEntity.get("uid")));
            int dmType = Integer.parseInt(String.valueOf(dmEntity.get("type")));

            if (dmType == 2) {
                Folder f = folderController.getFolder(sessionUid, dmEntityUid);
                if (f != null) {
                    if (!(f.getUid() == targetUid && targetType == 2)) {
                        f.setParentUid(targetUid);
                        f.setParentType(targetType);
                        folderController.updateFolder(sessionUid, f);
                    }
                }
            }
            if (dmType == 3) {
                Document doc = documentController.getDocument(sessionUid, dmEntityUid);
                if (doc != null && targetType == 2) {
                    doc.setFolderUid(targetUid);
                    documentController.updateDocument(sessionUid, doc);
                }
            }
        }
    }

    private void deleteEntities() throws Exception
    {
        List<Map<String, Object>> dmEntities = (ArrayList<Map<String, Object>>) new JSONDeserializer()
                .deserialize(parameters.get("dmEntityPojosJson"));
        List<Map<String, Object>> undeletedEntities = new ArrayList<>();
        Map<Long, String> deletedWithWarningEntities = new HashMap<>();
        for (Map<String, Object> dmEntity : dmEntities) {
            long dmEntityUid = Long.parseLong(String.valueOf(dmEntity.get("uid")));
            int dmEntityType = Integer.parseInt(String.valueOf(dmEntity.get("type")));

            switch (dmEntityType) {
                case 1:
                    if(ConfigurationManager.getValue("client",Config.TRASH_FEATURE_ENABLED) != null
                            &&ConfigurationManager.getValue("client",Config.TRASH_FEATURE_ENABLED).equals("true")) {
                        extensionController.addEntityToTrash(sessionUid, dmEntityUid, false);
                    } else
                        workspaceController.deleteWorkspace(sessionUid, dmEntityUid);
                    break;
                case 2:
                    if(ConfigurationManager.getValue("client",Config.TRASH_FEATURE_ENABLED) != null
                            &&ConfigurationManager.getValue("client",Config.TRASH_FEATURE_ENABLED).equals("true")) {
                        extensionController.addEntityToTrash(sessionUid, dmEntityUid, false);
                    } else {
                        folderController.deleteFolder(sessionUid, dmEntityUid);
                    }
                    break;
                case 3:
                    // mode to trash ?
                    boolean toTrash = (ConfigurationManager.getValue("client",Config.TRASH_FEATURE_ENABLED) != null
                            &&ConfigurationManager.getValue("client",Config.TRASH_FEATURE_ENABLED).equals("true"));
                    try {
                        this.deleteDocument(dmEntityUid, toTrash);
                    } catch (DeletingDocumentsWithActiveShareException e) {
                        undeletedEntities.add(dmEntity);
                    } catch (DocumentDeletedWithActiveShareException e) {
                        deletedWithWarningEntities.put(e.getDmEntityId(), e.getDmEntityPath());
                    }
                break;
            }
        }
        if (undeletedEntities.size() > 0) {
            throw new DeletingDocumentsWithActiveShareException(createPathList(undeletedEntities));
        }
        if (deletedWithWarningEntities.size() > 0) {
            throw new DocumentsDeletedWithActiveShareException(deletedWithWarningEntities);
        }
    }

    private Map<Long, String> createPathList (List<Map<String, Object>> dmEntities) throws Exception {
        Map<Long, String> paths = new HashMap<>();
        try {
            dmEntities.forEach(map -> {
                try {
                    Document document = documentController.getDocument(sessionUid, Long.parseLong(String.valueOf(map.get("uid"))));
                    paths.put(document.getUid(), document.getPath());
                } catch (Exception e) {
                }
            });
        } catch (Exception ee) {
            throw ee;
        }
        return paths;
    }

    private void permanentDocumentDelete() throws Exception {

        long dmEntityUid = Long.parseLong(parameters.get("dmEntityUid"));
        int dmEntityType = Integer.parseInt(parameters.get("dmEntityType"));
        switch (dmEntityType) {
            case 1:
                    workspaceController.deleteWorkspace(sessionUid, dmEntityUid);
                break;
            case 2:
                    folderController.deleteFolder(sessionUid, dmEntityUid);
                    break;
            case 3:
                    this.deleteDocument(dmEntityUid, false);
                break;
        }

    }

    private void deleteDocument(long dmEntityUid, boolean toTrash) throws Exception {
        String path = "";
        try {
            Document document = documentController.getDocument(sessionUid, dmEntityUid);
            path = document.getPath();
            if (toTrash) {
                extensionController.addEntityToTrash(sessionUid, dmEntityUid, forceDeletion);
            } else {
                documentController.deleteDocument(sessionUid, dmEntityUid, forceDeletion);
            }
        } catch (DeleteDocumentWithActiveShareException e) {
            boolean askConfirmationIfShared =
                   ConfigurationManager.getValue("client",Config.ASK_CONFIRMATION_ON_DELETING_SHARED_DOCUMENT) != null ?
                            Boolean.valueOf(ConfigurationManager.getValue("client",Config.ASK_CONFIRMATION_ON_DELETING_SHARED_DOCUMENT)) :
                            true;
            if (askConfirmationIfShared) {
                Document d = documentController.getDocument(sessionUid, dmEntityUid);
                Map<Long, String> paths = new HashMap<>();
                paths.put(dmEntityUid, d.getPath());
                throw new DeletingDocumentsWithActiveShareException(paths);
            } else {
                try {
                    // force deletion
                    if (toTrash) {
                        extensionController.addEntityToTrash(sessionUid, dmEntityUid, true);
                    } else {
                        documentController.deleteDocument(sessionUid, dmEntityUid, true);
                    }
                } catch (org.kimios.client.exception.DocumentDeletedWithActiveShareException ee) {
                    throw new DocumentDeletedWithActiveShareException(dmEntityUid, path);
                }
            }
        } catch (org.kimios.client.exception.DocumentDeletedWithActiveShareException ee) {
            throw new DocumentDeletedWithActiveShareException(dmEntityUid, path);
        }
    }

    private void updateEntities() throws Exception
    {
        List<Map<String, Object>> dmEntities = (ArrayList<Map<String, Object>>) new JSONDeserializer()
                .deserialize(parameters.get("dmEntityPojosJson"));

        for (Map<String, Object> dmEntity : dmEntities) {
            long dmEntityUid = Long.parseLong(String.valueOf(dmEntity.get("uid")));
            int dmType = Integer.parseInt(String.valueOf(dmEntity.get("type")));
            String name = String.valueOf(dmEntity.get("name"));

            switch (dmType) {
                case 1:
                    WorkspaceController wctr = workspaceController;
                    Workspace w = wctr.getWorkspace(sessionUid, dmEntityUid);
                    w.setName(name);
                    wctr.updateWorkspace(sessionUid, w);
                    if (securityController.hasFullAccess(sessionUid, dmEntityUid, 1)) {
                        boolean changeSecurity = true;
                        if (parameters.get("changeSecurity") != null) {
                            changeSecurity = Boolean.parseBoolean(parameters.get("changeSecurity"));
                        }
                        if (changeSecurity == true) {
                            securityController.updateDMEntitySecurities(sessionUid, dmEntityUid, 1,
                                    (parameters.get("isRecursive") != null && parameters.get(
                                            "isRecursive").equals("true")),
                                    (parameters.get("appendMode") != null && parameters.get(
                                            "appendMode").equals("false")),
                                    DMEntitySecuritiesParser.parseFromJson(parameters.get("sec"), dmEntityUid, 1));
                        }
                    }
                    break;
                case 2:
                    FolderController fctr = folderController;
                    Folder f = fctr.getFolder(sessionUid, dmEntityUid);
                    f.setName(name);
                    fctr.updateFolder(sessionUid, f);
                    boolean recursive = false;
                    recursive = parameters.get("isRecursive") != null && parameters.get("isRecursive").equals("true");
                    if (securityController.hasFullAccess(sessionUid, f.getUid(), 2)) {
                        boolean changeSecurity = true;
                        if (parameters.get("changeSecurity") != null) {
                            changeSecurity = Boolean.parseBoolean(parameters.get("changeSecurity"));
                        }
                        if (changeSecurity == true) {
                            securityController.updateDMEntitySecurities(sessionUid, dmEntityUid, 2,
                                    recursive,
                                    true,
                                    DMEntitySecuritiesParser.parseFromJson(
                                            parameters.get("sec"), dmEntityUid, 2));
                        }
                    }
                    break;
                case 3:
                    DocumentController dsm = documentController;
                    Document d = dsm.getDocument(sessionUid, dmEntityUid);
                    if (!d.getName().equals(name)) {
                        d.setName(name);
                        dsm.updateDocument(sessionUid, d);
                    }
                    long docType = Long.parseLong(parameters.get("documentTypeUid"));

                    String sec = parameters.get("sec");
                    String metaValues = parameters.get("metaValues");

                    if (parameters.get("changeSecurity") != null &&
                            Boolean.parseBoolean(parameters.get("changeSecurity")) == true)
                    {

                        securityController.updateDMEntitySecurities(sessionUid, dmEntityUid, 3, false, false,
                                DMEntitySecuritiesParser.parseFromJson(sec, dmEntityUid, 3));
                    }
                    Map<Meta, String> mMetasValues = DMEntitySecuritiesParser
                            .parseMetasValuesFromJson(sessionUid, metaValues, documentVersionController);
                    String xmlMeta = XMLGenerators.getMetaDatasDocumentXMLDescriptor(mMetasValues, "yyyy-MM-dd");
                    documentVersionController.updateDocumentVersion(sessionUid, d.getUid(), docType, xmlMeta);
                    break;
            }
        }
    }
}

