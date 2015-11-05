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
package org.kimios.kernel.repositories.impl;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.configuration.Config;
import org.kimios.kernel.dms.model.DocumentVersion;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.RepositoryException;
import org.kimios.kernel.repositories.model.Repository;
import org.kimios.kernel.repositories.model.RepositoryAccessor;
import org.kimios.utils.configuration.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class RepositoryManager
{
    private static Logger log = LoggerFactory.getLogger(RepositoryManager.class);

    private RepositoryService repositoryService;

    private Repository defaultRepository;

    private static RepositoryManager manager;

    private String defaultRepositoryPath;

    private RepositoryAccessor defaultAccessor;

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
                defaultAccessor = new DefaultRepositoryAccessor(defaultRepositoryPath);
                /*
                    Create default repo
                 */
                File repoDir = new File(defaultRepositoryPath);
                if(!repoDir.exists()){
                    repoDir.mkdirs();
                }
            } else {
                defaultRepository = repository;
                defaultRepositoryPath = repository.getPath();
                if(repository.getImplementor() != null){
                    Class<?> accessorImpl = Class.forName(repository.getImplementor());
                    Object accessorInstance = accessorImpl.getDeclaredConstructors()[0].newInstance(defaultRepositoryPath);
                    if(accessorInstance instanceof RepositoryAccessor){
                        defaultAccessor = (RepositoryAccessor)accessorInstance;
                    }
                } else {
                    defaultAccessor  = new DefaultRepositoryAccessor(defaultRepositoryPath);
                }

            }
        } catch (Exception e) {
            log.error("Repository error: unable to get default repository", e);
        }
    }

    public static InputStream accessVersionStream(DocumentVersion version)
            throws RepositoryException, ConfigException, IOException
    {
        return init().defaultAccessor.accessVersionStream(version);
    }

    public static void writeVersion(DocumentVersion version, InputStream in)
            throws DataSourceException, ConfigException, RepositoryException
    {
        init().defaultAccessor.writeVersion(version, in);
    }

    public static void readVersionToStream(DocumentVersion version, OutputStream out)
            throws DataSourceException, ConfigException, RepositoryException
    {
        init().defaultAccessor.readVersionToStream(version, out);
    }

    public static OutputStream accessOutputStreamVersion(DocumentVersion version) throws Exception
    {
        return init().defaultAccessor.accessOutputStreamVersion(version);
    }

    public static File directFileAccess(DocumentVersion version) throws Exception
    {
        return init().defaultAccessor.directFileAccess(version);
    }

    public static RandomAccessFile randomAccessFile(DocumentVersion version, String mode) throws Exception
    {
        return init().defaultAccessor.randomAccessFile(version, mode);
    }

    public static void initRepositoryStorage(DocumentVersion version) throws Exception
    {
        init().defaultAccessor.initRepositoryStorage(version);
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
        init().defaultAccessor.copyVersion(source, target);
    }
}
