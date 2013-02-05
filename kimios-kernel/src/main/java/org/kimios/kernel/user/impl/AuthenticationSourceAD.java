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
package org.kimios.kernel.user.impl;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.user.AuthenticationSourceImpl;
import org.kimios.kernel.user.GroupFactory;
import org.kimios.kernel.user.UserFactory;
import org.kimios.kernel.user.impl.factory.activedirectory.ActiveDirectoryGroupFactory;
import org.kimios.kernel.user.impl.factory.activedirectory.ActiveDirectoryUserFactory;

/**
 * @see GenericLDAPImpl
 * @deprecated
 */
public class AuthenticationSourceAD extends AuthenticationSourceImpl
{
    private String adminLogin;

    private String adminPassword;

    private String baseDn;

    private String serverUrl;

    private String usersCn;

    private String userWrapLeft;

    private String userWrapRight;

    public UserFactory getUserFactory() throws DataSourceException, ConfigException
    {
        return new ActiveDirectoryUserFactory(this);
    }

    public GroupFactory getGroupFactory() throws DataSourceException, ConfigException
    {
        return new ActiveDirectoryGroupFactory(this);
    }

    public String getAdminLogin()
    {
        return adminLogin;
    }

    public String getAdminPassword()
    {
        return adminPassword;
    }

    public String getBaseDn()
    {
        return baseDn;
    }

    public String getServerUrl()
    {
        return serverUrl;
    }

    public String getUsersCn()
    {
        return usersCn;
    }

    public String getUserWrapLeft()
    {
        return userWrapLeft;
    }

    public String getUserWrapRight()
    {
        return userWrapRight;
    }
}

