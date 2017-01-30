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
package org.kimios.kernel.dms;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.dms.model.DocumentType;
import org.kimios.kernel.dms.model.DocumentVersion;
import org.kimios.exceptions.DataSourceException;

import java.util.Vector;

public interface DocumentVersionFactory
{
    public DocumentVersion getDocumentVersion(long uid) throws ConfigException, DataSourceException;

    public DocumentVersion getLastDocumentVersion(Document d) throws ConfigException, DataSourceException;

    public Vector<DocumentVersion> getVersions(Document d) throws ConfigException, DataSourceException;

    public void saveDocumentVersion(DocumentVersion v) throws ConfigException, DataSourceException;

    public void updateDocumentVersion(DocumentVersion v) throws ConfigException, DataSourceException;

    public void updateDocumentVersionBulk(DocumentVersion v) throws ConfigException, DataSourceException;

    public void deleteDocumentVersion(DocumentVersion v) throws ConfigException, DataSourceException;

    public Vector<DocumentVersion> getVersionsToDelete() throws ConfigException, DataSourceException;

    public Vector<DocumentVersion> getTwoLastDocumentVersion(Document d) throws ConfigException, DataSourceException;

    public void removeDocumentType(DocumentType dt) throws ConfigException, DataSourceException;

    public DocumentVersion getVersionByHashes(String md5, String sha1) throws ConfigException, DataSourceException;

    public void saveDocumentVersionBulk(DocumentVersion v) throws ConfigException, DataSourceException;
}

