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

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.kimios.kernel.dms.extension.impl.DMEntityAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "dm_entity")
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(name = "seq", allocationSize = 1, sequenceName = "dm_entity_id_seq")
public class DMEntityImpl implements DMEntity, Serializable
{
    public static String DM_PATH_SEPARATOR = "/";

    public static String DM_EXTENSION_SEPARATOR = ".";

    private static Logger log = LoggerFactory.getLogger(DMEntity.class);

    protected long uid;

    protected int type;

    protected String path = "";

    protected String name;

    protected String owner;

    protected String ownerSource;

    protected Date creationDate;

    protected Date updateDate;

    protected Map<String, DMEntityAttribute> attributes = new HashMap<String, DMEntityAttribute>();

    public DMEntityImpl()
    {
    }

    public DMEntityImpl(long uid, int type)
    {
        this.uid = uid;
        this.type = type;
    }

    @ElementCollection(fetch = FetchType.EAGER, targetClass = DMEntityAttribute.class)
    @MapKeyColumn(name = "attribute_name")
    @CollectionTable(name = "dm_entity_attributes",
            joinColumns = @JoinColumn(name = "dm_entity_id"))
    public Map<String, DMEntityAttribute> getAttributes()
    {
        return attributes;
    }

    public void setAttributes(Map<String, DMEntityAttribute> attributes)
    {
        this.attributes = attributes;
    }

    @Column(name = "creation_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreationDate()
    {
        return creationDate;
    }

    @Column(name = "dm_entity_name", nullable = false)
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Column(name = "dm_entity_owner", nullable = false)
    public String getOwner()
    {
        return owner;
    }

    @Column(name = "dm_entity_owner_source", nullable = false)
    public String getOwnerSource()
    {
        return ownerSource;
    }

    @Column(name = "dm_entity_type", nullable = false)
    public int getType()
    {
        return this.type;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @Column(name = "dm_entity_id")
    public long getUid()
    {
        return this.uid;
    }

    @Column(name = "dm_entity_path", columnDefinition = "text", nullable = false, unique = true)
    public String getPath()
    {
        return this.path;
    }

    public void setUid(long uid)
    {
        this.uid = uid;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    @Column(name = "update_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdateDate()
    {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate)
    {
        this.updateDate = updateDate;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public void setOwnerSource(String ownerSource)
    {
        this.ownerSource = ownerSource;
    }

    public void setCreationDate(Date creationDate)
    {
        this.creationDate = creationDate;
    }

    public int compareTo(DMEntity o)
    {
        return this.getName().toLowerCase().compareTo(o.getName().toLowerCase());
    }

    public boolean equals(Object o)
    {
        return (o instanceof DMEntityImpl && ((DMEntityImpl) o).getUid() == this.getUid());
    }

    @Override public String toString()
    {
        return "DMEntityImpl{" +
                "uid=" + uid +
                ", type=" + type +
                ", path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", owner='" + owner + '\'' +
                ", ownerSource='" + ownerSource + '\'' +
                ", creationDate=" + creationDate +
                ", updateDate=" + updateDate +
                ", attributes=" + attributes +
                '}';
    }
}

