package org.kimios.kernel.converter.exception;

public class MethodNotImplemented extends ConverterException {
    public MethodNotImplemented(String s) {
        super("Method " + s + " is not implemented for this Converter implementation");
    }
}
