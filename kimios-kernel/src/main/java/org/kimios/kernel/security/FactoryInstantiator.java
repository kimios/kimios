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
package org.kimios.kernel.security;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.security.pwdgen.CredentialsGenerator;
import org.kimios.kernel.security.pwdgen.md5.MD5Generator;

public class FactoryInstantiator implements ISecurityFactoryInstantiator {
    private static FactoryInstantiator instance;

    synchronized public static FactoryInstantiator getInstance()
    {
        if (instance == null) {
            instance = new FactoryInstantiator();
        }
        return instance;
    }

    private DMEntitySecurityFactory dMEntitySecurityFactory;

    private RoleFactory roleFactory;

    private AuthenticatedServiceFactory authenticatedServiceFactory;

    @Override
    public CredentialsGenerator getCredentialsGenerator() throws ConfigException
    {
        return new MD5Generator();
    }

    @Override
    public DMEntitySecurityFactory getDMEntitySecurityFactory()
    {
        return dMEntitySecurityFactory;
    }

    public void setDMEntitySecurityFactory(
            DMEntitySecurityFactory dmEntitySecurityFactory)
    {
        this.dMEntitySecurityFactory = dmEntitySecurityFactory;
    }

    @Override
    public RoleFactory getRoleFactory()
    {
        return roleFactory;
    }

    public void setRoleFactory(RoleFactory roleFactory)
    {
        this.roleFactory = roleFactory;
    }

    @Override
    public AuthenticatedServiceFactory getAuthenticatedServiceFactory()
    {
        return authenticatedServiceFactory;
    }

    public void setAuthenticatedServiceFactory(AuthenticatedServiceFactory authenticatedServiceFactory)
    {
        this.authenticatedServiceFactory = authenticatedServiceFactory;
    }
}

