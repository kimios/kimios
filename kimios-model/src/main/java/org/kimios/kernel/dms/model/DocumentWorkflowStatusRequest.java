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
package org.kimios.kernel.dms.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@IdClass(DocumentWorkflowStatusRequestPK.class)
@Table(name = "document_workflow_status_request")
public class DocumentWorkflowStatusRequest implements Serializable
{
    @Id
    @Column(name = "username")
    private String userName;

    @Id
    @Column(name = "user_source")
    private String userSource;

    @ManyToOne(targetEntity = Document.class)
    @JoinColumn(name = "document_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Document document;

    @Id
    @Column(name = "document_id", nullable = false)
    private long documentUid;

    @ManyToOne(targetEntity = WorkflowStatus.class)
    @JoinColumn(name = "workflow_status_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private WorkflowStatus workflowStatus;

    @Id
    @Column(name = "workflow_status_id", nullable = false)
    private long workflowStatusUid;

    @Id
    @Column(name = "request_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Id
    @Column(name = "request_status")
    private int status;

    @Column(name = "request_comment")
    private String comment;

    @Column(name = "validator_user_name")
    private String validatorUserName;

    @Column(name = "validator_user_source")
    private String validatorUserSource;

    @Column(name = "validation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validationDate;

    public DocumentWorkflowStatusRequest()
    {
    }

    public DocumentWorkflowStatusRequest(String userName, String userSource, String validatorUserName,
            String validatorUserSource,
            long documentUid, long workflowStatusUid, Date date, int status, String comment, Date validationDate)
    {
        this.userName = userName;
        this.userSource = userSource;
        this.validatorUserName = validatorUserName;
        this.validatorUserSource = validatorUserSource;
        this.documentUid = documentUid;
        this.workflowStatusUid = workflowStatusUid;
        this.date = date;
        this.status = status;
        this.comment = comment;
        this.validationDate = validationDate;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getUserSource()
    {
        return userSource;
    }

    public void setUserSource(String userSource)
    {
        this.userSource = userSource;
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

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public String getValidatorUserName()
    {
        return validatorUserName;
    }

    public void setValidatorUserName(String validatorUserName)
    {
        this.validatorUserName = validatorUserName;
    }

    public String getValidatorUserSource()
    {
        return validatorUserSource;
    }

    public void setValidatorUserSource(String validatorUserSource)
    {
        this.validatorUserSource = validatorUserSource;
    }

    public Date getValidationDate()
    {
        return validationDate;
    }

    public void setValidationDate(Date validationDate)
    {
        this.validationDate = validationDate;
    }

    public org.kimios.kernel.ws.pojo.DocumentWorkflowStatusRequest toPojo()
    {
        return new org.kimios.kernel.ws.pojo.DocumentWorkflowStatusRequest(this.userName, this.userSource,
                this.validatorUserName, this.validatorUserSource, this.documentUid, this.workflowStatusUid, this.date,
                this.status, this.comment, this.validationDate);
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
        if (this.workflowStatus != null) {
            this.workflowStatusUid = workflowStatus.getUid();
        }
        this.workflowStatus = workflowStatus;
    }
}
