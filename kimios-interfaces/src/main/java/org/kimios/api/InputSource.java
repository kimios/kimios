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

package org.kimios.api;


import org.kimios.exceptions.MethodNotImplemented;

import java.io.IOException;
import java.io.InputStream;

public interface InputSource {

    InputStream getInputStream() throws MethodNotImplemented, IOException;

    String getType() throws MethodNotImplemented;

    String getMimeType();

    void setMimeType(String  mimeType) throws MethodNotImplemented;

    String getName() throws MethodNotImplemented;

    void setHumanName(String altName);

    String getHumanName();

    void setPublicUrl(String publicUrl);

    public String getPublicUrl();

    public String getToken();
}
