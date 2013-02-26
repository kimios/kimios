package org.kimios.kernel.converter.exception;

import org.kimios.kernel.exception.DmsKernelException;

public class ConverterException extends DmsKernelException {
    public ConverterException(String s) {
        super(s);
    }

    public ConverterException(Exception e) {
        super(e);
    }
}
