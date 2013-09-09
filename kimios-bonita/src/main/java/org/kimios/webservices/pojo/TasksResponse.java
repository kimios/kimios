package org.kimios.webservices.pojo;

import java.util.List;

public class TasksResponse {

    private List<TaskWrapper> tasks;
    private long totalProperty;

    public TasksResponse() {
    }

    public TasksResponse(List<TaskWrapper> tasks, long totalProperty) {
        this.tasks = tasks;
        this.totalProperty = totalProperty;
    }

    public List<TaskWrapper> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskWrapper> tasks) {
        this.tasks = tasks;
    }

    public long getTotalProperty() {
        return totalProperty;
    }

    public void setTotalProperty(long totalProperty) {
        this.totalProperty = totalProperty;
    }
}
