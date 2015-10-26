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

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.kimios.kernel.share.model.MailContact;
import org.kimios.webservices.exceptions.DMServiceException;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * Created by farf on 19/07/15.
 */
@WebService(targetNamespace = "http://kimios.org", serviceName = "ShareService")
@CrossOriginResourceSharing(allowAllOrigins = true)
public interface ShareService {

    /**
     * Share provided documents by email    (SOAP Function)
     */
    void shareByEmail(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentIds") @WebParam(name = "documentIds") List<Long> documentIds,
            @QueryParam(value = "recipients") @WebParam(name = "recipients") Map<String, String> recipients,
            @QueryParam(value = "subject") @WebParam(name = "subject") String subject,
            @QueryParam(value = "content") @WebParam(name = "content") String content,
            @QueryParam(value = "senderAddress") @WebParam(name = "senderAddress") String senderAddress,
            @QueryParam(value = "senderName") @WebParam(name = "senderName") String senderName,
            @DefaultValue("false") @QueryParam(value = "defaultSender") @WebParam(name = "defaultSender")  Boolean defaultSender)
            throws DMServiceException;




    /**
     * Share provided documents by email    (REST Method)
     */
    @POST
    @Path("/share-by-mail")
    @Consumes("application/json")
    void shareByEmailFullContact(
            @QueryParam(value = "sessionId") String sessionId,
            @QueryParam(value = "documentIds") List<Long> documentIds,
            List<MailContact> recipients,
            @QueryParam(value = "subject")  String subject,
            @QueryParam(value = "content")  String content,
            @QueryParam(value = "senderAddress")  String senderAddress,
            @QueryParam(value = "senderName") String senderName,
            @DefaultValue("false") @QueryParam(value = "defaultSender")  Boolean defaultSender)
            throws DMServiceException;


    @GET
    @Path("/search-contact")
    List<MailContact> searchContact(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "query") @WebParam(name = "query") String query
            )
        throws DMServiceException;
}
