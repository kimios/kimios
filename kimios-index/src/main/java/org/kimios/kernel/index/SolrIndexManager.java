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
 * MERCHANTABILITY oSr FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.kernel.index;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import org.apache.solr.common.util.SimpleOrderedMap;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.controller.IPathController;
import org.kimios.kernel.dms.*;
import org.kimios.kernel.dms.model.*;
import org.kimios.kernel.events.impl.AddonDataHandler;
import org.kimios.kernel.events.model.EventContext;
import org.kimios.exceptions.DataSourceException;
import org.kimios.exceptions.IndexException;
import org.kimios.kernel.index.query.factory.DocumentFactory;
import org.kimios.kernel.index.query.factory.DocumentIndexStatusFactory;
import org.kimios.kernel.index.query.model.DocumentIndexStatus;
import org.kimios.kernel.index.query.model.SearchResponse;
import org.kimios.kernel.jobs.Job;
import org.kimios.kernel.jobs.JobImpl;
import org.kimios.kernel.jobs.ThreadManager;
import org.kimios.kernel.security.model.DMEntityACL;
import org.kimios.kernel.security.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;


public class SolrIndexManager
        implements ISolrIndexManager {
    private static Logger log = LoggerFactory.getLogger(SolrIndexManager.class);

    private Reindexer reindexer = null;

    private Thread reindexThread = null;

    private IPathController pathController;

    private org.kimios.kernel.index.query.factory.DocumentFactory solrDocumentFactory;

    public DocumentFactory getSolrDocumentFactory() {
        return solrDocumentFactory;
    }

    public void setSolrDocumentFactory(DocumentFactory solrDocumentFactory) {
        this.solrDocumentFactory = solrDocumentFactory;
    }


    private DocumentIndexStatusFactory documentIndexStatusFactory;

    public void setDocumentIndexStatusFactory(DocumentIndexStatusFactory documentIndexStatusFactory) {
        this.documentIndexStatusFactory = documentIndexStatusFactory;
    }

    private ObjectMapper mp;

    public SolrIndexManager(SolrServer solr, SolrServer contentSolr) {
        this.solr = solr;
        this.contentSolrServer = contentSolr;
        mp = new ObjectMapper();
        mp.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mp.addMixIn(Meta.class, AddonDataHandler.MetaMixIn.class);
    }

    private final SolrServer solr;

    private final SolrServer contentSolrServer;

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

    public void indexDocument(DMEntity documentEntity)
            throws IndexException, DataSourceException, ConfigException {
        try {

            Document document = (Document) documentEntity;
            String docQuery = "DocumentUid:" + documentEntity.getUid();
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery(docQuery);
            this.deleteDocument(document);
            //load data
            final DocumentVersion version =
                    FactoryInstantiator.getInstance().getDocumentVersionFactory().getLastDocumentVersion(document);
            final DocumentIndexStatus documentIndexStatus = new DocumentIndexStatus();
            documentIndexStatus.setDmEntity((DMEntityImpl)documentEntity);

            final long readVersionTimeOut = 60;
            final TimeUnit readVersionTimeoutTimeUnit = TimeUnit.SECONDS;


            final Document doc = (Document)documentEntity;


            final String docPath = documentEntity.getPath();


            final SolrDocGenerator solrDocGenerator = new SolrDocGenerator(document, mp);
            SolrInputDocument solrInputDocument = solrDocGenerator.toSolrInputDocument(true, true, documentIndexStatus);


            Session session = EventContext.get().getSession();
            String taskId = UUID.randomUUID().toString();
            Job<Map<String, Object>> rn = new JobImpl<Map<String,Object>>(taskId) {


                @Override
                public Map<String, Object> execute() {

                    Callable<Map<String,Object>> reader = new Callable<Map<String, Object>>() {
                        @Override
                        public Map<String, Object> call() throws Exception {
                            return new SolrDocFileReaderCallable(null, -1, null, null)
                                    .readVersionFileToData((Document) doc,
                                            version,
                                            documentIndexStatus);
                        }
                    };
                    //handle asynchronous indexing !

                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    Future<Map<String, Object>> result = executorService.submit(reader);
                    Map<String, Object> item = null;
                    try {
                        item = result.get(readVersionTimeOut, readVersionTimeoutTimeUnit);
                        documentIndexStatus.setReadFileDatas(item);

                        //put in index !!

                        SolrInputDocument contentSolrInputDocument = solrDocGenerator.toSolrContentInputDocument(true, true,
                                documentIndexStatus.getReadFileDatas(), documentIndexStatus);

                        contentSolrServer.add(contentSolrInputDocument);
                        contentSolrServer.commit();
                        log.info("content added & committed to body index !!");


                    }catch (InterruptedException ex){
                        result.cancel(true);
                        documentIndexStatus.setError("Interrupted error " + ex.getMessage());
                        documentIndexStatus.setBodyIndexed(false);

                        return null;
                    }
                    catch (TimeoutException ex){
                        //Important to kill running thread !!
                        boolean cancelled = result.cancel(true);
                        documentIndexStatus.setError("Timeout Error (def: " + readVersionTimeOut + " " + readVersionTimeoutTimeUnit.toString() + "). " + ex.getMessage());
                        log.error("timeout: cancelled read thread for #" + documentIndexStatus.getDmEntity().getUid() + "(" + cancelled + ")");
                        documentIndexStatus.setBodyIndexed(false);

                        return null;
                    }
                    catch (ExecutionException ex){
                        result.cancel(true);
                        documentIndexStatus.setError("Exception Error: " + ex.getMessage());
                        documentIndexStatus.setBodyIndexed(false);
                        log.error("exception: cancelled read thread for #" + documentIndexStatus.getDmEntity().getUid(), ex);

                        return null;
                    }
                    catch (Exception ex){
                        log.error("error while indexing doc", ex);
                    }
                    finally {
                        try{
                            executorService.shutdown();
                        } catch (Exception ex){

                        }
                    }
                    executorService.shutdownNow();

                    return item;
                }
            };
            rn.setSession(session);
            ThreadManager.getInstance().startJob(session, rn);
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
            SolrFolderGenerator generator = new SolrFolderGenerator(folder, metaValues, this.mp);
            SolrInputDocument solrInputDocument = generator.toSolrInputDocument(folder, metaValues);
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


    public void threadedIndexDocumentList(final List<DMEntity> documentEntities,
                                          final long readVersionTimeOut,
                                          final TimeUnit readVersionTimeoutTimeUnit,
                                          final boolean updateDocsMetaWrapper,
                                          int poolSize)
            throws IndexException, DataSourceException, ConfigException {
        try {

            final ExecutorService executorService = new CustomSolrDocThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(), this.solr);

            Map<DocumentIndexStatus, DocumentVersion> documentVersionMap = new HashMap<DocumentIndexStatus, DocumentVersion>();
            final Map<DocumentIndexStatus, SolrDocCallable> docIndexTasks = new HashMap<DocumentIndexStatus, SolrDocCallable>();
            final BlockingQueue<DocumentIndexStatus> sharedQueue = new LinkedBlockingQueue<DocumentIndexStatus>();
            for (final DMEntity doc : documentEntities) {
                if (log.isDebugEnabled()) {
                    log.debug("Adding TO File Read Queue Document doc: #" + doc.getUid() + " " +
                            doc.getName() + " " + doc.getPath());

                }
                //load data
                DocumentIndexStatus documentIndexStatus = new DocumentIndexStatus();
                documentIndexStatus.setDmEntity((DMEntityImpl) doc);
                documentIndexStatus.setEntityId(doc.getUid());
                if(doc instanceof Document){
                    DocumentVersion version =
                            FactoryInstantiator.getInstance().getDocumentVersionFactory().getLastDocumentVersion((Document)doc);
                    documentVersionMap.put(documentIndexStatus, version);
                } else {
                    documentVersionMap.put(documentIndexStatus, new DocumentVersion());
                }


                SolrDocCallable solrInputDocumentCallable =
                        new SolrDocCallable(documentIndexStatus, this,
                                updateDocsMetaWrapper, false,
                                readVersionTimeOut, readVersionTimeoutTimeUnit,
                                mp);
                docIndexTasks.put(documentIndexStatus, solrInputDocumentCallable);

            }
            //launcg thread to read files in parallel and get future
            //1 thread to read files, and put data on queue
            Future<Map<Long, Map<String, Object>>> futuresFilesMetaDatas =
                    Executors.newSingleThreadExecutor().submit(new SolrDocFileReaderCallable(documentVersionMap,
                            readVersionTimeOut, readVersionTimeoutTimeUnit, sharedQueue));


            log.info("started File Reader. Status Queue Size: {} ", sharedQueue.size());

            Future<?> globalFuture = executorService.submit(new Callable<Integer>() {

                @Override
                public Integer call() {
                    //pool queeeeue
                    int docCount = 0;
                    List<DocumentIndexStatus> notFound = new ArrayList<DocumentIndexStatus>();
                    while (!Thread.currentThread().isInterrupted()) {

                        if (docCount >= documentEntities.size()) {
                            log.info("document processed count: {}. Exiting !", docCount);
                            break;
                        }
                        try {
                            DocumentIndexStatus indexStatus = sharedQueue.take();
                            //when polled
                            log.debug("taken document : {}. will now wait for processed data",
                                    indexStatus);

                            //launch index status
                            SolrDocCallable solrDocCallable = docIndexTasks.get(indexStatus);
                            if(solrDocCallable != null){
                                solrDocCallable.setFileData(indexStatus.getReadFileDatas());
                                if(log.isDebugEnabled() && indexStatus.getDmEntity() instanceof Document){
                                    log.debug("parsed file data fields count {}. has body - {}",
                                            indexStatus.getReadFileDatas().size(),
                                            indexStatus.getReadFileDatas().get("DocumentBody") != null);
                                }

                                Future<SolrInputDocument[]> doc =
                                        executorService.submit(solrDocCallable);

                                try {
                                    SolrInputDocument[] docData = doc.get();
                                    solr.deleteById(Long.toString(indexStatus.getDmEntity().getUid()));
                                    solr.add(docData[0]);
                                    contentSolrServer.deleteById(Long.toString(indexStatus.getDmEntity().getUid()));
                                    if(docData[1] != null){
                                        contentSolrServer.add(docData[1]);
                                    }

                                } catch (Exception ex) {
                                    log.error("error while adding doc #" + indexStatus.getDmEntity().getUid(), ex);
                                }

                            } else {
                                log.error("SolrDocCallable Not Found for Index Status {}", indexStatus);
                                notFound.add(indexStatus);
                            }
                        } catch (Exception ex) {
                            log.error("error while taking and processing !", ex);
                        }

                        docCount++;


                    }

                    for(DocumentIndexStatus indexStatus: notFound) {
                        log.debug("processing missed document : {}. will now wait for processed data",
                                indexStatus);
                        SolrDocCallable solrDocCallable = docIndexTasks.get(indexStatus);
                        if (solrDocCallable != null) {
                            solrDocCallable.setFileData(indexStatus.getReadFileDatas());
                            log.debug("parsed file data fields count {}. has body - {}",
                                    indexStatus.getReadFileDatas().size(),
                                    indexStatus.getReadFileDatas().get("DocumentBody") != null);
                            Future<SolrInputDocument[]> doc =
                                    executorService.submit(solrDocCallable);


                            try {
                                SolrInputDocument[] slDocs = doc.get();
                                solr.deleteById(Long.toString(indexStatus.getDmEntity().getUid()));
                                solr.add(slDocs[0]);
                                contentSolrServer.deleteById(Long.toString(indexStatus.getDmEntity().getUid()));
                                if(slDocs[1] != null){
                                    contentSolrServer.add(slDocs[1]);
                                }
                            } catch (Exception ex) {
                                log.error("error while adding doc #" + indexStatus.getDmEntity().getUid(), ex);
                            }
                        } else {
                            log.error("definitely not processing do {}", indexStatus);

                        }
                    }
                    log.debug("returning doc count processed {}", docCount);
                    try {
                        solr.commit();

                    }catch (Exception ex){
                        log.error("error while committing block !!!", ex);
                    }
                    try {
                        contentSolrServer.commit();

                    }catch (Exception ex){
                        log.error("error while committing content block !!!", ex);
                    }

                    return  docCount;

                }
            });


            Object ret = globalFuture.get();
            log.debug("Global result {}", ret);
            executorService.shutdown();

            //update processed meta data addon fields
            if(updateDocsMetaWrapper){
                for(DMEntity entity: documentEntities){
                    if(entity instanceof Document){
                        FactoryInstantiator.getInstance().getDocumentFactory().saveDocument((Document)entity);
                    } else if(entity instanceof Folder){
                        FactoryInstantiator.getInstance().getFolderFactory().saveFolder((Folder)entity);
                    }
                }
            }
            //save document index status

            log.info("{} available doc status against / {} doc.", documentVersionMap.size(), documentEntities.size());


            int statusSaved = 0;
            for(DocumentIndexStatus dis: documentVersionMap.keySet()){
                documentIndexStatusFactory.saveItem(dis);
                statusSaved++;
            }
            log.info("documentidxstatus saved {}", statusSaved);
        }
        catch (Exception io) {
            throw new IndexException(io, "An exception occured while indexing document list " + io.getMessage());
        }
    }


    public void indexDocumentList(List<DMEntity> documentEntities)
            throws IndexException, DataSourceException, ConfigException {
        try {
            List<SolrInputDocument> updatedDocument = new ArrayList<SolrInputDocument>();
            List<SolrInputDocument> updatedDocumentContent = new ArrayList<SolrInputDocument>();
            List<String> updatedDocumentIds = new ArrayList<String>();
            for (DMEntity doc : documentEntities) {

                if (log.isDebugEnabled()) {
                    log.debug("Start Adding Document doc: #" + doc.getUid() + " " +
                            doc.getName() + " " + doc.getPath());
                }

                SolrInputDocument solrInputDocument = null;
                SolrInputDocument contentSolrInputDoc = null;
                if(doc instanceof Folder){

                    List<VirtualFolderMetaData> metaDatas = FactoryInstantiator.getInstance()
                            .getVirtualFolderFactory()
                            .virtualFolderMetaDataList((Folder)doc);
                    SolrFolderGenerator solrFolderGenerator = new SolrFolderGenerator((Folder)doc, metaDatas, this.mp);
                    solrInputDocument = solrFolderGenerator.toSolrInputDocument((Folder) doc, metaDatas);
                    updatedDocument.add(solrInputDocument);
                    updatedDocumentIds.add(String.valueOf(doc.getUid()));
                } else {

                    DocumentIndexStatus st = new DocumentIndexStatus();
                    st.setDmEntity((DMEntityImpl)doc);
                    st.setEntityId(doc.getUid());
                    Map<String, Object> fileDatas =
                            new SolrDocFileReaderCallable(null, -1, null, null)
                            .readVersionFileToData((Document)doc,
                                    FactoryInstantiator.getInstance()
                                            .getDocumentVersionFactory().getLastDocumentVersion((Document)doc),
                                    st);
                    SolrDocGenerator n = new SolrDocGenerator((Document)doc, this.mp);
                    solrInputDocument = n.toSolrInputDocument(false, true,  st);

                    //content
                    contentSolrInputDoc = n.toSolrContentInputDocument(false, true, st.getReadFileDatas(), st);
                }

                if (solrInputDocument != null && contentSolrInputDoc != null) {
                    updatedDocumentIds.add(String.valueOf(doc.getUid()));
                    updatedDocument.add(solrInputDocument);
                    updatedDocumentContent.add(contentSolrInputDoc);


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
            this.contentSolrServer.deleteById(updatedDocumentIds);
            this.contentSolrServer.add(updatedDocumentContent);
            this.solr.commit();
            this.contentSolrServer.commit();
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

            HashMap<String, List> tagsFacetsData = new HashMap();
            if (rsp.getFacetFields() != null) {
                for (FacetField facet : rsp.getFacetFields()) {
                    if (log.isDebugEnabled())
                        log.debug("Returned TAG Facet " + facet.getName() + " --> " + facet.getValueCount());
                    tagsFacetsData.put(facet.getName(), new ArrayList());
                    List<FacetField.Count> items = facet.getValues();
                    if (items != null) {
                        for (FacetField.Count fc : items) {
                            tagsFacetsData
                                    .get(facet.getName())
                                    .add(new Object[]{fc.getName(), fc.getCount()});
                        }
                    }
                }
            }
            searchResponse.setAllfacetsData(tagsFacetsData);

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
                /*
                    Regenerate document body
                 */
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

        try {
            this.contentSolrServer.shutdown();
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
