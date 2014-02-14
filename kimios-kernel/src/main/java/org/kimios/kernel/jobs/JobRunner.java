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

@Deprecated
public class JobRunner extends Thread
{
    private Job myJob;

    private Object[] params;

    private Session session;

    protected JobRunner(ThreadGroup group, Job job, String name, Session session, Object... params)
    {
        super(group, name);
        this.myJob = job;
        this.params = params;
        this.session = session;
    }

    @Override
    public void run()
    {
        this.myJob.setStatus(Job.PROCESSING);
        try {
            this.myJob.execute(this.session, params);
            this.myJob.setStatus(Job.FINISHED);
        } catch (Exception e) {
            e.printStackTrace();
            this.myJob.setStatus(Job.STOPPED_IN_ERROR);
            this.myJob.setStackTrace(e);
        }
    }

    public int getStatus()
    {
        return this.myJob.getStatus();
    }
}

