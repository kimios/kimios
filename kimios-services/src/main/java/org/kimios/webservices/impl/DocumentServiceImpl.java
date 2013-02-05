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
package org.kimios.webservices.impl;

import java.util.List;
import java.util.Vector;

import javax.jws.WebService;

import org.kimios.kernel.dms.Bookmark;
import org.kimios.kernel.dms.SymbolicLink;
import org.kimios.kernel.security.Session;
import org.kimios.kernel.ws.pojo.Document;
import org.kimios.kernel.ws.pojo.WorkflowStatus;
import org.kimios.webservices.CoreService;
import org.kimios.webservices.DMServiceException;
import org.kimios.webservices.DocumentService;

@WebService(targetNamespace = "http://kimios.org", serviceName = "ReportingService", name = "ReportingService")
public class DocumentServiceImpl extends CoreService implements DocumentService
{
    /**
     * @param sessionId
     * @param documentId
     * @return
     * @throws DMServiceException
     */
    public Document getDocument(String sessionId, long documentId) throws DMServiceException
    {

        try {
            Session session = getHelper().getSession(sessionId);
            org.kimios.kernel.dms.Document doc = documentController.getDocument(session, documentId);
            return documentController.getDocumentPojo(doc);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param folderId
     * @return
     * @throws DMServiceException
     */
    public Document[] getDocuments(String sessionId, long folderId) throws DMServiceException
    {

        try {
            Session session = getHelper().getSession(sessionId);
            int i = 0;
            List<Document> l = documentController.getDocumentsPojos(session, folderId);
            Document[] pojos = new Document[l.size()];
            for (Document d : l) {
                pojos[i++] = d;
            }
            return pojos;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param name
     * @param extension
     * @param mimeType
     * @param folderId
     * @param isSecurityInherited
     * @return
     * @throws DMServiceException
     */
    public long createDocument(String sessionId, String name, String extension, String mimeType,
            long folderId, boolean isSecurityInherited) throws DMServiceException
    {

        try {
            Session session = getHelper().getSession(sessionId);
            long uid = documentController
                    .createDocument(session, name, extension, mimeType, folderId, isSecurityInherited);
            return uid;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param path
     * @param isSecurityInherited
     * @return
     * @throws DMServiceException
     */
    public long createDocumentFromFullPath(String sessionId, String path, boolean isSecurityInherited)
            throws DMServiceException
    {

        try {
            Session session = getHelper().getSession(sessionId);
            return documentController.createDocument(session, path, isSecurityInherited);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param documentId
     * @param name
     * @param extension
     * @param mimeType
     * @param folderId
     * @throws DMServiceException
     */
    public void updateDocument(String sessionId, long documentId, String name, String extension, String mimeType,
            long folderId) throws DMServiceException
    {

        try {
            Session session = getHelper().getSession(sessionId);
            documentController.updateDocument(session, documentId, folderId, name, extension, mimeType);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param documentId
     * @throws DMServiceException
     */
    public void deleteDocument(String sessionId, long documentId) throws DMServiceException
    {

        try {
            Session session = getHelper().getSession(sessionId);
            documentController.deleteDocument(session, documentId);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param documentId
     * @return
     * @throws DMServiceException
     */
    public Document[] getRelatedDocuments(String sessionId, long documentId) throws DMServiceException
    {

        try {
            Session session = getHelper().getSession(sessionId);
            int i = 0;
            List<Document> l = documentController.getRelatedDocumentsPojos(session, documentId);
            Document[] pojos = new Document[l.size()];
            for (Document d : l) {
                pojos[i++] = d;
            }
            return pojos;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param documentId
     * @param relatedDocumentId
     * @throws DMServiceException
     */
    public void addRelatedDocument(String sessionId, long documentId, long relatedDocumentId) throws DMServiceException
    {

        try {
            Session session = getHelper().getSession(sessionId);
            documentController.addRelatedDocument(session, documentId, relatedDocumentId);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param documentId
     * @param relatedDocumentId
     * @throws DMServiceException
     */
    public void removeRelatedDocument(String sessionId, long documentId, long relatedDocumentId)
            throws DMServiceException
    {

        try {
            Session session = getHelper().getSession(sessionId);
            documentController.removeRelatedDocument(session, documentId, relatedDocumentId);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param documentId
     * @throws DMServiceException
     */
    public void checkoutDocument(String sessionId, long documentId) throws DMServiceException
    {

        try {
            Session session = getHelper().getSession(sessionId);
            documentController.checkoutDocument(session, documentId);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param documentId
     * @throws DMServiceException
     */
    public void checkinDocument(String sessionId, long documentId) throws DMServiceException
    {

        try {
            Session session = getHelper().getSession(sessionId);
            documentController.checkinDocument(session, documentId);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param parentId
     * @param parentType
     * @return
     * @throws DMServiceException
     */
    public org.kimios.kernel.ws.pojo.SymbolicLink[] getChildSymbolicLinks(String sessionId, long parentId,
            int parentType) throws DMServiceException
    {

        try {
            Session session = getHelper().getSession(sessionId);
            Vector<SymbolicLink> vSym = documentController.getChildSymbolicLinks(session, parentId, parentType);
            org.kimios.kernel.ws.pojo.SymbolicLink[] pojos = new org.kimios.kernel.ws.pojo.SymbolicLink[vSym.size()];
            int i = 0;
            for (SymbolicLink sl : vSym) {
                pojos[i] = sl.toPojo();
                i++;
            }
            return pojos;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param targetId
     * @param targetType
     * @return
     * @throws DMServiceException
     */
    public org.kimios.kernel.ws.pojo.SymbolicLink[] getSymbolicLinksCreated(String sessionId, long targetId,
            int targetType) throws DMServiceException
    {

        try {
            Session session = getHelper().getSession(sessionId);
            Vector<SymbolicLink> vSym = documentController.getSymbolicLinkCreated(session, targetId, targetType);
            org.kimios.kernel.ws.pojo.SymbolicLink[] pojos = new org.kimios.kernel.ws.pojo.SymbolicLink[vSym.size()];
            int i = 0;
            for (SymbolicLink sl : vSym) {
                pojos[i] = sl.toPojo();
                i++;
            }
            return pojos;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param name
     * @param dmEntityId
     * @param dmEntityType
     * @param parentId
     * @param parentType
     * @throws DMServiceException
     */
    public void addSymbolicLink(String sessionId, String name, long dmEntityId, int dmEntityType, long parentId,
            int parentType) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionId);

            documentController.addSymbolicLink(session, name, dmEntityId, dmEntityType, parentId, parentType);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param dmEntityId
     * @param dmEntityType
     * @param parentId
     * @param parentType
     * @throws DMServiceException
     */
    public void removeSymbolicLink(String sessionId, long dmEntityId, int dmEntityType, long parentId, int parentType)
            throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionId);

            documentController.removeSymbolicLink(session, dmEntityId, dmEntityType, parentId, parentType);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param dmEntityId
     * @param dmEntityType
     * @param parentId
     * @param parentType
     * @param newParentId
     * @param newParentType
     * @throws DMServiceException
     */
    public void updateSymbolicLink(String sessionId, long dmEntityId, int dmEntityType, long parentId, int parentType,
            long newParentId, int newParentType) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionId);

            documentController.removeSymbolicLink(session, dmEntityId, dmEntityType, parentId, parentType);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @return
     * @throws DMServiceException
     */
    public org.kimios.kernel.ws.pojo.Bookmark[] getBookmarks(String sessionId) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionId);

            Vector<Bookmark> vBookmarks = documentController.getBookmarks(session);
            org.kimios.kernel.ws.pojo.Bookmark[] pojos = new org.kimios.kernel.ws.pojo.Bookmark[vBookmarks.size()];
            int i = 0;
            for (Bookmark b : vBookmarks) {
                pojos[i] = b.toPojo();
                i++;
            }

            return pojos;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param dmEntityId
     * @param dmEntityType
     * @throws DMServiceException
     */
    public void addBookmark(String sessionId, long dmEntityId, int dmEntityType) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionId);

            documentController.addBookmark(session, dmEntityId, dmEntityType);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param dmEntityId
     * @param dmEntityType
     * @throws DMServiceException
     */
    public void removeBookmark(String sessionId, long dmEntityId, int dmEntityType) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionId);

            documentController.removeBoomark(session, dmEntityId, dmEntityType);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @return
     * @throws DMServiceException
     */
    public org.kimios.kernel.ws.pojo.Bookmark[] getRecentItems(String sessionId) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionId);

            Vector<Bookmark> vBookmarks = documentController.getRecentItems(session);
            org.kimios.kernel.ws.pojo.Bookmark[] pojos = new org.kimios.kernel.ws.pojo.Bookmark[vBookmarks.size()];
            int i = 0;
            for (Bookmark b : vBookmarks) {
                pojos[i] = b.toPojo();
                i++;
            }

            return pojos;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param documentId
     * @return
     * @throws DMServiceException
     */
    public WorkflowStatus getLastWorkflowStatus(String sessionId, long documentId) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionId);

            WorkflowStatus wfs = documentController.getLastWorkflowStatus(session, documentId).toPojo();

            return wfs;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * Return the checked out documents for the current user
     */
    public Document[] getMyCheckedOutDocuments(String sessionId) throws DMServiceException
    {
        try {
            Session session = getHelper().getSession(sessionId);
            List<org.kimios.kernel.dms.Document> documents = documentController.getMyCheckedOutDocuments(session);
            List<Document> docs = documentController.convertToPojos(session, documents);
            return docs.toArray(new Document[]{ });
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }
}

