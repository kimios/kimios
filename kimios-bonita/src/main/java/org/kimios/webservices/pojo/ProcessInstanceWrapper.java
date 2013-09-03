package org.kimios.webservices.pojo;

import java.util.Date;

public class ProcessInstanceWrapper {
    private Long id;
    private String name;
    private Long calledId;
    private String description;
    private Date endDate;
    private Date lastUpdate;
    private Long processDefinitionId;
    private Long rootProcessInstanceId;
    private Date startDate;
    private Long startBy;
    private String state;
    private String stringIndex1;
    private String stringIndex2;
    private String stringIndex3;
    private String stringIndex4;
    private String stringIndex5;

    @Override
    public String toString() {
        return "ProcessInstanceWrapper{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", calledId=" + calledId +
                ", description='" + description + '\'' +
                ", endDate=" + endDate +
                ", lastUpdate=" + lastUpdate +
                ", processDefinitionId=" + processDefinitionId +
                ", rootProcessInstanceId=" + rootProcessInstanceId +
                ", startDate=" + startDate +
                ", startBy=" + startBy +
                ", state='" + state + '\'' +
                ", stringIndex1='" + stringIndex1 + '\'' +
                ", stringIndex2='" + stringIndex2 + '\'' +
                ", stringIndex3='" + stringIndex3 + '\'' +
                ", stringIndex4='" + stringIndex4 + '\'' +
                ", stringIndex5='" + stringIndex5 + '\'' +
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

    public Long getCalledId() {
        return calledId;
    }

    public void setCalledId(Long calledId) {
        this.calledId = calledId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(Long processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public Long getRootProcessInstanceId() {
        return rootProcessInstanceId;
    }

    public void setRootProcessInstanceId(Long rootProcessInstanceId) {
        this.rootProcessInstanceId = rootProcessInstanceId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Long getStartBy() {
        return startBy;
    }

    public void setStartBy(Long startBy) {
        this.startBy = startBy;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStringIndex1() {
        return stringIndex1;
    }

    public void setStringIndex1(String stringIndex1) {
        this.stringIndex1 = stringIndex1;
    }

    public String getStringIndex2() {
        return stringIndex2;
    }

    public void setStringIndex2(String stringIndex2) {
        this.stringIndex2 = stringIndex2;
    }

    public String getStringIndex3() {
        return stringIndex3;
    }

    public void setStringIndex3(String stringIndex3) {
        this.stringIndex3 = stringIndex3;
    }

    public String getStringIndex4() {
        return stringIndex4;
    }

    public void setStringIndex4(String stringIndex4) {
        this.stringIndex4 = stringIndex4;
    }

    public String getStringIndex5() {
        return stringIndex5;
    }

    public void setStringIndex5(String stringIndex5) {
        this.stringIndex5 = stringIndex5;
    }
}
