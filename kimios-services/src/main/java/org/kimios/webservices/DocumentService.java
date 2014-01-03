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
package org.kimios.webservices;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.kimios.kernel.ws.pojo.Bookmark;
import org.kimios.kernel.ws.pojo.Document;
import org.kimios.kernel.ws.pojo.SymbolicLink;
import org.kimios.kernel.ws.pojo.WorkflowStatus;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA. User: farf Date: 4/1/12 Time: 4:59 PM To change this template use File | Settings | File
 * Templates.
 */
@Path("/document")
@WebService(targetNamespace = "http://kimios.org", serviceName = "DocumentService")
@CrossOriginResourceSharing(allowAllOrigins = true)
public interface DocumentService {
    @GET
    @Path("/getDocument")
    @Produces("application/json")
    public Document getDocument(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                                @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId) throws DMServiceException;

    @GET
    @Path("/getDocuments")
    @Produces("application/json")
    public Document[] getDocuments(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                                   @QueryParam(value = "folderId") @WebParam(name = "folderId") long folderUid) throws DMServiceException;

    @GET
    @Path("/createDocument")
    @Produces("application/json")
    public long createDocument(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                               @QueryParam(value = "name") @WebParam(name = "name") String name,
                               @QueryParam(value = "extension") @WebParam(name = "extension") String extension,
                               @QueryParam(value = "mimeType") @WebParam(name = "mimeType") String mimeType,
                               @QueryParam(value = "folderId") @WebParam(name = "folderId") long folderUid,
                               @QueryParam(value = "isSecurityInherited") @WebParam(name = "isSecurityInherited")
                               boolean isSecurityInherited) throws DMServiceException;

