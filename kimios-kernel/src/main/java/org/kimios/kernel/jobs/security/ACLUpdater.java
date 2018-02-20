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
package org.kimios.kernel.jobs.security;

import org.kimios.kernel.dms.model.DMEntity;
import org.kimios.kernel.dms.FactoryInstantiator;
import org.kimios.api.events.annotations.DmsEvent;
import org.kimios.api.events.annotations.DmsEventName;
import org.kimios.kernel.events.model.EventContext;
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.security.*;
import org.kimios.kernel.security.model.DMEntityACL;
import org.kimios.kernel.security.model.DMEntitySecurity;
import org.kimios.kernel.security.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
public class ACLUpdater implements IACLUpdater {
    private static Logger log = LoggerFactory.getLogger(IACLUpdater.class);

    //TODO: FIX Potential trouble Regarding Ended Transaction before end of clean. Should ENHANCE Performance
    @DmsEvent(eventName = {DmsEventName.ENTITY_ACL_UPDATE})
    public List<DMEntityACL> updateAclsRecursiveMode(Session session, List<DMEntitySecurity> securityItems, DMEntity entity,
                                                     boolean addMode, List<DMEntityACL> removedAcls)
            throws Exception {

        DMEntitySecurityFactory fact =
                org.kimios.kernel.security.FactoryInstantiator.getInstance().getDMEntitySecurityFactory();
        List<DMEntityACL> listAclToIndex = new ArrayList<DMEntityACL>();

        if (!addMode) {
            fact.cleanACLRecursive(entity);
            log.debug("entity existing acls have been removed, because of not in append mode");
        } else {
            log.debug("existing acls are kept!, but some may be removed : {}", removedAcls.size());



        }


        for (DMEntitySecurity acl : securityItems) {
            acl.setDmEntity(entity);
            listAclToIndex.addAll(fact.saveDMEntitySecurity(acl, null));
            log.debug("added acl {} for {}", (acl.getType() == 1 ? "user " : "group ") + acl.getName() + "@" + acl.getSource(), entity.getPath());
        }

        if(addMode){
            //process removed items

        }

        List<DMEntity> items = FactoryInstantiator.getInstance().getDmEntityFactory().getEntities(entity.getPath());

        log.debug("loaded child entities: {}", items.size());
        List<Long> childLists = new ArrayList<Long>();
        for (DMEntity it : items) {
            childLists.add(it.getUid());
            //generate sec for childrens, from previouslys created sec (to avoid xml parsing on each loop)
            for (DMEntitySecurity sec : securityItems) {
                DMEntitySecurity nSec =
                        new DMEntitySecurity(it.getUid(), it.getType(), sec.getName(), sec.getSource(), sec.getType(),
                                sec.isRead(), sec.isWrite(), sec.isFullAccess(), it);
                listAclToIndex.addAll(fact.saveDMEntitySecurity(nSec, null));
                log.debug("added acl {} for {}", (nSec.getType() == 1 ? "user " : "group ") + nSec.getName() + "@" + nSec.getSource(), it.getPath());
            }
        }

        if(addMode && removedAcls.size() > 0){

            //add main entity
            childLists.add(entity.getUid());
            List<String> hashToRemove = new ArrayList<String>();
            for(DMEntityACL r: removedAcls) {
                hashToRemove.add(r.getRuleHash());
            }

            String deleteRemovedAclsQuery = "delete from DMEntityACL acl where acl.ruleHash in (:removedHashList) " +
                    " and acl.dmEntityUid in (:childList)";

            int removedItems = ((HFactory)FactoryInstantiator.getInstance()
                    .getDocumentFactory())
                    .getSession().createQuery(deleteRemovedAclsQuery)
                    .setParameterList("childList", childLists)
                    .setParameterList("removedHashList", hashToRemove)
                    .executeUpdate();

            log.debug("removed acls for children: {}", removedItems);

        }

        EventContext.addParameter("acls", listAclToIndex);
        return listAclToIndex;
    }
}

