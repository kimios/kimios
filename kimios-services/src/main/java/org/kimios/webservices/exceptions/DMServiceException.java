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
package org.kimios.webservices.exceptions;

import javax.xml.ws.WebFault;

@WebFault(name = "DMServiceException", targetNamespace = "http://webservices.kimios.org")
public class DMServiceException extends Exception
{
    public DMServiceException()
    {
        super();
    }

    public DMServiceException(String message)
    {
        super(message);
        code = 0;
    }

    public DMServiceException(String message, Throwable cause)
    {
        super(message, cause);
        code = 0;
    }

    public DMServiceException(int code, String message, Throwable cause)
    {
        super(message, cause);
        this.code = code;
    }

    private int code = 0;

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }
}
