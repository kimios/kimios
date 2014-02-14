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

import java.util.Calendar;
import java.util.Date;

public class Log
{
    private long uid;

    private Calendar date;

    private String user;

    private String userSource;

    private long dmEntityUid;

    private int dmEntityType;

    private int operation;

    public Log()
    {
    }

    public Log(long uid, Date date, String user, String userSource, long dmEntityUid, int dmEntityType, int operation)
    {
        this.uid = uid;
        this.date = Calendar.getInstance();
        this.date.setTime(date);
        this.user = user;
        this.userSource = userSource;
        this.dmEntityUid = dmEntityUid;
        this.dmEntityType = dmEntityType;
        this.operation = operation;
    }

    public Calendar getDate()
    {
        return date;
    }

    public void setDate(Calendar date)
    {
        this.date = date;
    }

    public int getDmEntityType()
    {
        return dmEntityType;
    }

    public void setDmEntityType(int dmEntityType)
    {
        this.dmEntityType = dmEntityType;
    }

    public long getDmEntityUid()
    {
        return dmEntityUid;
    }

    public void setDmEntityUid(long dmEntityUid)
    {
        this.dmEntityUid = dmEntityUid;
    }

    public int getOperation()
    {
        return operation;
    }

    public void setOperation(int operation)
    {
        this.operation = operation;
    }

    public long getUid()
    {
        return uid;
    }

    public void setUid(long uid)
    {
        this.uid = uid;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getUserSource()
    {
        return userSource;
    }

    public void setUserSource(String userSource)
    {
        this.userSource = userSource;
    }
}
