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

    private SolrInputDocument toSolrInputDocument(Document document,
                                                  SolrDocument previousSolrDocument,
                                                  boolean asyncDocumentRead,
                                                  long readTimeOut,
                                                  TimeUnit timeUnit)
            throws DataSourceException, ConfigException {
        return toSolrInputDocument(document, previousSolrDocument, false, true, asyncDocumentRead, readTimeOut, timeUnit);
    }


    private SolrInputDocument toSolrInputDocument(Document document,
                                                  SolrDocument previousSolrDocument,
                                                  boolean flush,
                                                  boolean updateMetasWrapper,
                                                  boolean asyncDocumentRead,
                                                  long readTimeOut,
                                                  TimeUnit timeUnit)
            throws DataSourceException, ConfigException {


        DocumentIndexStatus documentIndexStatus = new DocumentIndexStatus();
        documentIndexStatus.setEntityId(document.getUid());


        SimpleDateFormat dateParser = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat dateTimeParser = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat utcDateParser = new SimpleDateFormat("dd-MM-yyyy");
        utcDateParser.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat utcDateTimeParser = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        utcDateTimeParser.setTimeZone(TimeZone.getTimeZone("UTC"));
        log.debug("processing document {} for path {}", document.getUid(), document.getPath());
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("DocumentUid", document.getUid());
        doc.addField("DocumentName", document.getName().toLowerCase());
        doc.addField("DocumentNameDisplayed", document.getName());
        doc.addField("DocumentNameAnalysed", document.getName());
        if (document.getExtension() != null) {
            doc.addField("DocumentExtension", document.getExtension().toLowerCase());
        }
        doc.addField("DocumentOwner", document.getOwner() + "@" + document.getOwnerSource());
        doc.addField("DocumentOwnerId", document.getOwner());
        doc.addField("DocumentOwnerSource", document.getOwnerSource());
        doc.addField("DocumentPath", document.getPath());
        doc.addField("DocumentParent", document.getFolder().getPath() + "/");
        doc.addField("DocumentParentId", document.getFolder().getUid());
        DocumentVersion version =
                FactoryInstantiator.getInstance().getDocumentVersionFactory().getLastDocumentVersion(document);

        if (version == null) {
            log.error("Document {} has no version", document.getUid());
            return null;
        }
        //standard datas
        doc.addField("DocumentCreationDate", document.getCreationDate());
        doc.addField("DocumentUpdateDate", document.getUpdateDate());
        doc.addField("DocumentVersionId", version.getUid());
        doc.addField("DocumentVersionCreationDate", version.getCreationDate());
        doc.addField("DocumentVersionUpdateDate", version.getModificationDate());
        doc.addField("DocumentVersionOwner", version.getAuthor() + "@" + version.getAuthorSource());
        doc.addField("DocumentVersionOwnerId", version.getAuthor());
        doc.addField("DocumentVersionOwnerSource", version.getAuthorSource());
        doc.addField("DocumentVersionLength", version.getLength());
        doc.addField("DocumentVersionHash", version.getHashMD5() + ":" + version.getHashSHA1());

        Lock lock = document.getCheckoutLock();

        doc.addField("DocumentCheckout", lock != null);

        if (lock != null) {
            doc.addField("DocumentCheckoutOwnerId", lock.getUser());
            doc.addField("DocumentCheckoutOwnerSource", lock.getUserSource());
            doc.addField("DocumentCheckoutDate", lock.getDate());
        }
        // DocumentOutWorkflow

        DocumentWorkflowStatusRequest req =
                FactoryInstantiator.getInstance().getDocumentWorkflowStatusRequestFactory().getLastPendingRequest(
                        document);
        DocumentWorkflowStatus st =
                FactoryInstantiator.getInstance().getDocumentWorkflowStatusFactory().getLastDocumentWorkflowStatus(
                        document.getUid());
        boolean outOfWorkflow = true;
        if (req != null) {
            outOfWorkflow = false;
        }
        if (st != null) {

            WorkflowStatus stOrg = FactoryInstantiator.getInstance().getWorkflowStatusFactory().getWorkflowStatus(
                    st.getWorkflowStatusUid());
            doc.addField("DocumentWorkflowStatusName", stOrg.getName());
            doc.addField("DocumentWorkflowStatusUid", st.getWorkflowStatusUid());
            if (stOrg.getSuccessorUid() == null) {
                outOfWorkflow = true;
            }
        }
        doc.addField("DocumentOutWorkflow", outOfWorkflow);
        List<MetaValue> values = null;
        if (version.getDocumentType() != null) {
            doc.addField("DocumentTypeUid", version.getDocumentType().getUid());
            doc.addField("DocumentTypeName", version.getDocumentType().getName());
            values = FactoryInstantiator.getInstance().getMetaValueFactory().getMetaValues(version);
            for (MetaValue value : values) {
                switch (value.getMeta().getMetaType()) {
                    case MetaType.STRING:
                        doc.addField("MetaDataString_" + value.getMetaUid(),
                                value.getValue() != null ? ((String) value.getValue().toString()) : null);
                        break;
                    case MetaType.BOOLEAN:
                        doc.addField("MetaDataBoolean_" + value.getMetaUid(), value.getValue());
                        break;
                    case MetaType.NUMBER:
                        doc.addField("MetaDataNumber_" + value.getMetaUid(), value.getValue());
                        break;
                    case MetaType.DATE:

                        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        if (value != null && value.getValue() != null) {
                            //reparse date for local
                            String dateString = dateParser.format(value.getValue());
                            try {
                                cal.setTime(utcDateParser.parse(dateString));
                                doc.addField("MetaDataDate_" + value.getMetaUid(), cal.getTime());
                            } catch (Exception ex) {
                                log.error("error while reparsing meta data date {} {} {}", value.getMeta().getName(), value, dateString);
                            }
                        }
                        break;
                    case MetaType.LIST:
                        if (value != null && value.getValue() != null) {
                            List<String> items = ((MetaListValue) value).getValue();
                            for (String u : items)
                                doc.addField("MetaDataList_" + value.getMetaUid(), u);
                        }
                        break;
                    default:
                        doc.addField("MetaData_" + value.getMetaUid(), value.getValue());
                        break;
                }
            }
        }
        for (String attribute : document.getAttributes().keySet()) {
            if (attribute.equals("SearchTag")) {
                //Custom parsing
                String[] tags = document.getAttributes().get(attribute) != null &&
                        document.getAttributes().get(attribute).getValue() != null ?
                        document.getAttributes().get(attribute).getValue().split("\\|\\|\\|") : new String[]{};
                for (String tag : tags) {
                    doc.addField("Attribute_SEARCHTAG", tag);
                }
            } else {
                doc.addField("Attribute_" + attribute.toUpperCase(),
                        document.getAttributes().get(attribute).getValue());
            }

        }

        if (updateMetasWrapper) {
            if ((values != null && values.size() > 0) || (document.getAttributes() != null || document.getAttributes().size() > 0)) {
                AddonDataHandler.AddonDatasWrapper wrapper = new AddonDataHandler.AddonDatasWrapper();
                wrapper.setEntityAttributes(document.getAttributes());
                wrapper.setEntityMetaValues(values);
                try {
                    document.setAddOnDatas(mp.writeValueAsString(wrapper));
                    if (flush) {
                        FactoryInstantiator.getInstance().getDocumentFactory().saveDocument(document);
                    } else
                        FactoryInstantiator.getInstance().getDocumentFactory().saveDocumentNoFlush(document);
                    log.debug("updating addon data with " + document.getAddOnDatas());
                } catch (Exception ex) {
                    log.error("error while generation addon meta field", ex);
                }
            } else {
                log.debug("not generating addon field because of no data");
            }
        }

        if (document.getAddOnDatas() != null && document.getAddOnDatas().length() > 0) {
            doc.addField("DocumentRawAddonDatas", document.getAddOnDatas());
            log.debug("adding current addon data {}", document.getAddOnDatas());
        }
        Object body = null;
        Map<String, Object> metaDatas = null;

        if(!asyncDocumentRead){
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
            if (body == null) {
                body = IndexHelper.EMPTY_STRING;
            }
            if (body instanceof String) {
                doc.addField("DocumentBody", body);
                documentIndexStatus.setBodyIndexed(true);
            }
            if (metaDatas != null) {
                for (String mKey : metaDatas.keySet()) {
                    doc.addField("FileMetaData_" + mKey, metaDatas.get(mKey));
                }
            }
        } else {
            if(fileReaderExecutor == null || fileReaderExecutor.isTerminated() || fileReaderExecutor.isShutdown()){
                final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(100);
                fileReaderExecutor = new ThreadPoolExecutor(5, fileReadThreadPoolSize,
                        0L, TimeUnit.MILLISECONDS,
                        queue);
            }
            try {
                ThreadedGlobalFilter globalFilter =
                        new ThreadedGlobalFilter(readTimeOut, timeUnit, fileReaderExecutor);
                //launch threaded file read
                body = globalFilter.getFileBody(document, version.getInputStream());
                metaDatas = globalFilter.getMetaDatas();
            } catch (Throwable ex) {
                log.debug("Error while getting body", ex);
                documentIndexStatus.setBodyIndexed(false);
                StringWriter stringWriterStackTrace = new StringWriter();
                PrintWriter prw = new PrintWriter(stringWriterStackTrace);
                ex.printStackTrace(prw);
                documentIndexStatus.setError(ex.getMessage()  + " ==> "
                    + stringWriterStackTrace);

            }
            if (body == null) {
                body = IndexHelper.EMPTY_STRING;
            }
            if (body instanceof String) {
                doc.addField("DocumentBody", body);
                documentIndexStatus.setBodyIndexed(true);
            }
            if (metaDatas != null) {
                for (String mKey : metaDatas.keySet()) {
                    doc.addField("FileMetaData_" + mKey, metaDatas.get(mKey));
                }
            }
        }



        List<DMEntityACL> acls =
                org.kimios.kernel.security.FactoryInstantiator.getInstance().getDMEntitySecurityFactory().getDMEntityACL(
                        document);

        for (int i = 0; i < acls.size(); i++) {
            doc.addField("DocumentACL", acls.get(i).getRuleHash());
        }




        documentIndexStatusFactory.saveItem(documentIndexStatus);

        return doc;
    }

    private SolrInputDocument toSolrInputDocument(Folder folder, List<VirtualFolderMetaData> metaValues)
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

            /*
                check if document must be reindexed
             */


            String docQuery = "DocumentUid:" + documentEntity.getUid();


            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery(docQuery);
            SolrDocumentList searchResponse = this.loadDocuments(solrQuery);

            SolrDocument previousRecord = null;
            if (searchResponse.getNumFound() == 1) {
                log.debug("found one document for uid #" + documentEntity.getUid());
                previousRecord = searchResponse.get(0);
            }
            this.deleteDocument(document);
            SolrInputDocument solrInputDocument = toSolrInputDocument(document, previousRecord, true, 2,
                    TimeUnit.MINUTES);
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
                 executorService = Executors.newFixedThreadPool(poolSize);
            List<SolrInputDocument> updatedDocument = new ArrayList<SolrInputDocument>();
            List<String> updatedDocumentIds = new ArrayList<String>();
            Map<Long, Future<SolrInputDocument>> dataFutures = new HashMap<Long, Future<SolrInputDocument>>();
            for (final DMEntity doc : documentEntities) {
                final long docId = doc.getUid();
                final String docPath = doc.getPath();
                if (log.isDebugEnabled()) {
                    log.debug("Start Adding Document doc: #" + doc.getUid() + " " +
                            doc.getName() + " " + doc.getPath());
                }


                if(!disableThreading && executorService != null){
                    Callable<SolrInputDocument> solrInputDocumentCallable = new Callable<SolrInputDocument>() {
                        @Override
                        public SolrInputDocument call() throws Exception {
                            log.debug("started solr input document for doc #" + docId
                                    + " (" + docPath + ")");
                            SolrInputDocument solrInputDocument = toSolrInputDocument((Document) doc,
                                    null,
                                    false,
                                    updateDocsMetaWrapper,
                                    asyncDocumentRead,
                                    readVersionTimeOut,
                                    readVersionTimeoutTimeUnit);
                            return solrInputDocument;
                        }
                    };

                    Future<SolrInputDocument> solrInputDocumentFuture =
                            executorService.submit(solrInputDocumentCallable);

                    dataFutures.put(doc.getUid(), solrInputDocumentFuture);
                }  else {
                    updatedDocument.add(toSolrInputDocument((Document)doc, null, false,
                            updateDocsMetaWrapper, asyncDocumentRead, readVersionTimeOut, readVersionTimeoutTimeUnit ));
                    updatedDocumentIds.add(String.valueOf(doc.getUid()));
                }
            }

            if(!disableThreading){
                for (Long item : dataFutures.keySet()) {
                    Future<SolrInputDocument> future = dataFutures.get(item);
                    try {
                        updatedDocument.add(future.get(readVersionTimeOut, readVersionTimeoutTimeUnit));
                        updatedDocumentIds.add(String.valueOf(item));
                        log.debug("thread read success. added contentn for doc #{}", item);
                    } catch (TimeoutException ex) {
                        log.debug("timeout for thread while reading doc #{}", item);
                        if (!future.isCancelled()) {
                            future.cancel(true);
                        }
                    } catch (Exception ex) {
                        log.debug("timeout for thread while reading doc #{}", item);
                    }
                }

                try {

                    executorService.shutdownNow();
                } catch (Exception ex) {

                }
            }
            if (updatedDocumentIds.size() != documentEntities.size()) {
                log.debug("{} documents won't be indexed", (documentEntities.size() - updatedDocument.size()));
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
                SolrInputDocument solrInputDocument = toSolrInputDocument((Document) doc, null, true, 2, TimeUnit.MINUTES);
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
