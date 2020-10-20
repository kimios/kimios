package org.kimios.kernel.share.system;

import org.apache.commons.lang.StringUtils;
import org.kimios.kernel.controller.ISecurityController;
import org.kimios.kernel.deployment.DataInitializerCtrl;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.share.controller.IShareController;
import org.kimios.kernel.share.jobs.ShareCleanerJob;
import org.kimios.utils.controller.threads.management.InSessionManageableServiceController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ShareCleaner extends InSessionManageableServiceController {

    private static Logger logger = LoggerFactory.getLogger(ShareCleaner.class);

    private ISecurityController securityController;
    private IShareController shareController;

    private boolean launchShareCleaner;

    public ShareCleaner() {
        super("Share Cleaner", 0, 1, TimeUnit.MINUTES);
        this.setDomain(StringUtils.isEmpty(System.getenv(DataInitializerCtrl.KIMIOS_DEFAULT_DOMAIN)) ?
                        (StringUtils.isEmpty(System.getProperty("kimios.default.domain")) ? "kimios" : System.getProperty("kimios.default.domain")) :
                        System.getenv(DataInitializerCtrl.KIMIOS_DEFAULT_DOMAIN));

        this.setLogin(StringUtils.isEmpty(System.getenv(DataInitializerCtrl.KIMIOS_ADMIN_USERID)) ?
                        (StringUtils.isEmpty(System.getProperty("kimios.admin.userid")) ? "admin" : System.getProperty("kimios.admin.userid")) :
                        System.getenv(DataInitializerCtrl.KIMIOS_ADMIN_USERID));
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

        Session session = this.securityController.startSession(this.getLogin(), this.getDomain());
        ShareCleanerJob job = new ShareCleanerJob(shareController, session);
        this.scheduleJobAtFixedRate(job);

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
    public void resumeThreadPoolExecutor() {
        super.resumeThreadPoolExecutor();
        Session session = this.securityController.startSession(this.getLogin(), this.getDomain());
        ShareCleanerJob job = new ShareCleanerJob(shareController, session);
        this.scheduleJobAtFixedRate(job);
    }
}
