/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2018  DevLib'
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
package org.kimios.deployer.core;


// Referenced classes of package org.kimios.installer.exception:
//            DataBaseException

public class DataSourceException extends Exception
{

    public DataSourceException(String message)
    {
        this.message = message;
    }

    public DataSourceException()
    {
        message = "";
    }

    public DataSourceException(DataBaseException dbe)
    {
        message = (new StringBuilder()).append(dbe.getCommand()).append("\n").append(dbe.getInitialMessage()).toString();
    }

    public String getMessage()
    {
        return message;
    }

    public String toString()
    {
        return (new StringBuilder()).append("A problem with the data source has occured :\n").append(message).toString();
    }

    private String message;
}

