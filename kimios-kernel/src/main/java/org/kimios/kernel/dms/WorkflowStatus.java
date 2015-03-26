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
package org.kimios.kernel.dms;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;

import javax.persistence.*;
import java.util.Vector;

@Entity
@Table(name = "workflow_status")
@SequenceGenerator(name = "seq", allocationSize = 1, sequenceName = "wkf_status_id_seq")
public class WorkflowStatus
{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    private long uid;

    @Column(name = "status_name", length = 200, nullable = false)
    private String name;

    @Column(name = "successor_id", nullable = true)
    private Long successorUid;

    @Column(name = "workflow_id", nullable = false)
    private long workflowUid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Workflow workflow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "successor_id", nullable = true, insertable = false, updatable = false)
    private WorkflowStatus successor;

    public WorkflowStatus()
    {
    }

    public WorkflowStatus(long uid, String name, Long successorUid, long workflowUid)
    {
        this.uid = uid;
        this.name = name;
        this.successorUid = successorUid;
        this.workflowUid = workflowUid;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public WorkflowStatus getSuccessor() throws ConfigException, DataSourceException
    {
        return this.successor;
    }

    public void setSuccessor(WorkflowStatus successor)
    {
        if (successor != null) {
            this.successorUid = successor.getUid();
        } else {
            this.successorUid = null;
        }
        this.successor = successor;
    }

    public long getUid()
    {
        return uid;
    }

    public void setUid(long uid)
    {
        this.uid = uid;
    }

    public Workflow getWorkflow()
    {
        return this.workflow;
    }

    public void setWorkflow(Workflow workflow)
    {
        this.workflow = workflow;
        if (workflow != null) {
            this.workflowUid = workflow.getUid();
        }
    }

    public Long getSuccessorUid()
    {
        return successorUid;
    }

    public void setSuccessorUid(Long successorUid)
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

    public WorkflowStatus getPredecessor() throws ConfigException, DataSourceException
    {
        Workflow w = this.getWorkflow();
        if (w != null) {
            Vector<WorkflowStatus> v =
                    FactoryInstantiator.getInstance().getWorkflowStatusFactory().getWorkflowStatuses(w);
            for (int i = 0; i < v.size(); i++) {
                if (this.equals(v.elementAt(i).getSuccessor())) {
                    return v.elementAt(i);
                }
            }
        }
        return null;
    }

    public boolean equals(Object o)
    {
        if (o instanceof WorkflowStatus) {
            WorkflowStatus t = (WorkflowStatus) o;
            return this.uid == t.getUid();
        } else {
            return false;
        }
    }

    public org.kimios.kernel.ws.pojo.WorkflowStatus toPojo()
    {
        return new org.kimios.kernel.ws.pojo.WorkflowStatus(this.uid, this.name,
                (this.successorUid != null ? this.successorUid : -1), this.workflowUid);
    }
}

