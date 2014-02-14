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

import org.kimios.client.exception.ExceptionHelper;
import org.kimios.kernel.ws.pojo.Log;
import org.kimios.webservices.LogService;

/**
 * LogController is used to get document logs
 *
 * @author Fabien Alin
 */
public class LogController
{

    private LogService client;

    public LogService getClient()
    {
        return client;
    }

    public void setClient( LogService client )
    {
        this.client = client;
    }

    /**
     * Get document logs
     */
    public Log[] getDocumentLogs( String sessionId, long documentId )
        throws Exception
    {
        try
        {
            Log[] tmp = client.getDocumentLogs( sessionId, documentId );
            if ( tmp != null )
            {
                return tmp;
            }
            else
            {
                return new Log[0];
            }
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }
}

