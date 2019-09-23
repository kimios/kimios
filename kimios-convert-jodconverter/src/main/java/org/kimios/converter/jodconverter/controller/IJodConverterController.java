package org.kimios.converter.jodconverter.controller;

import org.kimios.exceptions.ConverterException;

import java.io.File;

public interface IJodConverterController {
    public File convertFile(File file, String fileResultName) throws ConverterException;
}
