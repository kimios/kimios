package org.kimios.api.controller;

public interface IManageableServiceController {
    public void pauseThreadPoolExecutor() throws InterruptedException;
    public void resumeThreadPoolExecutor();
    public String statusThreadPoolExecutor();
    public String serviceName();
}
