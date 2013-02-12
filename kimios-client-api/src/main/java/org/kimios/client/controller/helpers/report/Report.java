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
package org.kimios.client.controller.helpers.report;

import java.util.Date;

/**
 * This class represents a report with its content
 */
public class Report
{

    private String name;

    private Date date;

    private Header header;

    private Body body;

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the date
     */
    public Date getDate()
    {
        return date;
    }

    /**
     * @return the header
     */
    public Header getHeader()
    {
        return header;
    }

    /**
     * @return the body
     */
    public Body getBody()
    {
        return body;
    }
}

