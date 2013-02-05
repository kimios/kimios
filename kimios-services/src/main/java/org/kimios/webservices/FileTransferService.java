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

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.kimios.kernel.ws.pojo.DataTransaction;

/**
 * Created by IntelliJ IDEA. User: farf Date: 4/1/12 Time: 4:58 PM
 */
@Path("/filetransfer")
@WebService(targetNamespace = "http://kimios.org", serviceName = "FileTransferService")
public interface FileTransferService
{
    @GET
    @Path("/startUploadTransaction")
    @Produces("application/json")
    public DataTransaction startUploadTransaction(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentId") @WebParam(name = "documentId") long documentId,
            @QueryParam(value = "isCompressed") @WebParam(name = "isCompressed") boolean isCompressed) throws DMServiceException;

    @GET
    @Path("/sendChunk")
    @Produces("application/json")
    public void sendChunk(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "transactionId") @WebParam(name = "transactionId") long transactionId,
            @QueryParam(value = "data") @WebParam(name = "data") byte[] data) throws DMServiceException;

    @GET
    @Path("/endUploadTransaction")
    @Produces("application/json")
    public void endUploadTransaction(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "transactionId") @WebParam(name = "transactionId") long transactionId,
            @QueryParam(value = "md5") @WebParam(name = "md5") String md5,
            @QueryParam(value = "sha1") @WebParam(name = "sha1") String sha1) throws DMServiceException;

    @GET
    @Path("/startDownloadTransaction")
    @Produces("application/json")
    public DataTransaction startDownloadTransaction(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentVersionId") @WebParam(name = "documentVersionId") long documentVersionId,
            @QueryParam(value = "isCompressed") @WebParam(name = "isCompressed") boolean isCompressed)
            throws DMServiceException;

    byte[] getChunck(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "transactionId") @WebParam(name = "transactionId") long transactionId,
            @QueryParam(value = "offset") @WebParam(name = "offset") long offset,
            @QueryParam(value = "chunkSize") @WebParam(name = "chunkSize") int chunkSize) throws DMServiceException;
}
