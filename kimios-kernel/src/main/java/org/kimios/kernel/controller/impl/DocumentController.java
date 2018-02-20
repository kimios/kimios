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
package org.kimios.kernel.controller.impl;

import org.apache.commons.lang.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.kimios.exceptions.*;
import org.kimios.kernel.configuration.Config;
import org.kimios.kernel.controller.*;
import org.kimios.kernel.dms.model.*;
import org.kimios.kernel.dms.model.Bookmark;
import org.kimios.kernel.dms.model.DMEntity;
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.dms.model.DocumentVersion;
import org.kimios.kernel.dms.model.Folder;
import org.kimios.kernel.dms.model.Meta;
import org.kimios.kernel.dms.model.MetaValue;
import org.kimios.kernel.dms.model.SymbolicLink;
import org.kimios.kernel.dms.model.WorkflowStatus;
import org.kimios.kernel.dms.utils.PathUtils;
import org.kimios.kernel.dms.*;
import org.kimios.kernel.dms.FactoryInstantiator;
import org.kimios.kernel.dms.utils.MetaPathHandler;
import org.kimios.kernel.dms.MetaProcessor;
import org.kimios.kernel.events.model.EventContext;
import org.kimios.api.events.annotations.DmsEvent;
import org.kimios.api.events.annotations.DmsEventName;
import org.kimios.kernel.events.impl.AddonDataHandler;
import org.kimios.kernel.filetransfer.model.DataTransfer;
import org.kimios.kernel.filetransfer.zip.FileCompressionHelper;
import org.kimios.kernel.log.model.DMEntityLog;
import org.kimios.kernel.repositories.impl.RepositoryManager;
import org.kimios.kernel.security.*;
import org.kimios.kernel.security.model.DMEntitySecurity;
import org.kimios.kernel.security.model.SecurityEntityType;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.user.model.User;
import org.kimios.utils.hash.HashCalculator;
import org.kimios.utils.configuration.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

@Transactional
public class DocumentController extends AKimiosController implements IDocumentController {

    private static Logger log = LoggerFactory.getLogger(DocumentController.class);

    IWorkspaceController wksCtrl;

    IFolderController fldCtrl;

    ISecurityController secCtrl;

    IDocumentVersionController vrsCtrl;

    IFileTransferController ftCtrl;


    public IWorkspaceController getWksCtrl() {
        return wksCtrl;
    }

    public void setWksCtrl(IWorkspaceController wksCtrl) {
        this.wksCtrl = wksCtrl;
    }

    public IFolderController getFldCtrl() {
        return fldCtrl;
    }

    public void setFldCtrl(IFolderController fldCtrl) {
        this.fldCtrl = fldCtrl;
    }

    public ISecurityController getSecCtrl() {
        return secCtrl;
    }

    public void setSecCtrl(ISecurityController secCtrl) {
        this.secCtrl = secCtrl;
    }

    public IDocumentVersionController getVrsCtrl() {
        return vrsCtrl;
    }

    public void setVrsCtrl(IDocumentVersionController vrsCtrl) {
        this.vrsCtrl = vrsCtrl;
    }

    public IFileTransferController getFtCtrl() {
        return ftCtrl;
    }

    public void setFtCtrl(IFileTransferController ftCtrl) {
        this.ftCtrl = ftCtrl;
    }

    /**
     * Get a document from its uid
     */
    public Document getDocument(Session session, long uid)
            throws DataSourceException, ConfigException, AccessDeniedException {
        Document d = dmsFactoryInstantiator.getDocumentFactory().getDocument(uid);
        if (d == null ||
                !getSecurityAgent().isReadable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }
        return d;
    }

    /**
     * Get a document from its name, its extension and its parent folder
     */
    public Document getDocument(Session session, String name, String extension, long folderUid)
            throws DataSourceException, ConfigException,
            AccessDeniedException {
        Document d = dmsFactoryInstantiator.getDocumentFactory()
                .getDocument(name, extension, dmsFactoryInstantiator.getFolderFactory().getFolder(folderUid));
        if (d == null ||
                !getSecurityAgent().isReadable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }

        return d;
    }

    /**
     * @param session
     * @return
     * @throws ConfigException
     * @throws DataSourceException
     * @throws AccessDeniedException
     */
    public List<Document> getDocuments(Session session)
            throws ConfigException, DataSourceException, AccessDeniedException {
        List<Document> d = dmsFactoryInstantiator.getDocumentFactory().getDocuments();
        return getSecurityAgent().areReadable(d, session.getUserName(), session.getUserSource(), session.getGroups());
    }

