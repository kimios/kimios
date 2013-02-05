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

import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.kimios.kernel.exception.MetaFeedSearchException;

@Entity
@Table(name = "meta_feed")
@SequenceGenerator(name = "seq", sequenceName = "meta_feed_id_seq")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "java_class", discriminatorType = DiscriminatorType.STRING, length = 255)
public abstract class MetaFeedImpl implements MetaFeed
{
    @Id @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    protected long uid;

    @Column(name = "meta_feed_name", nullable = false)
    protected String name;

    @Column(name = "java_class", nullable = false, insertable = false, updatable = false)
    private String javaClass;

    public MetaFeedImpl()
    {
    }

    public long getUid()
    {
        return uid;
    }

    public void setUid(long uid)
    {
        this.uid = uid;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public abstract List<String> getValues();

    public abstract String[] search(String criteria) throws MetaFeedSearchException;

    public String getJavaClass()
    {
        return javaClass;
    }

    public void setJavaClass(String javaClass)
    {
        this.javaClass = javaClass;
    }

    public org.kimios.kernel.ws.pojo.MetaFeed toPojo()
    {
        return new org.kimios.kernel.ws.pojo.MetaFeed(this.getUid(), this.getName(), this.getJavaClass());
    }
}

