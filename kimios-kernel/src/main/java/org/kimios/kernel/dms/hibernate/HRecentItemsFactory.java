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

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.configuration.Config;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.dms.DMEntityImpl;
import org.kimios.kernel.dms.RecentItemsFactory;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.log.ActionType;
import org.kimios.utils.configuration.ConfigurationManager;

import java.util.List;
import java.util.Vector;

public class HRecentItemsFactory extends HFactory implements RecentItemsFactory
{
    public Vector<DMEntity> getRecentItems(String owner, String ownerSource) throws ConfigException, DataSourceException
    {

        Long itemLimit = Long.parseLong(ConfigurationManager.getValue(Config.DEFAULT_RECENT_ITEMS));
        try {
            Vector<DMEntity> vRecents = new Vector<DMEntity>();
            String hQuery = "SELECT new DMEntityImpl(d.dmEntityUid, d.dmEntityType, doc.path) from DMEntityLog d " +
                    "left join d.dmEntity doc " +
                    " WHERE d.action in(:read,:create,:update) " +
                    " AND (doc is not null) " +
                    " AND (d.dmEntityType = :dmEntityType) " +
                    "AND d.user like :uname AND d.userSource like :usource GROUP BY d.dmEntityUid, d.dmEntityType, doc.path, d.user, d.userSource " +
                    "ORDER BY max(d.date) DESC ";

            Query qu = getSession().createQuery(hQuery);
            qu.setParameter("read", ActionType.READ)
                    .setParameter("create", ActionType.CREATE)
                    .setParameter("update", ActionType.UPDATE)
                    .setParameter("uname", owner)
                    .setParameter("usource", ownerSource)
                    .setParameter("dmEntityType", 3)
                    .setMaxResults(itemLimit.intValue());

            List<DMEntityImpl> lRecents = qu.list();

            vRecents.addAll(lRecents);

            return vRecents;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }
}

