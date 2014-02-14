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
package org.kimios.kernel.dms.hibernate;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.Workflow;
import org.kimios.kernel.dms.WorkflowFactory;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;

import java.util.List;
import java.util.Vector;

public class HWorkflowFactory extends HFactory implements WorkflowFactory
{
    public void deleteWorkflow(Workflow wf) throws ConfigException,
            DataSourceException
    {
        try {
            getSession().delete(wf);
            getSession().flush();
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public Workflow getWorkflow(long uid) throws ConfigException,
            DataSourceException
    {
        try {
            Workflow wf = (Workflow) getSession().get(Workflow.class, new Long(uid));
            return wf;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public Vector<Workflow> getWorkflows() throws ConfigException,
            DataSourceException
    {
        try {
            Vector<Workflow> vWorkflows = new Vector<Workflow>();
            Criteria c = getSession().createCriteria(Workflow.class)
                    .addOrder(Order.asc("name"));
            List<Workflow> lWorkflows = c.list();
            for (Workflow wf : lWorkflows) {
                vWorkflows.add(wf);
            }
            return vWorkflows;
        } catch (HibernateException e) {
            throw e;
        }
    }

    public long saveWorkflow(Workflow wf) throws ConfigException,
            DataSourceException
    {
        try {
            getSession().save(wf);
            getSession().flush();
            return wf.getUid();
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public void updateWorkflow(Workflow wf) throws ConfigException,
            DataSourceException
    {
        try {
            getSession().update(wf);
            getSession().flush();
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }
}

