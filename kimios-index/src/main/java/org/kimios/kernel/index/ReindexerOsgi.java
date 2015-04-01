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
import org.kimios.kernel.controller.IPathController;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.dms.DMEntityType;
import org.kimios.kernel.dms.FactoryInstantiator;
import org.kimios.kernel.index.query.model.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;

public class ReindexerOsgi implements Callable<ReindexerOsgi.ReindexResult> {



    public class ReindexResult{

        private long reindexedCount;
        private long duration;
        private int entitiesCount;
        private Exception exception;

        public ReindexResult(long reindexedCount, long duration, int entitiesCount, Exception ex) {
            this.reindexedCount = reindexedCount;
            this.duration = duration;
            this.exception = ex;
            this.entitiesCount = entitiesCount;
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

        @Override
        public String toString() {
            return "ReindexResult{" +
                    "reindexedCount=" + reindexedCount +
                    ", duration=" + (duration/1000/60) + " min." + ((duration/1000) % 60) + " secs." +
                    ", entitiesCount=" + entitiesCount +
                    ", exception=" + exception +
                    '}';
        }
    }

    private static Logger log = LoggerFactory.getLogger(ReindexerOsgi.class);

    private int reindexProgression = -1;

    private String finalPath;

    private ISolrIndexManager indexManager;

    private IPathController pathController;

    private int blockSize;

    public ReindexerOsgi(ISolrIndexManager indexManager, IPathController pathController, String path, int blockSize) {
        this.indexManager = indexManager;
        this.finalPath = path;
        this.pathController = pathController;
        this.blockSize = blockSize;
    }


    private ReindexResult reindexResult;

    public ReindexResult getReindexResult() {
        return reindexResult;
    }

    public void setReindexResult(ReindexResult reindexResult) {
        this.reindexResult = reindexResult;
    }

    public ReindexResult call() {

        int indexed = 0;
        int total = 0;
        long start = System.currentTimeMillis();
        long duration = 0;
        ReindexResult r = new ReindexResult(indexed, duration, total, null);
        this.reindexResult = r;
        try {

            reindexProgression = 0;

            /*
                    Delete items
             */
            String indexPath = this.finalPath != null ? this.finalPath.trim() : "/";
            try {
                String fPath = ClientUtils.escapeQueryChars(indexPath);
                if(fPath.endsWith(ClientUtils.escapeQueryChars("/")))
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
            total =   FactoryInstantiator.getInstance()
                            .getDmEntityFactory()
                            .getEntitiesByPathAndTypeCount(finalPath, DMEntityType.DOCUMENT)
                            .intValue();

            //total = entities.size();
            log.debug("Entities to index: " + total);
            r.setEntitiesCount(total);
            int documentBlockSize = blockSize;
            int indexingBlockCount = total / documentBlockSize;
            int docLeak = total % documentBlockSize;

            if(docLeak > 0)
                indexingBlockCount++;


            log.info("Reindexing " + total + " documents: block size " + documentBlockSize + "  / blcok count " + indexingBlockCount);

            /*
            log.info("Reindexing documents count {} by block of {} ({} blocks, with {} leak)", total,documentBlockSize, indexingBlockCount, docLeak);
               Sub listing

            log.info("Reindexing: " + documentBlockSize + " / " + indexingBlockCount + " / " + docLeak);
            List<List<DMEntity>> blockItems = new ArrayList<List<DMEntity>>();
            for (int cbl = 0; cbl < indexingBlockCount; cbl++) {
                blockItems.add(
                        entities.subList(cbl * documentBlockSize,
                                (cbl * documentBlockSize)
                                + ( (docLeak > 0 && cbl == (indexingBlockCount -1)) ? docLeak : documentBlockSize))
                );
            }    */
            /*
               Add remaining docs

            log.info("Remaining docs: " + docLeak);
            if (docLeak > 0) {
                List<DMEntity> remaining = new ArrayList<DMEntity>();
                for (int u = (entities.size() - docLeak);
                     u < entities.size(); u++) {
                    remaining.add(entities.get(u));
                }
                blockItems.add(remaining);
            }      */
            th.loadTxManager().getTransaction().rollback();

            for (int u = 0; u < indexingBlockCount; u++) {
                th.loadTxManager().begin();

                List<DMEntity> entityList = FactoryInstantiator.getInstance()
                        .getDmEntityFactory().getEntitiesByPathAndType(finalPath, DMEntityType.DOCUMENT, u * documentBlockSize, ((docLeak > 0 && u == (indexingBlockCount - 1)) ? docLeak : documentBlockSize));
                indexManager.indexDocumentList(entityList);
                indexed += entityList.size();
                r.setReindexedCount(indexed);
                th.loadTxManager().commit();

                if(reindexProgression < 100){
                    reindexProgression = (int) Math.round((double) indexed / (double) total * 100);
                }

            }
        } catch (Exception ex) {
            log.error("Exception during reindex! Process stopped", ex);
            r.setException(ex);
        } finally {
            try {
                new OsgiTransactionHelper().commit();
            } catch (Exception e) {
                //
            }
            duration = System.currentTimeMillis() - start;
            r.setDuration(duration);
            r.setEntitiesCount(total);
            r.setReindexedCount(indexed);
            reindexProgression = -1;
            return r;
        }
    }

    public int getReindexProgression() {
        return reindexProgression;
    }

    public void setReindexProgression(int reindexProgression) {
        this.reindexProgression = reindexProgression;
    }
}
