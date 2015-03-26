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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kimios.client.controller.helpers.rules;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kimios.kernel.ws.pojo.Group;
import org.kimios.kernel.ws.pojo.User;

/**
 * @author Fabien Alin
 */
public class UserConverter
    implements Converter
{

    public boolean canConvert( Class arg0 )
    {
        return ( User.class == arg0 || Group.class == arg0 );
    }

    public void marshal( Object arg0, HierarchicalStreamWriter arg1, MarshallingContext arg2 )
    {

    }

    public Object unmarshal( HierarchicalStreamReader arg0, UnmarshallingContext arg1 )
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }
}

