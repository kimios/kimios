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

import java.io.Serializable;
import java.util.Date;

public class DocumentWorkflowStatusRequestPK implements Serializable
{
    private String userName;

    private String userSource;

    private long documentUid;

    private long workflowStatusUid;

    private Date date;

    public DocumentWorkflowStatusRequestPK()
    {
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DocumentWorkflowStatusRequestPK that = (DocumentWorkflowStatusRequestPK) o;

        if (documentUid != that.documentUid) {
            return false;
        }
        if (workflowStatusUid != that.workflowStatusUid) {
            return false;
        }
        if (date != null ? !date.equals(that.date) : that.date != null) {
            return false;
        }
        if (userName != null ? !userName.equals(that.userName) : that.userName != null) {
            return false;
        }
        if (userSource != null ? !userSource.equals(that.userSource) : that.userSource != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = userName != null ? userName.hashCode() : 0;
        result = 31 * result + (userSource != null ? userSource.hashCode() : 0);
        result = 31 * result + (int) (documentUid ^ (documentUid >>> 32));
        result = 31 * result + (int) (workflowStatusUid ^ (workflowStatusUid >>> 32));
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
