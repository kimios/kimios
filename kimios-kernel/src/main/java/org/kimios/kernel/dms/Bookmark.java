/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
 *
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.kernel.dms;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(BookmarkPK.class)
@Table(name = "bookmarks")
public class Bookmark implements Serializable
{
    @Id @Column(name = "bk_owner")
    private String owner;

    @Id @Column(name = "bk_owner_source")
    private String ownerSource;

    @Id @Column(name = "dm_entity_id")
    private long uid;

    @Column(name = "dm_entity_type")
    private int type;

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public void setOwnerSource(String ownerSource)
    {
        this.ownerSource = ownerSource;
    }

    public void setUid(long uid)
    {
        this.uid = uid;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public Bookmark()
    {
    }

    public Bookmark(String owner, String ownerSource, long uid, int type)
    {
        this.owner = owner;
        this.ownerSource = ownerSource;
        this.uid = uid;
        this.type = type;
    }

    public String getOwner()
    {
        return owner;
    }

    public String getOwnerSource()
    {
        return ownerSource;
    }

    public int getType()
    {
        return type;
    }

    public long getUid()
    {
        return uid;
    }

    public org.kimios.kernel.ws.pojo.Bookmark toPojo()
    {
        org.kimios.kernel.ws.pojo.Bookmark b = new org.kimios.kernel.ws.pojo.Bookmark();
        b.setDmEntityType(this.type);
        b.setDmEntityUid(this.uid);
        return b;
    }
}

