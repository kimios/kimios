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

import org.kimios.converter.exception.MethodNotImplemented;

public abstract class InputSourceImpl implements InputSource {
    /**
     * A alternative user-friendly document name
     */
    protected String humanName;

    protected String mimeType = "application/octet-stream";

    public void setHumanName(String name) {
        this.humanName = name;
    }

    public String getHumanName() {
        return humanName;
    }

    /**
     * A public url, used in cache
     */
    protected String publicUrl;

    public String getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public void setMimeType(String mimeType) throws MethodNotImplemented {
        this.mimeType = mimeType;
    }
}
