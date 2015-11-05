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
package org.kimios.kernel.controller.impl;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.IWorkspaceController;
import org.kimios.kernel.dms.utils.PathUtils;
import org.kimios.kernel.dms.model.Workspace;
import org.kimios.kernel.events.model.EventContext;
import org.kimios.kernel.events.model.annotations.DmsEvent;
import org.kimios.kernel.events.model.annotations.DmsEventName;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.NamingException;
import org.kimios.kernel.log.model.DMEntityLog;
import org.kimios.kernel.security.model.Role;
import org.kimios.kernel.security.model.Session;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Workspace Controller :
 *
 * Workspace management methods
 */
@Transactional
public class WorkspaceController extends AKimiosController implements IWorkspaceController
{
    /**
     * Return workspace for a given id
     *
     * @return workspace
     */
    public Workspace getWorkspace(Session session, long workspaceUid)
            throws ConfigException, DataSourceException, AccessDeniedException
    {
        Workspace w = dmsFactoryInstantiator.getWorkspaceFactory().getWorkspace(workspaceUid);
        if (w == null || !getSecurityAgent().isReadable(w,
                session.getUserName(), session.getUserSource(), session.getGroups()))
        {
            throw new AccessDeniedException();
        }

        return w;
    }

    /**
     * Return workspace for a given name
     *
     * @return workspace
     */
    public Workspace getWorkspace(Session session, String workspaceName)
            throws ConfigException, DataSourceException, AccessDeniedException
    {
        Workspace w = dmsFactoryInstantiator.getWorkspaceFactory().getWorkspace(workspaceName);
        if (w == null || !getSecurityAgent().isReadable(w,
                session.getUserName(), session.getUserSource(), session.getGroups()))
        {
            throw new AccessDeniedException();
        }

        return w;
    }

    /**
     * Get workspace readable by a given user
     *
     * @return workspace list
     */
    public List<Workspace> getWorkspaces(Session session) throws ConfigException, DataSourceException
    {
        List<Workspace> workspaces = dmsFactoryInstantiator.getWorkspaceFactory().getWorkspaces();
        return getSecurityAgent()
                .areReadable(workspaces, session.getUserName(), session.getUserSource(), session.getGroups());
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IWorkspaceController#createWorkspace(org.kimios.kernel.security.Session, java.lang.String)
    */
    @DmsEvent(eventName = { DmsEventName.WORKSPACE_CREATE })
    public long createWorkspace(Session session, String name)
            throws NamingException, ConfigException, DataSourceException, AccessDeniedException
    {
        name = name.trim();
        PathUtils.validDmEntityName(name);
        if (dmsFactoryInstantiator.getWorkspaceFactory().getWorkspace(name) != null) {
            throw new NamingException("A workspace named \"" + name + "\" already exists.");
        }
        Date updateDate = new Date();
        Workspace w = new Workspace(-1, name, session.getUserName(), session.getUserSource(), updateDate);
        w.setUpdateDate(updateDate);
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.WORKSPACE, session.getUserName(), session.getUserSource()) != null ||
                getSecurityAgent().isAdmin(session.getUserName(), session.getUserSource()))
        {
            dmsFactoryInstantiator.getDmEntityFactory().generatePath(w);
            dmsFactoryInstantiator.getWorkspaceFactory().saveWorkspace(w);
            return w.getUid();
        } else {
            throw new AccessDeniedException();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IWorkspaceController#updateWorkspace(org.kimios.kernel.security.Session, long, java.lang.String)
    */
    @DmsEvent(eventName = { DmsEventName.WORKSPACE_UPDATE })
    public void updateWorkspace(Session session, long workspaceUid, String name)
            throws NamingException, ConfigException, DataSourceException, AccessDeniedException
    {
        name = name.trim();
        PathUtils.validDmEntityName(name);
        Workspace test = dmsFactoryInstantiator.getWorkspaceFactory().getWorkspace(name);
        if (test != null && test.getUid() != workspaceUid) {
            throw new NamingException("A workspace named \"" + name + "\" already exists.");
        }
        Workspace w = dmsFactoryInstantiator.getWorkspaceFactory().getWorkspace(workspaceUid);
        if (getSecurityAgent().isWritable(w,
                session.getUserName(), session.getUserSource(), session.getGroups()))
        {

            dmsFactoryInstantiator.getDmEntityFactory().updatePath(w, name);
            w.setUpdateDate(new Date());
            dmsFactoryInstantiator.getWorkspaceFactory().updateWorkspace(w);
        } else {
            throw new AccessDeniedException();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IWorkspaceController#deleteWorkspace(org.kimios.kernel.security.Session, long)
    */
    @DmsEvent(eventName = { DmsEventName.WORKSPACE_DELETE })
    public void deleteWorkspace(Session session, long workspaceUid)
            throws ConfigException, DataSourceException, AccessDeniedException
    {
        Workspace w = dmsFactoryInstantiator.getWorkspaceFactory().getWorkspace(workspaceUid);
        if (getSecurityAgent().isWritable(w,
                session.getUserName(), session.getUserSource(), session.getGroups()))
        {
            //Check if any child isn't writable
            if (getSecurityAgent().hasAnyChildCheckedOut(w, session.getUserName(), session.getUserSource())) {
                throw new AccessDeniedException();
            }
            if (getSecurityAgent()
                    .hasAnyChildNotWritable(w, session.getUserName(), session.getUserSource(), session.getGroups()))
            {
                throw new AccessDeniedException();
            }

            // delete full path
            dmsFactoryInstantiator.getDmEntityFactory().deteteEntities(w.getPath());
            EventContext.addParameter("removed", w);
        } else {
            throw new AccessDeniedException();
        }
    }

    /**
     * Get DMS Logs for a given workspace
     */
    public Vector<DMEntityLog<Workspace>> getLogs(Session session, long workspaceUid)
            throws AccessDeniedException, ConfigException, DataSourceException
    {
        Workspace w = dmsFactoryInstantiator.getWorkspaceFactory().getWorkspace(workspaceUid);
        if (getSecurityAgent().isReadable(w, session.getUserName(), session.getUserSource(), session.getGroups())) {
            return logFactoryInstantiator.getEntityLogFactory().getLogs(w);
        } else {
            throw new AccessDeniedException();
        }
    }
}

