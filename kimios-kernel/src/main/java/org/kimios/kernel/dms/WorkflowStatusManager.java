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
package org.kimios.kernel.dms;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "workflow_status_manager")
@IdClass(WorkflowStatusManagerPK.class)
public class WorkflowStatusManager implements Serializable
{
    @Id
    @Column(name = "security_entity_name", nullable = false)
    private String securityEntityName;

    @Id
    @Column(name = "security_entity_source", nullable = false)
    private String securityEntitySource;

    @Id
    @Column(name = "security_entity_type", nullable = false)
    private int securityEntityType;

    @Id
    @Column(name = "workflow_status_id", nullable = false)
    private long workflowStatusUid;

    @ManyToOne(targetEntity = WorkflowStatus.class)
    @JoinColumn(name = "workflow_status_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private WorkflowStatus workflowStatus;

    public WorkflowStatusManager()
    {
    }

    public WorkflowStatusManager(String securityEntityName,
            String securityEntitySource, int securityEntityType,
            long workflowStatusUid)
    {
        this.securityEntityName = securityEntityName;
        this.securityEntitySource = securityEntitySource;
        this.securityEntityType = securityEntityType;
        this.workflowStatusUid = workflowStatusUid;
    }

    public String getSecurityEntityName()
    {
        return securityEntityName;
    }

    public void setSecurityEntityName(String securityEntityName)
    {
        this.securityEntityName = securityEntityName;
    }

    public String getSecurityEntitySource()
    {
        return securityEntitySource;
    }

    public void setSecurityEntitySource(String securityEntitySource)
    {
        this.securityEntitySource = securityEntitySource;
    }

    public int getSecurityEntityType()
    {
        return securityEntityType;
    }

    public void setSecurityEntityType(int securityEntityType)
    {
        this.securityEntityType = securityEntityType;
    }

    public long getWorkflowStatusUid()
    {
        return workflowStatusUid;
    }

    public void setWorkflowStatusUid(long workflowStatusUid)
    {
        this.workflowStatusUid = workflowStatusUid;
    }

    public WorkflowStatus getWorkflowStatus()
    {
        return this.workflowStatus;
    }

    public void setWorksflowStatus(WorkflowStatus ws)
    {
        if (ws != null) {
            this.setWorkflowStatusUid(ws.getUid());
        }
        this.workflowStatus = ws;
    }

    public org.kimios.kernel.ws.pojo.WorkflowStatusManager toPojo()
    {
        return new org.kimios.kernel.ws.pojo.WorkflowStatusManager(this.securityEntityName, this.securityEntitySource,
                this.securityEntityType, this.workflowStatusUid);
    }
}

