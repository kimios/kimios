package org.kimios.webservices.impl.factory;

import org.bonitasoft.engine.bpm.process.ProcessInstance;
import org.kimios.webservices.pojo.ProcessInstanceWrapper;

public class ProcessInstanceWrapperFactory {

    public static ProcessInstanceWrapper createProcessInstanceWrapper(ProcessInstance pInstance) {
        ProcessInstanceWrapper processInstanceWrapper = new ProcessInstanceWrapper();
        processInstanceWrapper.setId(pInstance.getId());
        processInstanceWrapper.setName(pInstance.getName());
        processInstanceWrapper.setCalledId(pInstance.getCallerId());
        processInstanceWrapper.setDescription(pInstance.getDescription());
        processInstanceWrapper.setEndDate(pInstance.getEndDate());
        processInstanceWrapper.setLastUpdate(pInstance.getLastUpdate());
        processInstanceWrapper.setProcessDefinitionId(pInstance.getProcessDefinitionId());
        processInstanceWrapper.setRootProcessInstanceId(pInstance.getRootProcessInstanceId());
        processInstanceWrapper.setStartDate(pInstance.getStartDate());
        processInstanceWrapper.setStartBy(pInstance.getStartedBy());
        processInstanceWrapper.setState(pInstance.getState());
        processInstanceWrapper.setStringIndex1(pInstance.getStringIndex1());
        processInstanceWrapper.setStringIndex2(pInstance.getStringIndex2());
        processInstanceWrapper.setStringIndex3(pInstance.getStringIndex3());
        processInstanceWrapper.setStringIndex4(pInstance.getStringIndex4());
        processInstanceWrapper.setStringIndex5(pInstance.getStringIndex5());
        return processInstanceWrapper;
    }

}
