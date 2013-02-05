/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
 *
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.kernel.dms;

import java.util.Vector;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;

public interface MetaFactory
{
    public Meta getMeta(DocumentType t, String name) throws ConfigException, DataSourceException;

    public Meta getMeta(long uid) throws ConfigException, DataSourceException;

    public Vector<Meta> getMetas(DocumentType t) throws ConfigException, DataSourceException;

    public Vector<Meta> getMetas() throws ConfigException, DataSourceException;

    public Vector<Meta> getUnheritedMetas(DocumentType t) throws ConfigException, DataSourceException;

    public void saveMeta(Meta m) throws ConfigException, DataSourceException;

    public void updateMeta(Meta m) throws ConfigException, DataSourceException;

    public void deleteMeta(Meta m) throws ConfigException, DataSourceException;
}

