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
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.Workflow;
import org.kimios.kernel.dms.WorkflowStatus;
import org.kimios.kernel.dms.WorkflowStatusFactory;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;

import java.util.List;
import java.util.Vector;

public class HWorkflowStatusFactory extends HFactory implements WorkflowStatusFactory
{
    public HWorkflowStatusFactory()
    {
    }

    public void deleteWorkflowStatus(WorkflowStatus wfs)
            throws ConfigException, DataSourceException
    {
        try {
            WorkflowStatus prev = wfs.getPredecessor();
            if (prev != null) {
                prev.setSuccessor(wfs.getSuccessor());
                this.changeWorkflowStatus(prev);
            }
            getSession().delete(wfs);
            getSession().flush();
        } catch (HibernateException he) {
            throw new DataSourceException(he);
        }
    }

    public WorkflowStatus getPreviousWorkflowStatus(WorkflowStatus wfs) throws ConfigException, DataSourceException
    {
        try {
            Criteria c = getSession().createCriteria(WorkflowStatus.class)
                    .add(Restrictions.eq("successorUid", wfs.getUid()));
            WorkflowStatus prev = (WorkflowStatus) c.uniqueResult();
            return prev;
        } catch (HibernateException he) {
            throw new DataSourceException(he);
        }
    }

    public WorkflowStatus getEndWorkflowStatus(Workflow wf)
            throws ConfigException, DataSourceException
    {
        try {
            //Pas de successeur
            DetachedCriteria successorList = DetachedCriteria.forClass(WorkflowStatus.class)
                    .add(Restrictions.isNull("successorUid"))
                    .add(Restrictions.eq("workflowUid", wf.getUid()));
            WorkflowStatus end = (WorkflowStatus) successorList.getExecutableCriteria(getSession()).uniqueResult();
            return end;
        } catch (HibernateException he) {
            throw new DataSourceException(he);
        }
    }

    public WorkflowStatus getStartWorkflowStatus(Workflow wf)
            throws ConfigException, DataSourceException
    {
        try {
            //Liste des successeurs
            DetachedCriteria successorList = DetachedCriteria.forClass(WorkflowStatus.class)
                    .add(Restrictions.isNotNull("successorUid"))
                    .add(Restrictions.eq("workflowUid", wf.getUid()));

            List<WorkflowStatus> lSList = successorList.getExecutableCriteria(getSession()).list();

            //Liste des status
            List<WorkflowStatus> list = getSession().createCriteria(WorkflowStatus.class)
                    .add(Restrictions.eq("workflowUid", wf.getUid()))
                    .list();
            //Not In
            list.removeAll(lSList);
            return list.get(0);
        } catch (HibernateException he) {
            throw he;
        }
    }

    public WorkflowStatus getWorkflowStatus(long uid) throws ConfigException,
            DataSourceException
    {
        try {
            WorkflowStatus wfs = (WorkflowStatus) getSession().get(WorkflowStatus.class, new Long(uid));
            return wfs;
        } catch (HibernateException he) {
            throw new DataSourceException(he);
        }
    }

    public Vector<WorkflowStatus> getWorkflowStatuses(Workflow wf)
            throws ConfigException, DataSourceException
    {
        try {
            Vector<WorkflowStatus> vWfStatus = new Vector<WorkflowStatus>();
            Criteria c = getSession().createCriteria(WorkflowStatus.class)
                    .add(Restrictions.eq("workflowUid", wf.getUid()))
                    .add(Restrictions.isNull("successorUid"));
            List<WorkflowStatus> lWfs = c.list();
            while (lWfs.size() > 0) {
                WorkflowStatus wfs = lWfs.get(0);
                vWfStatus.add(0, wfs);
                c = getSession().createCriteria(WorkflowStatus.class)
                        .add(Restrictions.eq("workflowUid", new Long(wf.getUid())))
                        .add(Restrictions.eq("successorUid", wfs.getUid()));
                lWfs = c.list();
            }
            return vWfStatus;
        } catch (HibernateException he) {
            throw he;
        }
    }

    public long saveWorkflowStatus(WorkflowStatus wfs) throws ConfigException,
            DataSourceException
    {
        try {
            if (wfs.getSuccessorUid() != null && wfs.getSuccessorUid() == -1) {
                wfs.setSuccessorUid(null);
            }
            getSession().save(wfs);
            getSession().flush();
            return wfs.getUid();
        } catch (HibernateException he) {
            throw new DataSourceException(he);
        }
    }

    public void updateWorkflowStatus(WorkflowStatus wfs, Vector<WorkflowStatus> vOrdered, int pos)
            throws ConfigException, DataSourceException
    {

        if (pos == vOrdered.size() - 1) {
            wfs.setSuccessorUid(null);
            wfs.setSuccessor(null);
        } else {
            WorkflowStatus next = vOrdered.elementAt(pos + 1);
            wfs.setSuccessorUid(next.getUid());
        }
        if (wfs.getUid() == -1) {
            this.saveWorkflowStatus(wfs);
        } else {
            this.changeWorkflowStatus(wfs);
        }
    }

    public void changeWorkflowStatus(WorkflowStatus wfs) throws ConfigException, DataSourceException
    {
        try {
            wfs = (WorkflowStatus) getSession().merge(wfs);
            getSession().update(wfs);
            getSession().flush();
        } catch (HibernateException he) {
            throw new DataSourceException(he);
        }
    }
}

