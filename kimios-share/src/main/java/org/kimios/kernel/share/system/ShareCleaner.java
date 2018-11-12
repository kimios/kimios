package org.kimios.kernel.share.system;

import org.apache.commons.lang.StringUtils;
import org.kimios.kernel.controller.ISecurityController;
import org.kimios.kernel.deployment.DataInitializerCtrl;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.share.controller.IShareController;
import org.kimios.kernel.share.jobs.ShareCleanerJob;
import org.kimios.utils.system.CustomScheduledThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ShareCleaner {

    private static Logger logger = LoggerFactory.getLogger(ShareCleaner.class);

    private volatile boolean active = true;
    private static Thread thrc;
    private ISecurityController securityController;
    private IShareController shareController;
    private CustomScheduledThreadPoolExecutor customScheduledThreadPoolExecutor;
    private boolean launchShareCleaner;

    public void startJob() {
        this.customScheduledThreadPoolExecutor = new CustomScheduledThreadPoolExecutor(1);

        String threadName = "Kimios Share Cleaner";
        String message = threadName + " is going to be launched";
        if (!this.launchShareCleaner) {
            message = threadName + " has NOT been launched";
            logger.info(message);
            // end
            return;
        }
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
        customScheduledThreadPoolExecutor.scheduleAtFixedRate(job, 0, 1, TimeUnit.MINUTES);

    }

    public void stopJob() {
        try {
            if(this.customScheduledThreadPoolExecutor != null){
                this.customScheduledThreadPoolExecutor.shutdownNow();
                this.customScheduledThreadPoolExecutor.awaitTermination(5, TimeUnit.SECONDS);
            }
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

    public boolean isLaunchShareCleaner() {
        return launchShareCleaner;
    }

    public void setLaunchShareCleaner(boolean launchShareCleaner) {
        this.launchShareCleaner = launchShareCleaner;
    }

}
