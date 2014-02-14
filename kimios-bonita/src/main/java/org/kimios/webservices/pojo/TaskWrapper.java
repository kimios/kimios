/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kimios.webservices.pojo;

import java.util.Calendar;
import java.util.List;

public class TaskWrapper {

    private Long id;
    private String name;
    private String description;
    private Calendar claimedDate;
    private Calendar expectedEndDate;
    private String priority;
    private String displayDescription;
    private String displayName;
    private Long executedBy;
    private Long flownodeDefinitionId;
    private Calendar lastUpdateDate;
    private Long parentContainerId;
    private Long parentProcessInstanceId;
    private Calendar reachedStateDate;
    private Long rootContainerId;
    private String state;
    private String stateCategory;
    private String type;
    private Long processDefinitionId;

    // direct url to Task

    private String url;

    // references to Process and Comments

    private ProcessWrapper processWrapper;
    private List<CommentWrapper> commentWrappers;

    // associated users

    private UserWrapper actor;
    private UserWrapper assignee;

    @Override
    public String toString() {
        return "TaskWrapper{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", actor=" + actor +
                ", assignee=" + assignee +
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
                ", commentWrappers=" + commentWrappers +
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

    public UserWrapper getActor() {
        return actor;
    }

    public void setActor(UserWrapper actor) {
        this.actor = actor;
    }

    public UserWrapper getAssignee() {
        return assignee;
    }

    public void setAssignee(UserWrapper assignee) {
        this.assignee = assignee;
    }

    public Calendar getClaimedDate() {
        return claimedDate;
    }

    public void setClaimedDate(Calendar claimedDate) {
        this.claimedDate = claimedDate;
    }

    public Calendar getExpectedEndDate() {
        return expectedEndDate;
    }

    public void setExpectedEndDate(Calendar expectedEndDate) {
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

    public Calendar getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Calendar lastUpdateDate) {
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

    public Calendar getReachedStateDate() {
        return reachedStateDate;
    }

    public void setReachedStateDate(Calendar reachedStateDate) {
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

    public List<CommentWrapper> getCommentWrappers() {
        return commentWrappers;
    }

    public void setCommentWrappers(List<CommentWrapper> commentWrappers) {
        this.commentWrappers = commentWrappers;
    }
}
