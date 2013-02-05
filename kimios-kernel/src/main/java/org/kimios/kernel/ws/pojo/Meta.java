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

public class Meta
{
    private long uid;

    private String name;

    private long documentTypeUid;

    private Long metaFeedUid;

    private int metaType;

    public Meta()
    {

    }

    public Meta(long uid, String name, long documentTypeUid, Long metaFeedUid, int metaType)
    {
        this.uid = uid;
        this.name = name;
        this.documentTypeUid = documentTypeUid;
        this.metaFeedUid = metaFeedUid;
        this.metaType = metaType;
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

    public long getDocumentTypeUid()
    {
        return documentTypeUid;
    }

    public void setDocumentTypeUid(long documentTypeUid)
    {
        this.documentTypeUid = documentTypeUid;
    }

    public Long getMetaFeedUid()
    {
        return metaFeedUid;
    }

    public void setMetaFeedUid(Long metaFeedUid)
    {
        this.metaFeedUid = metaFeedUid;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}

