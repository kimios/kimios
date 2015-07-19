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

package org.kimios.kernel.repositories.pgp;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.DocumentVersion;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.RepositoryException;
import org.kimios.kernel.repositories.RepositoryAccessor;
import org.kimios.utils.configuration.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.Security;

/**
 * Created by farf on 7/12/14.
 */
public class PgpRepository implements RepositoryAccessor {

    private static Logger log = LoggerFactory.getLogger(PgpRepository.class);

    private String defaultRepositoryPath;

    private String privateKeyPath;

    private String publicKeyPath;

    private String privateKeyPass;

    private Long privateKeyId;

    private PGPPublicKey pgpPublicKey;

    private PGPPrivateKey pgpPrivateKey;

    public PgpRepository(String repositoryPath) throws Exception {

        defaultRepositoryPath = repositoryPath;

        privateKeyPath = ConfigurationManager.getValue("dms.pgp.repository.prvkey");
        publicKeyPath = ConfigurationManager.getValue("dms.pgp.repository.pubkey");
        privateKeyPass = ConfigurationManager.getValue("dms.pgp.repository.keypass");
        privateKeyId = Long.parseLong(ConfigurationManager.getValue("dms.pgp.repository.keyid"));


        Security.addProvider(new BouncyCastleProvider());
        PGPPublicKeyRing keyRing = PGPEncryptionUtil.getKeyring(new FileInputStream(new File(publicKeyPath)));
        pgpPublicKey = PGPEncryptionUtil.getEncryptionKey(keyRing);

        pgpPrivateKey = PGPEncryptionUtil.findPrivateKey(
                new FileInputStream(new File(privateKeyPath)),
                //Long.parseLong(privateKeyId, 16),
                privateKeyId,
                privateKeyPass.toCharArray());
        log.info("Kimios PGP Repository initialized. Path {}. Public Key ID {}. Private Key ID {}",
                defaultRepositoryPath,
                pgpPublicKey.getKeyID(),
                pgpPrivateKey.getKeyID());
    }

    public InputStream accessVersionStream(DocumentVersion version)
            throws RepositoryException, ConfigException, IOException {

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PGPEncryptionUtil.decryptFileFromKey(
                    new FileInputStream(defaultRepositoryPath + version.getStoragePath()),
                    bos,
                    pgpPrivateKey,
                    privateKeyPass.toCharArray()
            );
            return new ByteArrayInputStream(bos.toByteArray());
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    public void writeVersion(DocumentVersion version, InputStream in)
            throws DataSourceException, ConfigException, RepositoryException {
        try {
            String storageDirPath = version.getStoragePath().substring(0, version.getStoragePath().lastIndexOf("/"));
            File f = new File(defaultRepositoryPath + storageDirPath);
            if (!f.exists()) {
                boolean created = f.mkdirs();
            }
            PGPEncryptionUtil util = new PGPEncryptionUtil(pgpPublicKey,
                    "kimioswriter" + version.getUid(),
                    new FileOutputStream(defaultRepositoryPath + version.getStoragePath()));
            int fileLength = org.apache.commons.io.IOUtils.copy(in, util.getPayloadOutputStream());
            version.setLength(fileLength);
            util.close();
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    public OutputStream accessOutputStreamVersion(DocumentVersion version) throws Exception {
        throw new RepositoryException("No direct output stream access available for PGP Repository");
    }

    public RandomAccessFile randomAccessFile(DocumentVersion version, String mode) throws Exception {
        throw new RepositoryException("No random access available for PGP Repository");
    }

    public void initRepositoryStorage(DocumentVersion version) throws Exception {
        try {
            String storageDirPath = version.getStoragePath().substring(0, version.getStoragePath().lastIndexOf("/"));
            File f = new File(defaultRepositoryPath + storageDirPath);
            if (!f.exists()) {
                f.mkdirs();
            }
            if (!new File(defaultRepositoryPath + version.getStoragePath())
                    .exists()) {
                FileWriter newFile =
                        new FileWriter(defaultRepositoryPath +
                                version.getStoragePath(), false);
                newFile.close();
            }
        } catch (IOException io) {
            throw new RepositoryException(io);
        }
    }


    public void copyVersion(DocumentVersion source, DocumentVersion target)
            throws DataSourceException, ConfigException, RepositoryException {
        try {
            String storageDirPath = source.getStoragePath().substring(0, source.getStoragePath().lastIndexOf("/"));
            File f = new File(defaultRepositoryPath + storageDirPath);
            if (!f.exists()) {
                f.mkdirs();
            }

            String sourcePath = defaultRepositoryPath + source.getStoragePath();
            String targetPath = defaultRepositoryPath + target.getStoragePath();

            FileUtils.copyFile(new File(sourcePath),
                    new File(targetPath), false);
        } catch (IOException e) {
            throw new RepositoryException(e);
        }
    }

    public void readVersionToStream(DocumentVersion version, OutputStream out)
            throws DataSourceException, ConfigException, RepositoryException {
        try{
            PGPEncryptionUtil.decryptFileFromKey(
                    new FileInputStream(defaultRepositoryPath + version.getStoragePath()),
                    out,
                    pgpPrivateKey,
                    privateKeyPass.toCharArray()
            );
        }catch (Exception e){
            throw new RepositoryException(e);
        }
    }

    @Override
    public File directFileAccess(DocumentVersion documentVersion) throws RepositoryException {
        throw new RepositoryException("method not available");
    }
}
