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

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.kimios.kernel.ws.pojo.DMEntity;
import org.kimios.kernel.ws.pojo.DMEntityAttribute;
import org.kimios.webservices.exceptions.DMServiceException;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

/**
 */
@Path("/extension")
@WebService(targetNamespace = "http://kimios.org", serviceName = "ExtensionService")
@CrossOriginResourceSharing(allowAllOrigins = true)
public interface ExtensionService
{
    @GET
    @Path("/getEntityAttributeValue")
    @Produces("application/json")
    public String getEntityAttributeValue(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityId,
            @QueryParam(value = "attributeName") @WebParam(name = "attributeName") String attributeName)
            throws DMServiceException;

    @GET
    @Path("/getEntityAttribute")
    @Produces("application/json")
    public DMEntityAttribute getEntityAttribute(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityId,
            @QueryParam(value = "attributeName") @WebParam(name = "attributeName") String attributeName)
            throws DMServiceException;

    @GET
    @Path("/getEntityAttributes")
    @Produces("application/json")
    public DMEntityAttribute[] getEntityAttributes(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityId,
            @QueryParam(value = "attributeName") @WebParam(name = "attributeName") String attributeName)
            throws DMServiceException;

    @GET
    @Path("/setEntityAttribute")
    @Produces("application/json")
    public void setEntityAttribute(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityId,
            @QueryParam(value = "attributeName") @WebParam(name = "attributeName") String attributeName,
            @QueryParam(value = "attributeValue") @WebParam(name = "attributeValue") String attributeValue,
            @QueryParam(value = "isIndexed") @WebParam(name = "isIndexed") boolean isIndexed)
            throws DMServiceException;

    @GET
    @Path("/generatePasswordForUser")
    @Produces("application/json")
    public String generatePasswordForUser(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "userId") @WebParam(name = "userId") String userId,
            @QueryParam(value = "userSource") @WebParam(name = "userSource") String userSource,
            @QueryParam(value = "sendMail") @WebParam(name = "sendMail") boolean sendMail) throws DMServiceException;


    @GET
    @Path("/trash")
    @Produces("application/json")
    public void trashEntity(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityId)
            throws DMServiceException;

    @GET
    @Path("/listTrash")
    @Produces("application/json")
    public List<DMEntity> viewTrash(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "start") @WebParam(name = "start") Integer start,
            @QueryParam(value = "count") @WebParam(name = "count") Integer count)
            throws DMServiceException;

    @GET
    @Path("/restoreFromTrash")
    @Produces("application/json")
    public DMEntity restoreFromTrash(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") Long dmEntityId)
            throws DMServiceException;


    @GET
    @Path("/createVirtualFolder")
    @Produces("application/json")
    public long saveVirtualFolder( @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                                   @QueryParam(value = "folderId") @WebParam(name = "folderId") Long id,
                                   @QueryParam(value = "folderName") @WebParam(name = "folderName") String folderName,
                                   @QueryParam(value = "parentId") @WebParam(name = "parentId") Long parentId,
                                   @QueryParam(value = "isSecurityInherited") @WebParam(name = "isSecurityInherited") boolean isSecurityInherited,
                                   @QueryParam(value = "documentTypeId") @WebParam(name = "documentTypeId") Long documentTypeId,
                                   @QueryParam(value = "metaItemsJsonString") @WebParam(name = "metaItemsJsonString") String metaItemsJsonString)
            throws DMServiceException;


    @GET
    @Path("/canHandleAutomaticPathDeposit")
    @Produces("application/json")
    public boolean canHandleAutomaticPathDeposit(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;


    @GET
    @Path(("/list-extensions"))
    @Produces("application/json")
    public List<String> listExtensions(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                                       @QueryParam(value = "extensionType") @WebParam(name = "extensionType") String extensionType)
        throws DMServiceException;
}
