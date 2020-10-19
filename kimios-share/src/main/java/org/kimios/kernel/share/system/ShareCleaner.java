package org.kimios.kernel.share.system;

import org.apache.commons.lang.StringUtils;
import org.kimios.api.controller.IManageableServiceController;
import org.kimios.kernel.controller.ISecurityController;
import org.kimios.kernel.deployment.DataInitializerCtrl;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.share.controller.IShareController;
import org.kimios.kernel.share.jobs.ShareCleanerJob;
import org.kimios.utils.system.CustomScheduledThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ShareCleaner implements IManageableServiceController {

    private static Logger logger = LoggerFactory.getLogger(ShareCleaner.class);

    private ISecurityController securityController;
    private IShareController shareController;
    private CustomScheduledThreadPoolExecutor customScheduledThreadPoolExecutor;
    private boolean launchShareCleaner;

    public ShareCleaner() {
        this.initExecutor();
    }

    private void initExecutor() {
        this.customScheduledThreadPoolExecutor = new CustomScheduledThreadPoolExecutor(8);
        this.customScheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
    }

    public void startJob() {
        String message = "Share Cleaner ";
        if (!this.launchShareCleaner) {
            message += " has NOT been launched (according to the configuration)";
            logger.info(message);
            // end
            return;
        }

        message += " is going to be launched";
        logger.info(message);

        String defaultDomain =
                StringUtils.isEmpty(System.getenv(DataInitializerCtrl.KIMIOS_DEFAULT_DOMAIN)) ?
                        (StringUtils.isEmpty(System.getProperty("kimios.default.domain")) ? "kimios" : System.getProperty("kimios.default.domain")) :
                        System.getenv(DataInitializerCtrl.KIMIOS_DEFAULT_DOMAIN);

        String adminLogin =
                StringUtils.isEmpty(System.getenv(DataInitializerCtrl.KIMIOS_ADMIN_USERID)) ?
                        (StringUtils.isEmpty(System.getProperty("kimios.admin.userid")) ? "admin" : System.getProperty("kimios.admin.userid")) :
                        System.getenv(DataInitializerCtrl.KIMIOS_ADMIN_USERID);

        Session session = this.securityController.startSession(adminLogin, defaultDomain);
        ShareCleanerJob job = new ShareCleanerJob(shareController, session);
        this.customScheduledThreadPoolExecutor.scheduleAtFixedRate(job, 0, 1, TimeUnit.MINUTES);

        logger.info("Share Cleaner started");
    }

    public void stopJob() {
        logger.info("Kimios Share Cleaner stopping…");
        try {
            if(this.customScheduledThreadPoolExecutor != null){
                this.customScheduledThreadPoolExecutor.shutdownNow();
                this.customScheduledThreadPoolExecutor.awaitTermination(5, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.info("Kimios Share Cleaner stopped");
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

    public boolean isLaunchShareCleaner() {
        return launchShareCleaner;
    }

    public void setLaunchShareCleaner(boolean launchShareCleaner) {
        this.launchShareCleaner = launchShareCleaner;
    }

    @Override
    public void pauseThreadPoolExecutor() throws InterruptedException {
        this.shutdownAndAwaitTermination(this.customScheduledThreadPoolExecutor);
    }

    @Override
    public void resumeThreadPoolExecutor() {
        String defaultDomain =
                StringUtils.isEmpty(System.getenv(DataInitializerCtrl.KIMIOS_DEFAULT_DOMAIN)) ?
                        (StringUtils.isEmpty(System.getProperty("kimios.default.domain")) ? "kimios" : System.getProperty("kimios.default.domain")) :
                        System.getenv(DataInitializerCtrl.KIMIOS_DEFAULT_DOMAIN);

        String adminLogin =
                StringUtils.isEmpty(System.getenv(DataInitializerCtrl.KIMIOS_ADMIN_USERID)) ?
                        (StringUtils.isEmpty(System.getProperty("kimios.admin.userid")) ? "admin" : System.getProperty("kimios.admin.userid")) :
                        System.getenv(DataInitializerCtrl.KIMIOS_ADMIN_USERID);
        Session session = this.securityController.startSession(adminLogin, defaultDomain);
        ShareCleanerJob job = new ShareCleanerJob(shareController, session);
        this.initExecutor();
        this.customScheduledThreadPoolExecutor.scheduleAtFixedRate(job, 0, 1, TimeUnit.MINUTES);
        System.out.println("resumeThreadPoolExecutor() : " +
                this.customScheduledThreadPoolExecutor.getTaskCount());
    }

    @Override
    public String statusThreadPoolExecutor() {
        return this.customScheduledThreadPoolExecutor.isTerminated() ?
                "inactive" :
                this.customScheduledThreadPoolExecutor.getTaskCount() > 0 ?
                "active" :
                this.customScheduledThreadPoolExecutor.isTerminating() ?
                        "terminating" :
                        "inactive";
    }

    @Override
    public String serviceName() {
        return this.getClass().getCanonicalName();
    }

    void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}
