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

    private ISecurityController securityController;
    private IShareController shareController;
    private CustomScheduledThreadPoolExecutor customScheduledThreadPoolExecutor;
    private boolean launchShareCleaner;

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
        this.customScheduledThreadPoolExecutor = new CustomScheduledThreadPoolExecutor(8);
        ShareCleanerJob job = new ShareCleanerJob(shareController, session);
        this.customScheduledThreadPoolExecutor.scheduleAtFixedRate(job, 0, 5, TimeUnit.SECONDS);

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
}
