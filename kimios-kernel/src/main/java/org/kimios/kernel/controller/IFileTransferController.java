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
package org.kimios.kernel.controller;

import org.kimios.exceptions.*;
import org.kimios.api.events.annotations.DmsEvent;
import org.kimios.api.events.annotations.DmsEventName;
import org.kimios.kernel.filetransfer.model.DataTransfer;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.share.model.Share;
import org.kimios.kernel.ws.pojo.DocumentWrapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface IFileTransferController
{
    /**
     * Start an upload transaction for a document (will update the last document version)
     */
    public DataTransfer startUploadTransaction( Session session, long documentIid, boolean isCompressed )
        throws CheckoutViolationException, AccessDeniedException, DataSourceException, IOException, ConfigException;

    /**
     * Write data to update temporary file of a given upload transaction
     */
    public void sendChunk( Session session, long transactionUid, byte[] data )
        throws ConfigException, AccessDeniedException, DataSourceException, FileNotFoundException, IOException;

    /**
     * End a given upload transaction :
     * <p/>
     * - InegrityCheck done - Update of the document version - Remove transaction and temporary file
     */
    @DmsEvent( eventName = { DmsEventName.FILE_UPLOAD } )
    public DataTransfer endUploadTransaction( Session session, long transactionUid, String hashMD5, String hashSHA1 )
        throws ConfigException, AccessDeniedException, IOException, DataSourceException, RepositoryException,
        TransferIntegrityException;

    /**
     * Start download transaction
     */
    public DataTransfer startDownloadTransaction( Session session, long documentVersionUid, boolean isCompressed )
        throws IOException, RepositoryException, DataSourceException, ConfigException, AccessDeniedException;

    /**
     * Start download transaction
     */
    public DataTransfer startDownloadTransaction( Session session, File file)
            throws IOException, RepositoryException, DataSourceException, ConfigException, AccessDeniedException;


    /**
     * Send chunk to client for a given offset and chunk size, in a given download transaction
     */
    public byte[] getChunk( Session session, long transferUid, long offset, int chunkSize )
        throws ConfigException, DataSourceException, AccessDeniedException, RepositoryException, IOException;


    /*
        Send document Stream on started transaction
     */
    public void uploadDocument( Session session, long transactionId, InputStream documentStream, String hashMd5,
                                String hashSha1 )
        throws CheckoutViolationException, AccessDeniedException, DataSourceException, IOException, ConfigException;

    /*
        Get document version stream for direct version read
    */
    public InputStream getDocumentVersionStream( Session session, long transactionId )
            throws ConfigException, AccessDeniedException, DataSourceException, IOException;

    /*
      Get document version stream for direct version read
    */
    public DocumentWrapper getDocumentVersionWrapper( Session session, long transactionId )
            throws ConfigException, AccessDeniedException, DataSourceException, IOException;

    /*
      Get document version stream for direct version read with custom name
    */
    public DocumentWrapper getDocumentVersionWrapper( Session session, long transactionId, List<Long> metaIds )
            throws ConfigException, AccessDeniedException, DataSourceException, IOException;

    /*
        Get document version stream for direct version read
    */
    public DocumentWrapper getDocumentVersionWrapper( String token, String password)
            throws ConfigException, AccessDeniedException, DataSourceException, IOException;

    /*
      Get document version stream for direct version streaming
  */
    public void readVersionStream( Session session, long transactionId, OutputStream versionStream)
            throws ConfigException, AccessDeniedException, DataSourceException, IOException;


    public void readVersionStream(String transactionToken, OutputStream versionStream)
            throws ConfigException, AccessDeniedException, DataSourceException, IOException;

    public File readFileStream(Session session, long transactionId, OutputStream versionStream)
            throws ConfigException, AccessDeniedException, DataSourceException, IOException;

    public DataTransfer startDownloadTransactionToken(Session session, long documentVersionUid, String password,
                                                      Share share)
            throws IOException, RepositoryException, DataSourceException, ConfigException, AccessDeniedException;
}
