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
package org.kimios.kernel.exception;

import org.hibernate.exception.ConstraintViolationException;

@SuppressWarnings("serial")
public class DataSourceException extends DmsKernelException
{
    private String message;

    private boolean integrityException = false;

    public DataSourceException(Exception e)
    {
        super(e);
        this.message = e.getMessage();
        this.setStackTrace(e.getStackTrace());

        this.integrityException = ExceptionAnalyser.isThrowableCausedBy(this, ConstraintViolationException.class);
    }

    public DataSourceException(Exception e, String message)
    {
        super(e);
        this.message = message;
        this.setStackTrace(e.getStackTrace());

        this.integrityException = ExceptionAnalyser.isThrowableCausedBy(this, ConstraintViolationException.class);
    }

    public String getMessage()
    {
        return this.message;
    }

    public String toString()
    {
        return "A problem with the data source has occured :\n" + this.message;
    }

    public boolean isIntegrityException()
    {
        return integrityException;
    }

    public void setIntegrityException(boolean integrityException)
    {
        this.integrityException = integrityException;
    }
}

