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
package org.kimios.kernel.controller.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.IFolderController;
import org.kimios.kernel.controller.ISearchController;
import org.kimios.kernel.controller.IWorkspaceController;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.dms.DocumentType;
import org.kimios.kernel.dms.Folder;
import org.kimios.kernel.dms.Meta;
import org.kimios.kernel.dms.MetaType;
import org.kimios.kernel.dms.Workspace;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.IndexException;
import org.kimios.kernel.index.DocumentBodyClause;
import org.kimios.kernel.index.DocumentNameClause;
import org.kimios.kernel.index.DocumentParentClause;
import org.kimios.kernel.index.DocumentTypeClause;
import org.kimios.kernel.index.DocumentUidClause;
import org.kimios.kernel.index.IndexHelper;
import org.kimios.kernel.index.IndexManager;
import org.kimios.kernel.index.MetaBooleanValueClause;
import org.kimios.kernel.index.MetaDateValueClause;
import org.kimios.kernel.index.MetaNumberValueClause;
import org.kimios.kernel.index.MetaStringValueClause;
import org.kimios.kernel.index.query.model.Criteria;
import org.kimios.kernel.security.Session;
import org.kimios.kernel.ws.pojo.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Deprecated
public class SearchController extends AKimiosController implements ISearchController
{
    private static Logger log = LoggerFactory.getLogger(SearchController.class);

    IWorkspaceController wksCtrl;

    IFolderController fldCtrl;

    public IWorkspaceController getWksCtrl()
    {
        return wksCtrl;
    }

    public void setWksCtrl(IWorkspaceController wksCtrl)
    {
        this.wksCtrl = wksCtrl;
    }

    public IFolderController getFldCtrl()
    {
        return fldCtrl;
    }

    public void setFldCtrl(IFolderController fldCtrl)
    {
        this.fldCtrl = fldCtrl;
    }

