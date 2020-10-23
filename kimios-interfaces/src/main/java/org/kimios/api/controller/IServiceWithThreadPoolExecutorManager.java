package org.kimios.api.controller;

import java.util.AbstractMap;
import java.util.Map;

public interface IServiceWithThreadPoolExecutorManager {

    public void stopAll() throws InterruptedException;

    public void startAll();

    public Map<Integer, AbstractMap.SimpleEntry<String, String>> statusAll();

    public void startServiceThreadPoolExecutor(Integer id) throws Exception;

    public void stopServiceThreadPoolExecutor(Integer id) throws Exception;
}
