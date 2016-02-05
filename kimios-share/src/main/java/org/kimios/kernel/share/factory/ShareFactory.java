/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
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

package org.kimios.kernel.share.factory;

import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.share.model.MailContact;
import org.kimios.kernel.share.model.Share;
import org.kimios.kernel.share.model.ShareStatus;
import sun.security.provider.SHA;

import java.util.List;

/**
 * Created by farf on 20/07/15.
 */
public class ShareFactory extends HFactory {



    public Share findById(long id){
        return (Share)getSession().load(Share.class, id);
    }

    public List<Share> listEntitiesSharedWith(String userId, String userSource, ShareStatus ... shareStatuses){
        String query = "select s from Share s join s.entity as ent fetch all properties " +
                " where s.targetUserId = :userId and s.targetUserSource = :userSource " +
                " and s.shareStatus in (:shareStatuses)" +
                " and s.expirationDate >=  CURRENT_TIMESTAMP()" +
                " and (ent.trashed = false or ent.trashed is null)";
        return getSession()
                .createQuery(query)
                .setString("userId", userId)
                .setString("userSource", userSource)
                .setParameterList("shareStatuses", shareStatuses)
                .list();
    }

    public List<Share> listEntitiesSharedBy(String userId, String userSource, ShareStatus ... shareStatuses){
        String query = "select s from Share s where s.creatorId = :userId and s.creatorSource = :userSource " +
                " and s.shareStatus in (:shareStatuses) ";
        return getSession()
                .createQuery(query)
                .setString("userId", userId)
                .setString("userSource", userSource)
                .setParameterList("shareStatuses", shareStatuses)
                .list();
    }


    public void removeShare(Share share){
        getSession().delete(share);
        getSession().flush();
    }


    public Share saveShare(Share share){
        getSession().saveOrUpdate(share);
        getSession().flush();
        return share;
    }

}
