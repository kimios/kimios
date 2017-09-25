package org.kimios.osgi.utils.notifier;

import org.kimios.kernel.index.controller.ISearchController;
import org.kimios.kernel.security.ISessionManager;
import org.kimios.kernel.security.model.Session;
import org.kimios.notifier.controller.NotifierController;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KimiosNotifierActivator implements BundleActivator {
    private static Logger logger = LoggerFactory.getLogger(KimiosNotifierActivator.class);

    private ServiceTracker searchServiceTracker;
    private ServiceTracker sessionManagerTracker;
    private NotifierThread thread;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        logger.info("starting kimios Notifier");

        searchServiceTracker = new ServiceTracker(bundleContext, ISearchController.class.getName(), null);
        searchServiceTracker.open();
        ISearchController searchController = (ISearchController) searchServiceTracker.getService();
        while (searchController == null) {
            searchController = (ISearchController) searchServiceTracker.getService();
            Thread.sleep(1000);
        }

        sessionManagerTracker = new ServiceTracker(bundleContext, ISessionManager.class.getName(), null);
        sessionManagerTracker.open();
        ISessionManager sessionManager = (ISessionManager) sessionManagerTracker.getService();
        while (sessionManager == null) {
            sessionManager = (ISessionManager) sessionManagerTracker.getService();
            Thread.sleep(1000);
        }

        thread = new NotifierThread(searchController, sessionManager);
       
        logger.info("kimios Notifier started");
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        logger.info("shutting down kimios Notifier");
        thread.stopThread();
        searchServiceTracker.close();
        sessionManagerTracker.close();
        logger.info("kimios Notifier has been shut down");
    }

    public static class NotifierThread extends Thread {

        private volatile boolean active = true;
        private final ISearchController searchController;
        private final ISessionManager sessionManager;

        private NotifierThread(ISearchController sController, ISessionManager sManager) {
            this.searchController = sController;
            this.sessionManager = sManager;
        }

        public void run() {
            NotifierController notifierController = new NotifierController(searchController);
            try {
                Session session = sessionManager.startSession("admin", "kimios", "kimios");
                while (active) {
                    Thread.sleep(5000);
                    if (active) {
                        notifierController.createNotifications(session);
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception in Thread: " + e.getCause());
            }
        }

        private void stopThread() {
            active = false;
        }
    }
}