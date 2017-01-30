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

import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.MetaFeedFactory;
import org.kimios.kernel.dms.model.MetaFeedImpl;
import org.kimios.exceptions.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HMetaFeedFactory extends HFactory implements MetaFeedFactory
{
    final Logger log = LoggerFactory.getLogger(HMetaFeedFactory.class);

    public void deleteMetaFeed(MetaFeedImpl metaFeed) throws ConfigException,
            DataSourceException
    {
        try {
            metaFeed = (MetaFeedImpl) getSession().merge(metaFeed);
            getSession().delete(metaFeed);
            getSession().flush();
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public MetaFeedImpl getMetaFeed(long uid) throws ConfigException,
            DataSourceException
    {
        try {
            MetaFeedImpl m = (MetaFeedImpl) getSession().get(MetaFeedImpl.class, uid);
            return m;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public List<MetaFeedImpl> getMetaFeeds() throws ConfigException,
            DataSourceException
    {
        try {
            List<MetaFeedImpl> lMfb = (List<MetaFeedImpl>) getSession().createCriteria(MetaFeedImpl.class)
                    .addOrder(Order.asc("name"))
                    .list();
            return lMfb;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public long saveMetaFeed(MetaFeedImpl metaFeed) throws ConfigException,
            DataSourceException
    {
        try {
            getSession().save(metaFeed);
            getSession().flush();
            return metaFeed.getUid();
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public void updateMetaFeed(MetaFeedImpl metaFeed) throws ConfigException,
            DataSourceException
    {
        try {
            metaFeed = (MetaFeedImpl) getSession().merge(metaFeed);
            getSession().update(metaFeed);
            getSession().flush();
        } catch (HibernateException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }
}

