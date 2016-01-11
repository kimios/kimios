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

package org.kimios.converter;

import org.kimios.converter.impl.BarcodeTransformer;
import org.kimios.converter.impl.DocToHTML;
import org.kimios.converter.exception.ConverterNotFound;
import org.kimios.converter.impl.FileToZip;
import org.kimios.converter.impl.PDFMerger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConverterFactory {

    private static Logger log = LoggerFactory.getLogger(ConverterFactory.class);

    public static Converter getConverter(String className) throws ConverterNotFound {
        try {

            log.debug("Calling ConverterFactory...");
            return (Converter) Class.forName(className).newInstance();

        } catch (ClassNotFoundException e) {

            /*
            Check if given converter corresponds to file extension
             */
            log.warn("Converter implementation not found, trying to get from extension...");

            if (className.equals("zip")) {
                return new FileToZip();
            }

            if (className.equals("pdf")) {
                return new PDFMerger();
            }

            if (className.equals("html")) {
                return new DocToHTML();
            }

            if (className.equals("barcode")) {
                return new BarcodeTransformer();
            }

            throw new ConverterNotFound(e);

        } catch (InstantiationException e) {
            throw new ConverterNotFound(e);

        } catch (IllegalAccessException e) {
            throw new ConverterNotFound(e);
        }
    }
}
