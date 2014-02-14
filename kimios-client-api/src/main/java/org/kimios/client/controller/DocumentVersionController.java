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
package org.kimios.client.controller;

import org.kimios.client.exception.*;
import org.kimios.kernel.ws.pojo.DocumentComment;
import org.kimios.kernel.ws.pojo.DocumentVersion;
import org.kimios.kernel.ws.pojo.Meta;
import org.kimios.kernel.ws.pojo.MetaValue;
import org.kimios.webservices.DMServiceException;
import org.kimios.webservices.DocumentVersionService;

import java.util.Date;
import java.util.List;

/**
 * DocumentVersionController target the content of documents and is used to
 * manage meta data values and comments
 */
public class DocumentVersionController
{

    private DocumentVersionService client;

    public DocumentVersionService getClient()
    {
        return client;
    }

    public void setClient( DocumentVersionService client )
    {
        this.client = client;
    }

    /**
     * Create a document version for a given document id
     */
    public long createDocumentVersion( String sessionId, long documentId )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            return client.createDocumentVersion( sessionId, documentId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Create a document version from the last existing version : copies meta data and document type
     * (used to create the document type history)
     */
    public long createDocumentVersionFromLatest( String sessionId, long documentId )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            return client.createDocumentVersionFromLatest( sessionId, documentId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Update document version for document type change
     */
    public void updateDocumentVersion( String sessionId, long documentId, long documentTypeId, String xmlStream )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            client.updateDocumentVersion( sessionId, documentId, documentTypeId, xmlStream );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Return the current Document version for a given document
     */
    public DocumentVersion getLastDocumenVersion( String sessionId, long documentId )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            return client.getLastDocumentVersion( sessionId, documentId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Return a document version for a given document version id
     */
    public DocumentVersion getDocumentVersion( String sessionId, long versionId )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            return client.getDocumentVersion( sessionId, versionId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Return all versions of a given document
     */
    public DocumentVersion[] getDocumentVersions( String sessionId, long documentId )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            return client.getDocumentVersions( sessionId, documentId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Get comment for a given comment id
     */
    public DocumentComment getDocumentComment( String sessionId, long documentId )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            return client.getDocumentComment( sessionId, documentId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Return comment list for a given document version id
     */
    public DocumentComment[] getDocumentComments( String sessionId, long documentVersionId )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            return client.getDocumentComments( sessionId, documentVersionId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Add comment on a given document version id
     */
    public void createDocumentComment( String sessionId, long documentVersionId, String comment )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            client.addDocumentComment( sessionId, documentVersionId, comment );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Update comment for a given document version id and comment id
     */
    public void updateDocumentComment( String sessionId, long commentId, long documentVersionId, String comment )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            client.updateDocumentComment( sessionId, documentVersionId, commentId, comment );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Remove comment
     */
    public void deleteDocumentComment( String sessionId, long commentId )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            client.removeDocumentComment( sessionId, commentId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Return the meta value for a given document version and a given meta data
     */
    public String getMetaStringValue( String sessionId, long documentVersionId, long metaId )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            return client.getMetaString( sessionId, documentVersionId, metaId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Return the meta value for a given document version and a given meta data
     */
    public double getMetaNumberValue( String sessionId, long documentVersionId, long metaId )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            return client.getMetaNumber( sessionId, documentVersionId, metaId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Return the meta value for a given document version and a given meta data
     */
    public boolean getMetaBooleanValue( String sessionId, long documentVersionId, long metaId )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            return client.getMetaBoolean( sessionId, documentVersionId, metaId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Return the meta value for a given document version and a given meta data
     */
    public Date getMetaDateValue( String sessionId, long documentVersionId, long metaId )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            return client.getMetaDate( sessionId, documentVersionId, metaId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Update meta values for a given document version from an xml descriptor
     */
    public void updateMetas( String sessionId, long documentVersionId, String xmlStream )
        throws Exception, DMSException, ConfigException, AccessDeniedException, XMLException
    {
        try
        {
            client.updateMetas( sessionId, documentVersionId, xmlStream );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Return meta for a given meta id
     */
    public Meta getMeta( String sessionId, long metaId )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            return client.getMeta( sessionId, metaId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }

    }

    /**
     * Return the meta datas list for a given document type
     */
    public Meta[] getMetas( String sessionId, long documentTypeId )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            return client.getMetas( sessionId, documentTypeId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Get the unherited meta list for a given document type id
     */
    public Meta[] getUnheritedMetas( String sessionId, long documentTypeId )
        throws Exception, DMSException, ConfigException, AccessDeniedException
    {
        try
        {
            return client.getUnheritedMetas( sessionId, documentTypeId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Get the whole meta values list, in one shot
     *
     * @param sessionId
     * @param documentVersionId
     * @return List<MetaValue>
     * @author <a href="mailto:fabien.alin@gmail.com">Fabien Alin aka Farf</a>
     * @since 1.0
     */
    public List<MetaValue> getMetaValues( String sessionId, long documentVersionId )
        throws Exception, DMServiceException
    {

        try
        {
            return client.getMetaValues( sessionId, documentVersionId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }

    }
}