    /**
     * Get children documents from a parent folder
     */
    public List<Document> getDocuments(Session session, long folderUid)
            throws ConfigException, DataSourceException, AccessDeniedException {
        Folder folder = dmsFactoryInstantiator.getFolderFactory().getFolder(folderUid);
        if (!getSecurityAgent()
                .isReadable(folder, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }
        List<Document> d = dmsFactoryInstantiator.getDocumentFactory().getDocuments(folder);
        log.debug("first document load for folder #" + folderUid + ": " + d.size());
        List<Document> readableItems = getSecurityAgent().areReadable(d, session.getUserName(), session.getUserSource(), session.getGroups());
        log.debug("after load, readable items are: " + readableItems.size());

        return readableItems;
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentController#createDocument(org.kimios.kernel.security.Session, java.lang.String, java.lang.String, java.lang.String, java.lang.String, long, boolean)
    */
    @DmsEvent(eventName = {DmsEventName.DOCUMENT_CREATE})
    public long createDocument(Session s, String name, String extension, String mimeType, long folderUid,
                               boolean isSecurityInherited)
            throws NamingException, ConfigException, DataSourceException, AccessDeniedException {
        name.trim();
        PathUtils.validDmEntityName(name);
        Folder parent = dmsFactoryInstantiator.getFolderFactory().getFolder(folderUid);
        if (dmsFactoryInstantiator.getDocumentFactory().getDocument(name, extension, parent) != null) {
            throw new NamingException("A document named \"" + name + "\" already exists at the specified location.");
        }
        Date creationDate = new Date();
        Document d =
                new Document(name, s.getUserName(), s.getUserSource(), creationDate, creationDate, folderUid, mimeType,
                        extension);
        DMEntitySecurityFactory dsf = securityFactoryInstantiator.getDMEntitySecurityFactory();
        if (getSecurityAgent().isWritable(parent, s.getUserName(), s.getUserSource(), s.getGroups())) {
            d.setFolder(parent);
            dmsFactoryInstantiator.getDmEntityFactory().generatePath(d);
            dmsFactoryInstantiator.getDocumentFactory().saveDocument(d);
            if (isSecurityInherited) {
                Vector<DMEntitySecurity> v = dsf.getDMEntitySecurities(parent);
                for (int i = 0; i < v.size(); i++) {
                    DMEntitySecurity des = new DMEntitySecurity(d.getUid(), d.getType(), v.elementAt(i).getName(),
                            v.elementAt(i).getSource(), v.elementAt(i)
                            .getType(), v.elementAt(i).isRead(), v.elementAt(i).isWrite(),
                            v.elementAt(i).isFullAccess(), d);

                    dsf.saveDMEntitySecurity(des, null);
                }
            }
            return d.getUid();
        } else {
            throw new AccessDeniedException();
        }
    }

    public long createDocument(Session s, String path, boolean isSecurityInherited)
            throws NamingException, ConfigException, DataSourceException, AccessDeniedException, PathException {
        return generateEntitiesFromPath(s, path, isSecurityInherited);
    }


    public long createDocument(Session s, String name, String extension, boolean isSecurityInherited, String securitiesXmlStream,
                               long documentTypeId, String metasXmlStream)
            throws NamingException, ConfigException, DataSourceException, AccessDeniedException, PathException {
        //parse metas
        List<MetaValue> values = MetaProcessor.getMetaValuesFromXML(metasXmlStream);
        DocumentType documentType = null;
        if(documentTypeId != -1){
            documentType = dmsFactoryInstantiator.getDocumentTypeFactory().getDocumentType(documentTypeId);
        }
        /*
            Load path structure setup
         */
        PathTemplate pathTemplate = dmsFactoryInstantiator.getPathTemplateFactory().getDefaultPathTemplate();
        if (pathTemplate == null) {
            log.warn("default path template not available. can't evaluate path. please contact your administrator. during this time, " +
                    "document type name will be used as path template");
            pathTemplate = documentType != null ? MetaPathHandler.defaultPathModel() : MetaPathHandler.defaultUserPathModel();
        } else
            log.debug("loaded path template #{} - {}", pathTemplate.getId(), pathTemplate.getTemplateName());


        String path = new MetaPathHandler().path(new Date(), s, pathTemplate.getPathElements(), documentType, values, extension);
        log.debug("generated path: {}", path);
        long documentId = generateEntitiesFromPath(s, path, isSecurityInherited);
        Document document = dmsFactoryInstantiator.getDocumentFactory().getDocument(documentId);
        log.debug("Adding document " + document + " to event context");
        EventContext.addParameter("document", document);

        if (!isSecurityInherited)
            secCtrl.updateDMEntitySecurities(s, documentId, securitiesXmlStream, false, false);

        vrsCtrl.createDocumentVersion(s, documentId);
        if (documentTypeId > 0) {
            EventContext.get().setEntity(document);
            vrsCtrl.updateDocumentVersion(s, documentId, documentTypeId, metasXmlStream);
        }


        EventContext.get().setEntity(document);
        return documentId;

    }


    @DmsEvent(eventName = {DmsEventName.FILE_UPLOAD})
    public long createDocumentWithProperties(Session s, String name, String extension, String mimeType, long folderUid,
                                             boolean isSecurityInherited, String securitiesXmlStream,
                                             boolean isRecursive, long documentTypeId, String metasXmlStream,
                                             InputStream documentStream, String hashMd5, String hashSha1)
            throws NamingException, ConfigException, DataSourceException, AccessDeniedException {

        try {
            long documentId = -1;
            name = name.trim();
            PathUtils.validDmEntityName(name);
            Folder parent = dmsFactoryInstantiator.getFolderFactory().getFolder(folderUid);
            if (dmsFactoryInstantiator.getDocumentFactory().getDocument(name, extension, parent) != null) {
                throw new NamingException("A document named \"" + name + "\" already exists at the specified location.");
            }
            Date creationDate = new Date();
            Document d =
                    new Document(name, s.getUserName(), s.getUserSource(), creationDate, creationDate, folderUid, mimeType,
                            extension);
            DMEntitySecurityFactory dsf = securityFactoryInstantiator.getDMEntitySecurityFactory();
            if (getSecurityAgent().isWritable(parent, s.getUserName(), s.getUserSource(), s.getGroups())) {
                d.setFolder(parent);
                dmsFactoryInstantiator.getDmEntityFactory().generatePath(d);
                dmsFactoryInstantiator.getDocumentFactory().saveDocument(d);
                if (isSecurityInherited) {
                    Vector<DMEntitySecurity> v = dsf.getDMEntitySecurities(parent);
                    for (int i = 0; i < v.size(); i++) {
                        DMEntitySecurity des = new DMEntitySecurity(d.getUid(), d.getType(), v.elementAt(i).getName(),
                                v.elementAt(i).getSource(), v.elementAt(i)
                                .getType(), v.elementAt(i).isRead(), v.elementAt(i).isWrite(),
                                v.elementAt(i).isFullAccess(), d);

                        dsf.saveDMEntitySecurity(des, null);
                    }
                }
                documentId = d.getUid();
            } else {
                throw new AccessDeniedException();
            }


            Document document = dmsFactoryInstantiator.getDocumentFactory().getDocument(documentId);
            log.info("Adding document " + document + " to event context");
            EventContext.addParameter("document", document);

            if (!isSecurityInherited)
                secCtrl.updateDMEntitySecurities(s, documentId, securitiesXmlStream, isRecursive, false);


            DataTransfer dt = ftCtrl.startUploadTransaction(s, documentId, false);
            DataTransfer transac = transferFactoryInstantiator.getDataTransferFactory().getDataTransfer(dt.getUid());
            DocumentVersion dv =
                    dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(transac.getDocumentVersionUid());

            User u = authFactoryInstantiator.getAuthenticationSourceFactory().getAuthenticationSource(
                    s.getUserSource()).getUserFactory().getUser(s.getUserName());
            if (!getSecurityAgent().isWritable(d, s.getUserName(), s.getUserSource(),
                    s.getGroups())) {
                throw new AccessDeniedException();
            }
            OutputStream out = new FileOutputStream(
                    new File(ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + transac.getFilePath()), true);

            byte[] b = new byte[2048];
            int readBytes;
            while ((readBytes = documentStream.read(b, 0, b.length)) > -1) {
                out.write(b, 0, readBytes);
            }
            out.flush();
            out.close();
            if (StringUtils.isNotBlank(hashMd5) && StringUtils.isNotBlank(hashSha1)) {
                //Return inpustream on file transmitted
                InputStream in = FileCompressionHelper.getTransactionFile(transac);
                /* Hash Calculation */
                String recHashMD5 = "";
                String recHashSHA1 = "";
                try {
                    HashCalculator hc = new HashCalculator("MD5");
                    recHashMD5 = (hc.hashToString(in).replaceAll(" ", ""));
                    in = FileCompressionHelper.getTransactionFile(transac);
                    hc.setAlgorithm("SHA-1");
                    recHashSHA1 = (hc.hashToString(in).replaceAll(" ", ""));

                } catch (NoSuchAlgorithmException nsae) {

                    hashMd5 = ("error: No algorithm defined");
                    hashSha1 = ("error: No algorithm defined");
                }

                if (!(hashMd5.equalsIgnoreCase(recHashMD5) && hashSha1.equalsIgnoreCase(recHashSHA1))) {
                    new File(
                            ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + transac.getFilePath()).delete();
                    throw new TransferIntegrityException();
                }

                in = FileCompressionHelper.getTransactionFile(transac);
                Vector<DocumentVersion> twoLast =
                        dmsFactoryInstantiator.getDocumentVersionFactory().getTwoLastDocumentVersion(d);
                DocumentVersion jBefore = null;
                for (DocumentVersion h : twoLast) {
                    jBefore = (h.getUid() != dv.getUid() ? h : null);
                }
                if (twoLast.contains(dv) && twoLast.size() > 1 && jBefore != null) {

                    if (jBefore.getStoragePath().equalsIgnoreCase(dv.getStoragePath())) {
                        //Same path: check the hash
                        if (!hashMd5.equalsIgnoreCase(jBefore.getHashMD5()) && !hashSha1.equalsIgnoreCase(
                                jBefore.getHashSHA1())) {
                            Date newDate = new Date();
                            dv.setStoragePath(
                                    new SimpleDateFormat("/yyyy/MM/dd/HH/mm/").format(newDate) + dv.getDocumentUid() + "_" +
                                            newDate.getTime() + ".bin");
                            // storing data
                            dv.setHashMD5(hashMd5);
                            dv.setHashSHA1(hashSha1);
                            dv.setLastUpdateAuthor(s.getUserName());
                            dv.setLastUpdateAuthorSource(s.getUserSource());
                            RepositoryManager.writeVersion(dv, in);
                            FactoryInstantiator.getInstance().getDocumentVersionFactory().updateDocumentVersion(dv);
                        } else {
                            //nothing: same file
                        }
                    } else {
                        //not the same path :
                        //Update:

                        if (!hashMd5.equalsIgnoreCase(dv.getHashMD5()) && !hashSha1.equalsIgnoreCase(dv.getHashSHA1())) {
                            dv.setHashMD5(hashMd5);
                            dv.setHashSHA1(hashSha1);
                            dv.setLastUpdateAuthor(s.getUserName());
                            dv.setLastUpdateAuthorSource(s.getUserSource());
                            RepositoryManager.writeVersion(dv, in);
                            FactoryInstantiator.getInstance().getDocumentVersionFactory().updateDocumentVersion(dv);
                        } else {
                        }
                    }
                } else {
                    if (twoLast.size() == 1 && twoLast.contains(dv)) {
                        dv.setHashMD5(hashMd5);
                        dv.setHashSHA1(hashSha1);
                        dv.setLastUpdateAuthor(s.getUserName());
                        dv.setLastUpdateAuthorSource(s.getUserSource());
                        RepositoryManager.writeVersion(dv, in);
                        FactoryInstantiator.getInstance().getDocumentVersionFactory().updateDocumentVersion(dv);
                    }
                }
                new File(ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + transac.getFilePath()).delete();
                if (transac.isHasBeenCheckedOutOnStart()) {
                    dmsFactoryInstantiator.getLockFactory().checkin(d, u);
                }
                transferFactoryInstantiator.getDataTransferFactory().removeDataTransfer(transac);
            } else {

                log.warn("No hash transmitted. Will calculate");
                //simple add :
                //Return inpustream on file transmitted
                InputStream in = FileCompressionHelper.getTransactionFile(transac);
                /* Hash Calculation */
                HashCalculator hc = new HashCalculator("MD5");
                String recHashMD5 = (hc.hashToString(in).replaceAll(" ", ""));
                in = FileCompressionHelper.getTransactionFile(transac);
                hc.setAlgorithm("SHA-1");
                String recHashSHA1 = (hc.hashToString(in).replaceAll(" ", ""));
                in = FileCompressionHelper.getTransactionFile(transac);
                dv.setHashMD5(recHashMD5);
                dv.setHashSHA1(recHashSHA1);
                dv.setLastUpdateAuthor(s.getUserName());
                dv.setLastUpdateAuthorSource(s.getUserSource());
                RepositoryManager.writeVersion(dv, in);
                FactoryInstantiator.getInstance().getDocumentVersionFactory().updateDocumentVersion(dv);
            }
            new File(ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + transac.getFilePath()).delete();
            if (transac.isHasBeenCheckedOutOnStart()) {
                dmsFactoryInstantiator.getLockFactory().checkin(document, u);
            }
            transferFactoryInstantiator.getDataTransferFactory().removeDataTransfer(transac);


            if (documentTypeId > 0)
                vrsCtrl.updateDocumentVersion(s, documentId, documentTypeId, metasXmlStream);


            EventContext.addParameter("document", document);

            return documentId;
        } catch (IOException e) {
            throw new AccessDeniedException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new ConfigException(e);
        } catch (Exception e){
            throw new DmsKernelException(e);
        }
    }


    private long generateEntitiesFromPath(Session s, String path, boolean isSecurityInherited) {
        String targetPath = path;
        Long documentId = null;
        if (path.startsWith("/")) {
            targetPath = path.substring(1);
        }
        String[] chunks = targetPath.split("/");
        if (chunks.length < 3) {
            throw new PathException("Invalid path : " + path);
        } else {
            long parentUid = -1;
            int parentType = -1;
            for (int i = 0; i < chunks.length; i++) {
                String tmpPath = "";
                for (int j = 0; j <= i; j++) {
                    tmpPath += "/" + chunks[j];
                }
                if (i == 0) {//the workspace
                    DMEntity dm = this.getDmEntity(tmpPath);
                    if (dm == null) {//must create
                        long uid = wksCtrl.createWorkspace(s, chunks[i]);
                        parentUid = uid;
                        parentType = DMEntityType.WORKSPACE;
                    } else {
                        parentUid = dm.getUid();
                        parentType = dm.getType();
                    }
                } else if (i < chunks.length - 1) {//a folder
                    DMEntity dm = this.getDmEntity(tmpPath);
                    if (dm == null) {//must create
                        long uid = fldCtrl.createFolder(s, chunks[i], parentUid, isSecurityInherited);
                        parentUid = uid;
                        parentType = DMEntityType.FOLDER;
                    } else {
                        parentUid = dm.getUid();
                        parentType = dm.getType();
                    }
                } else if (i == chunks.length - 1) {//the document
                    documentId = this.createDocument(s, PathUtils.getFileNameWithoutExtension(chunks[i]),
                            PathUtils.getFileExtension(chunks[i]), null, parentUid, isSecurityInherited);
                }
            }
        }
        return documentId;
    }

    @DmsEvent(eventName = {DmsEventName.FILE_UPLOAD})
    public long createDocumentFromFullPathWithProperties(Session s, String path,
                                                         boolean isSecurityInherited, String securitiesXmlStream,
                                                         boolean isRecursive, long documentTypeId, String metasXmlStream,
                                                         InputStream documentStream, String hashMd5, String hashSha1)
            throws NamingException, ConfigException, DataSourceException, AccessDeniedException {

        try {

            EventContext.get();
            EventContext.clear();
            Long documentId = null;
             /*
                Check for custom path generation
             */

            DocumentType documentType = null;
            if(documentTypeId > -1){
                documentType = dmsFactoryInstantiator.getDocumentTypeFactory().getDocumentType(documentTypeId);
            }
            if (StringUtils.isBlank(path)) {
                //parse metas
                List<MetaValue> values = MetaProcessor.getMetaValuesFromXML(metasXmlStream);

                PathTemplate pathTemplate = dmsFactoryInstantiator.getPathTemplateFactory().getDefaultPathTemplate();
                if (pathTemplate == null) {
                    log.error("default path template not available. can't evaluate path. please contact your administrator");
                    pathTemplate = documentType != null ? MetaPathHandler.defaultPathModel() : MetaPathHandler.defaultUserPathModel();
                } else
                    log.debug("loaded path template #{} - {}", pathTemplate.getId(), pathTemplate.getTemplateName());
                path = new MetaPathHandler().path(new Date(), s, pathTemplate.getPathElements(), documentType, values,
                        path.substring(path.lastIndexOf("\\.")));
                log.info("generated path: {}", path);

            }
            documentId = generateEntitiesFromPath(s, path, isSecurityInherited);
            Document document = dmsFactoryInstantiator.getDocumentFactory().getDocument(documentId);
            log.info("Adding document " + document + " to event context");
            EventContext.addParameter("document", document);
            if (!isSecurityInherited)
                secCtrl.updateDMEntitySecurities(s, documentId, securitiesXmlStream, isRecursive, false);

            vrsCtrl.createDocumentVersion(s, documentId);
            DataTransfer dt = ftCtrl.startUploadTransaction(s, documentId, false);
            DataTransfer transac = transferFactoryInstantiator.getDataTransferFactory().getDataTransfer(dt.getUid());
            DocumentVersion dv =
                    dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(transac.getDocumentVersionUid());


            if (!getSecurityAgent().isWritable(document, s.getUserName(), s.getUserSource(),
                    s.getGroups())) {
                throw new AccessDeniedException();
            }
            OutputStream out = new FileOutputStream(
                    new File(ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + transac.getFilePath()), true);

            byte[] b = new byte[2048];
            int readBytes;
            while ((readBytes = documentStream.read(b, 0, b.length)) > -1) {
                out.write(b, 0, readBytes);
            }
            out.flush();
            out.close();
            if (StringUtils.isNotBlank(hashMd5) && StringUtils.isNotBlank(hashSha1)) {
                User u = authFactoryInstantiator.getAuthenticationSourceFactory().getAuthenticationSource(
                        s.getUserSource()).getUserFactory().getUser(s.getUserName());
                if (!getSecurityAgent().isWritable(document, s.getUserName(), s.getUserSource(), s.getGroups())) {
                    throw new AccessDeniedException();
                }

                //Return inpustream on file transmitted
                InputStream in = FileCompressionHelper.getTransactionFile(transac);

               /* Hash Calculation */
                String recHashMD5 = "";
                String recHashSHA1 = "";
                try {
                    HashCalculator hc = new HashCalculator("MD5");
                    recHashMD5 = (hc.hashToString(in).replaceAll(" ", ""));
                    in = FileCompressionHelper.getTransactionFile(transac);
                    hc.setAlgorithm("SHA-1");
                    recHashSHA1 = (hc.hashToString(in).replaceAll(" ", ""));

                } catch (NoSuchAlgorithmException nsae) {

                    hashMd5 = ("error: No algorithm defined");
                    hashSha1 = ("error: No algorithm defined");
                }

                if (!(hashMd5.equalsIgnoreCase(recHashMD5) && hashSha1.equalsIgnoreCase(recHashSHA1))) {
                    new File(
                            ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + transac.getFilePath()).delete();
                    throw new TransferIntegrityException();
                }

                in = FileCompressionHelper.getTransactionFile(transac);
                Vector<DocumentVersion> twoLast =
                        dmsFactoryInstantiator.getDocumentVersionFactory().getTwoLastDocumentVersion(document);
                DocumentVersion jBefore = null;
                for (DocumentVersion h : twoLast) {
                    jBefore = (h.getUid() != dv.getUid() ? h : null);
                }
                if (twoLast.contains(dv) && twoLast.size() > 1 && jBefore != null) {

                    if (jBefore.getStoragePath().equalsIgnoreCase(dv.getStoragePath())) {
                        //Same path: check the hash
                        if (!hashMd5.equalsIgnoreCase(jBefore.getHashMD5()) && !hashSha1.equalsIgnoreCase(
                                jBefore.getHashSHA1())) {
                            Date newDate = new Date();
                            dv.setStoragePath(
                                    new SimpleDateFormat("/yyyy/MM/dd/HH/mm/").format(newDate) + dv.getDocumentUid() + "_" +
                                            newDate.getTime() + ".bin");
                            // storing data
                            dv.setHashMD5(hashMd5);
                            dv.setHashSHA1(hashSha1);
                            dv.setLastUpdateAuthor(s.getUserName());
                            dv.setLastUpdateAuthorSource(s.getUserSource());
                            RepositoryManager.writeVersion(dv, in);
                            FactoryInstantiator.getInstance().getDocumentVersionFactory().updateDocumentVersion(dv);
                        } else {
                            //nothing: same file
                        }
                    } else {
                        //not the same path :
                        //Update:

                        if (!hashMd5.equalsIgnoreCase(dv.getHashMD5()) && !hashSha1.equalsIgnoreCase(dv.getHashSHA1())) {
                            dv.setHashMD5(hashMd5);
                            dv.setHashSHA1(hashSha1);
                            dv.setLastUpdateAuthor(s.getUserName());
                            dv.setLastUpdateAuthorSource(s.getUserSource());
                            RepositoryManager.writeVersion(dv, in);
                            FactoryInstantiator.getInstance().getDocumentVersionFactory().updateDocumentVersion(dv);
                        } else {
                        }
                    }
                } else {
                    if (twoLast.size() == 1 && twoLast.contains(dv)) {
                        dv.setHashMD5(hashMd5);
                        dv.setHashSHA1(hashSha1);
                        dv.setLastUpdateAuthor(s.getUserName());
                        dv.setLastUpdateAuthorSource(s.getUserSource());
                        RepositoryManager.writeVersion(dv, in);
                        FactoryInstantiator.getInstance().getDocumentVersionFactory().updateDocumentVersion(dv);
                    }
                }
                new File(ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + transac.getFilePath()).delete();
                if (transac.isHasBeenCheckedOutOnStart()) {
                    dmsFactoryInstantiator.getLockFactory().checkin(document, u);
                }
                transferFactoryInstantiator.getDataTransferFactory().removeDataTransfer(transac);
            } else {
                throw new TransferIntegrityException();
            }


            if (documentTypeId > 0)
                vrsCtrl.updateDocumentVersion(s, documentId, documentTypeId, metasXmlStream);

            return documentId;

        } catch (IOException e) {
            throw new AccessDeniedException();
        } catch (Exception e){
            throw new DmsKernelException(e);
        }

    }


    @DmsEvent(eventName = {DmsEventName.FILE_UPLOAD})
    public long createDocumentFromFullPathWithProperties(Session s, String path,
                                                         boolean isSecurityInherited, List<DMEntitySecurity> items,
                                                         boolean isRecursive, long documentTypeId, List<MetaValue> metaValues,
                                                         InputStream documentStream, String hashMd5, String hashSha1)
            throws NamingException, ConfigException, DataSourceException, AccessDeniedException {

        try {

            EventContext initialContext = EventContext.get();
            EventContext.clear();
            Long documentId = null;
            //path contains only documentName
            if (!path.contains("/")) {
                DocumentType documentType = null;
                if(documentTypeId > -1){
                    documentType = dmsFactoryInstantiator.getDocumentTypeFactory().getDocumentType(documentTypeId);
                }
                PathTemplate pathTemplate = dmsFactoryInstantiator.getPathTemplateFactory().getDefaultPathTemplate();
                if (pathTemplate == null) {
                    log.error("default path template not available. can't evaluate path. please contact your administrator");
                    pathTemplate = documentType != null ? MetaPathHandler.defaultPathModel() : MetaPathHandler.defaultUserPathModel();

                }
                log.debug("loaded path template #{} - {}", pathTemplate.getId(), pathTemplate.getTemplateName());
                path = new MetaPathHandler().path(new Date(), s, pathTemplate.getPathElements(), documentType, metaValues,
                        path.substring(path.lastIndexOf("\\."))) + "/" + path;
                log.info("generated path: {}", path);
            }
            documentId = generateEntitiesFromPath(s, path, isSecurityInherited);
            Document document = dmsFactoryInstantiator.getDocumentFactory().getDocument(documentId);
            log.info("Adding document " + document + " to event context");
            log.info("EventContext info " + EventContext.get().getEntity() + " " + EventContext.get().getEntity());
            EventContext.addParameter("document", document);
            if (!isSecurityInherited)
                secCtrl.updateDMEntitySecurities(s, documentId, items, isRecursive, false);
            vrsCtrl.createDocumentVersion(s, documentId);
            DataTransfer dt = ftCtrl.startUploadTransaction(s, documentId, false);
            DataTransfer transac = transferFactoryInstantiator.getDataTransferFactory().getDataTransfer(dt.getUid());
            DocumentVersion dv =
                    dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(transac.getDocumentVersionUid());


            if (!getSecurityAgent().isWritable(document, s.getUserName(), s.getUserSource(),
                    s.getGroups())) {
                throw new AccessDeniedException();
            }
            OutputStream out = new FileOutputStream(
                    new File(ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + transac.getFilePath()), true);

            byte[] b = new byte[2048];
            int readBytes;
            while ((readBytes = documentStream.read(b, 0, b.length)) > -1) {
                out.write(b, 0, readBytes);
            }
            out.flush();
            out.close();


            User u = authFactoryInstantiator.getAuthenticationSourceFactory().getAuthenticationSource(
                    s.getUserSource()).getUserFactory().getUser(s.getUserName());
            if (!getSecurityAgent().isWritable(document, s.getUserName(), s.getUserSource(), s.getGroups())) {
                throw new AccessDeniedException();
            }

            if (StringUtils.isNotBlank(hashMd5) && StringUtils.isNotBlank(hashSha1)) {
                //Return inpustream on file transmitted
                InputStream in = FileCompressionHelper.getTransactionFile(transac);

               /* Hash Calculation */
                String recHashMD5 = "";
                String recHashSHA1 = "";
                try {
                    HashCalculator hc = new HashCalculator("MD5");
                    recHashMD5 = (hc.hashToString(in).replaceAll(" ", ""));
                    in = FileCompressionHelper.getTransactionFile(transac);
                    hc.setAlgorithm("SHA-1");
                    recHashSHA1 = (hc.hashToString(in).replaceAll(" ", ""));

                } catch (NoSuchAlgorithmException nsae) {

                    hashMd5 = ("error: No algorithm defined");
                    hashSha1 = ("error: No algorithm defined");
                }

                if (!(hashMd5.equalsIgnoreCase(recHashMD5) && hashSha1.equalsIgnoreCase(recHashSHA1))) {
                    new File(
                            ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + transac.getFilePath()).delete();
                    throw new TransferIntegrityException();
                }

                in = FileCompressionHelper.getTransactionFile(transac);
                Vector<DocumentVersion> twoLast =
                        dmsFactoryInstantiator.getDocumentVersionFactory().getTwoLastDocumentVersion(document);
                DocumentVersion jBefore = null;
                for (DocumentVersion h : twoLast) {
                    jBefore = (h.getUid() != dv.getUid() ? h : null);
                }
                if (twoLast.contains(dv) && twoLast.size() > 1 && jBefore != null) {

                    if (jBefore.getStoragePath().equalsIgnoreCase(dv.getStoragePath())) {
                        //Same path: check the hash
                        if (!hashMd5.equalsIgnoreCase(jBefore.getHashMD5()) && !hashSha1.equalsIgnoreCase(
                                jBefore.getHashSHA1())) {
                            Date newDate = new Date();
                            dv.setStoragePath(
                                    new SimpleDateFormat("/yyyy/MM/dd/HH/mm/").format(newDate) + dv.getDocumentUid() + "_" +
                                            newDate.getTime() + ".bin");
                            // storing data
                            dv.setHashMD5(hashMd5);
                            dv.setHashSHA1(hashSha1);
                            dv.setLastUpdateAuthor(s.getUserName());
                            dv.setLastUpdateAuthorSource(s.getUserSource());
                            RepositoryManager.writeVersion(dv, in);
                            FactoryInstantiator.getInstance().getDocumentVersionFactory().updateDocumentVersion(dv);
                        } else {
                            //nothing: same file
                        }
                    } else {
                        //not the same path :
                        //Update:

                        if (!hashMd5.equalsIgnoreCase(dv.getHashMD5()) && !hashSha1.equalsIgnoreCase(dv.getHashSHA1())) {
                            dv.setHashMD5(hashMd5);
                            dv.setHashSHA1(hashSha1);
                            dv.setLastUpdateAuthor(s.getUserName());
                            dv.setLastUpdateAuthorSource(s.getUserSource());
                            RepositoryManager.writeVersion(dv, in);
                            FactoryInstantiator.getInstance().getDocumentVersionFactory().updateDocumentVersion(dv);
                        } else {
                        }
                    }
                } else {
                    if (twoLast.size() == 1 && twoLast.contains(dv)) {
                        dv.setHashMD5(hashMd5);
                        dv.setHashSHA1(hashSha1);
                        dv.setLastUpdateAuthor(s.getUserName());
                        dv.setLastUpdateAuthorSource(s.getUserSource());
                        RepositoryManager.writeVersion(dv, in);
                        FactoryInstantiator.getInstance().getDocumentVersionFactory().updateDocumentVersion(dv);
                    }
                }

            } else {
                //simple add :
                //Return inpustream on file transmitted
                InputStream in = FileCompressionHelper.getTransactionFile(transac);
                /* Hash Calculation */
                HashCalculator hc = new HashCalculator("MD5");
                String recHashMD5 = (hc.hashToString(in).replaceAll(" ", ""));
                in = FileCompressionHelper.getTransactionFile(transac);
                hc.setAlgorithm("SHA-1");
                String recHashSHA1 = (hc.hashToString(in).replaceAll(" ", ""));
                in = FileCompressionHelper.getTransactionFile(transac);
                dv.setHashMD5(recHashMD5);
                dv.setHashSHA1(recHashSHA1);
                dv.setLastUpdateAuthor(s.getUserName());
                dv.setLastUpdateAuthorSource(s.getUserSource());
                RepositoryManager.writeVersion(dv, in);
                FactoryInstantiator.getInstance().getDocumentVersionFactory().updateDocumentVersion(dv);
	        }
            new File(ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + transac.getFilePath()).delete();
            if (transac.isHasBeenCheckedOutOnStart()) {
                dmsFactoryInstantiator.getLockFactory().checkin(document, u);
            }
            transferFactoryInstantiator.getDataTransferFactory().removeDataTransfer(transac);

            initialContext.setEntity(document);
            log.debug("documentTypeId {} metaSet {}", documentTypeId, metaValues.size());
            if (documentTypeId > 0) {
                vrsCtrl.updateDocumentVersion(s, documentId, documentTypeId, metaValues);
            }

            return documentId;

        } catch (IOException e) {
            throw new AccessDeniedException();
        } catch (NoSuchAlgorithmException e) {
            throw new ConfigException(e);
        } catch (Exception e){
            throw new DmsKernelException(e);
        }
    }


    private DMEntity getDmEntity(String path) {
        try {
            return dmsFactoryInstantiator.getDmEntityFactory().getEntity(path);
        } catch (Exception ex) {
            return null;
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentController#updateDocument(org.kimios.kernel.security.Session, long, long, java.lang.String, java.lang.String, java.lang.String)
    */
    @DmsEvent(eventName = {DmsEventName.DOCUMENT_UPDATE})
    public void updateDocument(Session s, long uid, long folderUid, String name, String extension, String mimeType)
            throws NamingException,
            CheckoutViolationException, AccessDeniedException, ConfigException, DataSourceException {
        name = name.trim();
        PathUtils.validDmEntityName(name);
        Folder parent = dmsFactoryInstantiator.getFolderFactory().getFolder(folderUid);
        Document test = dmsFactoryInstantiator.getDocumentFactory().getDocument(name, extension, parent);
        if (test != null && test.getUid() != uid) {
            throw new NamingException("A document named \"" + name + "\" already exists at the specified location.");
        }
        Document doc = dmsFactoryInstantiator.getDocumentFactory().getDocument(uid);
        boolean proceed = true;
        if (doc.getFolderUid() != folderUid) {
            proceed = getSecurityAgent().isWritable(doc.getFolder(), s.getUserName(), s.getUserSource(), s.getGroups())
                    && getSecurityAgent().isWritable(parent, s.getUserName(), s.getUserSource(), s.getGroups());
        }
        if (getSecurityAgent().isWritable(doc, s.getUserName(), s.getUserSource(), s.getGroups()) && proceed) {
            doc.setFolder(parent);
            doc.setFolderUid(folderUid);
            doc.setExtension(extension);
            doc.setMimeType(mimeType);
            dmsFactoryInstantiator.getDmEntityFactory().updatePath(doc, name);
            doc.setUpdateDate(new Date());
            dmsFactoryInstantiator.getDocumentFactory().updateDocument(doc);
        } else {
            throw new AccessDeniedException();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentController#deleteDocument(org.kimios.kernel.security.Session, long)
    */
    @DmsEvent(eventName = {DmsEventName.DOCUMENT_DELETE})
    public void deleteDocument(Session s, long uid)
            throws CheckoutViolationException, AccessDeniedException, ConfigException, DataSourceException {
        Document d = dmsFactoryInstantiator.getDocumentFactory().getDocument(uid);
        Lock lock = d.getCheckoutLock();
        if (lock != null) {
            if (!s.getUserName().equals(lock.getUser())) {
                throw new CheckoutViolationException();
            }
        }
        if (getSecurityAgent().isWritable(d, s.getUserName(), s.getUserSource(), s.getGroups()) &&
                getSecurityAgent().isWritable(d.getFolder(), s.getUserName(), s.getUserSource(), s.getGroups())) {
            dmsFactoryInstantiator.getDocumentFactory().deleteDocument(d);
        } else {
            throw new AccessDeniedException();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentController#checkoutDocument(org.kimios.kernel.security.Session, long)
    */
    @DmsEvent(eventName = {DmsEventName.DOCUMENT_CHECKOUT})
    public void checkoutDocument(Session s, long uid)
            throws CheckoutViolationException, AccessDeniedException, ConfigException, DataSourceException {
        Document d = dmsFactoryInstantiator.getDocumentFactory().getDocument(uid);
        if (getSecurityAgent().isWritable(d, s.getUserName(), s.getUserSource(), s.getGroups())) {
            dmsFactoryInstantiator.getLockFactory().checkout(
                    d,
                    authFactoryInstantiator.getAuthenticationSourceFactory().getAuthenticationSource(s.getUserSource())
                            .getUserFactory().getUser(s.getUserName()));
        } else {
            throw new AccessDeniedException();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentController#checkinDocument(org.kimios.kernel.security.Session, long)
    */
    @DmsEvent(eventName = {DmsEventName.DOCUMENT_CHECKIN})
    public void checkinDocument(Session s, long uid)
            throws CheckoutViolationException, AccessDeniedException, ConfigException, DataSourceException {
        Document d = dmsFactoryInstantiator.getDocumentFactory().getDocument(uid);
        if (getSecurityAgent().isWritable(d, s.getUserName(), s.getUserSource(), s.getGroups())) {
            dmsFactoryInstantiator.getLockFactory().checkin(
                    d,
                    authFactoryInstantiator.getAuthenticationSourceFactory().getAuthenticationSource(s.getUserSource())
                            .getUserFactory().getUser(s.getUserName()));
        } else {
            throw new AccessDeniedException();
        }
    }

    /**
     * List related document of the document specified by its uid
     */
    public List<Document> getRelatedDocuments(Session session, long uid)
            throws AccessDeniedException, ConfigException, DataSourceException {
        Document d = dmsFactoryInstantiator.getDocumentFactory().getDocument(uid);
        if (getSecurityAgent().isReadable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            List<Document> relDocs = dmsFactoryInstantiator.getDocumentFactory().getRelatedDocuments(d);
            return getSecurityAgent()
                    .areReadable(relDocs, session.getUserName(), session.getUserSource(), session.getGroups());
        } else {
            throw new AccessDeniedException();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentController#addRelatedDocument(org.kimios.kernel.security.Session, long, long)
    */
    @DmsEvent(eventName = {DmsEventName.DOCUMENT_ADD_RELATED})
    public void addRelatedDocument(Session s, long uid, long relatedDocumentUid)
            throws AccessDeniedException, ConfigException, DataSourceException {
        Document d = dmsFactoryInstantiator.getDocumentFactory().getDocument(uid);
        Document relatedDoc = dmsFactoryInstantiator.getDocumentFactory().getDocument(relatedDocumentUid);
        if (getSecurityAgent().isWritable(d, s.getUserName(), s.getUserSource(), s.getGroups()) &&
                getSecurityAgent().isWritable(relatedDoc, s.getUserName(), s.getUserSource(), s.getGroups())) {
            if (!relatedDoc.getRelatedDocuments().contains(d) && !d.getRelatedDocuments().contains(relatedDoc)) {
                dmsFactoryInstantiator.getDocumentFactory().addRelatedDocument(d, relatedDoc);
                Map<String, Object> args = new HashMap<String, Object>();
                args.put("RelatedDocument", relatedDoc);
            } else {
                throw new AccessDeniedException();
            }
        } else {
            throw new AccessDeniedException();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentController#removeRelatedDocument(org.kimios.kernel.security.Session, long, long)
    */
    @DmsEvent(eventName = {DmsEventName.DOCUMENT_REMOVE_RELATED})
    public void removeRelatedDocument(Session s, long uid, long relatedDocumentUid)
            throws AccessDeniedException, ConfigException, DataSourceException {
        Document d = dmsFactoryInstantiator.getDocumentFactory().getDocument(uid);
        Document relatedDoc = dmsFactoryInstantiator.getDocumentFactory().getDocument(relatedDocumentUid);
        if (getSecurityAgent().isWritable(d, s.getUserName(), s.getUserSource(), s.getGroups()) &&
                getSecurityAgent().isWritable(relatedDoc, s.getUserName(), s.getUserSource(), s.getGroups())) {
            if (d.getRelatedDocuments().contains(relatedDoc) || relatedDoc.getRelatedDocuments().contains(d)) {
                dmsFactoryInstantiator.getDocumentFactory().removeRelatedDocument(d, relatedDoc);
                Map<String, Object> args = new HashMap<String, Object>();
                args.put("RelatedDocument", relatedDoc);
            } else {
                throw new AccessDeniedException();
            }
        } else {
            throw new AccessDeniedException();
        }
    }

    /**
     * Get the bookmarks list of the given user
     */
    public List<Bookmark> getBookmarks(Session session) throws DataSourceException, ConfigException {
        List<DMEntity> bl = dmsFactoryInstantiator.getBookmarkFactory()
                .getBookmarks(session.getUserName(), session.getUserSource(), session.getGroups());
        Vector<Bookmark> vBookmarks = new Vector<Bookmark>();
        List<DMEntity> entities = getSecurityAgent().areReadable(bl, session.getUserName(), session.getUserSource(), session.getGroups());
        for (DMEntity d : entities) {
            if(d instanceof Document){
                if(((Document)d).getTrashed() != null && ((Document)d).getTrashed()){
                    continue;
                }
            }
            Bookmark bmk = new Bookmark(session.getUserName(), session.getUserSource(), SecurityEntityType.USER,
                    d.getUid(), d.getType());
            bmk.setEntity(d);
            vBookmarks.add(bmk);
        }
        return vBookmarks;
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentController#addBookmark(org.kimios.kernel.security.Session, long, int)
    */
    public void addBookmark(Session session, long dmEntityUid)
            throws AccessDeniedException, DataSourceException, ConfigException {
        DMEntity d = dmsFactoryInstantiator.getDmEntityFactory().getEntity(dmEntityUid);
        if (getSecurityAgent().isReadable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            dmsFactoryInstantiator.getBookmarkFactory()
                    .addBookmark(session.getUserName(), session.getUserSource(), 1, d.getUid(), d.getType());
        } else {
            throw new AccessDeniedException();
        }
    }

    public void addGroupBookmark(Session session, long dmEntityUid, String groupId, String groupSource)
            throws AccessDeniedException, DataSourceException, ConfigException {
        DMEntity d = dmsFactoryInstantiator.getDmEntityFactory().getEntity(dmEntityUid);
        if (getSecurityAgent().isReadable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            dmsFactoryInstantiator.getBookmarkFactory()
                    .addBookmark(groupId, groupSource, 2, d.getUid(), d.getType());
        } else {
            throw new AccessDeniedException();
        }
    }


    public void removeGroupBoomark(Session session, long dmEntityUid, String groupId, String groupSource)
            throws AccessDeniedException, DataSourceException, ConfigException {
        DMEntity d = dmsFactoryInstantiator.getDmEntityFactory().getEntity(dmEntityUid);
        if (getSecurityAgent().isReadable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            dmsFactoryInstantiator.getBookmarkFactory()
                    .removeBookmark(groupId, groupSource, 2, d.getUid(), d.getType());
        } else {
            throw new AccessDeniedException();
        }
    }


    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentController#removeBoomark(org.kimios.kernel.security.Session, long, int)
    */
    public void removeBoomark(Session session, long dmEntityUid)
            throws AccessDeniedException, DataSourceException, ConfigException {
        DMEntity d = dmsFactoryInstantiator.getDmEntityFactory().getEntity(dmEntityUid);
        if (getSecurityAgent().isReadable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            dmsFactoryInstantiator.getBookmarkFactory()
                    .removeBookmark(session.getUserName(), session.getUserSource(), 1, d.getUid(), d.getType());
        } else {
            throw new AccessDeniedException();
        }
    }

    /**
     * Get the last consulted items for the given user
     */
    public List<Bookmark> getRecentItems(Session session) throws DataSourceException, ConfigException {
        Vector<DMEntity> ri = dmsFactoryInstantiator.getRecentItemFactory()
                .getRecentItems(session.getUserName(), session.getUserSource());
        ArrayList<Bookmark> vBookmarks = new ArrayList<Bookmark>();
        for (DMEntity d : ri) {
            if (getSecurityAgent().isReadable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
                if(d instanceof Document){
                    if(((Document)d).getTrashed() != null && ((Document)d).getTrashed()){
                        continue;
                    }
                }
                Bookmark b = new Bookmark(session.getUserName(), session.getUserSource(), 1, d.getUid(), d.getType());
                b.setEntity(dmsFactoryInstantiator.getDmEntityFactory().getEntity(d.getUid()));
                vBookmarks.add(b);
            }
        }
        return vBookmarks;
    }

    /**
     * Get the symbolic links created in workspace or folder (not recursive)
     */
    public List<SymbolicLink> getChildSymbolicLinks(Session session, long parentUid)
            throws DataSourceException, ConfigException,
            AccessDeniedException {
        DMEntity parent = dmsFactoryInstantiator.getDmEntityFactory().getEntity(parentUid);
        if (!getSecurityAgent()
                .isReadable(parent, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }
        return dmsFactoryInstantiator.getSymbolicLinkFactory().getChildSymbolicLinks(parent);
    }


    /**
     * Get the symbolic links created in workspace or folder (not recursive)
     */
    public List<org.kimios.kernel.ws.pojo.SymbolicLink> getChildSymbolicLinksPojos(Session session, long parentUid)
            throws DataSourceException, ConfigException,
            AccessDeniedException {
        DMEntity parent = dmsFactoryInstantiator.getDmEntityFactory().getEntity(parentUid);
        if (!getSecurityAgent()
                .isReadable(parent, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }
        List<SymbolicLink> symbolicLinkList = dmsFactoryInstantiator.getSymbolicLinkFactory().getChildSymbolicLinks(parent);
        List<org.kimios.kernel.ws.pojo.SymbolicLink> items = new ArrayList<org.kimios.kernel.ws.pojo.SymbolicLink>();
        for (SymbolicLink symbolicLink : symbolicLinkList){
            org.kimios.kernel.ws.pojo.SymbolicLink symbolicLink1 = symbolicLink.toPojo();
            //reset Pojo Document
            symbolicLink1.setTarget(dmsFactoryInstantiator
                    .getDocumentFactory()
                    .getDocumentPojoFromId(symbolicLink1.getDmEntityUid()));
            items.add(symbolicLink1);
        }
        return items;
    }

    /**
     * Get the symbolic links created for a specific target
     */
    public List<SymbolicLink> getSymbolicLinkCreated(Session session, long targetUid)
            throws DataSourceException, ConfigException,
            AccessDeniedException {
        DMEntity target = dmsFactoryInstantiator.getDmEntityFactory().getEntity(targetUid);
        if (!getSecurityAgent()
                .isReadable(target, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }
        return dmsFactoryInstantiator.getSymbolicLinkFactory().getSymbolicLinks(target);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentController#addSymbolicLink(org.kimios.kernel.security.Session, java.lang.String, long, int, long, int)
    */
    public void addSymbolicLink(Session session, String name, long dmEntityUid, long parentUid)
            throws AccessDeniedException,
            DataSourceException, ConfigException {
        DMEntity parent = dmsFactoryInstantiator.getDmEntityFactory().getEntity(parentUid);
        if (!getSecurityAgent()
                .isWritable(parent, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }
        DMEntity toLink = dmsFactoryInstantiator.getDmEntityFactory().getEntity(dmEntityUid);
        if (!getSecurityAgent()
                .isReadable(toLink, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }
        SymbolicLink sl = new SymbolicLink();
        sl.setDmEntityType(toLink.getType());
        sl.setDmEntityUid(dmEntityUid);
        sl.setParentType(parent.getType());
        sl.setParentUid(parentUid);
        sl.setCreationDate(new Date());
        sl.setUpdateDate(sl.getCreationDate());
        sl.setOwner(session.getUserName());
        sl.setOwnerSource(session.getUserSource());
        sl.setName(toLink.getName());
        sl.setPath(parent.getPath() + toLink.getPath().substring(toLink.getPath().lastIndexOf("/")));
        dmsFactoryInstantiator.getSymbolicLinkFactory().addSymbolicLink(sl);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentController#removeSymbolicLink(org.kimios.kernel.security.Session, long, int, long, int)
    */
    public void removeSymbolicLink(Session session, long dmEntityUid, long parentUid)
            throws AccessDeniedException,
            DataSourceException, ConfigException {

        DMEntity parent = dmsFactoryInstantiator.getDmEntityFactory().getEntity(parentUid);
        if (!getSecurityAgent()
                .isWritable(parent, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }
        DMEntity linked = dmsFactoryInstantiator.getDmEntityFactory().getEntity(dmEntityUid);
        if (!getSecurityAgent()
                .isReadable(linked, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }
        SymbolicLink link = new SymbolicLink();
        link.setDmEntityType(linked.getType());
        link.setDmEntityUid(dmEntityUid);
        link.setParentType(parent.getType());
        link.setParentUid(parentUid);

        dmsFactoryInstantiator.getSymbolicLinkFactory().removeSymbolicLink(link);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentController#updateSymbolicLink(org.kimios.kernel.security.Session, long, int, long, int, java.lang.String, long, int)
    */
    public void updateSymbolicLink(Session session, long dmEntityUid, int dmEntityType, long parentUid, int parentType,
                                   String newName, long newParentUid,
                                   int newParentType) throws AccessDeniedException, DataSourceException, ConfigException {
        SymbolicLink key = dmsFactoryInstantiator.getSymbolicLinkFactory()
                .getSymbolicLink(dmEntityUid, dmEntityType, parentUid, parentType);
        DMEntity parent = null;
        switch (parentType) {
            case DMEntityType.WORKSPACE:
                parent = dmsFactoryInstantiator.getWorkspaceFactory().getWorkspace(parentUid);
                break;
            case DMEntityType.FOLDER:
                parent = dmsFactoryInstantiator.getFolderFactory().getFolder(parentUid);
                break;
            default:
                break;
        }
        if (!getSecurityAgent()
                .isWritable(parent, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }
        DMEntity linked = null;
        switch (dmEntityType) {
            case DMEntityType.FOLDER:
                linked = dmsFactoryInstantiator.getFolderFactory().getFolder(dmEntityUid);
                break;
            case DMEntityType.DOCUMENT:
                linked = dmsFactoryInstantiator.getDocumentFactory().getDocument(dmEntityUid);
                break;
            default:
                break;
        }
        if (!getSecurityAgent()
                .isReadable(linked, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }
        DMEntity newp = null;
        if (newParentType != parentType || newParentUid != parentUid) {
            switch (newParentType) {
                case DMEntityType.WORKSPACE:
                    newp = dmsFactoryInstantiator.getWorkspaceFactory().getWorkspace(newParentUid);
                    break;
                case DMEntityType.FOLDER:
                    newp = dmsFactoryInstantiator.getFolderFactory().getFolder(newParentUid);
                    break;
                default:
                    break;
            }
            if (!getSecurityAgent()
                    .isWritable(newp, session.getUserName(), session.getUserSource(), session.getGroups())) {
                throw new AccessDeniedException();
            }
        }
        key.setParentType(newParentType);
        key.setParentUid(newParentUid);
        key.setName(newName);
        dmsFactoryInstantiator.getSymbolicLinkFactory().updateSymbolicLink(key);
    }

    /**
     * Return the log recorded for a given document
     */
    public List<DMEntityLog<Document>> getDocumentLog(Session s, long documentUid)
            throws AccessDeniedException, ConfigException, DataSourceException {
        Document d = dmsFactoryInstantiator.getDocumentFactory().getDocument(documentUid);
        if (getSecurityAgent().isReadable(d, s.getUserName(), s.getUserSource(), s.getGroups())) {
            return logFactoryInstantiator.getEntityLogFactory().getLogs(d);
        } else {
            throw new AccessDeniedException();
        }
    }

    /**
     * Return the last workflow status for a given document
     */
    public WorkflowStatus getLastWorkflowStatus(Session session, long documentUid)
            throws AccessDeniedException, ConfigException, DataSourceException {
        Document d = dmsFactoryInstantiator.getDocumentFactory().getDocument(documentUid);
        if (getSecurityAgent().isReadable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            long wfsUid =
                    dmsFactoryInstantiator.getDocumentWorkflowStatusFactory().getLastDocumentWorkflowStatus(documentUid)
                            .getWorkflowStatusUid();
            return dmsFactoryInstantiator.getWorkflowStatusFactory().getWorkflowStatus(wfsUid);
        } else {
            throw new AccessDeniedException();
        }
    }

    /**
     * Return the checked out documents for the current user
     */
    public List<Document> getMyCheckedOutDocuments(Session session)
            throws ConfigException, DataSourceException, AccessDeniedException {
        List<Document> docs = dmsFactoryInstantiator.getDocumentFactory().getLockedDocuments(session.getUserName(),
                session.getUserSource());
        docs = getSecurityAgent()
                .areReadable(docs, session.getUserName(), session.getUserSource(), session.getGroups());
        return docs;
    }

    public List<org.kimios.kernel.ws.pojo.Document> getDocumentsPojos(Session session, long folderUid)
            throws AccessDeniedException, ConfigException, DataSourceException {
        List<Document> docs = this.getDocuments(session, folderUid);
        log.debug("documents loaded for folder " + folderUid + ": " + docs.size());
        return dmsFactoryInstantiator.getDocumentFactory().getDocumentsPojos(docs);
    }

    public List<org.kimios.kernel.ws.pojo.Document> convertToPojos(Session session, List<Document> docs)
            throws ConfigException, DataSourceException {
        return dmsFactoryInstantiator.getDocumentFactory().getDocumentsPojos(docs);
    }

    public List<org.kimios.kernel.ws.pojo.DMEntity> convertEntitiesToPojos(Session session, List<DMEntity> items)
            throws ConfigException, DataSourceException {

        List<org.kimios.kernel.ws.pojo.DMEntity> pojos = new ArrayList<org.kimios.kernel.ws.pojo.DMEntity>();
        for(DMEntity u: items){
            if(u.getType() == 3){
                pojos.add(dmsFactoryInstantiator.getDocumentFactory().getDocumentPojoFromId(u.getUid()));
            } else {
                pojos.add(u.toPojo());
            }
        }
        return pojos;
    }

    public List<org.kimios.kernel.ws.pojo.Bookmark> convertBookmarksToPojos(Session session, List<Bookmark> bookmarks)
            throws ConfigException, DataSourceException {

        List<org.kimios.kernel.ws.pojo.Bookmark> bookmarksPojoList =
                new ArrayList<org.kimios.kernel.ws.pojo.Bookmark>();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.addMixIn(Meta.class, AddonDataHandler.MetaMixIn.class);

            for (Bookmark bookmark : bookmarks) {
                org.kimios.kernel.ws.pojo.Bookmark bookmarkPojo = bookmark.toPojo();
                if(bookmarkPojo.getEntity().getType() == 3){
                    //reset pojo
                    bookmarkPojo.setEntity(dmsFactoryInstantiator.getDocumentFactory().getDocumentPojoFromId(
                            bookmarkPojo.getDmEntityUid()
                    ));
                }
                org.kimios.kernel.ws.pojo.DMEntity pojoEntity = bookmarkPojo.getEntity();
                String[] it = new String[]{"", "String", "Number", "Date", "Boolean"};
                if (pojoEntity instanceof org.kimios.kernel.ws.pojo.Folder) {
                    // generate folder meta datas
                    //check if folder has meta

                    Folder f = dmsFactoryInstantiator.getFolderFactory().getFolder(pojoEntity.getUid());
                    if(f.getAddOnDatas() != null){
                        try{

                            AddonDataHandler.AddonDatasWrapper wrapper = mapper.readValue(((Folder) bookmark.getEntity()).getAddOnDatas(),
                                    AddonDataHandler.AddonDatasWrapper.class);

                            if(wrapper.getEntityMetaValues() != null){
                                for(MetaValue mv: wrapper.getEntityMetaValues()){
                                    org.kimios.kernel.ws.pojo.MetaValue pojoMetaValue = new org.kimios.kernel.ws.pojo.MetaValue();
                                    pojoMetaValue.setMeta(mv.getMeta().toPojo());
                                    pojoMetaValue.setMetaId(mv.getMetaUid());
                                    pojoMetaValue.setValue(mv.getValue());
                                    pojoEntity.getMetaDatas().put("MetaData" + it[mv.getMeta().getMetaType()]+ "_" + mv.getMetaUid(), pojoMetaValue);
                                }
                            }

                        }catch (IOException exception){
                            log.error("error while parsing json meta datas for entity folder #" + bookmark.getEntity().getUid(), exception);
                        }
                    }
                }
                bookmarksPojoList.add(bookmarkPojo);
            }

        return bookmarksPojoList;
    }

    public List<org.kimios.kernel.ws.pojo.Document> convertToPojosFromIds(Session session, List<Long> docsIds)
            throws ConfigException, DataSourceException {
        return dmsFactoryInstantiator.getDocumentFactory().getDocumentsPojosFromIds(docsIds);
    }

    public List<org.kimios.kernel.ws.pojo.Document> getRelatedDocumentsPojos(Session session, long documentUid)
            throws AccessDeniedException, ConfigException, DataSourceException {
        List<Document> docs = this.getRelatedDocuments(session, documentUid);
        return dmsFactoryInstantiator.getDocumentFactory().getDocumentsPojos(docs);
    }

    public org.kimios.kernel.ws.pojo.Document getDocumentPojo(Document document)
            throws AccessDeniedException, ConfigException, DataSourceException {
        return this.buidDocumentPojoFromDocument(document);
    }

    @DmsEvent(eventName = {DmsEventName.DOCUMENT_COPY})
    public Document copyDocument(Session session, long sourceDocumentId, String documentCopyName)
            throws AccessDeniedException, ConfigException, DataSourceException {


        Document document = dmsFactoryInstantiator.getDocumentFactory().getDocument(sourceDocumentId);
        if (document != null
                && getSecurityAgent().isReadable(document, session.getUserName(), session.getUserSource(), session.getGroups())) {

            Folder parent = dmsFactoryInstantiator.getFolderFactory().getFolder(document.getFolderUid());
            if (getSecurityAgent().isWritable(parent, session.getUserName(), session.getUserSource(), session.getGroups())) {


                if (documentCopyName.equals(document.getName())) {
                    throw new AccessDeniedException();
                }
                Date creationDate = new Date();
                Document documentCopy =
                        new Document(documentCopyName,
                                session.getUserName(),
                                session.getUserSource(),
                                creationDate, creationDate, parent.getUid(),
                                document.getMimeType(),
                                document.getExtension());
                documentCopy.setFolder(parent);
                dmsFactoryInstantiator.getDmEntityFactory().generatePath(documentCopy);
                dmsFactoryInstantiator.getDocumentFactory().saveDocument(documentCopy);

                DMEntitySecurityFactory dsf = securityFactoryInstantiator.getDMEntitySecurityFactory();
                List<DMEntitySecurity> acls = dsf.getDMEntitySecurities(document);
                for (DMEntitySecurity acl : acls) {
                    DMEntitySecurity aclCopy = new DMEntitySecurity();
                    aclCopy.setDmEntityType(acl.getDmEntityType());
                    aclCopy.setDmEntityUid(acl.getDmEntityUid());
                    aclCopy.setDmEntity(documentCopy);
                    aclCopy.setFullAccess(acl.isFullAccess());
                    aclCopy.setWrite(acl.isWrite());
                    aclCopy.setRead(acl.isRead());
                    aclCopy.setFullName(acl.getFullName());

                    dsf.saveDMEntitySecurity(aclCopy, null);
                }

                DocumentVersion dv = dmsFactoryInstantiator.getDocumentVersionFactory().getLastDocumentVersion(document);
                DocumentVersion newVersion =
                        org.kimios.kernel.factory.DocumentVersionFactory.createDocumentVersion(-1, session.getUserName(), session.getUserSource(), new Date(), new Date(),
                                documentCopy, dv.getCustomVersion(), dv.getLength(), dv.getDocumentType());
                newVersion.setHashMD5(dv.getHashMD5());
                newVersion.setHashSHA1(dv.getHashSHA1());
                newVersion.setLastUpdateAuthor(session.getUserName());
                newVersion.setLastUpdateAuthorSource(session.getUserSource());
                dmsFactoryInstantiator.getDocumentVersionFactory().saveDocumentVersion(newVersion);
                RepositoryManager.copyVersion(dv, newVersion);
                //Copying metas values
                List<MetaValue> vMetas = dmsFactoryInstantiator.getMetaValueFactory().getMetaValues(dv);
                Vector<MetaValue> toSave = new Vector<MetaValue>();
                for (MetaValue m : vMetas) {
                    switch (m.getMeta().getMetaType()) {
                        case MetaType.STRING:
                            toSave.add(new MetaStringValue(newVersion, m.getMeta(),
                                    (m.getValue() != null ? (String) m.getValue() : "")));
                            break;
                        case MetaType.NUMBER:
                            toSave.add(new MetaNumberValue(newVersion, m.getMeta(),
                                    (m.getValue() != null ? (Double) m.getValue() : -1)));
                            break;

                        case MetaType.DATE:
                            toSave.add(new MetaDateValue(newVersion, m.getMeta(),
                                    (m.getValue() != null ? (Date) m.getValue() : null)));
                            break;

                        case MetaType.BOOLEAN:
                            toSave.add(new MetaBooleanValue(newVersion, m.getMeta(),
                                    (m.getValue() != null ? (Boolean) m.getValue() : null)));
                            break;
                        case MetaType.LIST:
                            toSave.add(new MetaListValue(newVersion, m.getMeta(),
                                    (m.getValue() != null ? (List) m.getValue() : null)));
                            break;
                    }
                }
                MetaValueFactory mvf = dmsFactoryInstantiator.getMetaValueFactory();
                for (MetaValue b : toSave) {
                    mvf.saveMetaValue(b);
                }


                EventContext.addParameter("document", document);
                return document;

            } else
                throw new AccessDeniedException();


        } else
            throw new AccessDeniedException();
    }





    /**
     * Get the bookmarked elements in a given path list of the given user, in a sp
     */
    public List<Bookmark> getBookmarksInPath(Session session, String path) throws DataSourceException, ConfigException {
        List<DMEntity> bl = dmsFactoryInstantiator.getBookmarkFactory()
                .getBookmarks(session.getUserName(), session.getUserSource(), session.getGroups(), path);
        List<Bookmark> vBookmarks = new ArrayList<Bookmark>();
        for (DMEntity d : bl) {
            if (getSecurityAgent().isReadable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
                if(d instanceof Document){
                    if(((Document)d).getTrashed() != null && ((Document)d).getTrashed()){
                        continue;
                    }
                }
                Bookmark bmk = new Bookmark(session.getUserName(), session.getUserSource(), SecurityEntityType.USER,
                        d.getUid(), d.getType());
                bmk.setEntity(d);
                vBookmarks.add(bmk);
            }
        }
        return vBookmarks;
    }



    private org.kimios.kernel.ws.pojo.Document buidDocumentPojoFromDocument(Document document)
            throws ConfigException, DataSourceException {
        return FactoryInstantiator.getInstance()
                .getDocumentFactory()
                .getDocumentPojoFromId(document.getUid());
    }
}
