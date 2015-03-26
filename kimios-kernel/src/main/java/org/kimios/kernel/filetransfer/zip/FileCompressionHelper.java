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
package org.kimios.kernel.filetransfer.zip;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.configuration.Config;
import org.kimios.kernel.dms.DocumentVersion;
import org.kimios.kernel.exception.RepositoryException;
import org.kimios.kernel.filetransfer.DataTransfer;
import org.kimios.kernel.repositories.RepositoryManager;
import org.kimios.utils.configuration.ConfigurationManager;

import java.io.*;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileCompressionHelper
{
    /**
     * @param t
     * @return
     * @throws ConfigException
     * @throws java.io.IOException
     */
    public static String getTempFilePath(DataTransfer t) throws ConfigException, IOException
    {
        File tmpDir = new File(ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + "/tmpfiles");
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        String tmpFileName = "/tmpfiles/up" + t.getDocumentVersionUid() + (new Date()).getTime();
        File tmp = new File(ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + tmpFileName);
        tmp.createNewFile();
        t.setFilePath(tmpFileName);
        return tmpFileName;
    }

    /**
     * @param t
     * @return
     * @throws ConfigException
     * @throws java.io.IOException
     * @throws RepositoryException
     */
    public static InputStream getTransactionFile(DataTransfer t)
            throws ConfigException, IOException, RepositoryException
    {

        if (t.getIsCompressed()) {
            FileInputStream toRead = new FileInputStream(
                    ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + t.getFilePath());
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(toRead));
            if (zis.getNextEntry() != null) {
                return zis;
            } else {
                throw new RepositoryException("Unable to read compressed data sent!");
            }
        } else {
            return new FileInputStream(ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + t.getFilePath());
        }
    }

    /**
     * @param dv
     * @param t
     * @return
     * @throws Exception
     */
    public static File getDownloadableCompressedVersion(DocumentVersion dv, DataTransfer t)
            throws RepositoryException, ConfigException, IOException
    {
        String tmpFileName = "/tmpfiles/dl" + t.getDocumentVersionUid() + t.getUid();
        File tmp = new File(ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + tmpFileName);
        FileOutputStream dest = new FileOutputStream(tmp);
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
        byte[] data = new byte[2048];
        BufferedInputStream original = new BufferedInputStream(RepositoryManager.accessVersionStream(dv), 2048);
        ZipEntry entry = new ZipEntry(t.getUid() + "_" + t.getDocumentVersionUid());
        out.putNextEntry(entry);
        int count;
        while ((count = original.read(data, 0, 2048)) != -1) {
            out.write(data, 0, count);
        }
        out.close();
        return tmp;
    }
}

