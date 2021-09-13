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

import org.apache.commons.lang.StringUtils;
import org.kimios.api.Converter;
import org.kimios.converter.impl.BarcodeTransformer;
import org.kimios.converter.impl.DocToHTML;
import org.kimios.exceptions.ConverterNotFound;
import org.kimios.converter.impl.FileToZip;
import org.kimios.converter.impl.PDFMerger;
import org.kimios.utils.extension.IExtensionRegistryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class ConverterFactory {

    private static Logger log = LoggerFactory.getLogger(ConverterFactory.class);

    private IExtensionRegistryManager extensionRegistryManager;

    public ConverterFactory(IExtensionRegistryManager extensionRegistryManager){
        this.extensionRegistryManager = extensionRegistryManager;
    }

    public Converter getConverter(String className, String outputFormat) throws ConverterNotFound {
        try {
            log.debug("calling ConverterFactory for {} {}", className, outputFormat);


            Collection<Class<? extends ConverterImpl>> impls = extensionRegistryManager.itemsAsClass(ConverterImpl.class);

            log.debug("converter items: " + impls);

            for(Class<? extends ConverterImpl> c: impls){
                if(c.getName().equals(className)){
                    if (StringUtils.isBlank(outputFormat)) {
                        return c.newInstance();
                    } else {
                        return (Converter)c.getDeclaredConstructor(new Class[]{String.class}).newInstance(outputFormat);
                    }
                }
            }
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

            throw new ConverterNotFound("Converter Not Found: " + className);


        } catch (InvocationTargetException e) {
            throw new ConverterNotFound(e);
        } catch (NoSuchMethodException e) {
            log.warn("converter {} doesn't implement other types, returning default", className);
            return this.getConverter(className, null);
        }  catch (InstantiationException e) {
            throw new ConverterNotFound(e);

        } catch (IllegalAccessException e) {
            throw new ConverterNotFound(e);
        }
    }
}
