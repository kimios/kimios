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
package org.kimios.kernel.security;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.dms.DMEntityImpl;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@IdClass(DMEntityACLPk.class)
@Table(name = "dm_entity_acl")
public class DMEntityACL implements Serializable
{
    @Id @Column(name = "dm_entity_id")
    private long dmEntityUid;

    @Column(name = "dm_entity_type")
    private int dmEntityType;

    @Id @Column(name = "rule_hash")
    private String ruleHash;

    @ManyToOne(targetEntity = DMEntityImpl.class)
    @JoinColumn(name = "dm_entity_id", nullable = false, insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DMEntityImpl entity;

    public DMEntityACL()
    {
    }

    public DMEntityACL(DMEntity item)
    {
        this.dmEntityType = item.getType();
        this.dmEntityUid = item.getUid();
    }

    public long getDmEntityUid()
    {
        return dmEntityUid;
    }

    public void setDmEntityUid(long dmEntityUid)
    {
        this.dmEntityUid = dmEntityUid;
    }

    public String getRuleHash()
    {
        return ruleHash;
    }

    public void setRuleHash(String ruleHash)
    {
        this.ruleHash = ruleHash;
    }

    public int getDmEntityType()
    {
        return dmEntityType;
    }

    public void setDmEntityType(int dmEntityType)
    {
        this.dmEntityType = dmEntityType;
    }

    public DMEntityImpl getEntity()
    {
        return entity;
    }

    public void setEntity(DMEntityImpl entity)
    {
        this.entity = entity;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof DMEntityACL) {
            return ((DMEntityACL) obj).dmEntityUid == this.dmEntityUid &&
                    ((DMEntityACL) obj).ruleHash.equals(this.ruleHash);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        int result = 17;
        result = 37 * result + ((Long) dmEntityUid).hashCode();
        result = 37 * result + ruleHash.hashCode();
        return result;
    }
}
