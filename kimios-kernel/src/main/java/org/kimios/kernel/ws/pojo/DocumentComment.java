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

public class DocumentComment
{
    private long uid;

    private long documentVersionUid;

    private String authorName;

    private String authorSource;

    private String comment;

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
}

