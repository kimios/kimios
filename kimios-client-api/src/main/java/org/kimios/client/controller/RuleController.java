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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kimios.client.controller;

import org.kimios.client.exception.ExceptionHelper;
import org.kimios.webservices.RuleService;

/**
 * @author Fabien Alin
 */
public class RuleController
{

    private RuleService client;

    public RuleService getClient()
    {
        return client;
    }

    public void setClient( RuleService client )
    {
        this.client = client;
    }

    public String[] getAvailablesRules( String sessionId )
        throws Exception
    {
        try
        {
            return client.getAvailablesRules( sessionId );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    public String getRuleParametersDescription( String sessionId, String javaClassName )
        throws Exception
    {
        try
        {
            return client.getRuleParam( sessionId, javaClassName );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }

    public void createRule( String sessionId, String conditionJavaClass, String ruleName, String path, String xml )
        throws Exception
    {
        try
        {
            client.createRule( sessionId, conditionJavaClass, ruleName, path, xml );
        }
        catch ( Exception e )
        {
            throw new ExceptionHelper().convertException( e );
        }
    }
}

