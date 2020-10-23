package org.kimios.utils.controller.threads.management;

import org.kimios.api.controller.IManageableServiceController;
import org.kimios.api.controller.ServiceWithThreadPoolExecutorManagerState;
import org.kimios.utils.system.CustomScheduledThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ManageableServiceController implements IManageableServiceController {

    private static Logger logger = LoggerFactory.getLogger(ManageableServiceController.class);

    protected CustomScheduledThreadPoolExecutor customScheduledThreadPoolExecutor;
    protected String serviceName;

    private long initialDelay, period;
    private TimeUnit periodTimeUnit;

    public ManageableServiceController() {
        this.initExecutor();
    }

    public ManageableServiceController(String serviceName) {
        this();
        this.serviceName = serviceName;
    }

    public ManageableServiceController(String serviceName, long initialDelay, long period, TimeUnit periodTimeUnit) {
        this(serviceName);
        this.initialDelay = initialDelay;
        this.period = period;
        this.periodTimeUnit = periodTimeUnit;
    }

    public long getInitialDelay() {
        return initialDelay;
    }

    public void setInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public TimeUnit getPeriodTimeUnit() {
        return periodTimeUnit;
    }

    public void setPeriodTimeUnit(TimeUnit periodTimeUnit) {
        this.periodTimeUnit = periodTimeUnit;
    }

    protected void initExecutor() {
        this.customScheduledThreadPoolExecutor = new CustomScheduledThreadPoolExecutor(8);
        this.customScheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
    }

    @Override
    public void pauseThreadPoolExecutor() throws InterruptedException {
        this.shutdownAndAwaitTermination(this.customScheduledThreadPoolExecutor);
    }

    @Override
    public void resumeThreadPoolExecutor() {
        this.initExecutor();
    }

    @Override
    public ServiceWithThreadPoolExecutorManagerState statusThreadPoolExecutor() {
        return this.customScheduledThreadPoolExecutor.isTerminated() ?
                ServiceWithThreadPoolExecutorManagerState.INACTIVE :
                this.customScheduledThreadPoolExecutor.getTaskCount() > 0 ?
                        ServiceWithThreadPoolExecutorManagerState.ACTIVE :
                        this.customScheduledThreadPoolExecutor.isTerminating() ?
                                ServiceWithThreadPoolExecutorManagerState.TERMINATING :
                                ServiceWithThreadPoolExecutorManagerState.INACTIVE;
    }

    @Override
    public String serviceName() {
        return this.serviceName;
    }

    private void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    public void scheduleJobAtFixedRate(Runnable job) {
        this.customScheduledThreadPoolExecutor.scheduleAtFixedRate(job, initialDelay, period, periodTimeUnit);
    }
}
