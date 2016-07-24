/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
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

package org.kimios.kernel.rules.impl;

import org.apache.commons.lang.StringUtils;
import org.kimios.kernel.controller.IWorkflowController;
import org.kimios.kernel.dms.FactoryInstantiator;
import org.kimios.kernel.dms.model.*;
import org.kimios.kernel.security.model.DMEntitySecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author farf
 * @version 1.1
 */
public class AutomaticWorkflowStartRule extends RuleImpl {


    private static Logger logger = LoggerFactory.getLogger(AutomaticWorkflowStartRule.class);

    @Override
    public boolean isTrue() {
        return this.getContext().getEntity() instanceof Document &&
                FactoryInstantiator.getInstance().getDocumentWorkflowStatusRequestFactory()
                        .getLastPendingRequest((Document)this.getContext().getEntity()) == null;
    }

    @Override
    public void execute() throws Exception {
        logger.debug("starting rule {} for {}", this.getClass().getName(), this.getContext().getEntity());
        //get workflow reference
        String workflowNameOrId = parameters.get("workflowName");
        String workflowStatusNameOrId = parameters.get("workflowStatus");
        logger.debug("loading workflow {}, with status {}", workflowNameOrId, workflowStatusNameOrId);
        Workflow workflow = null;
        try{

            workflow = FactoryInstantiator.getInstance().getWorkflowFactory()
                    .getWorkflow(workflowNameOrId);
        } catch (Exception ex){
            logger.warn("workflow not found for name {}", workflowNameOrId);
            return;
        }

        if(workflow != null){
            //start workflow
            WorkflowStatus workflowStatus = null;
            if(workflowStatusNameOrId != null){
                logger.debug("loading workflow status {}", workflowStatusNameOrId);
                List<WorkflowStatus> items = FactoryInstantiator.getInstance().getWorkflowStatusFactory()
                        .getWorkflowStatuses(workflow);
                for(WorkflowStatus status: items){
                    if(status.getName().equals(workflowStatusNameOrId)){
                        workflowStatus = status;
                        break;
                    }
                }
            }
            if(workflowStatus == null){
                workflowStatus = FactoryInstantiator.getInstance().getWorkflowStatusFactory()
                        .getStartWorkflowStatus(workflow);
            }
            if(logger.isDebugEnabled()){
                logger.debug("starting workflow {} on status {}", workflow, workflowStatus);
            }
            IWorkflowController workflowController = contextHolder.getService(IWorkflowController.class);
            workflowController.createWorkflowRequest(this.getContext().getSession(),
                    this.getContext().getEntity().getUid(),
                    workflowStatus.getUid());
            if(logger.isDebugEnabled()){
                logger.debug("started workflow status for doc {}", ctx.getEntity());
            }
        }
    }
}
