package org.kimios.webservices.pojo;

import org.bonitasoft.engine.bpm.flownode.HumanTaskInstance;

public class TaskWrapper {

    private Long id;
    private String name;
    private String description;
    private Long actorId;
    private Long assigneeId;

    public TaskWrapper(HumanTaskInstance task) {
        id = task.getId();
        name = task.getName();
        description = task.getDescription();
        actorId = task.getActorId();
        assigneeId = task.getAssigneeId();
    }

    @Override
    public String toString() {
        return "TaskWrapper{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", actorId=" + actorId +
                ", assigneeId=" + assigneeId +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getActorId() {
        return actorId;
    }

    public void setActorId(Long actorId) {
        this.actorId = actorId;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }
}
