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
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.kimios.kernel.controller.IDocumentController;
import org.kimios.kernel.controller.IDocumentVersionController;
import org.kimios.kernel.controller.IFileTransferController;
import org.kimios.kernel.dms.model.DocumentVersion;
import org.kimios.editors.ExternalEditor;
import org.kimios.kernel.filetransfer.model.DataTransfer;
import org.kimios.kernel.repositories.impl.RepositoryManager;
import org.kimios.kernel.security.model.Session;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class EtherpadEditor implements ExternalEditor<EtherpadEditorData> {


    private IDocumentController documentController;
    private IDocumentVersionController documentVersionController;
    private IFileTransferController fileTransferController;


    private String etherpadUrl = "http://localhost:9001";
    private String apiKey = "myApiKey";

    private EPLiteClient client;

    public EtherpadEditor(IDocumentController documentController, IDocumentVersionController documentVersionController,
                          IFileTransferController fileTransferController,
                            String etherpadUrl, String etherpadApiKey){
        this.documentVersionController = documentVersionController;
        this.documentController = documentController;
        this.fileTransferController = fileTransferController;

        this.etherpadUrl = etherpadUrl;
        this.apiKey = etherpadApiKey;

    }

    private void init(){
        client = new EPLiteClient(etherpadUrl,
                apiKey);
    }

    @Override
    public EtherpadEditorData startDocumentEdit(Session session, long documentId) throws Exception {
        /*
            Create Pad on Remote Etherpad, and generate Edition Url
            If not any Document Id is provided, a new document will be automatically generated
         */
        //Should check document type to ensure mime type editable (text/plain, html...)


        documentController.checkoutDocument(session, documentId);
        DocumentVersion documentVersion =
                documentVersionController.getLastDocumentVersion(session, documentId);
        //create pad, if it doesn't exist

        String padId = "Kimios_" + documentId + "_" + session.getUid();
        client.createPad(padId);
        client.setText(padId, IOUtils.toString(RepositoryManager.accessVersionStream(documentVersion)));
        EtherpadEditorData etherpadEditorData = new EtherpadEditorData();
        etherpadEditorData.setDocumentId(documentId);
        etherpadEditorData.setPadId(padId);
        etherpadEditorData.setEtherPadUrl(etherpadUrl + "/p/" + padId);

        return etherpadEditorData;
    }

    @Override
    public EtherpadEditorData versionDocument(Session session, EtherpadEditorData editData) throws Exception {
        //update content
        long newVersion = documentVersionController.createDocumentVersionFromLatest(session, editData.getDocumentId());
        loadPadContentToLastVersion(session, editData);

        return editData;
    }

    @Override
    public EtherpadEditorData endDocumentEdit(Session session, EtherpadEditorData editData) throws Exception {
        loadPadContentToLastVersion(session, editData);
        documentController.checkoutDocument(session, editData.getDocumentId());
        return editData;
    }


    private void loadPadContentToLastVersion(Session session, EtherpadEditorData editData) throws Exception {
        Map data = client.getText(editData.getPadId());
        String content = (String)data.get("text");
        DataTransfer dt = fileTransferController.startUploadTransaction(session, editData.getDocumentId(), false);
        //hash content
        String md5hash = DigestUtils.md5Hex(content).toLowerCase();
        String sha1hash = DigestUtils.shaHex(content).toLowerCase();
        InputStream is = new ByteArrayInputStream( content.getBytes(StandardCharsets.UTF_8) );
        fileTransferController.uploadDocument(session, dt.getUid(), is, md5hash, sha1hash );
    }
}
