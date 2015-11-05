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

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "meta")
@SequenceGenerator(allocationSize = 1, name = "seq", sequenceName = "meta_id_seq")
public class Meta implements Serializable
{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    private long uid;

    @Column(name = "meta_name", nullable = false)
    private String name;

    @ManyToOne(targetEntity = DocumentType.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DocumentType documentType;

    @Column(name = "document_type_id", nullable = false)
    private long documentTypeUid;

    @Column(name = "meta_type", nullable = false)
    private int metaType;

    @ManyToOne(targetEntity = MetaFeedImpl.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "meta_feed_id", nullable = true)
    private MetaFeedImpl metaFeedBean;

    @Column(name = "mandatory", nullable = false)
    private boolean mandatory = false;

    @Column(name= "position", nullable = true)
    private Integer position;



    public Meta()
    {}

    public Meta(long uid, String name, long documentTypeUid, MetaFeedImpl metaFeedBean, int metaType)
    {
        this.uid = uid;
        this.name = name;
        this.documentTypeUid = documentTypeUid;
        this.metaFeedBean = metaFeedBean;
        this.metaType = metaType;
    }

    public Meta(long uid, String name, long documentTypeUid, MetaFeedImpl metaFeedBean, int metaType, boolean mandatory)
    {
        this.uid = uid;
        this.name = name;
        this.documentTypeUid = documentTypeUid;
        this.metaFeedBean = metaFeedBean;
        this.metaType = metaType;
        this.mandatory = mandatory;
    }

    public Meta(long uid, String name, long documentTypeUid, MetaFeedImpl metaFeedBean, int metaType, boolean mandatory, Integer position)
    {
        this.uid = uid;
        this.name = name;
        this.documentTypeUid = documentTypeUid;
        this.metaFeedBean = metaFeedBean;
        this.metaType = metaType;
        this.mandatory = mandatory;
        this.position = position;
    }

    public int getMetaType()
    {
        return metaType;
    }

    public void setMetaType(int metaType)
    {
        this.metaType = metaType;
    }

    public long getUid()
    {
        return uid;
    }

    public void setUid(long uid)
    {
        this.uid = uid;
    }

    public DocumentType getDocumentType()
    {
        return this.documentType;
    }

    public void setDocumentType(DocumentType documentType)
    {
        if (documentType != null) {
            this.documentTypeUid = documentType.getUid();
        }

        this.documentType = documentType;
    }

    public MetaFeedImpl getMetaFeed()
    {
        return metaFeedBean;
    }

    public long getDocumentTypeUid()
    {
        return documentTypeUid;
    }

    public void setDocumentTypeUid(long documentTypeUid)
    {
        this.documentTypeUid = documentTypeUid;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public MetaFeedImpl getMetaFeedBean()
    {
        return metaFeedBean;
    }

    public void setMetaFeedBean(MetaFeedImpl metaFeedBean)
    {
        this.metaFeedBean = metaFeedBean;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public org.kimios.kernel.ws.pojo.Meta toPojo()
    {
        long metaFeedUid = -1;
        if (this.metaFeedBean != null) {
            metaFeedUid = this.metaFeedBean.getUid();
        }
        return new org.kimios.kernel.ws.pojo.Meta(this.uid, this.name, this.documentTypeUid, metaFeedUid,
                this.metaType, this.mandatory, this.position);
    }

    public boolean equals(Object o)
    {
        if (o instanceof Meta) {
            Meta new_name = (Meta) o;
            return this.uid == new_name.getUid();
        } else {
            return false;
        }
    }
}

