package org.kimios.webservices.pojo;

import java.util.List;

public class TasksResponse {

    private List<TaskWrapper> tasks;
    private Number totalProperty;

    public TasksResponse() {
    }

    public TasksResponse(List<TaskWrapper> tasks, Number totalProperty) {
        this.tasks = tasks;
        this.totalProperty = totalProperty;
    }

    public List<TaskWrapper> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskWrapper> tasks) {
        this.tasks = tasks;
    }

    public Number getTotalProperty() {
        return totalProperty;
    }

    public void setTotalProperty(Number totalProperty) {
        this.totalProperty = totalProperty;
    }
}
