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
package org.kimios.kernel.ws.pojo;

import java.util.Date;

public class DocumentVersion
{
    private long uid;

    private String author;

    private String authorSource;

    private Date creationDate;

    private Date modificationDate;

    private long documentUid;

    private long length;

    private long documentTypeUid;

    private String documentTypeName;

    private String hashMd5;

    private String hashSha;

    public DocumentVersion()
    {
    }

    public DocumentVersion(long uid, String author, String authorSource,
            Date creationDate, Date modificationDate, long documentUid,
            long length, long documentTypeUid, String documentTypeName, String hashMd5, String hashSha)
    {
        this.uid = uid;
        this.author = author;
        this.authorSource = authorSource;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
        this.documentUid = documentUid;
        this.length = length;
        this.documentTypeUid = documentTypeUid;
        this.documentTypeName = documentTypeName;
        this.hashMd5 = hashMd5;
        this.hashSha = hashSha;
    }

    public long getUid()
    {
        return uid;
    }

    public void setUid(long uid)
    {
        this.uid = uid;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getAuthorSource()
    {
        return authorSource;
    }

    public void setAuthorSource(String authorSource)
    {
        this.authorSource = authorSource;
    }

    public Date getCreationDate()
    {
        return creationDate;
    }

    public void setCreationDate(Date creationDate)
    {
        this.creationDate = creationDate;
    }

    public Date getModificationDate()
    {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate)
    {
        this.modificationDate = modificationDate;
    }

    public long getDocumentUid()
    {
        return documentUid;
    }

    public void setDocumentUid(long documentUid)
    {
        this.documentUid = documentUid;
    }

    public long getLength()
    {
        return length;
    }

    public void setLength(long length)
    {
        this.length = length;
    }

    public long getDocumentTypeUid()
    {
        return documentTypeUid;
    }

    public void setDocumentTypeUid(long documentTypeUid)
    {
        this.documentTypeUid = documentTypeUid;
    }

    public String getDocumentTypeName()
    {
        return documentTypeName;
    }

    public void setDocumentTypeName(String documentTypeName)
    {
        this.documentTypeName = documentTypeName;
    }

    public String getHashMd5()
    {
        return hashMd5;
    }

    public void setHashMd5(String hashMd5)
    {
        this.hashMd5 = hashMd5;
    }

    public String getHashSha()
    {
        return hashSha;
    }

    public void setHashSha(String hashSha)
    {
        this.hashSha = hashSha;
    }
}

