package org.kimios.webservices.pojo;

import org.bonitasoft.engine.bpm.process.ProcessDeploymentInfo;

public class ProcessWrapper {

    private Long id;
    private Long processId;
    private String name;
    private String description;

    public ProcessWrapper(ProcessDeploymentInfo process) {
        id = process.getId();
        processId = process.getProcessId();
        name = process.getName();
        description = process.getDescription();
    }

    @Override
    public String toString() {
        return "ProcessWrapper{" +
                "id=" + id +
                ", processId=" + processId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
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
}
