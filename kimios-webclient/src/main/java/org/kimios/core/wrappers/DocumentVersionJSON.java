/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kimios.core.wrappers;

import org.kimios.kernel.ws.pojo.DocumentVersion;

import java.text.SimpleDateFormat;

/**
 *
 * @author Farf
 */
public class DocumentVersionJSON {
    
    private long uid;
    private long length;
    private long documentUid;
    private long documentTypeUid;
    private String documentTypeName;
    private String creationDate;
    private String modificationDate;
    private String owner;
    private String ownerSource;

    public String getDocumentTypeName() {
        return documentTypeName;
    }

    public void setDocumentTypeName(String documentTypeName) {
        this.documentTypeName = documentTypeName;
    }

    public long getDocumentTypeUid() {
        return documentTypeUid;
    }

    public void setDocumentTypeUid(long documentTypeUid) {
        this.documentTypeUid = documentTypeUid;
    }

    public long getDocumentUid() {
        return documentUid;
    }

    public void setDocumentUid(long documentUid) {
        this.documentUid = documentUid;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(String modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerSource() {
        return ownerSource;
    }

    public void setOwnerSource(String ownerSource) {
        this.ownerSource = ownerSource;
    }
    
    
    
    public DocumentVersionJSON(DocumentVersion ver){
        this.uid = ver.getUid();
        this.documentTypeName = ver.getDocumentTypeName();
        this.documentUid = ver.getDocumentUid();
        this.length = ver.getLength();
        this.documentTypeUid = ver.getDocumentTypeUid();
        this.creationDate = new SimpleDateFormat("MM/dd/yyy hh:mm:ss").format(ver.getCreationDate().getTime());
        this.modificationDate = new SimpleDateFormat("MM/dd/yyy hh:mm:ss").format(ver.getModificationDate().getTime());
        this.owner = ver.getAuthor();
        this.ownerSource = ver.getAuthorSource();
    }

}

