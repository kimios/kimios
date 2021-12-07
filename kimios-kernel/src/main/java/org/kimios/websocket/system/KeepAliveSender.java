package org.kimios.websocket.system;

import org.kimios.kernel.controller.ISecurityController;
import org.kimios.utils.controller.threads.management.InSessionManageableServiceController;
import org.kimios.websocket.client.controller.IWebSocketManager;
import org.kimios.websocket.system.job.KeepAliveSenderJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class KeepAliveSender extends InSessionManageableServiceController {

    private static Logger logger = LoggerFactory.getLogger(KeepAliveSender.class);

    private IWebSocketManager webSocketManager;
    private ISecurityController securityController;

    public KeepAliveSender() {
        super("Keep Alive Sender", 0, 2, TimeUnit.MINUTES);
    }

    public IWebSocketManager getWebSocketManager() {
        return webSocketManager;
    }

    public void setWebSocketManager(IWebSocketManager webSocketManager) {
        this.webSocketManager = webSocketManager;
    }

    public ISecurityController getSecurityController() {
        return securityController;
    }

    public void setSecurityController(ISecurityController securityController) {
        this.securityController = securityController;
    }

    public void startJob() {
        KeepAliveSenderJob job = new KeepAliveSenderJob(webSocketManager, securityController);
        this.scheduleJobAtFixedRate(job);
        logger.debug("Starting sending notifications");
    }

    public void stopJob() {
        logger.info("Notification Sender stopping…");
        try {
            if(this.customScheduledThreadPoolExecutor != null){
                this.customScheduledThreadPoolExecutor.shutdownNow();
                this.customScheduledThreadPoolExecutor.awaitTermination(5, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.info("Notification Sender stopped");
    }

    @Override
    public void resumeThreadPoolExecutor() {
        super.resumeThreadPoolExecutor();
        KeepAliveSenderJob job = new KeepAliveSenderJob(webSocketManager, securityController);
        this.scheduleJobAtFixedRate(job);
    }
}
