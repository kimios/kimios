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
package org.kimios.kernel.controller;

import org.kimios.exceptions.*;
import org.kimios.kernel.dms.model.*;
import org.kimios.api.events.annotations.DmsEvent;
import org.kimios.api.events.annotations.DmsEventName;
import org.kimios.kernel.log.model.DMEntityLog;
import org.kimios.kernel.security.model.DMEntitySecurity;
import org.kimios.kernel.security.model.Session;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface IDocumentController {
    /**
     * Get a document from its uid
     */
    public Document getDocument(Session session, long uid)
            throws DataSourceException, ConfigException, AccessDeniedException;

    /**
     * Get a document from its name, its extension and its parent folder
     */
    public Document getDocument(Session session, String name, String extension,
                                long folderUid) throws DataSourceException, ConfigException,
            AccessDeniedException;

    /**
     * @param session
     * @return
     * @throws ConfigException
     * @throws DataSourceException
     * @throws AccessDeniedException
     */
    public List<Document> getDocuments(Session session) throws ConfigException,
            DataSourceException, AccessDeniedException;

    /**
     * Get children documents from a parent folder
     */
    public List<Document> getDocuments(Session session, long folderUid)
            throws ConfigException, DataSourceException, AccessDeniedException;

    /**
     * Create a document from its path
     */
    public long createDocument(Session s, String path,
                               boolean isSecurityInherited) throws NamingException,
            ConfigException, DataSourceException, AccessDeniedException, PathException;

    /**
     * Create a document
     */
    @DmsEvent(eventName = {DmsEventName.DOCUMENT_CREATE})
    public long createDocument(Session s, String name,
                               String extension, String mimeType, long folderUid,
                               boolean isSecurityInherited) throws NamingException,
            ConfigException, DataSourceException, AccessDeniedException;


    /**
     * Create a document, and version with properties
     */
    @DmsEvent(eventName = {DmsEventName.DOCUMENT_CREATE})
    public long createDocument(Session s, String name, String extension,
                               boolean isSecurityInherited, String securitiesXmlStream,
                                long documentTypeId, String metasXmlStream)
            throws NamingException, ConfigException, DataSourceException, AccessDeniedException, PathException;

    /**
     * Create a document with properties
     */
    @DmsEvent(eventName = {DmsEventName.FILE_UPLOAD})
    public long createDocumentWithProperties(Session s,
                                             String name,
                                             String extension,
                                             String mimeType,
                                             long folderUid,
                                             boolean isSecurityInherited,
                                             String securitiesXmlStream,
                                             boolean isRecursive,
                                             long documentTypeId,
                                             String metasXmlStream,
                                             InputStream documentStream,
                                             String hashMd5,
                                             String hashSha1) throws IOException;


    /**
     * Create a document from full path with properties
     */
    @DmsEvent(eventName = {DmsEventName.FILE_UPLOAD})
    public long createDocumentFromFullPathWithProperties(Session s,
                                                         String path,
                                                         boolean isSecurityInherited,
                                                         String securitiesXmlStream,
                                                         boolean isRecursive,
                                                         long documentTypeId,
                                                         String metasXmlStream,
                                                         InputStream documentStream,
                                                         String hashMd5,
                                                         String hashSha1) throws IOException;

    /**
     * Create a document from full path with properties
     */
    @DmsEvent(eventName = {DmsEventName.FILE_UPLOAD})
    public long createDocumentFromFullPathWithProperties(Session s,
                                             String path,
                                             boolean isSecurityInherited,
                                             List<DMEntitySecurity> securities,
                                             boolean isRecursive,
                                             long documentTypeId,
                                             List<MetaValue> metaValues,
                                             InputStream documentStream,
                                             String hashMd5,
                                             String hashSha1) throws IOException;

    /**
     * Update a document (for move, rename, extension and mime type change)
     */
    @DmsEvent(eventName = {DmsEventName.DOCUMENT_UPDATE})
    public void updateDocument(Session s, long uid, long folderUid,
                               String name, String extension, String mimeType)
            throws NamingException, CheckoutViolationException,
            AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Remove a document
     */
    @DmsEvent(eventName = {DmsEventName.DOCUMENT_DELETE})
    public void deleteDocument(Session s, long uid, boolean force)
            throws CheckoutViolationException, AccessDeniedException,
            ConfigException, DataSourceException;

    /**
     * Check out the document
     */
    @DmsEvent(eventName = {DmsEventName.DOCUMENT_CHECKOUT})
    public void checkoutDocument(Session s, long uid)
            throws CheckoutViolationException, AccessDeniedException,
            ConfigException, DataSourceException;

    /**
     * Remove the checkout
     */
    @DmsEvent(eventName = {DmsEventName.DOCUMENT_CHECKIN})
    public void checkinDocument(Session s, long uid)
            throws CheckoutViolationException, AccessDeniedException,
            ConfigException, DataSourceException;

    /**
     * List related document of the document specified by its uid
     */
    public List<Document> getRelatedDocuments(Session session, long uid)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Add a related document to another
     */
    @DmsEvent(eventName = {DmsEventName.DOCUMENT_ADD_RELATED})
    public void addRelatedDocument(Session s, long uid, long relatedDocumentUid)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Remove a related document from another
     */
    @DmsEvent(eventName = {DmsEventName.DOCUMENT_REMOVE_RELATED})
    public void removeRelatedDocument(Session s, long uid,
                                      long relatedDocumentUid) throws AccessDeniedException,
            ConfigException, DataSourceException;

    /**
     * Get the bookmarks list of the given user
     */
    public List<Bookmark> getBookmarks(Session session)
            throws DataSourceException, ConfigException;

    /**
     * Create a new bookmark for the given user
     */
    public void addBookmark(Session session, long dmEntityUid)
            throws AccessDeniedException, DataSourceException, ConfigException;

    /**
     * Remove a bookmark for the given user
     */
    public void removeBoomark(Session session, long dmEntityUid)
            throws AccessDeniedException, DataSourceException, ConfigException;

    /**
     * Create a new bookmark for the given group
     */
    public void addGroupBookmark(Session session, long dmEntityUid, String groupId, String groupSource)
            throws AccessDeniedException, DataSourceException, ConfigException;

    /**
     * Remove a bookmark for the given group
     */
    public void removeGroupBoomark(Session session, long dmEntityUid, String groupId, String groupSource)
            throws AccessDeniedException, DataSourceException, ConfigException;

    /**
     * Get the last consulted items for the given user
     */
    public List<Bookmark> getRecentItems(Session session)
            throws DataSourceException, ConfigException;

    /**
     * Get the symbolic links created in workspace or folder (not recursive)
     */
    public List<SymbolicLink> getChildSymbolicLinks(Session session, long parentUid) throws DataSourceException,
            ConfigException, AccessDeniedException;


    /**
     * Get the symbolic links created in workspace or folder Pojos (not recursive)
     */
    public List<org.kimios.kernel.ws.pojo.SymbolicLink> getChildSymbolicLinksPojos(Session session, long parentUid)
            throws DataSourceException, ConfigException,
            AccessDeniedException;

    /**
     * Get the symbolic links created for a specific target
     */
    public List<SymbolicLink> getSymbolicLinkCreated(Session session, long targetUid) throws DataSourceException,
            ConfigException, AccessDeniedException;

    /**
     * Create a new symbolic link for a given entity, in a given entity
     */
    public void addSymbolicLink(Session session, String name, long dmEntityUid, long parentUid)
            throws AccessDeniedException, DataSourceException, ConfigException;

    /**
     * Remove a symbolic link existing in a specific entity
     */
    public void removeSymbolicLink(Session session, long dmEntityUid, long parentUid)
            throws AccessDeniedException, DataSourceException, ConfigException;

    /**
     * Update the symbolic link
     */
    public void updateSymbolicLink(Session session, long dmEntityUid,
                                   int dmEntityType, long parentUid, int parentType, String newName,
                                   long newParentUid, int newParentType) throws AccessDeniedException,
            DataSourceException, ConfigException;

    /**
     * Return the log recorded for a given document
     */
    public List<DMEntityLog<Document>> getDocumentLog(Session s,
                                                        long documentUid) throws AccessDeniedException, ConfigException,
            DataSourceException;

    /**
     * Return the last workflow status for a given document
     */
    public WorkflowStatus getLastWorkflowStatus(Session session,
                                                long documentUid) throws AccessDeniedException, ConfigException,
            DataSourceException;

    /**
     * Return the checked out documents for the current user
     */
    public List<Document> getMyCheckedOutDocuments(Session session)
            throws ConfigException, DataSourceException, AccessDeniedException;

    /**
     * Copy the document in the same folder (last version content & meta), and return it
     */
    public Document copyDocument(Session session, long sourceDocumentId, String documentCopyName)
            throws AccessDeniedException, ConfigException, DataSourceException;

    public List<org.kimios.kernel.ws.pojo.Document> getDocumentsPojos(Session session, long folderUid)
            throws AccessDeniedException, ConfigException, DataSourceException;

    public List<org.kimios.kernel.ws.pojo.Document> getRelatedDocumentsPojos(Session session, long documentUid)
            throws AccessDeniedException, ConfigException, DataSourceException;

    public org.kimios.kernel.ws.pojo.Document getDocumentPojo(Document document)
            throws AccessDeniedException, ConfigException, DataSourceException;

    public List<org.kimios.kernel.ws.pojo.Document> convertToPojos(Session session, List<Document> docs)
            throws ConfigException, DataSourceException;

    public List<org.kimios.kernel.ws.pojo.DMEntity> convertEntitiesToPojos(Session session, List<DMEntity> items)
            throws ConfigException, DataSourceException;

    public List<org.kimios.kernel.ws.pojo.Document> convertToPojosFromIds(Session session, List<Long> docsIds)
            throws ConfigException, DataSourceException;

    public List<org.kimios.kernel.ws.pojo.Bookmark> convertBookmarksToPojos(Session session, List<Bookmark> docs)
            throws ConfigException, DataSourceException;

    public List<Bookmark> getBookmarksInPath(Session session, String path) throws DataSourceException, ConfigException;
}
