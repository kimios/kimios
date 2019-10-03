package org.kimios.api.controller;

import org.kimios.exceptions.ConverterException;

import java.io.File;

public interface IFileConverterController extends Comparable<IFileConverterController> {
    File convertFile(File file, String fileResultName) throws ConverterException;

    int getScore();
}
