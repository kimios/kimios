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
