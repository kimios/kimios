package org.kimios.webservices.impl.factory;

import org.bonitasoft.engine.bpm.process.ProcessDeploymentInfo;
import org.kimios.webservices.pojo.ProcessWrapper;

public class ProcessWrapperFactory {

    public static ProcessWrapper createProcessWrapper(ProcessDeploymentInfo process) {
        ProcessWrapper wrapper = new ProcessWrapper();
        wrapper.setId(process.getId());
        wrapper.setProcessId(process.getProcessId());
        wrapper.setName(process.getName());
        wrapper.setDescription(process.getDescription());
        wrapper.setActivationState(process.getActivationState() != null ? process.getActivationState().name() : null);
        wrapper.setConfigurationState(process.getConfigurationState() != null ? process.getConfigurationState().name() : null);
        wrapper.setDeployedBy(process.getDeployedBy());
        wrapper.setDeploymentDate(process.getDeploymentDate());
        wrapper.setDisplayName(process.getDisplayName());
        wrapper.setDisplayDescription(process.getDisplayDescription());
        wrapper.setIconPath(process.getIconPath());
        wrapper.setLastUpdateDate(process.getLastUpdateDate());
        wrapper.setVersion(process.getVersion());
        return wrapper;
    }

}
