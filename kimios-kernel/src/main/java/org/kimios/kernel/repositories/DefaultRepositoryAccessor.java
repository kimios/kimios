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

package org.kimios.kernel.repositories;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.DocumentVersion;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.RepositoryException;

import java.io.*;

/**
 * Created by farf on 7/12/14.
 */
public class DefaultRepositoryAccessor implements RepositoryAccessor {


    private String defaultRepositoryPath;

    public DefaultRepositoryAccessor(String repositoryPath){
        defaultRepositoryPath = repositoryPath;
    }

    public InputStream accessVersionStream(DocumentVersion version)
            throws RepositoryException, ConfigException, IOException
    {
        return new FileInputStream(defaultRepositoryPath + version.getStoragePath());
    }

    public void writeVersion(DocumentVersion version, InputStream in)
            throws DataSourceException, ConfigException, RepositoryException
    {
        try {
            String storageDirPath = version.getStoragePath().substring(0, version.getStoragePath().lastIndexOf("/"));
            File f = new File(defaultRepositoryPath + storageDirPath);
            if (!f.exists()) {
                boolean created = f.mkdirs();
            }
            BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream(
                            defaultRepositoryPath + version.getStoragePath()));
            BufferedInputStream input = new BufferedInputStream(in);
            int len = 0;
            byte[] buffer = new byte[10000];
            while ((len = input.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
            out.close();

            FileInputStream fis =
                    new FileInputStream(
                            defaultRepositoryPath + version.getStoragePath());
            version.setLength(fis.available());
            fis.close();
        } catch (IOException e) {
            throw new RepositoryException(e);
        }
    }

    public OutputStream accessOutputStreamVersion(DocumentVersion version) throws Exception
    {
        try {
            return new FileOutputStream(
                    defaultRepositoryPath + version.getStoragePath());
        } catch (IOException io) {
            throw new RepositoryException(io);
        }
    }

    public RandomAccessFile randomAccessFile(DocumentVersion version, String mode) throws Exception
    {
        try {
            return new RandomAccessFile(
                    new File(defaultRepositoryPath + version.getStoragePath()),
                    mode
            );
        } catch (IOException io) {
            throw new RepositoryException(io);
        }
    }

    public void initRepositoryStorage(DocumentVersion version) throws Exception
    {
        try {
            String storageDirPath = version.getStoragePath().substring(0, version.getStoragePath().lastIndexOf("/"));
            File f = new File(defaultRepositoryPath + storageDirPath);
            if (!f.exists()) {
                f.mkdirs();
            }
            if (!new File(defaultRepositoryPath + version.getStoragePath())
                    .exists())
            {
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
            throws DataSourceException, ConfigException, RepositoryException
    {
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
            IOUtils.copy(new FileInputStream(defaultRepositoryPath + version.getStoragePath()), out);
        }catch (Exception e){
            throw new RepositoryException(e);
        }
    }
}
