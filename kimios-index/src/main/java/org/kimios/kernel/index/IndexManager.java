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
import java.io.Reader;
import java.util.List;
import java.util.Vector;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.configuration.Config;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.dms.DMEntityType;
import org.kimios.kernel.dms.Document;
import org.kimios.kernel.dms.DocumentVersion;
import org.kimios.kernel.dms.FactoryInstantiator;
import org.kimios.kernel.dms.MetaType;
import org.kimios.kernel.dms.MetaValue;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.IndexException;
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.security.DMEntityACL;
import org.kimios.utils.configuration.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class IndexManager implements LuceneIndexManager
{
    private static Logger log = LoggerFactory.getLogger(IndexManager.class);

    private static IndexManager instance = null;

    private IndexWriter indexModifier;

    private Directory indexDirectory;

    private Thread reindexThread = null;

    private int reindexProgression = -1;

    private IndexManager() throws ConfigException, IndexException
    {
        try {
            File lock = new File(ConfigurationManager.getValue(Config.DEFAULT_INDEX_PATH) + "/write.lock");
            if (lock.exists()) {
                lock.delete();
            }

            File indexDir = new File(ConfigurationManager.getValue(Config.DEFAULT_INDEX_PATH));
            boolean create = indexDir.exists();
            this.indexDirectory = FSDirectory.open(indexDir);

            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
                    Version.LUCENE_46,
                    IndexHelper.getAnalyzer()
            );
            if(log.isDebugEnabled()){
                indexWriterConfig.setInfoStream(System.err);
            }
            indexModifier =
                    new IndexWriter(this.indexDirectory, indexWriterConfig);

            /*
            *
            *  Enable lucene debug
            *
            */
        } catch (IOException io) {
            throw new IndexException(io, io.getMessage());
        }
    }

    public static synchronized IndexManager getInstance() throws ConfigException, IndexException
    {
        if (instance == null) {
            instance = new IndexManager();
        }
        return instance;
    }

    public void reindex(String path) throws DataSourceException, ConfigException, IndexException
    {
        final String finalPath = path;
        if (this.reindexThread == null || !this.reindexThread.isAlive()) {
            this.reindexThread = new Thread(new Runnable()
            {
                public void run()
                {
                    try {
                        reindexProgression = 0;
                        int indexed = 0;
                        close();

                        /*
                        *  Init Auto commit hibernate Session
                        *
                        */
                        HFactory.setAutoCommit(true);
                        File indexDir = new File(ConfigurationManager.getValue(Config.DEFAULT_INDEX_PATH));
                        deleteDirectory(indexDir);
                        Directory d = FSDirectory.open(indexDir);
                        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_46,
                                IndexHelper.getAnalyzer());
                        indexWriterConfig.setMaxBufferedDocs(100);
                        indexWriterConfig.setRAMBufferSizeMB(32);
                        indexModifier = new IndexWriter(d, indexWriterConfig);
                        List<DMEntity> entities = FactoryInstantiator.getInstance().getDmEntityFactory()
                                .getEntitiesByPathAndType(finalPath, DMEntityType.DOCUMENT);
                        int total = entities.size();
                        log.debug("Entities to index: " + total);
                        for (int i = 0; i < entities.size(); i++) {
                            try {
                                if (entities.get(i).getClass().equals(Document.class)) {
                                    log.info(reindexProgression + "% - Indexing " + entities.get(i).getPath());
                                    indexDocument((Document) entities.get(i));
                                    indexed++;
                                }
                                reindexProgression = (int) Math.round((double) indexed / (double) total * 100);
                            } catch (Throwable ex) {
                                log.error("Exception on " + entities.get(i).getPath() + " during reindex", ex);
                            }
                        }
                        reindexProgression = -1;
                    } catch (Exception ex) {
                        log.error("Exception during reindex! Process stopped", ex);
                    }
                }
            });
            this.reindexThread.setName("ReindexThread");
            this.reindexThread.start();
        } else {
            throw new IndexException(null, "A reindex process is already running.");
        }
    }

    public int getReindexProgression()
    {
        return this.reindexProgression;
    }

    public boolean deleteDirectory(File path)
    {
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

    public void deleteDocument(Document document) throws IndexException
    {
        try {
            this.indexModifier.deleteDocuments(new DocumentUidClause(document.getUid()).getLuceneQuery());
            commit();
            log.debug("Commited deletion of document " + document.getUid());
        } catch (IOException e) {
            throw new IndexException(e,
                    "An exception occured while deleting document " + document.getUid() + " : " + e.getMessage());
        }
    }

    public void indexDocument(DMEntity documentEntity) throws IndexException, DataSourceException, ConfigException
    {
        try {
            Document document = (Document) documentEntity;
            this.deleteDocument(document);
            org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
            doc.add(IndexHelper.getUnanalyzedField("DocumentUid", document.getUid()));
            doc.add(IndexHelper.getUnanalyzedField("DocumentName", document.getName().toLowerCase()));
            doc.add(IndexHelper.getAnalyzedField("DocumentNameAnalysed", document.getName()));
            if (document.getExtension() != null) {
                doc.add(IndexHelper.getUnanalyzedField("DocumentExtension", document.getExtension().toLowerCase()));
            }
            doc.add(IndexHelper
                    .getUnanalyzedField("DocumentOwner", document.getOwner() + "@" + document.getOwnerSource()));
            doc.add(IndexHelper.getUnanalyzedField("DocumentParent", document.getFolder().getPath() + "/"));
            DocumentVersion version =
                    FactoryInstantiator.getInstance().getDocumentVersionFactory().getLastDocumentVersion(document);

            //standard datas
            doc.add(IndexHelper.getUnanalyzedField("DocumentCreationDate", document.getCreationDate()));
            doc.add(IndexHelper.getUnanalyzedField("DocumentUpdateDate", document.getUpdateDate()));
            doc.add(IndexHelper.getUnanalyzedField("DocumentVersionUpdateDate", version.getModificationDate()));

            if (version.getDocumentType() != null) {
                doc.add(IndexHelper.getUnanalyzedField("DocumentTypeUid", version.getDocumentType().getUid()));
                List<MetaValue> values = FactoryInstantiator.getInstance().getMetaValueFactory().getMetaValues(version);
                for (MetaValue value : values) {
                    switch (value.getMeta().getMetaType()) {
                        case MetaType.STRING:
                            doc.add(IndexHelper.getUnanalyzedField("MetaData" + value.getMetaUid(),
                                    ((String) value.getValue()).toLowerCase()));
                            break;
                        default:
                            doc.add(IndexHelper.getUnanalyzedField("MetaData" + value.getMetaUid(), value.getValue()));
                            break;
                    }
                }
            }
            for (String attribute : document.getAttributes().keySet()) {
                doc.add(IndexHelper.getUnanalyzedField("Attribute_" + attribute.toUpperCase(),
                        document.getAttributes().get(attribute).getValue()));
            }
            Object body = null;
            try {
                IndexFilter filter = FiltersMapper.getInstance().getFiltersFor(document.getExtension());
                if (filter != null) {
                    body = filter.getBody(version.getInputStream());
                }
            } catch (Throwable ex) {
                log.debug("Error while getting body", ex);
            }
            if (body == null) {
                body = IndexHelper.EMPTY_STRING;
            }
            if (body instanceof String) {
                doc.add(IndexHelper.getAnalyzedNotStoredField("DocumentBody", (String) body));
            } else {
                doc.add(IndexHelper.getAnalyzedNotStoredFromReaderField("DocumentBody", (Reader) body));
            }

            List<DMEntityACL> acls =
                    org.kimios.kernel.security.FactoryInstantiator.getInstance().getDMEntitySecurityFactory()
                            .getDMEntityACL(document);
            for (int i = 0; i < acls.size(); i++) {
                doc.add(IndexHelper.getUnanalyzedField("DocumentACL", acls.get(i).getRuleHash()));
            }
            this.indexModifier.addDocument(doc);
            commit();
        } catch (IOException io) {
            throw new IndexException(io,
                    "An exception occured while indexing document " + documentEntity.getUid() + " : " +
                            io.getMessage());
        }
    }

    public void close() throws IndexException
    {
        try {
            commit();
            indexModifier.close();
        } catch (IOException io) {
            throw new IndexException(io, io.getMessage());
        }
    }

    /*
    * TODO: Review collector (create one collector for unique result...
    */

    public List<Integer> executeQuery(Query query) throws IndexException
    {
        IndexReader reader = this.getIndexReader();
        IndexSearcher searcher = new IndexSearcher(reader);
        try {
            final List<Integer> list = new Vector<Integer>();
            TopDocs topDoc = searcher.search(query, Integer.MAX_VALUE);
            for (ScoreDoc dc : topDoc.scoreDocs) {
                list.add(dc.doc);
            }
            return list;
        } catch (IOException io) {
            throw new IndexException(io, io.getMessage());
        } finally {

            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                throw new IndexException(e, e.getMessage());
            }
        }
    }

    public void updateAcls(long docUid, List<DMEntityACL> acls) throws IndexException
    {
        IndexReader reader = null;
        try {
            reader = IndexReader.open(this.indexDirectory);
            log.trace("Updating ACL for document #" + docUid);
            Query q = new DocumentUidClause(docUid).getLuceneQuery();
            List<Integer> list = this.executeQuery(q);
            if (list.size() > 0) {
                org.apache.lucene.document.Document d = reader.document(list.get(0));
                this.indexModifier.deleteDocuments(q);
                d.removeFields("DocumentACL");

                for (int j = 0; j < acls.size(); j++) {
                    d.add(IndexHelper.getUnanalyzedField("DocumentACL", acls.get(j).getRuleHash()));
                }

                this.indexModifier.addDocument(d);
            }
        } catch (Exception e) {
            throw new IndexException(e, e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                throw new IndexException(e, e.getMessage());
            }
        }
    }

    public void commit() throws IndexException
    {
        try {
            this.indexModifier.forceMergeDeletes();
            this.indexModifier.commit();
        } catch (Exception e) {
            throw new IndexException(e, e.getMessage());
        }
    }

    public void deletePath(String path) throws IndexException
    {
        IndexReader reader = null;
        try {

            log.debug("Path delete: " + path);
            if (path.endsWith("/")) {
                path = path.substring(0, path.lastIndexOf("/"));
            }
            reader = IndexReader.open(this.indexDirectory);
            Query q = new DocumentParentClause(path).getLuceneQuery();
            this.indexModifier.deleteDocuments(q);
            reader.close();
            commit();
        } catch (Exception ex) {
            throw new IndexException(ex, ex.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                throw new IndexException(e, "Error while closing reader");
            }
        }
    }

    public void updatePath(String oldPath, String newPath) throws IndexException
    {
        IndexReader reader = null;
        try {
            if (oldPath.endsWith("/")) {
                oldPath = oldPath.substring(0, oldPath.lastIndexOf("/"));
            }
            if (!newPath.endsWith("/")) {
                newPath += "/";
            }
            reader = IndexReader.open(this.indexDirectory);
            Query q = new DocumentParentClause(oldPath).getLuceneQuery();
            List<Integer> list = this.executeQuery(q);
            Vector<org.apache.lucene.document.Document> docs = new Vector<org.apache.lucene.document.Document>();
            for (int i = 0; i < list.size(); i++) {
                docs.add(reader.document(list.get(i)));
            }
            this.indexModifier.deleteDocuments(q);
            for (int i = 0; i < docs.size(); i++) {
                String path = docs.get(i).get("DocumentParent");
                path = newPath + path.substring(oldPath.length() + 1);
                docs.get(i).removeField("DocumentParent");
                docs.get(i).add(IndexHelper.getUnanalyzedField("DocumentParent", path));
                this.indexModifier.addDocument(docs.get(i));
            }
            reader.close();
            commit();
        } catch (Exception ex) {
            throw new IndexException(ex, ex.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                throw new IndexException(e, "Error while closing reader");
            }
        }
    }

    public IndexReader getIndexReader() throws IndexException
    {
        try {
            return IndexReader.open(this.indexDirectory);
        } catch (IOException io) {
            throw new IndexException(io, io.getMessage());
        }
    }

    @Override
    public void updateAcls(long docUid, List<DMEntityACL> acls, boolean commit) throws IndexException {
        this.updateAcls(docUid, acls);
    }


}

