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

package org.kimios.osgi.karaf;

import org.apache.commons.io.IOUtils;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.kimios.kernel.controller.IFileTransferController;
import org.kimios.kernel.controller.IPathController;
import org.kimios.kernel.controller.IWorkspaceController;
import org.kimios.kernel.dms.model.DMEntity;
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.filetransfer.model.DataTransfer;
import org.kimios.kernel.security.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
@Command(
        scope = "kimios",
        name = "export-data",
        description = "Launch Documents Tree Export Process")
public class ExportFilesCommand extends KimiosCommand
{


    private static Logger logger = LoggerFactory.getLogger(ExportFilesCommand.class);

    @Argument(index = 0, name = "kimiosPath",
            description = "Workspace Path to export",
            required = true, multiValued = false)
    String path = null;

    @Argument(index = 1, name = "dirPath",
            description = "Directory to export to",
            required = true, multiValued = false)
    String localPath = null;

    @Override protected void doExecuteKimiosCommand() throws Exception
    {
        if (this.isConnected()) {
            File exp = new File(localPath);
            if(!exp.exists()){
                exp.mkdirs();
            }
            new Thread(new Exporter(path, this.getCurrentSession(), exp, workspaceController, fileTransferController, this.pathController))
                    .start();
        }
    }

    private static class Exporter implements Runnable {


        private IWorkspaceController workspaceController;
        private IFileTransferController fileTransferController;
        private IPathController pathController;

        private String path;
        private Session session;
        private File exp;

        public Exporter(String path, Session session, File exp, IWorkspaceController workspaceController, IFileTransferController fileTransferController, IPathController pathController) {
            this.path = path;
            this.session = session;
            this.workspaceController = workspaceController;
            this.fileTransferController = fileTransferController;
            this.pathController = pathController;
            this.exp = exp;
        }

        @Override
        public void run() {
            List<DMEntity> entities = null;
            entities = pathController.getDMEntitiesByPathAndType(path, 3);
            Map<Long, DMEntity> item = new HashMap<Long, DMEntity>();
            for(DMEntity e: entities){
                //download document

                if(!item.containsKey(e.getUid())){
                    try {
                        item.put(e.getUid(), e);
                        DataTransfer dt = fileTransferController.startDownloadTransaction(session, e.getUid(), false);
                        //create path
                        String fullPath = exp.getAbsolutePath() + "/"
                                + e.getPath().substring(0, e.getPath().lastIndexOf("/"));
                        File f = new File(fullPath);
                        f.mkdirs();
                        InputStream fileStream =
                                fileTransferController.getDocumentVersionStream(session, dt.getUid());
                        File docFile = new File(f, e.getPath().substring(e.getPath().lastIndexOf("/") + 1));
                        IOUtils.copyLarge(fileStream, new FileOutputStream(docFile));
                        logger.info("copied file {} to {}", e.getPath(), docFile.getAbsolutePath());
                        //check sise
                        //last version
                    }catch (Exception ex){
                        logger.error("error while copying doc {}", e, ex);
                    }
                } else {
                    logger.info("COULD HAVE PROCESSED {} twice", e);
                }

            }
        }
    }
}
