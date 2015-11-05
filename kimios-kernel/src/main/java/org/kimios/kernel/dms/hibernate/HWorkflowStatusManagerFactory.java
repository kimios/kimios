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
package org.kimios.kernel.dms.hibernate;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.model.Workflow;
import org.kimios.kernel.dms.model.WorkflowStatusManager;
import org.kimios.kernel.dms.WorkflowStatusManagerFactory;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.security.model.SecurityEntity;

import java.util.List;
import java.util.Vector;

public class HWorkflowStatusManagerFactory extends HFactory implements
        WorkflowStatusManagerFactory
{
    public void deleteWorkflowStatusManager(WorkflowStatusManager wsm)
            throws ConfigException, DataSourceException
    {
        try {
            getSession().delete(wsm);
        } catch (HibernateException he) {
            throw he;
        }
    }

    public WorkflowStatusManager getWorkflowStatusManager(
            String securityEntityName, String securityEntitySource,
            int securityEntityType, long workflowStatusUid)
            throws ConfigException, DataSourceException
    {
        try {
            Criteria c = getSession().createCriteria(WorkflowStatusManager.class)
                    .add(Restrictions.eq("securityEntityType", securityEntityType))
                    .add(Restrictions.eq("securityEntityName", securityEntityName))
                    .add(Restrictions.eq("securityEntitySource", securityEntitySource))
                    .add(Restrictions.eq("workflowStatusUid", workflowStatusUid))
                    .setMaxResults(1);
            WorkflowStatusManager wsm = (WorkflowStatusManager) c.uniqueResult();
            return wsm;
        } catch (HibernateException he) {
            throw he;
        }
    }

    public Vector<WorkflowStatusManager> getWorkflowStatusManagers(
            long workflowStatusUid) throws ConfigException, DataSourceException
    {
        try {
            Vector<WorkflowStatusManager> vWsm = new Vector<WorkflowStatusManager>();
            Criteria c = getSession().createCriteria(WorkflowStatusManager.class)
                    .add(Restrictions.eq("workflowStatusUid", workflowStatusUid));
            List<WorkflowStatusManager> lWsm = c.list();
            for (WorkflowStatusManager wsm : lWsm) {
                vWsm.add(wsm);
            }
            return vWsm;
        } catch (HibernateException he) {
            throw he;
        }
    }

    public Vector<WorkflowStatusManager> getWorkflowStatusManagers(
            SecurityEntity sec, Workflow wf) throws ConfigException,
            DataSourceException
    {
        try {
            Vector<WorkflowStatusManager> vWsm = new Vector<WorkflowStatusManager>();
            Criteria c = getSession().createCriteria(WorkflowStatusManager.class)
                    .add(Restrictions.eq("securityEntityType", sec.getType()))
                    .add(Restrictions.eq("securityEntityName", sec.getID()))
                    .add(Restrictions.eq("securityEntitySource", sec.getAuthenticationSourceName()))
                    .add(Restrictions.in("workflowStatusUid",
                            getSession().createQuery("select list(uid) from WorkflowStatus where workflowId=:wkUid")
                                    .setParameter("wkUid", wf.getUid())
                                    .list()))
                    .addOrder(Order.desc("requestDate"));
            List<WorkflowStatusManager> lWsm = c.list();
            for (WorkflowStatusManager wsm : lWsm) {
                vWsm.add(wsm);
            }
            return vWsm;
        } catch (HibernateException he) {
            throw he;
        }
    }

    public void saveWorkflowStatusManager(WorkflowStatusManager wsm)
            throws ConfigException, DataSourceException
    {
        try {
            getSession().save(wsm);
        } catch (HibernateException he) {
            throw he;
        }
    }
}

