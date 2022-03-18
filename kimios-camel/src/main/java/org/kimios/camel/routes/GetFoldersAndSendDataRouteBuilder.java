package org.kimios.camel.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.kimios.kernel.controller.IDocumentController;
import org.kimios.kernel.controller.IFolderController;
import org.kimios.kernel.dms.model.Folder;
import org.kimios.kernel.ws.pojo.DMEntity;
import org.kimios.kernel.ws.pojo.DataMessage;
import org.kimios.kernel.ws.pojo.Document;
import org.kimios.kernel.ws.pojo.task.TaskGetFoldersAndSendData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GetFoldersAndSendDataRouteBuilder extends RouteBuilder {

    private IFolderController folderController;
    private IDocumentController documentController;

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

    @Override
    public void configure() throws Exception {
        from("direct:getFoldersAndSendData")
                .process(new GetFoldersProcessor(this.folderController, this.documentController))
                .to("direct:sendData");
    }

    private class GetFoldersProcessor implements Processor {
        private IFolderController folderController;
        private IDocumentController documentController;

        public GetFoldersProcessor(IFolderController folderController, IDocumentController documentController) {
            super();
            this.folderController = folderController;
            this.documentController = documentController;
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

            List<DMEntity> entityList = new ArrayList<>();
            entityList.addAll(
                    folderList
                            .stream()
                            .map(folder -> folder.toPojo())
                            .map(folder -> {
                                folder.setBookmarked(
                                        taskGetFoldersAndSendData.getBookmarkedUidList().contains(folder.getUid()));
                                return folder;
                            })
                            .collect(Collectors.toList())
            );
            entityList.addAll(documentList);

            DataMessage dataMessage = new DataMessage(
                    taskGetFoldersAndSendData.getSession().getWebSocketToken(),
                    taskGetFoldersAndSendData.getSession().getUid(),
                    entityList,
                    taskGetFoldersAndSendData.getParent()
            );
            exchange.getOut().setBody(dataMessage);
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        }
    }
}
