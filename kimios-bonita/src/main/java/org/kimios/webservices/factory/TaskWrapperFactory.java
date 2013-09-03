package org.kimios.webservices.factory;

import org.bonitasoft.engine.bpm.flownode.HumanTaskInstance;
import org.kimios.webservices.pojo.TaskWrapper;

public class TaskWrapperFactory {

    public static TaskWrapper createTaskWrapper(HumanTaskInstance task) {
        TaskWrapper wrapper = new TaskWrapper();
        wrapper.setId(task.getId());
        wrapper.setName(task.getName());
        wrapper.setDescription(task.getDescription());
        wrapper.setActorId(task.getActorId());
        wrapper.setAssigneeId(task.getAssigneeId());
        wrapper.setClaimedDate(task.getClaimedDate());
        wrapper.setExpectedEndDate(task.getExpectedEndDate());
        wrapper.setPriority(task.getPriority() != null ? task.getPriority().name() : null);
        wrapper.setDisplayDescription(task.getDisplayDescription());
        wrapper.setDisplayName(task.getDisplayName());
        wrapper.setExecutedBy(task.getExecutedBy());
        wrapper.setFlownodeDefinitionId(task.getFlownodeDefinitionId());
        wrapper.setLastUpdateDate(task.getLastUpdateDate());
        wrapper.setParentContainerId(task.getParentContainerId());
        wrapper.setParentProcessInstanceId(task.getParentProcessInstanceId());
        wrapper.setProcessDefinitionId(task.getProcessDefinitionId());
        wrapper.setReachedStateDate(task.getReachedStateDate());
        wrapper.setRootContainerId(task.getRootContainerId());
        wrapper.setState(task.getState());
        wrapper.setStateCategory(task.getStateCategory() != null ? task.getStateCategory().name() : null);
        wrapper.setType(task.getType() != null ? task.getType().name() : null);
        return wrapper;
    }

}
