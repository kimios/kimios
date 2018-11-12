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

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.configuration.Config;
import org.kimios.kernel.controller.IDocumentVersionController;
import org.kimios.kernel.dms.model.DocumentVersion;
import org.kimios.exceptions.DataSourceException;
import org.kimios.utils.configuration.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class RepositoryCleaner implements Runnable
{
    private boolean stop = false;

    private static Logger log = LoggerFactory.getLogger(RepositoryCleaner.class);

    private static Thread thrc;

    private IDocumentVersionController versionController;

    public IDocumentVersionController getVersionController()
    {
        return versionController;
    }

    public void setVersionController(IDocumentVersionController versionController)
    {
        this.versionController = versionController;
    }

    public void run()
    {
        try {

            log.info("Repo Cleaner (I am " + this + ". Thread " + thrc + "(" + Thread.currentThread().getId() +" ) ");
            while (Thread.currentThread().isInterrupted()) {
                List<DocumentVersion> versions = versionController.getOprhansDocumentVersion();
                for (DocumentVersion v: versions) {
                    try {
                        if(log.isDebugEnabled()){
                            log.debug("removing version #" + v.getUid() + " -> " + v.getStoragePath());
                        }
                        new File(ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) +
                                v.getStoragePath()).delete();
                        versionController.deleteDocumentVersion(v.getUid());
                    } catch (ConfigException e) {
                        e.printStackTrace();
                    } catch (DataSourceException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(Thread.currentThread().isInterrupted())
                    break;
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    break;
                }
            }
        } catch (ConfigException ce) {
            ce.printStackTrace();
            this.stop();
        } catch (DataSourceException dbe) {
            dbe.printStackTrace();
            this.stop();
        } catch (Exception e) {
            e.printStackTrace();
            this.stop();
        }
        log.info("Kimios Repository Cleaner - ended job");
    }

    public void stop()
    {
        Thread.currentThread().interrupt();
    }

    public void startJob()
    {
        log.info("Kimios Repository Cleaner - Starting job.");
        synchronized (this) {
            thrc = new Thread(this, "Kimios Repository Cleaner");
            thrc.start();
        }
        log.info("Kimios Repository Cleaner - Started job.");
    }

    public void stopJob()
    {
        log.info("Kimios Repository Cleaner - Closing ...");
        this.stop();
    }
}

