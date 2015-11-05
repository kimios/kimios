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

public class WorkflowStatusManagerPK implements Serializable
{
    private String securityEntityName;

    private String securityEntitySource;

    private int securityEntityType;

    private long workflowStatusUid;

    public WorkflowStatusManagerPK()
    {
    }

    public WorkflowStatusManagerPK(String securityEntityName,
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WorkflowStatusManagerPK that = (WorkflowStatusManagerPK) o;

        if (securityEntityType != that.securityEntityType) {
            return false;
        }
        if (workflowStatusUid != that.workflowStatusUid) {
            return false;
        }
        if (securityEntityName != null ? !securityEntityName.equals(that.securityEntityName) :
                that.securityEntityName != null)
        {
            return false;
        }
        if (securityEntitySource != null ? !securityEntitySource.equals(that.securityEntitySource) :
                that.securityEntitySource != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = securityEntityName != null ? securityEntityName.hashCode() : 0;
        result = 31 * result + (securityEntitySource != null ? securityEntitySource.hashCode() : 0);
        result = 31 * result + securityEntityType;
        result = 31 * result + (int) (workflowStatusUid ^ (workflowStatusUid >>> 32));
        return result;
    }
}

