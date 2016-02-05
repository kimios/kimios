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

package org.kimios.osgi.karaf;

import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.commands.Option;
import org.kimios.kernel.controller.IAdministrationController;
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.jobs.IJobController;
import org.kimios.kernel.jobs.Job;

import java.util.List;

/**
 */
@Command(
        scope = "kimios",
        name = "list-jobs",
        description = "List Running Jobs")
public class JobListCommand extends KimiosCommand



{

    @Option(name = "-i",
            aliases = "--info",
            description = "Display detailed information about each running job",
            required = false, multiValued = false)
    boolean info = false;



    @Override protected void doExecuteKimiosCommand() throws Exception
    {
        if (this.isConnected()) {


            Class<IJobController> cJob = IJobController.class;
            IJobController jobController = getService(cJob);


            List jobs = jobController.listRunningTasks(this.getCurrentSession());

            String jobTpl = "id: %s, className: %s, user: %s, status: %s";

            if (jobs.size() > 0) {
                for (Object o : jobs) {
                    Job j = (Job)o;
                    String statusStr = "PROCESSING";
                    switch (j.getStatus()){
                        case 1: statusStr = "PROCESSING"; break;
                        case 0: statusStr = "FINISHED"; break;
                        case 2: statusStr = "STOPPED_IN_ERROR"; break;
                        default:
                    }
                    System.out.println(String.format(jobTpl, j.getTaskId(), j.getClass().getSimpleName(),
                            j.getUser(),
                            statusStr));

                    if(info){
                        System.out.println("\n\rInformations:\n\r " + j.getInformation());
                    }
                }
            } else {
                System.out.println("No running jobs");
            }
        }
    }
}
