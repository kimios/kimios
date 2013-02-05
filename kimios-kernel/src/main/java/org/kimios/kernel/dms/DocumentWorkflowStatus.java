/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
 *
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.kernel.dms;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;

@Entity
@IdClass(DocumentWorkflowStatusPK.class)
@Table(name = "document_workflow_status")
public class DocumentWorkflowStatus implements Serializable
{
    @Id
    @Column(name = "document_id")
    private long documentUid;

    @ManyToOne(targetEntity = Document.class)
    @JoinColumn(name = "document_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Document document;

    @Column(name = "workflow_status_id")
    private long workflowStatusUid;

    @ManyToOne(targetEntity = WorkflowStatus.class)
    @JoinColumn(name = "workflow_status_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private WorkflowStatus workflowStatus;

    @Column(name = "status_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date statusDate;

    @Column(name = "security_entity_name", nullable = false)
    private String securityEntityName;

    @Column(name = "security_entity_source", nullable = false)
    private String securityEntitySource;

    public DocumentWorkflowStatus()
    {
    }

    public DocumentWorkflowStatus(long documentUid, long workflowStatusUid,
            Date statusDate, String securityEntityName,
            String securityEntitySource)
    {
        this.documentUid = documentUid;
        this.workflowStatusUid = workflowStatusUid;
        this.statusDate = statusDate;
        this.securityEntityName = securityEntityName;
        this.securityEntitySource = securityEntitySource;
    }

    public long getDocumentUid()
    {
        return documentUid;
    }

    public void setDocumentUid(long documentUid)
    {
        this.documentUid = documentUid;
    }

    public long getWorkflowStatusUid()
    {
        return workflowStatusUid;
    }

    public void setWorkflowStatusUid(long workflowStatusUid)
    {
        this.workflowStatusUid = workflowStatusUid;
    }

    public Date getStatusDate()
    {
        return statusDate;
    }

    public void setStatusDate(Date statusDate)
    {
        this.statusDate = statusDate;
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

    public org.kimios.kernel.ws.pojo.WorkflowStatus toPojo() throws ConfigException, DataSourceException
    {
        return FactoryInstantiator.getInstance().getWorkflowStatusFactory()
                .getWorkflowStatus(this.getWorkflowStatusUid()).toPojo();
    }

    public Document getDocument()
    {
        return document;
    }

    public void setDocument(Document document)
    {
        if (document != null) {
            this.documentUid = document.getUid();
        }
        this.document = document;
    }

    public WorkflowStatus getWorkflowStatus()
    {
        return workflowStatus;
    }

    public void setWorkflowStatus(WorkflowStatus workflowStatus)
    {
        if (workflowStatus != null) {
            this.workflowStatusUid = workflowStatus.getUid();
        }
        this.workflowStatus = workflowStatus;
    }
}

