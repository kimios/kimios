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
package org.kimios.kernel.dms;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "workspace")
@PrimaryKeyJoinColumn(name = "id")
public class Workspace extends DMEntityImpl
{
    public Workspace()
    {
        this.type = DMEntityType.WORKSPACE;
    }

    public Workspace(long uid, String name, String owner, String ownerSource, Date creationDate)
    {
        this.type = DMEntityType.WORKSPACE;
        this.uid = uid;
        this.name = name;
        this.owner = owner;
        this.ownerSource = ownerSource;
        this.creationDate = creationDate;
    }

    public Workspace(long uid, String name, String owner, String ownerSource, Date creationDate, Date updateDate)
    {
        this.type = DMEntityType.WORKSPACE;
        this.uid = uid;
        this.name = name;
        this.owner = owner;
        this.ownerSource = ownerSource;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
    }

    @Transient
    public org.kimios.kernel.ws.pojo.Workspace toPojo()
    {
        return new org.kimios.kernel.ws.pojo.Workspace(this.uid, this.name,
                this.owner, this.ownerSource, this.creationDate, this.updateDate, this.path);
    }
}

