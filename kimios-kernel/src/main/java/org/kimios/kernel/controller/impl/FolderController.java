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
import org.kimios.kernel.controller.IFolderController;
import org.kimios.kernel.dms.model.*;
import org.kimios.kernel.dms.utils.PathUtils;
import org.kimios.kernel.dms.*;
import org.kimios.kernel.events.model.EventContext;
import org.kimios.api.events.annotations.DmsEvent;
import org.kimios.api.events.annotations.DmsEventName;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.NamingException;
import org.kimios.kernel.exception.TreeException;
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.log.model.DMEntityLog;
import org.kimios.kernel.security.model.DMEntitySecurity;
import org.kimios.kernel.security.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
public class FolderController extends AKimiosController implements IFolderController {

    private static Logger log = LoggerFactory.getLogger(FolderController.class);




    /* (non-Javadoc)
        * @see org.kimios.kernel.controller.impl.IFolderController#getFolder(org.kimios.kernel.security.Session, long)
        */
    public Folder getFolder(Session session, long folderUid)
            throws ConfigException, DataSourceException, AccessDeniedException {
        Folder f = dmsFactoryInstantiator.getFolderFactory().getFolder(folderUid);
        if (f == null || !getSecurityAgent().isReadable(f,
                session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }

        return f;
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IFolderController#getFolder(org.kimios.kernel.security.Session, java.lang.String, long, int)
    */
    public Folder getFolder(Session session, String name, long parentUid, int parentType)
            throws ConfigException, DataSourceException, AccessDeniedException {
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
                session.getUserName(), session.getUserSource(), session.getGroups())) {
            throw new AccessDeniedException();
        }

        return f;
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IFolderController#getFolders(org.kimios.kernel.security.Session, long, int)
    */
    public List<Folder> getFolders(Session session, long parentUid)
            throws ConfigException, DataSourceException, AccessDeniedException {
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
            throws NamingException, ConfigException, DataSourceException, AccessDeniedException {
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

        EventContext.get().setParentEntity(parent);

        Date creationDate = new Date();
        Folder f = new Folder(-1, name, session.getUserName(), session.getUserSource(), creationDate, parentUid,
                parent.getType());
        f.setUpdateDate(creationDate);
        if (getSecurityAgent()
                .isWritable(parent, session.getUserName(), session.getUserSource(), session.getGroups())) {

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
            EventContext.get().setEntity(f);

            return f.getUid();
        } else {
            throw new AccessDeniedException();
        }
    }

    @DmsEvent(eventName = {DmsEventName.FOLDER_CREATE})
    public long createVirtualFolder(Session session, Long id, String name, Long parentId, List<MetaValue> metaValues)
            throws NamingException, ConfigException, DataSourceException, AccessDeniedException {


        Folder f = null;
        DMEntityImpl parent = null;
        if(parentId != null){
            parent = (DMEntityImpl)dmsFactoryInstantiator.getDmEntityFactory().getEntity(parentId);

        }

        if(id != null){
            f = dmsFactoryInstantiator.getFolderFactory().getFolder(id);
            f.setUpdateDate(new Date());
        }
        if(id == null){
            if(parent == null){
                parent = dmsFactoryInstantiator.getWorkspaceFactory().getWorkspace("Public Folders");
            }
            Date creationDate = new Date();
            f = new Folder(-1, name, session.getUserName(), session.getUserSource(), creationDate, parent.getUid(),
                    parent.getType());
            f.setUpdateDate(creationDate);
        }



        if(parent != null && getSecurityAgent()
                .isWritable(parent, session.getUserName(), session.getUserSource(), session.getGroups())){
            f.setParent(parent);
            dmsFactoryInstantiator.getDmEntityFactory().generatePath(f);
            dmsFactoryInstantiator.getFolderFactory().saveFolder(f);
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

            //define metadata
            //TODO: should clean meta !
            for (MetaValue metaValue : metaValues) {
                VirtualFolderMetaData virtualFolderMetaData = new VirtualFolderMetaData();
                virtualFolderMetaData.setVirtualFolderId(f.getUid());
                virtualFolderMetaData.setMetaId(metaValue.getMetaUid());
                Meta m = FactoryInstantiator.getInstance().getMetaFactory().getMeta(metaValue.getMetaUid());
                virtualFolderMetaData.setMeta(m);
                switch (m.getMetaType()) {
                    case MetaType.STRING:
                        virtualFolderMetaData.setStringValue(metaValue.getValue().toString());
                        break;
                    case MetaType.DATE:
                        virtualFolderMetaData.setDateValue((Date) metaValue.getValue());
                        break;
                }
                log.debug("added virtual folder meta data: {}", virtualFolderMetaData);
                dmsFactoryInstantiator.getVirtualFolderFactory().saveOrUpdateMeta(virtualFolderMetaData);

                log.debug("added virtual folder meta data: {}", virtualFolderMetaData);
            }


            List<VirtualFolderMetaData> metaDataList =
                    dmsFactoryInstantiator.getVirtualFolderFactory().virtualFolderMetaDataList(f);
            EventContext.get().addParameter("virtualFolder",
                    dmsFactoryInstantiator.getFolderFactory().getFolder(f.getUid()));
            EventContext.get().addParameter("virtualFolderMetas", metaDataList
                    );

            log.debug("virtual folder meta datas submitted to event context : {}", metaDataList);

            return f.getUid();
        }
        else
            throw new AccessDeniedException();
    }


    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IFolderController#updateFolder(org.kimios.kernel.security.Session, long, java.lang.String, long, int)
    */
    @DmsEvent(eventName = {DmsEventName.FOLDER_UPDATE})
    public void updateFolder(Session session, long folderUid, String name, long parentUid)
            throws NamingException, TreeException, AccessDeniedException, ConfigException, DataSourceException {
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
            throws AccessDeniedException, ConfigException, DataSourceException {
        Folder f = dmsFactoryInstantiator.getFolderFactory().getFolder(folderUid);
        if (getSecurityAgent().isWritable(f, session.getUserName(), session.getUserSource(), session.getGroups())
                && getSecurityAgent()
                .isWritable(f.getParent(), session.getUserName(), session.getUserSource(), session.getGroups())) {
            //Check if any child isn't writable
            if (getSecurityAgent().hasAnyChildCheckedOut(f, session.getUserName(), session.getUserSource())) {
                throw new AccessDeniedException();
            }
            if (getSecurityAgent()
                    .hasAnyChildNotWritable(f, session.getUserName(), session.getUserSource(), session.getGroups())) {
                throw new AccessDeniedException();
            }


            List<VirtualFolderMetaData> metaValues = dmsFactoryInstantiator.getVirtualFolderFactory().virtualFolderMetaDataList(f);
            for(VirtualFolderMetaData metaData: metaValues){
                dmsFactoryInstantiator.getVirtualFolderFactory().deleteMeta(metaData);
            }
            ((HFactory)dmsFactoryInstantiator.getVirtualFolderFactory()).flush();

            // delete full path
            dmsFactoryInstantiator.getDmEntityFactory().deleteEntities(f.getPath());
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
            throws AccessDeniedException, ConfigException, DataSourceException {
        Folder f = dmsFactoryInstantiator.getFolderFactory().getFolder(folderUid);
        if (getSecurityAgent().isReadable(f, session.getUserName(), session.getUserSource(), session.getGroups())) {
            return logFactoryInstantiator.getEntityLogFactory().getLogs(f);
        } else {
            throw new AccessDeniedException();
        }
    }

    /***
     *
     * Load virtual folder meta datas
     *
     * @param session
     * @param folderId
     * @return
     * @throws ConfigException
     * @throws DataSourceException
     * @throws AccessDeniedException
     */
    @Override
    public List<MetaValue> listMetaValues(Session session, long folderId)
            throws ConfigException, DataSourceException, AccessDeniedException
    {
        Folder f = dmsFactoryInstantiator.getFolderFactory().getFolder(folderId);
        if (getSecurityAgent().isReadable(f, session.getUserName(), session.getUserSource(), session.getGroups())) {
            List<VirtualFolderMetaData> virtualFolderMetaDatas =
                    dmsFactoryInstantiator.getVirtualFolderFactory().virtualFolderMetaDataList(f);

            List<MetaValue> metaValues = new ArrayList<MetaValue>();
            for(VirtualFolderMetaData m: virtualFolderMetaDatas){
                if(m.getMeta().getMetaType() == MetaType.STRING){
                    MetaStringValue mv = new MetaStringValue();
                    mv.setValue(m.getStringValue());
                    mv.setMeta(m.getMeta());
                    metaValues.add(mv);
                } else {
                    MetaDateValue mv = new MetaDateValue();
                    mv.setValue(m.getDateValue());
                    mv.setMeta(m.getMeta());
                    metaValues.add(mv);
                }
            }

            return metaValues;
        } else {
            throw new AccessDeniedException();
        }
    }

    /***
     *
     * Get Folders with Meta Datas
     *
     * @param session Session Id
     * @param folders Folders Ids List
     * @return
     * @throws ConfigException
     * @throws DataSourceException
     * @throws AccessDeniedException
     */
    @Override
    public Map<org.kimios.kernel.ws.pojo.Folder, List<org.kimios.kernel.ws.pojo.MetaValue>>
            getFolderWithMetaDatas(Session session, List<Long> folders)
            throws ConfigException, DataSourceException, AccessDeniedException
    {
        Map<org.kimios.kernel.ws.pojo.Folder, List<org.kimios.kernel.ws.pojo.MetaValue>> folderListMap =
                new HashMap<org.kimios.kernel.ws.pojo.Folder, List<org.kimios.kernel.ws.pojo.MetaValue>>();
        for(Long folderId: folders){
            Folder f = dmsFactoryInstantiator.getFolderFactory().getFolder(folderId);
            if (getSecurityAgent().isReadable(f, session.getUserName(), session.getUserSource(), session.getGroups())) {
                List<VirtualFolderMetaData> virtualFolderMetaDatas =
                        dmsFactoryInstantiator.getVirtualFolderFactory().virtualFolderMetaDataList(f);
                List<org.kimios.kernel.ws.pojo.MetaValue> metaValues = new ArrayList<org.kimios.kernel.ws.pojo.MetaValue>();
                for(VirtualFolderMetaData m: virtualFolderMetaDatas){
                    org.kimios.kernel.ws.pojo.MetaValue mv =
                            new org.kimios.kernel.ws.pojo.MetaValue();
                    mv.setValue(m.getStringValue());
                    mv.setMeta(m.getMeta().toPojo());
                    metaValues.add(mv);
                }
                folderListMap.put(f.toPojo(), metaValues);
            } else {
                throw new AccessDeniedException();
            }
        }
        return folderListMap;
    }
}

