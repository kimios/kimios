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
import org.slf4j.LoggerFactory;

public abstract class JobImpl<T> implements Job
{
    private Session session;

    protected Exception exception;

    private int status;

        public final void setSession(Session session)
    {
        this.session = session;
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

    abstract public Object getInformation() throws Exception;

    public final Session getUserSession()
    {
        return this.session;
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

}

