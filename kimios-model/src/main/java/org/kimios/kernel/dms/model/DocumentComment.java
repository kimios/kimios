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
import java.util.Date;

@Entity
@Table(name = "document_comment")
@SequenceGenerator(allocationSize = 1, name = "seq", sequenceName = "doc_comment_id_seq")
public class DocumentComment
{
    @Id @Column(name = "id")
    @GeneratedValue(generator = "seq", strategy = GenerationType.AUTO)
    private long uid;

    @ManyToOne(targetEntity = DocumentVersion.class)
    @JoinColumn(name = "document_version_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DocumentVersion documentVersion;

    @Column(name = "document_version_id", nullable = false)
    private long documentVersionUid;

    @Column(name = "author_name", nullable = false)
    private String authorName;

    @Column(name = "author_source", nullable = false)
    private String authorSource;

    @Column(name = "comment_content", nullable = false, length = 5000, columnDefinition = "text")
    private String comment;

    @Column(name = "comment_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    public DocumentComment()
    {
    }

    public DocumentComment(long uid, long documentVersionUid, String authorName,
            String authorSource, String comment, Date date)
    {
        this.uid = uid;
        this.documentVersionUid = documentVersionUid;
        this.authorName = authorName;
        this.authorSource = authorSource;
        this.comment = comment;
        this.date = date;
    }

    public long getUid()
    {
        return uid;
    }

    public void setUid(long uid)
    {
        this.uid = uid;
    }

    public long getDocumentVersionUid()
    {
        return documentVersionUid;
    }

    public void setDocumentVersionUid(long documentVersionUid)
    {
        this.documentVersionUid = documentVersionUid;
    }

    public String getAuthorName()
    {
        return authorName;
    }

    public void setAuthorName(String authorName)
    {
        this.authorName = authorName;
    }

    public String getAuthorSource()
    {
        return authorSource;
    }

    public void setAuthorSource(String authorSource)
    {
        this.authorSource = authorSource;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public DocumentVersion getDocumentVersion()
    {
        return documentVersion;
    }

    public void setDocumentVersion(DocumentVersion version)
    {
        if (version != null) {
            this.documentVersionUid = version.getUid();
        }
        this.documentVersion = version;
    }

    public org.kimios.kernel.ws.pojo.DocumentComment toPojo()
    {
        return new org.kimios.kernel.ws.pojo.DocumentComment(this.uid, this.documentVersionUid, this.authorName,
                this.authorSource, this.comment, this.date);
    }
}

