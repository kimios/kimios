package org.kimios.websocket.system.job;

import org.kimios.kernel.controller.ISecurityController;
import org.kimios.kernel.ws.pojo.UpdateNoticeMessage;
import org.kimios.kernel.ws.pojo.UpdateNoticeType;
import org.kimios.websocket.client.controller.IWebSocketManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeepAliveSenderJob implements Runnable {

    private static Logger log = LoggerFactory.getLogger(KeepAliveSenderJob.class);

    IWebSocketManager webSocketManager;
    ISecurityController securityController;

    public KeepAliveSenderJob(IWebSocketManager webSocketManager, ISecurityController securityController) {
        this.webSocketManager = webSocketManager;
        this.securityController = securityController;
    }

    public Integer execute() throws Exception {
        log.debug("Starting sending notifications");
        try {
            UpdateNoticeMessage updateNoticeMessage = new UpdateNoticeMessage(
                    UpdateNoticeType.KEEP_ALIVE_PING,
                    securityController.getSystemWebSocketToken(),
                    null
            );
            this.webSocketManager.sendUpdateNotice(updateNoticeMessage);
        } catch (Exception e) {
            log.error("error while sending notification ", e);
        }
        return 1;
    }

    @Override
    public void run() {
        try {
            this.execute();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
