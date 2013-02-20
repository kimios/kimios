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

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.IndexException;
import org.kimios.kernel.index.query.model.Criteria;
import org.kimios.kernel.index.query.model.SearchRequest;
import org.kimios.kernel.index.query.model.SearchResponse;
import org.kimios.kernel.security.Session;
import org.kimios.kernel.ws.pojo.Document;
import org.xml.sax.SAXException;

public interface ISearchController
{
    public List<org.kimios.kernel.dms.Document> quickSearch( Session session, String query, DMEntity entity )
        throws IndexException, DataSourceException, ConfigException;

    public SearchResponse quickSearchPojos( Session session, String query, DMEntity entity, int start, int pageSize,
                                            String sortField, String sortDir )
        throws IndexException, DataSourceException, ConfigException;

    /**
     * @param session
     * @param xmlStream
     * @return
     * @throws DataSourceException
     * @throws ConfigException
     * @throws IndexException
     * @throws java.io.IOException
     * @throws javax.xml.parsers.ParserConfigurationException
     *
     * @throws org.xml.sax.SAXException
     */
    public List<org.kimios.kernel.dms.Document> advancedSearch( Session session, String xmlStream, DMEntity entity )
        throws DataSourceException, ConfigException, IndexException, IOException, ParserConfigurationException,
        SAXException;

    public List<Document> advancedSearchPojos( Session session, String xmlStream, DMEntity entity )
        throws DataSourceException, ConfigException, IndexException, IOException, ParserConfigurationException,
        SAXException;


    public SearchResponse advancedSearchDocuments( Session session, List<Criteria> criteriaList,
                                                   int start, int pageSize, String sortField, String sortDir )
            throws DataSourceException, ConfigException, IndexException, IOException, ParseException;


    public void saveSearchQuery( Session session, Long id, String name, List<Criteria> criteriaList, String sortField,
                                 String sortDir )
        throws DataSourceException, ConfigException, IndexException, IOException;

    public void updateSearchQuery( Session session, Long id, String name, List<Criteria> criteriaList, int start,
                                   int pageSize, String sortField, String sortDir )
        throws AccessDeniedException, DataSourceException, ConfigException, IndexException, IOException;

    public void deleteSearchQuery( Session session, Long id )
        throws AccessDeniedException, DataSourceException, ConfigException, IndexException, IOException;

    public SearchRequest loadSearchQuery( Session session, Long id )
        throws AccessDeniedException, DataSourceException, ConfigException, IndexException, IOException;

    public List<SearchRequest> listSavedSearch( Session session )
        throws AccessDeniedException, DataSourceException, ConfigException, IndexException, IOException;

    public SearchResponse executeSearchQuery( Session session, Long id, int start, int pageSize, String sortField,
                                              String sortDir )
            throws AccessDeniedException, DataSourceException, ConfigException, IndexException, IOException, ParseException;


}
