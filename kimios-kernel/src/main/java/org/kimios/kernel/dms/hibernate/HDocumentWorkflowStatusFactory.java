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
import org.kimios.kernel.dms.model.DocumentWorkflowStatus;
import org.kimios.kernel.dms.DocumentWorkflowStatusFactory;
import org.kimios.exceptions.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;

import java.util.List;
import java.util.Vector;

public class HDocumentWorkflowStatusFactory extends HFactory implements
        DocumentWorkflowStatusFactory
{
    public void deleteDocumentWorkflowStatus(DocumentWorkflowStatus dws)
            throws ConfigException, DataSourceException
    {
        try {
            getSession().delete(dws);
        } catch (HibernateException he) {
            throw he;
        }
    }

    public DocumentWorkflowStatus getDocumentWorkflowStatus(long documentUid,
            long workflowStatusUid) throws ConfigException, DataSourceException
    {
        try {
            Criteria c = getSession().createCriteria(DocumentWorkflowStatus.class)
                    .add(Restrictions.eq("documentUid", new Long(documentUid)))
                    .add(Restrictions.eq("workflowStatusUid", new Long(workflowStatusUid)));

            DocumentWorkflowStatus dws = (DocumentWorkflowStatus) c.uniqueResult();
            return dws;
        } catch (HibernateException he) {
            throw he;
        }
    }

    public Vector<DocumentWorkflowStatus> getDocumentWorkflowStatuses(
            long documentUid) throws ConfigException, DataSourceException
    {
        try {
            Vector<DocumentWorkflowStatus> vDws = new Vector<DocumentWorkflowStatus>();
            Criteria c = getSession().createCriteria(DocumentWorkflowStatus.class)
                    .add(Restrictions.eq("documentUid", new Long(documentUid)));
            List<DocumentWorkflowStatus> lDws = c.list();
            for (DocumentWorkflowStatus dws : lDws) {
                vDws.add(dws);
            }
            return vDws;
        } catch (HibernateException he) {
            throw he;
        }
    }

    public DocumentWorkflowStatus getLastDocumentWorkflowStatus(long documentVersionUid)
            throws ConfigException, DataSourceException
    {
        try {
            Criteria c = getSession().createCriteria(DocumentWorkflowStatus.class)
                    .add(Restrictions.eq("documentUid", new Long(documentVersionUid)))
                    .addOrder(Order.desc("statusDate"))
                    .setMaxResults(1);
            DocumentWorkflowStatus dws = (DocumentWorkflowStatus) c.uniqueResult();
            return dws;
        } catch (HibernateException he) {
            throw he;
        }
    }

    public void saveDocumentWorkflowStatus(DocumentWorkflowStatus dws)
            throws ConfigException, DataSourceException
    {
        try {
            getSession().save(dws);
            getSession().flush();
        } catch (HibernateException he) {
            throw he;
        }
    }
}

