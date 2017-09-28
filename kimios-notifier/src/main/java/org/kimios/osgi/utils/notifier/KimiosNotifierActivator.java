package org.kimios.osgi.utils.notifier;

import org.kimios.kernel.index.controller.ISearchController;
import org.kimios.kernel.security.ISessionManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KimiosNotifierActivator implements BundleActivator {
    private static Logger logger = LoggerFactory.getLogger(KimiosNotifierActivator.class);

    private ServiceTracker serviceTracker;
    private ServiceTracker sessionManagerTracker;
    private ISessionManager sessionManager;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        logger.info("starting kimios Notifier");

        sessionManagerTracker = new ServiceTracker(bundleContext, ISessionManager.class.getName(), null);
        sessionManagerTracker.open();
        sessionManager = (ISessionManager) sessionManagerTracker.getService();
        while (sessionManager == null) {
            sessionManager = (ISessionManager) sessionManagerTracker.getService();
            Thread.sleep(1000);
        }

        NotifierServiceTrackerCustomizer customizer = new NotifierServiceTrackerCustomizer(bundleContext, sessionManager);
        serviceTracker = new ServiceTracker(bundleContext, ISearchController.class
                .getName(), customizer);
        serviceTracker.open();

        logger.info("kimios Notifier started");
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        logger.info("shutting down kimios Notifier");
        sessionManagerTracker.close();
        serviceTracker.close();
        logger.info("kimios Notifier has been shut down");
    }
}