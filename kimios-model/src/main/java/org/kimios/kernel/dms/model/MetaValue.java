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
package org.kimios.kernel.dms.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.kimios.exceptions.ConfigException;
import org.kimios.exceptions.DataSourceException;
import org.kimios.exceptions.MetaValueTypeException;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "metaTypeName")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MetaStringValue.class, name = "string"),
        @JsonSubTypes.Type(value = MetaNumberValue.class, name = "number"),
        @JsonSubTypes.Type(value = MetaDateValue.class, name = "date"),
        @JsonSubTypes.Type(value = MetaBooleanValue.class, name = "boolean"),
        @JsonSubTypes.Type(value = MetaListValue.class, name = "list")
})
public interface MetaValue<T> extends Serializable
{
    public org.kimios.kernel.dms.model.DocumentVersion getDocumentVersion();

    public void setDocumentVersion(org.kimios.kernel.dms.model.DocumentVersion documentVersion);

    public org.kimios.kernel.dms.model.Meta getMeta() throws ConfigException, DataSourceException;

    public void setMeta(org.kimios.kernel.dms.model.Meta meta);

    public long getDocumentVersionUid();

    public void setDocumentVersionUid(long documentVersionUid);

    public long getMetaUid();

    public void setMetaUid(long metaUid);

    public T getValue();

    public void setValue(T value) throws MetaValueTypeException;
}

