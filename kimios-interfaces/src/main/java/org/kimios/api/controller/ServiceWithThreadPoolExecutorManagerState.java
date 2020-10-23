package org.kimios.api.controller;

public enum ServiceWithThreadPoolExecutorManagerState {
    ACTIVE("active"),
    TERMINATING("terminating"),
    INACTIVE("inactive");

    private final String value;

    ServiceWithThreadPoolExecutorManagerState(String state) {
        this.value = state;
    }

    public String getValue() {
        return value;
    }
}
