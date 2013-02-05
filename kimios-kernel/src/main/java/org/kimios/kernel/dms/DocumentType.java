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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;

@Entity
@Table(name = "document_type")
@SequenceGenerator(allocationSize = 1, name = "seq", sequenceName = "dt_id_seq")
public class DocumentType implements Serializable
{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    private long uid;

    @Column(name = "type_name", nullable = false, length = 200)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "document_type_id", nullable = true)
    private DocumentType documentType;

    public DocumentType()
    {
    }

    public DocumentType(long uid, String name, DocumentType documentType)
    {
        this.uid = uid;
        this.name = name;
        this.documentType = documentType;
    }

    public DocumentType getDocumentType() throws ConfigException, DataSourceException
    {
        return this.documentType;
    }

    public void setDocumentType(DocumentType documentType)
    {
        this.documentType = documentType;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public long getUid()
    {
        return uid;
    }

    public void setUid(long uid)
    {
        this.uid = uid;
    }

    public org.kimios.kernel.ws.pojo.DocumentType toPojo() throws ConfigException, DataSourceException
    {
        long docTypeUid = -1;
        if (this.getDocumentType() != null) {
            docTypeUid = this.getDocumentType().getUid();
        }
        return new org.kimios.kernel.ws.pojo.DocumentType(this.uid, this.name, docTypeUid);
    }
}

