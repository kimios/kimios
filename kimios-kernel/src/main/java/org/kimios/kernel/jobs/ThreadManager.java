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

import org.kimios.kernel.security.Session;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadManager
{
    private static ThreadManager manager = new ThreadManager();

    private ThreadPoolExecutor executor;

    synchronized public static ThreadManager getInstance()
    {
        if (manager == null) {
            manager = new ThreadManager();
        }

        return manager;
    }

    private ThreadManager()
    {
        //TODO: manage pool executor conf
        executor = new ThreadPoolExecutor(1, 5, 10000, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100));
    }

    public void startJob(Session session, Job job, String name, Object... params)
    {
        //TODO: mange thread llist...(Extend thread pool executor ???)
        job.setParams(params);
        job.setSession(session);
        executor.execute(job);
    }
}

