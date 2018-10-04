package org.kimios.telemetry.system;

import org.kimios.kernel.controller.ISecurityController;
import org.kimios.kernel.security.model.Session;
import org.kimios.telemetry.controller.CustomScheduledThreadPoolExecutor;
import org.kimios.telemetry.controller.ITelemetryController;
import org.kimios.telemetry.jobs.TelemetrySenderJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TelemetrySender implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(TelemetrySender.class);

    private static Thread thrc;
    private ISecurityController securityController;
    private ITelemetryController telemetryController;
    private CustomScheduledThreadPoolExecutor customScheduledThreadPoolExecutor;

    public void startJob() {
        this.customScheduledThreadPoolExecutor = new CustomScheduledThreadPoolExecutor(1);

        synchronized (this) {
            thrc = new Thread(this, "Kimios Telemetry Sender");
            thrc.start();
        }
    }

    public void stopJob() {
        try {
            this.customScheduledThreadPoolExecutor.shutdown();
            thrc.join();
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

    @Override
    public void run() {
        if (this.telemetryController.getUuid() != null) {
            try {
                Session session = this.securityController.startSession("admin", "kimios");
                TelemetrySenderJob job = new TelemetrySenderJob(this.telemetryController, session);
                this.customScheduledThreadPoolExecutor.scheduleAtFixedRate(job, 0, 1, TimeUnit.MINUTES);
            } catch (Exception e) {
                logger.info("Thread interrupted " + e.getMessage());
                this.stopJob();
            }
        }

    }
}
