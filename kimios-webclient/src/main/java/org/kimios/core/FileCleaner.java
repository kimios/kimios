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
package org.kimios.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 *
 * File Cleaner Utility Class
 *
 * @author jerome
 */
public class FileCleaner {

    private final Logger log = LoggerFactory.getLogger(FileCleaner.class);

    /**
     * Remove all files for a given temporary files path.
     * 
     * @param tmpFilesPath The temporary files path
     */
    public void cleanTemporaryFiles(File tmpFilesPath) {
        log.info("Cleaning " + tmpFilesPath.getPath() + " ... ");
        if(!tmpFilesPath.exists()){
            tmpFilesPath.mkdirs();
            return;
        }
        File[] files = tmpFilesPath.listFiles();

        if (files == null || files.length == 0) {
            log.info("Nothing to clean.");
        } else {
            for (File f : files) {
                log.info("[" + (f.delete() == true ? "x" : " ") + "] " + f.getPath());
            }
            log.info("Cleaning done.");
        }
    }
}
