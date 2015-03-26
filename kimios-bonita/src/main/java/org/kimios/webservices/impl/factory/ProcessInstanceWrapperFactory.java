/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2015  DevLib'
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
