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
package org.kimios.kernel.controller.impl;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.configuration.Config;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.IServerInformationController;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.security.model.Session;
import org.kimios.utils.configuration.ConfigurationManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Transactional
public class ServerInformationController extends AKimiosController implements IServerInformationController
{
    private static String SERVER_VERSION = "Kimios 1.1";

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.IServerInformationController#getServerVersion()
    */
    public String getServerVersion()
    {
        return SERVER_VERSION;
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.IServerInformationController#getServerOnlineTime(org.kernel.security.Session)
    */
    public Date getServerOnlineTime(Session session) throws DataSourceException, ConfigException, AccessDeniedException
    {
        throw new AccessDeniedException();
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.IServerInformationController#getServerName()
    */
    public String getServerName() throws ConfigException
    {
        return ConfigurationManager.getValue(Config.SERVER_NAME);
    }
}

