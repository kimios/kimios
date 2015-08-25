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
package org.kimios.kernel.controller.impl;

import com.google.common.base.Joiner;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.util.DateMathParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.ISearchController;
import org.kimios.kernel.dms.*;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.IndexException;
import org.kimios.kernel.index.ISolrIndexManager;
import org.kimios.kernel.index.query.FacetQueryBuilder;
import org.kimios.kernel.index.query.QueryBuilder;
import org.kimios.kernel.index.query.factory.SearchRequestFactory;
import org.kimios.kernel.index.query.factory.SearchRequestSecurityFactory;
import org.kimios.kernel.index.query.model.*;
import org.kimios.kernel.security.DMSecurityRule;
import org.kimios.kernel.security.SecurityAgent;
import org.kimios.kernel.security.SecurityEntityType;
import org.kimios.kernel.security.Session;
import org.kimios.kernel.user.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Fabien Alin
 * @version 1.0
 */
@Transactional
public class SolrSearchController
        extends AKimiosController
        implements ISearchController {
    private static Logger log = LoggerFactory.getLogger(SolrSearchController.class);

    private ISolrIndexManager solrIndexManager;

    private SearchRequestSecurityFactory searchRequestSecurityFactory;

    private SearchRequestFactory searchRequestFactory;

    public SearchRequestFactory getSearchRequestFactory() {
        return searchRequestFactory;
    }

    public void setSearchRequestFactory(SearchRequestFactory searchRequestFactory) {
        this.searchRequestFactory = searchRequestFactory;
    }

    public ISolrIndexManager getSolrIndexManager() {
        return solrIndexManager;
    }

    public void setSolrIndexManager(ISolrIndexManager solrIndexManager) {
        this.solrIndexManager = solrIndexManager;
    }

    private SearchResponse quickSearchIds(Session session, String query, DMEntity entity, int start, int pageSize,
                                          String sortField, String sortDir) {
        String documentNameQuery = QueryBuilder.documentNameQuery(query);

        SolrQuery indexQuery = new SolrQuery();

        ArrayList<String> filterQueries = new ArrayList<String>();
        if (!getSecurityAgent().isAdmin(session.getUserName(), session.getUserSource())) {
            String aclQuery = QueryBuilder.buildAclQuery(session);
            filterQueries.add(aclQuery);
        }
        if (entity != null) {
            DMEntity entityLoaded = dmsFactoryInstantiator.getDmEntityFactory().getEntity(entity.getUid());
            String pathQuery = "DocumentParent:" + ClientUtils.escapeQueryChars(entityLoaded.getPath() + "/") + "*";
            filterQueries.add(pathQuery);
        }
        indexQuery.setFilterQueries(filterQueries.toArray(new String[]{}));
        if (sortField != null) {
            indexQuery.addSort(sortField,
                    SolrQuery.ORDER.valueOf((sortDir != null ? sortDir.toLowerCase() : "asc")));
        }
        indexQuery.addSort("score", SolrQuery.ORDER.desc);
        indexQuery.setQuery(documentNameQuery);
        if (start > -1 && pageSize > -1) {
            indexQuery.setStart(start);
            indexQuery.setRows(pageSize);
        }

        SearchResponse searchResponse = solrIndexManager.executeSolrQuery(indexQuery);
        return searchResponse;
    }

    public List<Document> quickSearch(Session session, String query, DMEntity entity)
            throws IndexException, DataSourceException, ConfigException {
        return dmsFactoryInstantiator.getDocumentFactory().getDocumentsFromIds(
                quickSearchIds(session, query, entity, -1, -1, null, null).getDocumentIds());
    }

    public SearchResponse quickSearchPojos(Session session, String query, DMEntity entity, int start, int pageSize,
                                           String sortField, String sortDir)
            throws IndexException, DataSourceException, ConfigException {
        SearchResponse searchResponse = quickSearchIds(session, query, entity, start, pageSize, sortField, sortDir);
        return searchResponse;
    }

    public SearchResponse quickSearchPojos(Session session, String query, long dmEntityUid, int start, int pageSize,
                                           String sortField, String sortDir)
            throws IndexException, DataSourceException, ConfigException {
        DMEntity e = dmsFactoryInstantiator.getDmEntityFactory().getEntity(dmEntityUid);
        DMEntity entity = null;
        if (e != null) {
            switch (e.getType()) {
                case DMEntityType.WORKSPACE:
                    entity = new Workspace(dmEntityUid, "", "", "", null);
                    break;
                case DMEntityType.FOLDER:
                    entity = new Folder(dmEntityUid, "", "", "", null, -1, -1);
                    break;
            }
        }
        if (entity != null) {
            return quickSearchPojos(session, query, entity, start, pageSize, sortField, sortDir);
        } else {
            return quickSearchPojos(session, query, null, start, pageSize, sortField, sortDir);
        }
    }

    @Deprecated
    public List<Document> advancedSearch(Session session, String xmlStream, DMEntity entity)
            throws DataSourceException, ConfigException, IndexException, IOException, ParserConfigurationException,
            SAXException {
        SearchResponse searchResponse = this.advancedSearchIds(session, xmlStream, entity, null, null);
        return dmsFactoryInstantiator.getDocumentFactory().getDocumentsFromIds(searchResponse.getDocumentIds());
    }


    @Deprecated
    public List<org.kimios.kernel.ws.pojo.Document> advancedSearchPojos(Session session, String xmlStream,
                                                                        DMEntity entity)
            throws DataSourceException, ConfigException, IndexException, IOException, ParserConfigurationException,
            SAXException {
        SearchResponse searchResponse = this.advancedSearchIds(session, xmlStream, entity, null, null);
        return dmsFactoryInstantiator.getDocumentFactory().getDocumentsPojosFromIds(searchResponse.getDocumentIds());
    }

    @Deprecated
    public List<org.kimios.kernel.ws.pojo.Document> advancedSearchPojos(Session session, String xmlStream,
                                                                        long dmEntityUid)
            throws DataSourceException, ConfigException, IndexException, IOException, ParserConfigurationException,
            SAXException {
        DMEntity e = dmsFactoryInstantiator.getDmEntityFactory().getEntity(dmEntityUid);
        DMEntity entity = null;
        switch (e.getType()) {
            case DMEntityType.WORKSPACE:
                entity = new Workspace(dmEntityUid, "", "", "", null);
                break;
            case DMEntityType.FOLDER:
                entity = new Folder(dmEntityUid, "", "", "", null, -1, -1);
                break;
        }

        SearchResponse searchResponse = this.advancedSearchIds(session, xmlStream, entity, null, null);
        return dmsFactoryInstantiator.getDocumentFactory().getDocumentsPojosFromIds(searchResponse.getDocumentIds());
    }


    @Deprecated
    private SearchResponse advancedSearchIds(Session session, String xmlStream, DMEntity entity, String sortField,
                                             String sortDir)
            throws DataSourceException, ConfigException, IndexException, IOException, ParserConfigurationException,
            SAXException {
        SolrQuery indexQuery = new SolrQuery();

        ArrayList<String> aclFilterQueries = new ArrayList<String>();
        ArrayList<String> filterQueries = new ArrayList<String>();
        if (!getSecurityAgent().isAdmin(session.getUserName(), session.getUserSource())) {
            String aclQuery = QueryBuilder.buildAclQuery(session);
            aclFilterQueries.add(aclQuery);
        }
        if (entity != null) {
            DMEntity entityLoaded = dmsFactoryInstantiator.getDmEntityFactory().getEntity(entity.getUid());
            String pathQuery = "DocumentParent:" + entityLoaded.getPath() + "/*";
            filterQueries.add(pathQuery);
        }
        //Parsing XML stream

        List<String> queries = new ArrayList<String>();
        org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                new java.io.ByteArrayInputStream(xmlStream.getBytes("UTF-8")));
        Element root = doc.getDocumentElement();
        NodeList childNodes = root.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            //Document name query
            if (node.getNodeName().equals("document-name")) {
                if (node.getTextContent() != null && !node.getTextContent().equals("")) {
                    queries.add(QueryBuilder.documentNameQuery(node.getTextContent().toLowerCase()));
                }
            }
            //Document body query
            if (node.getNodeName().equals("text")) {
                if (node.getTextContent() != null && !node.getTextContent().equals("")) {
                    String bodyQuery = ClientUtils.escapeQueryChars(node.getTextContent());
                    queries.add("DocumentBody:" + bodyQuery);
                }
            }
            //Document uid query
            if (node.getNodeName().equals("document-uid")) {
                if (node.getTextContent() != null && !node.getTextContent().equals("")) {
                    filterQueries.add("DocumentUid:" + node.getTextContent());
                }
            }
            //Document type query
            if (node.getNodeName().equals("document-type-uid")) {
                if (node.getTextContent() != null && !node.getTextContent().equals("")) {
                    long dtUid = Long.parseLong(node.getTextContent());
                    List<DocumentType> items =
                            dmsFactoryInstantiator.getDocumentTypeFactory().getChildrenDocumentType(dtUid);
                    List<DocumentType> documentTypeList = new ArrayList<DocumentType>(items);
                    for (DocumentType it : items) {
                        documentTypeList.addAll(
                                dmsFactoryInstantiator.getDocumentTypeFactory().getChildrenDocumentType(it.getUid()));
                    }

                    StringBuilder builder = new StringBuilder();

                    builder.append("DocumentTypeUid:(" + dtUid + (documentTypeList.size() > 0 ? " OR " : ""));
                    int idx = 1;
                    for (DocumentType dtIt : documentTypeList) {
                        builder.append(dtIt.getUid());
                        idx++;
                        if (idx < documentTypeList.size()) {
                            builder.append(" OR ");
                        }
                    }
                    builder.append(")");
                    filterQueries.add(builder.toString());
                }
            }

            //Meta data queries
            if (node.getNodeName().equals("meta-value")) {
                Meta meta = dmsFactoryInstantiator.getMetaFactory().getMeta(
                        Long.parseLong(node.getAttributes().getNamedItem("uid").getTextContent()));
                if (meta != null) {
                    if (meta.getMetaType() == MetaType.STRING) {
                        if (node.getTextContent() != null && !node.getTextContent().equals("")) {

                            String metaStringQuery = "MetaDataString_" + meta.getUid() + ":*" +
                                    ClientUtils.escapeQueryChars(node.getTextContent()) + "*";
                            queries.add(metaStringQuery);
                        }
                    }
                    if (meta.getMetaType() == MetaType.NUMBER) {
                        Double min = null;
                        Double max = null;
                        boolean toAdd = false;
                        if (node.getAttributes().getNamedItem("number-from") != null
                                && !node.getAttributes().getNamedItem("number-from").getTextContent().equals("")) {
                            min = Double.parseDouble(
                                    node.getAttributes().getNamedItem("number-from").getTextContent());
                            toAdd = true;
                        }
                        if (node.getAttributes().getNamedItem("number-to") != null
                                && !node.getAttributes().getNamedItem("number-to").getTextContent().equals("")) {
                            max =
                                    Double.parseDouble(node.getAttributes().getNamedItem("number-to").getTextContent());
                            toAdd = true;
                        }
                        if (toAdd) {
                            String metaNumberQuery =
                                    "MetaDataNumber_" + meta.getUid() + ":[" + (min != null ? min : "*") + " TO " + (
                                            max != null ? max : "*") + "]";

                            queries.add(metaNumberQuery);
                        }
                    }
                    if (meta.getMetaType() == MetaType.DATE) {
                        Date min = null;
                        Date max = null;
                        boolean toAdd = false;
                        if (node.getAttributes().getNamedItem("date-from") != null
                                && !node.getAttributes().getNamedItem("date-from").getTextContent().equals("")) {

                            min = new Date(
                                    Long.parseLong(node.getAttributes().getNamedItem("date-from").getTextContent()));
                            toAdd = true;
                        }
                        if (node.getAttributes().getNamedItem("date-to") != null
                                && !node.getAttributes().getNamedItem("date-to").getTextContent().equals("")) {

                            max = new Date(
                                    Long.parseLong(node.getAttributes().getNamedItem("date-to").getTextContent()));
                            toAdd = true;
                        }
                        if (toAdd) {
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                            df.setTimeZone(TimeZone.getTimeZone("UTC"));
                            String metaDateQuery =
                                    "MetaDataDate_" + meta.getUid() + ":[" + (min != null ? df.format(min) : "*")
                                            + " TO " + (max != null ? df.format(max) : "*") + "]";
                            queries.add(metaDateQuery);
                        }
                    }
                    if (meta.getMetaType() == MetaType.BOOLEAN) {
                        if (node.getAttributes().getNamedItem("boolean-value") != null
                                && !node.getAttributes().getNamedItem("boolean-value").getTextContent().equals("")) {

                            String metaBoolQuery = "MetaDataBoolean_" + meta.getUid() + ":" + Boolean.parseBoolean(
                                    node.getAttributes().getNamedItem("boolean-value").getTextContent());
                            queries.add(metaBoolQuery);
                        }
                    }
                }
            }
        }

        indexQuery.setFilterQueries(aclFilterQueries.toArray(new String[]{}));
        indexQuery.setSort("score", SolrQuery.ORDER.desc);
        StringBuilder sQuery = new StringBuilder();
        for (String q : queries) {

            sQuery.append("+");
            sQuery.append(q);
            sQuery.append(" ");
        }

        if (queries.size() == 0) {
            /*
                Convert filter queries in query, to get result
             */
            for (String q : filterQueries) {

                sQuery.append("+");
                sQuery.append(q);
                sQuery.append(" ");
            }
        }

        log.debug("Solr Final Query: " + sQuery);

        indexQuery.setQuery(sQuery.toString());
        SearchResponse response = solrIndexManager.executeSolrQuery(indexQuery);
        return response;
    }


    public Long advancedSaveSearchQuery(Session session, SearchRequest searchRequest)
            throws DataSourceException, ConfigException, IndexException, IOException {
        searchRequest.setOwner(session.getUserName());
        searchRequest.setOwnerSource(session.getUserSource());
        if (searchRequest.getId() == null) {
            searchRequest.setCreationDate(new Date());
            searchRequest.setUpdateDate(searchRequest.getCreationDate());
        } else {
            searchRequest.setUpdateDate(new Date());
        }
        if(searchRequest.getTemporary() != null && searchRequest.getTemporary()){
            searchRequest.setSearchSessionId(session.getUid());
        }
        searchRequestFactory.save(searchRequest);
        searchRequestFactory.getSession().flush();
        return searchRequest.getId();
    }


    public Long advancedSaveSearchQueryWithSecurity(Session session, SearchRequest searchRequest, List<SearchRequestSecurity> securities)
            throws DataSourceException, ConfigException, IndexException, IOException {

        if (searchRequest.getId() != null) {
            //update mode
            if (!canWrite(session, searchRequest)) {
                throw new AccessDeniedException();
            }
            searchRequest.setUpdateDate(new Date());
        } else {
            searchRequest.setCreationDate(new Date());
            searchRequest.setUpdateDate(searchRequest.getCreationDate());
        }
        searchRequest.setOwner(session.getUserName());
        searchRequest.setOwnerSource(session.getUserSource());

        if(searchRequest.getTemporary() != null && searchRequest.getTemporary()){
            searchRequest.setSearchSessionId(session.getUid());
        }

        searchRequestFactory.save(searchRequest);
        searchRequestFactory.getSession().flush();
        //process security
        searchRequestSecurityFactory.cleanACL(searchRequest);
        if (searchRequest.getSecurities() != null && searchRequest.getSecurities().size() > 0) {
            for (SearchRequestSecurity secItem : securities) {
                secItem.setSearchRequest(searchRequest);
                searchRequestSecurityFactory.saveSearchRequestSecurity(secItem);
                log.debug("saving search request acl " + secItem);
            }
            // Views is supposed to be published, because of rights definition
            searchRequest.setPublished(true);
            searchRequest.setTemporary(false);
            searchRequest.setSearchSessionId(null);

        } else {
            searchRequest.setPublished(false);
            if(searchRequest.getTemporary() != null && searchRequest.getTemporary()){
                searchRequest.setSearchSessionId(session.getUid());
            } else {
                searchRequest.setTemporary(false);
            }
        }
        searchRequestFactory.save(searchRequest);
        searchRequestFactory.getSession().flush();
        return searchRequest.getId();
    }


    public void saveSearchQuery(Session session, Long id, String name, List<Criteria> criteriaList, String sortField,
                                String sortDir)
            throws DataSourceException, ConfigException, IndexException, IOException {
        SearchRequest searchRequest = new SearchRequest();
        if (id != null) {
            searchRequest.setId(id); // update mode
            if (!canWrite(session, searchRequest)) {
                throw new AccessDeniedException();
            }
            searchRequest.setUpdateDate(new Date());
        } else {
            searchRequest.setCreationDate(new Date());
            searchRequest.setUpdateDate(searchRequest.getCreationDate());
        }
        searchRequest.setName(name);
        searchRequest.setCriteriaList(criteriaList);
        searchRequest.setOwner(session.getUserName());
        searchRequest.setOwnerSource(session.getUserSource());
        searchRequest.setSortField(sortField);
        searchRequest.setSortDir(sortDir);
        searchRequest.setPublished(false);
        searchRequest.setTemporary(false);
        searchRequest.setPublicAccess(false);
        searchRequestFactory.save(searchRequest);
    }

    private boolean canWrite(Session session, SearchRequest searchRequest) {
        if (!SecurityAgent.getInstance().isAdmin(session.getUserName(), session.getUserSource())) {
             /*
            filter rights
         */
            List<String> hashs = new ArrayList<String>();
            List<String> noAccessHash = new ArrayList<String>();
            noAccessHash.add(DMSecurityRule
                    .getInstance(session.getUserName(), session.getUserSource(), SecurityEntityType.USER, DMSecurityRule.NOACCESS).getRuleHash());
            hashs.add(
                    DMSecurityRule.getInstance(session.getUserName(), session.getUserSource(), SecurityEntityType.USER, DMSecurityRule.WRITERULE)
                            .getRuleHash());
            hashs.add(DMSecurityRule.getInstance(session.getUserName(), session.getUserSource(), SecurityEntityType.USER, DMSecurityRule.FULLRULE)
                    .getRuleHash());
            for (Group g : session.getGroups()) {
                hashs.add(DMSecurityRule
                        .getInstance(g.getGid(), g.getAuthenticationSourceName(), SecurityEntityType.GROUP,
                                DMSecurityRule.WRITERULE).getRuleHash());
                hashs.add(DMSecurityRule
                        .getInstance(g.getGid(), g.getAuthenticationSourceName(), SecurityEntityType.GROUP,
                                DMSecurityRule.FULLRULE).getRuleHash());
            }

            return searchRequestSecurityFactory.ruleExists(searchRequest, session.getUserName(), session.getUserSource(), hashs, noAccessHash);
        }
        return true;
    }

    private boolean canRead(Session session, SearchRequest searchRequest) {
        if (!SecurityAgent.getInstance().isAdmin(session.getUserName(), session.getUserSource())) {
             /*
                filter rights
             */
            List<String> hashs = new ArrayList<String>();
            List<String> noAccessHash = new ArrayList<String>();
            noAccessHash.add(DMSecurityRule
                    .getInstance(session.getUserName(), session.getUserSource(), SecurityEntityType.USER, DMSecurityRule.NOACCESS).getRuleHash());
            hashs.add(
                    DMSecurityRule.getInstance(session.getUserName(), session.getUserSource(), SecurityEntityType.USER, DMSecurityRule.READRULE)
                            .getRuleHash());
            hashs.add(
                    DMSecurityRule.getInstance(session.getUserName(), session.getUserSource(), SecurityEntityType.USER, DMSecurityRule.WRITERULE)
                            .getRuleHash());
            hashs.add(DMSecurityRule.getInstance(session.getUserName(), session.getUserSource(), SecurityEntityType.USER, DMSecurityRule.FULLRULE)
                    .getRuleHash());
            for (Group g : session.getGroups()) {
                hashs.add(DMSecurityRule
                        .getInstance(g.getGid(), g.getAuthenticationSourceName(), SecurityEntityType.GROUP,
                                DMSecurityRule.READRULE).getRuleHash());
                hashs.add(DMSecurityRule
                        .getInstance(g.getGid(), g.getAuthenticationSourceName(), SecurityEntityType.GROUP,
                                DMSecurityRule.WRITERULE).getRuleHash());
                hashs.add(DMSecurityRule
                        .getInstance(g.getGid(), g.getAuthenticationSourceName(), SecurityEntityType.GROUP,
                                DMSecurityRule.FULLRULE).getRuleHash());
            }

            return searchRequestSecurityFactory.ruleExists(searchRequest, session.getUserName(), session.getUserSource(), hashs, noAccessHash);
        }
        return true;
    }

    public void deleteSearchQuery(Session session, Long id)
            throws AccessDeniedException, DataSourceException, ConfigException, IndexException, IOException {
        SearchRequest searchRequest = searchRequestFactory.loadById(id);


        if (!canWrite(session, searchRequest)) {
            throw new AccessDeniedException();
        }
        searchRequestFactory.deleteSearchRequest(id);
    }

    public SearchRequest loadSearchQuery(Session session, Long id)
            throws AccessDeniedException, DataSourceException, ConfigException, IndexException, IOException {
        SearchRequest searchRequest = searchRequestFactory.loadById(id);

        if ((searchRequest.getPublicAccess() != null && searchRequest.getPublicAccess())
                || canRead(session, searchRequest)
                || (searchRequest.getOwner().equals(session.getUserName())
                && searchRequest.getOwnerSource().equals(session.getUserSource()))) {
            //load securities
            List<SearchRequestSecurity> securities = searchRequestSecurityFactory.getSearchRequestSecurities(searchRequest);
            for (SearchRequestSecurity sec : securities)
                sec.setSearchRequest(null);

            searchRequest.setSecurities(securities);
            return searchRequest;
        } else {
            throw new AccessDeniedException();
        }
    }

    public List<SearchRequest> listSavedSearch(Session session)
            throws AccessDeniedException, DataSourceException, ConfigException, IndexException, IOException {

        try {
            List<SearchRequest> requests = searchRequestFactory.loadAllSearchRequests();
            if (SecurityAgent.getInstance().isAdmin(session.getUserName(), session.getUserSource())) {
                return requests;
            }
        /*
            filter rights
         */
            List<String> hashs = new ArrayList<String>();
            List<String> noAccessHash = new ArrayList<String>();
            noAccessHash.add(DMSecurityRule
                    .getInstance(session.getUserName(), session.getUserSource(), SecurityEntityType.USER, DMSecurityRule.NOACCESS).getRuleHash());
            hashs.add(DMSecurityRule.getInstance(session.getUserName(), session.getUserSource(), SecurityEntityType.USER, DMSecurityRule.READRULE)
                    .getRuleHash());
            hashs.add(
                    DMSecurityRule.getInstance(session.getUserName(), session.getUserSource(), SecurityEntityType.USER, DMSecurityRule.WRITERULE)
                            .getRuleHash());
            hashs.add(DMSecurityRule.getInstance(session.getUserName(), session.getUserSource(), SecurityEntityType.USER, DMSecurityRule.FULLRULE)
                    .getRuleHash());
            for (Group g : session.getGroups()) {
                hashs.add(DMSecurityRule
                        .getInstance(g.getGid(), g.getAuthenticationSourceName(), SecurityEntityType.GROUP,
                                DMSecurityRule.READRULE).getRuleHash());
                hashs.add(DMSecurityRule
                        .getInstance(g.getGid(), g.getAuthenticationSourceName(), SecurityEntityType.GROUP,
                                DMSecurityRule.WRITERULE).getRuleHash());
                hashs.add(DMSecurityRule
                        .getInstance(g.getGid(), g.getAuthenticationSourceName(), SecurityEntityType.GROUP,
                                DMSecurityRule.FULLRULE).getRuleHash());
            }
            List<SearchRequest> readables = searchRequestSecurityFactory
                    .authorizedRequests(requests, session.getUserName(), session.getUserSource(), hashs, noAccessHash);
            return readables;
        } catch (Exception e) {
            log.error("error while loading queries", e);
            throw new AccessDeniedException();
        }
    }


    public List<SearchRequest> searchRequestList(Session session) {
        List<SearchRequest> searchRequestList = searchRequestFactory.loadSearchRequest(session.getUserName(), session.getUid());
        List<SearchRequest> searchRequests = new ArrayList<SearchRequest>();
        for (SearchRequest searchRequest : searchRequestList) {
            if (canRead(session, searchRequest))
                searchRequests.add(searchRequest);
        }
        return searchRequests;
    }


    public List<SearchRequest> searchPublicRequestList(Session session) {
        return searchRequestFactory.listPublicSearchRequest();
    }

    public List<SearchRequest> loadMysSearchQueriesNotPublished(Session session)
            throws DataSourceException, ConfigException {
        return searchRequestFactory.loadMySearchRequestNotPublished(session);
    }

    private String convertDateString(String input) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat targetDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        return targetDateFormat.format(dateFormat.parse(input));
    }

    private String temporarySearchNameUpdate(List<Criteria> criteriaList) {
        try {
            String appendName = "";
            for (Criteria c : criteriaList) {
                boolean isDateField = c.getFieldName().startsWith("MetaDataDate") ||
                        c.getFieldName().equals("DocumentCreationDate") ||
                        c.getFieldName().equals("DocumentUpdateDate") ||
                        c.getFieldName().equals("DocumentVersionCreationDate") ||
                        c.getFieldName().equals("DocumentVersionUpdateDate");
                String tmpAppendName = "";
                if (!c.getFieldName().equals("DocumentTypeUid"))

                    if (c.getQuery() != null) {
                        if (c.getFieldName().contains("Meta") && c.getQuery().contains("[\"")
                                && c.getQuery().contains("\"]")) {
                            //value list handling
                            tmpAppendName += "\"" + c.getQuery().replaceAll("\"\\]", "")
                                    .replaceAll("\\[\"", "")
                                    .replaceAll("\",\"", ",")
                                    .replaceAll("\\[\\]", "");
                            tmpAppendName = tmpAppendName.replaceAll("\"\"", "") + "\",";
                        } else {
                            tmpAppendName += "\"" + c.getQuery() + "\",";
                        }

                    } else if (c.getRangeMin() != null || c.getRangeMax() != null) {
                        if (StringUtils.isNotBlank(c.getRangeMin()) && StringUtils.isNotBlank(c.getRangeMax())) {
                            if (isDateField) {
                                tmpAppendName += convertDateString(c.getRangeMin())
                                        + " to " + convertDateString(c.getRangeMax());
                            } else {
                                tmpAppendName += c.getRangeMin()
                                        + " to " + c.getRangeMax();
                            }
                        } else if (StringUtils.isNotBlank(c.getRangeMin())) {
                            if (isDateField) {
                                tmpAppendName += convertDateString(c.getRangeMin());
                            } else
                                tmpAppendName += c.getRangeMin();
                        } else if (StringUtils.isNotBlank(c.getRangeMax())) {
                            if (isDateField) {
                                tmpAppendName += convertDateString(c.getRangeMax());
                            } else
                                tmpAppendName += c.getRangeMax();
                        }
                    }
                appendName += StringUtils.isNotBlank(tmpAppendName) ? tmpAppendName + "," : "";
            }

            appendName = appendName.replace("\"\"", "");
            appendName = appendName.replace(",,", "");
            appendName = appendName.replace("\"\"", "\",\"");
            if (appendName.trim().endsWith(",")) {
                appendName = appendName.substring(0, appendName.lastIndexOf(","));
            }

            return appendName;

        } catch (Exception ex) {
            return "New Search Request (" + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + ")";

        }
    }


    public SearchResponse advancedSearchDocuments(Session session, List<Criteria> criteriaList, int start,
                                                  int pageSize, String sortField, String sortDir, String virtualPath, Long requestId, Boolean mustSave)
            throws DataSourceException, ConfigException, IndexException, IOException, ParseException {

        SolrQuery query =
                this.parseQueryFromListCriteria(session, start, pageSize, criteriaList, sortField, sortDir, virtualPath, null);
        SearchResponse searchResponse = solrIndexManager.executeSolrQuery(query);

        /*
            Create temporary request
         */
        SearchRequest searchRequest = null;
        if (requestId != null && requestId > 0) {
            searchRequest = searchRequestFactory.loadById(requestId);
            if (searchRequest.getTemporary() != null && searchRequest.getTemporary()) {
                searchRequest.setName(temporarySearchNameUpdate(criteriaList));
                searchRequest.setCriteriaList(criteriaList);
                searchRequest.setUpdateDate(new Date());
                searchRequestFactory.save(searchRequest);
                searchRequest = searchRequestFactory.loadById(requestId);
            }
        } else {
            searchRequest = new SearchRequest();
            String name = temporarySearchNameUpdate(criteriaList);
            searchRequest.setName(name);
            searchRequest.setCreationDate(new Date());
            searchRequest.setUpdateDate(searchRequest.getCreationDate());
            searchRequest.setTemporary(true);
            searchRequest.setPublished(false);
            searchRequest.setPublicAccess(false);
            searchRequest.setCriteriaList(criteriaList);
            searchRequest.setOwner(session.getUserName());
            searchRequest.setOwnerSource(session.getUserSource());
            searchRequest.setSearchSessionId(session.getUid());
            searchRequest.setSortField(sortField);
            searchRequest.setSortDir(sortDir);

            if (mustSave != null && mustSave) {
                Long reqId = searchRequestFactory.save(searchRequest);
                searchRequest.setId(reqId);
            } else {
                searchRequest.setTransient(true);
            }
        }
        searchResponse.setTemporaryRequest(searchRequest);
        return searchResponse;

    }


    public SearchResponse advancedSearchDocuments(Session session, List<Criteria> criteriaList, int start,
                                                  int pageSize, String sortField, String sortDir)
            throws DataSourceException, ConfigException, IndexException, IOException, ParseException {

        return advancedSearchDocuments(session, criteriaList, start, pageSize, sortField, sortDir, null, null, null);
    }

    private SolrQuery parseQueryFromListCriteria(Session session, int page, int pageSize, List<Criteria> criteriaList,
                                                 String sortField, String sortDir)
            throws ParseException {

        return this.parseQueryFromListCriteria(session, page, pageSize, criteriaList, sortField, sortDir, null, null);
    }

    private SolrQuery parseQueryFromListCriteria(Session session, int page, int pageSize, List<Criteria> criteriaList,
                                                 String sortField, String sortDir, String virtualPath, Long savedId)
            throws ParseException {

        SolrQuery indexQuery = new SolrQuery();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        ArrayList<String> aclFilterQueries = new ArrayList<String>();
        ArrayList<String> filterQueries = new ArrayList<String>();
        ArrayList<String> queries = new ArrayList<String>();
        if (!getSecurityAgent().isAdmin(session.getUserName(), session.getUserSource())) {
            String aclQuery = QueryBuilder.buildAclQuery(session);
            aclFilterQueries.add(aclQuery);
        }

        boolean someFacet = false;

        int requiredDepth = -1;
        Map<Integer, String> pathIndex = new HashMap<Integer, String>();
        if (virtualPath != null) {
            if (virtualPath.startsWith("/"))
                virtualPath = virtualPath.substring(1);
            String[] vPaths = virtualPath.split("/");
            for (String p : vPaths) {
                requiredDepth++;
                pathIndex.put(requiredDepth, p);
                log.info("puting depth {} ==> {} ", requiredDepth, p);
            }
        }


        boolean queryHasExclusiveFacetApplied = false;


        Map<Criteria, String> filtersMap = new HashMap<Criteria, String>();

        for (Criteria c : criteriaList) {


            if (StringUtils.isNotBlank(c.getQuery()) && (StringUtils.isBlank(c.getFieldName()) || c.getFieldName().startsWith("__multi"))) {
                String queryValue = null;
                if (c.isRawQuery()) {
                    queryValue = c.getQuery();
                } else {
                    queryValue = ClientUtils.escapeQueryChars(c.getQuery());
                }


                if (c.getFieldName().startsWith("__multi")) {
                    //indexQuery
                    String fieldsName = c.getFieldName().substring("__multi".length());
                    indexQuery.setRequestHandler("search-def");
                    indexQuery.set("qf", fieldsName);
                    queries.add(c.getQuery());
                } else {
                    queries.add(queryValue);
                }

            } else if (c.isFaceted()) {


                log.info(" > Required level " + requiredDepth + ". Path Index Size: " + pathIndex.size() + " " + virtualPath + "  / c: " + c.getLevel() + " => " + c.getFieldName());
                if (c.getLevel() != null && c.getLevel() > -1 && pathIndex.containsKey(c.getLevel())) {
                    /*
                        Gen filter queries
                     */
                    Class fieldType = DmsSolrFields.sortFieldMapping.get(c.getFieldName());
                    if (fieldType == null && c.getFieldName().startsWith("MetaData")) {
                        fieldType = DmsSolrFields.sortMetaFieldMapping.get(c.getFieldName().substring(0, c.getFieldName().lastIndexOf("_")));
                    }

                    log.info("fieldType class {}", fieldType);
                    String fQuery = null;
                    if (fieldType.equals(Date.class)) {
                        String pathDate = pathIndex.get(c.getLevel());
                        fQuery = c.getFieldName() + ":[" + pathDate + "]";
                    } else if (Number.class.isAssignableFrom(fieldType)) {
                        String numberPath = pathIndex.get(c.getLevel());
                        fQuery = c.getFieldName() + ":[" + numberPath + "]";
                    } else if (fieldType.equals(String.class)) {
                        fQuery = c.getFieldName() + ":" + ClientUtils.escapeQueryChars(pathIndex.get(c.getLevel()));
                    }

                    if (fQuery != null) {
                        //should we calculate facet as exclusive ?
                        queryHasExclusiveFacetApplied = c.isExclusiveFacet();
                        filtersMap.put(c, fQuery);
                    }
                } else {
                    try {
                        /*
                            Apply facet if at required level
                         */
                        if (c.getLevel().equals(pathIndex.size())) {
                            log.info("Using criteria for facet: " + c.getFieldName() + " / " + c.getFacetField());
                            Class fieldType = DmsSolrFields.sortFieldMapping.get(c.getFieldName());
                            if (fieldType == null && c.getFieldName().startsWith("MetaData")) {
                                fieldType = DmsSolrFields.sortMetaFieldMapping.get(c.getFieldName().substring(0, c.getFieldName().lastIndexOf("_")));
                            }
                            if (fieldType.equals(Date.class)) {
                                indexQuery =
                                        FacetQueryBuilder.dateFacetBuiler(indexQuery, c.getFieldName(), c.getRangeMin(),
                                                c.getRangeMax(), c.getDateFacetGapType(),
                                                c.getDateFacetGapRange());
                            } else if (Number.class.isAssignableFrom(fieldType)) {
                                indexQuery =
                                        FacetQueryBuilder.numberFacetBuiler(indexQuery, c.getFieldName(), c.getRangeMin(),
                                                c.getRangeMax(), c.getFacetRangeGap());
                            } else if (fieldType.equals(String.class)) {

                                if (c.getFieldName().equals("Attribute_SEARCHTAG") && savedId != null) {
                                    //dirty grouping on search tag regarding current query
                                    // grouping queryId_searchTagName
                                    String searchTagFacetQuery = savedId.toString() + "_" + c.getQuery();
                                    indexQuery = FacetQueryBuilder.searchTagFacetBuilder(indexQuery, c.getFieldName(), searchTagFacetQuery);

                                } else
                                    indexQuery =
                                            FacetQueryBuilder.stringFacetBuilder(indexQuery, c.getFieldName(), c.getQuery());
                            } else {
                                throw new IndexException("UnknownFacetFieldType");
                            }

                            someFacet = c.isFaceted();
                        } else {
                            log.info("Ignoring criteria: " + c.getFieldName() + " / " + c.getFacetField());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (c.getFiltersValues() != null && c.getFiltersValues().size() > 0) {
                for (String e : c.getFiltersValues()) {
                    if (c.isRawQuery()) {
                        filtersMap.put(c, c.getFieldName() + ":" + e);
                        //filterQueries.add(c.getFieldName() + ":" + e);
                    } else {
                        //filterQueries.add(c.getFieldName() + ":" + ClientUtils.escapeQueryChars(e));
                        filtersMap.put(c, c.getFieldName() + ":" + ClientUtils.escapeQueryChars(e));
                    }

                }
            } else if (c.getQuery() != null && c.getQuery().trim().length() > 0 || c.getRangeMin() != null
                    || c.getRangeMax() != null) {

                if (c.isRawQuery()) {
                    // create direct query


                    //reparse date if necessary

                    if (c.getFieldName().contains("Date")) {

                        String finalDateQuery = "";
                        SimpleDateFormat sdfTmp = new SimpleDateFormat(c.getDateFormat());
                        sdfTmp.setTimeZone(TimeZone.getTimeZone("UTC"));

                        SimpleDateFormat solrFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                        solrFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                        if (c.getQuery().toLowerCase().contains(" to ")) {
                            finalDateQuery += "[";
                            String[] tQueryParts = c.getQuery().toLowerCase().split(" to ");
                            Date date1 = null;
                            Date date2 = null;
                            try {
                                date1 = sdfTmp.parse(tQueryParts[0]);
                                finalDateQuery += solrFormat.format(date1);
                            } catch (Exception ex) {
                                finalDateQuery += tQueryParts[0].toUpperCase();
                            }
                            try {
                                date2 = sdfTmp.parse(tQueryParts[1]);
                                finalDateQuery += " TO " + solrFormat.format(date2);
                            } catch (Exception ex) {
                                finalDateQuery += " TO " + tQueryParts[1].toUpperCase();
                            }

                            finalDateQuery += "]";
                        } else {
                            Date date1 = null;
                            try {
                                date1 = sdfTmp.parse(c.getQuery());
                                finalDateQuery += solrFormat.format(date1);
                                //only one date, so add current day

                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(date1.getTime());
                                calendar.add(Calendar.DATE, 1);
                                calendar.add(Calendar.MILLISECOND, -1);
                                String endDate = solrFormat.format(calendar.getTime());
                                finalDateQuery = "[" + finalDateQuery + " TO " + endDate + "]";

                            } catch (Exception ex) {
                                finalDateQuery += c.getQuery();
                            }

                        }
                        log.info("Raw FINAL DATE QUERY {}", finalDateQuery);
                        queries.add((c.getOperator() != null && c.getOperator().length() > 0 ?
                                c.getOperator() : "") + c.getFieldName() + ":" + finalDateQuery);

                    } else {
                        queries.add((c.getOperator() != null && c.getOperator().length() > 0 ?
                                c.getOperator() : "") + c.getFieldName() + ":" + c.getQuery());
                    }


                } else {

                    if (c.getFieldName().equals("DocumentName")) {
                        queries.add(QueryBuilder.documentNameQuery(c.getQuery()));
                    } else if (c.getFieldName().equals("DocumentBody")) {
                        //build query
                        String[] tmpQuery = c.getQuery().split("\\s");
                        StringBuilder bld = new StringBuilder();
                        for (String u : tmpQuery) {
                            bld.append("+");
                            bld.append(ClientUtils.escapeQueryChars(u));
                            bld.append(" ");
                        }
                        queries.add("DocumentBody:(" + bld.toString().trim() + ")");
                    } else if (c.getFieldName().equals("DocumentUid")) {
                        filtersMap.put(c, "DocumentUid:" + c.getQuery());
                    } else if (c.getFieldName().equals("DocumentOwner")) {
                        filtersMap.put(c, "DocumentOwner:*" + c.getQuery() + "*");
                    } else if (c.getFieldName().equals("DocumentParent")) {
                        filtersMap.put(c, QueryBuilder.documentParentQuery(c.getQuery()));
                    } else if (c.getFieldName().equals("DocumentVersionUpdateDate")) {
                        queries.add(
                                QueryBuilder.dateQuery("DocumentVersionUpdateDate", c.getRangeMin(), c.getRangeMax()));
                    } else if (c.getFieldName().equals("DocumentCreationDate")) {
                        queries.add(QueryBuilder.dateQuery("DocumentCreationDate", c.getRangeMin(), c.getRangeMax()));
                    } else if (c.getFieldName().equals("DocumentVersionHash")) {
                        queries.add("DocumentVersionHash:" + ClientUtils.escapeQueryChars(c.getQuery()));
                    } else if (c.getFieldName().equals("DocumentTypeUid")) {
                        long dtUid = Long.parseLong(c.getQuery());
                        List<DocumentType> items =
                                dmsFactoryInstantiator.getDocumentTypeFactory().getChildrenDocumentType(dtUid);
                        List<DocumentType> documentTypeList = new ArrayList<DocumentType>(items);
                        for (DocumentType it : items) {
                            documentTypeList.addAll(
                                    dmsFactoryInstantiator.getDocumentTypeFactory().getChildrenDocumentType(it.getUid()));
                        }

                        StringBuilder builder = new StringBuilder();

                        builder.append("DocumentTypeUid:(" + dtUid + (documentTypeList.size() > 0 ? " OR " : ""));
                        int idx = 0;
                        for (DocumentType dtIt : documentTypeList) {
                            builder.append(dtIt.getUid());
                            idx++;
                            if (idx < documentTypeList.size()) {
                                builder.append(" OR ");
                            }
                        }
                        builder.append(")");
                        //filterQueries.add(builder.toString());
                        filtersMap.put(c, builder.toString());
                    } else if (c.getFieldName().startsWith("MetaData")) {
                        Meta meta = dmsFactoryInstantiator.getMetaFactory().getMeta(c.getMetaId());
                        if (meta != null) {
                            if (meta.getMetaType() == MetaType.STRING) {
                                if (meta.getMetaFeedBean() != null) {
                                    ObjectMapper mapper = new ObjectMapper();
                                    try {
                                        List<String> list = mapper.readValue(c.getQuery(), new TypeReference<List<String>>() {
                                        });
                                        List<String> tmpQ = new ArrayList<String>();
                                        for (String u : list) {
                                            String queryVal = "*" +
                                                ClientUtils.escapeQueryChars(u.toLowerCase()) + "*";
                                            if(!queryVal.equals("**")){
                                                String metaStringQuery = "MetaDataString_" + meta.getUid() + ":" + queryVal;
                                                tmpQ.add(metaStringQuery + " OR ");
                                            }
                                        }
                                        String eq = "";
                                        if (tmpQ.size() > 0) {
                                            for (String z : tmpQ) {
                                                eq += z;
                                            }
                                            log.debug("query multivalue {}", eq);
                                            eq = eq.substring(0, eq.lastIndexOf("OR"));

                                            eq = "(" + eq + ")";
                                            queries.add(eq);
                                        }

                                    } catch (Exception e) {
                                        log.error("error while parsing multivalued meta value", e);
                                        String metaStringQuery = "MetaDataString_" + meta.getUid() + ":*" +
                                                ClientUtils.escapeQueryChars(c.getQuery().toLowerCase()) + "*";
                                        queries.add(metaStringQuery);
                                    }

                                } else {


                                    String metaStringQuery = null;
                                    if (c.getQuery().contains(" ")) {

                                        String[] tmpQuery = c.getQuery().split("\\s");
                                        StringBuilder bld = new StringBuilder();
                                        for (String u : tmpQuery) {
                                            bld.append("+*");
                                            bld.append(ClientUtils.escapeQueryChars(u.toLowerCase()));
                                            bld.append("* ");
                                        }

                                        metaStringQuery = "MetaDataString_" + meta.getUid() + ":("
                                                + bld.toString().trim() + ")";
                                    } else {
                                        metaStringQuery = "MetaDataString_" + meta.getUid() + ":*" +
                                                ClientUtils.escapeQueryChars(c.getQuery().toLowerCase()) + "*";
                                    }
                                    queries.add(metaStringQuery);

                                }


                            }
                            if (meta.getMetaType() == MetaType.LIST) {


                                ObjectMapper mapper = new ObjectMapper();
                                try {
                                    List<String> list = mapper.readValue(c.getQuery(), new TypeReference<List<String>>() {
                                    });


                                    List<String> tmpQ = new ArrayList<String>();
                                    for (String u : list) {
                                        String metaStringQuery = "MetaDataList_" + meta.getUid() + ":*" +
                                                ClientUtils.escapeQueryChars(u.toLowerCase()) + "*";
                                        tmpQ.add(metaStringQuery + " OR ");
                                    }
                                    String eq = "";
                                    if (list.size() > 0) {
                                        for (String z : tmpQ) {
                                            eq += z;
                                        }
                                        log.debug("query multivalue {}", eq);
                                        eq = eq.substring(0, eq.lastIndexOf("OR"));

                                        eq = "(" + eq + ")";
                                        queries.add(eq);
                                    }

                                } catch (Exception e) {
                                    log.error("error while parsing multivalued meta value", e);
                                }


                            }
                            if (meta.getMetaType() == MetaType.NUMBER) {
                                Double min = null;
                                Double max = null;
                                boolean toAdd = false;
                                if (c.getRangeMin() != null && c.getRangeMin().trim().length() > 0) {
                                    min = Double.parseDouble(c.getRangeMin());
                                    toAdd = true;
                                }
                                if (c.getRangeMax() != null && c.getRangeMax().trim().length() > 0) {
                                    max = Double.parseDouble(c.getRangeMax());
                                    toAdd = true;
                                }
                                if (toAdd) {
                                    String metaNumberQuery =
                                            QueryBuilder.numberQuery("MetaDataNumber_" + meta.getUid(), c.getRangeMin(),
                                                    c.getRangeMax());

                                    queries.add(metaNumberQuery);
                                }
                            }
                            if (meta.getMetaType() == MetaType.DATE) {
                                Date min = null;
                                Date max = null;
                                boolean toAdd = false;
                                if (c.getRangeMin() != null && c.getRangeMin().trim().length() > 0) {

                                    try {
                                        min = sdf.parse(c.getRangeMin());
                                        toAdd = true;
                                    } catch (Exception e) {
                                        toAdd = false;
                                    }
                                }
                                if (c.getRangeMax() != null && c.getRangeMax().trim().length() > 0) {
                                    try {
                                        max = sdf.parse(c.getRangeMax());
                                        toAdd = true;
                                    } catch (Exception e) {
                                        toAdd = false;
                                    }
                                }
                                if (toAdd) {

                                    String metaDateQuery =
                                            QueryBuilder.dateQuery("MetaDataDate_" + meta.getUid(), c.getRangeMin(),
                                                    c.getRangeMax());

                                    queries.add(metaDateQuery);
                                }
                            }
                            if (meta.getMetaType() == MetaType.BOOLEAN) {
                                String metaBoolQuery =
                                        "MetaDataBoolean_" + meta.getUid() + ":" + Boolean.parseBoolean(c.getQuery());
                                queries.add(metaBoolQuery);
                            }
                        }
                    }
                }
            }
        }


        for (Criteria c : criteriaList) {
            if (filtersMap.get(c) != null) {
                filterQueries.add(filtersMap.get(c));
            }
        }

        indexQuery.setFilterQueries(filterQueries.toArray(new String[]{}));
        if (sortField != null) {
            indexQuery.setSort(sortField, sortDir != null
                    ? SolrQuery.ORDER.valueOf(sortDir.toLowerCase())
                    : SolrQuery.ORDER.asc);
        }
        if (!indexQuery.getSorts().contains(new SolrQuery.SortClause("score", SolrQuery.ORDER.desc)) &&
                !indexQuery.getSorts().contains(new SolrQuery.SortClause("score", SolrQuery.ORDER.asc))) {
            indexQuery.addSort("score", SolrQuery.ORDER.desc);
        }
        StringBuilder sQuery = new StringBuilder();

        for (String q : queries) {
            //sQuery.append("+");
            sQuery.append(q);
            sQuery.append(" ");
        }

        log.debug("queryHasExclusiveFacetApplied {} someFacet {}", queryHasExclusiveFacetApplied, someFacet);

        if (queryHasExclusiveFacetApplied) {
            sQuery = new StringBuilder();
            sQuery.append("*:*");
            filterQueries.clear();
            for (Criteria c : criteriaList) {
                if (c.isExclusiveFacet() && filtersMap.get(c) != null) {
                    filterQueries.add(filtersMap.get(c));
                }
            }
            filterQueries.addAll(aclFilterQueries);
            indexQuery.setFilterQueries(filterQueries.toArray(new String[]{}));
        } else if (queries.size() == 0) {
            /*
                Convert filter queries in query, to get result
             */
            if (someFacet) {
                sQuery.append("*:*");
                filterQueries.addAll(aclFilterQueries);
                indexQuery.setFilterQueries(filterQueries.toArray(new String[]{}));
            } else {
                for (String q : filterQueries) {
                    sQuery.append("+");
                    sQuery.append(q);
                    sQuery.append(" ");
                }
                filterQueries.clear();
                filterQueries.addAll(aclFilterQueries);
                indexQuery.setFilterQueries(filterQueries.toArray(new String[]{}));
            }


        } else {
            filterQueries.addAll(aclFilterQueries);
            indexQuery.setFilterQueries(filterQueries.toArray(new String[]{}));

        }

        if (indexQuery.getFilterQueries() != null)
            log.debug("Solr Final Filter Query " + Joiner.on(" ").join(indexQuery.getFilterQueries()));
        log.debug("Solr Final Query: " + sQuery);
        if (indexQuery.getFacetFields() != null)
            log.debug("Solr Final Facet  " + Joiner.on(" ").join(indexQuery.getFacetFields()));
        if (indexQuery.getFacetQuery() != null)
            log.debug("Solr Final Facet  Query " + Joiner.on(" ").join(indexQuery.getFacetQuery()));

        indexQuery.setQuery(sQuery.toString());
        if (pageSize > -1 && page > -1) {
            indexQuery.setRows(pageSize);
            indexQuery.setStart(page);
        } else {
            indexQuery.setRows(Integer.MAX_VALUE);
        }


        return indexQuery;

    }


    public SearchResponse executeSearchQuery(Session session, Long id, int start, int pageSize, String sortField,
                                             String sortDir)
            throws AccessDeniedException, DataSourceException, ConfigException, IndexException, IOException, ParseException {
        return this.executeSearchQueryOrBrowse(session, id, start, pageSize, sortField, sortDir, null);
    }


    public SearchResponse executeSearchQueryOrBrowse(Session session, Long id, int start, int pageSize,
                                                     String sortField, String sortDir, String virtualPath)
            throws AccessDeniedException, DataSourceException, ConfigException, IndexException, IOException, ParseException {
        SearchRequest searchRequest = searchRequestFactory.loadById(id);
        if (searchRequest == null
                || !(searchRequest.getPublicAccess()
                || canRead(session, searchRequest))) {
            throw new AccessDeniedException();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        List<Criteria> criteriaList =
                objectMapper.readValue(searchRequest.getCriteriasListJson(), new TypeReference<List<Criteria>>() {
                });
        SolrQuery query =
                this.parseQueryFromListCriteria(session, start, pageSize, criteriaList, sortField, sortDir, virtualPath, id);
        SearchResponse searchResponse = this.solrIndexManager.executeSolrQuery(query);
        return searchResponse;
    }


    @Override
    public List<String> listAvailableFields(Session session) throws AccessDeniedException, IndexException {
        return solrIndexManager.filterFields();
    }


    public SearchRequestSecurityFactory getSearchRequestSecurityFactory() {
        return searchRequestSecurityFactory;
    }

    public void setSearchRequestSecurityFactory(SearchRequestSecurityFactory searchRequestSecurityFactory) {
        this.searchRequestSecurityFactory = searchRequestSecurityFactory;
    }
}