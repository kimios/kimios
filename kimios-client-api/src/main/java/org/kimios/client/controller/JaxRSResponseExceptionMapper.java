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

package org.kimios.client.controller;

import org.apache.cxf.jaxrs.client.ResponseExceptionMapper;
import org.kimios.webservices.DMServiceException;

import javax.ws.rs.core.Response;

/**
 * @author Fabien ALIN <fabien.alin@gmail.com>
 */
public class JaxRSResponseExceptionMapper
    implements ResponseExceptionMapper<DMServiceException>
{


    public DMServiceException fromResponse( Response r )
    {
        try{
            DMServiceException ex = r.readEntity( DMServiceException.class );
            return ex;
        }catch (Exception e){
            System.out.println(" error while deserializing ");
            e.printStackTrace();
        }
        return  null;
    }
}
