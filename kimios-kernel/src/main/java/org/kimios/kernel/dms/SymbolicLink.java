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
package org.kimios.kernel.dms;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Table(name = "symbolic_link", uniqueConstraints=@UniqueConstraint(columnNames={"target_entity_id", "parent_id"}))
@PrimaryKeyJoinColumn(name = "id")
public class SymbolicLink extends DMEntityImpl
{
    private long dmEntityUid;

    private int dmEntityType;

    private long parentUid;

    private int parentType;

    private DMEntity parent;

    private DMEntity linkTarget;

    public SymbolicLink(long uid, int type)
    {
        this.uid = uid;
        this.type = type;
    }

    public SymbolicLink()
    {
        this.type = DMEntityType.SYMBOLIC_LINK;
    }

    @Column(name = "target_entity_id", nullable = false)
    public long getDmEntityUid()
    {
        return dmEntityUid;
    }

    public void setDmEntityUid(long dmEntityUid)
    {
        this.dmEntityUid = dmEntityUid;
    }

    @Column(name = "target_entity_type", nullable = false)
    public int getDmEntityType()
    {
        return dmEntityType;
    }

    public void setDmEntityType(int dmEntityType)
    {
        this.dmEntityType = dmEntityType;
    }

    @Column(name = "parent_id", nullable = false)
    public long getParentUid()
    {
        return parentUid;
    }

    public void setParentUid(long parentUid)
    {
        this.parentUid = parentUid;
    }

    @Column(name = "parent_type", nullable = false)
    public int getParentType()
    {
        return parentType;
    }

    public void setParentType(int parentType)
    {
        this.parentType = parentType;
    }

    @ManyToOne(targetEntity = DMEntityImpl.class)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    public DMEntity getParent()
    {
        return parent;
    }

    public void setParent(DMEntity parent)
    {
        this.parent = parent;
    }

    @ManyToOne(targetEntity = DMEntityImpl.class)
    @JoinColumn(name = "target_entity_id", insertable = false, updatable = false)
    public DMEntity getLinkTarget()
    {
        return linkTarget;
    }

    public void setLinkTarget(DMEntity linkTarget)
    {
        this.linkTarget = linkTarget;
    }

    @Transient
    @Override
    public org.kimios.kernel.ws.pojo.SymbolicLink toPojo()
    {
        org.kimios.kernel.ws.pojo.SymbolicLink sl = new org.kimios.kernel.ws.pojo.SymbolicLink();

        sl.setUid(this.getUid());
        sl.setType(DMEntityType.SYMBOLIC_LINK);

        Calendar cal = Calendar.getInstance();
        cal.setTime(this.creationDate);
        sl.setCreationDate(cal);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(this.updateDate);
        sl.setUpdateDate(cal2);

        sl.setPath(this.getPath());
        sl.setParentType(this.parentType);
        sl.setParentUid(this.parentUid);


        /*
            Target
         */
        sl.setName(this.getLinkTarget().getName());
        sl.setDmEntityType(this.dmEntityType);
        sl.setDmEntityUid(this.dmEntityUid);
        sl.setTarget(this.getLinkTarget().toPojo());

        /*
           item
        */


        return sl;
    }
}

