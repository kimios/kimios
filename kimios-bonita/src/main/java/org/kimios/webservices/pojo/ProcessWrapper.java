package org.kimios.webservices.pojo;

import java.util.Date;

public class ProcessWrapper {

    private Long id;
    private Long processId;
    private String name;
    private String description;
    private String activationState;
    private String configurationState;
    private Long deployedBy;
    private Date deploymentDate;
    private String displayName;
    private String displayDescription;
    private String iconPath;
    private Date lastUpdateDate;
    private String version;

    private String url;

    @Override
    public String toString() {
        return "ProcessWrapper{" +
                "id=" + id +
                ", processId=" + processId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", activationState='" + activationState + '\'' +
                ", configurationState='" + configurationState + '\'' +
                ", deployedBy=" + deployedBy +
                ", deploymentDate=" + deploymentDate +
                ", displayName='" + displayName + '\'' +
                ", displayDescription='" + displayDescription + '\'' +
                ", iconPath='" + iconPath + '\'' +
                ", lastUpdateDate=" + lastUpdateDate +
                ", version='" + version + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
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

    public String getActivationState() {
        return activationState;
    }

    public void setActivationState(String activationState) {
        this.activationState = activationState;
    }

    public String getConfigurationState() {
        return configurationState;
    }

    public void setConfigurationState(String configurationState) {
        this.configurationState = configurationState;
    }

    public Long getDeployedBy() {
        return deployedBy;
    }

    public void setDeployedBy(Long deployedBy) {
        this.deployedBy = deployedBy;
    }

    public Date getDeploymentDate() {
        return deploymentDate;
    }

    public void setDeploymentDate(Date deploymentDate) {
        this.deploymentDate = deploymentDate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayDescription() {
        return displayDescription;
    }

    public void setDisplayDescription(String displayDescription) {
        this.displayDescription = displayDescription;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
