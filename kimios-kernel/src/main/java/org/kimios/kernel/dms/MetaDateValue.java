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

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.kimios.kernel.exception.MetaValueTypeException;
import org.kimios.kernel.hibernate.DateUserType;

@Entity
@Table(name = "meta_date_value")
@TypeDefs({
        @TypeDef(name = "dateUserType", typeClass = DateUserType.class)
})
public class MetaDateValue extends MetaValueBean
{
    @Type(type = "dateUserType")
    @Column(name = "meta_date_value", nullable = true)
    private Date value;

    public MetaDateValue()
    {
    }

    public MetaDateValue(DocumentVersion version, long metaUid, Date value)
    {
        super(version, metaUid);
        this.value = value;
    }

    public MetaDateValue(DocumentVersion version, Meta meta, Date value)
    {
        super(version, meta);
        this.value = value;
    }

    public Date getValue()
    {
        return this.value;
    }

    public void setValue(Object value) throws MetaValueTypeException
    {

        if (value.getClass().equals(Date.class) || value.getClass().equals(Timestamp.class)) {
            this.value = (Date) value;
        } else {
            throw new MetaValueTypeException(
                    "Meta value type \"" + Date.class.getName() + "\" was expected instead of \"" +
                            value.getClass().getName() + "\"");
        }
    }

    public void setValue(Date value)
    {
        this.value = value;
    }
}

