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
package org.kimios.kernel.user;

import java.io.Serializable;

public class GroupPK implements Serializable
{
    private String gid;

    private String authenticationSourceName;

    public GroupPK()
    {
    }

    public String getAuthenticationSourceName()
    {
        return authenticationSourceName;
    }

    public void setAuthenticationSourceName(String authenticationSourceName)
    {
        this.authenticationSourceName = authenticationSourceName;
    }

    public String getGid()
    {
        return gid;
    }

    public void setGid(String gid)
    {
        this.gid = gid;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GroupPK groupPK = (GroupPK) o;

        if (authenticationSourceName != null ? !authenticationSourceName.equals(groupPK.authenticationSourceName) :
                groupPK.authenticationSourceName != null)
        {
            return false;
        }
        if (gid != null ? !gid.equals(groupPK.gid) : groupPK.gid != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = gid != null ? gid.hashCode() : 0;
        result = 31 * result + (authenticationSourceName != null ? authenticationSourceName.hashCode() : 0);
        return result;
    }
}

