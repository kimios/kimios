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
import org.kimios.kernel.user.impl.factory.ldap.LDAPGroupFactory;
import org.kimios.kernel.user.impl.factory.ldap.LDAPUserFactory;

/**
 * @see GenericLDAPImpl
 * @deprecated
 */
public class AuthenticationSourceLDAP extends AuthenticationSourceImpl
{
    private String serverUrl;

    private String baseDn;

    private String authenticationMode;

    private String referralMode;

    private String usersDn;

    private String groupsDn;

    private String rootDn;

    private String rootDnPassword;

    private String userNameAttribute;

    private String uidAttribute;

    private String groupNameAttribute;

    private String gidAttribute;

    private String mailAttribute;

    public UserFactory getUserFactory()
    {
        return new LDAPUserFactory(this);
    }

    public GroupFactory getGroupFactory() throws DataSourceException, ConfigException
    {
        return new LDAPGroupFactory(this);
    }

    public String getServerUrl()
    {
        return serverUrl;
    }

    public String getBaseDn()
    {
        return baseDn;
    }

    public String getAuthenticationMode()
    {
        return authenticationMode;
    }

    public String getReferralMode()
    {
        return referralMode;
    }

    public String getUsersDn()
    {
        return usersDn;
    }

    public String getGroupsDn()
    {
        return groupsDn;
    }

    public String getRootDn()
    {
        return rootDn;
    }

    public String getRootDnPassword()
    {
        return rootDnPassword;
    }

    public String getUserNameAttribute()
    {
        return userNameAttribute;
    }

    public String getUidAttribute()
    {
        return uidAttribute;
    }

    public String getGroupNameAttribute()
    {
        return groupNameAttribute;
    }

    public String getGidAttribute()
    {
        return gidAttribute;
    }

    public String getMailAttribute()
    {
        return mailAttribute;
    }
}

