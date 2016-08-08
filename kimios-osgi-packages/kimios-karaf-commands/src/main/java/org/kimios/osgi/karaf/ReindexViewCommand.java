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

package org.kimios.osgi.karaf;

import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.kimios.kernel.index.ReindexerProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;


/**
 */
@Service
@Command(
        scope = "kimios",
        name = "viewreindex",
        description = "View reindex process running")
public class ReindexViewCommand extends KimiosCommand {


    private static Logger log = LoggerFactory.getLogger(ReindexViewCommand.class);

    @Override
    protected void doExecuteKimiosCommand() throws Exception {
        if (this.isConnected()) {



            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DecimalFormat df = new DecimalFormat("#0.##");


            List<ReindexerProcess.ReindexResult> reindexResults =
                    searchManagementController.viewIndexingProcess(this.getCurrentSession());

            for (ReindexerProcess.ReindexResult item :reindexResults) {
                Calendar calendarStartDate = Calendar.getInstance();
                calendarStartDate.setTimeInMillis(item.getStart());

                double indexingRate = (double)item.getReindexedCount() / ((double)item.getDuration() / 1000 / 60);
                String formattedRate = df.format(indexingRate);
                double remainingTime = ((item.getEntitiesCount() - item.getReindexedCount()) / indexingRate);
                String formattedRemaining = df.format(remainingTime);
                String message = "Indexing "
                        + item.getPath()
                        + " : " + item.getReindexProgression() + " %. "
                        + ". Indexed "
                        + item.getReindexedCount() + " on "
                        + item.getEntitiesCount()
                        + ". Started on " + sdf.format(calendarStartDate.getTime())
                        + ". Elapsed Time: " + (item.getDuration() / 1000 / 60) + " minutes"
                        + ". Indexing rate: " + formattedRate + " per minutes"
                        + ". Average Remaining Duration "
                        +  formattedRemaining + " minutes.";

                log.info(message);

                System.out.println(message);
            }
        }


    }
}
