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
package org.kimios.webservices;

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.kimios.kernel.index.query.model.Criteria;
import org.kimios.kernel.index.query.model.SearchRequest;
import org.kimios.kernel.index.query.model.SearchResponse;
import org.kimios.kernel.ws.pojo.DMEntity;
import org.kimios.kernel.ws.pojo.Document;
import org.kimios.webservices.exceptions.DMServiceException;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: farf Date: 4/1/12 Time: 5:07 PM
 */
@Path("/search")
@WebService(targetNamespace = "http://kimios.org", serviceName = "SearchService")
@CrossOriginResourceSharing(allowAllOrigins = true)
public interface SearchService {
    @GET
    @Path("/quickSearch")
    @Produces("application/json")
    public SearchResponse quickSearch(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionUid,
            @QueryParam(value = "query") @WebParam(name = "query") String query,
            @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityId,
            @QueryParam(value = "start") @WebParam(name = "start") int start,
            @QueryParam(value = "pageSize") @WebParam(name = "pageSize") int pageSize,
            @QueryParam(value = "sortField") @WebParam(name = "sortField") String sortField,
            @QueryParam(value = "sortDir") @WebParam(name = "sortDir") String sortDir)
            throws DMServiceException;

    @GET
    @Path("/advancedSearch")
    @Produces("application/json")
    public List<Document> advancedSearch(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "xmlStream") @WebParam(name = "xmlStream") String xmlStream,
            @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityId)
            throws DMServiceException;

    @GET
    @Path("/getDMentityFromPath")
    @Produces("application/json")
    public DMEntity getDMentityFromPath(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "path") @WebParam(name = "path") String path)
            throws DMServiceException;

    @GET
    @Path("/getPathFromDMEntity")
    @Produces("application/json")
    public String getPathFromDMEntity(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "entityId") @WebParam(name = "entityId") long entityId)
            throws DMServiceException;


    @POST
    @Path("/advancedSearchDocument")
    @Consumes("application/json")
    @Produces("application/json")
    public SearchResponse advancedSearchDocuments(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @WebParam(name = "criterias") List<Criteria> criterias,
            @QueryParam(value = "start") @WebParam(name = "start") int start,
            @QueryParam(value = "pageSize") @WebParam(name = "pageSize") int pageSize,
            @QueryParam(value = "sortField") @WebParam(name = "sortField") String sortField,
            @QueryParam(value = "sortDir") @WebParam(name = "sortDir") String sortDir,
            @QueryParam(value = "virtualPath") @WebParam(name = "virtualPath") String virtualPath,
            @QueryParam(value = "requestId") @WebParam(name = "requestId") @DefaultValue(value = "-1") long requestId,
            @QueryParam(value = "mustSave") @WebParam(name = "mustSave") @DefaultValue(value = "false") boolean mustSave
        )
            throws DMServiceException;


    @POST
    @Path("/saveSearchQuery")
    @Produces("application/json")
    @Consumes("application/json")
    public void saveSearchQuery(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                                @QueryParam(value = "id") @WebParam(name = "id") Long id,
                                @QueryParam(value = "name") @WebParam(name = "name") String name,
                                @WebParam(name = "criterias") List<org.kimios.kernel.index.query.model.Criteria> criterias,
                                @QueryParam(value = "sortField") @WebParam(name = "sortField") String sortField,
                                @QueryParam(value = "sortDir") @WebParam(name = "sortDir") String sortDir)

            throws DMServiceException;

    @GET
    @Path("/deleteSearchQuery")
    @Produces("application/json")
    public void deleteSearchQuery(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                                  @QueryParam(value = "searchQueryId") @WebParam(name = "searchQueryId") Long id)
            throws DMServiceException;

    @GET
    @Path("/listSearchQueries")
    @Produces("application/json")
    public List<SearchRequest> listSearchQueries(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;


    @GET
    @Path("/listMySearchQueries")
    @Produces("application/json")
    public List<SearchRequest> listMySearchQueries(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId )
            throws DMServiceException;


    @GET
    @Path("/loadSearchQuery")
    @Produces("application/json")
    public SearchRequest loadSearchQuery(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "searchQueryId") @WebParam(name = "searchQueryId") Long id)
            throws DMServiceException;

    @GET
    @Path("/executeSearchQuery")
    @Produces("application/json")
    public SearchResponse executeSearchQuery(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "searchQueryId") @WebParam(name = "searchQueryId") long queryId,
            @QueryParam(value = "start") @WebParam(name = "start") int start,
            @QueryParam(value = "pageSize") @WebParam(name = "pageSize") int pageSize,
            @QueryParam(value = "sortField") @WebParam(name = "sortField") String sortField,
            @QueryParam(value = "sortDir") @WebParam(name = "sortDir") String sortDir,
            @QueryParam(value = "virtualPath") @WebParam(name = "virtualPath") String virtualPath)
            throws DMServiceException;

    @GET
    @Path("/listFields")
    @Produces("application/json")
    public List<String> listAvailableSearchFields(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;


    @POST
    @Path("/advancedSaveSearchQuery")
    @Produces("application/json")
    @Consumes("application/json")
    public Long advancedSaveSearchQuery(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @WebParam(name = "searchRequest") SearchRequest searchRequest)
        throws DMServiceException;
}
