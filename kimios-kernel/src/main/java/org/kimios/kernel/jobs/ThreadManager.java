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
package org.kimios.kernel.jobs;

import org.kimios.kernel.security.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ThreadManager {

    private static ThreadManager manager;

    private KimiosExecutor executor;

    private static Logger logger = LoggerFactory.getLogger(ThreadManager.class);

    synchronized public static ThreadManager getInstance() throws RuntimeException {
        if (manager == null) {
            try {
                manager = new ThreadManager();
            }catch (Exception ex){
                throw new RuntimeException(ex);
            }
        }

        return manager;
    }

    private ThreadManager() throws Exception  {
        executor = KimiosExecutorBuilder.build("kimios");
    }

    public void startJob(Session session, Job job) {
        job.setSession(session);
        executor.submit(job);
    }


    public void shutDown() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }


    public List listRunningJob() {
        if (executor != null) {
            List<Callable> items = executor.listTasks();
            for(Callable r: items){
                if(r instanceof Job){
                    logger.info("loaded kimios task {} ==> {}", ((Job) r).getTaskId(), r);
                }  else {
                    logger.info("task isn't kimios task ==> {}", r);
                }
            }
            return items;
        }
        return new ArrayList();
    }

    public Job taskById(String id) {
        if (executor != null) {
            Callable r = executor.tasksById(id);
            if(r instanceof Job){
                return (Job)r;
            } else {
                logger.info("task is null or not kimios task {}", r);
                return null;
            }
        }
        return null;
    }
}

