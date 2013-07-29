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
package org.kimios.client.controller;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.kimios.client.exception.AccessDeniedException;
import org.kimios.client.exception.ConfigException;
import org.kimios.client.exception.DMSException;
import org.kimios.client.exception.ExceptionHelper;
import org.kimios.kernel.ws.pojo.Bookmark;
import org.kimios.kernel.ws.pojo.Document;
import org.kimios.kernel.ws.pojo.SymbolicLink;
import org.kimios.webservices.DocumentService;

import java.io.InputStream;

/**
 * DocumentController is used to manage and get information about DMSs
 * documents, recent items, symbolic links and bookmarks
 */
public class DocumentController {


    private DocumentService client;

    public DocumentService getClient() {
        return client;
    }

    public void setClient(DocumentService client) {
        this.client = client;
    }

    /**
     * Get a document from its uid
     */
    public Document getDocument(String sessionId, long documentId)
            throws Exception, DMSException, ConfigException, AccessDeniedException {
        try {
            return client.getDocument(sessionId, documentId);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    /**
     * Get children documents from a parent folder
     */
    public Document[] getDocuments(String sessionId, long folderId)
            throws Exception, DMSException, ConfigException, AccessDeniedException {
        try {
            return client.getDocuments(sessionId, folderId);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    /**
     * Create a document
     */
    public long createDocument(String sessionId, Document d, boolean isSecurityInherited)
            throws Exception, DMSException, ConfigException, AccessDeniedException {
        try {
            return client.createDocument(sessionId, d.getName(), d.getExtension(), d.getMimeType(), d.getFolderUid(),
                    isSecurityInherited);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    /**
     * Create a document from full  path
     */
    public long createDocumentFromFullPath(String sessionId, String fullPath, boolean isSecurityInherited)
            throws Exception, DMSException, ConfigException, AccessDeniedException {
        try {
            return client.createDocumentFromFullPath(sessionId, fullPath, isSecurityInherited);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    public void createDocumentWithProperties(String sessionId, String name, String extension, String mimeType,
                                             long folderUid, boolean isSecurityInherited, String securitiesXmlStream,
                                             boolean isRecursive, long documentTypeId, String metasXmlStream,
                                             InputStream documentStream) throws Exception {
        try {
            String hashMD5 = Hex.encodeHexString(DigestUtils.md5(documentStream));
            String hashSHA1 = Hex.encodeHexString(DigestUtils.sha(documentStream));

             client.createDocumentWithProperties(sessionId, name, extension, mimeType, folderUid, isSecurityInherited,
                    securitiesXmlStream, isRecursive, documentTypeId, metasXmlStream, documentStream, hashMD5, hashSHA1);

        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    /**
     * Update a document (for move, rename, extension and mime type change)
     */
    public void updateDocument(String sessionId, Document d)
            throws Exception, DMSException, ConfigException, AccessDeniedException {
        try {
            client.updateDocument(sessionId, d.getUid(), d.getName(), d.getExtension(), d.getMimeType(),
                    d.getFolderUid());
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    /**
     * Check out the document
     */
    public void checkoutDocument(String sessionId, long documentId)
            throws Exception, DMSException, ConfigException, AccessDeniedException {
        try {
            client.checkoutDocument(sessionId, documentId);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    /**
     * Remove the checkout
     */
    public void checkinDocument(String sessionId, long documentId)
            throws Exception, DMSException, ConfigException, AccessDeniedException {
        try {
            client.checkinDocument(sessionId, documentId);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    /**
     * Remove a document
     */
    public void deleteDocument(String sessionId, long documentId)
            throws Exception, DMSException, ConfigException, AccessDeniedException {
        try {
            client.deleteDocument(sessionId, documentId);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    /**
     * Create a new bookmark for the given user
     */
    public void addBookmark(String sessionId, long dmEntityId, int dmEntityType)
            throws Exception, DMSException, ConfigException, AccessDeniedException {
        try {
            client.addBookmark(sessionId, dmEntityId, dmEntityType);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    /**
     * Remove a bookmark for the given user
     */
    public void removeBookmark(String sessionId, long dmEntityId, int dmEntityType)
            throws Exception, DMSException, ConfigException, AccessDeniedException {
        try {
            client.removeBookmark(sessionId, dmEntityId, dmEntityType);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    /**
     * Get the bookmarks list of the given user
     */
    public Bookmark[] getBookmarks(String sessionId)
            throws Exception, DMSException, ConfigException, AccessDeniedException {
        try {
            return client.getBookmarks(sessionId);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    /**
     * Get the last consulted items for the given user
     */
    public Bookmark[] getRecentItems(String sessionId)
            throws Exception, DMSException, ConfigException, AccessDeniedException {
        try {
            return client.getRecentItems(sessionId);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    /**
     * List related document of the document specified by its uid
     */
    public Document[] getRelatedDocuments(String sessionId, long documentId)
            throws Exception, DMSException, ConfigException, AccessDeniedException {
        try {
            return client.getRelatedDocuments(sessionId, documentId);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    /**
     * Add a related document to another
     */
    public void addRelatedDocument(String sessionId, long documentId, long relatedDocumentId)
            throws Exception, DMSException, ConfigException, AccessDeniedException {
        try {
            client.addRelatedDocument(sessionId, documentId, relatedDocumentId);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    /**
     * Remove a related document from another
     */
    public void removeRelatedDocument(String sessionId, long documentId, long relatedDocumentId)
            throws Exception, DMSException, ConfigException, AccessDeniedException {
        try {
            client.removeRelatedDocument(sessionId, documentId, relatedDocumentId);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    /**
     * Get the symbolic links created in workspace or folder (not recursive)
     */
    public SymbolicLink[] getChildSymbolicLinks(String sessionId, long parentId, int parentType)
            throws Exception, DMSException, ConfigException, AccessDeniedException {
        try {
            return client.getChildSymbolicLinks(sessionId, parentId, parentType);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    /**
     * Get the symbolic links created for a specific target
     */
    public SymbolicLink[] getSymbolicLinksCreated(String sessionId, long dmEntityId, int dmEntityType)
            throws Exception {
        try {
            return client.getSymbolicLinksCreated(sessionId, dmEntityId, dmEntityType);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    public void createSymbolicLink(String sessionId, long targetId, int targetType, long parentId, int parentType,
                                   String name)
            throws Exception {
        try {
            client.addSymbolicLink(sessionId, name, targetId, targetType, parentId, parentType);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    /**
     * Update the symbolic link
     */
    public void updateSymbolicLink(String sessionId, long targetId, int targetType, long parentId, int parentType,
                                   long newParentId, int newParentType, String name)
            throws Exception {
        try {
            client.updateSymbolicLink(sessionId, targetId, targetType, parentId, parentType, newParentId,
                    newParentType);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    /**
     * Get the checked out documents for the current user
     */
//    public Document[] getMyCheckedOutDocuments(String sessionId) throws Exception {
//        try {
//            GetMyCheckedOutDocuments op = new GetMyCheckedOutDocuments();
//            op.setSessionUid(sessionId);
//            GetMyCheckedOutDocumentsResponse response = client.getMyCheckedOutDocuments(op);
//            if (response.get_return() != null) {
//                return response.get_return();
//            } else {
//                return new Document[0];
//            }
//        } catch (Exception e) {
//            throw new ExceptionHelper().convertException(e);
//        }
//    }
}

