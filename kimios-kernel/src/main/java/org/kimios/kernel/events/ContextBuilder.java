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
package org.kimios.kernel.events;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.*;
import org.kimios.kernel.events.annotations.DmsEventName;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.filetransfer.DataTransfer;
import org.kimios.kernel.filetransfer.DataTransferFactory;
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.security.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class ContextBuilder
{
    final private static Logger log = LoggerFactory.getLogger(ContextBuilder.class);

    private static String document = "DOCUMENT";

    private static String documentVersion = "DOCUMENT_VERSION";

    private static String folder = "FOLDER";

    private static String workspace = "WORKSPACE";

    private static String fileTransfer = "FILE";

    private static String extension = "EXTENSION";

    private static String[] documentMethod = { "createDocument", "updateDocument", "deleteDocument",
            "checkoutDocument", "checkinDocument", "addRelatedDocument", "removeRelatedDocument", "createDocumentWithProperties", "createDocumentFromFullPathWithProperties" };

    private static String[] folderMethod = { "createFolder", "updateFolder", "deleteFolder" };

    private static String[] workspaceMethod = { "createWorkspace", "updateWorkspace", "deleteWorkspace" };

    private static String[] documentVersionMethod = { "createDocumentVersion", "createDocumentVersionFromLatest",
            "updateDocumentVersion", "deleteDocumentVersion", "startDownloadTransaction" };

    private static String[] fileTransferMethod = { "endUploadTransaction" };

    private static String[] extensionMethods = { "setAttribute", "trash", "untrash" };

    public static EventContext buildContext(DmsEventName n, Method invokedMethod, Object[] arguments)
    {
        try {
            EventContext ctx = EventContext.get();
            ctx.setEvent(n);
            ctx.setContextParameters(arguments);
            if(arguments[0] instanceof Session)
                ctx.setSession((Session) arguments[0]);

            if (n.name().startsWith(documentVersion)) {
                documentVersionContextBuilder(invokedMethod, arguments, ctx);
            }
            if (n.name().startsWith(document)) {
                documentContextBuilder(invokedMethod, arguments, ctx);
            }
            if (n.name().startsWith(folder)) {
                folderContextBuilder(invokedMethod, arguments, ctx);
            }
            if (n.name().startsWith(workspace)) {
                workspaceContextBuilder(invokedMethod, arguments, ctx);
            }
            if (n.name().startsWith(fileTransfer)) {
                fileTransferContextBuilder(invokedMethod, arguments, ctx);
            }
            if(n.name().startsWith(extension)){
                extensionContextBuilder(invokedMethod, arguments, ctx);
            }
            if (ctx.getEntity() != null) {
                String path = ctx.getEntity().getPath();
                ctx.setParentEntity(FactoryInstantiator.getInstance().getDmEntityFactory()
                        .getEntity(path.substring(0, path.lastIndexOf("/"))));
            }


            return ctx;
        } catch (DataSourceException e) {
            log.error("Exception while building context", e);
        } catch (ConfigException e) {
            log.error("Exception while building context", e);
        }
        log.error("Returning null context");
        return null;
    }

    private static EventContext folderContextBuilder(Method mi, Object[] arguments, EventContext ctx)
            throws DataSourceException, ConfigException
    {
        String name = mi.getName();
        if (name.equalsIgnoreCase(folderMethod[0])) {
            /*
            * building dm entity
            */
            Folder fold = new Folder();
            fold.setUid(-1);
            fold.setName(arguments[1] != null ? arguments[1].toString() : "");
            fold.setParentUid((Long) arguments[2]);
            FactoryInstantiator.getInstance().getDmEntityFactory().generatePath(fold);
            ctx.setEntity(fold);
        }
        if (name.equalsIgnoreCase(folderMethod[1])) {
            /*
            * Load the current (before delete)
            */
            FolderFactory fc = FactoryInstantiator.getInstance().getFolderFactory();
            Folder fold = fc.getFolder((Long) arguments[1]);
            HFactory t = (HFactory) fc;
            t.getSession().evict(fold);
            ctx.setEntity(fold);
        }
        if (name.equalsIgnoreCase(folderMethod[2])) {
            /*
            * Load the current (before Update)
            */
            FolderFactory fc = FactoryInstantiator.getInstance().getFolderFactory();
            Folder fold = fc.getFolder((Long) arguments[1]);
            HFactory t = (HFactory) fc;
            t.getSession().evict(fold);
            ctx.setEntity(fold);
        }

        return ctx;
    }

    private static EventContext documentContextBuilder(Method mi, Object[] arguments,  EventContext ctx)
            throws DataSourceException, ConfigException
    {
        String name = mi.getName();

        // createDocument
        if (name.equalsIgnoreCase(documentMethod[0])) {
            if (arguments.length == 3) {
                /*
                   Create document from full path
                */
                Document doc = new Document();
                doc.setUid(-1);
                String docPath = ((String) arguments[1]);
                String fldPath = docPath.substring(0, docPath.lastIndexOf("/"));
                Folder f = (Folder) FactoryInstantiator.getInstance().getDmEntityFactory().getEntity(fldPath);
                doc.setFolderUid(f.getUid());
                doc.setPath((String) arguments[1]);
                ctx.setEntity(doc);
            } else {
                Document doc = new Document();
                doc.setUid(-1);
                doc.setName(arguments[1] != null ? arguments[1].toString() : "");
                doc.setMimeType(arguments[3] != null ? arguments[3].toString() : "");
                doc.setFolderUid((Long) arguments[4]);
                doc.setType(DMEntityType.DOCUMENT);
                FactoryInstantiator.getInstance().getDmEntityFactory().generatePath(doc);
                ctx.setEntity(doc);
            }
        }

        // updateDocument
        if (name.equalsIgnoreCase(documentMethod[1])) {
            DocumentFactory fc = FactoryInstantiator.getInstance().getDocumentFactory();
            Document document = fc.getDocument((Long) arguments[1]);
            HFactory t = (HFactory) fc;
            //t.getSession().evict(document);
            ctx.setEntity(document);
        }

        // deleteDocument
        if (name.equalsIgnoreCase(documentMethod[2])) {
            DocumentFactory fc = FactoryInstantiator.getInstance().getDocumentFactory();
            Document document = fc.getDocument((Long) arguments[1]);
            HFactory t = (HFactory) fc;
            //t.getSession().evict(document);
            ctx.setEntity(document);
        }

        // checkoutDocument
        if (name.equalsIgnoreCase(documentMethod[3])) {
            DocumentFactory fc = FactoryInstantiator.getInstance().getDocumentFactory();
            Document document = fc.getDocument((Long) arguments[1]);
            HFactory t = (HFactory) fc;
            //t.getSession().evict(document);
            ctx.setEntity(document);
        }

        // checkinDocument
        if (name.equalsIgnoreCase(documentMethod[4])) {
            DocumentFactory fc = FactoryInstantiator.getInstance().getDocumentFactory();
            Document document = fc.getDocument((Long) arguments[1]);
            HFactory t = (HFactory) fc;
            //t.getSession().evict(document);
            ctx.setEntity(document);
        }

        // addRelatedDocument
        if (name.equalsIgnoreCase(documentMethod[5])) {
            DocumentFactory fc = FactoryInstantiator.getInstance().getDocumentFactory();
            Document document = fc.getDocument((Long) arguments[1]);
            HFactory t = (HFactory) fc;
            t.getSession().evict(document);
            ctx.setEntity(document);
            EventContext.addParameter("relatedDocument", fc.getDocument((Long) arguments[2]));
        }

        // removeRelatedDocument
        if (name.equalsIgnoreCase(documentMethod[6])) {
            DocumentFactory fc = FactoryInstantiator.getInstance().getDocumentFactory();
            Document document = fc.getDocument((Long) arguments[1]);
            HFactory t = (HFactory) fc;
            //t.getSession().evict(document);
            ctx.setEntity(document);
            EventContext.addParameter("relatedDocument", fc.getDocument((Long) arguments[2]));
        }


        // createDocumentWithProperties
        if (name.equalsIgnoreCase(documentMethod[7])) {
            Document doc = (Document)EventContext.getParameters().get("document");
            ctx.setEntity(doc);
        }

        // createDocumentFromFullPathWithProperties
        if (name.equalsIgnoreCase(documentMethod[8])) {
            Document doc = (Document)EventContext.getParameters().get("document");
            ctx.setEntity(doc);
        }

        return ctx;
    }

    private static EventContext documentVersionContextBuilder(Method mi, Object[] arguments, EventContext ctx)
            throws DataSourceException, ConfigException
    {
        String name = mi.getName();

        // createDocumentVersion
        if (name.equalsIgnoreCase(documentVersionMethod[0])) {
            DocumentFactory fc = FactoryInstantiator.getInstance().getDocumentFactory();
            Document doc = fc.getDocument((Long) arguments[1]);
            ctx.setEntity(doc);
            DocumentVersion dv =
                    FactoryInstantiator.getInstance().getDocumentVersionFactory().getLastDocumentVersion(doc);
            EventContext.addParameter("previousVersion", dv);
        }

        // createDocumentVersionFromLatest
        if (name.equalsIgnoreCase(documentVersionMethod[1])) {
            DocumentFactory fc = FactoryInstantiator.getInstance().getDocumentFactory();
            Document doc = fc.getDocument((Long) arguments[1]);
            ctx.setEntity(doc);
            DocumentVersion dv =
                    FactoryInstantiator.getInstance().getDocumentVersionFactory().getLastDocumentVersion(doc);
            EventContext.addParameter("previousVersion", dv);
        }

        // updateDocumentVersion
        if (name.equalsIgnoreCase(documentVersionMethod[2])) {
            DocumentVersionFactory fc = FactoryInstantiator.getInstance().getDocumentVersionFactory();
            Document tDoc =
                    FactoryInstantiator.getInstance().getDocumentFactory().getDocument((Long) arguments[1]);
            DocumentVersion version = fc.getLastDocumentVersion(tDoc);
            HFactory t = (HFactory) fc;
            if(version != null){
                t.getSession().evict(version);
                Document document = version.getDocument();
                ctx.setEntity(document);
            }

            EventContext.addParameter("version", version);


            log.trace("added version to context: " + version);
            log.trace("item " + document);

            //check if document type set
            if (((Long) arguments[2]) > 0) {
                DocumentTypeFactory dtF = FactoryInstantiator.getInstance().getDocumentTypeFactory();
                DocumentType dt = dtF.getDocumentType((Long) arguments[2]);
                EventContext.addParameter("documentTypeSet", dt);
            }
        }

        // deleteDocumentVersion
        if (name.equalsIgnoreCase(documentVersionMethod[3])) {
            try {
                DocumentVersionFactory fc = FactoryInstantiator.getInstance().getDocumentVersionFactory();
                DocumentVersion version = fc.getDocumentVersion((Long) arguments[1]);
                HFactory t = (HFactory) fc;
                t.getSession().evict(version);
                Document document = version.getDocument();
                ctx.setEntity(document);
                EventContext.addParameter("version", version);
            } catch (Exception e) {
                log.error("Error while build version context", e);
            }
        }

        // startUploadTransaction
        if (name.equalsIgnoreCase(documentVersionMethod[4])) {
            try {
                DocumentVersionFactory fc = FactoryInstantiator.getInstance().getDocumentVersionFactory();
                DocumentVersion version = fc.getDocumentVersion((Long) arguments[1]);
                HFactory t = (HFactory) fc;
                t.getSession().evict(version);
                Document document = version.getDocument();
                ctx.setEntity(document);
                EventContext.addParameter("version", version);
            } catch (Exception e) {
                log.error("Error while build version context", e);
            }
        }
        return ctx;
    }

    private static EventContext workspaceContextBuilder(Method mi, Object[] arguments,  EventContext ctx)
            throws DataSourceException, ConfigException
    {
        String name = mi.getName();
        if (name.equalsIgnoreCase(workspaceMethod[0])) {
            Workspace wks = new Workspace();
            wks.setUid(-1);
            wks.setName(arguments[1] != null ? arguments[1].toString() : "");
            try {
                FactoryInstantiator.getInstance().getDmEntityFactory().generatePath(wks);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ctx.setEntity(wks);
        }
        if (name.equalsIgnoreCase(workspaceMethod[1])) {
            /*
            * Load the current (before Update )
            */
            try {
                WorkspaceFactory wc = FactoryInstantiator.getInstance().getWorkspaceFactory();
                Workspace wsk = wc.getWorkspace((Long) arguments[1]);
                HFactory t = (HFactory) wc;
                t.getSession().evict(wsk);
                ctx.setEntity(wsk);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (name.equalsIgnoreCase(workspaceMethod[2])) {
            /*
            * Load the current (before  delete)
            */
            WorkspaceFactory wc = FactoryInstantiator.getInstance().getWorkspaceFactory();
            Workspace wsk = wc.getWorkspace((Long) arguments[1]);
            HFactory t = (HFactory) wc;
            t.getSession().evict(wsk);
            ctx.setEntity(wsk);
        }
        return ctx;
    }

    private static EventContext fileTransferContextBuilder(Method mi, Object[] arguments, EventContext ctx)
            throws DataSourceException, ConfigException
    {
        String name = mi.getName();
        //file upload
        if (name.equalsIgnoreCase(fileTransferMethod[0])) {
            try {
                DataTransferFactory dtFactory =
                        org.kimios.kernel.filetransfer.FactoryInstantiator.getInstance().getDataTransferFactory();
                DataTransfer dt =
                        org.kimios.kernel.filetransfer.FactoryInstantiator.getInstance().getDataTransferFactory()
                                .getDataTransfer((Long) arguments[1]);
                HFactory t = (HFactory) dtFactory;
                t.getSession().evict(dt);
                DocumentVersionFactory fc = FactoryInstantiator.getInstance().getDocumentVersionFactory();
                DocumentVersion version = fc.getDocumentVersion(dt.getDocumentVersionUid());
                t.getSession().evict(version);
                Document document = version.getDocument();
                ctx.setEntity(document);
                EventContext.addParameter("version", version);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ctx;
    }

    private static EventContext extensionContextBuilder(Method mi, Object[] arguments, EventContext ctx)
            throws DataSourceException, ConfigException
    {
        String name = mi.getName();
        //set attribute on entity
        if (name.equalsIgnoreCase(extensionMethods[0])) {
            try {
                DMEntity entity = FactoryInstantiator.getInstance().getDmEntityFactory()
                        .getEntity((Long)arguments[1]);
                ctx.setEntity(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (name.equalsIgnoreCase(extensionMethods[1])) {
            try {
                DMEntity entity = FactoryInstantiator.getInstance().getDmEntityFactory()
                        .getEntity((Long)arguments[1]);
                ctx.setEntity(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (name.equalsIgnoreCase(extensionMethods[2])) {
            try {
                DMEntity entity = FactoryInstantiator.getInstance().getDmEntityFactory()
                        .getEntity((Long)arguments[1]);
                ctx.setEntity(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ctx;
    }
}

