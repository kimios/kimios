package org.kimios.webservices;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Date;

/**
 */
@Provider
public class DateParamConverter implements ParamConverter<Date>, ParamConverterProvider {


    public Date fromString(String value) throws IllegalArgumentException {
        long timeMillis = -1;
        timeMillis = Long.parseLong(value);
        return new Date(timeMillis);
    }

    public String toString(Date date) throws IllegalArgumentException {
        long timeMillis = -1;
        if (date != null) {
            timeMillis = date.getTime();
            return Long.toString(timeMillis);
        }
        return null;

    }

    public ParamConverter getConverter(Class rawType, Type genericType, Annotation[] annotations) {
        if(rawType.equals(Date.class)){
            return this;
        } else
            return null;

    }
}
