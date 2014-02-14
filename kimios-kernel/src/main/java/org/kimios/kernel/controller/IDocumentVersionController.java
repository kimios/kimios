/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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
package org.kimios.kernel.controller;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.*;
import org.kimios.kernel.events.annotations.DmsEvent;
import org.kimios.kernel.events.annotations.DmsEventName;
import org.kimios.kernel.exception.*;
import org.kimios.kernel.security.Session;

import java.util.List;
import java.util.Vector;

public interface IDocumentVersionController
{
    /**
     * Return a document version for a given document version id
     */
    public DocumentVersion getDocumentVersion(Session session,
            long documentVersionId) throws ConfigException,
            DataSourceException, AccessDeniedException;

    /**
     * Create a document version for a given document id
     */
    @DmsEvent(eventName = { DmsEventName.DOCUMENT_VERSION_CREATE })
    public long createDocumentVersion(Session session, long documentId)
            throws CheckoutViolationException, ConfigException,
            DataSourceException, AccessDeniedException;

    /**
     * Create a document version from the last existing version : copies meta data and document type (used to create the
     * document type history)
     */
    @DmsEvent(eventName = { DmsEventName.DOCUMENT_VERSION_CREATE_FROM_LATEST })
    public long createDocumentVersionFromLatest(Session session,
            long documentId) throws CheckoutViolationException,
            ConfigException, DataSourceException, AccessDeniedException, RepositoryException;

    /**
     * Update document version for document type change
     */
    @DmsEvent(eventName = { DmsEventName.DOCUMENT_VERSION_UPDATE })
    public void updateDocumentVersion(Session session, long documentId,
            long documentTypeId, String xmlStream) throws XMLException, CheckoutViolationException,
            ConfigException, DataSourceException, AccessDeniedException;

    /**
     * Remove document version and linked metas
     */
    public void deleteDocumentVersion(long documentVersionId)
            throws CheckoutViolationException, ConfigException,
            DataSourceException, AccessDeniedException;

    /**
     * Return the meta value for a given document version and a given meta data
     */
    public Object getMetaValue(Session session, long documentVersionId,
            long metaId) throws ConfigException, DataSourceException,
            AccessDeniedException;

    /**
     * Convenience method to instantiate meta value bean
     */
    public MetaValue toMetaValue(int metaType, DocumentVersion documentVersion,
            Meta meta, String metaValue);

    /**
     * Update meta values for a given document version from an xml descriptor
     */
    public void updateMetasValue(Session session, long uid, String xmlStream)
            throws XMLException, AccessDeniedException, ConfigException,
            DataSourceException;

    /**
     * Return all versions of a given document
     */
    public Vector<DocumentVersion> getDocumentVersions(Session session,
            long documentId) throws ConfigException, DataSourceException;

    /**
     * Return the current Document version for a given document
     */
    public DocumentVersion getLastDocumentVersion(Session session,
            long documentId) throws AccessDeniedException, ConfigException,
            DataSourceException;

    /**
     * Get comment for a given comment id
     */
    public DocumentComment getDocumentComment(Session session, long uid)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Return comment list for a given document version id
     */
    public Vector<DocumentComment> getDocumentComments(Session session,
            long documentVersionId) throws AccessDeniedException,
            ConfigException, DataSourceException;

    /**
     * Add comment on a given document version id
     */
    public long createDocumentComment(Session session, long documentVersionId,
            String comment) throws AccessDeniedException, ConfigException,
            DataSourceException;

    /**
     * Update comment for a given document version id and comment id
     */
    public void updateDocumentComment(Session session, long documentVersionId,
            long commentId, String newComment) throws AccessDeniedException,
            ConfigException, DataSourceException;

    /**
     * Remove comment
     */
    public void deleteDocumentComment(Session session, long commentId)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Return the meta datas list for a given document type
     */
    public Vector<Meta> getMetas(Session session, long documentTypeId)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Return meta for a given meta id
     */
    public Meta getMeta(Session session, long metaId)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Get the unherited meta list for a given document type id
     */
    public Vector<Meta> getUnheritedMetas(Session session, long documentTypeId)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Get document version by Hashes
     */
    @Deprecated
    public DocumentVersion getDocumentVersion(Session session, String hashMD5,
            String hashSHA) throws AccessDeniedException, ConfigException,
            DataSourceException;

    /**
     * @return DocumentType
     */
    public DocumentType getDocumentTypeByName(Session session, String name)
            throws AccessDeniedException, ConfigException, DataSourceException;

    public void updateDocumentVersionInformation(Session session, long documentVersionId)
            throws ConfigException, DataSourceException, AccessDeniedException, RepositoryException;

    /**
     * Get document version by Hashes
     */
    public List<MetaValue> getMetaValues(Session session, long documentVersionId)
            throws ConfigException, DataSourceException, AccessDeniedException;

    /**
     * List document versions orphans
     */
    public List<DocumentVersion> getOprhansDocumentVersion();
}
