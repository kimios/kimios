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

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by farf on 19/04/15.
 */
public class CustomSolrDocThreadPoolExecutor extends ThreadPoolExecutor {

    private static Logger logger = LoggerFactory.getLogger(CustomSolrDocThreadPoolExecutor.class);

    private static final RejectedExecutionHandler defaultHandler =
            new AbortPolicy();



    private SolrServer solr;

    public CustomSolrDocThreadPoolExecutor(int corePoolSize,
                                           int maximumPoolSize,
                                           long keepAliveTime,
                                           TimeUnit unit,
                                           BlockingQueue<Runnable> workQueue,
                                           SolrServer solr) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                Executors.defaultThreadFactory(), defaultHandler);
        this.solr = solr;
    }


    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        logger.trace("ending thread {}", r);
        //if ended, commit solr index
        super.afterExecute(r, t);
        /*

        if(t != null){
            logger.error("error while ending solr doc runnable", t);
        }
        if (t == null && r instanceof Future<?>) {
            try {
                Future<?> future = (Future<?>) r;
                if (future.isDone()) {

                    SolrInputDocument solrDoc = (SolrInputDocument)future.get();
                    String docId = solrDoc.getField("DocumentUid").getValue().toString();
                    try {
                        this.solr.deleteById(docId);
                        this.solr.add(solrDoc, 360000);
                    }catch (Exception ex){
                        logger.error("error while commiting doc #" + docId, ex);
                    }
                }
            } catch (CancellationException ce) {
                t = ce;
            } catch (ExecutionException ee) {
                t = ee.getCause();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt(); // ignore/reset
            }
        }      */
    }

    @Override
    protected void terminated() {

    }



}
