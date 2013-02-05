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

package org.kimios.core;


import flexjson.ObjectBinder;
import flexjson.JSONException;
import flexjson.ObjectFactory;
import flexjson.transformer.AbstractTransformer;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.lang.reflect.Type;

/**
 * Created by IntelliJ IDEA.
 * User: farf
 * Date: 4/30/12
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class DateTranformerExt  extends AbstractTransformer implements ObjectFactory {

/**
 * User: brandongoodin
 * Date: Dec 12, 2007
 * Time: 11:20:39 PM
 */
    SimpleDateFormat simpleDateFormatter;

    public DateTranformerExt(String dateFormat) {
        simpleDateFormatter = new SimpleDateFormat(dateFormat);
    }


    public void transform(Object value) {
        getContext().writeQuoted(value != null ? simpleDateFormatter.format(value) : "");
    }

    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) {
        try {
            return simpleDateFormatter.parse( value.toString() );
        } catch (ParseException e) {
            throw new JSONException(String.format( "Failed to parse %s with %s pattern.", value, simpleDateFormatter.toPattern() ), e );
        }
    }
}