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

package org.kimios.kernel.controller.impl;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.ISearchManagementController;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.IndexException;
import org.kimios.kernel.index.ISolrIndexManager;
import org.kimios.kernel.index.ReindexerProcess;
import org.kimios.kernel.index.SolrIndexManager;
import org.kimios.kernel.security.Role;
import org.kimios.kernel.security.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Search Management Controller
 */
@Transactional
public class SearchManagementController extends AKimiosController implements ISearchManagementController {


    private static Logger log = LoggerFactory.getLogger(SearchManagementController.class);

    private ISolrIndexManager indexManager;

    public ISolrIndexManager getIndexManager() {
        return indexManager;
    }

    public void setIndexManager(ISolrIndexManager indexManager) {
        this.indexManager = indexManager;
    }

    /* (non-Javadoc)
        * @see org.kimios.kernel.controller.impl.IAdministrationController#reindex(org.kimios.kernel.security.Session, java.lang.String)
        */
    public void reindex(Session session, String path)
            throws AccessDeniedException, IndexException, ConfigException, DataSourceException {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.ADMIN, session.getUserName(), session.getUserSource()) != null) {
            indexManager.reindex(path);
        } else {
            throw new AccessDeniedException();
        }
    }


    private ExecutorService executor = null;

    synchronized public void parallelReindex(Session session, List<String> paths,
                                             List<Long> excludedIds,
                                             List<String> excludedExtensions,
                                             Integer blockSize,
                                             Long readFileTimeOut,
                                             TimeUnit readFileTimeoutUnit,
                                             int threadPoolSize,
                                             boolean updateDocsMetaWrapper,
                                             boolean disableThreading,
                                             int entityType)
            throws AccessDeniedException, IndexException, ConfigException, DataSourceException {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.ADMIN, session.getUserName(), session.getUserSource()) != null) {

            int block = blockSize != null && blockSize > 0
                    ? blockSize : 20;


            List<String> finalPaths = null;
            if (executor == null || executor.isTerminated()) {
                executor = new CustomThreadPoolExecutor(8, 8,
                        0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>());

                finalPaths = paths;

            } else {
                //check existing running task
                Map<String, ReindexerProcess> reindexResults = ((CustomThreadPoolExecutor) executor).getItems();
                finalPaths = new ArrayList<String>();
                for (String iPath : paths) {
                    if(!reindexResults.keySet().contains(iPath)){
                        finalPaths.add(iPath);
                        log.info("will start reindex for {}", iPath);
                    } else {
                        log.error("path {} is already processed by index: stage {}%", iPath, reindexResults.get(iPath).getReindexProgression());
                    }
                }
            }
            for (String u : finalPaths) {
                ReindexerProcess osgiReindexer = new ReindexerProcess(
                        indexManager,
                        u,
                        block,
                        excludedIds,
                        excludedExtensions,
                        readFileTimeOut,
                        readFileTimeoutUnit,
                        threadPoolSize,
                        updateDocsMetaWrapper,
                        disableThreading,
                        entityType
                );
                executor.submit(osgiReindexer);
            }

        } else {
            throw new AccessDeniedException();
        }
    }


    public List<ReindexerProcess.ReindexResult> viewIndexingProcess(Session session)
            throws AccessDeniedException, IndexException, ConfigException, DataSourceException {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.ADMIN, session.getUserName(), session.getUserSource()) != null) {
            if (executor != null && !executor.isTerminated()) {

                Map<String, ReindexerProcess> reindexResults = ((CustomThreadPoolExecutor) executor).getItems();
                List<ReindexerProcess.ReindexResult> values = new ArrayList<ReindexerProcess.ReindexResult>();
                for (ReindexerProcess rp : reindexResults.values()) {
                    values.add(rp.getReindexResult());
                }

                return values;
            } else {
                log.info("no index process are running.");
                return new ArrayList<ReindexerProcess.ReindexResult>();
            }
        } else {
            throw new AccessDeniedException();
        }
    }


    public void killAndCleanReindexProcess(Session session)
            throws AccessDeniedException, IndexException, ConfigException, DataSourceException {

        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.ADMIN, session.getUserName(), session.getUserSource()) != null) {
            if (executor != null) {
                List<Runnable> items = executor.shutdownNow();
                for (Runnable r : items) {
                    log.info("runnable item kill/removed {}", r);
                }
                executor = null;
            } else {
                log.info("no index process are running");

            }
        } else {
            throw new AccessDeniedException();
        }

    }


    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISearchManagementController#getReindexProgress(org.kimios.kernel.security.Session)
    */
    public int getReindexProgress(Session session)
            throws AccessDeniedException, IndexException, ConfigException, DataSourceException {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.ADMIN, session.getUserName(), session.getUserSource()) != null) {
            return indexManager.getReindexProgression();
        } else {
            throw new AccessDeniedException();
        }
    }

    @Override
    public List<String> listDocumentAvailableFields(Session session) throws AccessDeniedException, IndexException, ConfigException, DataSourceException {
        if (indexManager instanceof SolrIndexManager) {
            return ((SolrIndexManager) indexManager).filterFields();
        } else
            throw new AccessDeniedException();
    }
}
