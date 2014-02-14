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

import java.io.Serializable;
import java.util.Date;

public class Folder extends DMEntity implements Serializable
{


    private long parentUid;

    private int parentType;

    public Folder()
    {
        this.type = 2;
    }

    public Folder(long uid, String name, String owner, String ownerSource,
            Date creationDate, Date updateDate, long parentUid, int parentType, String path)
    {
        super(uid, 2, name, owner, ownerSource, creationDate, updateDate, path);
        this.parentUid = parentUid;
        this.parentType = parentType;
    }

    public int getParentType()
    {
        return parentType;
    }

    public void setParentType(int parentType)
    {
        this.parentType = parentType;
    }

    public long getParentUid()
    {
        return parentUid;
    }

    public void setParentUid(long parentUid)
    {
        this.parentUid = parentUid;
    }
}

