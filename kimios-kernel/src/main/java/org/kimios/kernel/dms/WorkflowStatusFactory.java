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

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.model.Workflow;
import org.kimios.kernel.dms.model.WorkflowStatus;
import org.kimios.exceptions.DataSourceException;

import java.util.Vector;

public interface WorkflowStatusFactory
{
    public WorkflowStatus getWorkflowStatus(long uid) throws ConfigException, DataSourceException;

    public WorkflowStatus getStartWorkflowStatus(Workflow wf) throws ConfigException, DataSourceException;

    public WorkflowStatus getEndWorkflowStatus(Workflow wf) throws ConfigException, DataSourceException;

    public Vector<WorkflowStatus> getWorkflowStatuses(Workflow wf) throws ConfigException, DataSourceException;

    public long saveWorkflowStatus(WorkflowStatus wfs) throws ConfigException, DataSourceException;

    public void updateWorkflowStatus(WorkflowStatus wfs, Vector<WorkflowStatus> vOrdered, int pos)
            throws ConfigException, DataSourceException;

    public void deleteWorkflowStatus(WorkflowStatus wfs) throws ConfigException, DataSourceException;

    public void changeWorkflowStatus(WorkflowStatus wfs) throws ConfigException, DataSourceException;
}

