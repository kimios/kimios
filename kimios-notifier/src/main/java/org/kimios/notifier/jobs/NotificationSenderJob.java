package org.kimios.notifier.jobs;

import org.kimios.kernel.jobs.JobImpl;
import org.kimios.kernel.security.model.Session;
import org.kimios.notifier.controller.INotifierController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class NotificationSenderJob extends JobImpl<Integer> implements Runnable {

    private static Logger log = LoggerFactory.getLogger(NotificationSenderJob.class);

    private INotifierController notifierController;

    public NotificationSenderJob(INotifierController notifierController, Session session) {
        //generate task id
        super( UUID.randomUUID().toString() );

        this.notifierController = notifierController;
        this.setSession(session);
    }

    @Override
    public Integer execute() throws Exception {
        log.debug("Starting sending notifications");
        try {
            this.notifierController.sendNotifications(this.getUserSession());
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
