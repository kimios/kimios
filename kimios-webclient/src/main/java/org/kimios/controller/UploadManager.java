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

import flexjson.JSONSerializer;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.lang.StringEscapeUtils;
import org.kimios.client.controller.helpers.*;
import org.kimios.core.configuration.Config;
import org.kimios.kernel.ws.pojo.DMEntitySecurity;
import org.kimios.kernel.ws.pojo.Document;
import org.kimios.kernel.ws.pojo.Meta;
import org.kimios.utils.configuration.ConfigurationManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.MessageDigest;
import java.util.*;

/**
 * @author Fabien Alin
 */
public class UploadManager extends Controller {


    private HttpServletRequest req;
    private HttpServletResponse resp;

    public UploadManager(Map<String, String> parameters) {
        super(parameters);
    }

    public UploadManager(Map<String, String> parameters, HttpServletRequest req, HttpServletResponse resp) {
        super(parameters);
        this.req = req;
        this.resp = resp;
    }

    @Override
    public String execute() throws Exception {
        String jsonResp = "";
        try {
            if (ServletFileUpload.isMultipartContent(req)) {
                resp.setContentType("text/html");
                jsonResp = startUploadFile(req);
            } else {
                if (action.equalsIgnoreCase("progress")) {
                    jsonResp = progress(req);
                } else {
                    return "NOACTION";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResp = "{success: false, exception: '" + e.getMessage() + "'}";
        }
        return jsonResp;
    }


    private static HashMap<String, UploadInfo> uploads = new HashMap<String, UploadInfo>();

    private static UploadInfo getUpload(String key) {
        return uploads.get(key);
    }

    synchronized private static void removeUpload(String key) {
        uploads.remove(key);
    }

    private static void startUpload(String key, UploadInfo o) {
        uploads.put(key, o);
    }


    private String startUploadFile(HttpServletRequest req) throws Exception {

        DiskFileItemFactory fp = new DiskFileItemFactory();
        fp.setRepository(new File(ConfigurationManager.getValue(Config.DM_TMP_FILES_PATH)));
        ServletFileUpload sfu = new ServletFileUpload(fp);
        QProgressListener pp = new QProgressListener(uploads);
        sfu.setProgressListener(pp);
        String uploadId = "";
        String name = "";
        String sec = "";
        String metaValues = "";
        String docTypeUid = "";
        String action = "";
        String documentUid = ""; //used for import
        String newVersion = "";
        boolean isRecursive = false;
        boolean isSecurityInherited = false;
        long folderUid = 0;
        String mimeType = "";
        String extension = "";
        FileItemIterator t = sfu.getItemIterator(req);
        while (t.hasNext()) {

            FileItemStream st = t.next();
            if (st.isFormField()) {
                String tmpVal = Streams.asString(st.openStream(), "UTF-8");
                log.debug(st.getFieldName() + " --> " + tmpVal);
                if (st.getFieldName().equalsIgnoreCase("actionUpload")) {
                    action = tmpVal;
                }
                if (st.getFieldName().equalsIgnoreCase("newVersion")) {
                    newVersion = tmpVal;
                }
                if (st.getFieldName().equalsIgnoreCase("documentUid")) {
                    documentUid = tmpVal;
                }
                if (st.getFieldName().equalsIgnoreCase("UPLOAD_ID")) {
                    uploadId = tmpVal;
                    pp.setUploadId(uploadId);
                }
                if (st.getFieldName().equalsIgnoreCase("name")) {
                    name = tmpVal;
                }
                if (st.getFieldName().equalsIgnoreCase("sec")) {
                    sec = StringEscapeUtils.unescapeHtml(tmpVal);
                }
                if (st.getFieldName().equalsIgnoreCase("documentTypeUid")) {
                    docTypeUid = tmpVal;
                }
                if (st.getFieldName().equalsIgnoreCase("metaValues")) {
                    metaValues = StringEscapeUtils.unescapeHtml(tmpVal);
                }
                if (st.getFieldName().equalsIgnoreCase("folderUid")) {
                    folderUid = Long.parseLong(tmpVal);
                }
                if (st.getFieldName().equalsIgnoreCase("inheritedPermissions")) {
                    isSecurityInherited = (tmpVal != null && tmpVal.equalsIgnoreCase("on"));
                }
                if (st.getFieldName().equalsIgnoreCase("isRecursive")) {
                    isRecursive = (tmpVal != null && tmpVal.equalsIgnoreCase("on"));
                }
            } else {
                InputStream in = st.openStream();

                mimeType = st.getContentType();
                extension = st.getName().substring(st.getName().lastIndexOf('.') + 1);
                int transferChunkSize = Integer.parseInt(ConfigurationManager.getValue(Config.DM_CHUNK_SIZE));

                if (action.equalsIgnoreCase("AddDocumentWithProperties")) {

                    MessageDigest md5 = MessageDigest.getInstance("MD5");
                    MessageDigest sha1 = MessageDigest.getInstance("SHA-1");

                    List<MessageDigest> digests = new ArrayList<MessageDigest>();
                    digests.add(md5);
                    digests.add(sha1);
                    InputStream hIn = new HashInputStream(digests, in);

                    String filename = fp.getRepository() + "/" + new Date();
                    OutputStream out = new FileOutputStream(filename);

                    int read = 0;
                    byte[] bytes = new byte[1024];
                    while ((read = hIn.read(bytes)) != -1)
                        out.write(bytes, 0, read);
                    out.close();

                    HashCalculator hc = new HashCalculator("MD5");
                    String hashMD5 = HashCalculator.buildHexaString(md5.digest()).replaceAll(" ", "");
                    hc.setAlgorithm("SHA-1");
                    String hashSHA1 = HashCalculator.buildHexaString(sha1.digest()).replaceAll(" ", "");

                    // Security Entities
                    Vector<DMEntitySecurity> des;
                    if (sec != null && !sec.isEmpty()) {
                        des = DMEntitySecuritiesParser.parseFromJson(sec, -1, DMEntityType.DOCUMENT);
                    } else {
                        des = new Vector<DMEntitySecurity>();
                    }
                    String securitiesXml = getSecurityEntities(des);

                    // Meta Datas
                    Map<Meta, String> mapMetasValues = DMEntitySecuritiesParser.parseMetasValuesFromJson(sessionUid, metaValues, documentVersionController);
                    String metaXml = XMLGenerators.getMetaDatasDocumentXMLDescriptor(mapMetasValues, "yyyy-MM-dd");

                    long documentTypeId = -1;
                    try {
                        documentTypeId = Long.parseLong(docTypeUid);
                    } catch (NumberFormatException nfe) {
                    }

                    in = new FileInputStream(filename);

                    documentController.createDocumentWithProperties(sessionUid, name, extension, mimeType, folderUid,
                            isSecurityInherited, securitiesXml, isRecursive, documentTypeId, metaXml, hashMD5, hashSHA1, in);


                } else if (action.equalsIgnoreCase("AddDocument")) {
                    Document d = new Document();
                    d.setCreationDate(Calendar.getInstance());
                    d.setExtension(extension);
                    d.setFolderUid(folderUid);
                    d.setCheckedOut(false);
                    d.setMimeType(mimeType);
                    d.setName(name);
                    d.setOwner("");
                    d.setUid(-1);
                    long docUid = documentController.createDocument(sessionUid, d, isSecurityInherited);
                    if (!isSecurityInherited) {
                        securityController.updateDMEntitySecurities(sessionUid, docUid, 3, false,
                                DMEntitySecuritiesParser.parseFromJson(sec, docUid, 3));
                    }

                    fileTransferController.uploadFileFirstVersion(sessionUid, docUid, in, false);
                    long documentTypeUid = -1;
                    try {
                        documentTypeUid = Long.parseLong(docTypeUid);
                    } catch (Exception e) {
                    }
                    if (documentTypeUid > 0) {
                        Map<Meta, String> mMetasValues = DMEntitySecuritiesParser.parseMetasValuesFromJson(sessionUid, metaValues, documentVersionController);
                        String xmlMeta = XMLGenerators.getMetaDatasDocumentXMLDescriptor(mMetasValues, "yyyy-MM-dd");
                        documentVersionController.updateDocumentVersion(sessionUid, docUid, documentTypeUid, xmlMeta);
                    }
                } else {

                    if (action.equalsIgnoreCase("Import")) {
                        documentController.checkoutDocument(sessionUid, Long.parseLong(documentUid));
                        fileTransferController.uploadFileNewVersion(sessionUid, Long.parseLong(documentUid), in, false);
                        documentController.checkinDocument(sessionUid, Long.parseLong(documentUid));
                    } else if (action.equalsIgnoreCase("UpdateCurrent")) {
                        documentController.checkoutDocument(sessionUid, Long.parseLong(documentUid));
                        fileTransferController.uploadFileUpdateVersion(sessionUid, Long.parseLong(documentUid), in, false);
                        documentController.checkinDocument(sessionUid, Long.parseLong(documentUid));
                    }
                }

            }
        }
        return "{success: true}";
    }

    private String getSecurityEntities(Vector<DMEntitySecurity> des) {
        String securitiesXml = "<security-rules dmEntityId=\"-1\" dmEntityTye=\"3\">\r\n";    // -1: unused in DMEntitySecurityUtil
        for (int i = 0; i < des.size(); i++) {
            securitiesXml += "\t<rule " +
                    "security-entity-type=\"" + des.elementAt(i).getType() + "\" " +
                    "security-entity-uid=\"" + StringTools.magicDoubleQuotes(des.elementAt(i).getName()) + "\" " +
                    "security-entity-source=\"" + StringTools.magicDoubleQuotes(des.elementAt(i).getSource())
                    + "\" " +
                    "read=\"" + des.elementAt(i).isRead() + "\" " +
                    "write=\"" + des.elementAt(i).isWrite() + "\" " +
                    "full=\"" + des.elementAt(i).isFullAccess() + "\" />\r\n";
        }
        securitiesXml += "</security-rules>";
        return securitiesXml;
    }

    private String progress(HttpServletRequest req) throws Exception {

        UploadInfo info = uploads.get(req.getParameter("progressId"));
        String jsonResp = new JSONSerializer()
                .exclude("class")
                .exclude("startTime")
                .serialize(info);

        return jsonResp;
    }

    class QProgressListener implements ProgressListener {

        private String uploadId = null;
        private Calendar startedAt;
        private HashMap<String, UploadInfo> lst;

        public QProgressListener(HashMap<String, UploadInfo> lst) {
            this.startedAt = Calendar.getInstance();
            this.lst = lst;
        }

        public void update(long pBytesRead, long pContentLength, int pItems) {
            if (uploadId != null) {
                lst.put(uploadId, new UploadInfo(startedAt.getTime().toString(), pBytesRead, pContentLength));
            }
        }

        public void setUploadId(String t) {
            this.uploadId = t;
        }
    }

}

