/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * Created by farf on 09/02/16.
 */
public class WrapCallable<V> implements Callable<V> {


    private static Logger log = LoggerFactory.getLogger(WrapCallable.class);

    private long startTime;
    private Callable<V> task;
    private Exception clientStack;
    private String clientThreadName;

    public WrapCallable(long startTime, Callable<V> task, final Exception clientStack, String clientThreadName){
        this.startTime = startTime;
        this.task = task;
        this.clientStack = clientStack;
        this.clientThreadName = clientThreadName;
    }

    @Override
    public V call() throws Exception {
        Thread.currentThread().setName(Thread.currentThread().getName() + "-" + ((Job)this.task).getTaskId());
        final long queueDuration = System.currentTimeMillis() - startTime;
        log.debug("Task {} spent {}ms in queue", task, queueDuration);
        //update Name:
        try {
            return task.call();
        } catch (Exception e) {
            log.error("Exception {} in task submitted from thread {} here:", e, clientThreadName, clientStack);
            throw e;
        }
    }


    public Callable<V> unwrap(){
        return this.task;
    }
}
