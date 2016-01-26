/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
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

package org.kimios.editors.impl.etherpad;

import net.gjerull.etherpad.client.EPLiteClient;
import net.gjerull.etherpad.client.EPLiteException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.kimios.editors.model.EtherpadEditorData;
import org.kimios.editors.model.EtherpadUserData;
import org.kimios.kernel.controller.IDocumentController;
import org.kimios.kernel.controller.IDocumentVersionController;
import org.kimios.kernel.controller.IFileTransferController;
import org.kimios.kernel.dms.model.DMEntity;
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.dms.model.DocumentVersion;
import org.kimios.editors.ExternalEditor;
import org.kimios.kernel.events.EventHandlerManager;
import org.kimios.kernel.events.GenericEventHandler;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.filetransfer.model.DataTransfer;
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.repositories.impl.RepositoryManager;
import org.kimios.kernel.security.ISecurityAgent;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.user.FactoryInstantiator;
import org.kimios.kernel.user.model.User;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EtherpadEditor implements ExternalEditor<EtherpadEditorData> {

    private static Logger logger = LoggerFactory.getLogger(EtherpadEditorData.class);


    private IDocumentController documentController;
    private IDocumentVersionController documentVersionController;
    private IFileTransferController fileTransferController;
    private ISecurityAgent securityAgent;


    private String etherpadUrl = "http://localhost:9001";
    private String apiKey = "myApiKey";

    private EPLiteClient client;

    private String etherpadProxyPath;


    private ConcurrentHashMap<Long, EtherpadEditorData> editorDatas =
            new ConcurrentHashMap<Long, EtherpadEditorData>();

    public EtherpadEditor(IDocumentController documentController, IDocumentVersionController documentVersionController,
                          IFileTransferController fileTransferController,
                          ISecurityAgent securityAgent,
                          String etherpadUrl, String etherpadApiKey, String etherpadProxyPath) {
        this.documentVersionController = documentVersionController;
        this.documentController = documentController;
        this.fileTransferController = fileTransferController;
        this.securityAgent = securityAgent;

        this.etherpadUrl = etherpadUrl;
        this.apiKey = etherpadApiKey;
        this.etherpadProxyPath = etherpadProxyPath;

    }

    public void init() {
        client = new EPLiteClient(etherpadUrl,
                apiKey);
    }


    private String createAuthorFromSession(Session session) {
        User u = FactoryInstantiator.getInstance()
                .getAuthenticationSourceFactory().getAuthenticationSource(session.getUserSource())
                .getUserFactory().getUser(session.getUserName());


        Map map = client.createAuthorIfNotExistsFor(session.getUserName() + "@" + session.getUserSource(), u.getFirstName() + " "
                + u.getLastName());

        logger.info("client etherpad return {}", map);

        if (map != null && map.get("authorID") != null) {
            return map.get("authorID").toString();
        } else {
            return "";
        }
    }

    @Override
    public EtherpadEditorData startDocumentEdit(Session session, long documentId) throws Exception {
        /*
            Create Pad on Remote Etherpad, and generate Edition Url
            If not any Document Id is provided, a new document will be automatically generated
         */
        //Should check document type to ensure mime type editable (text/plain, html...)

        //set reference to eventhandler instance if exists !!!

        init();

        /*for (GenericEventHandler eventHandler : EventHandlerManager.getInstance().handlers()) {
            if (eventHandler instanceof EtherpadEventHandler) {
                EtherpadEventHandler handler = (EtherpadEventHandler) eventHandler;
                handler.setEtherpadEditor(this);
            }
        }*/

        //check if already stored as currently edited. if yes, give back existing editor data!

        EtherpadEditorData data = this.editorDatas.get(documentId);
        if(data != null){
            //check if can write
            Document document = documentController.getDocument(session, documentId);
            String kimiosUserId = session.getUserName() + "@" +
                    session.getUserSource();
            List<DMEntity> entities = new ArrayList<DMEntity>();
            entities.add(document);
            List<DMEntity> finalList =
                    securityAgent.areWritable(entities, session.getUserName(), session.getUserSource(), session.getGroups());
            if(finalList.size() == 0){
                logger.error("user {} has no write access on document for collaborative edit", kimiosUserId);
                throw new AccessDeniedException();
            } else {
                logger.info("user {} will be added to editor data, and enter in pad! {} (groupId: {})", kimiosUserId, data.getPadId(), data.getGroupId());

                //TODO: notify first pad user(s) about new user !!!!!
                String authorId = createAuthorFromSession(session);

                //get group id from padId

                String groupId = data.getGroupId();


                Map map = client.createSession(groupId, authorId, 1);
                String sessionId = map.get("sessionID").toString();
                EtherpadUserData userData = new EtherpadUserData();
                userData.setAuthorID(authorId);
                userData.setSessionID(sessionId);
                userData.setGroupID(groupId);
                data.getUsersDatas().put(kimiosUserId, userData);
                return data;
            }
        }

        // FIRST EDIT
        documentController.checkoutDocument(session, documentId);
        documentVersionController.createDocumentVersionFromLatest(session, documentId);
        //create pad, if it doesn't exist
        DocumentVersion documentVersion =
                documentVersionController.getLastDocumentVersion(session, documentId);
        String padId = "Kimios_" + documentId;
        //try to get existing pad !
        EtherpadEditorData etherpadEditorData = new EtherpadEditorData();
        String authorId = createAuthorFromSession(session);
        String kimiosUserId = session.getUserName() + "@" +
                session.getUserSource();
        //groupMapper == userMapper
        Map map = client.createGroupIfNotExistsFor(kimiosUserId);
        String groupId = map.get("groupID").toString();
        logger.info("Group Pad Created !!!!. Should Store groupId linked to document ????");
        try {


            String encoding = encodingDetector(documentVersion);

            logger.info("found encoding for version #{}: {}", documentVersion.getUid(), encoding);

            if(encoding == null){
                encoding = "UTF-8";
            }

            map = client.createGroupPad(groupId, padId, IOUtils.toString(RepositoryManager.accessVersionStream(documentVersion), encoding));
            padId = map.get("padID").toString();
            logger.info("created padID {}", padId);
        } catch (EPLiteException ex) {
            if (ex.getMessage().contains("padName does already exist")) {
                //check if pad is currently edited by users !!
                //throw new AccessDeniedException();
                //should get access to pad by another way
                Object o = client.listPads(groupId).get("padIDs");
                logger.info("o {}", o.getClass().getName());
                Iterable padIds = null;
                if( o instanceof Array)    {
                    padIds  = Arrays.asList(o);
                } else if( o instanceof Iterable){
                    padIds = (Iterable) o;
                }
                for(Object id: padIds){
                    String cId = id.toString();
                    logger.info("checking padId {}", cId);
                    if(cId.endsWith(padId)){
                        padId = cId;
                        break;
                    }
                }
            }
        }
        //padId = map.get("padID").toString();
        map = client.createSession(groupId, authorId, 1000000000);
        String sessionId = map.get("sessionID").toString();

        EtherpadUserData userData = new EtherpadUserData();
        userData.setAuthorID(authorId);
        userData.setSessionID(sessionId);
        userData.setGroupID(groupId);
        etherpadEditorData.setDocumentId(documentId);
        etherpadEditorData.setGroupId(groupId);
        etherpadEditorData.getUsersDatas().put(kimiosUserId, userData);
        //TODO: parameterized proxy Name (match the proxy servlet name, defined in context file)
        etherpadEditorData.setProxyName("etherpadProxy");
        etherpadEditorData.setPadId(padId);
        etherpadEditorData.setEtherPadUrl(etherpadProxyPath + "/p/" + padId);
        this.editorDatas.put(documentId, etherpadEditorData);
        return etherpadEditorData;
    }



    private String encodingDetector(DocumentVersion version) throws Exception {
        UniversalDetector detector = new UniversalDetector(null);

// (2)
        InputStream fis = RepositoryManager.accessVersionStream(version);
        int nread;
        byte[] buf = new byte[4096];
        while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();
        if (encoding != null) {
            logger.info("Detected encoding = " + encoding);
            return encoding;
        } else {
            logger.info("No encoding detected.");
            return null;
        }

    }




    @Override
    public EtherpadEditorData versionDocument(Session session, EtherpadEditorData editData) throws Exception {
        //update content
        long newVersion = documentVersionController.createDocumentVersionFromLatest(session, editData.getDocumentId());
        loadPadContentToLastVersion(session, editData);
        return editData;
    }

    protected void endDocumentEditFromCheckout(Session session, long documentId) throws Exception {
        EtherpadEditorData etherpadEditorData = editorDatas.get(documentId);
        endDocumentEdit(session, etherpadEditorData);
    }

    @Override
    public EtherpadEditorData endDocumentEdit(Session session, EtherpadEditorData editData) throws Exception {
        loadPadContentToLastVersion(session, editData);
        documentController.checkinDocument(session, editData.getDocumentId());
        //delete pad
        client.deletePad(editData.getPadId());
        this.editorDatas.remove(editData.getDocumentId());
        return editData;
    }


    private void loadPadContentToLastVersion(Session session, EtherpadEditorData editData) throws Exception {
        Map data = client.getText(editData.getPadId());
        String content = (String) data.get("text");

        logger.info("running pad to version for document {}", editData.getDocumentId());

        DataTransfer dt = fileTransferController.startUploadTransaction(session, editData.getDocumentId(), false);

        logger.info("will update version with transaction {} for doc {}", dt.getUid(), dt.getDocumentVersionUid());

        //hash content
        String md5hash = DigestUtils.md5Hex(content).toLowerCase();
        String sha1hash = DigestUtils.shaHex(content).toLowerCase();
        InputStream is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        fileTransferController.uploadDocument(session, dt.getUid(), is, null, null);

        fileTransferController.endUploadTransaction(session, dt.getUid(), md5hash, sha1hash);
    }
}
