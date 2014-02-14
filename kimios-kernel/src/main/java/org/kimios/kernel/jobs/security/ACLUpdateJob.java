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
package org.kimios.kernel.jobs.security;

import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.events.EventContext;
import org.kimios.kernel.jobs.JobImpl;
import org.kimios.kernel.security.DMEntityACL;
import org.kimios.kernel.security.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ACLUpdateJob extends JobImpl
{
    private static Logger log = LoggerFactory.getLogger(ACLUpdateJob.class);

    private IACLUpdater updater;

    public ACLUpdateJob(IACLUpdater updater)
    {
        this.updater = updater;
    }

    public List<DMEntityACL> execute(Session session, Object... params) throws Exception
    {
        log.debug("Starting job execution");
        String xmlStream = (String) params[0];
        DMEntity entity = (DMEntity) params[1];
        List<DMEntityACL> acls = updater.updateAclsRecursiveMode(session, xmlStream, entity);
        EventContext.addParameter("acls", acls);
        log.debug("Ending job execution");
        return acls;
    }

    @Override
    public Object getInformation() throws Exception
    {
        return null;
    }
}

