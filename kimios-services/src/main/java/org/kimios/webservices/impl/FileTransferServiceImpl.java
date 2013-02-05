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
package org.kimios.webservices.impl;

import javax.jws.WebService;

import org.kimios.kernel.security.Session;
import org.kimios.kernel.ws.pojo.DataTransaction;
import org.kimios.webservices.CoreService;
import org.kimios.webservices.DMServiceException;
import org.kimios.webservices.FileTransferService;

@WebService(targetNamespace = "http://kimios.org", serviceName = "FileTransferService", name = "FileTransferService")
public class FileTransferServiceImpl extends CoreService implements FileTransferService
{
    /**
     * @param sessionUid
     * @param documentId
     * @param isCompressed
     * @return
     * @throws DMServiceException
     */
    public DataTransaction startUploadTransaction(String sessionUid, long documentId, boolean isCompressed)
            throws DMServiceException
    {
        try {
            Session session = getHelper().getSession(sessionUid);
            DataTransaction dtr = transferController.startUploadTransaction(session, documentId, isCompressed).toPojo();
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
    public void sendChunk(String sessionUid, long transactionUid, byte[] data) throws DMServiceException
    {
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
            throws DMServiceException
    {
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
            throws DMServiceException
    {
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
            throws DMServiceException
    {
        try {
            Session session = getHelper().getSession(sessionUid);
            byte[] t = transferController.getChunk(session, transactionUid, offset, chunkSize);
            return t;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }
}

