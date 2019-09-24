package org.kimios.jod.controller;

import org.kimios.exceptions.ConverterException;

import java.io.File;

public interface IJodConverterController {
    File convertFile(File file, String fileResultName) throws ConverterException;
}
