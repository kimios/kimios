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

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.dms.DMEntityType;
import org.kimios.kernel.dms.FactoryInstantiator;
import org.kimios.kernel.index.query.model.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class ReindexerProcess implements Callable<ReindexerProcess.ReindexResult> {


    private int indexed;
    private int total;
    private long start;
    private long duration;
    private List<Long> excludedIds = null;

    public class ReindexResult {

        private long reindexedCount;
        private long duration;
        private int entitiesCount;
        private int reindexProgression = 0;
        private Exception exception;
        private String path;

        public ReindexResult(String path, long reindexedCount, long duration, int entitiesCount, Exception ex) {
            this.path = path;
            this.reindexedCount = reindexedCount;
            this.duration = duration;
            this.exception = ex;
            this.entitiesCount = entitiesCount;
        }

        public int getReindexProgression() {
            return reindexProgression;
        }

        public void setReindexProgression(int reindexProgression) {
            this.reindexProgression = reindexProgression;
        }

        public long getReindexedCount() {
            return reindexedCount;
        }

        public void setReindexedCount(long reindexedCount) {
            this.reindexedCount = reindexedCount;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public Exception getException() {
            return exception;
        }

        public void setException(Exception exception) {
            this.exception = exception;
        }

        public int getEntitiesCount() {
            return entitiesCount;
        }

        public void setEntitiesCount(int entitiesCount) {
            this.entitiesCount = entitiesCount;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        @Override
        public String toString() {
            return "ReindexResult{" +
                    "reindexedCount=" + reindexedCount +
                    ", duration=" + duration +
                    ", entitiesCount=" + entitiesCount +
                    ", exception=" + exception +
                    ", path='" + path + '\'' +
                    '}';
        }
    }

    private static Logger log = LoggerFactory.getLogger(ReindexerProcess.class);

    private int reindexProgression = -1;

    private String finalPath;

    private ISolrIndexManager indexManager;

    private int blockSize;

    private List<String> extensionsExcluded;

    private Long threadReadTimeOut;

    private TimeUnit threadReadTimeoutTimeUnit;

    private boolean updateDocsMetaWrapper;

    private boolean disableThreading;

    private int threadPoolSize;

    public ReindexerProcess(ISolrIndexManager indexManager, String path, int blockSize, List<Long> excludedIds, List<String> extensionsExcluded,
                            Long threadReadTimeOut, TimeUnit threadReadTimeoutTimeUnit, int threadPoolSize,
                            boolean updateDocsMetaWrapper,
                            boolean disableThreading) {
        this.indexManager = indexManager;
        this.finalPath = path;
        this.blockSize = blockSize;
        indexed = 0;
        total = 0;
        start = System.currentTimeMillis();
        duration = 0;
        this.reindexResult = new ReindexResult(finalPath, indexed, duration, total, null);
        this.excludedIds = excludedIds;
        this.threadReadTimeOut = threadReadTimeOut;
        this.threadReadTimeoutTimeUnit = threadReadTimeoutTimeUnit;
        this.extensionsExcluded = extensionsExcluded;
        this.updateDocsMetaWrapper = updateDocsMetaWrapper;
        this.disableThreading = disableThreading;
        this.threadPoolSize = threadPoolSize;
    }


    private ReindexResult reindexResult;

    public ReindexResult getReindexResult() {
        return reindexResult;
    }

    public void setReindexResult(ReindexResult reindexResult) {
        this.reindexResult = reindexResult;
    }

    public ReindexResult call() {

        try {

            reindexProgression = 0;

            /*
                    Delete items
             */
            String indexPath = this.finalPath != null ? this.finalPath.trim() : "/";
            try {
                String fPath = ClientUtils.escapeQueryChars(indexPath);
                if (fPath.endsWith(ClientUtils.escapeQueryChars("/")))
                    indexPath = fPath + "*";
                else
                    indexPath = fPath;
                SearchResponse re = indexManager.executeSolrQuery(new SolrQuery("DocumentPath:" + indexPath));
                log.info("Process will update " + re.getResults() + " documents for path " + indexPath);
                indexManager.deleteByQuery("DocumentPath:" + indexPath);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                log.info("Incorrect Path, or index process error for " + indexPath + ". Reindex process canceled.");
                throw new Exception("Incorrect Path, or index process error for " + indexPath + ". Reindex process canceled.");
            }
            OsgiTransactionHelper th = new OsgiTransactionHelper();
            th.startNew(null);
            //List<DMEntity> entities =

            total = FactoryInstantiator.getInstance()
                    .getDmEntityFactory()
                    .getEntitiesByPathAndTypeCount(finalPath, DMEntityType.DOCUMENT, excludedIds, extensionsExcluded)
                    .intValue();
            //total = entities.size();
            log.debug("Entities to index: " + total);
            this.reindexResult.setEntitiesCount(total);
            int documentBlockSize = blockSize;
            int indexingBlockCount = total / documentBlockSize;
            int docLeak = total % documentBlockSize;

            if (docLeak > 0)
                indexingBlockCount++;

            log.debug("Reindexing " + total + " documents: block size " + documentBlockSize + "  / blcok count " + indexingBlockCount);

            th.loadTxManager().getTransaction().rollback();

            for (int u = 0; u < indexingBlockCount; u++) {
                th.loadTxManager().begin();

                List<DMEntity> entityList = null;
                entityList = FactoryInstantiator.getInstance()
                        .getDmEntityFactory().getEntitiesByPathAndType(finalPath, DMEntityType.DOCUMENT, u * documentBlockSize,
                                ((docLeak > 0 && u == (indexingBlockCount - 1)) ? docLeak : documentBlockSize),
                                excludedIds,
                                extensionsExcluded);
                try {
                    if (threadReadTimeoutTimeUnit != null && threadReadTimeOut != null) {
                        indexManager.threadedIndexDocumentList(entityList, threadReadTimeOut, threadReadTimeoutTimeUnit,
                                updateDocsMetaWrapper, threadPoolSize, disableThreading);
                    } else
                        indexManager.indexDocumentList(entityList);
                    indexed += entityList.size();
                } catch (Exception ex) {
                    log.error("an error happen during indexing for block {} / {}", u + 1, indexingBlockCount);
                }

                this.reindexResult.setReindexedCount(indexed);
                if(updateDocsMetaWrapper)
                    th.loadTxManager().commit();
                else
                    th.loadTxManager().rollback();


                if (reindexProgression < 100) {
                    reindexProgression = (int) Math.round((double) indexed / (double) total * 100);

                }
                this.reindexResult.setReindexProgression(reindexProgression);


                if (Thread.interrupted()) {
                    log.info("reindex thread for path {} has been canceled", finalPath);
                    return this.reindexResult;
                }

            }
        } catch (Exception ex) {
            log.error("Exception during reindex! Process stopped", ex);
            this.reindexResult.setException(ex);
        } finally {
            try {
                new OsgiTransactionHelper().commit();
            } catch (Exception e) {
                //
            }
            duration = System.currentTimeMillis() - start;
            this.reindexResult.setDuration(duration);
            this.reindexResult.setEntitiesCount(total);
            this.reindexResult.setReindexedCount(indexed);
            reindexProgression = -1;
            return this.reindexResult;
        }
    }

    public int getReindexProgression() {
        return reindexProgression;
    }

    public void setReindexProgression(int reindexProgression) {
        this.reindexProgression = reindexProgression;
    }
}
