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
package org.kimios.kernel.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.lucene.search.Query;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.controller.IPathController;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.dms.Document;
import org.kimios.kernel.dms.DocumentVersion;
import org.kimios.kernel.dms.FactoryInstantiator;
import org.kimios.kernel.dms.MetaType;
import org.kimios.kernel.dms.MetaValue;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.IndexException;
import org.kimios.kernel.security.DMEntityACL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrIndexManager implements ISolrIndexManager {
    private static Logger log = LoggerFactory.getLogger(SolrIndexManager.class);

    private Reindexer reindexer = null;

    private Thread reindexThread = null;

    private IPathController pathController;

    public SolrIndexManager(SolrServer solr) {
        this.solr = solr;
    }

    private final SolrServer solr;

    public synchronized void reindex(String path) throws DataSourceException, ConfigException, IndexException {
        final String finalPath = path;
        if (this.reindexThread == null || !this.reindexThread.isAlive()) {

            this.reindexer = new Reindexer(this, pathController, finalPath);
            this.reindexThread = new Thread(this.reindexer);
            this.reindexThread.setName("ReindexThread");
            this.reindexThread.start();
        } else {
            throw new IndexException(null, "A reindex process is already running.");
        }
    }

    public int getReindexProgression() {
        if (this.reindexThread != null && this.reindexer != null) {
            return this.reindexer.getReindexProgression();
        } else {
            return -1;
        }
    }

    public boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    public void deleteDocument(Document document) throws IndexException {
        try {
            this.solr.deleteById(String.valueOf(document.getUid()));
            log.debug("Commited deletion of document " + document.getUid());
            this.solr.commit();
        } catch (IOException e) {
            throw new IndexException(e,
                    "An exception occured while deleting document " + document.getUid() + " : " + e.getMessage());
        } catch (SolrServerException e) {
            throw new IndexException(e,
                    "An exception occured while deleting document " + document.getUid() + " : " + e.getMessage());
        }
    }

    private SolrInputDocument toSolrInputDocument(Document document) throws DataSourceException, ConfigException {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("DocumentUid", document.getUid());
        doc.addField("DocumentName", document.getName().toLowerCase());
        doc.addField("DocumentNameAnalysed", document.getName());
        if (document.getExtension() != null) {
            doc.addField("DocumentExtension", document.getExtension().toLowerCase());
        }
        doc.addField("DocumentOwner", document.getOwner() + "@" + document.getOwnerSource());
        doc.addField("DocumentOwnerId", document.getOwner());
        doc.addField("DocumentOwnerSource", document.getOwnerSource());
        doc.addField("DocumentParent", document.getFolder().getPath() + "/");
        DocumentVersion version =
                FactoryInstantiator.getInstance().getDocumentVersionFactory().getLastDocumentVersion(document);


        if (version == null) {
            log.error("Document {} has no version", document.getUid());
            return null;
        }
        //standard datas
        doc.addField("DocumentCreationDate", document.getCreationDate());
        doc.addField("DocumentUpdateDate", document.getUpdateDate());
        doc.addField("DocumentVersionCreationDate", version.getCreationDate());
        doc.addField("DocumentVersionUpdateDate", version.getModificationDate());
        doc.addField("DocumentVersionOwner", version.getAuthor() + "@" + version.getAuthorSource());
        doc.addField("DocumentVersionOwnerId", version.getAuthor());
        doc.addField("DocumentVersionOwnerSource", version.getAuthorSource());
        doc.addField("DocumentVersionLength", version.getLength());
        doc.addField("DocumentVersionHash", version.getHashMD5() + ":" + version.getHashSHA1());

        if (version.getDocumentType() != null) {
            doc.addField("DocumentTypeUid", version.getDocumentType().getUid());
            doc.addField("DocumentTypeName", version.getDocumentType().getName());
            List<MetaValue> values = FactoryInstantiator.getInstance().getMetaValueFactory().getMetaValues(version);
            for (MetaValue value : values) {
                switch (value.getMeta().getMetaType()) {
                    case MetaType.STRING:
                        doc.addField("MetaDataString_" + value.getMetaUid(),
                                ((String) value.getValue()).toLowerCase());
                        break;
                    case MetaType.BOOLEAN:
                        doc.addField("MetaDataBoolean_" + value.getMetaUid(),
                                value.getValue());
                        break;
                    case MetaType.NUMBER:
                        doc.addField("MetaDataNumber_" + value.getMetaUid(),
                                value.getValue());
                        break;
                    case MetaType.DATE:

                        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        cal.setTime((Date) value.getValue());
                        doc.addField("MetaDataDate_" + value.getMetaUid(),
                                cal.getTime());

                        log.info("Inserting date in solr: " + value.getValue() + " / " + cal.getTime() + " / " + cal);
                        break;
                    default:
                        doc.addField("MetaData_" + value.getMetaUid(), value.getValue());
                        break;
                }
            }
        }
        for (String attribute : document.getAttributes().keySet()) {
            doc.addField("Attribute_" + attribute.toUpperCase(),
                    document.getAttributes().get(attribute).getValue());
        }
        Object body = null;
        try {
            IndexFilter filter = FiltersMapper.getInstance().getFiltersFor(document.getExtension());
            log.debug(document.getExtension() + " --> " + (filter != null ? filter.getClass().getName() : "No filter"));
            if (filter != null) {
                body = filter.getBody(version.getInputStream());
                log.debug((String) body);
            }
        } catch (Throwable ex) {
            log.debug("Error while getting body", ex);
        }
        if (body == null) {
            body = IndexHelper.EMPTY_STRING;
        }
        if (body instanceof String) {
            doc.addField("DocumentBody", (String) body);
        }

        List<DMEntityACL> acls =
                org.kimios.kernel.security.FactoryInstantiator.getInstance().getDMEntitySecurityFactory()
                        .getDMEntityACL(document);

        for (int i = 0; i < acls.size(); i++) {
            doc.addField("DocumentACL", acls.get(i).getRuleHash());
        }

        return doc;
    }

    public void indexDocument(DMEntity documentEntity) throws IndexException, DataSourceException, ConfigException {
        try {


            Document document = (Document) documentEntity;
            this.deleteDocument(document);
            SolrInputDocument solrInputDocument = toSolrInputDocument(document);
            this.solr.add(solrInputDocument);
            this.solr.commit();
        } catch (IOException io) {
            throw new IndexException(io,
                    "An exception occured while indexing document " + documentEntity.getUid() + " : " +
                            io.getMessage());
        } catch (SolrServerException ex) {
            throw new IndexException(ex,
                    "An exception occured while indexing document " + documentEntity.getUid() + " : " +
                            ex.getMessage());
        }
    }

    public void indexDocumentList(List<DMEntity> documentEntities)
            throws IndexException, DataSourceException, ConfigException {
        try {
            List<SolrInputDocument> updatedDocument = new ArrayList<SolrInputDocument>();
            List<String> updatedDocumentIds = new ArrayList<String>();
            for (DMEntity doc : documentEntities) {
                SolrInputDocument solrInputDocument = toSolrInputDocument((Document) doc);
                if (solrInputDocument != null) {
                    updatedDocumentIds.add(String.valueOf(doc.getUid()));
                    updatedDocument.add(solrInputDocument);
                    log.info("Doc added to solr Query " + doc + " / " + solrInputDocument);
                }
            }

            this.solr.deleteById(updatedDocumentIds);
            this.solr.add(updatedDocument);
            this.solr.commit();
        } catch (IOException io) {
            throw new IndexException(io,
                    "An exception occured while indexing document list " + io.getMessage());
        } catch (SolrServerException ex) {
            throw new IndexException(ex,
                    "An exception occured while indexing document " + ex.getMessage());
        }
    }

    public List<? extends Number> executeQuery(Query query) throws IndexException {
        QueryResponse rsp;
        try {
            rsp = solr.query(new SolrQuery(query.toString()));
            final List<Long> list = new Vector<Long>();
            SolrDocumentList documentList = rsp.getResults();
            for (SolrDocument dc : documentList) {
                list.add((Long) dc.getFieldValue("DocumentUid"));
            }
            return list;
        } catch (SolrServerException ex) {
            throw new IndexException(ex, ex.getMessage());
        }
    }

    public List<Long> executeSolrQuery(SolrQuery query) throws IndexException {
        QueryResponse rsp;
        try {
            rsp = solr.query(query);
            final List<Long> list = new Vector<Long>();
            SolrDocumentList documentList = rsp.getResults();
            for (SolrDocument dc : documentList) {

                log.debug("Solr result doc: " + dc);

                list.add((Long) dc.getFieldValue("DocumentUid"));
            }
            return list;
        } catch (SolrServerException ex) {
            throw new IndexException(ex, ex.getMessage());
        }
    }

    public void updateAcls(long docUid, List<DMEntityACL> acls) throws IndexException {
        try {
            log.trace("Updating ACL for document #" + docUid);
            QueryResponse rsp = this.solr.query(new SolrQuery("DocumentUid:" + docUid));
            if (rsp.getResults().getNumFound() > 0) {
                SolrDocument doc = rsp.getResults().get(0);
                this.solr.deleteById(String.valueOf(docUid));
                doc.removeFields("DocumentACL");
                SolrInputDocument docUpdate = ClientUtils.toSolrInputDocument(doc);
                for (int j = 0; j < acls.size(); j++) {
                    docUpdate.addField("DocumentACL", acls.get(j).getRuleHash());
                }
                this.solr.add(docUpdate);
            }
        } catch (Exception e) {
            throw new IndexException(e, e.getMessage());
        } finally {
            try {
                this.solr.commit();
            } catch (Exception e) {
                throw new IndexException(e, e.getMessage());
            }
        }
    }

    public void deletePath(String path) throws IndexException {
        try {

            log.debug("Path delete: " + path);
            if (path.endsWith("/")) {
                path = path.substring(0, path.lastIndexOf("/"));
            }
            Query q = new DocumentParentClause(path).getLuceneQuery();
            this.solr.deleteByQuery("DocumentParent:" + path + "/*");
            this.solr.commit();
        } catch (Exception ex) {
            throw new IndexException(ex, ex.getMessage());
        }
    }

    public void updatePath(String oldPath, String newPath) throws IndexException {
        try {
            if (oldPath.endsWith("/")) {
                oldPath = oldPath.substring(0, oldPath.lastIndexOf("/"));
            }
            if (!newPath.endsWith("/")) {
                newPath += "/";
            }
            Query q = new DocumentParentClause(oldPath).getLuceneQuery();
            SolrDocumentList items =
                    this.solr.query(new SolrQuery(q.toString())).getResults();

            if (items.getNumFound() > 0) {
                List<SolrInputDocument> documentList = new ArrayList<SolrInputDocument>();
                this.solr.deleteByQuery(q.toString());
                for (SolrDocument doc : items) {
                    String path = doc.getFieldValue("DocumentParent").toString();
                    path = newPath + path.substring(oldPath.length() + 1);
                    doc.removeFields("DocumentParent");
                    SolrInputDocument updatedDocument = ClientUtils.toSolrInputDocument(doc);
                    updatedDocument.addField("DocumentParent", path);
                    documentList.add(updatedDocument);
                }
                this.solr.add(documentList);
                this.solr.commit();
            }
        } catch (Exception ex) {
            throw new IndexException(ex, ex.getMessage());
        }
    }

    public IPathController getPathController() {
        return pathController;
    }

    public void setPathController(IPathController pathController) {
        this.pathController = pathController;
    }
}
