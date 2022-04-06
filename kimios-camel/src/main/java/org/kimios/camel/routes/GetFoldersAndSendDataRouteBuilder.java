package org.kimios.camel.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.kimios.kernel.controller.IDocumentController;
import org.kimios.kernel.controller.IFolderController;
import org.kimios.kernel.controller.ISecurityController;
import org.kimios.kernel.dms.model.Folder;
import org.kimios.kernel.ws.pojo.DMEntityWrapper;
import org.kimios.kernel.ws.pojo.DataMessage;
import org.kimios.kernel.ws.pojo.Document;
import org.kimios.kernel.ws.pojo.task.TaskGetFoldersAndSendData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GetFoldersAndSendDataRouteBuilder extends RouteBuilder {

    private IFolderController folderController;
    private IDocumentController documentController;
    private ISecurityController securityController;

    public IFolderController getFolderController() {
        return folderController;
    }

    public void setFolderController(IFolderController folderController) {
        this.folderController = folderController;
    }

    public IDocumentController getDocumentController() {
        return documentController;
    }

    public void setDocumentController(IDocumentController documentController) {
        this.documentController = documentController;
    }

    public ISecurityController getSecurityController() {
        return securityController;
    }

    public void setSecurityController(ISecurityController securityController) {
        this.securityController = securityController;
    }

    @Override
    public void configure() throws Exception {
        from("direct:getFoldersAndSendData")
                .process(new GetFoldersProcessor(this.folderController, this.documentController, this.securityController))
                .to("direct:sendData");
    }

    private class GetFoldersProcessor implements Processor {
        private IFolderController folderController;
        private IDocumentController documentController;
        private ISecurityController securityController;

        public GetFoldersProcessor(
                IFolderController folderController,
                IDocumentController documentController,
                ISecurityController securityController
        ) {
            super();
            this.folderController = folderController;
            this.documentController = documentController;
            this.securityController = securityController;
        }

        @Override
        public void process(Exchange exchange) throws Exception {
            TaskGetFoldersAndSendData taskGetFoldersAndSendData =
                    exchange.getIn().getBody(TaskGetFoldersAndSendData.class);

            List<Folder> folderList = this.folderController.getFolders(
                    taskGetFoldersAndSendData.getSession(), taskGetFoldersAndSendData.getParent().getUid());
            // if entity is folder
            List<Document> documentList = new ArrayList<>();
            if (taskGetFoldersAndSendData.getParent().getType() == 2) {
                // get also documents
                documentList = this.documentController.getDocumentsPojos(
                        taskGetFoldersAndSendData.getSession(), taskGetFoldersAndSendData.getParent().getUid());
            }

            List<DMEntityWrapper> entityList = new ArrayList<>();
            entityList.addAll(
                    folderList
                            .stream()
                            .map(folder -> folder.toPojo())
                            .map(folder -> {
                                folder.setBookmarked(
                                        taskGetFoldersAndSendData.getBookmarkedUidList().contains(folder.getUid()));
                                return folder;
                            })
                            .map(folder -> new DMEntityWrapper(folder))
                            .collect(Collectors.toList())
            );
            entityList.addAll(
                    documentList.stream().map(document -> new DMEntityWrapper(document))
                    .collect(Collectors.toList())
            );

            // add permissions canRead, canWrite, hasFullAccess 
            entityList.stream().map(dmEntityWrapper -> {
                long dmEntityUid = dmEntityWrapper.getDmEntity().getUid();
                dmEntityWrapper.setCanRead(
                        this.securityController.canRead(taskGetFoldersAndSendData.getSession(), dmEntityUid));
                dmEntityWrapper.setCanWrite(
                        this.securityController.canWrite(taskGetFoldersAndSendData.getSession(), dmEntityUid));
                dmEntityWrapper.setHasFullAccess(
                        this.securityController.hasFullAccess(taskGetFoldersAndSendData.getSession(), dmEntityUid));

                return dmEntityWrapper;
            });

            DataMessage dataMessage = new DataMessage(
                    taskGetFoldersAndSendData.getSession().getWebSocketToken(),
                    taskGetFoldersAndSendData.getSession().getUid(),
                    entityList,
                    new DMEntityWrapper(
                            taskGetFoldersAndSendData.getParent(),
                            this.securityController.canRead(
                                    taskGetFoldersAndSendData.getSession(),
                                    taskGetFoldersAndSendData.getParent().getUid()
                            ),
                            this.securityController.canWrite(
                                    taskGetFoldersAndSendData.getSession(),
                                    taskGetFoldersAndSendData.getParent().getUid()
                            ),
                            this.securityController.hasFullAccess(
                                    taskGetFoldersAndSendData.getSession(),
                                    taskGetFoldersAndSendData.getParent().getUid()
                            )
                    )
            );
            exchange.getOut().setBody(dataMessage);
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        }
    }
}
