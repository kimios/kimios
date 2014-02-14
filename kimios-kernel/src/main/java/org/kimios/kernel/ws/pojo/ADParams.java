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

public class ADParams
{
    public String authenticationSourceName;

    private String serverUrl;

    private String baseDN;

    private String adminLogin;

    private String adminPassword;

    private String userWrapLeft;

    private String userWrapRight;

    private String usersCN;

    public ADParams()
    {
    }

    public ADParams(String authenticationSourceName, String serverUrl,
            String baseDN, String adminLogin, String adminPassword,
            String userWrapLeft, String userWrapRight, String usersCN)
    {
        this.authenticationSourceName = authenticationSourceName;
        this.serverUrl = serverUrl;
        this.baseDN = baseDN;
        this.adminLogin = adminLogin;
        this.adminPassword = adminPassword;
        this.userWrapLeft = userWrapLeft;
        this.userWrapRight = userWrapRight;
        this.usersCN = usersCN;
    }

    public String getAuthenticationSourceName()
    {
        return authenticationSourceName;
    }

    public void setAuthenticationSourceName(String authenticationSourceName)
    {
        this.authenticationSourceName = authenticationSourceName;
    }

    public String getServerUrl()
    {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl)
    {
        this.serverUrl = serverUrl;
    }

    public String getBaseDN()
    {
        return baseDN;
    }

    public void setBaseDN(String baseDN)
    {
        this.baseDN = baseDN;
    }

    public String getAdminLogin()
    {
        return adminLogin;
    }

    public void setAdminLogin(String adminLogin)
    {
        this.adminLogin = adminLogin;
    }

    public String getAdminPassword()
    {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword)
    {
        this.adminPassword = adminPassword;
    }

    public String getUserWrapLeft()
    {
        return userWrapLeft;
    }

    public void setUserWrapLeft(String userWrapLeft)
    {
        this.userWrapLeft = userWrapLeft;
    }

    public String getUserWrapRight()
    {
        return userWrapRight;
    }

    public void setUserWrapRight(String userWrapRight)
    {
        this.userWrapRight = userWrapRight;
    }

    public String getUsersCN()
    {
        return usersCN;
    }

    public void setUsersCN(String usersCN)
    {
        this.usersCN = usersCN;
    }
}

