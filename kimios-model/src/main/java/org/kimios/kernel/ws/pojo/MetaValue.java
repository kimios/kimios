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
package org.kimios.kernel.ws.pojo;

/**
 *
 *
 */
public class MetaValue
{
    private long documentVersionId;

    private long metaId;

    private Object value;

    private Meta meta;

    public long getDocumentVersionId()
    {
        return documentVersionId;
    }

    public void setDocumentVersionId(long documentVersionId)
    {
        this.documentVersionId = documentVersionId;
    }

    public long getMetaId()
    {
        return metaId;
    }

    public void setMetaId(long metaId)
    {
        this.metaId = metaId;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    public Meta getMeta()
    {
        return meta;
    }

    public void setMeta(Meta meta)
    {
        this.meta = meta;
    }
}