    private List<Long> quickSearchIds(Session session, String query, DMEntity entity)
            throws IndexException, DataSourceException, ConfigException
    {
        final IndexReader reader = IndexManager.getInstance().getIndexReader();
        IndexSearcher searcher = null;
        List<Long> ids = new ArrayList<Long>();
        try {
            searcher = new IndexSearcher(reader);
            BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
            BooleanQuery luceneQuery = new BooleanQuery();

            if (!getSecurityAgent().isAdmin(session.getUserName(), session.getUserSource())) {
                luceneQuery.add(IndexHelper.getACLQuery(session), Occur.MUST);
            }

            boolean fileExtSearch = false;

            fileExtSearch = query.toLowerCase().contains(".");

            if (fileExtSearch) {
                String docName = null;
                String extension = null;
                extension = query.toLowerCase().substring(query.indexOf(".") + 1);
                docName = query.toLowerCase().substring(0, query.indexOf("."));
                luceneQuery.add(new WildcardQuery(new Term("DocumentName", "*" + docName.toLowerCase() + "*")),
                        Occur.MUST);
                luceneQuery.add(new WildcardQuery(new Term("DocumentExtension", extension + "*")), Occur.MUST);
            } else {
                luceneQuery.add(new WildcardQuery(new Term("DocumentName", "*" + query.toLowerCase() + "*")),
                        Occur.MUST);
            }

            //Path query
            if (entity != null) {
                if (entity instanceof Workspace) {
                    entity = wksCtrl
                            .getWorkspace(session, entity.getUid());
                }
                if (entity instanceof Folder) {
                    entity = fldCtrl
                            .getFolder(session, entity.getUid());
                }
                luceneQuery.add(new DocumentParentClause(entity.getPath()).getLuceneQuery(), Occur.MUST);
            }
            log.debug("Query: " + luceneQuery.toString());
            TopDocs topDoc = searcher.search(luceneQuery, Integer.MAX_VALUE);
            log.debug("Result Count: " + topDoc.scoreDocs.length);
            for (ScoreDoc dc : topDoc.scoreDocs) {
                log.trace("Result: " + dc.doc + " - " + dc.score);
                try {
                    org.apache.lucene.document.Document document = reader.document(dc.doc);
                    long dId = Long.parseLong(document.get("DocumentUid"));
                    ids.add(dId);
                } catch (NumberFormatException e) {
                } catch (CorruptIndexException e) {
                } catch (IOException e) {
                }
            }
        } catch (IOException io) {
            throw new IndexException(io, io.getMessage());
        } catch (Exception e) {
            throw new IndexException(e, e.getMessage());
        } finally {
            try {
                if (searcher != null) {
                    searcher.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                throw new IndexException(e, e.getMessage());
            }
        }
        return ids;
    }

    public List<Document> quickSearchPojos(Session session, String query, DMEntity entity)
            throws IndexException, DataSourceException, ConfigException
    {
        List<Long> docs = this.quickSearchIds(session, query, entity);
        return dmsFactoryInstantiator.getDocumentFactory()
                .getDocumentsPojosFromIds(docs);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISearchController#advancedSearch(org.kimios.kernel.security.Session, java.lang.String, org.kimios.kernel.dms.DMEntity)
    */

    public List<org.kimios.kernel.dms.Document> quickSearch(Session session, String query,
            DMEntity entity) throws IndexException, DataSourceException,
            ConfigException
    {
        List<Long> docs = this.quickSearchIds(session, query, entity);
        return dmsFactoryInstantiator.getDocumentFactory().getDocumentsFromIds(docs);
    }

    public List<org.kimios.kernel.dms.Document> advancedSearch(Session session, String xmlStream,
            DMEntity entity) throws DataSourceException, ConfigException,
            IndexException, IOException, ParserConfigurationException,
            SAXException
    {
        List<Long> docs = this.advancedSearchIds(session, xmlStream, entity);
        return dmsFactoryInstantiator.getDocumentFactory().getDocumentsFromIds(docs);
    }

    private List<Long> advancedSearchIds(Session session, String xmlStream, DMEntity entity)
            throws DataSourceException, ConfigException, IndexException, IOException, ParserConfigurationException,
            SAXException
    {
        Vector<Query> queries = new Vector<Query>();
        //Constructing queries
        try {
            //Path query
            if (entity != null) {
                if (entity instanceof Workspace) {
                    entity = wksCtrl
                            .getWorkspace(session, entity.getUid());
                }
                if (entity instanceof Folder) {
                    entity = fldCtrl
                            .getFolder(session, entity.getUid());
                }
                queries.add(new DocumentParentClause(entity.getPath()).getLuceneQuery());
            }
        } catch (AccessDeniedException ade) {
            throw new IndexException(ade, ade.getMessage());
        }
        //ACL query
        if (!getSecurityAgent().isAdmin(session.getUserName(), session.getUserSource())) {
            queries.add(IndexHelper.getACLQuery(session));
        }

        //Parsing XML stream
        org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new java.io.ByteArrayInputStream(xmlStream.getBytes("UTF-8")));
        Element root = doc.getDocumentElement();
        NodeList childNodes = root.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            //Document name query
            if (node.getNodeName().equals("document-name")) {
                if (node.getTextContent() != null && !node.getTextContent().equals("")) {
                    queries.add(new DocumentNameClause(node.getTextContent().toLowerCase()).getLuceneQuery());
                }
            }
            //Document body query
            if (node.getNodeName().equals("text")) {
                if (node.getTextContent() != null && !node.getTextContent().equals("")) {
                    Query bodyQuery = new DocumentBodyClause(node.getTextContent()).getLuceneQuery();
                    if (bodyQuery instanceof BooleanQuery) {
                        for (BooleanClause bClause : ((BooleanQuery) bodyQuery).clauses()) {
                            bClause.setOccur(Occur.MUST);
                        }
                    }
                    queries.add(bodyQuery);
                }
            }
            //Document uid query
            if (node.getNodeName().equals("document-uid")) {
                if (node.getTextContent() != null && !node.getTextContent().equals("")) {
                    queries.add(new DocumentUidClause(Long.parseLong(node.getTextContent())).getLuceneQuery());
                }
            }
            //Document type query
            if (node.getNodeName().equals("document-type-uid")) {
                if (node.getTextContent() != null && !node.getTextContent().equals("")) {
                    long dtUid = Long.parseLong(node.getTextContent());
                    BooleanQuery dtQuery = new BooleanQuery();
                    dtQuery.add(new DocumentTypeClause(dtUid).getLuceneQuery(), Occur.SHOULD);
                    List<DocumentType> documentTypeList = loadDocumentTypeTree(dtUid);
                    for (DocumentType dtIt : documentTypeList) {
                        dtQuery.add(new DocumentTypeClause(dtIt.getUid()).getLuceneQuery(), Occur.SHOULD);
                    }
                    queries.add(dtQuery);
                }
            }

            //Meta data queries
            if (node.getNodeName().equals("meta-value")) {
                Meta meta = dmsFactoryInstantiator.getMetaFactory()
                        .getMeta(Long.parseLong(node.getAttributes().getNamedItem("uid").getTextContent()));
                if (meta != null) {
                    if (meta.getMetaType() == MetaType.STRING) {
                        if (node.getTextContent() != null && !node.getTextContent().equals("")) {
                            queries.add(new MetaStringValueClause(meta.getUid(), node.getTextContent().toLowerCase())
                                    .getLuceneQuery());
                        }
                    }
                    if (meta.getMetaType() == MetaType.NUMBER) {
                        double min = MetaNumberValueClause.MIN;
                        double max = MetaNumberValueClause.MAX;
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
                            queries.add(new MetaNumberValueClause(meta.getUid(), min, max).getLuceneQuery());
                        }
                    }
                    if (meta.getMetaType() == MetaType.DATE) {
                        Date min = MetaDateValueClause.MIN;
                        Date max = MetaDateValueClause.MAX;
                        boolean toAdd = false;
                        if (node.getAttributes().getNamedItem("date-from") != null &&
                                !node.getAttributes().getNamedItem("date-from").getTextContent().equals(""))
                        {
                            min = new Date(
                                    Long.parseLong(node.getAttributes().getNamedItem("date-from").getTextContent()));
                            toAdd = true;
                        }
                        if (node.getAttributes().getNamedItem("date-to") != null &&
                                !node.getAttributes().getNamedItem("date-to").getTextContent().equals(""))
                        {
                            max = new Date(
                                    Long.parseLong(node.getAttributes().getNamedItem("date-to").getTextContent()));
                            toAdd = true;
                        }
                        if (toAdd) {
                            queries.add(new MetaDateValueClause(meta.getUid(), min, max).getLuceneQuery());
                        }
                    }
                    if (meta.getMetaType() == MetaType.BOOLEAN) {
                        if (node.getAttributes().getNamedItem("boolean-value") != null &&
                                !node.getAttributes().getNamedItem("boolean-value").getTextContent().equals(""))
                        {
                            queries.add(new MetaBooleanValueClause(meta.getUid(), Boolean.parseBoolean(
                                    node.getAttributes().getNamedItem("boolean-value").getTextContent()))
                                    .getLuceneQuery());
                        }
                    }
                }
            }
        }

