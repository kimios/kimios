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
package org.kimios.kernel.events.impl;

import org.kimios.kernel.dms.*;
import org.kimios.kernel.events.EventContext;
import org.kimios.kernel.events.GenericEventHandler;
import org.kimios.kernel.events.annotations.DmsEvent;
import org.kimios.kernel.events.annotations.DmsEventName;
import org.kimios.kernel.events.annotations.DmsEventOccur;
import org.kimios.kernel.log.ActionType;
import org.kimios.kernel.log.DMEntityLog;
import org.kimios.kernel.log.FactoryInstantiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class ActionLogger extends GenericEventHandler
{
    private static Logger logger = LoggerFactory.getLogger(ActionLogger.class);

    @DmsEvent(eventName = { DmsEventName.WORKSPACE_CREATE }, when = DmsEventOccur.AFTER)
    public void createWorkspace(Object[] paramsObj, Object returnObj, EventContext ctx) throws Exception
    {
        Workspace workspace = (Workspace) ctx.getEntity();
        workspace.setUid((Long) returnObj);
        ctx.setEntity(workspace);
        saveLog(new DMEntityLog<Workspace>(), ActionType.CREATE, ctx);
    }

    @DmsEvent(eventName = { DmsEventName.WORKSPACE_UPDATE }, when = DmsEventOccur.AFTER)
    public void updateWorkspace(Object[] paramsObj, Object returnObj, EventContext ctx) throws Exception
    {
        saveLog(new DMEntityLog<Workspace>(), ActionType.UPDATE, ctx);
    }

    @DmsEvent(eventName = { DmsEventName.WORKSPACE_DELETE }, when = DmsEventOccur.AFTER)
    public void deleteWorkspace(Object[] paramsObj, Object returnObj, EventContext ctx) throws Exception
    {
        saveLog(new DMEntityLog<Workspace>(), ActionType.DELETE, ctx);
    }

    @DmsEvent(eventName = { DmsEventName.FOLDER_CREATE }, when = DmsEventOccur.AFTER)
    public void createFolder(Object[] paramsObj, Object returnObj, EventContext ctx) throws Exception
    {
        Folder folder = (Folder) ctx.getEntity();
        folder.setUid((Long) returnObj);
        ctx.setEntity(folder);
        saveLog(new DMEntityLog<Folder>(), ActionType.CREATE, ctx);
    }

    @DmsEvent(eventName = { DmsEventName.FOLDER_UPDATE }, when = DmsEventOccur.AFTER)
    public void updateFolder(Object[] paramsObj, Object returnObj, EventContext ctx) throws Exception
    {
        saveLog(new DMEntityLog<Folder>(), ActionType.UPDATE, ctx);
    }

    @DmsEvent(eventName = { DmsEventName.FOLDER_DELETE }, when = DmsEventOccur.AFTER)
    public void deleteFolder(Object[] paramsObj, Object returnObj, EventContext ctx) throws Exception
    {
        saveLog(new DMEntityLog<Folder>(), ActionType.DELETE, ctx);
    }

    @DmsEvent(eventName = { DmsEventName.DOCUMENT_CREATE }, when = DmsEventOccur.AFTER)
    public void createDocument(Object[] paramsObj, Object returnObj, EventContext ctx)
    {
        Document document = (Document) ctx.getEntity();
        document.setUid((Long) returnObj);
        ctx.setEntity(document);
        saveLog(new DMEntityLog<Document>(), ActionType.CREATE, ctx);
    }

    @DmsEvent(eventName = { DmsEventName.DOCUMENT_UPDATE }, when = DmsEventOccur.AFTER)
    public void updateDocument(Object[] paramsObj, Object returnObj, EventContext ctx)
    {
        saveLog(new DMEntityLog<Document>(), ActionType.UPDATE, ctx);
    }

    @DmsEvent(eventName = { DmsEventName.DOCUMENT_DELETE }, when = DmsEventOccur.AFTER)
    public void deleteDocument(Object[] paramsObj, Object returnObj, EventContext ctx)
    {
        saveLog(new DMEntityLog<Document>(), ActionType.DELETE, ctx);
    }

    @DmsEvent(eventName = { DmsEventName.DOCUMENT_CHECKOUT }, when = DmsEventOccur.AFTER)
    public void checkoutDocument(Object[] paramsObj, Object returnObj, EventContext ctx)
    {
        saveLog(new DMEntityLog<Document>(), ActionType.CHECKOUT, ctx);
    }

    @DmsEvent(eventName = { DmsEventName.DOCUMENT_CHECKIN }, when = DmsEventOccur.AFTER)
    public void checkinDocument(Object[] paramsObj, Object returnObj, EventContext ctx)
    {
        saveLog(new DMEntityLog<Document>(), ActionType.CHECKIN, ctx);
    }

    @DmsEvent(eventName = { DmsEventName.DOCUMENT_ADD_RELATED }, when = DmsEventOccur.AFTER)
    public void addRelatedDocument(Object[] paramsObj, Object returnObj, EventContext ctx)
    {
        saveLog(new DMEntityLog<Document>(), ActionType.ADD_RELATED_DOCUMENT, ctx);
    }

    @DmsEvent(eventName = { DmsEventName.DOCUMENT_REMOVE_RELATED }, when = DmsEventOccur.AFTER)
    public void removeRelatedDocument(Object[] paramsObj, Object returnObj, EventContext ctx) throws Exception
    {
        saveLog(new DMEntityLog<Document>(), ActionType.REMOVE_RELATED_DOCUMENT, ctx);
    }

    @DmsEvent(eventName = { DmsEventName.DOCUMENT_VERSION_CREATE }, when = DmsEventOccur.AFTER)
    public void createDocumentVersion(Object[] paramsObj, Object returnObj, EventContext ctx) throws Exception
    {
        saveLog(new DMEntityLog<Document>(), ActionType.CREATE_DOCUMENT_VERSION, ctx);
    }

    @DmsEvent(eventName = { DmsEventName.DOCUMENT_VERSION_CREATE_FROM_LATEST }, when = DmsEventOccur.AFTER)
    public void createDocumentVersionFromLatest(Object[] paramsObj, Object returnObj, EventContext ctx)
            throws Exception
    {
        saveLog(new DMEntityLog<Document>(), ActionType.CREATE_DOCUMENT_VERSION_FROM_LATEST, ctx);
    }

    @DmsEvent(eventName = { DmsEventName.DOCUMENT_VERSION_UPDATE }, when = DmsEventOccur.AFTER)
    public void updateDocumentVersion(Object[] paramsObj, Object returnObj, EventContext ctx)
    {
        saveLog(new DMEntityLog<Document>(), ActionType.UPDATE_DOCUMENT_VERSION, ctx);
    }

    @DmsEvent(eventName = { DmsEventName.FILE_UPLOAD }, when = DmsEventOccur.AFTER)
    public void uploadVersionEnd(Object[] paramsObj, Object returnObj, EventContext ctx)
    {
        saveLog(new DMEntityLog<Document>(), ActionType.UPDATE_DOCUMENT_VERSION, ctx);
    }

    @DmsEvent(eventName = { DmsEventName.DOCUMENT_VERSION_READ }, when = DmsEventOccur.AFTER)
    public void startDownloadTransaction(Object[] paramsObj, Object returnObj, EventContext ctx)
    {
        saveLog(new DMEntityLog<Document>(), ActionType.READ, ctx);
    }

    @DmsEvent(eventName = { DmsEventName.DOCUMENT_TRASH }, when = DmsEventOccur.AFTER)
    public void trashDocument(Object[] paramsObj, Object returnObj, EventContext ctx)
    {
        ctx.setEntity((DMEntity)EventContext.getParameters().get("document"));
        saveLog(new DMEntityLog<Document>(), ActionType.TRASH_DOCUMENT, ctx);
    }

    @DmsEvent(eventName = { DmsEventName.DOCUMENT_UNTRASH }, when = DmsEventOccur.AFTER)
    public void untrashDocument(Object[] paramsObj, Object returnObj, EventContext ctx)
    {
        ctx.setEntity((DMEntity)EventContext.getParameters().get("document"));
        saveLog(new DMEntityLog<Document>(), ActionType.UNTRASH_DOCUMENT, ctx);
    }

    @DmsEvent(eventName = { DmsEventName.DOCUMENT_COPY }, when = DmsEventOccur.AFTER)
    public void copyDocument(Object[] paramsObj, Object returnObj, EventContext ctx)
    {
        ctx.setEntity((DMEntity)EventContext.getParameters().get("document"));
        saveLog(new DMEntityLog<Document>(), ActionType.COPY_DOCUMENT, ctx);
    }

    @DmsEvent(eventName = { DmsEventName.DOCUMENT_SHARED }, when = DmsEventOccur.AFTER)
    public void shareDocuments(Object[] paramsObj, Object returnObj, EventContext ctx)
    {
        List<Long> docIds = (List)paramsObj[1];
        for(Long id: docIds){
            ctx.setEntity(org.kimios.kernel.dms.FactoryInstantiator.getInstance().getDocumentFactory().getDocument(id));
            saveLog(new DMEntityLog<Document>(), ActionType.DOCUMENT_SHARED, ctx);
        }

    }

    private <T extends DMEntityImpl> void saveLog(DMEntityLog<T> log, int actionType, EventContext ctx)
    {
        try {
            logger.debug("Save log on: " + ctx.getEvent() + " - " +
                    (ctx.getEntity() != null ? ctx.getEntity().getUid() : " Entity is null"));
            log.setDate(new Date());
            log.setUser(ctx.getSession().getUserName());
            log.setUserSource(ctx.getSession().getUserSource());
            log.setAction(actionType);
            log.setDMEntity(ctx.getEntity());
            //if delete action, store item data as json
            if(actionType == ActionType.DELETE){
                if(ctx.getEntity() != null){
                    // store document path
                    log.setActionData(ctx.getEntity().getPath() + " - (owner:" + ctx.getEntity().getOwner() + "@" + ctx.getEntity().getOwnerSource() + ")");
                }
            }
            FactoryInstantiator.getInstance().getEntityLogFactory().saveLog(log);
        } catch (Exception e) {
            logger.error(
                    ctx.getEvent() + " - " + (ctx.getEntity() != null ? ctx.getEntity().getUid() : " Entity is null"),
                    e);
        }
    }
}

