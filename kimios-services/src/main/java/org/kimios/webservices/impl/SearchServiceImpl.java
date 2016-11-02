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
package org.kimios.webservices.impl;


import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.kimios.kernel.configuration.Config;
import org.kimios.kernel.index.query.model.Criteria;
import org.kimios.kernel.index.query.model.SearchRequest;
import org.kimios.kernel.index.query.model.SearchResponse;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.ws.pojo.Document;
import org.kimios.utils.configuration.ConfigurationManager;
import org.kimios.webservices.CoreService;
import org.kimios.webservices.exceptions.DMServiceException;
import org.kimios.webservices.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebService;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebService( targetNamespace = "http://kimios.org", serviceName = "SearchService", name = "SearchService" )
public class SearchServiceImpl
        extends CoreService
        implements SearchService, CamelContextAware {

    private static Logger log = LoggerFactory.getLogger(SearchService.class);

    public SearchResponse quickSearch(String sessionUid, String query, long dmEntityUid, int start,
                                      int pageSize, String sortField, String sortDir)
            throws DMServiceException {
        try {
            Session s = getHelper().getSession(sessionUid);
            return searchController.quickSearchPojos(s, query, dmEntityUid, start, pageSize, sortField, sortDir);

        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionUid
     * @param xmlStream
     * @return
     * @throws DMServiceException
     */
    public List<Document> advancedSearch(String sessionUid, String xmlStream, long dmEntityUid)
            throws DMServiceException {
        try {
            Session s = getHelper().getSession(sessionUid);
            return searchController.advancedSearchPojos(s, xmlStream, dmEntityUid);

        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public org.kimios.kernel.ws.pojo.DMEntity getDMentityFromPath( String sessionUid, String path )
        throws DMServiceException
    {
        try
        {
            Session s = getHelper().getSession( sessionUid );
            org.kimios.kernel.ws.pojo.DMEntity entity = null;
            return pathController.getDMEntityPojoFromPath(s, path);
        }
        catch ( Exception e )
        {
            throw getHelper().convertException( e );
        }
    }

    public String getPathFromDMEntity(String sessionUid, long entityUid)
            throws DMServiceException {
        try {
            Session s = getHelper().getSession(sessionUid);
            String r = pathController.getPathFromDMEntity(s, entityUid);
            return r;
        }
        catch ( Exception e )
        {
            throw getHelper().convertException( e );
        }
    }


    public SearchResponse advancedSearchDocuments( String sessionId, List<Criteria> criterias, int start, int pageSize,
                                                   String sortField, String sortDir, String virtualPath, long requestId, boolean mustSave )
        throws DMServiceException
    {
        try
        {
            Session s = getHelper().getSession(sessionId);
            return searchController.advancedSearchDocuments( s, criterias, start, pageSize, sortField, sortDir,
                                                             virtualPath, requestId, mustSave );
        }
        catch ( Exception e )
        {
            throw getHelper().convertException( e );
        }
    }


    public void saveSearchQuery( String sessionId, Long id, String name,
                                 List<org.kimios.kernel.index.query.model.Criteria> criterias, String sortField,
                                 String sortDir )
        throws DMServiceException
    {
        try
        {
            Session s = getHelper().getSession( sessionId );
            searchController.saveSearchQuery( s, id, name, criterias, sortField, sortDir );
        }
        catch ( Exception e )
        {
            throw getHelper().convertException( e );
        }

    }

    public void deleteSearchQuery( String sessionId, Long id )
        throws DMServiceException
    {
        try
        {
            Session s = getHelper().getSession( sessionId );
            searchController.deleteSearchQuery(s, id);
        }
        catch ( Exception e )
        {
            throw getHelper().convertException( e );
        }
    }

    public List<SearchRequest> listSearchQueries( String sessionId )
        throws DMServiceException
    {
        try
        {
            Session s = getHelper().getSession( sessionId );
            //return searchController.listSavedSearch( s )
            return searchController.searchRequestList(s);
        }
        catch ( Exception e )
        {
            throw getHelper().convertException( e );
        }
    }

    public List<SearchRequest> listMySearchQueries( String sessionId )
            throws DMServiceException
    {
        try
        {
            Session s = getHelper().getSession( sessionId );
            return searchController.loadMysSearchQueriesNotPublished(s);
        }
        catch ( Exception e )
        {
            throw getHelper().convertException( e );
        }
    }

    public List<SearchRequest> listPublicSearchQueries( String sessionId )
            throws DMServiceException
    {
        try
        {
            Session s = getHelper().getSession( sessionId );
            return searchController.searchPublicRequestList( s );
        }
        catch ( Exception e )
        {
            throw getHelper().convertException( e );
        }
    }

    public SearchRequest loadSearchQuery( String sessionId, Long id )
        throws DMServiceException
    {
        try
        {
            Session s = getHelper().getSession( sessionId );
            return searchController.loadSearchQuery( s, id );
        }
        catch ( Exception e )
        {
            throw getHelper().convertException( e );
        }
    }

    public SearchResponse executeSearchQuery( String sessionId, long queryId, int start, int pageSize, String sortField,
                                              String sortDir, String virtualPath )
        throws DMServiceException
    {
        try
        {
            Session s = getHelper().getSession( sessionId );
            return searchController.executeSearchQueryOrBrowse( s, queryId, start, pageSize, sortField, sortDir,
                                                                virtualPath );
        }
        catch ( Exception e )
        {
            throw getHelper().convertException( e );
        }
    }

    public List<String> listAvailableSearchFields(String sessionId) throws DMServiceException {
        try
        {
            Session s = getHelper().getSession( sessionId );
            return searchManagementController.listDocumentAvailableFields(s);
        }
        catch ( Exception e )
        {
            throw getHelper().convertException( e );
        }
    }


    public Long advancedSaveSearchQuery(String sessionId, SearchRequest searchRequest) throws DMServiceException {
        try
        {
            Session s = getHelper().getSession( sessionId );
            return searchController.advancedSaveSearchQueryWithSecurity(s, searchRequest, searchRequest.getSecurities());
        }
        catch ( Exception e )
        {
            throw getHelper().convertException( e );
        }
    }

    public InputStream advancedSearchDocumentsExport(String sessionId, List<Criteria> criterias, int start, int pageSize,
                                                  String sortField, String sortDir, String virtualPath, long requestId )
            throws DMServiceException
    {
        try
        {
            Session s = getHelper().getSession(sessionId);
            SearchResponse searchResponse = searchController.advancedSearchDocuments( s, criterias, start, pageSize, sortField, sortDir,
                    virtualPath, requestId, false );
            org.apache.camel.ProducerTemplate template = camelContext.createProducerTemplate();
            //read file
            String fileName = "Kimios_Export_" + new SimpleDateFormat("yyyy_MM_dd_HH_mm").format(new Date()) + ".csv";
            template.sendBodyAndHeader("direct:csvExport", searchResponse.getRows(), "kimiosCsvFileName", fileName);
            //read file
            /*Response.ResponseBuilder response = Response.ok(
                    new FileInputStream(ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + "/csv/" + fileName));
            response.header("Content-Description", "File Transfer");
            response.header("Content-Type", "text/csv");
            response.header("Content-Transfer-Encoding", "binary");
            response.header("Expires", "0");
            response.header("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.header("Pragma", "public");
            response.header("Content-Length",
                    new File(ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + "/csv/" + fileName).length());
            response.header("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            return response.build();*/
            return new FileInputStream(ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + "/csv/" + fileName);

        }
        catch ( Exception e )
        {
            throw getHelper().convertException( e );
        }
    }

    private CamelContext camelContext;

    @Override
    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    @Override
    public CamelContext getCamelContext() {
        return camelContext;
    }
}

