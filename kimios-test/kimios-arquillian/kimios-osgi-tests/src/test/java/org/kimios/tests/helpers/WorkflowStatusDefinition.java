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
package org.kimios.tests.helpers;


import org.kimios.kernel.ws.pojo.WorkflowStatus;
import org.kimios.kernel.ws.pojo.WorkflowStatusManager;

/**
 * The workflow status definition
 *
 * @author Fabien Alin
 */
public class WorkflowStatusDefinition
{

    private WorkflowStatus workflowStatus;

    private long uid;

    private long workflowUid;

    private String name;

    private long successorUid;

    private int position;


    private WorkflowStatusManager[] workflowStatusManagers;

    public WorkflowStatus getWorkflowStatus()
    {
        return workflowStatus;
    }

    public void setWorkflowStatus( WorkflowStatus workflowStatus )
    {
        this.workflowStatus = workflowStatus;
        this.uid = workflowStatus.getUid();
        this.name = workflowStatus.getName();
        this.successorUid = workflowStatus.getSuccessorUid();
        this.workflowUid = workflowStatus.getUid();
    }

    public WorkflowStatusManager[] getWorkflowStatusManagers()
    {
        return workflowStatusManagers;
    }

    public void setWorkflowStatusManagers( WorkflowStatusManager[] workflowStatusManagers )
    {
        this.workflowStatusManagers = workflowStatusManagers;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public long getSuccessorUid()
    {
        return successorUid;
    }

    public void setSuccessorUid( long successorUid )
    {
        this.successorUid = successorUid;
    }

    public long getUid()
    {
        return uid;
    }

    public void setUid( long uid )
    {
        this.uid = uid;
    }

    public long getWorkflowUid()
    {
        return workflowUid;
    }

    public void setWorkflowUid( long workflowStatusUid )
    {
        this.workflowUid = workflowStatusUid;
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition( int position )
    {
        this.position = position;
    }
}

