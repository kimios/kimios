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
package org.kimios.kernel.dms.model;

import java.io.Serializable;

public class BookmarkPK implements Serializable
{
    private String owner;

    private String ownerSource;

    private long uid;

    private int ownerType;

    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public String getOwnerSource()
    {
        return ownerSource;
    }

    public void setOwnerSource(String ownerSource)
    {
        this.ownerSource = ownerSource;
    }

    public long getUid()
    {
        return uid;
    }

    public void setUid(long uid)
    {
        this.uid = uid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookmarkPK that = (BookmarkPK) o;

        if (uid != that.uid) return false;
        if (ownerType != that.ownerType) return false;
        if (!owner.equals(that.owner)) return false;
        return ownerSource.equals(that.ownerSource);

    }

    @Override
    public int hashCode() {
        int result = owner.hashCode();
        result = 31 * result + ownerSource.hashCode();
        result = 31 * result + (int) (uid ^ (uid >>> 32));
        result = 31 * result + ownerType;
        return result;
    }

    public int getOwnerType() {

        return ownerType;
    }

    public void setOwnerType(int ownerType) {
        this.ownerType = ownerType;
    }

}

