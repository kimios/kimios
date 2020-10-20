package org.kimios.notifier.system;

import org.apache.commons.lang.StringUtils;
import org.kimios.kernel.controller.ISecurityController;
import org.kimios.kernel.deployment.DataInitializerCtrl;
import org.kimios.kernel.security.model.Session;
import org.kimios.notifier.controller.INotifierController;
import org.kimios.notifier.jobs.NotificationCreatorJob;
import org.kimios.utils.controller.threads.management.InSessionManageableServiceController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class NotificationCreator extends InSessionManageableServiceController {

    private static Logger logger = LoggerFactory.getLogger(NotificationCreator.class);

    private ISecurityController securityController;
    private INotifierController notifierController;

    public NotificationCreator() {
        super("Notification Creator", 0, 1, TimeUnit.MINUTES);
        this.setDomain(StringUtils.isEmpty(System.getenv(DataInitializerCtrl.KIMIOS_DEFAULT_DOMAIN)) ?
                (StringUtils.isEmpty(System.getProperty("kimios.default.domain")) ? "kimios" : System.getProperty("kimios.default.domain")) :
                System.getenv(DataInitializerCtrl.KIMIOS_DEFAULT_DOMAIN));

        this.setLogin(StringUtils.isEmpty(System.getenv(DataInitializerCtrl.KIMIOS_ADMIN_USERID)) ?
                (StringUtils.isEmpty(System.getProperty("kimios.admin.userid")) ? "admin" : System.getProperty("kimios.admin.userid")) :
                System.getenv(DataInitializerCtrl.KIMIOS_ADMIN_USERID));
    }

    public void startJob() {
        Session session = this.securityController.startSession(this.getLogin(), this.getDomain());
        NotificationCreatorJob job = new NotificationCreatorJob(notifierController, session);
        this.scheduleJobAtFixedRate(job);
    }

    public void stopJob() {
        try {
            if(this.customScheduledThreadPoolExecutor != null){
                this.customScheduledThreadPoolExecutor.shutdownNow();
                this.customScheduledThreadPoolExecutor.awaitTermination(5, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.info("Notification Creator stopped");
    }


    public INotifierController getNotifierController() {
        return notifierController;
    }

    public void setNotifierController(INotifierController notifierController) {
        this.notifierController = notifierController;
    }

    public ISecurityController getSecurityController() {
        return securityController;
    }

    public void setSecurityController(ISecurityController securityController) {
        this.securityController = securityController;
    }

    @Override
    public void resumeThreadPoolExecutor() {
        super.resumeThreadPoolExecutor();
        Session session = this.securityController.startSession(this.getLogin(), this.getDomain());
        NotificationCreatorJob job = new NotificationCreatorJob(notifierController, session);
        this.scheduleJobAtFixedRate(job);
    }
}
