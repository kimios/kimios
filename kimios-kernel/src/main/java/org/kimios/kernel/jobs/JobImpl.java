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

import org.kimios.kernel.dms.model.DMEntity;
import org.kimios.kernel.security.model.Session;
import org.slf4j.LoggerFactory;

public abstract class JobImpl<T> implements Job<T>
{
    private Session session;

    protected Exception exception;

    private int status;

    private String user;

    protected DMEntity dmEntity;

    public JobImpl(String taskId){
        this.taskId = taskId;
    }

    @Override
    public String getUser() {
        return user;
    }

    public final void setSession(Session session)
    {

        this.session = session;
        if(session != null){
            this.user  = session.getUserName() + "@" + session.getUserSource();
        }
    }

    public T call()
    {
        status = PROCESSING;
        try {
            T ret =  execute();
            status = FINISHED;
            return ret;
        } catch (Exception e) {
            LoggerFactory.getLogger(this.getClass().getName()).error("exception during job execution", e);
            status = STOPPED_IN_ERROR;
            setStackTrace(e);
        }
        return null;
    }

    abstract public T execute() throws Exception;

    public Object getInformation() throws Exception{
        StringBuilder builder = new StringBuilder();
        builder.append("jobId: ");
        builder.append(this.getTaskId());
        builder.append("\n");
        builder.append("jobType: ");
        builder.append("\n");
        builder.append(this.getClass().getName());
        builder.append("\n");
        builder.append("Run on entity ? ");
        if(dmEntity != null){
            builder.append("#");
            builder.append(dmEntity.getUid());
            builder.append(" Path: ");
            builder.append(dmEntity.getPath());
        } else {
            builder.append("N/A");
        }
        builder.append("\n");
        builder.append("\n");
        return builder;
    }

    public final Session getUserSession()
    {
        return this.session;
    }

    public final String user(){
        return this.user;
    }

    public final void setStackTrace(Exception e)
    {
        this.exception = e;
    }

    public final void setStatus(int status)
    {
        this.status = status;
    }

    public final int getStatus()
    {
        return this.status;
    }

    public final void throwException() throws Exception
    {
        throw exception;
    }

    private String taskId;

    @Override
    public String toString() {
        return "Job{" +
                "ll=" +taskId +
                '}';
    }

    public String getTaskId(){
        return this.taskId;
    }

    public void setDmEntity(DMEntity entity){
        this.dmEntity = entity;
    }

}

