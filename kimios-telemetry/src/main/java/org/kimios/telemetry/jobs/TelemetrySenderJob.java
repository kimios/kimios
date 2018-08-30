package org.kimios.telemetry.jobs;

import org.kimios.kernel.jobs.JobImpl;
import org.kimios.kernel.security.model.Session;
import org.kimios.telemetry.controller.ITelemetryController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class TelemetrySenderJob extends JobImpl<Integer> implements Runnable {

    private static Logger log = LoggerFactory.getLogger(TelemetrySenderJob.class);

    private ITelemetryController telemetryController;

    public TelemetrySenderJob(ITelemetryController telemetryController, Session session) {
        //generate task id
        super( UUID.randomUUID().toString() );

        this.telemetryController = telemetryController;
        this.setSession(session);
    }

    @Override
    public void run() {
        log.debug("Starting one job sending notifications");
        try {
            this.telemetryController.sendNotifications(this.getUserSession());
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    @Override
    public Integer execute() {
        try {
            this.run();
        } catch (Exception e) {
            throw e;
        }
        return 1;
    }
}
