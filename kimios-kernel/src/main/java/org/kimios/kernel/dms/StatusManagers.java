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

import java.util.Vector;

/*
* Wrapper class for status rights management
*/
public class StatusManagers
{
    private WorkflowStatus status;

    private Vector<WorkflowStatusManager> managers;

    public StatusManagers(WorkflowStatus status,
            Vector<WorkflowStatusManager> managers)
    {
        this.status = status;
        this.managers = managers;
    }

    public WorkflowStatus getStatus()
    {
        return status;
    }

    public void setStatus(WorkflowStatus status)
    {
        this.status = status;
    }

    public Vector<WorkflowStatusManager> getManagers()
    {
        return managers;
    }

    public void setManagers(Vector<WorkflowStatusManager> managers)
    {
        this.managers = managers;
    }
}
