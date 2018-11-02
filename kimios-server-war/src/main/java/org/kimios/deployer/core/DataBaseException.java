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
//            DataSourceException

public class DataBaseException extends DataSourceException
{

    public DataBaseException(String command, String initialMessage)
    {
        this.command = command;
        this.initialMessage = initialMessage;
    }

    public String getCommand()
    {
        return command;
    }

    public String getMessage()
    {
        return getInitialMessage();
    }

    public String getInitialMessage()
    {
        return initialMessage;
    }

    public String toString()
    {
        return (new StringBuilder()).append("A data base error has occured with the following command :\n").append(command).append("\nInitial message was : ").append(initialMessage).toString();
    }

    private String command;
    private String initialMessage;
}

