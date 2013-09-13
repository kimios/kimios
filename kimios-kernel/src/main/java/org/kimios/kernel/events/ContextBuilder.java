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
package org.kimios.kernel.events;

import org.aopalliance.intercept.MethodInvocation;
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
            "checkoutDocument", "checkinDocument", "addRelatedDocument", "removeRelatedDocument" };

    private static String[] folderMethod = { "createFolder", "updateFolder", "deleteFolder" };

    private static String[] workspaceMethod = { "createWorkspace", "updateWorkspace", "deleteWorkspace" };

    private static String[] documentVersionMethod = { "createDocumentVersion", "createDocumentVersionFromLatest",
            "updateDocumentVersion", "deleteDocumentVersion", "startDownloadTransaction" };

    private static String[] fileTransferMethod = { "endUploadTransaction" };

    private static String[] extensionMethods = { "setAttribute" };

    public static EventContext buildContext(DmsEventName n, MethodInvocation mi)
    {
        try {
            EventContext ctx = EventContext.get();
            ctx.setEvent(n);
            ctx.setContextParameters(mi.getArguments());
            ctx.setSession((Session) mi.getArguments()[0]);

            if (n.name().startsWith(documentVersion)) {
                documentVersionContextBuilder(mi, ctx);
            }
            if (n.name().startsWith(document)) {
                documentContextBuilder(mi, ctx);
            }
            if (n.name().startsWith(folder)) {
                folderContextBuilder(mi, ctx);
            }
            if (n.name().startsWith(workspace)) {
                workspaceContextBuilder(mi, ctx);
            }
            if (n.name().startsWith(fileTransfer)) {
                fileTransferContextBuilder(mi, ctx);
            }
            if(n.name().startsWith(extension)){
                extensionContextBuilder(mi, ctx);
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

    private static EventContext folderContextBuilder(MethodInvocation mi, EventContext ctx)
            throws DataSourceException, ConfigException
    {
        String name = mi.getMethod().getName();
        if (name.equalsIgnoreCase(folderMethod[0])) {
            /*
            * building dm entity
            */
            Folder fold = new Folder();
            fold.setUid(-1);
            fold.setName(mi.getArguments()[1] != null ? mi.getArguments()[1].toString() : "");
            fold.setParentUid((Long) mi.getArguments()[2]);
            FactoryInstantiator.getInstance().getDmEntityFactory().generatePath(fold);
            ctx.setEntity(fold);
        }
        if (name.equalsIgnoreCase(folderMethod[1])) {
            /*
            * Load the current (before delete)
            */
            FolderFactory fc = FactoryInstantiator.getInstance().getFolderFactory();
            Folder fold = fc.getFolder((Long) mi.getArguments()[1]);
            HFactory t = (HFactory) fc;
            t.getSession().evict(fold);
            ctx.setEntity(fold);
        }
        if (name.equalsIgnoreCase(folderMethod[2])) {
            /*
            * Load the current (before Update)
            */
            FolderFactory fc = FactoryInstantiator.getInstance().getFolderFactory();
            Folder fold = fc.getFolder((Long) mi.getArguments()[1]);
            HFactory t = (HFactory) fc;
            t.getSession().evict(fold);
            ctx.setEntity(fold);
        }

        return ctx;
    }

    private static EventContext documentContextBuilder(MethodInvocation mi, EventContext ctx)
            throws DataSourceException, ConfigException
    {
        String name = mi.getMethod().getName();

        // createDocument
        if (name.equalsIgnoreCase(documentMethod[0])) {
            if (mi.getArguments().length == 3) {
                /*
                   Create document from full path
                */
                Document doc = new Document();
                doc.setUid(-1);
                String docPath = ((String) mi.getArguments()[1]);
                String fldPath = docPath.substring(0, docPath.lastIndexOf("/"));
                Folder f = (Folder) FactoryInstantiator.getInstance().getDmEntityFactory().getEntity(fldPath);
                doc.setFolderUid(f.getUid());
                doc.setPath((String) mi.getArguments()[1]);
                ctx.setEntity(doc);
            } else {
                Document doc = new Document();
                doc.setUid(-1);
                doc.setName(mi.getArguments()[1] != null ? mi.getArguments()[1].toString() : "");
                doc.setMimeType(mi.getArguments()[3] != null ? mi.getArguments()[3].toString() : "");
                doc.setFolderUid((Long) mi.getArguments()[4]);
                doc.setType(DMEntityType.DOCUMENT);
                FactoryInstantiator.getInstance().getDmEntityFactory().generatePath(doc);
                ctx.setEntity(doc);
            }
        }

        // updateDocument
        if (name.equalsIgnoreCase(documentMethod[1])) {
            DocumentFactory fc = FactoryInstantiator.getInstance().getDocumentFactory();
            Document document = fc.getDocument((Long) mi.getArguments()[1]);
            HFactory t = (HFactory) fc;
            t.getSession().evict(document);
            ctx.setEntity(document);
        }

        // deleteDocument
        if (name.equalsIgnoreCase(documentMethod[2])) {
            DocumentFactory fc = FactoryInstantiator.getInstance().getDocumentFactory();
            Document document = fc.getDocument((Long) mi.getArguments()[1]);
            HFactory t = (HFactory) fc;
            t.getSession().evict(document);
            ctx.setEntity(document);
        }

        // checkoutDocument
        if (name.equalsIgnoreCase(documentMethod[3])) {
            DocumentFactory fc = FactoryInstantiator.getInstance().getDocumentFactory();
            Document document = fc.getDocument((Long) mi.getArguments()[1]);
            HFactory t = (HFactory) fc;
            t.getSession().evict(document);
            ctx.setEntity(document);
        }

        // checkinDocument
        if (name.equalsIgnoreCase(documentMethod[4])) {
            DocumentFactory fc = FactoryInstantiator.getInstance().getDocumentFactory();
            Document document = fc.getDocument((Long) mi.getArguments()[1]);
            HFactory t = (HFactory) fc;
            t.getSession().evict(document);
            ctx.setEntity(document);
        }

        // addRelatedDocument
        if (name.equalsIgnoreCase(documentMethod[5])) {
            DocumentFactory fc = FactoryInstantiator.getInstance().getDocumentFactory();
            Document document = fc.getDocument((Long) mi.getArguments()[1]);
            HFactory t = (HFactory) fc;
            t.getSession().evict(document);
            ctx.setEntity(document);
            EventContext.addParameter("relatedDocument", fc.getDocument((Long) mi.getArguments()[2]));
        }

        // removeRelatedDocument
        if (name.equalsIgnoreCase(documentMethod[6])) {
            DocumentFactory fc = FactoryInstantiator.getInstance().getDocumentFactory();
            Document document = fc.getDocument((Long) mi.getArguments()[1]);
            HFactory t = (HFactory) fc;
            t.getSession().evict(document);
            ctx.setEntity(document);
            EventContext.addParameter("relatedDocument", fc.getDocument((Long) mi.getArguments()[2]));
        }

        return ctx;
    }

    private static EventContext documentVersionContextBuilder(MethodInvocation mi, EventContext ctx)
            throws DataSourceException, ConfigException
    {
        String name = mi.getMethod().getName();

        // createDocumentVersion
        if (name.equalsIgnoreCase(documentVersionMethod[0])) {
            DocumentFactory fc = FactoryInstantiator.getInstance().getDocumentFactory();
            Document doc = fc.getDocument((Long) mi.getArguments()[1]);
            ctx.setEntity(doc);
            DocumentVersion dv =
                    FactoryInstantiator.getInstance().getDocumentVersionFactory().getLastDocumentVersion(doc);
            EventContext.addParameter("previousVersion", dv);
        }

        // createDocumentVersionFromLatest
        if (name.equalsIgnoreCase(documentVersionMethod[1])) {
            DocumentFactory fc = FactoryInstantiator.getInstance().getDocumentFactory();
            Document doc = fc.getDocument((Long) mi.getArguments()[1]);
            ctx.setEntity(doc);
            DocumentVersion dv =
                    FactoryInstantiator.getInstance().getDocumentVersionFactory().getLastDocumentVersion(doc);
            EventContext.addParameter("previousVersion", dv);
        }

        // updateDocumentVersion
        if (name.equalsIgnoreCase(documentVersionMethod[2])) {
            DocumentVersionFactory fc = FactoryInstantiator.getInstance().getDocumentVersionFactory();
            Document tDoc =
                    FactoryInstantiator.getInstance().getDocumentFactory().getDocument((Long) mi.getArguments()[1]);
            DocumentVersion version = fc.getLastDocumentVersion(tDoc);
            HFactory t = (HFactory) fc;
            t.getSession().evict(version);
            Document document = version.getDocument();
            ctx.setEntity(document);
            EventContext.addParameter("version", version);

            //check if document type set
            if (((Long) mi.getArguments()[2]) > 0) {
                DocumentTypeFactory dtF = FactoryInstantiator.getInstance().getDocumentTypeFactory();
                DocumentType dt = dtF.getDocumentType((Long) mi.getArguments()[2]);
                EventContext.addParameter("documentTypeSet", dt);
            }
        }

        // deleteDocumentVersion
        if (name.equalsIgnoreCase(documentVersionMethod[3])) {
            try {
                DocumentVersionFactory fc = FactoryInstantiator.getInstance().getDocumentVersionFactory();
                DocumentVersion version = fc.getDocumentVersion((Long) mi.getArguments()[1]);
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
                DocumentVersion version = fc.getDocumentVersion((Long) mi.getArguments()[1]);
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

    private static EventContext workspaceContextBuilder(MethodInvocation mi, EventContext ctx)
            throws DataSourceException, ConfigException
    {
        String name = mi.getMethod().getName();
        if (name.equalsIgnoreCase(workspaceMethod[0])) {
            Workspace wks = new Workspace();
            wks.setUid(-1);
            wks.setName(mi.getArguments()[1] != null ? mi.getArguments()[1].toString() : "");
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
                Workspace wsk = wc.getWorkspace((Long) mi.getArguments()[1]);
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
            Workspace wsk = wc.getWorkspace((Long) mi.getArguments()[1]);
            HFactory t = (HFactory) wc;
            t.getSession().evict(wsk);
            ctx.setEntity(wsk);
        }
        return ctx;
    }

    private static EventContext fileTransferContextBuilder(MethodInvocation mi, EventContext ctx)
            throws DataSourceException, ConfigException
    {
        String name = mi.getMethod().getName();
        //file upload
        if (name.equalsIgnoreCase(fileTransferMethod[0])) {
            try {
                DataTransferFactory dtFactory =
                        org.kimios.kernel.filetransfer.FactoryInstantiator.getInstance().getDataTransferFactory();
                DataTransfer dt =
                        org.kimios.kernel.filetransfer.FactoryInstantiator.getInstance().getDataTransferFactory()
                                .getDataTransfer((Long) mi.getArguments()[1]);
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

    private static EventContext extensionContextBuilder(MethodInvocation mi, EventContext ctx)
            throws DataSourceException, ConfigException
    {
        String name = mi.getMethod().getName();
        //set attribute on entity
        if (name.equalsIgnoreCase(extensionMethods[0])) {
            try {
                DMEntity entity = FactoryInstantiator.getInstance().getDmEntityFactory()
                        .getEntity((Long)mi.getArguments()[1]);
                ctx.setEntity(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ctx;
    }
}

