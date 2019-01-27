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

import flexjson.JSONSerializer;
import org.apache.commons.lang.StringUtils;
import org.kimios.client.controller.helpers.XMLGenerators;
import org.kimios.core.configuration.Config;
import org.kimios.kernel.ws.pojo.Document;
import org.kimios.kernel.ws.pojo.DocumentComment;
import org.kimios.kernel.ws.pojo.DocumentVersion;
import org.kimios.kernel.ws.pojo.Meta;
import org.kimios.utils.configuration.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.util.*;

public class DocumentVersionActionHandler extends Controller
{

    private static Logger logger = LoggerFactory.getLogger(DocumentVersionActionHandler.class);

    private HttpServletResponse resp;
    private HttpServletRequest request;

    public DocumentVersionActionHandler(Map<String, String> parameters)
    {
        super(parameters);
    }

    public DocumentVersionActionHandler(Map<String, String> parameters, HttpServletResponse resp)
    {
        super(parameters);
        this.resp = resp;
    }

    public DocumentVersionActionHandler(Map<String, String> parameters, HttpServletRequest request, HttpServletResponse resp)
    {
        super(parameters);
        this.resp = resp;
        this.request = request;
    }

    private static String[] DL_ACTIONS = new String[]{"GetLastVersion", "GetDocumentVersion", "getTemporaryFile"};

    public String execute() throws Exception
    {
        String jsonResp = "";
        if (action != null) {
            if (action.equals("GetLastVersion")) {
                getLastVersion();
            }
            if (action.equals("GetDocumentVersion")) {
                getDocumentVersion();
            }
            if (action.equals("getDocumentVersions")) {
                jsonResp = getDocumentVersions();
            }
            if (action.equals("getComments")) {
                jsonResp = getComments();
            }
            if (action.equals("AddComment")) {
                addComment();
            }
            if (action.equals("UpdateComment")) {
                updateComment();
            }
            if (action.equals("DeleteComment")) {
                deleteComment();
            }
            if (action.equals("updateMetas")) {
                updateMetas();
            }
            if (action.equals("pdfToImage")) {
                jsonResp = pdfToImage();
            }
            if (action.equals("getTemporaryFile")) {
                getTemporaryFile();
            }

            if (action.equals("setVersionId")) {
                setVersionId();
            }

            if(Arrays.asList(DL_ACTIONS).indexOf(action) > - 1){
                jsonResp = "downloadaction";
            }

            return jsonResp;
        } else {
            return "{\"success\":false}";
        }
    }

    private void setVersionId() throws Exception
    {
        Long versionId = Long.parseLong(parameters.get("versionId"));
        String customVersionId = parameters.get("customVersionId");
        documentVersionController.updateDocumentVersionId(sessionUid, versionId, customVersionId);
    }

    private String pdfToImage() throws Exception
    {
        String pdfPath = null;
        File pdfFile = null;
        try {
            Document doc = documentController.getDocument(sessionUid, Long.parseLong(parameters.get("uid")));
            DocumentVersion dv = documentVersionController.getLastDocumenVersion(sessionUid, doc.getUid());
            String prefix =
                   ConfigurationManager.getValue("client",Config.DM_TMP_FILES_PATH) + "/pdf_" + parameters.get("uid") + "_"
                            + dv.getUid() + "_" + dv.getHashMd5() + "_" + dv.getHashSha();
            pdfPath = prefix + ".pdf";
            pdfFile = new File(pdfPath);
            if (pdfFile.exists()) {
                try{
                    pdfFile.delete();
                }catch (Exception ex){

                }
            }
            fileTransferController
                  .downloadFileVersion(sessionUid, dv.getUid(), new FileOutputStream(pdfPath), false);
            List<Map<String, String>> imgPaths = new ArrayList<Map<String, String>>(  );
            Map<String, String> items = new HashMap<String, String>(  );
            items.put( "num", "0" );
            items.put( "path", pdfPath );
            imgPaths.add( items );
            /*imgPaths = PdfToImage.convert(doc.getUid(), dv.getUid(), pdfPath, dv.getHashMd5(), dv.getHashSha());*/
            return new JSONSerializer().serialize(imgPaths);
        } catch (Exception e) {
            return "{\"success\":false,\"exception\":\"" + e.getMessage() + "\"}";
        } finally {
            /*boolean deleted = pdfFile.delete();
            if (!deleted) {
                pdfFile.deleteOnExit();
            }*/
        }
    }

