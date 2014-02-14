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
package org.kimios.kernel.reporting.report;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Report
{
    private String name;

    private Date date;

    private Header header;

    private Body body;

    public Body getBody()
    {
        return body;
    }

    public void setBody(Body body)
    {
        this.body = body;
    }

    public Report()
    {
        this("unamed report");
    }

    public Report(String name)
    {
        this(name, new Date());
    }

    public Report(String name, Date date)
    {
        this.name = name;
        this.date = date;
        this.header = new Header();
        this.body = new Body();
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public void setHeader(Header header)
    {
        this.header = header;
    }

    public Header getHeader()
    {
        return this.header;
    }

    public void addColumn(String column)
    {
        this.header.addColumn(new Column(column));
    }

    public void addColumns(List<String> columns)
    {
        for (String column : columns) {
            this.header.addColumn(new Column(column));
        }
    }

    public void removeColumn(String column)
    {
        this.header.removeColumn(new Column(column));
    }

    public void clearColumns()
    {
        this.header.clearColumns();
    }

    public String getColumn(int index)
    {
        return this.header.getColumns().get(index).getName();
    }

    public List<String> getColumns()
    {
        List<String> columns = new ArrayList<String>();
        for (Column column : this.header.getColumns()) {
            columns.add(column.getName());
        }
        return columns;
    }

    public void addRow(Row row)
    {
        this.body.getRows().add(row);
    }

    public Row getRow(int i)
    {
        return this.body.getRows().get(i);
    }

    public int getRowsSize()
    {
        return this.body.getRows().size();
    }

    public void removeRow(int i)
    {
        this.body.getRows().remove(i);
    }

    public String toXML()
    {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("report", Report.class);
        xstream.alias("column", Column.class);
        xstream.alias("row", Row.class);
        xstream.alias("cell", Cell.class);
        xstream.addImplicitCollection(Header.class, "columns");
        xstream.addImplicitCollection(Body.class, "rows");
        xstream.addImplicitCollection(Row.class, "cells");
        return xstream.toXML(this);
    }
}
