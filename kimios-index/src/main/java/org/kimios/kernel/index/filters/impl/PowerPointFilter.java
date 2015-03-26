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
package org.kimios.kernel.index.filters.impl;

import org.apache.poi.hslf.extractor.PowerPointExtractor;
import org.kimios.kernel.index.IndexFilter;

import java.io.IOException;
import java.io.InputStream;

public class PowerPointFilter implements IndexFilter
{
    public String getBody(InputStream in) throws IOException
    {
        PowerPointExtractor extractor = new PowerPointExtractor(in);
        return new String(extractor.getText().getBytes("UTF-8"), "UTF-8");
    }
}

