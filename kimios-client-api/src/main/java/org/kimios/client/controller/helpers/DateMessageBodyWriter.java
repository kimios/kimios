package org.kimios.client.controller.helpers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
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
