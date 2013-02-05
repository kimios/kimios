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
package org.kimios.kernel.dms.hibernate;

import java.util.List;
import java.util.Vector;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.DocumentComment;
import org.kimios.kernel.dms.DocumentCommentFactory;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;

public class HDocumentCommentFactory extends HFactory implements DocumentCommentFactory
{
    public void deleteDocumentComment(DocumentComment comment)
            throws ConfigException, DataSourceException
    {
        try {
            getSession().delete(comment);
        } catch (HibernateException he) {
            throw he;
        }
    }

    public DocumentComment getDocumentComment(long uid) throws ConfigException,
            DataSourceException
    {
        try {
            DocumentComment dc = (DocumentComment) getSession().get(DocumentComment.class, new Long(uid));
            return dc;
        } catch (HibernateException he) {
            throw he;
        }
    }

    public Vector<DocumentComment> getDocumentComments(long documentVersionUid)
            throws ConfigException, DataSourceException
    {
        try {

            Vector<DocumentComment> vDc = new Vector<DocumentComment>();
            Criteria c = getSession().createCriteria(DocumentComment.class)
                    .add(Restrictions.eq("documentVersionUid", documentVersionUid))
                    .addOrder(Order.desc("date"));
            List<DocumentComment> lDc = c.list();
            for (DocumentComment dc : lDc) {
                vDc.add(dc);
            }
            return vDc;
        } catch (HibernateException he) {
            throw he;
        }
    }

    public long saveDocumentComment(DocumentComment comment)
            throws ConfigException, DataSourceException
    {
        try {
            getSession().save(comment);
            return comment.getUid();
        } catch (HibernateException he) {
            throw he;
        }
    }

    public void updateDocumentComment(DocumentComment comment)
            throws ConfigException, DataSourceException
    {
        try {
            getSession().update(comment);
        } catch (HibernateException he) {
            throw he;
        }
    }
}

