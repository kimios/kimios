package org.kimios.osgi.utils.notifier;

import org.kimios.kernel.index.controller.ISearchController;
import org.kimios.kernel.security.ISessionManager;
import org.kimios.kernel.security.model.Session;
import org.kimios.notifier.controller.NotifierController;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotifierServiceTrackerCustomizer implements ServiceTrackerCustomizer {
    private static Logger logger = LoggerFactory.getLogger(NotifierServiceTrackerCustomizer.class);

    private BundleContext context = null;
    private MyThread thread;
    private ISessionManager sessionManager;

    public NotifierServiceTrackerCustomizer(BundleContext context, ISessionManager sManager) {
        this.context = context;
        this.sessionManager = sManager;
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {
        ISearchController searchController = (ISearchController) context.getService(serviceReference);

        thread = new MyThread(searchController, sessionManager);
        thread.start();
        return searchController;
    }

    @Override
    public void modifiedService(ServiceReference serviceReference, Object o) {

    }

    @Override
    public void removedService(ServiceReference serviceReference, Object o) {
        context.ungetService(serviceReference);
        System.out.println("How sad. SearchController is gone");
        thread.stopThread();
    }

    public static class MyThread extends Thread {

        private volatile boolean active = true;
        private final ISearchController searchService;
        private final ISessionManager sessionManager;

        public MyThread(ISearchController sService, ISessionManager sManager) {
            this.searchService = sService;
            this.sessionManager = sManager;
        }

        public void run() {
            NotifierController notifierController = new NotifierController(searchService);
            Session session = this.sessionManager.startSession("admin", "kimios");
            while (active) {
                try {
                    Thread.sleep(5000);
                    if (active) {
                        logger.info("createNotifications now");
                        notifierController.createNotifications(session);
                    }
                } catch (Exception e) {
                    System.out.println("Thread interrupted " + e.getMessage());
                }
            }
        }

        public void stopThread() {
            active = false;
        }
    }
}
