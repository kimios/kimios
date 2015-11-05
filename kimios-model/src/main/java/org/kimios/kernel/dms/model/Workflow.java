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
package org.kimios.kernel.dms.model;

import javax.persistence.*;

@Entity
@Table(name = "workflow")
@SequenceGenerator(allocationSize = 1, name = "seq", sequenceName = "wkf_id_seq")
public class Workflow
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @Column(name = "id")
    private long uid;

    @Column(name = "workflow_name", length = 200, nullable = false)
    private String name;

    @Column(name = "workflow_description", length = 5000)
    private String description;

    public Workflow()
    {
    }

    public Workflow(long uid, String name, String description)
    {
        this.uid = uid;
        this.name = name;
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public long getUid()
    {
        return uid;
    }

    public void setUid(long uid)
    {
        this.uid = uid;
    }

    public org.kimios.kernel.ws.pojo.Workflow toPojo()
    {
        return new org.kimios.kernel.ws.pojo.Workflow(this.uid, this.name, this.description);
    }
}

