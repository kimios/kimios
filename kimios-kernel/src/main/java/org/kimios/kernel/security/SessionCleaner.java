/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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
package org.kimios.kernel.security;

import org.kimios.kernel.configuration.Config;
import org.kimios.utils.configuration.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionCleaner implements Runnable
{
    private static Logger log = LoggerFactory.getLogger(SessionCleaner.class);

    private static Thread thSc;

    private boolean stop = false;

    private ISessionManager sessionManager;

    public ISessionManager getSessionManager()
    {
        return sessionManager;
    }

    public void setSessionManager(ISessionManager sessionManager)
    {
        this.sessionManager = sessionManager;
    }

    public synchronized void stop()
    {
        this.stop = true;
    }

    public void run()
    {
        try {
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
            }

            log.info("[SESSIONCLEANNER] - Starting ...");
            try {
                sessionManager.initSessionContext();
            } catch (Exception de) {
                log.error("[SESSIONCLEANNER] - STARTING FAILURE ...", de);
                throw de;
            }
            log.info("[SESSIONCLEANNER] - Starded !");

            long sessionExpire = Long.parseLong(ConfigurationManager.getValue(Config.DEFAULT_SESSION_TIMEOUT)) * 60000;

            while (!this.stop) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {

                }
                try {
                    sessionManager.cleanSessionContext(sessionExpire);
                } catch (Exception e) {
                    log.info("Cleaning relaunched ...");
                }
            }

            log.info("[SESSIONCLEANNER] - Closing ...");
            try {
                sessionManager.closeSessionContext();
            } catch (Exception de) {
                log.error("[SESSIONCLEANNER] - CLOSING FAILURE ...", de);
                throw de;
            }
            log.info("[SESSIONCLEANNER] - Closed !");
        } catch (Exception e) {
            log.error("Session Cleaner failure ...", e);
        }
    }

    public void startJob()
    {
        synchronized (this) {
            thSc = new Thread(this, "Kimios Session Cleaner");
            thSc.start();
        }
    }

    public void stopJob()
    {
        log.info("Kimios Session Cleaner - Closing ...");
        this.stop();
        try {
            if (thSc != null) {
                thSc.join();
            }
        } catch (Exception e) {

        }
        log.info("Kimios Session Cleaner - Closed");
    }
}

