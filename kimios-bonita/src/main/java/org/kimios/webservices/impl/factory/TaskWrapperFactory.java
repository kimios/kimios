package org.kimios.webservices.impl.factory;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.bpm.flownode.HumanTaskInstance;
import org.bonitasoft.engine.bpm.flownode.MultiInstanceActivityInstance;
import org.bonitasoft.engine.bpm.flownode.impl.MultiInstanceActivityInstanceImpl;
import org.bonitasoft.engine.bpm.flownode.impl.UserTaskInstanceImpl;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.identity.UserNotFoundException;
import org.kimios.webservices.pojo.TaskWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TaskWrapperFactory {
    private static Logger log = LoggerFactory.getLogger(TaskWrapperFactory.class);

    public static TaskWrapper createTaskWrapper(HumanTaskInstance task, IdentityAPI identityAPI) throws UserNotFoundException {
        TaskWrapper wrapper = new TaskWrapper();
        wrapper.setId(task.getId());
        wrapper.setName(task.getName());
        wrapper.setDescription(task.getDescription());

        wrapper.setClaimedDate(getCalendar(task.getClaimedDate()));
        wrapper.setExpectedEndDate(getCalendar(task.getExpectedEndDate()));
        wrapper.setPriority(task.getPriority() != null ? task.getPriority().name() : null);
        wrapper.setDisplayDescription(task.getDisplayDescription());
        wrapper.setDisplayName(task.getDisplayName());
        wrapper.setExecutedBy(task.getExecutedBy());
        wrapper.setFlownodeDefinitionId(task.getFlownodeDefinitionId());
        wrapper.setLastUpdateDate(getCalendar(task.getLastUpdateDate()));
        wrapper.setParentContainerId(task.getParentContainerId());
        wrapper.setParentProcessInstanceId(task.getParentProcessInstanceId());
        wrapper.setProcessDefinitionId(task.getProcessDefinitionId());
        wrapper.setReachedStateDate(getCalendar(task.getReachedStateDate()));
        wrapper.setRootContainerId(task.getRootContainerId());
        wrapper.setState(task.getState());
        wrapper.setStateCategory(task.getStateCategory() != null ? task.getStateCategory().name() : null);
        wrapper.setType(task.getType() != null ? task.getType().name() : null);

        try{
            User actor =  identityAPI.getUser(task.getActorId());
            wrapper.setActor(UserWrapperFactory.createUserWrapper(actor));
        }catch (Exception e){
            log.error("No user for task " + e.getMessage(), e);
        }

        try{
            User assignee =  identityAPI.getUser(task.getAssigneeId());
            wrapper.setAssignee(UserWrapperFactory.createUserWrapper(assignee));
        }catch (Exception e){
            log.error("No assignee for task " + e.getMessage(), e);
        }

        return wrapper;
    }


    public static TaskWrapper createTaskWrapper(MultiInstanceActivityInstance task, IdentityAPI identityAPI) throws UserNotFoundException {
        TaskWrapper wrapper = new TaskWrapper();
        wrapper.setId(task.getId());
        wrapper.setName(task.getName());
        wrapper.setDescription(task.getDescription());

        wrapper.setClaimedDate(null);
        wrapper.setExpectedEndDate(null);
        wrapper.setPriority(null);
        wrapper.setDisplayDescription(task.getDisplayDescription());
        wrapper.setDisplayName(task.getDisplayName());
        wrapper.setExecutedBy(task.getExecutedBy());
        wrapper.setFlownodeDefinitionId(task.getFlownodeDefinitionId());
        wrapper.setLastUpdateDate(getCalendar(task.getLastUpdateDate()));
        wrapper.setParentContainerId(task.getParentContainerId());
        wrapper.setParentProcessInstanceId(task.getParentProcessInstanceId());
        wrapper.setProcessDefinitionId(task.getProcessDefinitionId());
        wrapper.setReachedStateDate(getCalendar(task.getReachedStateDate()));
        wrapper.setRootContainerId(task.getRootContainerId());
        wrapper.setState(task.getState());
        wrapper.setStateCategory(task.getStateCategory() != null ? task.getStateCategory().name() : null);
        wrapper.setType(task.getType() != null ? task.getType().name() : null);



        return wrapper;
    }


    private static Calendar getCalendar(Date date){
        if(date != null){
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTime(date);

            return calendar;
        }
        return null;
    }
}
