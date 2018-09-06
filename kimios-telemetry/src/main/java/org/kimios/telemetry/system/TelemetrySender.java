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

    private volatile boolean active = true;
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
            this.stop();
            thrc.join();
        } catch (Exception e) {
        }
        logger.info("Notification Sender stopped");
    }

    public void stop() {
        this.active = false;
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
        Session session = this.securityController.startSession("admin", "kimios");
        //while (active) {
            try {
                if (active) {
                    TelemetrySenderJob job = new TelemetrySenderJob(this.telemetryController, session);
                    this.customScheduledThreadPoolExecutor.scheduleAtFixedRate(job, 0, 1, TimeUnit.MINUTES);
                }
//                Thread.sleep(5000);
            } catch (Exception e) {
                this.stop();
                logger.info("Thread interrupted " + e.getMessage());
            }
        //}
    }
}