    private void getTemporaryFile() throws Exception
    {
        String imgPath = null;
        if (securityController.canRead(sessionUid,
                Long.parseLong(parameters.get("uid")), 3))
        {
            imgPath = parameters.get("path");
            String filename = imgPath.substring(imgPath.lastIndexOf('/')).substring(1);
            int length = (int) (new File(imgPath).length());
            resp.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
            if(filename.endsWith( "pdf")){
                resp.setContentType( "application/pdf" );
            } else
                resp.setContentType("image/png");
            resp.setContentLength(length);
            String temporaryFilesPath =ConfigurationManager.getValue("client",Config.DM_TMP_FILES_PATH);
            int transferChunkSize = Integer.parseInt(ConfigurationManager.getValue("client", Config.DM_CHUNK_SIZE));
            fileTransferController.downloadTemporaryFile(sessionUid,
                    imgPath, resp.getOutputStream(), length);
            resp.getOutputStream().flush();
            resp.getOutputStream().close();
        }
    }


    private void getFilePreview() throws Exception
    {
        String imgPath = null;
        Long documentId = Long.parseLong(parameters.get("uid"));
        if (securityController.canRead(sessionUid,
                documentId, 3))
        {

            Document document = documentController.getDocument(sessionUid, documentId);
            DocumentVersion version =
                    documentVersionController.getLastDocumenVersion(sessionUid, documentId);

            String fileName = document.getName() + (StringUtils.isNotBlank(document.getExtension())
                    ? "." + document.getExtension() : "");
            imgPath = parameters.get("path");
            int length = (int) (new File(imgPath).length());
            resp.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

            //TODO: use mime type handling
            if(fileName.endsWith( "pdf")){
                resp.setContentType( "application/pdf" );
            } else
                resp.setContentType("application/octet-stream");
            resp.setContentLength(length);
            String temporaryFilesPath =ConfigurationManager.getValue("client",Config.DM_TMP_FILES_PATH);
            int transferChunkSize = Integer.parseInt(ConfigurationManager.getValue("client", Config.DM_CHUNK_SIZE));
            fileTransferController.downloadTemporaryFile(sessionUid,
                    imgPath, resp.getOutputStream(), length);
            resp.getOutputStream().flush();
            resp.getOutputStream().close();
        }
    }



    private String encodeFileNameInHeader(HttpServletRequest request, String docName) throws Exception {



        String userAgent = request.getHeader("user-agent");
        boolean isInternetExplorer = (userAgent.indexOf("MSIE") > -1);

        String item = null;
        if (isInternetExplorer) {
            item = "attachment; filename=\"" + URLEncoder.encode(docName, "utf-8") + "\"";
        } else {
            item = "attachment; filename=\"" + MimeUtility.encodeWord(docName) + "\"";
        }

        return  item;
    }





    private void getLastVersion() throws Exception
    {
        Document doc = documentController.getDocument(sessionUid, Long.parseLong(parameters.get("uid")));
        String docName = doc.getName() +  (StringUtils.isNotBlank(doc.getExtension()) ? "." + doc.getExtension() : "");
        resp.setHeader("Content-Disposition",encodeFileNameInHeader(request, docName));
        resp.setContentType(doc.getMimeType());
        resp.setContentLength((int) doc.getLength());
        fileTransferController.downloadFileVersion(sessionUid, doc.getLastVersionId(), resp.getOutputStream(), false);
        resp.getOutputStream().flush();
        resp.getOutputStream().close();
    }

