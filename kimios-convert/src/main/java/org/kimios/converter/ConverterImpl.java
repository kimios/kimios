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

import org.kimios.api.Converter;
import org.kimios.api.InputSource;
import org.kimios.exceptions.ConverterException;
import org.kimios.exceptions.MethodNotImplemented;
import org.kimios.kernel.configuration.Config;
import org.kimios.utils.configuration.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public abstract class ConverterImpl implements Converter {

    protected static Logger log = LoggerFactory.getLogger(ConverterImpl.class);
    protected final String temporaryRepository;
    protected final String externalBaseUrl;

    protected ConverterImpl() {
        temporaryRepository = ConfigurationManager.getValue(Config.DEFAULT_TEMPORARY_PATH);
        externalBaseUrl = "/services/rest/converter/preview/p/";
        try{
            File file = new File(temporaryRepository);
            if(!file.exists())
                file.mkdirs();
        } catch (Exception e){
            log.error("Error while creating temp repository converter", e);
        }
        if(log.isDebugEnabled()){
            log.debug("Calling " + this.getClass().getName() + " converter implementation...");
        }
    }

    public InputSource convertInputSource(InputSource source) throws ConverterException {
        throw new MethodNotImplemented("convertInputSource");
    }

    public InputSource convertInputSources(List<InputSource> sources) throws ConverterException {
        throw new MethodNotImplemented("convertInputSources");
    }
}
