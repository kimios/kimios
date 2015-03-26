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
package org.kimios.client.controller;

import org.kimios.client.exception.*;
import org.kimios.kernel.ws.pojo.*;
import org.kimios.webservices.StudioService;

/**
 * StudioController is used to manage document types, metafeeds and workflows
 */
public class StudioController
{

    private StudioService client;

    public StudioService getClient()
    {
        return client;
    }

    public void setClient( StudioService client )
    {
        this.client = client;
    }

    /**
     * Return document Type list (NB: this method is free of access, because of
     * use in document management, not only in studio
     */
    public DocumentType[] getDocumentTypes( String sessionId )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            return client.getDocumentTypes( sessionId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Return Document Type for a given uid
     */
    public DocumentType getDocumentType( String sessionId, long documentTypeId )
        throws Exception
    {
        try
        {
            return client.getDocumentType( sessionId, documentTypeId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Return values list for a given meta feed
     */
    public String[] getMetaFeedValues( String sessionId, long metaFeedId )
        throws Exception
    {
        try
        {
            return client.getMetaFeedValues( sessionId, metaFeedId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * List available meta feed (see meta feed manager
     */
    public String[] getAvailableMetaFeedTypes( String sessionId )
        throws Exception
    {
        try
        {
            return client.getAvailableMetaFeeds( sessionId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Return the meta feed list
     */
    public MetaFeed[] getMetaFeeds( String sessionId )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            return client.getMetaFeeds( sessionId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Search a value for a given instantiated meta feed
     */
    public String[] searchMetaFeedValues( String sessionId, long metaFeedId, String criteria )
        throws Exception
    {
        try
        {
            return client.searchMetaFeedValues( sessionId, metaFeedId, criteria );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Return an instantiated meta feed for a given id
     */
    public MetaFeed getMetaFeed( String sessionId, long metaFeedId )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            return client.getMetaFeed( sessionId, metaFeedId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Update meta feed name
     */
    public void updateMetaFeed( String sessionId, long metaFeedId, String name )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            client.updateMetaFeed( sessionId, metaFeedId, name );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Instantiate a meta feed of given class name and store it
     * database
     */
    public long addMetaFeed( String sessionId, String name, String className )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            return client.createMetaFeed( sessionId, name, className );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Remove meta feed
     */
    public void removeMetaFeed( String sessionId, long metaFeedId )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            client.deleteMetaFeed( sessionId, metaFeedId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Update existing document type
     */
    public void updateDocumentType( String sessionId, String xmlStream )
        throws Exception, AccessDeniedException, DMSException, MetaValueTypeException
    {
        try
        {
            client.updateDocumentType( sessionId, xmlStream );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Create document type (and metas), from an xml descriptor
     */
    public void addDocumentType( String sessionId, String xmlStream )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            client.createDocumentType( sessionId, xmlStream );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Remove document type
     */
    public void deleteDocumentType( String sessionId, long documentTypeId )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            client.deleteDocumentType( sessionId, documentTypeId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Update value list for the default enumeration meta feed
     */
    public void updateEnumerationValues( String sessionId, String xmlStream )
        throws Exception, AccessDeniedException, DMSException, XMLException
    {
        try
        {
            client.updateEnumerationValues( sessionId, xmlStream );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Create a new worfklow from an xml descriptor
     */
    public long createWorkflow( String sessionId, String name, String description, String xmlStream )
        throws Exception, AccessDeniedException, DMSException, XMLException
    {
        try
        {
            return client.createWorkflow( sessionId, name, description, xmlStream );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Update workflow from an xml descriptor
     */
    public void updateWorkflow( String sessionId, long workflowId, String name, String description, String xmlStream )
        throws Exception, AccessDeniedException, DMSException, XMLException
    {
        try
        {
            client.updateWorkflow( sessionId, workflowId, name, description, xmlStream );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Remove workflow
     */
    public void deleteWorkflow( String sessionId, long workflowId )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            client.deleteWorkflow( sessionId, workflowId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Return workflows list
     */
    public Workflow[] getWorkflows( String sessionId )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            return client.getWorkflows( sessionId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Get workflow for a given id
     */
    public Workflow getWorkflow( String sessionId, long workflowId )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            return client.getWorkflow( sessionId, workflowId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Get status of a given workflow
     */
    public WorkflowStatus[] getWorkflowStatuses( String sessionId, long workflowId )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            return client.getWorkflowStatuses( sessionId, workflowId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Get a given workflow status
     */
    public WorkflowStatus getWorkflowStatus( String sessionId, long workflowStatusId )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            return client.getWorkflowStatus( sessionId, workflowStatusId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Add a workflow status for a given workflow
     */
    public long createWorkflowStatus( String sessionId, long workflowId, String name, long successorId )
        throws Exception
    {
        try
        {
            return client.createWorkflowStatus( sessionId, workflowId, name, successorId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Update workflow status
     */
    public void updateWorkflowStatus( String sessionId, long workflowStatusId, long workflowId, String name,
                                      long successorId )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            client.updateWorkflowStatus( sessionId, workflowStatusId, workflowId, name, successorId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Remove Workflow status
     */
    public void deleteWorkflowStatus( String sessionId, long workflowStatusId )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            client.deleteWorkflowStatus( sessionId, workflowStatusId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Add user as manager of a given workflow status (means the user will be
     * able to validate or reject the status)
     */
    public void createWorkflowStatusManager( String sessionId, long workflowStatusId, String securityEntityName,
                                             String securityEntitySource, int securityEntityType )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            client.createWorkflowStatusManager( sessionId, workflowStatusId, securityEntityName, securityEntitySource,
                                                securityEntityType );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Remove user from status management
     */
    public void deleteWorkflowStatusManager( String sessionId, long workflowStatusId, String securityEntityName,
                                             String securityEntitySource, int securityEntityType )
        throws Exception, AccessDeniedException, DMSException
    {
        try
        {
            client.deleteWorkflowStatusManager( sessionId, workflowStatusId, securityEntityName, securityEntitySource,
                                                securityEntityType );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Get managers list for a given workflow status
     */
    public WorkflowStatusManager[] getWorkflowStatusManagers( String sessionId, long workflowStatusId )
        throws Exception
    {
        try
        {
            return client.getWorkflowStatusManagers( sessionId, workflowStatusId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }
}

