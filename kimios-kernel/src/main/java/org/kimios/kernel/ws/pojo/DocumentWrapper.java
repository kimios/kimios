/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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

package org.kimios.kernel.ws.pojo;

import javax.activation.MimetypesFileTypeMap;
import java.io.IOException;

public class DocumentWrapper {
    private String storagePath;
    private String filename;
    private Long length;
    private MimetypesFileTypeMap mft;

    public DocumentWrapper(String storagePath, String filename, Long length) throws IOException {
        this.storagePath = storagePath;
        this.filename = filename;
        this.length = length;
        this.mft = new MimetypesFileTypeMap(this.getClass().getClassLoader().getResourceAsStream("META-INF/mime.types"));
    }

    public String getContentType() {
        return mft.getContentType(filename);
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }
}
