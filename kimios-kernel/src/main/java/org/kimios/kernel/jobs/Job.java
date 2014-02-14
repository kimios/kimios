/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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

public interface Job extends Runnable
{
    public static final int FINISHED = 0;
    public static final int STOPPED_IN_ERROR = 2;
    public static final int PROCESSING = 1;

    /*
    *  Set the security properties of the job, ie the sessionUid
    */
    public void setSession(Session session);

    /*
    *  Start
    */
    public Object execute(Session session, Object... params) throws Exception;

    /*
    *  Return the session owner of the started job
    */
    public Session getUserSession();

    /*
    *  Return informations about the processing job
    */
    public Object getInformation() throws Exception;

    /*
    *  Set the running status of the job
    */
    public void setStatus(int status);

    /*
    *  Get the running status of the job
    */
    public int getStatus();

    /*
    *  Set the exception statck trace of the job
    */
    public void setStackTrace(Exception e);

    /*
    *  Throw the catched exception
    */
    public void throwException() throws Exception;

    /*
    *  Set launch parameters
    *
    */
    public void setParams(Object[] params);
}

