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

package org.kimios.kernel.share.jobs;

import org.kimios.kernel.jobs.JobImpl;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.share.controller.IShareController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Created by farf on 29/04/16.
 */
public class ShareCleanerJob extends JobImpl<Integer> implements Runnable {


    private static Logger logger = LoggerFactory.getLogger(ShareCleanerJob.class);

    private IShareController shareController;

    public ShareCleanerJob(IShareController shareController, Session session){
        //generate task id
        super( UUID.randomUUID().toString() );

        this.shareController = shareController;
        this.setSession(session);
    }

    public Integer execute() throws Exception {
        logger.info("Starting cleaning expired shares (and their data transfers)");
        int nbCleaned = this.shareController.disableExpiredShares(getUserSession());
        logger.info("just cleaned " + nbCleaned + " shares");
        return nbCleaned;
    }

    @Override
    public void run() {
        try {
            this.execute();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            logger.debug("Starting cleaning expired shares (and their data transfers)");
            this.shareController.disableExpiredShares(getUserSession());
        }catch (Exception ex){
            logger.error("error while cleaning shares: ", ex);
        }
    }
}
