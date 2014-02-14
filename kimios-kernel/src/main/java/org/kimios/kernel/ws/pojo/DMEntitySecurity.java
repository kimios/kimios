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

public class DMEntitySecurity
{
    private long dmEntityUid;

    private int dmEntityType;

    private String name;

    private String source;

    private String fullName;

    private int type;

    private boolean read;

    private boolean write;

    private boolean fullAccess;

    public DMEntitySecurity()
    {

    }

    public DMEntitySecurity(long dmEntityUid, int dmEntityType, String name, String source, int type, boolean read,
            boolean write, boolean fullAccess)
    {
        this.dmEntityUid = dmEntityUid;
        this.dmEntityType = dmEntityType;
        this.name = name;
        this.source = source;
        this.type = type;
        this.read = read;
        this.write = write;
        this.fullAccess = fullAccess;
    }

    public DMEntitySecurity(long dmEntityUid, int dmEntityType, String name, String source, String fullName, int type,
            boolean read, boolean write, boolean fullAccess)
    {
        this.dmEntityUid = dmEntityUid;
        this.dmEntityType = dmEntityType;
        this.name = name;
        this.source = source;
        this.type = type;
        this.read = read;
        this.write = write;
        this.fullAccess = fullAccess;
        this.fullName = fullName;
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

    public boolean isFullAccess()
    {
        return fullAccess;
    }

    public void setFullAccess(boolean fullAccess)
    {
        this.fullAccess = fullAccess;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isRead()
    {
        return read;
    }

    public void setRead(boolean read)
    {
        this.read = read;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public boolean isWrite()
    {
        return write;
    }

    public void setWrite(boolean write)
    {
        this.write = write;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }
}

