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

import org.kimios.converter.source.InputSource;
import org.kimios.converter.exception.ConverterException;

import java.util.List;

public interface Converter {

    /**
     * Get a InputSource from a given InputSource
     */
    InputSource convertInputSource(InputSource source)
            throws ConverterException;

    /**
     * Get a InputSource from a collection of InputSource
     */
    InputSource convertInputSources(List<InputSource> sources)
            throws ConverterException;

    /*
        Get Content Type OutPut
     */
    String converterTargetMimeType();

}
