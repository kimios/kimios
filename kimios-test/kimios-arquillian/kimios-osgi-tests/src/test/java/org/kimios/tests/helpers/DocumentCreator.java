/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
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

package org.kimios.tests.helpers;

import org.apache.commons.io.IOUtils;
import org.kimios.kernel.controller.IDocumentController;
import org.kimios.kernel.dms.model.Folder;
import org.kimios.kernel.security.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Created by farf on 25/05/16.
 */
public class DocumentCreator {


    private static Logger logger = LoggerFactory.getLogger(DocumentCreator.class);

    private IDocumentController documentController;

    public DocumentCreator(IDocumentController documentController){
        this.documentController = documentController;
    }

    public long createDocument(Session session, Folder folder, ClassLoader cl, String pathStream, String docName, String docExtensio){
        long docUid = -1;
        try {
            InputStream docStream = cl.getResourceAsStream(pathStream);
            logger.info("test document will be created in {} - content {}", folder, docStream);
            docUid = this.documentController.createDocumentWithProperties(
                    session,
                    docName,
                    docExtensio,
                    "",
                    folder.getUid(),
                    false,
                    "<security-rules dmEntityId=\"-1\" dmEntityTye=\"3\"></security-rules>",
                    false,
                    -1,
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?><document-meta></document-meta>",
                    docStream,
                    "",
                    ""
            );
            return docUid;
        } catch (Exception e) {
            logger.error("error while document creation", e);
        }
        logger.info("Created document Id {}", docUid);
        return docUid;
    }

}
