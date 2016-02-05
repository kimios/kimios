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

import java.util.Vector;

public class Row
{
    private Vector<Cell> cells;

    public Row(Vector<Cell> cells)
    {
        this.cells = cells;
    }

    public Row()
    {
        this.cells = new Vector<Cell>();
    }

    public Vector<Cell> getCells()
    {
        return this.cells;
    }

    public void addCell(Cell cell)
    {
        boolean insert = true;
        for (Cell c : this.cells) {
            if (c.getColumnName().equals(cell.getColumnName())) {
                insert = false;
            }
        }
        if (insert) {
            this.cells.add(cell);
        }
    }

    public void removeCell(Cell cell)
    {
        for (Cell c : this.cells) {
            if (c.getColumnName().equals(cell.getColumnName())) {
                this.cells.remove(cell);
            }
        }
    }

    public Object getValue(String columnName)
    {
        for (Cell c : cells) {
            if (c.getColumnName().equals(columnName)) {
                return c.getValue();
            }
        }
        return null;
    }

    public void setValue(String columnName, Object value)
    {
        for (Cell c : cells) {
            if (c.getColumnName().equals(columnName)) {
                c.setValue(value);
            }
        }
    }

    public int size()
    {
        return this.cells.size();
    }
}