    @GET
    @Path("/createDocumentFromFullPath")
    @Produces("application/json")
    public long createDocumentFromFullPath(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "path") @WebParam(name = "path") String path,
            @QueryParam(value = "isSecurityInherited") @WebParam(name = "isSecurityInherited")
            boolean isSecurityInherited) throws DMServiceException;

    @POST
    @Path("/createDocumentWithProperties")
    @Produces("application/json")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public long createDocumentWithProperties(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "name") @WebParam(name = "name") String name,
            @QueryParam(value = "extension") @WebParam(name = "extension") String extension,
            @QueryParam(value = "mimeType") @WebParam(name = "mimeType") String mimeType,
            @QueryParam(value = "folderId") @WebParam(name = "folderId") long folderUid,
            @QueryParam(value = "isSecurityInherited") @WebParam(name = "isSecurityInherited") boolean isSecurityInherited,
            @QueryParam(value = "securitiesXmlStream") @WebParam(name = "securitiesXmlStream") String securitiesXmlStream,
            @QueryParam(value = "isRecursive") @WebParam(name = "isRecursive") boolean isRecursive,
            @QueryParam(value = "documentTypeId") @WebParam(name = "documentTypeId") long documentTypeId,
            @QueryParam(value = "metasXmlStream") @WebParam(name = "metasXmlStream") String metasXmlStream,
            @Multipart(value = "document") InputStream documentStream,
            @Multipart(value = "md5") String hashMd5,
            @Multipart(value = "sha1") String hashSha1) throws DMServiceException;



    @POST
    @Path("/createDocumentFromFullPathWithProperties")
    @Produces("application/json")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public long createDocumentFromFullPathWithProperties(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "path") @WebParam(name = "path") String name,
            @QueryParam(value = "isSecurityInherited") @WebParam(name = "isSecurityInherited") boolean isSecurityInherited,
            @QueryParam(value = "securitiesXmlStream") @WebParam(name = "securitiesXmlStream") String securitiesXmlStream,
            @QueryParam(value = "isRecursive") @WebParam(name = "isRecursive") boolean isRecursive,
            @QueryParam(value = "documentTypeId") @WebParam(name = "documentTypeId") long documentTypeId,
            @QueryParam(value = "metasXmlStream") @WebParam(name = "metasXmlStream") String metasXmlStream,
            @Multipart(value = "document") InputStream documentStream,
            @Multipart(value = "md5") String hashMd5,
            @Multipart(value = "sha1") String hashSha1) throws DMServiceException;

    @GET
    @Path("/updateDocument")
    @Produces("application/json")
    public void updateDocument(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                               @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId,
                               @QueryParam(value = "name") @WebParam(name = "name") String name,
                               @QueryParam(value = "extension") @WebParam(name = "extension") String extension,
                               @QueryParam(value = "mimeType") @WebParam(name = "mimeType") String mimeType,
                               @QueryParam(value = "folderId") @WebParam(name = "folderId") long folderUid) throws DMServiceException;

    @GET
    @Path("/deleteDocument")
    @Produces("application/json")
    public void deleteDocument(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                               @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId) throws DMServiceException;

    @GET
    @Path("/getRelatedDocuments")
    @Produces("application/json")
    public Document[] getRelatedDocuments(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId) throws DMServiceException;

    @GET
    @Path("/addRelatedDocument")
    @Produces("application/json")
    public void addRelatedDocument(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                                   @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId,
                                   @QueryParam(value = "relatedDocumentUid") @WebParam(name = "relatedDocumentUid") long relatedDocumentUid)
            throws DMServiceException;

    @GET
    @Path("/removeRelatedDocument")
    @Produces("application/json")
    public void removeRelatedDocument(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                                      @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId,
                                      @QueryParam(value = "relatedDocumentUid") @WebParam(name = "relatedDocumentUid") long relatedDocumentUid)
            throws DMServiceException;

    @GET
    @Path("/checkoutDocument")
    @Produces("application/json")
    public void checkoutDocument(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                                 @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId) throws DMServiceException;

    @GET
    @Path("/checkinDocument")
    @Produces("application/json")
    public void checkinDocument(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                                @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId) throws DMServiceException;

    @GET
    @Path("/getChildSymbolicLinks")
    @Produces("application/json")
    public SymbolicLink[] getChildSymbolicLinks(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "parentId") @WebParam(name = "parentId") long parentUid) throws DMServiceException;

    @GET
    @Path("/getSymbolicLinksCreated")
    @Produces("application/json")
    public SymbolicLink[] getSymbolicLinksCreated(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "targetId") @WebParam(name = "targetId") long targetUid) throws DMServiceException;

    @GET
    @Path("/addSymbolicLink")
    @Produces("application/json")
    public void addSymbolicLink(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                                @QueryParam(value = "name") @WebParam(name = "name") String name,
                                @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityUid,
                                @QueryParam(value = "parentId") @WebParam(name = "parentId") long parentUid)
            throws DMServiceException;

    @GET
    @Path("/removeSymbolicLink")
    @Produces("application/json")
    public void removeSymbolicLink(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                                   @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityUid,
                                   @QueryParam(value = "parentId") @WebParam(name = "parentId") long parentUid)
            throws DMServiceException;

    @GET
    @Path("/updateSymbolicLink")
    @Produces("application/json")
    public void updateSymbolicLink(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                                   @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityUid,
                                   @QueryParam(value = "parentId") @WebParam(name = "parentId") long parentUid,
                                   @QueryParam(value = "newParentId") @WebParam(name = "newParentId") long newParentUid)
            throws DMServiceException;

    @GET
    @Path("/getBookmarks")
    @Produces("application/json")
    public Bookmark[] getBookmarks(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET
    @Path("/addBookmark")
    @Produces("application/json")
    public void addBookmark(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                            @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityUid)
            throws DMServiceException;

    @GET
    @Path("/removeBookmark")
    @Produces("application/json")
    public void removeBookmark(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                               @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityUid)
            throws DMServiceException;

    @GET
    @Path("/getRecentItems")
    @Produces("application/json")
    public Bookmark[] getRecentItems(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET
    @Path("/getLastWorkflowStatus")
    @Produces("application/json")
    public WorkflowStatus getLastWorkflowStatus(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId) throws DMServiceException;

    @GET
    @Path("/getMyCheckedOutDocuments")
    @Produces("application/json")
    public Document[] getMyCheckedOutDocuments(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;
}
