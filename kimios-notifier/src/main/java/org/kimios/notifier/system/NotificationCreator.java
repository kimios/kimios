package org.kimios.notifier.system;

import org.kimios.kernel.index.controller.impl.CustomThreadPoolExecutor;
import org.kimios.kernel.security.ISessionManager;
import org.kimios.kernel.security.model.Session;
import org.kimios.notifier.controller.INotifierController;
import org.kimios.notifier.jobs.NotificationCreatorJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class NotificationCreator {

    private static Logger logger = LoggerFactory.getLogger(NotificationCreator.class);

    private static NotificationCreatorThread thrc;
    private ISessionManager sessionManager;
    private INotifierController notifierController;

    public void startUp() {
        Session session = this.sessionManager.startSession("admin", "kimios");
        thrc = new NotificationCreatorThread(notifierController, session);
        thrc.run();
    }

    public static class NotificationCreatorThread extends Thread {

        private volatile boolean active = true;
        private final INotifierController notifierController;
        private final Session session;
        private CustomThreadPoolExecutor customThreadPoolExecutor;

        public NotificationCreatorThread(INotifierController notifierController, Session session) {
            this.notifierController = notifierController;
            this.session = session;
            this.customThreadPoolExecutor = new CustomThreadPoolExecutor(8, 8,
                    10000L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());
        }

        public void run() {
            while (active) {
                try {
                    Thread.sleep(5000);
                    if (active) {
                        logger.info("createNotifications now");
                        NotificationCreatorJob job = new NotificationCreatorJob(notifierController, session);
                        Integer i = customThreadPoolExecutor.submit(job).get();
                        logger.info("created " + i + " notification(s)");
                    }
                } catch (Exception e) {
                    System.out.println("Thread interrupted " + e.getMessage());
                }
            }
        }

        public void stopThread() {
            active = false;
        }
    }
}
