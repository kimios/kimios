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

import org.kimios.kernel.exception.MetaValueTypeException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "meta_number_value")
public class MetaNumberValue extends MetaValueBean<Double>
{
    @Column(name = "meta_number_value", nullable = false)
    private double value;

    public MetaNumberValue()
    {
    }

    public MetaNumberValue(DocumentVersion version, long metaUid, double value)
    {
        super(version, metaUid);
        this.value = value;
    }

    public MetaNumberValue(DocumentVersion version, Meta meta, double value)
    {
        super(version, meta);
        this.value = value;
    }

    public Double getValue()
    {
        return this.value;
    }

    public void setValue(Double value) throws MetaValueTypeException
    {
        if (value != null && value.getClass().equals(Double.class)) {
            this.value = (Double) value;
        } else {
            throw new MetaValueTypeException(
                    "Meta value type \"" + Double.class.getName() + "\" was expected instead of \"" +
                            value.getClass().getName() + "\"");
        }
    }
}

