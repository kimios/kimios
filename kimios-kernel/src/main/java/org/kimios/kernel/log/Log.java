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
package org.kimios.kernel.log;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "generic_log")
@SequenceGenerator(sequenceName = "log_id_seq", name = "seq", allocationSize = 1)
public class Log implements ILog, Serializable
{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    protected long uid;

    @Column(name = "log_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date date;

    @Column(name = "username")
    protected String user;

    @Column(name = "user_source")
    protected String userSource;

    @Column(name = "action", nullable = false)
    protected int action;

    @Column(name = "action_parameters", nullable = true)
    protected Long actionParameters;

    public long getUid()
    {
        return uid;
    }

    public void setUid(long uid)
    {
        this.uid = uid;
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

    public Long getActionParameters()
    {
        return this.actionParameters;
    }

    public void setActionParameters(Long actionParameters)
    {
        this.actionParameters = actionParameters;
    }
}

