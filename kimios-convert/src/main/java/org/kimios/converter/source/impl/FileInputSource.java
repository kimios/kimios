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

package org.kimios.converter.source.impl;

import org.kimios.exceptions.MethodNotImplemented;
import org.kimios.converter.source.InputSourceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Represents a simple physical file
 */
public class FileInputSource extends InputSourceImpl {
    private File file;

    private String token;

    public FileInputSource(File file) {
        this.file = file;
    }

    public FileInputSource(File file, String contentType) {
        this.file = file;
        this.mimeType = contentType;
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

    @Override
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
