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
package org.kimios.kernel.controller;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.*;
import org.kimios.kernel.exception.*;
import org.kimios.kernel.security.Session;
import org.kimios.kernel.xml.XSDException;

import java.util.List;
import java.util.Vector;

public interface IStudioController
{
    /**
     * Return Document Type for a given uid
     */
    public DocumentType getDocumentType(long uid) throws ConfigException,
            DataSourceException;

    /**
     * Return document Type list (NB: this method is free of access, because of use in document management, not only in
     * studio
     */
    public Vector<DocumentType> getDocumentTypes() throws ConfigException,
            DataSourceException;

    /**
     * Create document type (and metas), from an xml descriptor
     */
    public void createDocumentType(Session session, String xmlStream)
            throws AccessDeniedException, ConfigException, DataSourceException,
            XMLException, XSDException;

    /**
     * Update existing document type
     */
    public void updateDocumentType(Session session, String xmlStream)
            throws MetaValueTypeException, AccessDeniedException,
            ConfigException, DataSourceException, XMLException, XSDException;

    /**
     * Remove document type
     */
    public void deleteDocumentType(Session session, long uid)
            throws AccessDeniedException, ConfigException, DataSourceException,
            XMLException;

    /**
     * List available meta feed (see meta feed manager
     */
    public List<String> getAvailableMetaFeedTypes(Session session)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Return an instantiated meta feed for a given id
     */
    public MetaFeedImpl getMetaFeed(long uid) throws ConfigException,
            DataSourceException;

    /**
     * Return the meta feed list
     */
    public List<MetaFeedImpl> getMetaFeeds(Session session)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Search a value for a given instantiated meta feed
     */
    public String[] searchMetaFeedValues(Session session, long metaFeedUid,
            String criteria) throws AccessDeniedException, ConfigException,
            DataSourceException, MetaFeedSearchException;

    /**
     * Instantiate a meta feed of given class name and store it database
     */
    public long createMetaFeed(Session session, String name, String className)
            throws RepositoryException, AccessDeniedException, ConfigException,
            DataSourceException;

    /**
     * Update meta feed name
     */
    public void updateMetaFeed(Session session, long uid, String name)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Remove meta feed
     */
    public void deleteMetaFeed(Session session, long uid)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Return values list for a given meta feed
     */
    public List<String> getMetaFeedValues(Session session, long uid)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Update value list for the default enumeration meta feed
     */
    public void updateEnumerationValues(Session session, String xmlStream)
            throws XMLException, AccessDeniedException, ConfigException,
            DataSourceException, XSDException;

    /**
     * Return workflows list
     */
    public Vector<Workflow> getWorkflows() throws ConfigException,
            DataSourceException;

    /**
     * Get workflow for a given id
     */
    public Workflow getWorkflow(long uid) throws ConfigException,
            DataSourceException;

    /**
     * Get status of a given workflow
     */
    public Vector<WorkflowStatus> getWorkflowStatuses(long workflowUid)
            throws ConfigException, DataSourceException;

    /**
     * Get a given workflow status
     */
    public WorkflowStatus getWorkflowStatus(long workflowStatusUid)
            throws ConfigException, DataSourceException;

    /**
     * Add user as manager of a given workflow status (means the user will be able to validate or reject the status)
     */
    public void createWorkflowStatusManager(Session session,
            long workflowStatusUid, String securityEntityName,
            String securityEnitySource, int securityEntityType)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Remove user from status management
     */
    public void deleteWorkflowStatusManager(Session session,
            long workflowStatusUid, String securityEntityName,
            String securityEnitySource, int securityEntityType)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Get managers list for a given workflow status
     */
    public Vector<WorkflowStatusManager> getWorkflowStatusManagers(
            Session session, long workflowStatusUid)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Create a new worfklow from an xml descriptor
     */
    public long createWorkflow(Session session, String name,
            String description, String xmlStream) throws XMLException,
            AccessDeniedException, ConfigException, DataSourceException,
            XSDException;

    /**
     * Update workflow from an xml descriptor
     */
    public void updateWorkflow(Session session, long workflowUid, String name,
            String description, String xmlStream) throws XMLException,
            AccessDeniedException, ConfigException, DataSourceException,
            XSDException;

    /**
     * Remove workflow
     */
    public void deleteWorkflow(Session session, long workflowUid)
            throws AccessDeniedException, ConfigException, DataSourceException;

    /**
     * Add a workflow status for a given workflow
     */
    public long createWorkflowStatus(Session session, long workflowUid,
            String name, long successorUid) throws ConfigException,
            DataSourceException, AccessDeniedException;

    /**
     * Update workflow status
     */
    public void updateWorkflowStatus(Session session, long workflowStatusUid,
            long workflowUid, String name, long successorUid)
            throws ConfigException, DataSourceException, AccessDeniedException;

    /**
     * Remove Workflow status
     */
    public void deleteWorkflowStatus(Session session, long workflowStatusUid)
            throws ConfigException, DataSourceException, AccessDeniedException;
}
