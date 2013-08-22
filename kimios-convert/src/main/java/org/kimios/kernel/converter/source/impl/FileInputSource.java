package org.kimios.kernel.converter.source.impl;

import org.kimios.kernel.converter.source.InputSourceImpl;
import org.kimios.kernel.converter.exception.MethodNotImplemented;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Represents a simple physical file
 */
public class FileInputSource extends InputSourceImpl {
    private File file;

    public FileInputSource(File file) {
        this.file = file;
    }

    public FileInputSource(String path) throws FileNotFoundException {
        this.file = new File(path);
    }

    public InputStream getInputStream() throws MethodNotImplemented, FileNotFoundException {
        return new FileInputStream(file);
    }

    public String getType() throws MethodNotImplemented {
        int dot = file.getName().lastIndexOf('.');
        if (dot == -1)
            return null;
        return file.getName().substring(dot + 1).toLowerCase();
    }

    public String getName() throws MethodNotImplemented {
        return file.getName();
    }
}
