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
package org.kimios.kernel.repositories;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import org.apache.commons.io.FileUtils;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.configuration.Config;
import org.kimios.kernel.dms.DocumentVersion;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.RepositoryException;
import org.kimios.utils.configuration.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryManager
{
    private static Logger log = LoggerFactory.getLogger(RepositoryManager.class);

    private RepositoryService repositoryService;

    private Repository defaultRepository;

    private static RepositoryManager manager;

    private String defaultRepositoryPath;

    synchronized public static RepositoryManager init()
    {
        if (manager == null) {
            manager = new RepositoryManager();
        }
        return manager;
    }

    public void postInit()
    {
        try {
            Repository repository = repositoryService.loadDefaultRepository();
            if (repository == null) {
                log.warn("Repository error: no default repository set. Setting to default");
                defaultRepositoryPath = ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH);
            } else {
                defaultRepository = repository;
                defaultRepositoryPath = repository.getPath();
            }
        } catch (Exception e) {
            log.error("Repository error: unable to get default repository", e);
        }
    }

    public static InputStream accessVersionStream(DocumentVersion version)
            throws RepositoryException, ConfigException, IOException
    {
        return new FileInputStream(manager.defaultRepositoryPath + version.getStoragePath());
    }

    public static void writeVersion(DocumentVersion version, InputStream in)
            throws DataSourceException, ConfigException, RepositoryException
    {
        try {
            String storageDirPath = version.getStoragePath().substring(0, version.getStoragePath().lastIndexOf("/"));
            File f = new File(manager.defaultRepositoryPath + storageDirPath);
            if (!f.exists()) {
                boolean created = f.mkdirs();
            }
            BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream(
                            manager.defaultRepositoryPath + version.getStoragePath()));
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
                            manager.defaultRepositoryPath + version.getStoragePath());
            version.setLength(fis.available());
            fis.close();
        } catch (IOException e) {
            throw new RepositoryException(e);
        }
    }

    public static OutputStream accessOutputStreamVersion(DocumentVersion version) throws Exception
    {
        try {
            return new FileOutputStream(
                    manager.defaultRepositoryPath + version.getStoragePath());
        } catch (IOException io) {
            throw new RepositoryException(io);
        }
    }

    public static RandomAccessFile randomAccessFile(DocumentVersion version, String mode) throws Exception
    {
        try {
            return new RandomAccessFile(
                    new File(manager.defaultRepositoryPath + version.getStoragePath()),
                    mode
            );
        } catch (IOException io) {
            throw new RepositoryException(io);
        }
    }

    public static void initRepositoryStorage(DocumentVersion version) throws Exception
    {
        try {
            String storageDirPath = version.getStoragePath().substring(0, version.getStoragePath().lastIndexOf("/"));
            File f = new File(manager.defaultRepositoryPath + storageDirPath);
            if (!f.exists()) {
                f.mkdirs();
            }
            if (!new File(manager.defaultRepositoryPath + version.getStoragePath())
                    .exists())
            {
                FileWriter newFile =
                        new FileWriter(manager.defaultRepositoryPath +
                                version.getStoragePath(), false);
                newFile.close();
            }
        } catch (IOException io) {
            throw new RepositoryException(io);
        }
    }

    public RepositoryService getRepositoryService()
    {
        return repositoryService;
    }

    public void setRepositoryService(RepositoryService repositoryService)
    {
        this.repositoryService = repositoryService;
    }

    public static void copyVersion(DocumentVersion source, DocumentVersion target)
            throws DataSourceException, ConfigException, RepositoryException
    {
        try {
            String storageDirPath = source.getStoragePath().substring(0, source.getStoragePath().lastIndexOf("/"));
            File f = new File(manager.defaultRepositoryPath + storageDirPath);
            if (!f.exists()) {
                f.mkdirs();
            }

            String sourcePath = manager.defaultRepositoryPath + source.getStoragePath();
            String targetPath = manager.defaultRepositoryPath + target.getStoragePath();

            log.debug("Source Version Path " + sourcePath + " | Target Path " + targetPath);

            FileUtils.copyFile(new File(sourcePath),
                    new File(targetPath), false);
        } catch (IOException e) {
            throw new RepositoryException(e);
        }
    }
}
