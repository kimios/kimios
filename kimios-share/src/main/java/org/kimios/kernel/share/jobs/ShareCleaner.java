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

import org.kimios.kernel.share.factory.ShareFactory;
import org.kimios.kernel.share.model.Share;
import org.kimios.kernel.share.model.ShareStatus;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by farf on 29/04/16.
 */
public class ShareCleaner {


    private static Logger logger = LoggerFactory.getLogger(ShareCleaner.class);

    private ShareFactory shareFactory;

    public ShareCleaner(ShareFactory shareFactory){
        this.shareFactory = shareFactory;
    }

    @Transactional
    public void clean() throws JobExecutionException {
        try {
                //load expired token
                List<Share> shares = shareFactory.listExpiredShares();
                for(Share s: shares) {
                    s.setShareStatus(ShareStatus.EXPIRED);
                    shareFactory.saveShare(s);
                    if(logger.isDebugEnabled()){
                        logger.debug("expiring share {}", s);
                    }
                }
        }
        catch (Exception ex){
            logger.error("error while cleaning shares");
        }
    }
}
