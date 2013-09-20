package org.kimios.webservices.impl.factory;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.bpm.flownode.HumanTaskInstance;
import org.bonitasoft.engine.identity.UserNotFoundException;
import org.kimios.webservices.pojo.TaskWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskWrapperFactory {
    private static Logger log = LoggerFactory.getLogger(TaskWrapperFactory.class);

    public static TaskWrapper createTaskWrapper(HumanTaskInstance task, IdentityAPI identityAPI) throws UserNotFoundException {
        TaskWrapper wrapper = new TaskWrapper();
        wrapper.setId(task.getId());
        wrapper.setName(task.getName());
        wrapper.setDescription(task.getDescription());
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

        if (task.getActorId() > 1)
            wrapper.setActor(UserWrapperFactory.createUserWrapper(identityAPI.getUser(task.getActorId())));

        if (task.getAssigneeId() > 1)
            wrapper.setAssignee(UserWrapperFactory.createUserWrapper(identityAPI.getUser(task.getAssigneeId())));

        return wrapper;
    }

}
