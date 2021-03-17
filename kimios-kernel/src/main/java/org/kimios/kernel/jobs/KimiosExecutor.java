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

;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by farf on 09/02/16.
 */
public class KimiosExecutor extends ThreadPoolExecutor {



    private static Logger log = LoggerFactory.getLogger(KimiosExecutor.class);

    public KimiosExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public KimiosExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public KimiosExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public KimiosExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }


    //ThreadPoolExecutor override

    private Map<String, Callable> running = Collections.synchronizedMap(new HashMap<String, Callable>());


    public List<Callable> listTasks(){
        return new ArrayList<Callable>(running.values());
    }

    public Callable tasksById(String id){
        return running.get(id);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new CustomFutureTask<T>(callable);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        log.info("before executing task {}", r);
        if(r instanceof CustomFutureTask && ((CustomFutureTask) r).unwrap() instanceof Job){
            Job job = (Job)((CustomFutureTask) r).unwrap();
            running.put(job.getTaskId(), job);
            log.info("added task {} ==> id {}", job, job.getTaskId());
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        log.info("after executing task {}", r);
        if(r instanceof CustomFutureTask && ((CustomFutureTask) r).unwrap() instanceof Job){
            Job job = (Job)((CustomFutureTask) r).unwrap();
            running.remove(job.getTaskId());
            log.info("removed task {} ==> id {}", job, job.getTaskId());
        }
    }



    @Override
    public <T> Future<T> submit(Callable<T> task) {
        final long startTime = System.currentTimeMillis();
        WrapCallable<T> item = new WrapCallable<T>(
                startTime,
                task,
                clientTrace(),
                Thread.currentThread().getName()
        );
        return super.submit(item);
    }

    private Exception clientTrace() {
        return new Exception("Client stack trace");
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return super.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return super.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return super.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return super.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return super.submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return super.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return super.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return super.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return super.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        super.execute(command);
    }

//...


}
