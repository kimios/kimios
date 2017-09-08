package org.kimios.kernel.notifier;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KimiosNotifierActivator implements BundleActivator {
    private static Logger logger = LoggerFactory.getLogger(KimiosNotifierActivator.class);

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        logger.info("starting kimios Notifier");
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        logger.info("shutting down kimios Notifier");
    }
}