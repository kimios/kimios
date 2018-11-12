package org.kimios.telemetry.system;

import org.apache.commons.lang.StringUtils;
import org.kimios.kernel.controller.ISecurityController;
import org.kimios.kernel.deployment.DataInitializerCtrl;
import org.kimios.kernel.security.model.Session;
import org.kimios.telemetry.controller.CustomScheduledThreadPoolExecutor;
import org.kimios.telemetry.controller.ITelemetryController;
import org.kimios.telemetry.jobs.TelemetrySenderJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TelemetrySender {

    private static Logger logger = LoggerFactory.getLogger(TelemetrySender.class);

    private ISecurityController securityController;
    private ITelemetryController telemetryController;
    private CustomScheduledThreadPoolExecutor customScheduledThreadPoolExecutor;

    public void startJob() {
        this.customScheduledThreadPoolExecutor = new CustomScheduledThreadPoolExecutor(1);

        if (this.telemetryController.getUuid() != null) {
            try {

                String defaultDomain =
                        StringUtils.isEmpty(System.getenv(DataInitializerCtrl.KIMIOS_DEFAULT_DOMAIN)) ?
                                (StringUtils.isEmpty(System.getProperty("kimios.default.domain")) ? "kimios" : System.getProperty("kimios.default.domain")) :
                                System.getenv(DataInitializerCtrl.KIMIOS_DEFAULT_DOMAIN);

                String adminLogin =
                        StringUtils.isEmpty(System.getenv(DataInitializerCtrl.KIMIOS_ADMIN_USERID)) ?
                                (StringUtils.isEmpty(System.getProperty("kimios.admin.userid")) ? "admin" : System.getProperty("kimios.admin.userid")) :
                                System.getenv(DataInitializerCtrl.KIMIOS_ADMIN_USERID);

                Session session = this.securityController.startSession(adminLogin, defaultDomain);
                TelemetrySenderJob job = new TelemetrySenderJob(this.telemetryController, session);
                this.customScheduledThreadPoolExecutor.scheduleAtFixedRate(job, 0, 1, TimeUnit.MINUTES);
            } catch (Exception e) {
                logger.info("Thread interrupted " + e.getMessage());
                this.stopJob();
            }
        } else {
            logger.warn("telemetry uuid isn't available. statistics upload disabled");
        }
    }

    public void stopJob() {
        try {
            this.customScheduledThreadPoolExecutor.shutdownNow();
            this.customScheduledThreadPoolExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("Exception raised while shutting down the job: " + e.getMessage());
        }
        logger.info("Notification Sender stopped");
    }

    public ISecurityController getSecurityController() {
        return securityController;
    }

    public void setSecurityController(ISecurityController securityController) {
        this.securityController = securityController;
    }
    public ITelemetryController getTelemetryController() {
        return telemetryController;
    }

    public void setTelemetryController(ITelemetryController telemetryController) {
        this.telemetryController = telemetryController;
    }
}
