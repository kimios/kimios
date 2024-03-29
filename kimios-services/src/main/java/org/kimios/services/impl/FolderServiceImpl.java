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
package org.kimios.services.impl;

import org.kimios.exceptions.AccessDeniedException;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.ws.pojo.Bookmark;
import org.kimios.kernel.ws.pojo.DMEntityWrapper;
import org.kimios.kernel.ws.pojo.DataMessage;
import org.kimios.kernel.ws.pojo.Folder;
import org.kimios.kernel.ws.pojo.MetaValue;
import org.kimios.kernel.ws.pojo.task.TaskGetFoldersAndSendData;
import org.kimios.kernel.ws.pojo.web.FolderUidListParam;
import org.kimios.webservices.FolderService;
import org.kimios.webservices.exceptions.DMServiceException;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@WebService(targetNamespace = "http://kimios.org", serviceName = "FolderService", name = "FolderService")
public class FolderServiceImpl extends CoreService implements FolderService
{
    public Folder getFolder(String sessionUid, long folderUid) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionUid);

            Folder f = folderController.getFolder(session, folderUid).toPojo();

            return f;
        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    @Override
    public DMEntityWrapper getFolderWrapper(String sessionUid, long folderUid) throws DMServiceException {
        try {

            Session session = getHelper().getSession(sessionUid);

            Folder f = folderController.getFolder(session, folderUid).toPojo();

            return new DMEntityWrapper(
                    f,
                    this.securityController.canRead(session, f.getUid()),
                    this.securityController.canWrite(session, f.getUid()),
                    this.securityController.hasFullAccess(session, f.getUid())
            );
        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public Folder[] getFolders(String sessionUid, long parentUid) throws DMServiceException
    {
        try {
            Session session = getHelper().getSession(sessionUid);
            List<org.kimios.kernel.dms.model.Folder> v = folderController.getFolders(session, parentUid);
            Folder[] r = new Folder[v.size()];
            int i = 0;
            for (org.kimios.kernel.dms.model.Folder it : v) {
                r[i] = it.toPojo();
                i++;
            }
            return r;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    @Override
    public List<DMEntityWrapper> getFolderWrappers(String sessionId, long parentId) throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            return this.folderController.getFolders(session, parentId).stream()
                    .map(folder -> folder.toPojo())
                    .map(folder -> new DMEntityWrapper(
                            folder,
                            this.securityController.canRead(session, folder.getUid()),
                            this.securityController.canWrite(session, folder.getUid()),
                            this.securityController.hasFullAccess(session, folder.getUid())
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    @Override
    public void getFoldersFolders(FolderUidListParam folderUidListParam) throws DMServiceException {
        Session session;
        try {
            session = getHelper().getSession(folderUidListParam.getSessionId());
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
        if (session == null) {
            throw getHelper().convertException(new AccessDeniedException());
        }
        List<Bookmark> bookmarkList = this.documentController.getBookmarks(session)
                .stream().map(bookmark -> bookmark.toPojo())
                .collect(Collectors.toList());
        List<Long> bookmarkedUidList = bookmarkList.stream().map(bookmark -> bookmark.getDmEntityUid())
                .collect(Collectors.toList());
        for (Long uid : folderUidListParam.getFolderUidList()) {
            Folder parentFolder = null;
            try{
                parentFolder = this.getFolder(folderUidListParam.getSessionId(), uid);
            } catch (Exception e) {

            }
            // folder with this uid has not been found
            // maybe this is a workspace
            org.kimios.kernel.ws.pojo.Workspace workspace = null;
            try {
                if (parentFolder == null) {
                    workspace = this.workspaceController.getWorkspace(session, uid).toPojo();
                }
            } catch (Exception e) {
                throw getHelper().convertException(e);
            }

            this.camelTool.getFoldersAndSendData(new TaskGetFoldersAndSendData(
                    session,
                    parentFolder != null ? parentFolder : workspace,
                    bookmarkedUidList
            ));
        }
    }

    public long createFolder(String sessionUid, String name, long parentUid, boolean isSecurityInherited)
            throws DMServiceException
    {
        try {
            Session session = getHelper().getSession(sessionUid);
            long uid = folderController.createFolder(session, name, parentUid, isSecurityInherited);
            return uid;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public void updateFolder(String sessionUid, long folderUid, String name, long parentUid)
            throws DMServiceException
    {
        try {
            Session session = getHelper().getSession(sessionUid);
            folderController.updateFolder(session, folderUid, name, parentUid);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public void deleteFolder(String sessionUid, long folderUid) throws DMServiceException
    {

        try {
            Session session = getHelper().getSession(sessionUid);
            folderController.deleteFolder(session, folderUid);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    @Override
    public MetaValue[] getFolderMetaValues(String sessionId, long folderId) throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            List<org.kimios.kernel.dms.model.MetaValue> values = folderController.listMetaValues(session, folderId);
            List<MetaValue> mvArray = new ArrayList<MetaValue>();
            for(org.kimios.kernel.dms.model.MetaValue v: values) {
                org.kimios.kernel.ws.pojo.MetaValue pojo = new org.kimios.kernel.ws.pojo.MetaValue();
                pojo.setMeta(v.getMeta().toPojo());
                pojo.setMetaId(v.getMetaUid());
                pojo.setValue(v.getValue());
                mvArray.add(pojo);
            }

            return mvArray.toArray(new MetaValue[]{});
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    @Override
    public HashMap<Folder, List<MetaValue>>
            getFoldersWithMetaValues(String sessionId, List<Long> foldersIds) throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            return (HashMap)folderController.getFolderWithMetaDatas(session, foldersIds);

        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    @Override
    public boolean canBeMoved(String sessionId, long folderId) throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            return this.folderController.canBeMoved(session, folderId);

        } catch (Exception e) {
            throw getHelper().convertException(e);
        }

    }

    @Override
    public boolean isWriteable(String sessionId, long folderId) throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            return this.folderController.isWriteable(session, folderId);

        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    @Override
    public DataMessage sendData(DataMessage dataMessage) throws DMServiceException {
        return null;
    }
}

