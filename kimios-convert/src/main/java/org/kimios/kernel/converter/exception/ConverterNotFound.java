package org.kimios.kernel.converter.exception;

public class ConverterNotFound extends ConverterException {
    public ConverterNotFound(String converterName) {
        super("Converter not found: " + converterName);
    }

    public ConverterNotFound(Exception e) {
        this(e.getMessage());
    }
}
