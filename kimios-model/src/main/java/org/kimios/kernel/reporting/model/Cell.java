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
package org.kimios.kernel.reporting.model;

public class Cell
{
    private String name;

    private Object value;

    public Cell(String columnName, Object value)
    {
        name = columnName;
        this.value = value;
    }

    public String getColumnName()
    {
        return name;
    }

    public void setColumnName(String columnName)
    {
        name = columnName;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    public boolean equals(Object o)
    {
        try {
            Cell c = (Cell) o;
            return c.getColumnName().equals(this.getColumnName());
        } catch (Exception e) {
            return false;
        }
    }
}

