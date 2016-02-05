/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
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

import org.kimios.api.MetaFeed;
import org.kimios.exceptions.MetaFeedSearchException;
import org.kimios.exceptions.MethodNotImplemented;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "meta_feed")
@SequenceGenerator(name = "seq", sequenceName = "meta_feed_id_seq")
public abstract class AbstractMetaFeed
{
    @Id @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    protected long uid;

    @Column(name = "meta_feed_name", nullable = false)
    protected String name;

    @Column(name = "java_class", nullable = false, insertable = false, updatable = false)
    private String javaClass;

    @ElementCollection(fetch = FetchType.LAZY)
    @MapKeyColumn(name = "mf_pref_name")
    @Column(name="mf_pref_value")
    @CollectionTable(name = "meta_feed_prefs",
            joinColumns = @JoinColumn(name = "meta_feed_id"))
    protected Map<String, String> preferences;


    public Map<String, String> getPreferences() {
        return preferences;
    }

    public void setPreferences(Map<String, String> preferences) {
        this.preferences = preferences;
    }

    public AbstractMetaFeed()
    {}

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

