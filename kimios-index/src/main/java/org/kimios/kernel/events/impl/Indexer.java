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
package org.kimios.kernel.events.impl;

import org.kimios.kernel.dms.*;
import org.kimios.kernel.events.EventContext;
import org.kimios.kernel.events.GenericEventHandler;
import org.kimios.kernel.events.annotations.DmsEvent;
import org.kimios.kernel.events.annotations.DmsEventName;
import org.kimios.kernel.events.annotations.DmsEventOccur;
import org.kimios.kernel.filetransfer.DataTransfer;
import org.kimios.kernel.index.IndexManager;
import org.kimios.kernel.security.DMEntityACL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

@Deprecated
public class Indexer extends GenericEventHandler
{
    private static Logger log = LoggerFactory.getLogger(Indexer.class);

    @DmsEvent(eventName = { DmsEventName.DOCUMENT_UPDATE }, when = DmsEventOccur.AFTER)
    public void documentUpdate(Object[] obj, Object retour, EventContext ctx) throws Exception
    {
        log.debug("Indexing document update: " + (Long) obj[1]);
        Document doc = FactoryInstantiator.getInstance()
                .getDocumentFactory().getDocument((Long) obj[1]);
        try {
            IndexManager.getInstance().indexDocument(doc);
        } catch (Exception e) {
            log.error(" index action Exception on Document " + doc.getUid(), e);
        }
    }

    @DmsEvent(eventName = { DmsEventName.DOCUMENT_VERSION_UPDATE }, when = DmsEventOccur.AFTER)
    public void documentVersionUpdate(Object[] obj, Object retour, EventContext ctx) throws Exception
    {
        log.debug("Indexing version update: " + (Long) obj[1]);
        Document doc = FactoryInstantiator.getInstance()
                .getDocumentFactory().getDocument((Long) obj[1]);
        try {
            IndexManager.getInstance().indexDocument(doc);
        } catch (Exception e) {
            log.error(" index action Exception on Document " + doc.getUid(), e);
        }
    }

    @DmsEvent(eventName = { DmsEventName.FILE_UPLOAD }, when = DmsEventOccur.AFTER)
    public void documentVersionUpdateUpload(Object[] obj, Object retour, EventContext ctx) throws Exception
    {
        log.debug("Indexing version update (after upload): " + (Long) obj[1]);
        Document doc = FactoryInstantiator.getInstance()
                .getDocumentVersionFactory().getDocumentVersion(((DataTransfer) retour).getDocumentVersionUid())
                .getDocument();
        try {
            IndexManager.getInstance().indexDocument(doc);
        } catch (Exception e) {
            log.error(" index action Exception on Document " + doc.getUid(), e);
        }
    }

    @DmsEvent(eventName = { DmsEventName.FOLDER_DELETE }, when = DmsEventOccur.AFTER)
    public void folderDelete(Object[] obj, Object retour, EventContext ctx) throws Exception
    {
        Folder fold = (Folder) EventContext.getParameters().get("removed");
        try {
            IndexManager.getInstance().deletePath(fold.getPath());
        } catch (Exception e) {
            log.error(" index removing action Exception on Document on folder remove " + fold.getPath(), e);
        }
    }

    @DmsEvent(eventName = { DmsEventName.WORKSPACE_DELETE }, when = DmsEventOccur.AFTER)
    public void workspaceDelete(Object[] obj, Object retour, EventContext ctx) throws Exception
    {
        Workspace wk = (Workspace) EventContext.getParameters().get("removed");
        try {
            IndexManager.getInstance().deletePath(wk.getPath());
        } catch (Exception e) {
            log.error(" index removing action Exception on Document on Workspace remove " + wk.getPath(), e);
        }
    }

    @DmsEvent(eventName = { DmsEventName.DOCUMENT_DELETE }, when = DmsEventOccur.AFTER)
    public void documentDelete(Object[] obj, Object retour, EventContext ctx) throws Exception
    {
        Document doc = new Document();
        doc.setUid((Long) obj[1]);
        try {
            IndexManager.getInstance().deleteDocument(doc);
        } catch (Exception e) {
            log.error(" index removing action Exception on Document " + doc.getUid(), e);
        }
    }

    @DmsEvent(eventName = { DmsEventName.ENTITY_ACL_UPDATE }, when = DmsEventOccur.AFTER)
    public void updateAcl(Object[] obj, Object retour, EventContext ctx)
    {
        try {
            log.debug("Starting Launching index acl update process " + Thread.currentThread().getName());
            List<DMEntityACL> acls = (List<DMEntityACL>) EventContext.getParameters().get("acls");
            if (acls == null) {
                acls = (List<DMEntityACL>) retour;
            }
            if (acls == null) {
                log.debug("No datas for acl update " + Thread.currentThread().getName());
                return;
            }
            try {
                log.debug("Launching index acl update process " + Thread.currentThread().getId());
                HashMap<Long, List<DMEntityACL>> hash = new HashMap<Long, List<DMEntityACL>>();
                for (DMEntityACL r : acls) {
                    if (r.getDmEntityType() == DMEntityType.DOCUMENT) {
                        if (hash.containsKey(r.getDmEntityUid())) {
                            hash.get(r.getDmEntityUid()).add(r);
                        } else {
                            hash.put(r.getDmEntityUid(), new Vector<DMEntityACL>());
                            hash.get(r.getDmEntityUid()).add(r);
                        }
                    }
                }
                for (Long uid : hash.keySet()) {
                    IndexManager.getInstance().updateAcls(uid, hash.get(uid));
                }
                IndexManager.getInstance().commit();
                log.debug("Ending index acl update process " + Thread.currentThread().getId());
            } catch (Throwable ex) {
                log.error("Index acl update failed " + Thread.currentThread().getId(), ex);
            }
        } catch (Exception e) {
            log.error("Starting Index acl update failed", e);
        }
    }

    @DmsEvent(eventName = { DmsEventName.FOLDER_UPDATE }, when = DmsEventOccur.AFTER)
    public void updateFolder(Object[] o, Object retour, EventContext ctx)
    {
        try {
            String oldPath = ctx.getEntity().getPath();
            FactoryInstantiator fc = FactoryInstantiator.getInstance();
            Folder f = fc.getFolderFactory().getFolder((Long) o[1]);
            String newPath = f.getPath();
            IndexManager.getInstance().updatePath(oldPath, newPath);
        } catch (Exception e) {
            log.error("Exception during index update : ", e);
        }
    }

    @DmsEvent(eventName = { DmsEventName.WORKSPACE_UPDATE }, when = DmsEventOccur.AFTER)
    public void updateWorkspace(Object[] o, Object retour, EventContext ctx)
    {
        try {
            Workspace w = FactoryInstantiator.getInstance().getWorkspaceFactory().getWorkspace((Long) o[1]);
            String oldName = ctx.getEntity().getName();
            if (!w.getName().equals(oldName)) {
                String newPath = w.getPath();
                String oldPath = "/" + oldName;
                IndexManager.getInstance().updatePath(oldPath, newPath);
            }
        } catch (Exception e) {
            log.error("Exception during index update : ", e);
        }
    }
}

