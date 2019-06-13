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
package org.kimios.webservices;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.kimios.kernel.ws.pojo.*;
import org.kimios.webservices.exceptions.DMServiceException;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA. User: farf Date: 4/1/12 Time: 4:59 PM To change this template use File | Settings | File
 * Templates.
 */
@Path("/document")
@WebService(targetNamespace = "http://kimios.org", serviceName = "DocumentService")
@CrossOriginResourceSharing(allowAllOrigins = true)
@Api(value="/document", description = "Documents Operations")
public interface DocumentService {
    @GET @ApiOperation(value ="")
    @Path("/getDocument")
    @Produces("application/json")
    public Document getDocument(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                                @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId) throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getDocuments")
    @Produces("application/json")
    public Document[] getDocuments(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                                   @QueryParam(value = "folderId") @WebParam(name = "folderId") long folderUid) throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/createDocument")
    @Produces("application/json")
    public long createDocument(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                               @QueryParam(value = "name") @WebParam(name = "name") String name,
                               @QueryParam(value = "extension") @WebParam(name = "extension") String extension,
                               @QueryParam(value = "mimeType") @WebParam(name = "mimeType") String mimeType,
                               @QueryParam(value = "folderId") @WebParam(name = "folderId") long folderUid,
                               @QueryParam(value = "isSecurityInherited") @WebParam(name = "isSecurityInherited")
                               boolean isSecurityInherited) throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/createDocumentFromFullPath")
    @Produces("application/json")
    public long createDocumentFromFullPath(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "path") @WebParam(name = "path") String path,
            @QueryParam(value = "isSecurityInherited") @WebParam(name = "isSecurityInherited")
            boolean isSecurityInherited) throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/createDocumentFromFullPathAndVersion")
    @Produces("application/json")
    public long createDocumentFromFullPathAndVersion(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "name") @WebParam(name = "name") String name,
            @QueryParam(value = "extension") @WebParam(name = "extension") String extension,
            @QueryParam(value = "isSecurityInherited") @WebParam(name = "isSecurityInherited") boolean isSecurityInherited,
            @QueryParam(value = "securitiesXmlStream") @WebParam(name = "securitiesXmlStream") String securitiesXmlStream,
            @QueryParam(value = "documentTypeId") @WebParam(name = "documentTypeId") long documentTypeId,
            @QueryParam(value = "metasXmlStream") @WebParam(name = "metasXmlStream") String metasXmlStream
            ) throws DMServiceException;

    @POST @ApiOperation(value ="")
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



    @POST @ApiOperation(value ="")
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

    @POST @ApiOperation(value ="")
    @Path("/createDocumentFromFullPathWithPropertiesNoHash")
    @Produces("application/json")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public long createDocumentFromFullPathWithPropertiesNoHash(
            @ApiParam(name = "sessionId")
            @Multipart(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @ApiParam(name = "path")
            @Multipart(value = "path") @WebParam(name = "path") String name,
            @ApiParam(name = "isSecurityInherited")
            @Multipart(value = "isSecurityInherited") @WebParam(name = "isSecurityInherited") boolean isSecurityInherited,
            @ApiParam(name = "securityItems")
            @Multipart(value = "securityItems") @WebParam(name = "securityItems") String securityItemsJson,
            @ApiParam(name = "isRecursive")
            @Multipart(value = "isRecursive") @WebParam(name = "isRecursive") boolean isRecursive,
            @ApiParam(name = "documentTypeId")
            @Multipart(value = "documentTypeId") @WebParam(name = "documentTypeId") Long documentTypeId,
            @ApiParam(name = "metaItems")
            @Multipart(value = "metaItems") @WebParam(name = "metaItems") String metaValuesJson,
            @ApiParam(name = "document", required = true)
            @Multipart(value = "document") InputStream documentStream,
            @ApiParam(name = "md5")
            @DefaultValue(value = "") @Multipart(value = "md5", required = false) String hashMd5,
            @ApiParam(name = "sha1")
            @DefaultValue(value = "") @Multipart(value = "sha1", required = false) String hashSha1) throws DMServiceException;



    @GET @ApiOperation(value ="")
    @Path("/updateDocument")
    @Produces("application/json")
    public void updateDocument(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                               @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId,
                               @QueryParam(value = "name") @WebParam(name = "name") String name,
                               @QueryParam(value = "extension") @WebParam(name = "extension") String extension,
                               @QueryParam(value = "mimeType") @WebParam(name = "mimeType") String mimeType,
                               @QueryParam(value = "folderId") @WebParam(name = "folderId") long folderUid) throws DMServiceException;

    @POST @ApiOperation(value ="")
    @Path("/updateDocumentTag")
    @Produces("application/json")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void updateDocumentTag(
            @ApiParam(name = "sessionId", required = true)
            @Multipart(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @ApiParam(name = "documentId", required = true)
            @Multipart(value = "documentId") @WebParam(name = "documentId") long documentId,
            @ApiParam(name = "tagId", required = true)
            @Multipart(value = "tagId") @WebParam(name = "tagId") long tagId,
            @ApiParam(name = "action", required = true)
            @Multipart(value = "action") @WebParam(name = "action") boolean action
    ) throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/deleteDocument")
    @Produces("application/json")
    public void deleteDocument(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                               @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId,
                               @QueryParam(value = "force") @WebParam(name = "force") @DefaultValue(value = "false") boolean force) throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getRelatedDocuments")
    @Produces("application/json")
    public Document[] getRelatedDocuments(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId) throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/addRelatedDocument")
    @Produces("application/json")
    public void addRelatedDocument(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                                   @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId,
                                   @QueryParam(value = "relatedDocumentUid") @WebParam(name = "relatedDocumentUid") long relatedDocumentUid)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/removeRelatedDocument")
    @Produces("application/json")
    public void removeRelatedDocument(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                                      @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId,
                                      @QueryParam(value = "relatedDocumentUid") @WebParam(name = "relatedDocumentUid") long relatedDocumentUid)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/checkoutDocument")
    @Produces("application/json")
    public void checkoutDocument(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                                 @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId) throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/checkinDocument")
    @Produces("application/json")
    public void checkinDocument(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                                @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId) throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getChildSymbolicLinks")
    @Produces("application/json")
    public SymbolicLink[] getChildSymbolicLinks(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "parentId") @WebParam(name = "parentId") long parentUid) throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getSymbolicLinksCreated")
    @Produces("application/json")
    public SymbolicLink[] getSymbolicLinksCreated(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "targetId") @WebParam(name = "targetId") long targetUid) throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/addSymbolicLink")
    @Produces("application/json")
    public void addSymbolicLink(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                                @QueryParam(value = "name") @WebParam(name = "name") String name,
                                @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityUid,
                                @QueryParam(value = "parentId") @WebParam(name = "parentId") long parentUid)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/removeSymbolicLink")
    @Produces("application/json")
    public void removeSymbolicLink(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                                   @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityUid,
                                   @QueryParam(value = "parentId") @WebParam(name = "parentId") long parentUid)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/updateSymbolicLink")
    @Produces("application/json")
    public void updateSymbolicLink(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                                   @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityUid,
                                   @QueryParam(value = "parentId") @WebParam(name = "parentId") long parentUid,
                                   @QueryParam(value = "newParentId") @WebParam(name = "newParentId") long newParentUid)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getBookmarks")
    @Produces("application/json")
    public Bookmark[] getBookmarks(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getBookmarksInPath")
    @Produces("application/json")
    public Bookmark[] getBookmarksInPath(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                                   @QueryParam(value = "path") @WebParam(name = "path") String path)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/addBookmark")
    @Produces("application/json")
    public void addBookmark(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                            @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityUid)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/removeBookmark")
    @Produces("application/json")
    public void removeBookmark(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                               @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityUid)
            throws DMServiceException;


    @GET @ApiOperation(value ="")
    @Path("/addGroupBookmark")
    @Produces("application/json")
    public void addGroupBookmark(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                            @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityUid,
                            @QueryParam(value = "groupId") @WebParam(name = "groupId") String groupId,
                            @QueryParam(value = "groupSource") @WebParam(name = "groupSource") String groupSource)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/removeGroupBookmark")
    @Produces("application/json")
    public void removeGroupBookmark(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                               @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityUid,
                               @QueryParam(value = "groupId") @WebParam(name = "groupId") String groupId,
                               @QueryParam(value = "groupSource") @WebParam(name = "groupSource") String groupSource)
            throws DMServiceException;


    @GET @ApiOperation(value ="")
    @Path("/getRecentItems")
    @Produces("application/json")
    public Bookmark[] getRecentItems(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getLastWorkflowStatus")
    @Produces("application/json")
    public WorkflowStatus getLastWorkflowStatus(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId) throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getMyCheckedOutDocuments")
    @Produces("application/json")
    public Document[] getMyCheckedOutDocuments(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/copyDocument")
    @Produces("application/json")
    public Document copyDocument(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "sourceDocumentId") @WebParam(name = "sourceDocumentId") long sourceDocumentId,
            @QueryParam(value = "documentCopyName") @WebParam(name = "documentCopyName") String documentCopyName)
            throws DMServiceException;


    @GET @ApiOperation(value ="")
    @Path("/get/{folderId}/csv")
    @Consumes(value = "*/*")
    @Produces("text/csv")
    public InputStream exportToCsv(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                                @PathParam(value = "folderId") @WebParam(name = "folderId") long folderUid) throws DMServiceException;
}
