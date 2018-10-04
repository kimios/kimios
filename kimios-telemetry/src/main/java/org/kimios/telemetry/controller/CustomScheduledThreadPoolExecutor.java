package org.kimios.telemetry.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

public class CustomScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {

    private static Logger logger = LoggerFactory.getLogger(CustomScheduledThreadPoolExecutor.class);

    public CustomScheduledThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                logger.error("ThreadPoolExecutor rejected Runnable");
            }
        });
    }

}
