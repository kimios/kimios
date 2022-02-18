package org.kimios.camel.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.kimios.kernel.controller.IFolderController;
import org.kimios.kernel.dms.model.Folder;
import org.kimios.kernel.ws.pojo.DataMessage;
import org.kimios.kernel.ws.pojo.task.TaskGetFoldersAndSendData;

import java.util.List;
import java.util.stream.Collectors;

public class GetFoldersAndSendDataRouteBuilder extends RouteBuilder {

    private IFolderController folderController;

    public IFolderController getFolderController() {
        return folderController;
    }

    public void setFolderController(IFolderController folderController) {
        this.folderController = folderController;
    }

    @Override
    public void configure() throws Exception {
        from("direct:getFoldersAndSendData")
                .process(new GetFoldersProcessor(this.folderController))
                .to("direct:sendData");
    }

    private class GetFoldersProcessor implements Processor {
        private IFolderController folderController;

        public GetFoldersProcessor(IFolderController folderController) {
            super();
            this.folderController = folderController;
        }

        @Override
        public void process(Exchange exchange) throws Exception {
            TaskGetFoldersAndSendData taskGetFoldersAndSendData =
                    exchange.getIn().getBody(TaskGetFoldersAndSendData.class);

            List<Folder> folderList = this.folderController.getFolders(
                    taskGetFoldersAndSendData.getSession(), taskGetFoldersAndSendData.getParent().getUid());

            DataMessage dataMessage = new DataMessage(
                    taskGetFoldersAndSendData.getSession().getWebSocketToken(),
                    taskGetFoldersAndSendData.getSession().getUid(),
                    folderList
                            .stream()
                            .map(folder -> folder.toPojo())
                            .map(folder -> {
                                folder.setBookmarked(
                                        taskGetFoldersAndSendData.getBookmarkedUidList().contains(folder.getUid()));
                                return folder;
                            })
                            .collect(Collectors.toList()),
                    taskGetFoldersAndSendData.getParent()
            );
            exchange.getOut().setBody(dataMessage);
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        }
    }
}
