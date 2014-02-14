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
package org.kimios.kernel.user;

import org.kimios.kernel.security.SecurityEntity;
import org.kimios.kernel.security.SecurityEntityType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "groups")
@IdClass(GroupPK.class)
public class Group implements SecurityEntity, Serializable
{
    @Id @Column(name = "gid")
    private String gid;

    @Column(name = "group_name")
    private String name;

    @Id @Column(name = "authentication_source")
    private String authenticationSourceName;

    @ManyToMany(targetEntity = User.class, mappedBy = "groups")
    private Set<User> users = new HashSet<User>();

    public Group()
    {
    }

    public Group(String gid, String name, String authenticationSourceName)
    {
        this.gid = gid;
        this.name = name;
        this.authenticationSourceName = authenticationSourceName;
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

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean equals(Object obj)
    {
        if (obj.getClass().equals(this.getClass())) {
            return ((Group) obj).getGid().equals(this.getGid()) && ((Group) obj).getName().equals(this.getName());
        } else {
            return false;
        }
    }

    public int getType()
    {
        return SecurityEntityType.GROUP;
    }

    public String getID()
    {
        return this.gid;
    }

    public Set<User> getUsers()
    {
        return users;
    }

    public void setUsers(Set<User> users)
    {
        this.users = users;
    }

    public org.kimios.kernel.ws.pojo.Group toPojo()
    {
        return new org.kimios.kernel.ws.pojo.Group(this.gid, this.name, this.authenticationSourceName);
    }
}

