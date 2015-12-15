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
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.kimios.kernel.ws.pojo.DocumentComment;
import org.kimios.kernel.ws.pojo.DocumentVersion;
import org.kimios.kernel.ws.pojo.Meta;
import org.kimios.webservices.exceptions.DMServiceException;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: farf Date: 4/1/12 Time: 4:59 PM
 */
@Path("/document-version")
@WebService(targetNamespace = "http://kimios.org", serviceName = "DocumentVersionService")
@CrossOriginResourceSharing(allowAllOrigins = true)
@Api(value="/document-version", description = "Document Versions Operations")
public interface DocumentVersionService
{
    @GET @ApiOperation(value ="")
    @Path("/getDocumentVersion")
    @Produces("application/json")
    public DocumentVersion getDocumentVersion(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentVersionId") @WebParam(name = "documentVersionId") long documentVersionId)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getLastDocumentVersion")
    @Produces("application/json")
    public DocumentVersion getLastDocumentVersion(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId) throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getDocumentVersions")
    @Produces("application/json")
    public DocumentVersion[] getDocumentVersions(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId) throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/createDocumentVersion")
    @Produces("application/json")
    public long createDocumentVersion(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionUid,
            @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/createDocumentVersionFromLatest")
    @Produces("application/json")
    public long createDocumentVersionFromLatest(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionUid,
            @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId) throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/updateDocumentVersion")
    @Produces("application/json")
    public void updateDocumentVersion(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId,
            @QueryParam(value = "documentTypeId") @WebParam(name = "documentTypeId") long documentTypeId,
            @QueryParam(value = "xmlStream") @WebParam(name = "xmlStream") String xmlStream) throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getMetaString")
    @Produces("application/json")
    public String getMetaString(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentVersionId") @WebParam(name = "documentVersionId") long documentVersionId,
            @QueryParam(value = "metaId") @WebParam(name = "metaId") long metaId) throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getMetaNumber")
    @Produces("application/json")
    public double getMetaNumber(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentVersionId") @WebParam(name = "documentVersionId") long documentVersionId,
            @QueryParam(value = "metaId") @WebParam(name = "metaId") long metaId) throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getMetaDate")
    @Produces("application/json")
    public Date getMetaDate(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentVersionId") @WebParam(name = "documentVersionId") long documentVersionId,
            @QueryParam(value = "metaId") @WebParam(name = "metaId") long metaId) throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getMetaBoolean")
    @Produces("application/json")
    public boolean getMetaBoolean(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentVersionId") @WebParam(name = "documentVersionId") long documentVersionId,
            @QueryParam(value = "metaId") @WebParam(name = "metaId") long metaId) throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getMetas")
    @Produces("application/json")
    public Meta[] getMetas(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentTypeId") @WebParam(name = "documentTypeId") long documentTypeId)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getUnheritedMetas")
    @Produces("application/json")
    public Meta[] getUnheritedMetas(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentTypeId") @WebParam(name = "documentTypeId") long documentTypeId)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getMeta")
    @Produces("application/json")
    public Meta getMeta(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "metaId") @WebParam(name = "metaId") long metaId) throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/updateMetas")
    @Produces("application/json")
    public void updateMetas(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentVersionId") @WebParam(name = "documentVersionId") long documentVersionId,
            @QueryParam(value = "xmlStream") @WebParam(name = "xmlStream") String xmlStream) throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/addDocumentComment")
    @Produces("application/json")
    public void addDocumentComment(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentVersionId") @WebParam(name = "documentVersionId") long documentVersionId,
            @QueryParam(value = "comment") @WebParam(name = "comment") String comment) throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/removeDocumentComment")
    @Produces("application/json")
    public void removeDocumentComment(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "commentId") @WebParam(name = "commentId") long commentId)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/updateDocumentComment")
    @Produces("application/json")
    public void updateDocumentComment(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentVersionId") @WebParam(name = "documentVersionId") long documentVersionId,
            @QueryParam(value = "commentId") @WebParam(name = "commentId") long commentId,
            @QueryParam(value = "newComment") @WebParam(name = "newComment") String newComment)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getDocumentComment")
    @Produces("application/json")
    public DocumentComment getDocumentComment(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "commentId") @WebParam(name = "commentId") long commentId)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getDocumentComments")
    @Produces("application/json")
    public DocumentComment[] getDocumentComments(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentVersionId") @WebParam(name = "documentVersionId") long documentVersionId)
            throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path("/getMetaValues")
    @Produces("application/json") List<org.kimios.kernel.ws.pojo.MetaValue> getMetaValues(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentVersionId") @WebParam(name = "documentVersionId") long documentVersionId)
            throws DMServiceException;
}
