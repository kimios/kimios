/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2017  DevLib'
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

package org.kimios.aspose.converters.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;

/**
 * Created by farf on 26/05/16.
 */
public class LicenceLoader {

    private static Logger logger = LoggerFactory.getLogger(LicenceLoader.class);

    public static void loadMailLicence(String licenceFileName){
        try {
            com.aspose.email.License license = new com.aspose.email.License();
            license.setLicense(new FileInputStream(licenceFileName));
            logger.info("licence Aspose Mail {}", license.isLicensed());
        }catch (Exception ex){
            logger.warn("unable to load Aspose licence", ex);
        }
    }

    public static void loadWordLicence(String licenceFileName){
        try {
            com.aspose.words.License license = new com.aspose.words.License();
            license.setLicense(new FileInputStream(licenceFileName));
            logger.info("licence Aspose Word {}", license.getIsLicensed());
        }catch (Exception ex){
            logger.warn("unable to load Aspose licence", ex);
        }
    }

    public static void loadCellsLicence(String licenceFileName){
        try {
            com.aspose.cells.License license = new com.aspose.cells.License();
            license.setLicense(new FileInputStream(licenceFileName));
            logger.info("licence Aspose Cells {}", license);
        }catch (Exception ex){
            logger.warn("unable to load Aspose licence", ex);
        }
    }

    public static void loadSlidesLicence(String licenceFileName){
        try {
            com.aspose.slides.License license = new com.aspose.slides.License();
            license.setLicense(new FileInputStream(licenceFileName));
            logger.info("licence Aspose Slides {}", license.isLicensed());
        }catch (Exception ex){
            logger.warn("unable to load Aspose licence", ex);
        }
    }

}
