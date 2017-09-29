package org.kimios.notifier.system;

import org.kimios.kernel.security.SessionManager;
import org.kimios.kernel.security.model.Session;
import org.kimios.notifier.controller.NotifierController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationCreator implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(NotificationCreator.class);

    private static Thread thrc;
    private volatile boolean active = true;
    private SessionManager sessionManager;
    private NotifierController notifierController;

    @Override
    public void run() {
        Session session = this.sessionManager.startSession("admin", "kimios");
        while (active) {
            try {
                Thread.sleep(5000);
                if (active) {
                    logger.info("createNotifications now");
                    this.notifierController.createNotifications(session);
                }
            } catch (Exception e) {
                System.out.println("Thread interrupted " + e.getMessage());
            }
        }
    }

    public void stop() {
        active = false;
    }

    public void startJob()
    {
        logger.info("Kimios Notification Creator - Starting job.");
        synchronized (this) {
            thrc = new Thread(this, "Kimios Notification Creator");
            thrc.start();
        }
        logger.info("Kimios Notification Creator - Started job.");
    }

    public void stopJob()
    {
        logger.info("Kimios Notification Creator - Closing ...");
        try {
            this.stop();
            thrc.join();
        } catch (Exception e) {

        }
        logger.info("Kimios Notification Creator - Closed");
    }
}
