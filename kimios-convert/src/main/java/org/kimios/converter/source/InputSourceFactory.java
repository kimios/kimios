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

package org.kimios.converter.source;

import org.kimios.converter.source.impl.DocumentVersionInputSource;
import org.kimios.converter.source.impl.FileInputSource;
import org.kimios.kernel.dms.model.DocumentVersion;

import java.io.File;

public class InputSourceFactory {

    public static InputSource getInputSource(DocumentVersion version) {
        return new DocumentVersionInputSource(version);
    }

    public static InputSource getInputSource(File file) {
        return new FileInputSource(file);
    }

    public static InputSource getInputSource(String fileName) {
        return getInputSource(new File(fileName));
    }

}
