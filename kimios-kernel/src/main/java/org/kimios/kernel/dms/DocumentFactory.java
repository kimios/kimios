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
import org.kimios.kernel.dms.model.Folder;
import org.kimios.exceptions.DataSourceException;

import java.util.Date;
import java.util.List;

public interface DocumentFactory
{
    public Document getDocument(long uid) throws ConfigException, DataSourceException;

    public Document getDocument(String name, String extension, Folder f) throws ConfigException, DataSourceException;

    public List<Document> getDocuments() throws ConfigException, DataSourceException;

    public List<Document> getDocuments(Folder f) throws ConfigException, DataSourceException;

    public void saveDocument(Document d) throws ConfigException, DataSourceException;

    public void saveDocumentNoFlush(Document d) throws ConfigException, DataSourceException;

    public void updateDocument(Document d) throws ConfigException, DataSourceException;

    public void deleteDocument(Document d) throws ConfigException, DataSourceException;

    public List<Document> getRelatedDocuments(Document d) throws ConfigException, DataSourceException;

    public List<Document> getExpiredDocuments(String sourceWorkspace, Date date);

    public List<Document> getLockedDocuments(String owner, String ownerSource) throws ConfigException,
        DataSourceException;

    public void addRelatedDocument(Document d, Document relatadDocument) throws ConfigException, DataSourceException;

    public void removeRelatedDocument(Document d, Document toRemove) throws ConfigException, DataSourceException;

    public List<Document> getDocumentsFromIds(List<Long> listIds) throws ConfigException, DataSourceException;

    public List<org.kimios.kernel.ws.pojo.Document> getDocumentsPojos(List<Document> list)
            throws ConfigException, DataSourceException;

    public List<org.kimios.kernel.ws.pojo.Document> getDocumentsPojosFromIds(List<Long> listIds)
            throws ConfigException, DataSourceException;

    public org.kimios.kernel.ws.pojo.Document getDocumentPojoFromId(long documentId)
            throws ConfigException, DataSourceException;

}

