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
package org.kimios.kernel.exception;

@SuppressWarnings("serial")
public class RepositoryException extends DmsKernelException
{
    private String message;


    public RepositoryException(Exception e){
        super(e);
        this.message = "Kimios repository Exception: " + e.getMessage();
    }

    public RepositoryException(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return this.message;
    }

    public String toString()
    {
        return "Kimios Repository Error has occured : " + this.message;
    }
}

