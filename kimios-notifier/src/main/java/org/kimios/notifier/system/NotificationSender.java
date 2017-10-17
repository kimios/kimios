package org.kimios.notifier.system;

import org.kimios.kernel.index.controller.CustomThreadPoolExecutor;
import org.kimios.kernel.security.ISessionManager;
import org.kimios.kernel.security.model.Session;
import org.kimios.notifier.controller.INotifierController;
import org.kimios.notifier.jobs.NotificationCreatorJob;
import org.kimios.notifier.jobs.NotificationSenderJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class NotificationSender implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(NotificationSender.class);

    private volatile boolean active = true;
    private static Thread thrc;
    private ISessionManager sessionManager;
    private INotifierController notifierController;
    private CustomThreadPoolExecutor customThreadPoolExecutor;

    public void startJob() {
        this.customThreadPoolExecutor = new CustomThreadPoolExecutor(8, 8,
                10000L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());

        synchronized (this) {
            thrc = new Thread(this, "Kimios Notification Sender");
            thrc.start();
        }
    }

    public void stopJob() {
        try {
            this.stop();
            thrc.join();
        } catch (Exception e) {
        }
        logger.info("Notification Sender stopped");
    }

    public void stop() {
        this.active = false;
    }

    public ISessionManager getSessionManager() {
        return sessionManager;
    }

    public void setSessionManager(ISessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public INotifierController getNotifierController() {
        return notifierController;
    }

    public void setNotifierController(INotifierController notifierController) {
        this.notifierController = notifierController;
    }

    @Override
    public void run() {
        Session session = this.sessionManager.startSession("admin", "kimios");
        while (active) {
            try {
                if (active) {
                    logger.info("sendNotifications now");
                    NotificationSenderJob job = new NotificationSenderJob(notifierController, session);
                    customThreadPoolExecutor.submit(job);
                    logger.info("just sent notification(s)");
                }
                Thread.sleep(5000);
            } catch (Exception e) {
                this.stop();
                logger.info("Thread interrupted " + e.getMessage());
            }
        }
    }
}
