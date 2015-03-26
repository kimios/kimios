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
package org.kimios.kernel.dms;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;

import java.util.List;

public interface MetaValueFactory
{
    public MetaValue getMetaValue(DocumentVersion documentVersion, Meta meta)
            throws ConfigException, DataSourceException;

    public List<MetaValue> getMetaValues(DocumentVersion documentVersion) throws ConfigException, DataSourceException;

    public void saveMetaValue(MetaValue metaValue) throws ConfigException, DataSourceException;

    /*
    *  Used to modify the meta value after a first set (like in event handler, or rule execution)
    */
    public void saveMetaValueOverride(MetaValue metaValue) throws ConfigException, DataSourceException;

    public void updateMetaValue(MetaValue metaValue) throws ConfigException, DataSourceException;

    public void deleteMetaValue(MetaValue metaValue) throws ConfigException, DataSourceException;

    public boolean hasValue(Meta meta) throws ConfigException, DataSourceException;

    public List<MetaValue> getMetaByValue(String value, int type) throws ConfigException, DataSourceException;

    public void cleanMetaValues() throws ConfigException, DataSourceException;

    public void cleanDocumentMetaValues(Document document) throws ConfigException, DataSourceException;
}

