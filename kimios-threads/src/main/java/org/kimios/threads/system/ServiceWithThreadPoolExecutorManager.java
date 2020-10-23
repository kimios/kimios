package org.kimios.threads.system;

import org.kimios.api.controller.IManageableServiceController;
import org.kimios.api.controller.IServiceWithThreadPoolExecutorManager;
import org.kimios.api.controller.ServiceWithThreadPoolExecutorManagerState;
import org.osgi.framework.ServiceReference;

import java.io.Serializable;
import java.util.*;

public class ServiceWithThreadPoolExecutorManager implements IServiceWithThreadPoolExecutorManager {

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
        System.out.println("stopping all threads now");
        for (IManageableServiceController controller : controllerList) {
            controller.pauseThreadPoolExecutor();
        }
        System.out.println("all threads stopped");
    }

    @Override
    public void startAll() {
        System.out.println("starting all threads now");
        for (IManageableServiceController controller : controllerList) {
            controller.resumeThreadPoolExecutor();
        }
        System.out.println("all threads started");
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
        System.out.println("threads manager init");
    }

    public void destroy() {
        System.out.println("threads manager destroy");
    }

    public void bind(ServiceReference reference) {
        System.out.println(">>> bind service");
        Arrays.asList(reference.getPropertyKeys()).forEach(key ->
                System.out.println("service property: " + key + " : " + reference.getProperty(key))
        );
        this.controllerMap.put(
                reference.hashCode(),
                (IManageableServiceController) reference.getBundle().getBundleContext().getService(reference)
        );
        System.out.println("<<<");
    }

    public void bind(Serializable service) {
    }

    public void unbind(ServiceReference reference) {
        System.out.println(">>> unbind service");
        Arrays.asList(reference.getPropertyKeys()).forEach(key ->
                System.out.println("service property: " + key + " : " + reference.getProperty(key))
        );
        this.controllerMap.remove(reference.hashCode());
        System.out.println("<<<");
    }
}
