/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2012  DevLib'
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

public class DocumentWorkflowStatusPK implements Serializable
{
    private long documentUid;

    private long workflowStatusUid;

    public DocumentWorkflowStatusPK()
    {
    }

    public DocumentWorkflowStatusPK(long documentUid, long workflowStatusUid,
            Date statusDate, String securityEntityName,
            String securityEntitySource)
    {
        this.documentUid = documentUid;
        this.workflowStatusUid = workflowStatusUid;
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DocumentWorkflowStatusPK that = (DocumentWorkflowStatusPK) o;

        if (documentUid != that.documentUid) {
            return false;
        }
        if (workflowStatusUid != that.workflowStatusUid) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = (int) (documentUid ^ (documentUid >>> 32));
        result = 31 * result + (int) (workflowStatusUid ^ (workflowStatusUid >>> 32));
        return result;
    }
}

