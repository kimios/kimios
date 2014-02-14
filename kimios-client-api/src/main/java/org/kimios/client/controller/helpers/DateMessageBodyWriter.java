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

package org.kimios.client.controller.helpers;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.Provider;
import java.util.Date;

/**
 */
@Provider
public class DateMessageBodyWriter
    implements ParamConverter<Date>
{


    public Date fromString( String value )
        throws IllegalArgumentException
    {
        System.out.println( " from string > " + value );
        long timeMillis = -1;
        timeMillis = Long.parseLong( value );
        return new Date( timeMillis );
    }

    public String toString( Date date )
        throws IllegalArgumentException
    {
        System.out.println( "Calling Write" );
        long timeMillis = -1;
        if ( date != null )
        {
            timeMillis = date.getTime();
            System.out.println( " > Writing: " + timeMillis );
            return Long.toString( timeMillis );
        }
        return null;

    }
}
