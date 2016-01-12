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
package org.kimios.kernel.rules;

import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.kimios.api.events.annotations.DmsEventName;
import org.kimios.api.events.annotations.DmsEventOccur;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.rules.model.RuleBean;

import java.util.List;

public class RuleBeanFactory extends HFactory
{
    public long save(RuleBean bean) throws DataSourceException
    {
        try {
            return (Long) getSession().save(bean);
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public void deleleRuleBean(RuleBean bean) throws DataSourceException
    {
        try {
            getSession().delete(bean);
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public List<RuleBean> loadConditionByEvent(DmsEventName name, DmsEventOccur occur) throws DataSourceException
    {
        try {
            return getSession().createCriteria(RuleBean.class)
                    .add(Restrictions.eq("eventType", name.ordinal()))
                    .add(Restrictions.eq("eventStatus", occur.ordinal()))
                    .list();
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public List<RuleBean> loadConditionByEventAndPath(DmsEventName name, String path, List<String> parentPaths)
            throws DataSourceException
    {
        try {

            String hql = "from RuleBean where path = :rulePath and events.dmsEventName in (" + name.ordinal() + ")";
            return getSession().createCriteria(RuleBean.class)
                    .add(Restrictions.or(Restrictions.eq("path", path),
                            Restrictions.and(
                                    Restrictions.eq("recursive", true),
                                    Restrictions.in("path", parentPaths)
                            )))
                    .list();
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }
}

