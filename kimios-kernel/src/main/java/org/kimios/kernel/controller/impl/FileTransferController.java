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
package org.kimios.kernel.controller.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.configuration.Config;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.IFileTransferController;
import org.kimios.kernel.dms.Document;
import org.kimios.kernel.dms.DocumentVersion;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.CheckoutViolationException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.RepositoryException;
import org.kimios.kernel.exception.TransferIntegrityException;
import org.kimios.kernel.filetransfer.DataTransfer;
import org.kimios.kernel.filetransfer.zip.FileCompressionHelper;
import org.kimios.kernel.security.Session;
import org.kimios.kernel.user.User;
import org.kimios.kernel.utils.HashCalculator;
import org.kimios.utils.configuration.ConfigurationManager;

public class FileTransferController extends AKimiosController implements IFileTransferController
{
    /**
     * Start an upload transaction for a given document (update the last version)
     */
    public DataTransfer startUploadTransaction(Session session, long documentId, boolean isCompressed)
            throws CheckoutViolationException, AccessDeniedException, DataSourceException, IOException, ConfigException
    {
        Document d = dmsFactoryInstantiator.getDocumentFactory().getDocument(documentId);
        User u = authFactoryInstantiator.getAuthenticationSourceFactory()
                .getAuthenticationSource(session.getUserSource()).getUserFactory().getUser(session.getUserName());
        if (!getSecurityAgent().isWritable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }
        boolean hasBeanCheckedOut = dmsFactoryInstantiator.getLockFactory().checkout(d, u);
        DocumentVersion dv = dmsFactoryInstantiator.getDocumentVersionFactory().getLastDocumentVersion(d);
        if (dv == null) {
            dv = new DocumentVersion(-1, session.getUserName(), session.getUserSource(), new Date(), new Date(),
                    d.getUid(), 0, null);
            dmsFactoryInstantiator.getDocumentVersionFactory().saveDocumentVersion(dv);
        }

        DataTransfer transac = new DataTransfer();
        transac.setUserName(session.getUserName());
        transac.setUserSource(session.getUserSource());
        transac.setDocumentVersionUid(dv.getUid());
        transac.setIsCompressed(isCompressed);
        transac.setHashMD5("");
        transac.setHashSHA("");
        transac.setHasBeenCheckedOutOnStart(hasBeanCheckedOut);
        //Size unused during upload
        transac.setDataSize(0);
        transac.setLastActivityDate(new Date());
        transac.setTransferMode(DataTransfer.UPLOAD);
        FileCompressionHelper.getTempFilePath(transac);
        transferFactoryInstantiator.getDataTransferFactory().addDataTransfer(transac);
        return transac;
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IFileTransferController#sendChunk(org.kimios.kernel.security.Session, long, byte[])
    */
    public void sendChunk(Session session, long transactionUid, byte[] data)
            throws ConfigException, AccessDeniedException, DataSourceException, FileNotFoundException, IOException
    {
        DataTransfer transac = transferFactoryInstantiator.getDataTransferFactory().getDataTransfer(transactionUid);
        DocumentVersion dv =
                dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(transac.getDocumentVersionUid());
        if (!getSecurityAgent()
                .isWritable(dv.getDocument(), session.getUserName(), session.getUserSource(), session.getGroups()))
        {
            throw new AccessDeniedException();
        }
        OutputStream out =
                new FileOutputStream(new File(
                        ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + transac.getFilePath()),
                        true);
        out.write(data);
        out.close();
    }

    /**
     * End a given upload transaction :
     *
     * - InegrityCheck done - Update of the document version - Remove transaction and temporary file
     */
    public DataTransfer endUploadTransaction(Session session, long transactionUid, String hashMD5, String hashSHA1)
            throws ConfigException, AccessDeniedException, IOException, DataSourceException, RepositoryException,
            TransferIntegrityException
    {
        DataTransfer transac = transferFactoryInstantiator.getDataTransferFactory().getDataTransfer(transactionUid);
        DocumentVersion dv =
                dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(transac.getDocumentVersionUid());
        Document d = dv.getDocument();
        User u = authFactoryInstantiator.getAuthenticationSourceFactory()
                .getAuthenticationSource(session.getUserSource()).getUserFactory().getUser(session.getUserName());
        if (!getSecurityAgent().isWritable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }

        //Return inpustream on file transmitted
        InputStream in = FileCompressionHelper.getTransactionFile(transac);

        /* Hash Calculation */
        try {
            HashCalculator hc = new HashCalculator("MD5");
            hashMD5 = (hc.hashToString(in).replaceAll(" ", ""));
            in = FileCompressionHelper.getTransactionFile(transac);
            hc.setAlgorithm("SHA-1");
            hashSHA1 = (hc.hashToString(in).replaceAll(" ", ""));
        } catch (NoSuchAlgorithmException nsae) {
            hashMD5 = ("error: No algothim defined");
            hashSHA1 = ("error: No algothim defined");
        }

        if (!(hashMD5.equalsIgnoreCase(hashMD5) && hashSHA1.equalsIgnoreCase(hashSHA1))) {
            new File(ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + transac.getFilePath()).delete();
            throw new TransferIntegrityException();
        }

        in = FileCompressionHelper.getTransactionFile(transac);
        Vector<DocumentVersion> twoLast =
                dmsFactoryInstantiator.getDocumentVersionFactory().getTwoLastDocumentVersion(d);
        DocumentVersion jBefore = null;
        for (DocumentVersion h : twoLast) {
            jBefore = (h.getUid() != dv.getUid() ? h : null);
        }
        if (twoLast.contains(dv) && twoLast.size() > 1 && jBefore != null) {

            if (jBefore.getStoragePath().equalsIgnoreCase(dv.getStoragePath())) {
                //Same path: check the hash
                if (!hashMD5.equalsIgnoreCase(jBefore.getHashMD5()) &&
                        !hashSHA1.equalsIgnoreCase(jBefore.getHashSHA1()))
                {
                    Date newDate = new Date();
                    dv.setStoragePath(
                            new SimpleDateFormat("/yyyy/MM/dd/HH/mm/").format(newDate) + dv.getDocumentUid() + "_" +
                                    newDate.getTime() + ".bin");
                    // storing data
                    dv.setHashMD5(hashMD5);
                    dv.setHashSHA1(hashSHA1);
                    dv.writeData(in);
                } else {
                    //nothing: same file
                }
            } else {
                //not the same path :
                //Update:

                if (!hashMD5.equalsIgnoreCase(dv.getHashMD5()) && !hashSHA1.equalsIgnoreCase(dv.getHashSHA1())) {
                    dv.setHashMD5(hashMD5);
                    dv.setHashSHA1(hashSHA1);
                    dv.writeData(in);
                } else {
                }
            }
        } else {
            if (twoLast.size() == 1 && twoLast.contains(dv)) {
                dv.setHashMD5(hashMD5);
                dv.setHashSHA1(hashSHA1);
                dv.writeData(in);
            }
        }
        new File(ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + transac.getFilePath()).delete();
        if (transac.isHasBeenCheckedOutOnStart()) {
            dmsFactoryInstantiator.getLockFactory().checkin(d, u);
        }
        transferFactoryInstantiator.getDataTransferFactory().removeDataTransfer(transac);
        return transac;
    }

    /**
     * Start download transaction
     */
    public DataTransfer startDownloadTransaction(Session session, long documentVersionUid, boolean isCompressed)
            throws IOException, RepositoryException, DataSourceException, ConfigException, AccessDeniedException
    {
        DocumentVersion dv = dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(documentVersionUid);
        if (!getSecurityAgent()
                .isReadable(dv.getDocument(), session.getUserName(), session.getUserSource(), session.getGroups()))
        {
            throw new AccessDeniedException();
        }
        DataTransfer transac = new DataTransfer();
        transac.setDocumentVersionUid(dv.getUid());
        transac.setIsCompressed(isCompressed);
        transac.setHashMD5(dv.getHashMD5());
        transac.setHashSHA(dv.getHashSHA1());
        transac.setLastActivityDate(new Date());
        transac.setUserName(session.getUserName());
        transac.setUserSource(session.getUserSource());
        transac.setDataSize(0);
        transac.setTransferMode(DataTransfer.DOWNLOAD);
        transferFactoryInstantiator.getDataTransferFactory().addDataTransfer(transac);
        if (isCompressed) {
            //File Compression
            File toSend = FileCompressionHelper.getDownloadableCompressedVersion(dv, transac);
            transac.setFilePath("/tmpfiles/" + toSend.getName());
            transac.setDataSize(toSend.length());
        } else {
            transac.setDataSize(dv.getLength());
        }
        transferFactoryInstantiator.getDataTransferFactory().updateDataTransfer(transac);

        return transac;
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IFileTransferController#getChunk(org.kimios.kernel.security.Session, long, long, int)
    */
    public byte[] getChunk(Session session, long transferUid, long offset, int chunkSize)
            throws ConfigException, DataSourceException, AccessDeniedException, RepositoryException, IOException
    {
        DataTransfer transac = transferFactoryInstantiator.getDataTransferFactory().getDataTransfer(transferUid);
        DocumentVersion dv =
                dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(transac.getDocumentVersionUid());
        if (getSecurityAgent()
                .isReadable(dv.getDocument(), session.getUserName(), session.getUserSource(), session.getGroups()))
        {
            InputStream in = null;
            if (transac.getIsCompressed()) {
                in = new FileInputStream(
                        new File(
                                ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + transac.getFilePath()));
            } else {
                in = dv.getInputStream();
            }
            byte[] b = new byte[chunkSize];
            in.skip(offset);
            int readBytes = in.read(b, 0, b.length);
            if (readBytes > 0) {
                byte[] toRet = new byte[readBytes];
                System.arraycopy(b, 0, toRet, 0, readBytes);
                return toRet;
            } else {
                return new byte[0];
            }
        } else {
            throw new AccessDeniedException();
        }
    }
}

