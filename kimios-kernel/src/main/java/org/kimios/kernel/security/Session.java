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
import java.util.Date;
import java.util.Vector;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.kimios.kernel.user.Group;


@Entity
@Table(name = "user_session")
public class Session implements Serializable
{

    @Id
    @Column(name = "id")
    private String uid;

    @Column(name = "user_id")
    private String userName;

    @Column(name = "user_source")
    private String userSource;


    @Column(name = "last_use")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUse;

    private Vector<Group> groups;

    private String metaDatas;

    public Session()
    {
    }

    public Session(String uid, String userName, String userSource, Date lastUse, Vector<Group> groups)
    {
        this.uid = uid;
        this.userName = userName;
        this.userSource = userSource;
        this.lastUse = lastUse;
        this.groups = groups;
    }

    public String getUserSource()
    {
        return userSource;
    }

    public void setUserSource(String userSource)
    {
        this.userSource = userSource;
    }

    public Vector<Group> getGroups()
    {
        return groups;
    }

    public void setGroups(Vector<Group> groups)
    {
        this.groups = groups;
    }

    public Date getLastUse()
    {
        return lastUse;
    }

    public void setLastUse(Date startTime)
    {
        this.lastUse = startTime;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getMetaDatas()
    {
        return metaDatas;
    }

    public void setMetaDatas(String metaDatas)
    {
        this.metaDatas = metaDatas;
    }

    @Override public String toString()
    {
        return "Session{" +
                "uid='" + uid + '\'' +
                ", userName='" + userName + '\'' +
                ", userSource='" + userSource + '\'' +
                ", lastUse=" + lastUse +
                ", metaDatas='" + metaDatas + '\'' +
                ", groups=" + groups +
                '}';
    }
}

