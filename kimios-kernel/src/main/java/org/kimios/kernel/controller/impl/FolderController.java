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
package org.kimios.kernel.controller.impl;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.IFolderController;
import org.kimios.kernel.controller.utils.PathUtils;
import org.kimios.kernel.dms.*;
import org.kimios.kernel.events.EventContext;
import org.kimios.kernel.events.annotations.DmsEvent;
import org.kimios.kernel.events.annotations.DmsEventName;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.NamingException;
import org.kimios.kernel.exception.TreeException;
import org.kimios.kernel.log.DMEntityLog;
import org.kimios.kernel.security.DMEntitySecurity;
import org.kimios.kernel.security.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class FolderController extends AKimiosController implements IFolderController
{
    Logger log = LoggerFactory.getLogger(FolderController.class);

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IFolderController#getFolder(org.kimios.kernel.security.Session, long)
    */
    public Folder getFolder(Session session, long folderUid)
            throws ConfigException, DataSourceException, AccessDeniedException
    {
        Folder f = dmsFactoryInstantiator.getFolderFactory().getFolder(folderUid);
        if (f == null || !getSecurityAgent().isReadable(f,
                session.getUserName(), session.getUserSource(), session.getGroups()))
        {
            throw new AccessDeniedException();
        }

        return f;
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IFolderController#getFolder(org.kimios.kernel.security.Session, java.lang.String, long, int)
    */
    public Folder getFolder(Session session, String name, long parentUid, int parentType)
            throws ConfigException, DataSourceException, AccessDeniedException
    {
        Folder f = null;
        switch (parentType) {
            case DMEntityType.WORKSPACE:
                f = dmsFactoryInstantiator.getFolderFactory()
                        .getFolder(name, dmsFactoryInstantiator.getWorkspaceFactory().getWorkspace(parentUid));
                break;
            case DMEntityType.FOLDER:
                f = dmsFactoryInstantiator.getFolderFactory()
                        .getFolder(name, dmsFactoryInstantiator.getFolderFactory().getFolder(parentUid));
                break;
        }
        if (f == null || !getSecurityAgent().isReadable(f,
                session.getUserName(), session.getUserSource(), session.getGroups()))
        {
            throw new AccessDeniedException();
        }

        return f;
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IFolderController#getFolders(org.kimios.kernel.security.Session, long, int)
    */
    public List<Folder> getFolders(Session session, long parentUid)
            throws ConfigException, DataSourceException, AccessDeniedException
    {
        List<Folder> folders = new Vector<Folder>();
        DMEntity entity = dmsFactoryInstantiator.getDmEntityFactory().getEntity(parentUid);
        switch (entity.getType()) {
            case DMEntityType.WORKSPACE:
                Workspace par = dmsFactoryInstantiator.getWorkspaceFactory().getWorkspace(parentUid);
                folders = dmsFactoryInstantiator.getFolderFactory().getFolders(par);
                break;
            case DMEntityType.FOLDER:
                folders = dmsFactoryInstantiator.getFolderFactory()
                        .getFolders(dmsFactoryInstantiator.getFolderFactory().getFolder(parentUid));
                break;
        }
        return getSecurityAgent()
                .areReadable(folders, session.getUserName(), session.getUserSource(), session.getGroups());
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IFolderController#createFolder(org.kimios.kernel.security.Session, java.lang.String, long, int, boolean)
    */
    @DmsEvent(eventName = {DmsEventName.FOLDER_CREATE})
    public long createFolder(Session session, String name, long parentUid, boolean isSecurityInherited)
            throws NamingException, ConfigException, DataSourceException, AccessDeniedException
    {
        name = name.trim();
        PathUtils.validDmEntityName(name);
        DMEntityImpl parent = (DMEntityImpl) dmsFactoryInstantiator.getDmEntityFactory().getEntity(parentUid);
        log.info("DmEntity {} {}", parentUid, parent);
        if (parent.getType() == DMEntityType.WORKSPACE) {
            parent = dmsFactoryInstantiator.getWorkspaceFactory().getWorkspace(parentUid);
            if (dmsFactoryInstantiator.getFolderFactory().getFolder(name, (Workspace) parent) != null) {
                throw new NamingException("A folder named \"" + name + "\" already exists at the specified location.");
            }
        }
        if (parent.getType() == DMEntityType.FOLDER) {
            parent = dmsFactoryInstantiator.getFolderFactory().getFolder(parentUid);
            if (dmsFactoryInstantiator.getFolderFactory().getFolder(name, (Folder) parent) != null) {
                throw new NamingException("A folder named \"" + name + "\" already exists at the specified location.");
            }
        }

        Date creationDate = new Date();
        Folder f = new Folder(-1, name, session.getUserName(), session.getUserSource(), creationDate, parentUid,
                parent.getType());
        f.setUpdateDate(creationDate);

        log.info("Dm Entity " + parent.toString());
        if (getSecurityAgent()
                .isWritable(parent, session.getUserName(), session.getUserSource(), session.getGroups()))
        {

            f.setParent(parent);
            dmsFactoryInstantiator.getDmEntityFactory().generatePath(f);
            dmsFactoryInstantiator.getFolderFactory().saveFolder(f);
            if (isSecurityInherited) {
                Vector<DMEntitySecurity> v =
                        securityFactoryInstantiator.getDMEntitySecurityFactory().getDMEntitySecurities(parent);
                for (int i = 0; i < v.size(); i++) {
                    DMEntitySecurity des = new DMEntitySecurity(
                            f.getUid(),
                            f.getType(),
                            v.elementAt(i).getName(),
                            v.elementAt(i).getSource(),
                            v.elementAt(i).getType(),
                            v.elementAt(i).isRead(),
                            v.elementAt(i).isWrite(),
                            v.elementAt(i).isFullAccess(),
                            f);
                    securityFactoryInstantiator.getDMEntitySecurityFactory().saveDMEntitySecurity(des);
                }
            }
            return f.getUid();
        } else {
            throw new AccessDeniedException();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IFolderController#updateFolder(org.kimios.kernel.security.Session, long, java.lang.String, long, int)
    */
    @DmsEvent(eventName = {DmsEventName.FOLDER_UPDATE})
    public void updateFolder(Session session, long folderUid, String name, long parentUid)
            throws NamingException, TreeException, AccessDeniedException, ConfigException, DataSourceException
    {
        name = name.trim();
        PathUtils.validDmEntityName(name);
        DMEntityImpl parent = (DMEntityImpl) dmsFactoryInstantiator.getDmEntityFactory().getEntity(parentUid);
        if (parent.getType() == DMEntityType.WORKSPACE) {
            parent = dmsFactoryInstantiator.getWorkspaceFactory().getWorkspace(parentUid);
            Folder test = dmsFactoryInstantiator.getFolderFactory().getFolder(name, (Workspace) parent);
            if (test != null && test.getUid() != folderUid) {
                throw new NamingException("A folder named \"" + name + "\" already exists at the specified location.");
            }
        }
        if (parent.getType() == DMEntityType.FOLDER) {
            parent = dmsFactoryInstantiator.getFolderFactory().getFolder(parentUid);
            Folder test = dmsFactoryInstantiator.getFolderFactory().getFolder(name, (Folder) parent);
            if (test != null && test.getUid() != folderUid) {
                throw new NamingException("A folder named \"" + name + "\" already exists at the specified location.");
            }
        }
        Folder folder = dmsFactoryInstantiator.getFolderFactory().getFolder(folderUid);
        boolean proceed = false;
        if (parentUid != folder.getParentUid()) {
            // move
            proceed = getSecurityAgent()
                    .isWritable(folder.getParent(), session.getUserName(), session.getUserSource(), session.getGroups())
                    && !getSecurityAgent()
                    .hasAnyChildNotWritable(folder, session.getUserName(), session.getUserSource(), session.getGroups())
                    && !getSecurityAgent().hasAnyChildCheckedOut(folder, session.getUserName(), session.getUserSource())
                    && getSecurityAgent()
                    .isWritable(parent, session.getUserName(), session.getUserSource(), session.getGroups());
        } else {
            //name change
            proceed = getSecurityAgent()
                    .isWritable(folder, session.getUserName(), session.getUserSource(), session.getGroups());
        }
        if (proceed) {
            folder.setParentUid(parentUid);
            folder.setParentType(parent.getType());
            folder.setParent((DMEntityImpl) dmsFactoryInstantiator.getDmEntityFactory().getEntity(parentUid));
            if (folder.getParentType() == DMEntityType.WORKSPACE || (folder.getUid() != folder.getParentUid())) {
                log.debug("Preparing to move: updating path (current: " + folder.getPath() + ")");
                dmsFactoryInstantiator.getDmEntityFactory().updatePath(folder, name);
                folder.setUpdateDate(new Date());
                log.debug("Preparing to move: updated path (new: " + folder.getPath() + ")");
                dmsFactoryInstantiator.getFolderFactory().updateFolder(folder);
            } else {
                throw new TreeException("");
            }
        } else {
            throw new AccessDeniedException();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IFolderController#deleteFolder(org.kimios.kernel.security.Session, long)
    */
    @DmsEvent(eventName = {DmsEventName.FOLDER_DELETE})
    public boolean deleteFolder(Session session, long folderUid)
            throws AccessDeniedException, ConfigException, DataSourceException
    {
        Folder f = dmsFactoryInstantiator.getFolderFactory().getFolder(folderUid);
        if (getSecurityAgent().isWritable(f, session.getUserName(), session.getUserSource(), session.getGroups())
                && getSecurityAgent()
                .isWritable(f.getParent(), session.getUserName(), session.getUserSource(), session.getGroups()))
        {
            //Check if any child isn't writable
            if (getSecurityAgent().hasAnyChildCheckedOut(f, session.getUserName(), session.getUserSource())) {
                throw new AccessDeniedException();
            }
            if (getSecurityAgent()
                    .hasAnyChildNotWritable(f, session.getUserName(), session.getUserSource(), session.getGroups()))
            {
                throw new AccessDeniedException();
            }

            // delete full path
            dmsFactoryInstantiator.getDmEntityFactory().deteteEntities(f.getPath());
            EventContext.addParameter("removed", f);
            return true;
        } else {
            throw new AccessDeniedException();
        }
    }

    /**
     * Get DMS Logs for a given folder id
     */
    public Vector<DMEntityLog<Folder>> getLogs(Session session, long folderUid)
            throws AccessDeniedException, ConfigException, DataSourceException
    {
        Folder f = dmsFactoryInstantiator.getFolderFactory().getFolder(folderUid);
        if (getSecurityAgent().isReadable(f, session.getUserName(), session.getUserSource(), session.getGroups())) {
            return logFactoryInstantiator.getEntityLogFactory().getLogs(f);
        } else {
            throw new AccessDeniedException();
        }
    }
}

