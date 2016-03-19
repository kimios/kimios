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
import org.kimios.kernel.events.model.EventContext;
import org.kimios.kernel.jobs.JobImpl;
import org.kimios.kernel.security.model.DMEntityACL;
import org.kimios.kernel.security.model.DMEntitySecurity;
import org.kimios.kernel.security.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class ACLUpdateJob extends JobImpl<List<DMEntityACL>> {
    private static Logger log = LoggerFactory.getLogger(ACLUpdateJob.class);

    private IACLUpdater updater;

    private List<DMEntitySecurity> securities;

    private boolean appendMode = false;

    private List<DMEntityACL> removedAcls;

    public ACLUpdateJob(IACLUpdater updater, Session session, DMEntity dmEntity,
                        List<DMEntitySecurity> securities, List<DMEntityACL> removedAcls, boolean appendMode) {

        //generate task id
        super( UUID.randomUUID().toString() );

        this.updater = updater;
        this.dmEntity = dmEntity;
        this.securities = securities;
        this.setSession(session);
        this.appendMode = appendMode;
        this.removedAcls = removedAcls;
    }

    public List<DMEntityACL> execute() throws Exception {
        log.debug("Starting ACL Recursive Mode Update job execution for entity {}", dmEntity.getPath());
        List<DMEntityACL> acls = null;
        acls = updater.updateAclsRecursiveMode(getUserSession(), securities, dmEntity, appendMode, removedAcls);
        log.debug("Ending job execution");
        return acls;
    }


    @Override
    public Object getInformation() throws Exception {
        //FIXME : Return data the job
        StringBuilder builder = new StringBuilder();
        builder.append("updateAclJob for entity ");
        builder.append(dmEntity.getPath());
        return builder;
    }
}