        //Merging queries
        BooleanQuery luceneQuery = new BooleanQuery();
        BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
        for (Query query : queries) {
            luceneQuery.add(query, Occur.MUST);
        }
        //Lauching Lucene search
        final IndexReader reader = IndexManager.getInstance().getIndexReader();
        IndexSearcher searcher = new IndexSearcher(reader);
        List<Long> ids = new ArrayList<Long>();
        log.debug("Query: " + luceneQuery.toString());
        try {
            TopDocs topDoc = searcher.search(luceneQuery, Integer.MAX_VALUE);
            log.debug("Result Count: " + topDoc.scoreDocs.length);
            for (ScoreDoc it : topDoc.scoreDocs) {
                log.trace("Lucene Result: " + it.doc + " - " + it.score);
                try {
                    org.apache.lucene.document.Document document = reader.document(it.doc);
                    long dId = Long.parseLong(document.get("DocumentUid"));
                    ids.add(dId);
                } catch (NumberFormatException e) {
                } catch (CorruptIndexException e) {
                } catch (IOException e) {
                }
            }
        } catch (IOException io) {
            throw new IndexException(io, io.getMessage());
        } finally {
            if (searcher != null) {
                searcher.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
        return ids;
    }

    public List<Document> advancedSearchPojos(Session session, String xmlStream,
            DMEntity entity)
            throws DataSourceException, ConfigException, IndexException, IOException, ParserConfigurationException,
            SAXException
    {
        List<Long> docs = this.advancedSearchIds(session, xmlStream, entity);
        return dmsFactoryInstantiator.getDocumentFactory().getDocumentsPojosFromIds(docs);
    }

    private List<DocumentType> loadDocumentTypeTree(long dtId) throws ConfigException, DataSourceException
    {
        List<DocumentType> items = dmsFactoryInstantiator.getDocumentTypeFactory().getChildrenDocumentType(dtId);
        List<DocumentType> finalList = new ArrayList<DocumentType>(items);
        for (DocumentType it : items) {
            finalList.addAll(dmsFactoryInstantiator.getDocumentTypeFactory().getChildrenDocumentType(it.getUid()));
        }
        return finalList;
    }


    public List<Document> advancedSearchDocuments( Session session, int page, int pageSize, List<Criteria> criteriaList,
                                                   DMEntity entity )
        throws DataSourceException, ConfigException, IndexException, IOException
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}

