package org.kimios.osgi.utils.notifier;

import org.kimios.kernel.jobs.Job;
import org.kimios.kernel.jobs.JobImpl;
import org.kimios.kernel.notifier.jobs.NotifierThreadManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class NotifierThread implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(NotifierThread.class);

    private volatile boolean running = true;

    public void terminate() {
        running = false;
        NotifierThreadManager.getInstance().shutDown();
    }

    @Override
    public void run() {
        String taskId = UUID.randomUUID().toString();
        Job<Boolean> rn = new JobImpl<Boolean>(taskId) {
            @Override
            public Boolean execute() {
                logger.info("kimios Notifier: executing job");

                return Boolean.TRUE;
            }
        };
        while (running) {
            try {
                logger.debug("Sleeping...");
                Thread.sleep(5000);
                logger.debug("Launching new task");
                if (running) {
                    NotifierThreadManager.getInstance().startJob(rn);
                }
            } catch (InterruptedException e) {
                logger.error("Exception", e);
                running = false;
            }
        }
    }
}
