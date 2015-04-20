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

import org.kimios.kernel.index.ReindexerProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by farf on 19/04/15.
 */
public class CustomThreadPoolExecutor extends ThreadPoolExecutor {

    private static Logger logger = LoggerFactory.getLogger(CustomThreadPoolExecutor.class);

    private static final RejectedExecutionHandler defaultHandler =
            new AbortPolicy();



    public CustomThreadPoolExecutor(int corePoolSize,
                                    int maximumPoolSize,
                                    long keepAliveTime,
                                    TimeUnit unit,
                                    BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                Executors.defaultThreadFactory(), defaultHandler);

        items = new HashMap<String, ReindexerProcess>();
    }


    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        logger.info("ending thread {}", r);
        if(t != null){
            logger.error("error while ending runnable", t);
        }

    }

    @Override
    protected void terminated() {
        //clean list of data ?
        items = null;
    }


    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
    }

    private Map<String, ReindexerProcess> items = null;

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        if(items == null){
            items = new HashMap<String, ReindexerProcess>();
        }
        Future<T> item = super.submit(task);
        if(task instanceof ReindexerProcess){
            items.put(((ReindexerProcess) task).getReindexResult().getPath(), (ReindexerProcess)task);
        }
        return item;
    }

    public Map<String, ReindexerProcess> getItems() {
        return items;
    }

    public void setItems(Map<String, ReindexerProcess> items) {
        this.items = items;
    }
}
