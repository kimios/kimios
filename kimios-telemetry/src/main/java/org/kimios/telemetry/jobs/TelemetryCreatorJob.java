package org.kimios.telemetry.jobs;

import org.kimios.kernel.jobs.JobImpl;
import org.kimios.kernel.security.model.Session;
import org.kimios.telemetry.controller.ITelemetryController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class TelemetryCreatorJob extends JobImpl<Integer> {

    private static Logger log = LoggerFactory.getLogger(TelemetryCreatorJob.class);

    private ITelemetryController telemetryController;

    public TelemetryCreatorJob(ITelemetryController telemetryController, Session session) {
        //generate task id
        super( UUID.randomUUID().toString() );

        this.telemetryController = telemetryController;
        this.setSession(session);
    }

    @Override
    public Integer execute() throws Exception {
        log.debug("Starting creating notifications");
        return this.telemetryController.createNotifications(getUserSession());
    }
}
