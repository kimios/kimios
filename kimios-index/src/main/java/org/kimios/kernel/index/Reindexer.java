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
package org.kimios.kernel.index;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.kimios.kernel.controller.IPathController;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.dms.DMEntityType;
import org.kimios.kernel.dms.FactoryInstantiator;
import org.kimios.kernel.index.query.model.SearchResponse;
import org.kimios.utils.spring.TransactionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;

/**
 * Created with IntelliJ IDEA. User: farf Date: 12/10/12 Time: 9:52 PM To change this template use File | Settings |
 * File Templates.
 */
public class Reindexer implements Runnable {
    private static Logger log = LoggerFactory.getLogger(Reindexer.class);

    private int reindexProgression = -1;

    private String finalPath;

    private ISolrIndexManager indexManager;

    private IPathController pathController;

    public Reindexer(ISolrIndexManager indexManager, IPathController pathController, String path) {
        this.indexManager = indexManager;
        this.finalPath = path;
        this.pathController = pathController;
    }

    public void run() {
        TransactionStatus status = null;
        try {

            reindexProgression = 0;
            int indexed = 0;

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
                return;
            }
            status = new TransactionHelper().startNew(null);
            List<DMEntity> entities =
                    FactoryInstantiator.getInstance()
                            .getDmEntityFactory()
                            .getEntitiesByPathAndType(finalPath, DMEntityType.DOCUMENT);
            int total = entities.size();
            log.debug("Entities to index: " + total);

            int documentBlockSize = 20;
            int indexingBlockCount = entities.size() / documentBlockSize;
            int docLeak = entities.size() % documentBlockSize;
            /*
               Sub listing
            */
            log.info("Reindexing: " + documentBlockSize + " / " + indexingBlockCount + " / " + docLeak);
            List<List<DMEntity>> blockItems = new ArrayList<List<DMEntity>>();
            for (int cbl = 0; cbl < indexingBlockCount; cbl++) {
                blockItems.add(
                        entities.subList(cbl * documentBlockSize,
                                (cbl * documentBlockSize) + documentBlockSize)
                );
            }
            /*
               Add remaining docs
            */
            log.info("Remaining docs: " + docLeak);
            if (docLeak > 0) {
                List<DMEntity> remaining = new ArrayList<DMEntity>();
                for (int u = (entities.size() - docLeak);
                     u < entities.size(); u++) {
                    remaining.add(entities.get(u));
                }
                blockItems.add(remaining);
            }

            for (List<DMEntity> entityList : blockItems) {
                indexManager.indexDocumentList(entityList);
                indexed += entityList.size();
                reindexProgression = (int) Math.round((double) indexed / (double) total * 100);
            }
            reindexProgression = -1;
        } catch (Exception ex) {
            log.error("Exception during reindex! Process stopped", ex);
        } finally {
            try {
                new TransactionHelper().rollback(status);
            } catch (Exception e) {
                log.error("Error while rollbacking transaction", e);
            }
        }
    }

    public int getReindexProgression() {
        return reindexProgression;
    }

    public void setReindexProgression(int reindexProgression) {
        this.reindexProgression = reindexProgression;
    }
}
