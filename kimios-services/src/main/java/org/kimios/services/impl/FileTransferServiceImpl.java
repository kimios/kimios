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

import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.ws.pojo.DataTransaction;
import org.kimios.kernel.ws.pojo.DocumentWrapper;
import org.kimios.webservices.exceptions.DMServiceException;
import org.kimios.webservices.FileTransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@WebService(targetNamespace = "http://kimios.org", serviceName = "FileTransferService", name = "FileTransferService")
public class FileTransferServiceImpl
        extends CoreService
        implements FileTransferService {

    private static Logger logger = LoggerFactory.getLogger(FileTransferServiceImpl.class);

    /**
     * @param sessionUid
     * @param documentId
     * @param isCompressed
     * @return
     * @throws DMServiceException
     */
    public DataTransaction startUploadTransaction(String sessionUid, long documentId, boolean isCompressed)
            throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionUid);
            DataTransaction dtr =
                    transferController.startUploadTransaction(session, documentId, isCompressed).toPojo();
            return dtr;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionUid
     * @param transactionUid
     * @param data
     * @throws DMServiceException
     */
    public void sendChunk(String sessionUid, long transactionUid, byte[] data)
            throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionUid);
            transferController.sendChunk(session, transactionUid, data);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionUid
     * @param transactionUid
     * @throws DMServiceException
     */
    public void endUploadTransaction(String sessionUid, long transactionUid, String md5, String sha1)
            throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionUid);
            transferController.endUploadTransaction(session, transactionUid, md5, sha1);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionUid
     * @param documentVersionUid
     * @param isCompressed
     * @return
     * @throws DMServiceException
     */
    public DataTransaction startDownloadTransaction(String sessionUid, long documentVersionUid, boolean isCompressed)
            throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionUid);
            DataTransaction dtr =
                    transferController.startDownloadTransaction(session, documentVersionUid, isCompressed).toPojo();
            return dtr;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionUid
     * @param transactionUid
     * @param offset
     * @param chunkSize
     * @return
     * @throws DMServiceException
     */
    public byte[] getChunck(String sessionUid, long transactionUid, long offset, int chunkSize)
            throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionUid);
            byte[] t = transferController.getChunk(session, transactionUid, offset, chunkSize);
            return t;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    @WebMethod(exclude = true)
    public void uploadDocument(String sessionId, long transactionId, InputStream documentStream, String hashMd5,
                               String hashSha1)
            throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            transferController.uploadDocument(session, transactionId, documentStream, hashMd5, hashSha1);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    @WebMethod(exclude = true)
    public Response downloadDocumentVersion(String sessionId, final long transactionId, Boolean inline)
            throws DMServiceException {
        try {
            final Session session = getHelper().getSession(sessionId);
            DocumentWrapper dw = transferController.getDocumentVersionWrapper(session, transactionId);
            StreamingOutput sOutput = new StreamingOutput() {
                @Override
                public void write(OutputStream output) throws IOException, WebApplicationException {
                    try {
                        transferController.readVersionStream(session, transactionId, output);
                        output.flush();
                        output.close();
                    } catch (Exception ex) {
                        logger.error("error on streaming", ex);
                    }
                }
            };
            Response.ResponseBuilder response = Response.ok(sOutput);
            response.header("Content-Description", "File Transfer");
            response.header("Content-Type", dw.getContentType());
            response.header("Content-Transfer-Encoding", "binary");
            response.header("Expires", "0");
            response.header("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.header("Pragma", "public");
            response.header("Content-Length", dw.getLength());
            response.header("Content-Disposition", (inline ? "inline;" : "attachment;") + " filename=\"" + dw.getFilename() + "\"");
            return response.build();
        } catch (Exception e) {
            logger.error("error", e);
            throw getHelper().convertException(e);
        }
    }


    @WebMethod(exclude = true)
    public Response downloadDocument(String sessionId, final long transactionId, Boolean inline)
            throws DMServiceException {
        return this.downloadDocument(sessionId, transactionId, inline, null);
    }


    @WebMethod(exclude = true)
    public Response downloadDocument(String sessionId, final long transactionId, Boolean inline, List<Long> metaIds)
            throws DMServiceException {
        try {
            final Session session = getHelper().getSession(sessionId);
            DocumentWrapper dw = transferController.getDocumentVersionWrapper(session, transactionId, metaIds);
            StreamingOutput sOutput = new StreamingOutput() {
                @Override
                public void write(OutputStream output) throws IOException, WebApplicationException {
                    try {
                        transferController.readVersionStream(session, transactionId, output);
                        output.flush();
                        output.close();
                    } catch (Exception ex) {
                        logger.error("error on streaming", ex);
                    }
                }
            };
            Response.ResponseBuilder response = Response.ok(sOutput);
            response.header("Content-Description", "File Transfer");
            response.header("Content-Type", dw.getContentType());
            response.header("Content-Transfer-Encoding", "binary");
            response.header("Expires", "0");
            response.header("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.header("Pragma", "public");
            response.header("Content-Length", dw.getLength());
            response.header("Content-Disposition", (inline ? "inline;" : "attachment;") + " filename=\"" + dw.getFilename() + "\"");
            return response.build();


        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    @WebMethod(exclude = true)
    public Response downloadDocumentByToken(UriInfo uriInfo,final String token, final String password) throws DMServiceException {
        try {
            DocumentWrapper dw = transferController.getDocumentVersionWrapper(token, password);
            StreamingOutput sOutput = new StreamingOutput() {
                @Override
                public void write(OutputStream output) throws IOException, WebApplicationException {
                    try {
                        transferController.readVersionStream(token, output);
                        output.flush();
                        output.close();
                    } catch (Exception ex) {
                        logger.error("error on streaming", ex);
                    }
                }
            };
            Response.ResponseBuilder response = Response.ok(sOutput);
            response.header("Content-Description", "File Transfer");
            response.header("Content-Type", dw.getContentType());
            response.header("Content-Transfer-Encoding", "binary");
            response.header("Expires", "0");
            response.header("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.header("Pragma", "public");
            response.header("Content-Length", dw.getLength());
            //response.header("Content-Disposition", (inline ? "inline;" : "attachment;") + " filename=\"" + dw.getFilename() + "\"");
            response.header("Content-Disposition", "attachment; filename=\"" + dw.getFilename() + "\"");
            return response.build();


        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public DataTransaction createTokenDownloadTransaction(String sessionUid, long documentVersionUid)
            throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionUid);
            DataTransaction dtr =
                    transferController.startDownloadTransactionToken(session, documentVersionUid, null, null).toPojo();
            return dtr;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    @Override
    public Response downloadDocumentVersionPreview(String sessionId, long transactionId, Boolean inline) throws DMServiceException {
        return null;
    }
}

