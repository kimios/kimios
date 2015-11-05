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
package org.kimios.kernel.ws.pojo;

public class WorkflowStatus
{
    private long uid;

    private String name;

    private long successorUid;

    private long workflowUid;

    public WorkflowStatus()
    {

    }

    public WorkflowStatus(long uid, String name, long successorUid,
            long workflowUid)
    {
        this.uid = uid;
        this.name = name;
        this.successorUid = successorUid;
        this.workflowUid = workflowUid;
    }

    public long getUid()
    {
        return uid;
    }

    public void setUid(long uid)
    {
        this.uid = uid;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public long getSuccessorUid()
    {
        return successorUid;
    }

    public void setSuccessorUid(long successorUid)
    {
        this.successorUid = successorUid;
    }

    public long getWorkflowUid()
    {
        return workflowUid;
    }

    public void setWorkflowUid(long workflowUid)
    {
        this.workflowUid = workflowUid;
    }
}

