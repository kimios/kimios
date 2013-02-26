package org.kimios.kernel.converter.exception;

import org.kimios.kernel.converter.Converter;

public class BadInputSource extends ConverterException {
    public BadInputSource(Converter converter) {
        super("Bad InputSource for given Converter: " + converter.getClass().getName());
    }
}
