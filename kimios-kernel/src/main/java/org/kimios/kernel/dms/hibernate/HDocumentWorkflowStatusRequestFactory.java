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
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.dms.model.DocumentWorkflowStatusRequest;
import org.kimios.kernel.dms.DocumentWorkflowStatusRequestFactory;
import org.kimios.kernel.dms.model.RequestStatus;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.security.model.SecurityEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class HDocumentWorkflowStatusRequestFactory extends HFactory implements
        DocumentWorkflowStatusRequestFactory
{
    public void deleteRequest(DocumentWorkflowStatusRequest request)
            throws ConfigException, DataSourceException
    {
        try {
            getSession().delete(request);
            getSession().flush();
        } catch (HibernateException he) {
            throw he;
        }
    }

    public Vector<DocumentWorkflowStatusRequest> getPendingRequests(
            SecurityEntity sec) throws ConfigException, DataSourceException
    {
        try {

            String hQuery = "select req from DocumentWorkflowStatusRequest req, WorkflowStatusManager m where "
                    + "m.workflowStatusUid = req.workflowStatusUid " +
                    " and req.status = :reqStatus" +
                    " and m.securityEntityName = :secName " +
                    " and m.securityEntitySource = :secSource" +
                    " and m.securityEntityType = :secType "
                    + " ORDER BY req.date DESC";

            List<DocumentWorkflowStatusRequest> lDwsr = getSession().createQuery(hQuery)
                    .setParameter("secName", sec.getID())
                    .setParameter("secSource", sec.getAuthenticationSourceName())
                    .setParameter("secType", sec.getType())
                    .setParameter("reqStatus", RequestStatus.PENDING)
                    .list();

            Vector<DocumentWorkflowStatusRequest> vDwsr = new Vector<DocumentWorkflowStatusRequest>();
            for (DocumentWorkflowStatusRequest dwsr : lDwsr) {
                vDwsr.add(dwsr);
            }
            return vDwsr;
        } catch (HibernateException he) {
            he.printStackTrace();
            throw he;
        }
    }

    public DocumentWorkflowStatusRequest getLastPendingRequest(Document d) throws ConfigException, DataSourceException
    {
        try {

            String hQuery = "from DocumentWorkflowStatusRequest where status = :reqStatus "
                    + "and documentUid = :documentId"
                    + " ORDER BY date DESC";
            DocumentWorkflowStatusRequest dws = (DocumentWorkflowStatusRequest) getSession().createQuery(hQuery)
                    .setParameter("reqStatus", RequestStatus.PENDING)
                    .setParameter("documentId", d.getUid())
                    .setMaxResults(1)
                    .uniqueResult();

            return dws;
        } catch (HibernateException he) {
            he.printStackTrace();
            throw he;
        }
    }

    public Vector<DocumentWorkflowStatusRequest> getRequests(long documentVersionUid)
            throws ConfigException, DataSourceException
    {
        try {
            Vector<DocumentWorkflowStatusRequest> vDwsr = new Vector<DocumentWorkflowStatusRequest>();
            Criteria c = getSession().createCriteria(DocumentWorkflowStatusRequest.class)
                    .add(Restrictions.eq("documentUid", documentVersionUid))
                    .addOrder(Order.desc("date"));
            List<DocumentWorkflowStatusRequest> lDwsr = c.list();
            for (DocumentWorkflowStatusRequest dwsr : lDwsr) {
                vDwsr.add(dwsr);
            }
            return vDwsr;
        } catch (HibernateException he) {
            throw he;
        }
    }

    public void saveRequest(DocumentWorkflowStatusRequest request)
            throws ConfigException, DataSourceException
    {
        try {
            try {
                request.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .parse(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(request.getDate())));
            } catch (java.text.ParseException e) {
            }
            getSession().save(request);
            getSession().flush();
        } catch (HibernateException he) {
            throw he;
        }
    }

    public void updateRequest(DocumentWorkflowStatusRequest newRequest) throws ConfigException,
            DataSourceException
    {
        try {
            getSession().update(newRequest);
            getSession().flush();
        } catch (HibernateException he) {
            throw he;
        }
    }

    public DocumentWorkflowStatusRequest getDocumentWorkflowStatusRequest(long documentUid, long workflowStatusUid,
            String userName, String userSource, Date reqDate)
            throws ConfigException, DataSourceException
    {

        try {
            DocumentWorkflowStatusRequest req =
                    (DocumentWorkflowStatusRequest) getSession().createCriteria(DocumentWorkflowStatusRequest.class)
                            .add(Restrictions.eq("documentUid", documentUid))
                            .add(Restrictions.eq("workflowStatusUid", workflowStatusUid))
                            .add(Restrictions.eq("userName", userName))
                            .add(Restrictions.eq("userSource", userSource))
                            .add(Restrictions.eq("date", reqDate)).uniqueResult();
            return req;
        } catch (HibernateException he) {
            throw he;
        }
    }
}

