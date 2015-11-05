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
package org.kimios.kernel.ws.pojo;

import java.util.Date;

public class Session
{
    private String sessionUid;

    private String userName;

    private String userSource;

    private Date lastUse;

    private String metaDatas;

    public Session()
    {

    }

    public Session(String sessionUid, String userName, String userSource, Date lastUse, String metaDatas)
    {
        super();
        this.sessionUid = sessionUid;
        this.userName = userName;
        this.userSource = userSource;
        this.lastUse = lastUse;
        this.metaDatas = metaDatas;
    }

    public String getSessionUid()
    {
        return sessionUid;
    }

    public void setSessionUid(String sessionUid)
    {
        this.sessionUid = sessionUid;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getUserSource()
    {
        return userSource;
    }

    public void setUserSource(String userSource)
    {
        this.userSource = userSource;
    }

    public Date getLastUse()
    {
        return lastUse;
    }

    public void setLastUse(Date lastUse)
    {
        this.lastUse = lastUse;
    }

    public String getMetaDatas()
    {
        return metaDatas;
    }

    public void setMetaDatas(String metaDatas)
    {
        this.metaDatas = metaDatas;
    }
}

