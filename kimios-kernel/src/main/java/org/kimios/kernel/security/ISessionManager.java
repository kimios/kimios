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

package org.kimios.kernel.security;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.user.AuthenticationSourceFactory;
import org.kimios.kernel.user.User;

import java.util.Collection;
import java.util.List;

public interface ISessionManager
{
    AuthenticationSourceFactory getAuthenticationSourceFactory();

    void setAuthenticationSourceFactory(
            AuthenticationSourceFactory authenticationSourceFactory);

    AuthenticatedServiceFactory getAuthenticatedServiceFactory();

    void setAuthenticatedServiceFactory(AuthenticatedServiceFactory authenticatedServiceFactory);

    Session startSession(String uid, String password, String userSource)
                    throws DataSourceException, ConfigException;

    Session startSession(String uid, String userSource) throws DataSourceException, ConfigException;

    Session getSession(String sessionUid) throws DataSourceException, ConfigException;

    void cleanSessionContext(long sessionExpire);

    void closeSessionContext() throws DataSourceException, ConfigException;

    void initSessionContext() throws DataSourceException, ConfigException;

    Collection<User> getConnectedUsers() throws DataSourceException, ConfigException;

    List<Session> getSessions() throws DataSourceException, ConfigException;

    List<Session> getSessions(String userName, String userSource) throws DataSourceException, ConfigException;

    void removeSession(String sessionUid) throws DataSourceException, ConfigException;

    void removeSessions(String userName, String userSource) throws DataSourceException, ConfigException;
}
