package org.kimios.osgi.utils.notifier;

import org.kimios.kernel.jobs.Job;
import org.kimios.kernel.jobs.JobImpl;
import org.kimios.kernel.notifier.jobs.NotifierThreadManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public class KimiosNotifierActivator implements BundleActivator {
    private static Logger logger = LoggerFactory.getLogger(KimiosNotifierActivator.class);

    Thread thread;
    NotifierThread notifierThread;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        logger.info("starting kimios Notifier");

        notifierThread = new NotifierThread();
        thread = new Thread(notifierThread);
        thread.start();

        logger.info("kimios Notifier started");
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        logger.info("shutting down kimios Notifier");
        if (thread != null) {
            notifierThread.terminate();
            thread.join();
            logger.info("kimios Notifier has been shut down");
        }
    }
}