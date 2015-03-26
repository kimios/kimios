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
import org.kimios.kernel.exception.DataSourceException;

import java.util.List;
import java.util.Vector;

public interface DocumentTypeFactory
{
    public DocumentType getDocumentType(long uid) throws ConfigException, DataSourceException;

    public Vector<DocumentType> getDocumentTypes() throws ConfigException, DataSourceException;

    public void saveDocumentType(DocumentType t) throws ConfigException, DataSourceException;

    public void updateDocumentType(DocumentType t) throws ConfigException, DataSourceException;

    public void deleteDocumentType(DocumentType t) throws ConfigException, DataSourceException;

    public DocumentType getDocumentTypeByName(String typeName) throws ConfigException, DataSourceException;

    public List<DocumentType> getChildrenDocumentType(long documentTypeId) throws ConfigException, DataSourceException;
}

