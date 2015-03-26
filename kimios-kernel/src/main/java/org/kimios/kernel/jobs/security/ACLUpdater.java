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

import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.dms.FactoryInstantiator;
import org.kimios.kernel.events.annotations.DmsEvent;
import org.kimios.kernel.events.annotations.DmsEventName;
import org.kimios.kernel.security.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Transactional
public class ACLUpdater implements IACLUpdater
{
    private static Logger log = LoggerFactory.getLogger(IACLUpdater.class);

    //TODO: FIX Potential trouble Regarding Ended Transaction before end of clean. Should ENHANCE Performance
    @DmsEvent(eventName = { DmsEventName.ENTITY_ACL_UPDATE })
    public List<DMEntityACL> updateAclsRecursiveMode(Session session, String xmlStream, DMEntity entity)
            throws Exception
    {

        DMEntitySecurityFactory fact =
                org.kimios.kernel.security.FactoryInstantiator.getInstance().getDMEntitySecurityFactory();
        Vector<DMEntitySecurity> des = DMEntitySecurityUtil.getDMentitySecuritesFromXml(xmlStream, entity);
        List<DMEntityACL> listAclToIndex = new ArrayList<DMEntityACL>();
        fact.cleanACLRecursive(entity);
        for (DMEntitySecurity acl : des) {
            listAclToIndex.addAll(fact.saveDMEntitySecurity(acl));
        }
        List<DMEntity> items = FactoryInstantiator.getInstance().getDmEntityFactory().getEntities(entity.getPath());
        for (DMEntity it : items) {
            //generate sec for childrens, from previouslys created sec (to avoid xml parsing on each loop)
            for (DMEntitySecurity sec : des) {
                DMEntitySecurity nSec =
                        new DMEntitySecurity(it.getUid(), it.getType(), sec.getName(), sec.getSource(), sec.getType(),
                                sec.isRead(), sec.isWrite(), sec.isFullAccess(), it);
                listAclToIndex.addAll(fact.saveDMEntitySecurity(nSec));
            }
        }

        //Load symbolic links mapper


        return listAclToIndex;
    }


    @DmsEvent(eventName = { DmsEventName.ENTITY_ACL_UPDATE })
    public List<DMEntityACL> updateAclsRecursiveMode(Session session, List<DMEntitySecurity> securityItems, DMEntity entity)
            throws Exception
    {

        DMEntitySecurityFactory fact =
                org.kimios.kernel.security.FactoryInstantiator.getInstance().getDMEntitySecurityFactory();
        List<DMEntityACL> listAclToIndex = new ArrayList<DMEntityACL>();
        fact.cleanACLRecursive(entity);
        for (DMEntitySecurity acl : securityItems) {
            acl.setDmEntity(entity);
            listAclToIndex.addAll(fact.saveDMEntitySecurity(acl));
        }
        List<DMEntity> items = FactoryInstantiator.getInstance().getDmEntityFactory().getEntities(entity.getPath());
        for (DMEntity it : items) {
            //generate sec for childrens, from previouslys created sec (to avoid xml parsing on each loop)
            for (DMEntitySecurity sec : securityItems) {
                DMEntitySecurity nSec =
                        new DMEntitySecurity(it.getUid(), it.getType(), sec.getName(), sec.getSource(), sec.getType(),
                                sec.isRead(), sec.isWrite(), sec.isFullAccess(), it);
                listAclToIndex.addAll(fact.saveDMEntitySecurity(nSec));
            }
        }
        return listAclToIndex;
    }
}

