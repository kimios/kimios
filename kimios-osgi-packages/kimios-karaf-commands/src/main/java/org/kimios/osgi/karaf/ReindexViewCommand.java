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

import org.apache.karaf.shell.commands.Command;
import org.kimios.kernel.index.ReindexerProcess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;


/**
 */
@Command(
        scope = "kimios",
        name = "viewreindex",
        description = "View reindex process running")
public class ReindexViewCommand extends KimiosCommand {


    @Override
    protected void doExecuteKimiosCommand() throws Exception {
        if (this.isConnected()) {



            List<ReindexerProcess.ReindexResult> reindexResults =
                    searchManagementController.viewIndexingProcess(this.getCurrentSession());

            for (ReindexerProcess.ReindexResult item :reindexResults) {
                log.info("Indexing "
                        + item.getPath()
                        + " : " + item.getReindexProgression() + " %. "
                        + ". Indexed "
                        + item.getReindexedCount() + " on "
                        + item.getEntitiesCount());

                System.out.println("Indexing "
                        + item.getPath()
                        + " : " + item.getReindexProgression() + " %. "
                        + ". Indexed "
                        + item.getReindexedCount() + " on "
                        + item.getEntitiesCount());
            }
        }


    }
}
