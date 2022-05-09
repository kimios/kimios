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
package org.kimios.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kimios.exceptions.DocumentDeletedWithActiveShareException;
import org.kimios.kernel.dms.model.Bookmark;
import org.kimios.kernel.dms.model.MetaValue;
import org.kimios.kernel.dms.model.SymbolicLink;
import org.kimios.kernel.security.model.DMEntitySecurity;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.ws.pojo.DMEntity;
import org.kimios.kernel.ws.pojo.DMEntityWrapper;
import org.kimios.kernel.ws.pojo.Document;
import org.kimios.kernel.ws.pojo.WorkflowStatus;
import org.kimios.kernel.ws.pojo.web.ConfirmNewVersionParam;
import org.kimios.webservices.DocumentService;
import org.kimios.webservices.exceptions.DMServiceException;

import javax.jws.WebService;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebService(targetNamespace = "http://kimios.org", serviceName = "DocumentService", name = "DocumentService")
public class DocumentServiceImpl extends CoreService implements DocumentService {


    /**
     * @param sessionId
     * @param documentId
     * @return
     * @throws DMServiceException
     */
    public Document getDocument(String sessionId, long documentId) throws DMServiceException {

        try {
            Session session = getHelper().getSession(sessionId);
            org.kimios.kernel.dms.model.Document doc = documentController.getDocument(session, documentId);
            return documentController.getDocumentPojo(doc);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    @Override
    public DMEntityWrapper getDocumentWrapper(String sessionId, long documentId) throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            org.kimios.kernel.dms.model.Document doc = documentController.getDocument(session, documentId);
            Document document = documentController.getDocumentPojo(doc);

            return new DMEntityWrapper(
                    document,
                    this.securityController.canRead(session, document.getUid()),
                    this.securityController.canWrite(session, document.getUid()),
                    this.securityController.hasFullAccess(session, document.getUid())
            );
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
    public Document[] getDocuments(String sessionId, long folderId) throws DMServiceException {

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

    @Override
    public List<DMEntityWrapper> getDocumentWrappers(String sessionId, long folderId) throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            return this.documentController.getDocumentsPojos(session, folderId).stream()
                    .map(
                            document -> new DMEntityWrapper(
                                    document,
                                    this.securityController.canRead(session, document.getUid()),
                                    this.securityController.canWrite(session, document.getUid()),
                                    this.securityController.hasFullAccess(session, document.getUid())
                            )
                    )
                    .collect(Collectors.toList());
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
                               long folderId, boolean isSecurityInherited) throws DMServiceException {

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
            throws DMServiceException {

        try {
            Session session = getHelper().getSession(sessionId);
            return documentController.createDocument(session, path, isSecurityInherited);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param name
     * @param extension
     * @param isSecurityInherited
     * @param securitiesXmlStream
     * @param documentTypeId
     * @param metasXmlStream
     * @return
     * @throws DMServiceException
     */
    public long createDocumentFromFullPathAndVersion(
            String sessionId,
            String name, String extension,
            boolean isSecurityInherited, String securitiesXmlStream,
            long documentTypeId, String metasXmlStream) throws DMServiceException {

        try {
            Session session = getHelper().getSession(sessionId);
            return documentController.createDocument(session, name, extension, isSecurityInherited,
                    securitiesXmlStream, documentTypeId, metasXmlStream);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param name
     * @param extension
     * @param mimeType
     * @param folderUid
     * @param isSecurityInherited
     * @param securitiesXmlStream
     * @param isRecursive
     * @param documentTypeId
     * @param metasXmlStream
     * @param documentStream
     * @param hashMd5
     * @param hashSha1
     * @return
     * @throws DMServiceException
     */
    public long createDocumentWithProperties(String sessionId, String name, String extension, String mimeType, long folderUid,
                                             boolean isSecurityInherited, String securitiesXmlStream, boolean isRecursive,
                                             long documentTypeId, String metasXmlStream, InputStream documentStream,
                                             String hashMd5, String hashSha1) throws DMServiceException {

        try {
            Session session = getHelper().getSession(sessionId);
            return documentController.createDocumentWithProperties(session, name, extension, mimeType, folderUid,
                    isSecurityInherited, securitiesXmlStream, isRecursive, documentTypeId, metasXmlStream,
                    documentStream, hashMd5, hashSha1);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }


    /**
     * @param sessionId
     * @param path
     * @param isSecurityInherited
     * @param securitiesXmlStream
     * @param isRecursive
     * @param documentTypeId
     * @param metasXmlStream
     * @param documentStream
     * @param hashMd5
     * @param hashSha1
     * @return
     * @throws DMServiceException
     */
    public long createDocumentFromFullPathWithProperties(String sessionId, String path,
                                                         boolean isSecurityInherited, String securitiesXmlStream, boolean isRecursive,
                                                         long documentTypeId, String metasXmlStream, InputStream documentStream,
                                                         String hashMd5, String hashSha1) throws DMServiceException {

        try {
            Session session = getHelper().getSession(sessionId);
            return documentController.createDocumentFromFullPathWithProperties(session, path,
                    isSecurityInherited, securitiesXmlStream, isRecursive, documentTypeId, metasXmlStream,
                    documentStream, hashMd5, hashSha1);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param path
     * @param isSecurityInherited
     * @param securityItemsJsonString
     * @param isRecursive
     * @param documentTypeId
     * @param metaItemsJsonString
     * @param documentStream
     * @param hashMd5
     * @param hashSha1
     * @return
     * @throws DMServiceException
     */
    public long createDocumentFromFullPathWithPropertiesNoHash(String sessionId, String path,
                                                               boolean isSecurityInherited, String securityItemsJsonString, boolean isRecursive,
                                                               Long documentTypeId, String metaItemsJsonString, InputStream documentStream,
                                                               String hashMd5, String hashSha1) throws DMServiceException {

        try {
            Session session = getHelper().getSession(sessionId);

            /*
                Convert pojo list
             */

            List<DMEntitySecurity> securityItems = new ObjectMapper().readValue(securityItemsJsonString, new TypeReference<List<DMEntitySecurity>>() {
            });
            /*for (DMEntitySecurity des : securityItems)
                targetList.add(new org.kimios.kernel.security.DMEntitySecurity(des.getDmEntityUid(), des.getDmEntityType(), des.getName(), des.getSource(), des.getType(), des.isRead(), des.isWrite(), des.isFullAccess())); */


            List<MetaValue> metaValues = new ObjectMapper().readValue(metaItemsJsonString, new TypeReference<List<MetaValue>>(){
            });
            /*for(MetaValue mv: metaItems){
                MetaValueBean mvb = null;
                switch (mv.getMeta().getMetaType()){
                    case MetaType.STRING:
                        mvb = new MetaStringValue();
                        break;
                    case MetaType.NUMBER:
                        mvb = new MetaNumberValue();
                        break;
                    case MetaType.DATE:
                        mvb = new MetaDateValue();
                        break;
                    case MetaType.BOOLEAN:
                        mvb = new MetaDateValue();
                        break;
                    case MetaType.LIST:
                        mvb = new MetaListValue();
                        break;
                }
                mvb.setValue(mv.getValue());
                mvb.setDocumentVersionUid(mv.getDocumentVersionId());
                mvb.setMetaUid(mv.getMetaId());
                metaValues.add(mvb);
            }*/

            return documentController.createDocumentFromFullPathWithProperties(session, path,
                    isSecurityInherited, securityItems, isRecursive, documentTypeId, metaValues,
                    documentStream, hashMd5, hashSha1);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    @Override
    public void uploadNewDocumentVersion(String sessionId, long documentId, InputStream documentStream, String hashMd5,
                                         String hashSha1, String fileName, boolean force) throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            long dataTransferId = documentController.initNewDocumentVersionUpload(session, documentId, documentStream, hashMd5, hashSha1, fileName
                    , force);
            if (! force) {
                this.documentController.checkNewVersionCandidate(session, dataTransferId, fileName);
            }
            this.documentController.confirmNewVersion(session, dataTransferId);
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
                               long folderId) throws DMServiceException {

        try {
            Session session = getHelper().getSession(sessionId);
            documentController.updateDocument(session, documentId, folderId, name, extension, mimeType);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public void updateDocumentTag(String sessionId, long documentId, String tagValue, boolean action) throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            documentController.updateDocumentTag(session, documentId, tagValue, action);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param documentId
     * @throws DMServiceException
     */
    public void deleteDocument(String sessionId, long documentId, boolean force) throws DMServiceException {

        try {
            Session session = getHelper().getSession(sessionId);
            org.kimios.kernel.dms.model.Document document = documentController.getDocumentWithShares(session, documentId);
            documentController.deleteDocument(session, documentId, force);
            if (document.getShareSet() != null
                    && document.getShareSet().size() > 0) {
                // throw exception to send a warning
                throw new DocumentDeletedWithActiveShareException();
            }
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
    public Document[] getRelatedDocuments(String sessionId, long documentId) throws DMServiceException {

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
    public void addRelatedDocument(String sessionId, long documentId, long relatedDocumentId) throws DMServiceException {

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
            throws DMServiceException {

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
    public void checkoutDocument(String sessionId, long documentId) throws DMServiceException {

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
    public void checkinDocument(String sessionId, long documentId) throws DMServiceException {

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
     * @return
     * @throws DMServiceException
     */
    public org.kimios.kernel.ws.pojo.SymbolicLink[] getChildSymbolicLinks(String sessionId, long parentId)
            throws DMServiceException {

        try {
            Session session = getHelper().getSession(sessionId);
            List<org.kimios.kernel.ws.pojo.SymbolicLink> links = documentController.getChildSymbolicLinksPojos(session, parentId);
            return links.toArray(new org.kimios.kernel.ws.pojo.SymbolicLink[]{});
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param targetId
     * @return
     * @throws DMServiceException
     */
    public org.kimios.kernel.ws.pojo.SymbolicLink[] getSymbolicLinksCreated(String sessionId, long targetId)
            throws DMServiceException {

        try {
            Session session = getHelper().getSession(sessionId);
            List<SymbolicLink> vSym = documentController.getSymbolicLinkCreated(session, targetId);
            List<org.kimios.kernel.ws.pojo.SymbolicLink> links = new ArrayList<org.kimios.kernel.ws.pojo.SymbolicLink>();
            for (SymbolicLink link : vSym)
                links.add(link.toPojo());
            return links.toArray(new org.kimios.kernel.ws.pojo.SymbolicLink[]{});
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param name
     * @param dmEntityId
     * @param parentId
     * @throws DMServiceException
     */
    public void addSymbolicLink(String sessionId, String name, long dmEntityId, long parentId)
            throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            documentController.addSymbolicLink(session, name, dmEntityId, parentId);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param dmEntityId
     * @param parentId
     * @throws DMServiceException
     */
    public void removeSymbolicLink(String sessionId, long dmEntityId, long parentId)
            throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            documentController.removeSymbolicLink(session, dmEntityId, parentId);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }




    /**
     * @param sessionId
     * @param dmEntityId
     * @param parentId
     * @param newParentId
     * @throws DMServiceException
     */
    public void updateSymbolicLink(String sessionId, long dmEntityId, long parentId, long newParentId)
            throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            documentController.removeSymbolicLink(session, dmEntityId, parentId);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @return
     * @throws DMServiceException
     */
    public org.kimios.kernel.ws.pojo.Bookmark[] getBookmarks(String sessionId) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            List<Bookmark> vBookmarks = documentController.getBookmarks(session);
            List<org.kimios.kernel.ws.pojo.Bookmark> links =
                    documentController.convertBookmarksToPojos(session, vBookmarks);
            return links.toArray(new org.kimios.kernel.ws.pojo.Bookmark[]{});
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @return
     * @throws DMServiceException
     */
    public org.kimios.kernel.ws.pojo.Bookmark[] getBookmarksInPath(String sessionId, String path) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);
            List<Bookmark> vBookmarks = documentController.getBookmarksInPath(session, path);
            List<org.kimios.kernel.ws.pojo.Bookmark> links =
                    documentController.convertBookmarksToPojos(session, vBookmarks);
            return links.toArray(new org.kimios.kernel.ws.pojo.Bookmark[]{});
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param dmEntityId
     * @throws DMServiceException
     */
    public void addBookmark(String sessionId, long dmEntityId) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            documentController.addBookmark(session, dmEntityId);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param dmEntityId
     * @throws DMServiceException
     */
    public void removeBookmark(String sessionId, long dmEntityId) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            documentController.removeBoomark(session, dmEntityId);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param dmEntityId
     * @param groupId
     * @param groupSource
     * @throws DMServiceException
     */
    public void removeGroupBookmark(String sessionId, long dmEntityId, String groupId, String groupSource) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            documentController.removeGroupBoomark(session, dmEntityId, groupId, groupSource);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param dmEntityId
     * @param groupId
     * @param groupSource
     * @throws DMServiceException
     */
    public void addGroupBookmark(String sessionId, long dmEntityId, String groupId, String groupSource) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);

            documentController.addGroupBookmark(session, dmEntityId, groupId, groupSource);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }


    /**
     * @param sessionId
     * @return
     * @throws DMServiceException
     */
    public org.kimios.kernel.ws.pojo.Bookmark[] getRecentItems(String sessionId) throws DMServiceException {

        try {

            Session session = getHelper().getSession(sessionId);
            List<Bookmark> vBookmarks = documentController.getRecentItems(session);
            List<org.kimios.kernel.ws.pojo.Bookmark> links =
                    documentController.convertBookmarksToPojos(session, vBookmarks);
            return links.toArray(new org.kimios.kernel.ws.pojo.Bookmark[]{});
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
    public WorkflowStatus getLastWorkflowStatus(String sessionId, long documentId) throws DMServiceException {

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
    public Document[] getMyCheckedOutDocuments(String sessionId) throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            List<org.kimios.kernel.dms.model.Document> documents = documentController.getMyCheckedOutDocuments(session);
            List<Document> docs = documentController.convertToPojos(session, documents);
            return docs.toArray(new Document[]{});
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    @Override
    public Document copyDocument(String sessionId, long sourceDocumentId, String documentCopyName) throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            org.kimios.kernel.dms.model.Document document = documentController.copyDocument(session, sourceDocumentId, documentCopyName);
            return documentController.getDocumentPojo(document);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }


    public InputStream exportToCsv(String sessionId, long folderId) throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            List<Document> documents = documentController.getDocumentsPojos(session, folderId);
            return camelTool.generateCsv(documents);

        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    @Override
    public List<DMEntity> retrieveDocumentParents(String sessionId, long documentId) throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            List<DMEntity> parents = this.documentController.retrieveDocumentParents(session, documentId);
            return parents;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    @Override
    public List<DMEntityWrapper> retrieveDocumentParentWrappers(String sessionId, long documentId) throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            List<DMEntity> parents = this.documentController.retrieveDocumentParents(session, documentId);
            return parents.stream()
                    .map(
                            dmEntity -> new DMEntityWrapper(
                                    dmEntity,
                                    this.securityController.canRead(session, dmEntity.getUid()),
                                    this.securityController.canWrite(session, dmEntity.getUid()),
                                    this.securityController.hasFullAccess(session, dmEntity.getUid())
                            )
                    ).collect(Collectors.toList());
        } catch (Exception e) {
            throw getHelper().convertException(e);

        }
    }

    @Override
    public DMEntityWrapper retrieveDocumentWrapper(String sessionId, long documentId) throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);

            Document document = this.getDocument(sessionId, documentId);

            return new DMEntityWrapper(
                    document,
                    this.securityController.canRead(session, documentId),
                    this.securityController.canWrite(session, documentId),
                    this.securityController.hasFullAccess(session, documentId)
            );
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    @Override
    public Long confirmNewVersion(ConfirmNewVersionParam confirmNewVersionParam) throws DMServiceException {
        try {
            Session session = getHelper().getSession(confirmNewVersionParam.getSessionId());
            return this.documentController.confirmNewVersion(session, confirmNewVersionParam.getDataTransferId());
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    @Override
    public Boolean abortNewVersion(ConfirmNewVersionParam confirmNewVersionParam) throws DMServiceException {
        try {
            Session session = getHelper().getSession(confirmNewVersionParam.getSessionId());
            return this.documentController.abortNewVersion(session, confirmNewVersionParam.getDataTransferId());
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

}

