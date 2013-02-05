/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2012  DevLib'
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
package org.kimios.kernel.controller.impl;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.ISearchController;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.dms.Document;
import org.kimios.kernel.dms.DocumentType;
import org.kimios.kernel.dms.Meta;
import org.kimios.kernel.dms.MetaType;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.IndexException;
import org.kimios.kernel.index.ISolrIndexManager;
import org.kimios.kernel.index.query.QueryBuilder;
import org.kimios.kernel.security.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 */
public class SolrSearchController extends AKimiosController implements ISearchController
{
    private static Logger log = LoggerFactory.getLogger(SolrSearchController.class);

    private ISolrIndexManager solrIndexManager;

    public ISolrIndexManager getSolrIndexManager()
    {
        return solrIndexManager;
    }

    public void setSolrIndexManager(ISolrIndexManager solrIndexManager)
    {
        this.solrIndexManager = solrIndexManager;
    }

    private List<Long> quickSearchIds(Session session, String query, DMEntity entity)
    {
        String documentNameQuery = QueryBuilder.documentNameQuery(query);

        SolrQuery indexQuery = new SolrQuery();

        ArrayList<String> filterQueries = new ArrayList<String>();
        if (!getSecurityAgent().isAdmin(session.getUserName(), session.getUserSource())) {
            String aclQuery = QueryBuilder.buildAclQuery(session);
            filterQueries.add(aclQuery);
        }
        if (entity != null) {
            DMEntity entityLoaded = dmsFactoryInstantiator.getDmEntityFactory().getEntity(entity.getUid());
            String pathQuery = "DocumentParent:" + entityLoaded.getPath() + "/*";
            filterQueries.add(pathQuery);
        }
        indexQuery.setFilterQueries(filterQueries.toArray(new String[]{ }));
        indexQuery.addSortField("score", SolrQuery.ORDER.desc);
        indexQuery.setQuery(documentNameQuery);

        List<Long> items = solrIndexManager.executeSolrQuery(indexQuery);

        return items;
    }

    public List<Document> quickSearch(Session session, String query, DMEntity entity)
            throws IndexException, DataSourceException, ConfigException
    {
        return dmsFactoryInstantiator.getDocumentFactory()
                .getDocumentsFromIds(quickSearchIds(session, query, entity));
    }

    public List<org.kimios.kernel.ws.pojo.Document> quickSearchPojos(Session session, String query, DMEntity entity)
            throws IndexException, DataSourceException, ConfigException
    {
        return dmsFactoryInstantiator.getDocumentFactory()
                .getDocumentsPojosFromIds(quickSearchIds(session, query, entity));
    }

    public List<Document> advancedSearch(Session session, String xmlStream, DMEntity entity)
            throws DataSourceException, ConfigException, IndexException, IOException, ParserConfigurationException,
            SAXException
    {
        List<Long> docs = this.advancedSearchIds(session, xmlStream, entity);
        return dmsFactoryInstantiator.getDocumentFactory().getDocumentsFromIds(docs);
    }

    public List<org.kimios.kernel.ws.pojo.Document> advancedSearchPojos(Session session, String xmlStream,
            DMEntity entity)
            throws DataSourceException, ConfigException, IndexException, IOException, ParserConfigurationException,
            SAXException
    {
        List<Long> docs = this.advancedSearchIds(session, xmlStream, entity);
        return dmsFactoryInstantiator.getDocumentFactory().getDocumentsPojosFromIds(docs);
    }

    private List<Long> advancedSearchIds(Session session, String xmlStream, DMEntity entity)
            throws DataSourceException, ConfigException, IndexException, IOException, ParserConfigurationException,
            SAXException
    {
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
        org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new java.io.ByteArrayInputStream(xmlStream.getBytes("UTF-8")));
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
                        documentTypeList.addAll(dmsFactoryInstantiator.getDocumentTypeFactory()
                                .getChildrenDocumentType(it.getUid()));
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
                Meta meta = dmsFactoryInstantiator.getMetaFactory()
                        .getMeta(Long.parseLong(node.getAttributes().getNamedItem("uid").getTextContent()));
                if (meta != null) {
                    if (meta.getMetaType() == MetaType.STRING) {
                        if (node.getTextContent() != null && !node.getTextContent().equals("")) {

                            String metaStringQuery =
                                    "MetaDataString_" + meta.getUid() + ":*" +
                                            ClientUtils.escapeQueryChars(node.getTextContent()) + "*";
                            queries.add(metaStringQuery);
                        }
                    }
                    if (meta.getMetaType() == MetaType.NUMBER) {
                        Double min = null;
                        Double max = null;
                        boolean toAdd = false;
                        if (node.getAttributes().getNamedItem("number-from") != null &&
                                !node.getAttributes().getNamedItem("number-from").getTextContent().equals(""))
                        {
                            min = Double.parseDouble(node.getAttributes().getNamedItem("number-from").getTextContent());
                            toAdd = true;
                        }
                        if (node.getAttributes().getNamedItem("number-to") != null &&
                                !node.getAttributes().getNamedItem("number-to").getTextContent().equals(""))
                        {
                            max = Double.parseDouble(node.getAttributes().getNamedItem("number-to").getTextContent());
                            toAdd = true;
                        }
                        if (toAdd) {
                            String metaNumberQuery =
                                    "MetaDataNumber_" + meta.getUid() + ":[" + (min != null ? min : "*")
                                            + " TO " + (max != null ? max : "*") + "]";

                            queries.add(metaNumberQuery);
                        }
                    }
                    if (meta.getMetaType() == MetaType.DATE) {
                        Date min = null;
                        Date max = null;
                        boolean toAdd = false;
                        if (node.getAttributes().getNamedItem("date-from") != null &&
                                !node.getAttributes().getNamedItem("date-from").getTextContent().equals(""))
                        {

                            min = new Date(
                                    Long.parseLong(node.getAttributes().getNamedItem("date-from").getTextContent()));

                            log.debug(node.getAttributes().getNamedItem("date-from").getTextContent() + " --> " + min);
                            toAdd = true;
                        }
                        if (node.getAttributes().getNamedItem("date-to") != null &&
                                !node.getAttributes().getNamedItem("date-to").getTextContent().equals(""))
                        {

                            max = new Date(
                                    Long.parseLong(node.getAttributes().getNamedItem("date-to").getTextContent()));
                            log.debug(node.getAttributes().getNamedItem("date-from").getTextContent() + " --> " + max);
                            toAdd = true;
                        }
                        if (toAdd) {

                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                            String metaDateQuery =
                                    "MetaDataDate_" + meta.getUid() + ":[" + (min != null ? df.format(min) : "*")
                                            + " TO " + (max != null ? df.format(max) : "*") + "]";
                            queries.add(metaDateQuery);
                        }
                    }
                    if (meta.getMetaType() == MetaType.BOOLEAN) {
                        if (node.getAttributes().getNamedItem("boolean-value") != null &&
                                !node.getAttributes().getNamedItem("boolean-value").getTextContent().equals(""))
                        {

                            String metaBoolQuery =
                                    "MetaDataBoolean_" + meta.getUid() + ":" + Boolean.parseBoolean(
                                            node.getAttributes().getNamedItem("boolean-value").getTextContent());
                            queries.add(metaBoolQuery);
                        }
                    }
                }
            }
        }

        indexQuery.setFilterQueries(aclFilterQueries.toArray(new String[]{ }));
        indexQuery.addSortField("score", SolrQuery.ORDER.desc);
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
        List<Long> items = solrIndexManager.executeSolrQuery(indexQuery);

        return items;
    }
}
