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
package org.kimios.kernel.controller.impl;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.controller.*;
import org.kimios.kernel.controller.utils.PathUtils;
import org.kimios.kernel.dms.*;
import org.kimios.kernel.exception.*;
import org.kimios.kernel.filetransfer.DataTransfer;
import org.kimios.kernel.log.DMEntityLog;
import org.kimios.kernel.security.DMEntitySecurity;
import org.kimios.kernel.security.DMEntitySecurityFactory;
import org.kimios.kernel.security.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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
        return getSecurityAgent().areReadable(d, session.getUserName(), session.getUserSource(), session.getGroups());
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentController#createDocument(org.kimios.kernel.security.Session, java.lang.String, java.lang.String, java.lang.String, java.lang.String, long, boolean)
    */
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

                    dsf.saveDMEntitySecurity(des);
                }
            }
            return d.getUid();
        } else {
            throw new AccessDeniedException();
        }
    }

    public long createDocument(Session s, String path, boolean isSecurityInherited)
            throws NamingException, ConfigException, DataSourceException, AccessDeniedException, PathException {
        String targetPath = path;
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
                        long uid = fldCtrl.createFolder(s, chunks[i], parentUid, parentType, isSecurityInherited);
                        parentUid = uid;
                        parentType = DMEntityType.FOLDER;
                    } else {
                        parentUid = dm.getUid();
                        parentType = dm.getType();
                    }
                } else if (i == chunks.length - 1) {//the document
                    return this.createDocument(s, PathUtils.getFileNameWithoutExtension(chunks[i]),
                            PathUtils.getFileExtension(chunks[i]), null, parentUid, isSecurityInherited);
                }
            }
        }
        return -1;
    }

    public long createDocumentWithProperties(Session s, String name, String extension, String mimeType, long folderUid,
                                             boolean isSecurityInherited, String securitiesXmlStream,
                                             boolean isRecursive, long documentTypeId, String metasXmlStream,
                                             InputStream documentStream, String hashMd5, String hashSha1)
            throws NamingException, ConfigException, DataSourceException, AccessDeniedException {

        try {
            long documentId = createDocument(s, name, extension, mimeType, folderUid, isSecurityInherited);

            if (!isSecurityInherited)
                secCtrl.updateDMEntitySecurities(s, documentId, DMEntityType.DOCUMENT, securitiesXmlStream, isRecursive);

            long versionId = vrsCtrl.createDocumentVersion(s, documentId);
            DataTransfer dt = ftCtrl.startUploadTransaction(s, documentId, false);

            ftCtrl.uploadDocument(s, dt.getUid(), documentStream, hashMd5, hashSha1);

            if (documentId > 0)
                vrsCtrl.updateDocumentVersion(s, documentId, documentTypeId, metasXmlStream);

            return documentId;

        } catch (IOException e) {
            throw new AccessDeniedException();
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
    public Vector<Bookmark> getBookmarks(Session session) throws DataSourceException, ConfigException {
        Vector<DMEntity> bl = dmsFactoryInstantiator.getBookmarkFactory()
                .getBookmarks(session.getUserName(), session.getUserSource());
        Vector<Bookmark> vBookmarks = new Vector<Bookmark>();
        for (DMEntity d : bl) {
            if (getSecurityAgent().isReadable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
                vBookmarks.add(new Bookmark(session.getUserName(), session.getUserSource(), d.getUid(), d.getType()));
            }
        }
        return vBookmarks;
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentController#addBookmark(org.kimios.kernel.security.Session, long, int)
    */
    public void addBookmark(Session session, long dmEntityUid, int dmEntityType)
            throws AccessDeniedException, DataSourceException, ConfigException {
        DMEntity d = dmsFactoryInstantiator.getDmEntityFactory().getEntity(dmEntityUid);
        if (getSecurityAgent().isReadable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            dmsFactoryInstantiator.getBookmarkFactory()
                    .addBookmark(session.getUserName(), session.getUserSource(), d.getUid(), d.getType());
        } else {
            throw new AccessDeniedException();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentController#removeBoomark(org.kimios.kernel.security.Session, long, int)
    */
    public void removeBoomark(Session session, long dmEntityUid, int dmEntityType)
            throws AccessDeniedException, DataSourceException, ConfigException {
        DMEntity d = dmsFactoryInstantiator.getDmEntityFactory().getEntity(dmEntityUid);
        if (getSecurityAgent().isReadable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            dmsFactoryInstantiator.getBookmarkFactory()
                    .removeBookmark(session.getUserName(), session.getUserSource(), d.getUid(), d.getType());
        } else {
            throw new AccessDeniedException();
        }
    }

    /**
     * Get the last consulted items for the given user
     */
    public Vector<Bookmark> getRecentItems(Session session) throws DataSourceException, ConfigException {
        Vector<DMEntity> ri = dmsFactoryInstantiator.getRecentItemFactory()
                .getRecentItems(session.getUserName(), session.getUserSource());
        Vector<Bookmark> vBookmarks = new Vector<Bookmark>();
        for (DMEntity d : ri) {
            if (getSecurityAgent().isReadable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
                vBookmarks.add(new Bookmark(session.getUserName(), session.getUserSource(), d.getUid(), d.getType()));
            }
        }
        return vBookmarks;
    }

    /**
     * Get the symbolic links created in workspace or folder (not recursive)
     */
    public Vector<SymbolicLink> getChildSymbolicLinks(Session session, long parentUid, int parentType)
            throws DataSourceException, ConfigException,
            AccessDeniedException {
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
                .isReadable(parent, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }
        return dmsFactoryInstantiator.getSymbolicLinkFactory().getChildSymbolicLinks(parent);
    }

    /**
     * Get the symbolic links created for a specific target
     */
    public Vector<SymbolicLink> getSymbolicLinkCreated(Session session, long targetUid, int targetType)
            throws DataSourceException, ConfigException,
            AccessDeniedException {
        DMEntity target = null;
        switch (targetType) {
            case DMEntityType.FOLDER:
                target = dmsFactoryInstantiator.getFolderFactory().getFolder(targetUid);
                break;
            case DMEntityType.DOCUMENT:
                target = dmsFactoryInstantiator.getDocumentFactory().getDocument(targetUid);
                break;
            default:
                break;
        }
        if (!getSecurityAgent()
                .isReadable(target, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }
        return dmsFactoryInstantiator.getSymbolicLinkFactory().getSymbolicLinks(target);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentController#addSymbolicLink(org.kimios.kernel.security.Session, java.lang.String, long, int, long, int)
    */
    public void addSymbolicLink(Session session, String name, long dmEntityUid, int dmEntityType, long parentUid,
                                int parentType) throws AccessDeniedException,
            DataSourceException, ConfigException {
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
        DMEntity toLink = null;
        switch (dmEntityType) {
            case DMEntityType.FOLDER:
                toLink = dmsFactoryInstantiator.getFolderFactory().getFolder(dmEntityUid);
                break;
            case DMEntityType.DOCUMENT:
                toLink = dmsFactoryInstantiator.getDocumentFactory().getDocument(dmEntityUid);
                break;
            default:
                break;
        }
        if (!getSecurityAgent()
                .isReadable(toLink, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }
        SymbolicLink sl = new SymbolicLink();
        sl.setDmEntityType(dmEntityType);
        sl.setDmEntityUid(dmEntityUid);
        sl.setParentType(parentType);
        sl.setParentUid(parentUid);
        sl.setCreationDate(new Date());
        sl.setOwner(session.getUserName());
        sl.setOwnerSource(session.getUserSource());
        sl.setName(name);
        dmsFactoryInstantiator.getSymbolicLinkFactory().addSymbolicLink(sl);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentController#removeSymbolicLink(org.kimios.kernel.security.Session, long, int, long, int)
    */
    public void removeSymbolicLink(Session session, long dmEntityUid, int dmEntityType, long parentUid, int parentType)
            throws AccessDeniedException,
            DataSourceException, ConfigException {
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
        dmsFactoryInstantiator.getSymbolicLinkFactory().removeSymbolicLink(key);
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
    public Vector<DMEntityLog<Document>> getDocumentLog(Session s, long documentUid)
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
        return dmsFactoryInstantiator.getDocumentFactory().getDocumentsPojos(docs);
    }

    public List<org.kimios.kernel.ws.pojo.Document> convertToPojos(Session session, List<Document> docs)
            throws ConfigException, DataSourceException {
        return dmsFactoryInstantiator.getDocumentFactory().getDocumentsPojos(docs);
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
        return document.toPojo();
    }
}
