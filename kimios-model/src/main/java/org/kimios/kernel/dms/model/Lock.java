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
@Table(name = "checkout")
public class Lock
{
    @Id
    @Column(name = "document_id", insertable = false, updatable = false)
    private long uid;

    @MapsId
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "document_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Document document;

    @Column(name = "username", nullable = false)
    private String user;

    @Column(name = "user_source", nullable = false)
    private String userSource;

    @Column(name = "checkout_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    public Lock()
    {
    }

    public Lock(Document document, String user, String userSource, Date date)
    {
        this.document = document;
        this.user = user;
        this.userSource = userSource;
        this.date = date;
    }

    public String getUserSource()
    {
        return userSource;
    }

    public void setUserSource(String userSource)
    {
        this.userSource = userSource;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public Document getDocument()
    {
        return document;
    }

    public void setDocument(Document document)
    {
        if (document != null) {
            this.uid = document.getUid();
        }
        this.document = document;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public long getUid()
    {
        return uid;
    }

    public void setUid(long uid)
    {
        this.uid = uid;
    }
}

