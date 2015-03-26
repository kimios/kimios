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
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.DocumentVersion;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.RepositoryException;

import java.io.*;

/**
 * Created by farf on 7/12/14.
 */
public interface RepositoryAccessor {


    public InputStream accessVersionStream(DocumentVersion version)
            throws RepositoryException, ConfigException, IOException;

    public void writeVersion(DocumentVersion version, InputStream in)
            throws DataSourceException, ConfigException, RepositoryException;

    public OutputStream accessOutputStreamVersion(DocumentVersion version) throws Exception;

    public RandomAccessFile randomAccessFile(DocumentVersion version, String mode) throws Exception;

    public void initRepositoryStorage(DocumentVersion version) throws Exception;

    public void copyVersion(DocumentVersion source, DocumentVersion target)
            throws DataSourceException, ConfigException, RepositoryException;

    public void readVersionToStream(DocumentVersion version, OutputStream out)
            throws DataSourceException, ConfigException, RepositoryException;
}
