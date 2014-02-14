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
package org.kimios.kernel.log;

import org.hibernate.annotations.ForeignKey;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.dms.DMEntityImpl;
import org.kimios.kernel.ws.pojo.Log;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "entity_log")
@SequenceGenerator(sequenceName = "entity_log_id_seq", name = "seq", allocationSize = 1)
public class DMEntityLog<T extends DMEntityImpl>
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @Column(name = "id")
    protected long id;

    @Column(name = "log_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date date;

    @Column(name = "username", nullable = true)
    protected String user;

    @Column(name = "user_source", nullable = true)
    protected String userSource;

    @Column(name = "dm_entity_id", nullable = false)
    protected long dmEntityUid;

    @Column(name = "dm_entity_type", nullable = false)
    protected int dmEntityType;

    @Column(name = "action")
    protected int action;

    @Column(name = "action_parameter", nullable = true)
    protected Long actionParameter;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dm_entity_id", updatable = false, insertable = false)
    @ForeignKey( name = "none")
    protected DMEntityImpl dmEntity;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public long getDmEntityUid()
    {
        return dmEntityUid;
    }

    public void setDmEntityUid(long dmEntityUid)
    {
        this.dmEntityUid = dmEntityUid;
    }

    public int getDmEntityType()
    {
        return dmEntityType;
    }

    public void setDmEntityType(int dmEntityType)
    {
        this.dmEntityType = dmEntityType;
    }

    public Long getActionParameter()
    {
        return actionParameter;
    }

    public void setActionParameter(Long actionParameter)
    {
        this.actionParameter = actionParameter;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getUserSource()
    {
        return userSource;
    }

    public void setUserSource(String userSource)
    {
        this.userSource = userSource;
    }

    public int getAction()
    {
        return action;
    }

    public void setAction(int action)
    {
        this.action = action;
    }

    public DMEntityImpl getDmEntity()
    {
        return dmEntity;
    }

    public void setDMEntity(DMEntity e)
    {
        this.setDmEntityType(e.getType());
        this.setDmEntityUid(e.getUid());
    }

    public org.kimios.kernel.ws.pojo.Log toPojo()
    {
        return new Log(this.id, this.date, this.user, this.userSource, this.dmEntityUid, this.dmEntityType,
                this.action);
    }
}

