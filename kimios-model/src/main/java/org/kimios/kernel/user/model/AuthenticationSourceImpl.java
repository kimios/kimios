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
package org.kimios.kernel.user.model;

import org.hibernate.Session;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;

/**
 * This class must be extended for each new authentication source implementation
 *
 * @author jludmann
 */
public abstract class AuthenticationSourceImpl extends HFactory implements AuthenticationSource
{
    protected Session session;

    protected String name;

    protected Boolean enableAuthByEmail;

    protected Boolean enableSSOCheck;


    public Boolean getEnableAuthByEmail() {
        return enableAuthByEmail;
    }

    public void setEnableAuthByEmail(Boolean enableAuthByEmail) {
        this.enableAuthByEmail = enableAuthByEmail;
    }

    public Boolean getEnableSSOCheck() {
        return enableSSOCheck;
    }

    public void setEnableSSOCheck(Boolean enableSSOCheck) {
        this.enableSSOCheck = enableSSOCheck;
    }

    /**
     * Get the current authentication source name
     */
    public final String getName()
    {
        return name;
    }

    /**
     * Set the current authentication source name
     */
    public final void setName(String name)
    {
        this.name = name;
    }

    /**
     * Convert current authentication source implementation to serializable plain old java object
     */
    public final org.kimios.kernel.ws.pojo.AuthenticationSource toPojo()
    {
        return new org.kimios.kernel.ws.pojo.AuthenticationSource(this.getName(), this.getClass().getName(), this.getEnableSSOCheck(), this.getEnableAuthByEmail());
    }

    public abstract GroupFactory getGroupFactory() throws DataSourceException,
            ConfigException;

    public abstract UserFactory getUserFactory() throws DataSourceException,
            ConfigException;
}

