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

package org.kimios.webservices.share;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.kimios.kernel.share.model.MailContact;
import org.kimios.kernel.ws.pojo.Share;
import org.kimios.kernel.ws.pojo.web.ShareByEmailFullContactParam;
import org.kimios.webservices.exceptions.DMServiceException;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Map;

/**
 * Created by farf on 19/07/15.
 */
@WebService(targetNamespace = "http://kimios.org", serviceName = "ShareService")
@CrossOriginResourceSharing(allowAllOrigins = true)
@Api(value="/share", description = "Share Operations")
public interface ShareService {

    /**
     * Share provided documents by email    (SOAP Function)
     */
    void shareByEmail(
            @WebParam(name = "sessionId") String sessionId,
            @WebParam(name = "documentIds") List<Long> documentIds,
            @WebParam(name = "recipients") Map<String, String> recipients,
            @WebParam(name = "subject") String subject,
            @WebParam(name = "content") String content,
            @WebParam(name = "senderAddress") String senderAddress,
            @WebParam(name = "senderName") String senderName,
            @WebParam(name = "defaultSender")  Boolean defaultSender,
            @WebParam(name = "password") String password,
            @WebParam(name = "expirationDate") String expirationDate)
            throws DMServiceException;



    @POST
    @Path("/share-document")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    void shareDocument(@FormParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                       @FormParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityId,
                       @FormParam(value = "targetUserId") @WebParam(name = "targetUserId") String userId,
                       @FormParam(value = "targetUserSource") @WebParam(name = "targetUserId") String userSource,
                       @FormParam(value = "read") @WebParam(name = "read") boolean read,
                       @FormParam(value = "write") @WebParam(name = "write") boolean write,
                       @FormParam(value = "fullAccess") @WebParam(name = "fullAccess") boolean fullAccess,
                       @FormParam(value = "expirationDate") @WebParam(name="expirationDate") String expirationDate,
                       @FormParam(value = "notify")@WebParam(name = "notity") boolean notify) throws DMServiceException;



    @GET
    @Path("/with-me")
    List<Share> listEntitiesSharedWithMe(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
        throws DMServiceException;


    @GET
    @Path("/by-me")
    @Produces("application/json")
    List<Share> listEntitiesSharedByMe(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET
    @Path("/document")
    @Produces(MediaType.APPLICATION_JSON)
    List<Share> listDocumentShares(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId
    ) throws DMServiceException;

    @POST
    @Path("/remove")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    void removeShare(@FormParam(value="sessionId") @WebParam(name = "sessionId") String sessionId,
                     @FormParam(value="shareId") @WebParam(name = "shareId") long shareId)
        throws DMServiceException;



    /**
     * Share provided documents by email    (REST Method)
     */
    @POST
    @Path("/share-by-mail")
    @Consumes("application/json")
    @ApiOperation(value = "Share provided documents by email",
            notes = "Share documents by email. An exception will be thrown if the total size exceed the parameters",
            response = void.class)
    void shareByEmailFullContact(
            @ApiParam(value = "sessionId")
            @QueryParam(value = "sessionId") String sessionId,

            @ApiParam(value = "documentIds")
            @QueryParam(value = "documentIds") List<Long> documentIds,


            @ApiParam(value = "recipients")
                    List<MailContact> recipients,

            @ApiParam(value = "subject")
            @QueryParam(value = "subject") String subject,

            @ApiParam(value = "content")
            @QueryParam(value = "content") String content,

            @ApiParam(value = "senderAddress")
            @QueryParam(value = "senderAddress") String senderAddress,

            @ApiParam(value = "senderName")
            @QueryParam(value = "senderName") String senderName,

            @DefaultValue("false") @ApiParam(value = "Default Sender") @QueryParam(value = "defaultSender") Boolean defaultSender,
            @ApiParam(value = "password") @QueryParam(value = "password") String password,

            @ApiParam(value = "expirationDate")
            @QueryParam(value = "expirationDate") String expirationDate)

            throws DMServiceException;

    @POST @ApiOperation(value ="")
    @Path("/share-by-mail-obj-param")
    @Produces("application/json")
    @Consumes("application/json")
    @WebMethod(operationName = "share-by-mail-obj-param")
    public void shareByEmailFullContact(@ApiParam() ShareByEmailFullContactParam shareByEmailFullContactParam)
            throws DMServiceException;


    @GET
    @Path("/search-contact")
    @ApiOperation(value = "List Contact",
            notes = "Provide list of previously used contacts",
            response = MailContact.class, responseContainer = "List")
    List<MailContact> searchContact(

            @ApiParam(value = "sessionId", name = "sessionId", required = true)
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,

            @ApiParam(value = "query", name = "query", required = true)
            @QueryParam(value = "query") @WebParam(name = "query") String query
            )
        throws DMServiceException;


    @GET
    @Path("/load-default-template")
    @ApiOperation(value = "")
    String loadDefaultTemplate(@ApiParam(value = "sessionId", name = "sessionId", required = true)
                               @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
        throws DMServiceException;

    @GET @ApiOperation(value ="")
    @Path( "/downloadDocumentByToken" )
    @Produces( { MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_FORM_URLENCODED } )
    public Response downloadDocumentByToken(
            @Context UriInfo uriInfo,
            @QueryParam(value = "token") String token,
            @QueryParam(value = "password") String password
    ) throws DMServiceException;

    @POST
    @Path( "/downloadDocumentByTokenAndPassword" )
    @Consumes( MediaType.APPLICATION_FORM_URLENCODED )
    @Produces( { MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_FORM_URLENCODED } )
    public Response downloadDocumentByTokenAndPassword(
            @Context UriInfo uriInfo,
            @FormParam(value = "token") String token,
            @FormParam(value = "password") String password
    ) throws DMServiceException;

    @GET
    @Path("/share")
    @Consumes( MediaType.APPLICATION_FORM_URLENCODED )
    @Produces(MediaType.APPLICATION_JSON)
    Share retrieveShare(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "id") @WebParam(name = "id") long id
    )
            throws DMServiceException;

    @POST
    @Path("/update")
    @Consumes( MediaType.APPLICATION_FORM_URLENCODED )
    @Produces(MediaType.APPLICATION_JSON)
    void updateShare(
            @FormParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @FormParam(value = "id") @WebParam(name = "id") long id,
            @FormParam(value = "targetUserId") @WebParam(name = "targetUserId") String userId,
            @FormParam(value = "targetUserSource") @WebParam(name = "targetUserId") String userSource,
            @FormParam(value = "read") @WebParam(name = "read") boolean read,
            @FormParam(value = "write") @WebParam(name = "write") boolean write,
            @FormParam(value = "fullAccess") @WebParam(name = "fullAccess") boolean fullAccess,
            @FormParam(value = "expirationDate") @WebParam(name="expirationDate") String expirationDate,
            @FormParam(value = "notify")@WebParam(name = "notity") boolean notify
    )
            throws DMServiceException;
}
