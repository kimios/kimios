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
package org.kimios.kernel.dms;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@IdClass(BookmarkPK.class)
@Table(name = "bookmarks")
public class Bookmark implements Serializable
{
    @Id @Column(name = "bk_owner")
    private String owner;

    @Id @Column(name = "bk_owner_source")
    private String ownerSource;

    @Id @Column(name = "bk_owner_type")
    private int ownerType;

    @Id @Column(name = "dm_entity_id")
    private long uid;

    @Column(name = "dm_entity_type")
    private int type;

    @Transient
    private DMEntity entity;

    public DMEntity getEntity() {
        return entity;
    }

    public void setEntity(DMEntity entity) {
        this.entity = entity;
    }

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

    public Bookmark(String owner, String ownerSource, int ownerType, long uid, int type)
    {
        this.owner = owner;
        this.ownerType = ownerType;
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

    public int getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(int ownerType) {
        this.ownerType = ownerType;
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
        if(entity != null){
            b.setEntity(entity.toPojo());
        }
        return b;
    }
}

