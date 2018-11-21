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
package org.kimios.kernel.controller;

import org.kimios.exceptions.ConfigException;
import org.kimios.exceptions.AccessDeniedException;
import org.kimios.exceptions.DataSourceException;
import org.kimios.kernel.security.model.Session;
import org.kimios.utils.registration.RegistrationData;

import java.util.Date;

public interface IServerInformationController
{
    public String getServerVersion();

    public Date getServerOnlineTime(Session session)
            throws DataSourceException, ConfigException, AccessDeniedException;

    public String getServerName() throws ConfigException;

    public String getTelemetryUUID() throws ConfigException;

    public void register(RegistrationData data) throws ConfigException;

    public boolean isRegistered() throws ConfigException;
}
