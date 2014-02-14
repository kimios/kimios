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
package org.kimios.kernel.ws.pojo;

public class JoomlaParam
{
    private String databaseUrl;

    private String databaseLogin;

    private String databasePassword;

    private String tablePrefix;

    private String authenticationSourceName;

    public JoomlaParam()
    {
    }

    public JoomlaParam(String databaseUrl, String databaseLogin, String databasePassword, String sourceName,
            String tablePrefix)
    {

        this.databaseUrl = databaseUrl;
        this.databaseLogin = databaseLogin;
        this.databasePassword = databasePassword;
        this.authenticationSourceName = sourceName;
        this.tablePrefix = tablePrefix;
    }

    public String getTablePrefix()
    {
        return tablePrefix;
    }

    public void setTablePrefix(String tablePrefix)
    {
        this.tablePrefix = tablePrefix;
    }

    public String getDatabaseUrl()
    {
        return databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl)
    {
        this.databaseUrl = databaseUrl;
    }

    public String getDatabaseLogin()
    {
        return databaseLogin;
    }

    public void setDatabaseLogin(String databaseLogin)
    {
        this.databaseLogin = databaseLogin;
    }

    public String getDatabasePassword()
    {
        return databasePassword;
    }

    public void setDatabasePassword(String databasePassword)
    {
        this.databasePassword = databasePassword;
    }

    public String getAuthenticationSourceName()
    {
        return authenticationSourceName;
    }

    public void setAuthenticationSourceName(String authenticationSourceName)
    {
        this.authenticationSourceName = authenticationSourceName;
    }
}

