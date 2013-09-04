package org.kimios.webservices.pojo;

import java.util.Date;

public class TaskWrapper {

    private Long id;
    private String name;
    private String description;
    private Long actorId;
    private Long assigneeId;
    private Date claimedDate;
    private Date expectedEndDate;
    private String priority;
    private String displayDescription;
    private String displayName;
    private Long executedBy;
    private Long flownodeDefinitionId;
    private Date lastUpdateDate;
    private Long parentContainerId;
    private Long parentProcessInstanceId;
    private Date reachedStateDate;
    private Long rootContainerId;
    private String state;
    private String stateCategory;
    private String type;
    private Long processDefinitionId;

    private String url;

    private ProcessWrapper processWrapper;

    @Override
    public String toString() {
        return "TaskWrapper{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", actorId=" + actorId +
                ", assigneeId=" + assigneeId +
                ", claimedDate=" + claimedDate +
                ", expectedEndDate=" + expectedEndDate +
                ", priority='" + priority + '\'' +
                ", displayDescription='" + displayDescription + '\'' +
                ", displayName='" + displayName + '\'' +
                ", executedBy=" + executedBy +
                ", flownodeDefinitionId=" + flownodeDefinitionId +
                ", lastUpdateDate=" + lastUpdateDate +
                ", parentContainerId=" + parentContainerId +
                ", parentProcessInstanceId=" + parentProcessInstanceId +
                ", reachedStateDate=" + reachedStateDate +
                ", rootContainerId=" + rootContainerId +
                ", state='" + state + '\'' +
                ", stateCategory='" + stateCategory + '\'' +
                ", type='" + type + '\'' +
                ", processDefinitionId=" + processDefinitionId +
                ", url='" + url + '\'' +
                ", processWrapper=" + processWrapper +
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

    public Date getClaimedDate() {
        return claimedDate;
    }

    public void setClaimedDate(Date claimedDate) {
        this.claimedDate = claimedDate;
    }

    public Date getExpectedEndDate() {
        return expectedEndDate;
    }

    public void setExpectedEndDate(Date expectedEndDate) {
        this.expectedEndDate = expectedEndDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDisplayDescription() {
        return displayDescription;
    }

    public void setDisplayDescription(String displayDescription) {
        this.displayDescription = displayDescription;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Long getExecutedBy() {
        return executedBy;
    }

    public void setExecutedBy(Long executedBy) {
        this.executedBy = executedBy;
    }

    public Long getFlownodeDefinitionId() {
        return flownodeDefinitionId;
    }

    public void setFlownodeDefinitionId(Long flownodeDefinitionId) {
        this.flownodeDefinitionId = flownodeDefinitionId;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Long getParentContainerId() {
        return parentContainerId;
    }

    public void setParentContainerId(Long parentContainerId) {
        this.parentContainerId = parentContainerId;
    }

    public Long getParentProcessInstanceId() {
        return parentProcessInstanceId;
    }

    public void setParentProcessInstanceId(Long parentProcessInstanceId) {
        this.parentProcessInstanceId = parentProcessInstanceId;
    }

    public Date getReachedStateDate() {
        return reachedStateDate;
    }

    public void setReachedStateDate(Date reachedStateDate) {
        this.reachedStateDate = reachedStateDate;
    }

    public Long getRootContainerId() {
        return rootContainerId;
    }

    public void setRootContainerId(Long rootContainerId) {
        this.rootContainerId = rootContainerId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateCategory() {
        return stateCategory;
    }

    public void setStateCategory(String stateCategory) {
        this.stateCategory = stateCategory;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(Long processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ProcessWrapper getProcessWrapper() {
        return processWrapper;
    }

    public void setProcessWrapper(ProcessWrapper processWrapper) {
        this.processWrapper = processWrapper;
    }
}
