/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2015  DevLib'
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

package org.kimios.kernel.converter.source.impl;

import org.kimios.kernel.converter.exception.MethodNotImplemented;
import org.kimios.kernel.converter.source.InputSourceImpl;
import org.kimios.kernel.dms.DocumentVersion;
import org.kimios.kernel.repositories.RepositoryManager;

import java.io.IOException;
import java.io.InputStream;

public class DocumentVersionInputSource extends InputSourceImpl {
    private DocumentVersion version;

    public DocumentVersionInputSource(org.kimios.kernel.dms.DocumentVersion version) {
        this.version = version;
    }

    public InputStream getInputStream() throws MethodNotImplemented, IOException {
        return RepositoryManager.accessVersionStream(version);
    }

    public String getType() throws MethodNotImplemented {
        return version.getDocument().getExtension() != null ?
            version.getDocument().getExtension().toLowerCase() : null;
    }

    public String getName() throws MethodNotImplemented {
        return version.getDocument().getName();
    }

    public String getStoragePath() {
        return version.getStoragePath();
    }

    public DocumentVersion getVersion() {
        return version;
    }


}
