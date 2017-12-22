package org.kimios.kernel.share.system;

import org.kimios.kernel.controller.ISecurityController;
import org.kimios.kernel.index.controller.CustomThreadPoolExecutor;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.share.controller.IShareController;
import org.kimios.kernel.share.jobs.ShareCleanerJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ShareCleaner implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(ShareCleaner.class);

    private volatile boolean active = true;
    private static Thread thrc;
    private ISecurityController securityController;
    private IShareController shareController;
    private CustomThreadPoolExecutor customThreadPoolExecutor;

    public void startJob() {
        this.customThreadPoolExecutor = new CustomThreadPoolExecutor(8, 8,
                10000L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());

        synchronized (this) {
            thrc = new Thread(this, "Kimios Share Cleaner");
            thrc.start();
        }
    }

    public void stopJob() {
        try {
            this.stop();
            thrc.join();
        } catch (Exception e) {
        }
        logger.info("Kimios Share Cleaner stopped");
    }

    public void stop() {
        this.active = false;
    }

    public IShareController getShareController() {
        return shareController;
    }

    public void setShareController(IShareController shareController) {
        this.shareController = shareController;
    }

    public ISecurityController getSecurityController() {
        return securityController;
    }

    public void setSecurityController(ISecurityController securityController) {
        this.securityController = securityController;
    }

    @Override
    public void run() {
        Session session = this.securityController.startSession("admin", "kimios");
        while (active) {
            try {
                if (active) {
                    logger.info("clean shares now");
                    ShareCleanerJob job = new ShareCleanerJob(shareController, session);
                    Integer i = customThreadPoolExecutor.submit(job).get();
                    logger.info("cleaned " + i + " shares");
                }
                Thread.sleep(5000);
            } catch (Exception e) {
                this.stop();
                logger.info("Thread interrupted " + e.getMessage());
            }
        }
    }
}