    private void getDocumentVersion() throws Exception
    {
        Document doc = documentController.getDocument(sessionUid, Long.parseLong(parameters.get("docUid")));
        String docName = doc.getName() +  "_" + parameters.get("verUid") + (StringUtils.isNotBlank(doc.getExtension()) ? "." + doc.getExtension() : "");
        resp.setHeader("Content-Disposition", encodeFileNameInHeader(request, docName));
        resp.setContentType(doc.getMimeType());
        DocumentVersion dv = documentVersionController.getDocumentVersion(sessionUid,
                Long.parseLong(parameters.get("verUid")));
        resp.setContentLength((int) dv.getLength());
        fileTransferController.downloadFileVersion(sessionUid, dv.getUid(), resp.getOutputStream(), false);
        resp.getOutputStream().flush();
        resp.getOutputStream().close();
    }

    private String getDocumentVersions() throws Exception
    {
        long documentUid = Long.parseLong(parameters.get("documentUid"));
        List<Map<String, Object>> versions = new ArrayList<Map<String, Object>>();
        for (DocumentVersion dv : documentVersionController.getDocumentVersions(sessionUid, documentUid)) {
            Map<String, Object> version = new HashMap<String, Object>();
            version.put("author", dv.getAuthor());
            version.put("authorSource", dv.getAuthorSource());
            version.put("creationDate", dv.getCreationDate().getTime());
            version.put("documentTypeName", dv.getDocumentTypeName());
            version.put("documentTypeUid", dv.getDocumentTypeUid());
            version.put("uid", dv.getUid());
            version.put("lastVersionId", dv.getUid());
            version.put("length", dv.getLength());
            version.put("documentUid", dv.getDocumentUid());
            version.put("modificationDate", dv.getModificationDate().getTime());
            version.put("customVersion", dv.getCustomVersion());
            version.put("customVersionPending", dv.getCustomVersionPending());
            versions.add(version);
        }
        return new JSONSerializer().serialize(versions);
    }

    private String getComments() throws Exception
    {
        long documentVersionUid = Long.parseLong(parameters.get("documentVersionUid"));
        List<Map<String, Object>> comments = new ArrayList<Map<String, Object>>();
        for (DocumentComment dc : documentVersionController.getDocumentComments(sessionUid, documentVersionUid)) {
            Map<String, Object> comment = new HashMap<String, Object>();
            comment.put("authorName", dc.getAuthorName());
            comment.put("authorSource", dc.getAuthorSource());
            comment.put("comment", dc.getComment());
            comment.put("date", dc.getDate().getTime());
            comment.put("documentVersionUid", dc.getDocumentVersionUid());
            comment.put("uid", dc.getUid());
            comments.add(comment);
        }
        return new JSONSerializer().serialize(comments);
    }

    private void addComment() throws Exception
    {
        long documentVersionUid = Long.parseLong(parameters.get("docVersionUid"));
        String comment = parameters.get("comment");
        documentVersionController.createDocumentComment(sessionUid, documentVersionUid, comment);
    }

    private void updateComment() throws Exception
    {
        long documentVersionUid = Long.parseLong(parameters.get("docVersionUid"));
        String comment = parameters.get("comment");
        long commentUid = Long.parseLong(parameters.get("commentUid"));
        documentVersionController.updateDocumentComment(sessionUid, commentUid, documentVersionUid, comment);
    }

    private void deleteComment() throws Exception
    {
        long commentUid = Long.parseLong(parameters.get("commentUid"));
        documentVersionController.deleteDocumentComment(sessionUid, commentUid);
    }

    // UNUSED
    private void updateMetas() throws Exception
    {
        long documentVersionUid = Long.parseLong(parameters.get("versionUid"));
        int docType = Integer.parseInt(String.valueOf(parameters.get("documentTypeUid")));
        String metaValues = parameters.get("metaValues");
        Map<Meta, String> mMetasValues =
                DMEntitySecuritiesParser.parseMetasValuesFromJson(sessionUid, metaValues, documentVersionController);
        String xmlMeta = XMLGenerators.getMetaDatasDocumentXMLDescriptor(mMetasValues, "MM/dd/yyyy");
    }
}
