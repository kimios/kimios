/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2017  DevLib'
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
package org.kimios.api.reporting;

import org.kimios.exceptions.ConfigException;
import org.kimios.exceptions.DataSourceException;

public abstract class ReportImpl
{
    protected String name;

    protected String sessionUid;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSessionUid()
    {
        return sessionUid;
    }

    public void setSessionUid(String sessionUid)
    {
        this.sessionUid = sessionUid;
    }

    public abstract String getData() throws ConfigException,
            DataSourceException;
}

