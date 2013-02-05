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
package org.kimios.kernel.log;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.dms.DMEntityImpl;
import org.kimios.kernel.dms.DMEntityType;
import org.kimios.kernel.dms.FactoryInstantiator;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.ws.pojo.Log;

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

    public DMEntity getDMEntity() throws ConfigException, DataSourceException
    {
        FactoryInstantiator fc = FactoryInstantiator.getInstance();
        switch (this.getDmEntityType()) {
            case DMEntityType.WORKSPACE:
                return fc.getWorkspaceFactory().getWorkspace(this.getDmEntityUid());
            case DMEntityType.FOLDER:
                return fc.getFolderFactory().getFolder(this.getDmEntityUid());
            case DMEntityType.DOCUMENT:
                return fc.getDocumentFactory().getDocument(this.getDmEntityUid());
            default:
                return null;
        }
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

