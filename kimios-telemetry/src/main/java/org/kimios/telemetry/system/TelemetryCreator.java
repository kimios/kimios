package org.kimios.telemetry.system;

import org.kimios.kernel.controller.ISecurityController;
import org.kimios.kernel.index.controller.CustomThreadPoolExecutor;
import org.kimios.kernel.security.model.Session;
import org.kimios.telemetry.controller.ITelemetryController;
import org.kimios.telemetry.jobs.TelemetryCreatorJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TelemetryCreator implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(TelemetryCreator.class);

    private volatile boolean active = true;
    private static Thread thrc;
    private ISecurityController securityController;
    private ITelemetryController telemetryController;
    private CustomThreadPoolExecutor customThreadPoolExecutor;

    public void startJob() {
        this.customThreadPoolExecutor = new CustomThreadPoolExecutor(8, 8,
                10000L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());

        synchronized (this) {
            thrc = new Thread(this, "Kimios Notification Creator");
            thrc.start();
        }
    }

    public void stopJob() {
        try {
            this.stop();
            thrc.join();
        } catch (Exception e) {
        }
        logger.info("Notification Creator stopped");
    }

    public void stop() {
        this.active = false;
    }

    public ITelemetryController getTelemetryController() {
        return telemetryController;
    }

    public void setTelemetryController(ITelemetryController telemetryController) {
        this.telemetryController = telemetryController;
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
                    logger.info("createNotifications now");
                    TelemetryCreatorJob job = new TelemetryCreatorJob(telemetryController, session);
                    Integer i = customThreadPoolExecutor.submit(job).get();
                    logger.info("created " + i + " notification(s)");
                }
                Thread.sleep(5000);
            } catch (Exception e) {
                this.stop();
                logger.info("Thread interrupted " + e.getMessage());
            }
        }
    }
}
