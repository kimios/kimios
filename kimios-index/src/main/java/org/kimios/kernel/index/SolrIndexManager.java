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
package org.kimios.kernel.index;

import org.apache.lucene.search.Query;
import org.apache.solr.client.solrj.*;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.controller.IPathController;
import org.kimios.kernel.dms.*;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.dms.Document;
import org.kimios.kernel.dms.DocumentVersion;
import org.kimios.kernel.dms.DocumentWorkflowStatusRequest;
import org.kimios.kernel.dms.Meta;
import org.kimios.kernel.dms.MetaValue;
import org.kimios.kernel.dms.WorkflowStatus;
import org.kimios.kernel.events.impl.AddonDataHandler;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.IndexException;
import org.kimios.kernel.index.filters.impl.GlobalFilter;
import org.kimios.kernel.index.filters.impl.ThreadedGlobalFilter;
import org.kimios.kernel.index.query.factory.DocumentFactory;
import org.kimios.kernel.index.query.factory.DocumentIndexStatusFactory;
import org.kimios.kernel.index.query.model.DocumentIndexStatus;
import org.kimios.kernel.index.query.model.SearchResponse;
import org.kimios.kernel.security.DMEntityACL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;


public class SolrIndexManager
        implements ISolrIndexManager {
    private static Logger log = LoggerFactory.getLogger(SolrIndexManager.class);

    private Reindexer reindexer = null;

    private Thread reindexThread = null;

    private IPathController pathController;

    private int fileReadThreadPoolSize = 10;

    private org.kimios.kernel.index.query.factory.DocumentFactory solrDocumentFactory;

    public DocumentFactory getSolrDocumentFactory() {
        return solrDocumentFactory;
    }



    private ExecutorService fileReaderExecutor;

    public void setSolrDocumentFactory(DocumentFactory solrDocumentFactory) {
        this.solrDocumentFactory = solrDocumentFactory;
    }


    private DocumentIndexStatusFactory documentIndexStatusFactory;

    public void setDocumentIndexStatusFactory(DocumentIndexStatusFactory documentIndexStatusFactory) {
        this.documentIndexStatusFactory = documentIndexStatusFactory;
    }

    private ObjectMapper mp;

    public SolrIndexManager(SolrServer solr) {
        this.solr = solr;
        mp = new ObjectMapper();
        mp.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        mp.getSerializationConfig().addMixInAnnotations(Meta.class, AddonDataHandler.MetaMixIn.class);
    }

    private final SolrServer solr;

    public synchronized void reindex(String path)
            throws DataSourceException, ConfigException, IndexException {
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

    public void deleteDocument(DMEntity document)
            throws IndexException {
        try {
            this.solr.deleteById(String.valueOf(document.getUid()));
            log.debug("Commited deletion of document " + document.getUid());
            this.solr.commit();
        } catch (IOException e) {
            throw new IndexException(e, "An exception occured while deleting document " + document.getUid() + " : "
                    + e.getMessage());
        } catch (SolrServerException e) {
            throw new IndexException(e, "An exception occured while deleting document " + document.getUid() + " : "
                    + e.getMessage());
        }
    }

    protected Map<String, Object> readVersionFileToData(Document document, DocumentVersion version,
                                                DocumentIndexStatus documentIndexStatus){
        Object body = IndexHelper.EMPTY_STRING;;
        Map<String, Object> metaDatas = null;
        try {
            GlobalFilter globalFilter = new GlobalFilter();
            body = globalFilter.getFileBody(document, version.getInputStream());
            metaDatas = globalFilter.getMetaDatas();
        } catch (Throwable ex) {
            log.debug("Error while getting body", ex);
            documentIndexStatus.setBodyIndexed(false);
            StringWriter stringWriterStackTrace = new StringWriter();
            PrintWriter prw = new PrintWriter(stringWriterStackTrace);
            ex.printStackTrace(prw);
            documentIndexStatus.setError(ex.getMessage() + " ==> "
                    + stringWriterStackTrace);
        }
        if(metaDatas == null){
            metaDatas = new HashMap<String, Object>();
        } else {
            for (String mKey : metaDatas.keySet()) {
                metaDatas.put("FileMetaData_" + mKey, metaDatas.get(mKey));
            }
        }
        if (body instanceof String) {
            metaDatas.put("DocumentBody", body);
            documentIndexStatus.setBodyIndexed(true);
        }
        return metaDatas;
    }

    protected SolrInputDocument toSolrInputDocument(Folder folder, List<VirtualFolderMetaData> metaValues)
            throws DataSourceException, ConfigException {

        SimpleDateFormat dateParser = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat dateTimeParser = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat utcDateParser = new SimpleDateFormat("dd-MM-yyyy");
        utcDateParser.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat utcDateTimeParser = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        utcDateTimeParser.setTimeZone(TimeZone.getTimeZone("UTC"));
        log.debug("processing folder {} for path {}", folder.getUid(), folder.getPath());
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("DocumentUid", folder.getUid());
        doc.addField("DocumentName", folder.getName().toLowerCase());
        doc.addField("DocumentNameDisplayed", folder.getName());
        doc.addField("DocumentNameAnalysed", folder.getName());

        doc.addField("DocumentOwner", folder.getOwner() + "@" + folder.getOwnerSource());
        doc.addField("DocumentOwnerId", folder.getOwner());
        doc.addField("DocumentOwnerSource", folder.getOwnerSource());
        doc.addField("DocumentPath", folder.getPath());
        doc.addField("DocumentParent", folder.getParent().getPath() + "/");
        doc.addField("DocumentParentId", folder.getParent().getUid());

        //standard datas
        doc.addField("DocumentCreationDate", folder.getCreationDate());
        doc.addField("DocumentUpdateDate", folder.getUpdateDate());
        doc.addField("DocumentVersionId", -1);
        doc.addField("DocumentVersionCreationDate", folder.getCreationDate());
        doc.addField("DocumentVersionUpdateDate", folder.getUpdateDate());
        doc.addField("DocumentVersionOwner", folder.getOwner() + "@" + folder.getOwnerSource());
        doc.addField("DocumentVersionOwnerId", folder.getOwner());
        doc.addField("DocumentVersionOwnerSource", folder.getOwnerSource());
        doc.addField("DocumentVersionLength", -10);
        doc.addField("DocumentVersionHash", "folder");
        doc.addField("DocumentOutWorkflow", true);
        List<MetaValue> values = null;
        if (metaValues.size() > 0) {
            log.debug("Document Type Found for version");


            String documentTypeName = null;
            Long documentTypeUid = null;
            for (VirtualFolderMetaData folderMetaData : metaValues) {


                if (documentTypeName == null && documentTypeUid == null) {
                    documentTypeName = folderMetaData.getMeta().getDocumentType().getName();
                    documentTypeUid = folderMetaData.getMeta().getDocumentTypeUid();
                    doc.addField("DocumentTypeUid", documentTypeUid);
                    doc.addField("DocumentTypeName", documentTypeName);
                }
                switch (folderMetaData.getMeta().getMetaType()) {

                    case MetaType.STRING:
                        doc.addField("MetaDataString_" + folderMetaData.getMetaId(),
                                folderMetaData.getStringValue());
                        break;
                    /*case MetaType.BOOLEAN:
                        doc.addField( "MetaDataBoolean_" + value.getMetaUid(), value.getValue() );
                        break;
                    case MetaType.NUMBER:
                        doc.addField( "MetaDataNumber_" + value.getMetaUid(), value.getValue() );
                        break;*/
                    case MetaType.DATE:

                        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        if (folderMetaData.getDateValue() != null) {
                            //reparse date for local
                            String dateString = dateParser.format(folderMetaData.getDateValue());
                            log.debug("meta parsed on re-index: " + dateString);
                            try {
                                cal.setTime(utcDateParser.parse(dateString));
                                log.debug("meta parsed on re-index: " + dateString + " ==> Cal:" + cal.getTime());
                                doc.addField("MetaDataDate_" + folderMetaData.getMetaId(), cal.getTime());
                            } catch (Exception ex) {
                                log.error("error while reparsing meta data date {} {} {}", folderMetaData.getMeta().getName(), folderMetaData.getDateValue(), dateString);
                            }
                        }
                        break;
                    /*case MetaType.LIST:
                        if(value != null && value.getValue() != null){
                            List<String> items = ((MetaListValue)value).getValue();
                            for(String u: items)
                                doc.addField( "MetaDataList_" + value.getMetaUid(),u);
                        }
                        break;*/
                    default:
                        doc.addField("MetaData_" + folderMetaData.getMetaId(), folderMetaData.getStringValue());
                        break;
                }
            }
        }
        doc.addField("Attribute_VirtualFolder", "folder");

        if (folder.getAddOnDatas() != null && folder.getAddOnDatas().length() > 0) {
            doc.addField("DocumentRawAddonDatas", folder.getAddOnDatas());
            log.debug("adding current addon data {}", folder.getAddOnDatas());
        } else {
            //try to regenerate field

            if ((values != null && values.size() > 0) || (folder.getAttributes() != null || folder.getAttributes().size() > 0)) {
                AddonDataHandler.AddonDatasWrapper wrapper = new AddonDataHandler.AddonDatasWrapper();
                wrapper.setEntityAttributes(folder.getAttributes());
                wrapper.setEntityMetaValues(values);
                try {
                    folder.setAddOnDatas(mp.writeValueAsString(wrapper));
                    FactoryInstantiator.getInstance().getFolderFactory().saveFolder(folder);
                } catch (Exception ex) {
                    log.error("error while generation addon meta field", ex);
                }
                //update document
                doc.addField("DocumentRawAddonDatas", folder.getAddOnDatas());
            } else {
                log.debug("not generating addon field because of no data");
            }
        }

        Object body = null;
        Map<String, Object> metaDatas = null;
        doc.addField("DocumentBody", IndexHelper.EMPTY_STRING);
        List<DMEntityACL> acls =
                org.kimios.kernel.security.FactoryInstantiator.getInstance().getDMEntitySecurityFactory().getDMEntityACL(
                        folder);
        for (int i = 0; i < acls.size(); i++) {
            doc.addField("DocumentACL", acls.get(i).getRuleHash());
        }

        return doc;
    }


    public void indexDocument(DMEntity documentEntity)
            throws IndexException, DataSourceException, ConfigException {
        try {

            Document document = (Document) documentEntity;
            String docQuery = "DocumentUid:" + documentEntity.getUid();
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery(docQuery);
            this.deleteDocument(document);
            //load data
            DocumentVersion version =
                    FactoryInstantiator.getInstance().getDocumentVersionFactory().getLastDocumentVersion(document);
            DocumentIndexStatus documentIndexStatus = new DocumentIndexStatus();
            Map<String, Object> fileData = readVersionFileToData(document, version, documentIndexStatus);
            SolrInputDocument solrInputDocument =
                    new SolrDocGenerator(document, mp).toSolrInputDocument(true, true, fileData, documentIndexStatus);
            this.solr.add(solrInputDocument);
            this.solr.commit();
        } catch (IOException io) {
            throw new IndexException(io,
                    "An exception occured while indexing document " + documentEntity.getUid() + " : "
                            +
                            io.getMessage());
        } catch (SolrServerException ex) {
            throw new IndexException(ex,
                    "An exception occured while indexing document " + documentEntity.getUid() + " : "
                            +
                            ex.getMessage());
        }
    }


    public void indexFolder(DMEntity documentEntity, List<VirtualFolderMetaData> metaValues)
            throws IndexException, DataSourceException, ConfigException {
        try {

            Folder folder = (Folder) documentEntity;
            this.deleteDocument(folder);
            SolrInputDocument solrInputDocument = toSolrInputDocument(folder, metaValues);
            this.solr.add(solrInputDocument);
            this.solr.commit();
        } catch (IOException io) {
            throw new IndexException(io,
                    "An exception occured while indexing folder " + documentEntity.getUid() + " : "
                            +
                            io.getMessage());
        } catch (SolrServerException ex) {
            throw new IndexException(ex,
                    "An exception occured while indexing folder " + documentEntity.getUid() + " : "
                            +
                            ex.getMessage());
        }
    }


    public void threadedIndexDocumentList(List<DMEntity> documentEntities,
                                          final long readVersionTimeOut,
                                          final TimeUnit readVersionTimeoutTimeUnit,
                                          final boolean updateDocsMetaWrapper,
                                          int poolSize,
                                          final boolean disableThreading,
                                          final boolean asyncDocumentRead)
            throws IndexException, DataSourceException, ConfigException {
        try {

            ExecutorService executorService = null;
            if(!disableThreading)
                 executorService = new CustomSolrDocThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS,
                         new LinkedBlockingQueue<Runnable>(), this.solr);
            List<SolrInputDocument> updatedDocument = new ArrayList<SolrInputDocument>();
            List<String> updatedDocumentIds = new ArrayList<String>();
            Map<Long, Future<SolrInputDocument>> dataFutures = new HashMap<Long, Future<SolrInputDocument>>();
            Map<Long, Map<String, Object>> futuresFilesMetaDatas =
                    new HashMap<Long, Map<String, Object>>();

            //1 thread to read files, and put data on queue
            Map<DocumentIndexStatus, DocumentVersion> documentVersionMap = new HashMap<DocumentIndexStatus, DocumentVersion>();
            Map<DocumentIndexStatus, SolrDocCallable> docIndexTasks = new HashMap<DocumentIndexStatus, SolrDocCallable>();

            final BlockingQueue<DocumentIndexStatus> sharedQueue = new LinkedBlockingQueue<DocumentIndexStatus>();
            for (final DMEntity doc : documentEntities) {
                if (log.isDebugEnabled()) {
                    log.debug("Adding TO File Read Queue Document doc: #" + doc.getUid() + " " +
                            doc.getName() + " " + doc.getPath());

                }
                Document document = (Document)doc;
                //load data
                DocumentIndexStatus documentIndexStatus = new DocumentIndexStatus();
                documentIndexStatus.setDmEntity(document);
                DocumentVersion version =
                        FactoryInstantiator.getInstance().getDocumentVersionFactory().getLastDocumentVersion(document);
                documentVersionMap.put(documentIndexStatus, version);

                SolrDocCallable solrInputDocumentCallable =
                        new SolrDocCallable(documentIndexStatus, this,
                                updateDocsMetaWrapper, false,
                                readVersionTimeOut, readVersionTimeoutTimeUnit,
                                mp);
                docIndexTasks.put(documentIndexStatus, solrInputDocumentCallable);

            }
            //launcg thread to read files in parallel
            executorService.submit(new SolrDocFileReaderCallable(documentVersionMap,
                    readVersionTimeOut, readVersionTimeoutTimeUnit, sharedQueue));


            executorService.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    //pool queeeeue

                    DocumentIndexStatus indexStatus = sharedQueue.poll(readVersionTimeOut, readVersionTimeoutTimeUnit);
                    //when polled
                }
            });

            //reloop on doc to start
            for (final DMEntity doc : documentEntities) {
                if (log.isDebugEnabled()) {
                    log.debug("Start Adding Document doc: #" + doc.getUid() + " " +
                            doc.getName() + " " + doc.getPath());
                }


                if(!disableThreading && executorService != null){

                    Future<SolrInputDocument> solrInputDocumentFuture =
                            executorService.submit(solrInputDocumentCallable);

                    dataFutures.put(doc.getUid(), solrInputDocumentFuture);
                }  else {
                    if(doc instanceof Folder){
                        SolrInputDocument solrInputDocument = toSolrInputDocument((Folder) doc,
                                FactoryInstantiator.getInstance()
                                        .getVirtualFolderFactory()
                                        .virtualFolderMetaDataList((Folder)doc));
                        updatedDocument.add(solrInputDocument);
                        updatedDocumentIds.add(String.valueOf(doc.getUid()));
                    } else {
                        Document document = (Document)doc;
                        //load data
                        DocumentVersion version =
                                FactoryInstantiator.getInstance().getDocumentVersionFactory().getLastDocumentVersion(document);
                        DocumentIndexStatus documentIndexStatus = new DocumentIndexStatus();
                        Map<String, Object> fileData = readVersionFileToData(document, version, documentIndexStatus);
                        SolrInputDocument solrInputDocument =
                                new SolrDocGenerator(document, mp)
                                        .toSolrInputDocument(false, updateDocsMetaWrapper,
                                                fileData,
                                                documentIndexStatus);

                        updatedDocument.add(solrInputDocument);
                        updatedDocumentIds.add(String.valueOf(doc.getUid()));
                    }
                }
            }

            if(!disableThreading){
                for (Long item : dataFutures.keySet()) {
                    /*Future<SolrInputDocument> future = dataFutures.get(item);
                    try {
                        updatedDocument.add(future.get(readVersionTimeOut, readVersionTimeoutTimeUnit));
                        updatedDocumentIds.add(String.valueOf(item));
                        log.debug("thread read success. added content for doc #{}", item);
                    } catch (TimeoutException ex) {
                        log.debug("timeout for thread while reading doc #{}", item);
                        if (!future.isCancelled()) {
                            future.cancel(true);
                        }
                    } catch (Exception ex) {
                        log.debug("timeout for thread while reading doc #{}", item);
                    }*/
                }

                executorService.shutdown();;
            }
            if (updatedDocumentIds.size() != documentEntities.size()) {
                log.debug("{} documents won't be indexed", (documentEntities.size() - updatedDocument.size()));
            }
        }
        catch (Exception io) {
            throw new IndexException(io, "An exception occured while indexing document list " + io.getMessage());
        }

        /*catch (IOException io) {
            throw new IndexException(io, "An exception occured while indexing document list " + io.getMessage());
        } catch (SolrServerException ex) {
            throw new IndexException(ex, "An exception occured while indexing document " + ex.getMessage());
        }*/
    }


    public void indexDocumentList(List<DMEntity> documentEntities)
            throws IndexException, DataSourceException, ConfigException {
        try {
            List<SolrInputDocument> updatedDocument = new ArrayList<SolrInputDocument>();
            List<String> updatedDocumentIds = new ArrayList<String>();
            for (DMEntity doc : documentEntities) {

                if (log.isDebugEnabled()) {
                    log.debug("Start Adding Document doc: #" + doc.getUid() + " " +
                            doc.getName() + " " + doc.getPath());
                }

                SolrInputDocument solrInputDocument = null;
                if(doc instanceof Folder){
                    solrInputDocument = toSolrInputDocument((Folder) doc,
                            FactoryInstantiator.getInstance()
                                    .getVirtualFolderFactory()
                                    .virtualFolderMetaDataList((Folder)doc));
                    updatedDocument.add(solrInputDocument);
                    updatedDocumentIds.add(String.valueOf(doc.getUid()));
                } else {
                    solrInputDocument = toSolrInputDocument((Document) doc, null, true, 2, TimeUnit.MINUTES);
                }

                if (solrInputDocument != null) {
                    updatedDocumentIds.add(String.valueOf(doc.getUid()));
                    updatedDocument.add(solrInputDocument);


                    log.debug("Solr Added doc: #" + solrInputDocument.getFieldValue("DocumentId"));

                    if (log.isTraceEnabled()) {

                        for (String field : solrInputDocument.getFieldNames()) {
                            if (!field.equals("DocumentBody")) {
                                log.trace("Solr result doc: ======> {} : {}", field, solrInputDocument.getFieldValue(field));
                            }
                        }

                    }
                }
            }

            this.solr.deleteById(updatedDocumentIds);
            this.solr.add(updatedDocument);
            this.solr.commit();
        } catch (IOException io) {
            throw new IndexException(io, "An exception occured while indexing document list " + io.getMessage());
        } catch (SolrServerException ex) {
            throw new IndexException(ex, "An exception occured while indexing document " + ex.getMessage());
        }
    }

    public List<? extends Number> executeQuery(Query query)
            throws IndexException {
        QueryResponse rsp;
        try {
            rsp = solr.query(new SolrQuery(query.toString()));
            final List<Long> list = new Vector<Long>();
            SolrDocumentList documentList = rsp.getResults();
            for (SolrDocument dc : documentList) {
                list.add((Long) dc.getFieldValue("DocumentUid"));
            }
            return list;
        } catch (Exception ex) {
            throw new IndexException(ex, ex.getMessage());
        }
    }


    private SolrDocumentList loadDocuments(SolrQuery query)
            throws IndexException {
        QueryResponse rsp;
        try {

            rsp = solr.query(query);
            SolrDocumentList documentList = rsp.getResults();
            return documentList;

        } catch (Exception ex) {
            throw new IndexException(ex, ex.getMessage());
        }
    }


    public SearchResponse executeSolrQuery(SolrQuery query)
            throws IndexException {
        QueryResponse rsp;
        try {

            query.addField("score");
            query.addField("*");
            rsp = solr.query(query);
            final List<Long> list = new Vector<Long>();
            SolrDocumentList documentList = rsp.getResults();
            for (SolrDocument dc : documentList) {

                if (log.isDebugEnabled()) {
                    log.debug("Solr result doc: #" + dc.getFieldValue("DocumentUid"));
                    for (String field : dc.getFieldNames()) {
                        if (!field.equals("DocumentBody")) {
                            log.debug("Solr result doc: ======> {} : {}", field, dc.getFieldValue(field));
                        }

                    }

                }

                list.add((Long) dc.getFieldValue("DocumentUid"));
            }
            SearchResponse searchResponse = new SearchResponse(list);

            if (log.isDebugEnabled())
                log.debug(" Solr Num found " + documentList.getNumFound());

            searchResponse.setResults(new Long(documentList.getNumFound()).intValue());
            searchResponse.setRows(solrDocumentFactory.getPojosFromSolrInputDocument(rsp.getResults()));
            HashMap facetsData = new HashMap();
            if (rsp.getFacetRanges() != null) {
                for (RangeFacet facet : rsp.getFacetRanges()) {
                    ;
                    List<RangeFacet.Count> items = facet.getCounts();
                    for (RangeFacet.Count fc : items) {
                        if (facet.getStart() instanceof Date) {
                            facetsData.put(fc.getValue() + " TO " + fc.getValue() + facet.getGap().toString(),
                                    new Object[]{fc.getValue(), fc.getCount()});
                        } else {
                            facetsData.put((fc.getValue() + " TO " + (Long.parseLong(fc.getValue())
                                            + ((Number) facet.getGap()).longValue())),
                                    new Object[]{fc.getValue(), fc.getCount()});
                        }
                    }
                    break;
                }
            }
            if (rsp.getFacetFields() != null) {
                for (FacetField facet : rsp.getFacetFields()) {
                    if (log.isDebugEnabled())
                        log.debug("Returned Facet " + facet.getName() + " --> " + facet.getValueCount());
                    List<FacetField.Count> items = facet.getValues();
                    if (items != null) {
                        for (FacetField.Count fc : items) {
                            facetsData.put(fc.getName(), new Object[]{fc.getName(), fc.getCount()});
                        }
                        break;
                    }

                }
            }
            if (rsp.getFacetQuery() != null) {
                for (String facetQuery : rsp.getFacetQuery().keySet()) {
                    if (log.isDebugEnabled())
                        log.debug("Returned Facet Query: " + facetQuery);
                    facetsData.put(facetQuery, new Object[]{facetQuery, rsp.getFacetQuery().get(facetQuery)});
                }
            }
            searchResponse.setFacetsData(facetsData);
            return searchResponse;
        } catch (Exception ex) {
            throw new IndexException(ex, ex.getMessage());
        }
    }

    public void updateAcls(long docUid, List<DMEntityACL> acls, boolean commit)
            throws IndexException {
        try {
            log.trace("Updating ACL for document #" + docUid);
            QueryResponse rsp = this.solr.query(new SolrQuery("DocumentUid:" + docUid));
            if (rsp.getResults().getNumFound() > 0) {
                SolrDocument doc = rsp.getResults().get(0);
                this.solr.deleteById(String.valueOf(docUid));
                doc.removeFields("DocumentACL");
                doc.removeFields("score");
                SolrInputDocument docUpdate = ClientUtils.toSolrInputDocument(doc);
                for (int j = 0; j < acls.size(); j++) {
                    docUpdate.addField("DocumentACL", acls.get(j).getRuleHash());
                }
                this.solr.add(docUpdate);
            }
        } catch (Exception e) {
            throw new IndexException(e, e.getMessage());
        } finally {

            if (commit) {
                try {
                    this.solr.commit();
                } catch (Exception e) {
                    throw new IndexException(e, e.getMessage());
                }
            }

        }
    }

    public void deletePath(String path)
            throws IndexException {
        try {

            log.debug("Path delete: " + path);
            if (path.endsWith("/")) {
                path = path.substring(0, path.lastIndexOf("/"));
            }

            UpdateResponse response = this.solr.deleteByQuery("DocumentParent:" + ClientUtils.escapeQueryChars(path + "/") + "*");
            log.debug(response.toString());
            this.solr.commit();
        } catch (Exception ex) {
            throw new IndexException(ex, ex.getMessage());
        }
    }

    public void deleteByQuery(String query)
            throws IndexException {
        try {
            this.solr.deleteByQuery(query);
            this.solr.commit();
        } catch (Exception ex) {
            throw new IndexException(ex, ex.getMessage());
        }
    }

    public void updatePath(String oldPath, String newPath)
            throws IndexException {
        try {
            if (oldPath.endsWith("/")) {
                oldPath = oldPath.substring(0, oldPath.lastIndexOf("/"));
            }
            if (!newPath.endsWith("/")) {
                newPath += "/";
            }
            Query q = new DocumentParentClause(oldPath).getLuceneQuery();
            String pathQuery = q.toString().replaceAll("/", "\\\\/");


            log.debug("find documents to update {}", pathQuery);

            SolrDocumentList items = this.solr.query(new SolrQuery(pathQuery)).getResults();


            if (items.getNumFound() > 0) {
                List<SolrInputDocument> documentList = new ArrayList<SolrInputDocument>();
                this.solr.deleteByQuery(q.toString());
                for (SolrDocument doc : items) {
                    String parentPath = doc.getFieldValue("DocumentParent").toString();
                    parentPath = newPath + parentPath.substring(oldPath.length() + 1);

                    String path = doc.getFieldValue("DocumentPath").toString();
                    path = newPath + path.substring(oldPath.length() + 1);


                    doc.removeFields("DocumentPath");
                    doc.removeFields("DocumentParent");
                    doc.removeFields("score");
                    SolrInputDocument updatedDocument = ClientUtils.toSolrInputDocument(doc);
                    updatedDocument.addField("DocumentParent", parentPath);
                    updatedDocument.addField("DocumentPath", path);
                    documentList.add(updatedDocument);
                }
                this.solr.add(documentList);
                this.solr.commit();
            }
        } catch (Exception ex) {
            throw new IndexException(ex, ex.getMessage());
        }
    }

    public List<String> filterFields() {

        try {
            SolrQuery query = new SolrQuery();
            query.setRequestHandler("luke");
            query.setQuery("numTerms=0");
            SolrResponse response = solr.query(query);
            SimpleOrderedMap items = (SimpleOrderedMap) response.getResponse().get("fields");
            List<String> fieldList = new ArrayList<String>();
            for (int u = 0; u < items.size(); u++) {
                fieldList.add(items.getName(u));


            }

            return fieldList;
        } catch (Exception e) {
            throw new IndexException(e, e.getMessage());
        }


    }


    public void commit() throws IndexException {

        try {
            this.solr.commit();
        } catch (Exception e) {
            throw new IndexException(e);
        }
    }

    public void closeSolr() {
        try {
            this.solr.shutdown();
        } catch (Exception e) {
            log.error("Error while closing Solr", e);
        }
    }

    public IPathController getPathController() {
        return pathController;
    }

    public void setPathController(IPathController pathController) {
        this.pathController = pathController;
    }
}
