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
package org.kimios.kernel.ws.pojo;

public class WorkflowStatusManager
{
    private String securityEntityName;

    private String securityEntitySource;

    private int securityEntityType;

    private long workflowStatusUid;

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
}

