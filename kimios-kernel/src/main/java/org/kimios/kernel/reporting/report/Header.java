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
package org.kimios.kernel.reporting.report;

import java.util.ArrayList;
import java.util.List;

public class Header
{
    private List<Column> columns;

    public Header()
    {
        this.columns = new ArrayList<Column>();
    }

    public Header(List<Column> columns)
    {
        this.columns = columns;
    }

    public List<Column> getColumns()
    {
        return this.columns;
    }

    public void setColumns(List<Column> columns)
    {
        this.columns = columns;
    }

    public void addColumn(Column column)
    {
        this.columns.add(column);
    }

    public void removeColumn(Column column)
    {
        this.columns.remove(column);
    }

    public void clearColumns()
    {
        this.columns.clear();
    }

    public int size()
    {
        return this.columns.size();
    }
}

