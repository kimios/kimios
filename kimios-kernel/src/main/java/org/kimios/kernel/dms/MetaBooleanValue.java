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
package org.kimios.kernel.dms;

import org.kimios.kernel.exception.MetaValueTypeException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "meta_boolean_value")
public class MetaBooleanValue extends MetaValueBean
{
    @Column(name = "meta_boolean_value", nullable = true)
    private boolean value;

    public MetaBooleanValue()
    {
    }

    public MetaBooleanValue(DocumentVersion version, long metaUid, boolean value)
    {
        super(version, metaUid);
        this.value = value;
    }

    public MetaBooleanValue(DocumentVersion version, Meta meta, boolean value)
    {
        super(version, meta);
        this.value = value;
    }

    public Boolean getValue()
    {
        return this.value;
    }

    public void setValue(Object value) throws MetaValueTypeException
    {
        if (value.getClass().equals(Boolean.class)) {
            this.value = (Boolean) value;
        } else {
            throw new MetaValueTypeException(
                    "Meta value type \"" + Boolean.class.getName() + "\" was expected instead of \"" +
                            value.getClass().getName() + "\"");
        }
    }

    public void setValue(boolean value)
    {
        this.value = value;
    }
}

