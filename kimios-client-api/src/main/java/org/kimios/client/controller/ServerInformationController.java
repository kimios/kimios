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
package org.kimios.client.controller;

import org.kimios.client.exception.ExceptionHelper;
import org.kimios.webservices.InformationService;

import java.util.Date;

/**
 * Used to get information about Kimios Server
 *
 * @author Fabien Alin
 */
public class ServerInformationController
{

    private InformationService client;

    public InformationService getClient()
    {
        return client;
    }

    public void setClient( InformationService client )
    {
        this.client = client;
    }

    /**
     * Return the current server version number
     */
    public String getServerVersionNumber()
        throws Exception
    {
        try
        {
            return client.getServerVersionNumber();
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    /**
     * Return the server online time
     */
    public Date getServerOnlineTime( String sessionId )
        throws Exception
    {
        try
        {
            return client.getServerOnlineTime( sessionId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }
}

