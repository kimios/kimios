package org.kimios.kernel.converter;

import org.kimios.kernel.converter.exception.ConverterException;
import org.kimios.kernel.converter.source.InputSource;

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

}
