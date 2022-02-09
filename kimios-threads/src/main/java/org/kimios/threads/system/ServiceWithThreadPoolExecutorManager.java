package org.kimios.threads.system;

import org.kimios.api.controller.IManageableServiceController;
import org.kimios.api.controller.IServiceWithThreadPoolExecutorManager;
import org.kimios.api.controller.ServiceWithThreadPoolExecutorManagerState;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceWithThreadPoolExecutorManager implements IServiceWithThreadPoolExecutorManager {

    private static Logger log = LoggerFactory.getLogger(ServiceWithThreadPoolExecutorManager.class);

    private List<IManageableServiceController> controllerList;

    private Map<Integer, IManageableServiceController> controllerMap = new HashMap<>();

    public List<IManageableServiceController> getControllerList() {
        return controllerList;
    }

    public void setControllerList(List<IManageableServiceController> controllerList) {
        this.controllerList = controllerList;
    }

    @Override
    public void stopAll() throws InterruptedException {
        log.info("stopping all threads now");
        for (IManageableServiceController controller : controllerList) {
            controller.pauseThreadPoolExecutor();
        }
        log.info("all threads stopped");
    }

    @Override
    public void startAll() {
        log.info("starting all threads now");
        for (IManageableServiceController controller : controllerList) {
            controller.resumeThreadPoolExecutor();
        }
        log.info("all threads started");
    }

    @Override
    public Map<Integer, AbstractMap.SimpleEntry<String, ServiceWithThreadPoolExecutorManagerState>> statusAll() {
        Map<Integer, AbstractMap.SimpleEntry<String, ServiceWithThreadPoolExecutorManagerState>> map = new HashMap<>();
        this.controllerMap.keySet().forEach(hashCode ->
            map.put(
                    hashCode,
                    new AbstractMap.SimpleEntry<String, ServiceWithThreadPoolExecutorManagerState>(
                            this.controllerMap.get(hashCode).serviceName(),
                            this.controllerMap.get(hashCode).statusThreadPoolExecutor()
                    )
            )
        );
        return map;
    }

    @Override
    public void startServiceThreadPoolExecutor(Integer id) throws Exception {
        this.controllerMap.get(id).resumeThreadPoolExecutor();
    }

    @Override
    public void stopServiceThreadPoolExecutor(Integer id) throws Exception {
        this.controllerMap.get(id).pauseThreadPoolExecutor();
    }

    public void init() {
        log.info("threads manager init");
    }

    public void destroy() {
        log.info("threads manager destroy");
    }

    public void bind(ServiceReference reference) {
        log.info(">>> bind service");
        Arrays.asList(reference.getPropertyKeys()).forEach(key ->
                log.info("service property: " + key + " : " + reference.getProperty(key))
        );
        this.controllerMap.put(
                reference.hashCode(),
                (IManageableServiceController) reference.getBundle().getBundleContext().getService(reference)
        );
        log.info("<<<");
    }

    public void bind(Serializable service) {
    }

    public void unbind(ServiceReference reference) {
        log.info(">>> unbind service");
        if (reference != null) {
            Arrays.asList(reference.getPropertyKeys()).forEach(key ->
                    log.info("service property: " + key + " : " + reference.getProperty(key))
            );
            this.controllerMap.remove(reference.hashCode());
        } else {
            log.info("reference is null");
        }
        log.info("<<<");
    }
}
