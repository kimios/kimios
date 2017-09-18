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
package org.kimios.kernel.notifier.jobs;

import org.kimios.kernel.jobs.Job;
import org.kimios.kernel.jobs.KimiosExecutor;
import org.kimios.kernel.jobs.KimiosExecutorBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotifierThreadManager {

    private static NotifierThreadManager manager;

    private KimiosExecutor executor;

    private static Logger logger = LoggerFactory.getLogger(NotifierThreadManager.class);

    synchronized public static NotifierThreadManager getInstance() throws RuntimeException {
        if (manager == null) {
            try {
                manager = new NotifierThreadManager();
            }catch (Exception ex){
                throw new RuntimeException(ex);
            }
        }

        return manager;
    }

    private NotifierThreadManager() throws Exception  {
        executor = KimiosExecutorBuilder.build("kimios Notifier");
    }

    public void startJob(Job job) {
        executor.submit(job);
    }


    public void shutDown() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }
}
