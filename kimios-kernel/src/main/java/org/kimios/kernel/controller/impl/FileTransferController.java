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
package org.kimios.kernel.controller.impl;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.configuration.Config;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.IFileTransferController;
import org.kimios.kernel.dms.*;
import org.kimios.kernel.dms.model.*;
import org.kimios.api.events.annotations.DmsEvent;
import org.kimios.api.events.annotations.DmsEventName;
import org.kimios.kernel.exception.*;
import org.kimios.kernel.filetransfer.model.DataTransfer;
import org.kimios.kernel.filetransfer.zip.FileCompressionHelper;
import org.kimios.kernel.repositories.impl.RepositoryManager;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.user.model.User;
import org.kimios.utils.hash.HashCalculator;
import org.kimios.kernel.ws.pojo.DocumentWrapper;
import org.kimios.utils.configuration.ConfigurationManager;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

@Transactional
public class FileTransferController
        extends AKimiosController
        implements IFileTransferController {
    /**
     * Start an upload transaction for a given document (update the last version)
     */
    public DataTransfer startUploadTransaction(Session session, long documentId, boolean isCompressed)
            throws CheckoutViolationException, AccessDeniedException, DataSourceException, IOException, ConfigException {
        Document d = dmsFactoryInstantiator.getDocumentFactory().getDocument(documentId);
        User u = authFactoryInstantiator.getAuthenticationSourceFactory().getAuthenticationSource(
                session.getUserSource()).getUserFactory().getUser(session.getUserName());
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
            throws ConfigException, AccessDeniedException, DataSourceException, FileNotFoundException, IOException {
        DataTransfer transac = transferFactoryInstantiator.getDataTransferFactory().getDataTransfer(transactionUid);
        DocumentVersion dv =
                dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(transac.getDocumentVersionUid());
        if (!getSecurityAgent().isWritable(dv.getDocument(), session.getUserName(), session.getUserSource(),
                session.getGroups())) {
            throw new AccessDeniedException();
        }
        OutputStream out = new FileOutputStream(
                new File(ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + transac.getFilePath()), true);
        out.write(data);
        out.close();
    }

    /**
     * End a given upload transaction :
     * <p/>
     * - InegrityCheck done - Update of the document version - Remove transaction and temporary file
     */
    @DmsEvent(eventName = {DmsEventName.FILE_UPLOAD})
    public DataTransfer endUploadTransaction(Session session, long transactionUid, String hashMD5, String hashSHA1)
            throws ConfigException, AccessDeniedException, IOException, DataSourceException, RepositoryException,
            TransferIntegrityException {
        DataTransfer transac = transferFactoryInstantiator.getDataTransferFactory().getDataTransfer(transactionUid);
        DocumentVersion dv =
                dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(transac.getDocumentVersionUid());
        Document d = dv.getDocument();
        User u = authFactoryInstantiator.getAuthenticationSourceFactory().getAuthenticationSource(
                session.getUserSource()).getUserFactory().getUser(session.getUserName());
        if (!getSecurityAgent().isWritable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }

        //Return inpustream on file transmitted
        InputStream in = FileCompressionHelper.getTransactionFile(transac);

        /* Hash Calculation */
        String recHashMD5 = "";
        String recHashSHA1 = "";
        try {
            HashCalculator hc = new HashCalculator("MD5");
            recHashMD5 = (hc.hashToString(in).replaceAll(" ", ""));
            in = FileCompressionHelper.getTransactionFile(transac);
            hc.setAlgorithm("SHA-1");
            recHashSHA1 = (hc.hashToString(in).replaceAll(" ", ""));
        } catch (NoSuchAlgorithmException nsae) {
            hashMD5 = ("error: No algothim defined");
            hashSHA1 = ("error: No algothim defined");
        }

        if (!(hashMD5.equalsIgnoreCase(recHashMD5) && hashSHA1.equalsIgnoreCase(recHashSHA1))) {
            new File(
                    ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + transac.getFilePath()).delete();
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
                if (!hashMD5.equalsIgnoreCase(jBefore.getHashMD5()) && !hashSHA1.equalsIgnoreCase(
                        jBefore.getHashSHA1())) {
                    Date newDate = new Date();
                    dv.setStoragePath(
                            new SimpleDateFormat("/yyyy/MM/dd/HH/mm/").format(newDate) + dv.getDocumentUid() + "_" +
                                    newDate.getTime() + ".bin");
                    // storing data
                    dv.setHashMD5(hashMD5);
                    dv.setHashSHA1(hashSHA1);
                    RepositoryManager.writeVersion(dv, in);
                    FactoryInstantiator.getInstance().getDocumentVersionFactory().updateDocumentVersion(dv);
                } else {
                    //nothing: same file
                }
            } else {
                //not the same path :
                //Update:

                if (!hashMD5.equalsIgnoreCase(dv.getHashMD5()) && !hashSHA1.equalsIgnoreCase(dv.getHashSHA1())) {
                    dv.setHashMD5(hashMD5);
                    dv.setHashSHA1(hashSHA1);
                    RepositoryManager.writeVersion(dv, in);
                    FactoryInstantiator.getInstance().getDocumentVersionFactory().updateDocumentVersion(dv);
                } else {
                }
            }
        } else if (twoLast.size() == 1 && twoLast.contains(dv)
                && !hashMD5.equalsIgnoreCase(dv.getHashMD5()) && !hashSHA1.equalsIgnoreCase(dv.getHashSHA1())) {
            dv.setHashMD5(hashMD5);
            dv.setHashSHA1(hashSHA1);
            RepositoryManager.writeVersion(dv, in);
            FactoryInstantiator.getInstance().getDocumentVersionFactory().updateDocumentVersion(dv);
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
    @DmsEvent(eventName = {DmsEventName.DOCUMENT_VERSION_READ})
    public DataTransfer startDownloadTransaction(Session session, long documentVersionUid, boolean isCompressed)
            throws IOException, RepositoryException, DataSourceException, ConfigException, AccessDeniedException {
        DocumentVersion dv =
                dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(documentVersionUid);
        if (!getSecurityAgent().isReadable(dv.getDocument(), session.getUserName(), session.getUserSource(),
                session.getGroups())) {
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
            throws ConfigException, DataSourceException, AccessDeniedException, RepositoryException, IOException {
        DataTransfer transac = transferFactoryInstantiator.getDataTransferFactory().getDataTransfer(transferUid);
        DocumentVersion dv =
                dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(transac.getDocumentVersionUid());
        if (getSecurityAgent().isReadable(dv.getDocument(), session.getUserName(), session.getUserSource(),
                session.getGroups())) {
            InputStream in = null;
            if (transac.getIsCompressed()) {
                in = new FileInputStream(new File(
                        ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + transac.getFilePath()));
            } else {
                in = RepositoryManager.accessVersionStream(dv);
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


    public void uploadDocument(Session session, long transactionId, InputStream documentStream, String hashMd5,
                               String hashSha1)
            throws ConfigException, AccessDeniedException, DataSourceException, IOException {
        DataTransfer transac = transferFactoryInstantiator.getDataTransferFactory().getDataTransfer(transactionId);
        DocumentVersion dv =
                dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(transac.getDocumentVersionUid());
        if (!getSecurityAgent().isWritable(dv.getDocument(), session.getUserName(), session.getUserSource(),
                session.getGroups())) {
            throw new AccessDeniedException();
        }
        OutputStream out = new FileOutputStream(
                new File(ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + transac.getFilePath()), true);

        byte[] b = new byte[2048];
        int readBytes;
        while ((readBytes = documentStream.read(b, 0, b.length)) > -1) {
            out.write(b, 0, readBytes);
        }
        out.flush();
        out.close();
        if (hashMd5 != null && hashSha1 != null) {
            this.endUploadTransaction(session, transactionId, hashMd5, hashSha1);
        }
    }


    public InputStream getDocumentVersionStream(Session session, long transactionId)
            throws ConfigException, AccessDeniedException, DataSourceException, IOException {
        DataTransfer transac = transferFactoryInstantiator.getDataTransferFactory().getDataTransfer(transactionId);
        if (transac != null && transac.getTransferMode() == DataTransfer.DOWNLOAD) {
            DocumentVersion dv = dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(
                    transac.getDocumentVersionUid());
            if (getSecurityAgent().isReadable(dv.getDocument(), session.getUserName(), session.getUserSource(),
                    session.getGroups())) {
                return RepositoryManager.accessVersionStream(dv);
            } else {
                throw new AccessDeniedException();
            }
        } else {
            throw new AccessDeniedException();
        }

    }

    public DocumentWrapper getDocumentVersionWrapper(Session session, long transactionId)
            throws ConfigException, AccessDeniedException, DataSourceException, IOException {

        DataTransfer transac = transferFactoryInstantiator.getDataTransferFactory().getDataTransfer(transactionId);
        if (transac != null && transac.getTransferMode() == DataTransfer.DOWNLOAD) {
            DocumentVersion dv = dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(
                    transac.getDocumentVersionUid());
            if (getSecurityAgent().isReadable(dv.getDocument(), session.getUserName(), session.getUserSource(),
                    session.getGroups())) {
                String filename = dv.getDocument().getName() + "." + dv.getDocument().getExtension();
                return new DocumentWrapper(ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + "/" +
                        dv.getStoragePath(), filename, dv.getLength());
            } else {
                throw new AccessDeniedException();
            }
        } else {
            throw new AccessDeniedException();
        }
    }

    public DocumentWrapper getDocumentVersionWrapper( Session session, long transactionId, List<Long> metaIds)
            throws ConfigException, AccessDeniedException, DataSourceException, IOException{
        if(metaIds == null || metaIds.size() == 0){
            return getDocumentVersionWrapper(session, transactionId);
        } else {
            DataTransfer transac = transferFactoryInstantiator.getDataTransferFactory().getDataTransfer(transactionId);
            if (transac != null && transac.getTransferMode() == DataTransfer.DOWNLOAD) {
                DocumentVersion dv = dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(
                        transac.getDocumentVersionUid());
                if (getSecurityAgent().isReadable(dv.getDocument(), session.getUserName(), session.getUserSource(),
                        session.getGroups())) {
                        String finalName = "";
                        for(Long metaId: metaIds){
                            Meta m = dmsFactoryInstantiator.getMetaFactory().getMeta(metaId);
                            MetaValue v = dmsFactoryInstantiator.getMetaValueFactory().getMetaValue(dv, m);
                            if(v.getMeta().getMetaType() == MetaType.STRING){
                                finalName = (v.getValue() != null && v.getValue().toString().length() > 0 ? v.getValue().toString() : dv.getDocument().getName())
                                        +  "." + dv.getDocument().getExtension();

                            }  else
                                throw new AccessDeniedException();
                        }
                        return new DocumentWrapper(ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + "/" +
                                dv.getStoragePath(), finalName, dv.getLength());
                } else {
                    throw new AccessDeniedException();
                }
            } else {
                throw new AccessDeniedException();
            }



        }
    }

    public DocumentWrapper getDocumentVersionWrapper(String token)
            throws ConfigException, AccessDeniedException, DataSourceException, IOException {

        DataTransfer transac = transferFactoryInstantiator.getDataTransferFactory()
                .getUploadDataTransferByDocumentToken(token);
        if (transac != null && transac.getTransferMode() == DataTransfer.TOKEN) {
            DocumentVersion dv = dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(
                    transac.getDocumentVersionUid());
            String filename = dv.getDocument().getName() + "." + dv.getDocument().getExtension();
            return new DocumentWrapper(ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + "/" +
                    dv.getStoragePath(), filename, dv.getLength());
        } else {
            throw new AccessDeniedException();
        }
    }


    public void readVersionStream(Session session, long transactionId, OutputStream versionStream)
            throws ConfigException, AccessDeniedException, DataSourceException, IOException {

        DataTransfer transac = transferFactoryInstantiator.getDataTransferFactory().getDataTransfer(transactionId);
        if (transac != null && transac.getTransferMode() == DataTransfer.DOWNLOAD) {
            DocumentVersion dv = dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(
                    transac.getDocumentVersionUid());
            if (getSecurityAgent().isReadable(dv.getDocument(), session.getUserName(), session.getUserSource(),
                    session.getGroups())) {
                RepositoryManager.readVersionToStream(dv, versionStream);
            } else {
                throw new AccessDeniedException();
            }
        } else {
            throw new AccessDeniedException();
        }
    }


    /**
     * Create Token For Download Transaction
     */
    public DataTransfer startDownloadTransactionToken(Session session, long documentVersionUid)
            throws IOException, RepositoryException, DataSourceException, ConfigException, AccessDeniedException {
        DocumentVersion dv =
                dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(documentVersionUid);
        if (!getSecurityAgent().isReadable(dv.getDocument(), session.getUserName(), session.getUserSource(),
                session.getGroups())) {
            throw new AccessDeniedException();
        }
        DataTransfer transac = new DataTransfer();
        transac.setDocumentVersionUid(dv.getUid());
        transac.setIsCompressed(false);
        transac.setHashMD5(dv.getHashMD5());
        transac.setHashSHA(dv.getHashSHA1());
        transac.setLastActivityDate(new Date());
        transac.setUserName(session.getUserName());
        transac.setUserSource(session.getUserSource());
        transac.setDataSize(0);
        transac.setTransferMode(DataTransfer.TOKEN);
        transac.setDownloadToken(UUID.randomUUID().toString());
        transferFactoryInstantiator.getDataTransferFactory().addDataTransfer(transac);
        transac.setDataSize(dv.getLength());
        transferFactoryInstantiator.getDataTransferFactory().updateDataTransfer(transac);
        return transac;
    }

    public void readVersionStream(String transactionToken, OutputStream versionStream)
            throws ConfigException, AccessDeniedException, DataSourceException, IOException {

        DataTransfer transac = transferFactoryInstantiator
                .getDataTransferFactory().getUploadDataTransferByDocumentToken(transactionToken);
        if (transac != null && transac.getTransferMode() == DataTransfer.TOKEN) {
            DocumentVersion dv = dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(
                    transac.getDocumentVersionUid());
            RepositoryManager.readVersionToStream(dv, versionStream);
        } else {
            throw new AccessDeniedException();
        }
    }
}

