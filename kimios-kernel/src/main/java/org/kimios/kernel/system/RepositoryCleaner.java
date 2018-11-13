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
package org.kimios.kernel.system;

import org.kimios.kernel.controller.IDocumentVersionController;
import org.kimios.kernel.jobs.RepositoryCleanerJob;
import org.kimios.utils.system.CustomScheduledThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class RepositoryCleaner {

    private static Logger log = LoggerFactory.getLogger(RepositoryCleaner.class);

    private IDocumentVersionController versionController;
    private CustomScheduledThreadPoolExecutor customScheduledThreadPoolExecutor;

    public IDocumentVersionController getVersionController() {
        return versionController;
    }

    public void setVersionController(IDocumentVersionController versionController) {
        this.versionController = versionController;
    }

    public void startJob() {
        log.info("Kimios Repository Cleaner - Starting…");

        this.customScheduledThreadPoolExecutor = new CustomScheduledThreadPoolExecutor(1);
        RepositoryCleanerJob job = new RepositoryCleanerJob(versionController);
        this.customScheduledThreadPoolExecutor.scheduleAtFixedRate(job, 0, 5, TimeUnit.SECONDS);

        log.info("Kimios Repository Cleaner - Started");
    }

    public void stopJob() {
        log.info("Kimios Repository Cleaner - closing ...");
        try {
            if(this.customScheduledThreadPoolExecutor != null){
                this.customScheduledThreadPoolExecutor.shutdownNow();
                this.customScheduledThreadPoolExecutor.awaitTermination(5, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.info("Kimios Repository Cleaner - closed");
    }
}

