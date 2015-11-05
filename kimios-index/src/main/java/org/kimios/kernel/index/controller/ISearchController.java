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
package org.kimios.kernel.index.controller;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.model.DMEntity;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.IndexException;
import org.kimios.kernel.index.query.model.Criteria;
import org.kimios.kernel.index.query.model.SearchRequest;
import org.kimios.kernel.index.query.model.SearchRequestSecurity;
import org.kimios.kernel.index.query.model.SearchResponse;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.ws.pojo.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface ISearchController {
    public List<org.kimios.kernel.dms.model.Document> quickSearch(Session session, String query, DMEntity entity)
            throws IndexException, DataSourceException, ConfigException;

    public SearchResponse quickSearchPojos(Session session, String query, DMEntity entity, int start, int pageSize,
                                           String sortField, String sortDir)
            throws IndexException, DataSourceException, ConfigException;

    public SearchResponse quickSearchPojos(Session session, String query, long dmEntityUid, int start, int pageSize,
                                           String sortField, String sortDir)
            throws IndexException, DataSourceException, ConfigException;

    public List<org.kimios.kernel.dms.model.Document> advancedSearch(Session session, String xmlStream, DMEntity entity)
            throws DataSourceException, ConfigException, IndexException, IOException, ParserConfigurationException,
            SAXException;

    public List<Document> advancedSearchPojos(Session session, String xmlStream, DMEntity entity)
            throws DataSourceException, ConfigException, IndexException, IOException, ParserConfigurationException,
            SAXException;

    public List<Document> advancedSearchPojos(Session session, String xmlStream, long dmEntityUid)
            throws DataSourceException, ConfigException, IndexException, IOException, ParserConfigurationException,
            SAXException;


    public SearchResponse advancedSearchDocuments(Session session, List<Criteria> criteriaList, int start,
                                                  int pageSize, String sortField, String sortDir)
            throws DataSourceException, ConfigException, IndexException, IOException, ParseException;

    public SearchResponse advancedSearchDocuments(Session session, List<Criteria> criteriaList, int start,
                                                  int pageSize, String sortField, String sortDir, String virtualPath, Long requestId, Boolean mustSave)
            throws DataSourceException, ConfigException, IndexException, IOException, ParseException;


    public void saveSearchQuery(Session session, Long id, String name, List<Criteria> criteriaList, String sortField,
                                String sortDir)
            throws DataSourceException, ConfigException, IndexException, IOException;

    public Long advancedSaveSearchQuery(Session session, SearchRequest request)
            throws DataSourceException, ConfigException, IndexException, IOException;

    public void deleteSearchQuery(Session session, Long id)
            throws AccessDeniedException, DataSourceException, ConfigException, IndexException, IOException;

    public SearchRequest loadSearchQuery(Session session, Long id)
            throws AccessDeniedException, DataSourceException, ConfigException, IndexException, IOException;

    public List<SearchRequest> loadMysSearchQueriesNotPublished(Session session)
            throws DataSourceException, ConfigException;


    public List<SearchRequest> listSavedSearch(Session session)
            throws AccessDeniedException, DataSourceException, ConfigException, IndexException, IOException;

    public SearchResponse executeSearchQuery(Session session, Long id, int start, int pageSize, String sortField,
                                             String sortDir)
            throws AccessDeniedException, DataSourceException, ConfigException, IndexException, IOException, ParseException;

    public SearchResponse executeSearchQueryOrBrowse(Session session, Long id, int start, int pageSize, String sortField,
                                                     String sortDir, String virtualPath)
            throws AccessDeniedException, DataSourceException, ConfigException, IndexException, IOException, ParseException;

    public List<String> listAvailableFields(Session session) throws AccessDeniedException, IndexException;

    public Long advancedSaveSearchQueryWithSecurity(Session session, SearchRequest searchRequest, List<SearchRequestSecurity> securities)
            throws DataSourceException, ConfigException, IndexException, IOException;

    public List<SearchRequest> searchRequestList(Session session)
            throws AccessDeniedException, DataSourceException,
            ConfigException, IndexException, IOException;


    public List<SearchRequest> searchPublicRequestList(Session session)
            throws AccessDeniedException, DataSourceException,
            ConfigException, IndexException, IOException;

}
