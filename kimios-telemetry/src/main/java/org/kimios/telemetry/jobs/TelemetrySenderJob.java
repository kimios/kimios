package org.kimios.telemetry.jobs;

import org.kimios.kernel.jobs.JobImpl;
import org.kimios.telemetry.controller.ITelemetryController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.kimios.kernel.security.model.Session;

import java.util.UUID;

public class TelemetrySenderJob extends JobImpl<Integer> {

    private static Logger log = LoggerFactory.getLogger(TelemetrySenderJob.class);

    private ITelemetryController telemetryController;

    public TelemetrySenderJob(ITelemetryController telemetryController, Session session) {
        //generate task id
        super( UUID.randomUUID().toString() );

        this.telemetryController = telemetryController;
        this.setSession(session);
    }

    @Override
    public Integer execute() throws Exception {
        log.debug("Starting sending notifications");
        try {
            this.telemetryController.sendToTelemetryPHP(this.getUserSession());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return 1;
    }
}
