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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@IdClass(MetaValueBeanPK.class)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonIgnoreProperties("documentVersion")
public abstract class MetaValueBean implements MetaValue, Serializable
{
    @Id @Column(name = "meta_id")
    protected long metaUid;

    @ManyToOne(targetEntity = DocumentVersion.class)
    @JoinColumn(name = "document_version_id", nullable = false, insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    protected DocumentVersion documentVersion;

    @Id
    @Column(name = "document_version_id")
    protected long documentVersionUid;

    @ManyToOne(targetEntity = Meta.class)
    @JoinColumn(name = "meta_id", nullable = false, insertable = false, updatable = false)
    protected Meta meta;

    public MetaValueBean()
    {

    }

    public MetaValueBean(DocumentVersion version, long metaUid)
    {
        this.metaUid = metaUid;
        this.documentVersion = version;
        this.documentVersionUid = version.getUid();
    }

    public MetaValueBean(DocumentVersion version, Meta meta)
    {
        if (meta != null) {
            this.metaUid = meta.getUid();
        }
        this.meta = meta;
        this.documentVersion = version;
        this.documentVersionUid = version.getUid();
    }

    public Meta getMeta()
    {
        return meta;
    }

    public void setMeta(Meta meta)
    {
        if (meta != null) {
            metaUid = meta.getUid();
        }
        this.meta = meta;
    }

    public final long getDocumentVersionUid()
    {
        return documentVersionUid;
    }

    public final void setDocumentVersionUid(long documentVersionUid)
    {
        this.documentVersionUid = documentVersionUid;
    }

    public final long getMetaUid()
    {
        return metaUid;
    }

    public final void setMetaUid(long metaUid)
    {
        this.metaUid = metaUid;
    }

    public DocumentVersion getDocumentVersion()
    {
        return documentVersion;
    }

    public void setDocumentVersion(DocumentVersion documentVersion)
    {
        this.documentVersion = documentVersion;
        if (documentVersion != null) {
            this.documentVersionUid = documentVersion.getUid();
        }
    }
}

