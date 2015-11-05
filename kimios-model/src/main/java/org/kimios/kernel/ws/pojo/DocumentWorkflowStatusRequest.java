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

import java.util.Calendar;
import java.util.Date;

public class DocumentWorkflowStatusRequest
{
    private String userName;

    private String userSource;

    private String validatorUserName;

    private String validatorUserSource;

    private long documentUid;

    private long workflowStatusUid;

    private Calendar date;

    private int status;

    private String comment;

    private Calendar validationDate;

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
        this.date = Calendar.getInstance();
        this.date.setTime(date);
        this.status = status;
        this.comment = comment;
        if (validationDate != null) {
            this.validationDate = Calendar.getInstance();
            this.validationDate.setTime(validationDate);
        }
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

    public Calendar getDate()
    {
        return date;
    }

    public void setDate(Calendar date)
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

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public Calendar getValidationDate()
    {
        return validationDate;
    }

    public void setValidationDate(Calendar validationDate)
    {
        this.validationDate = validationDate;
    }
}
