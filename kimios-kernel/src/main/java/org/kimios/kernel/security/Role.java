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
package org.kimios.kernel.security;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "roles")
@IdClass(RolePK.class)
public class Role implements Serializable
{
    @Id @Column(name = "role_value", nullable = false)
    private int role;

    @Id @Column(name = "username", nullable = false)
    private String userName;

    @Id @Column(name = "user_source", nullable = false)
    private String userSource;

    public Role()
    {
    }

    public final static int WORKSPACE = 1;

    public final static int STUDIO = 2;

    public final static int ADMIN = 3;

    public final static int METAFEEDDENIED = 4;

    public final static int REPORTING = 5;

    public Role(int role, String userName, String userSource)
    {
        this.role = role;
        this.userName = userName;
        this.userSource = userSource;
    }

    public int getRole()
    {
        return role;
    }

    public void setRole(int role)
    {
        this.role = role;
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

    public org.kimios.kernel.ws.pojo.Role toPojo()
    {
        return new org.kimios.kernel.ws.pojo.Role(this.role, this.userName, this.userSource);
    }

    @Override public String toString()
    {
        return "Role{" +
                "role=" + role +
                ", userName='" + userName + '\'' +
                ", userSource='" + userSource + '\'' +
                '}';
    }
}

