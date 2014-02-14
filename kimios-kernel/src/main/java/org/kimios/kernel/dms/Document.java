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
package org.kimios.kernel.dms;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "document")
@PrimaryKeyJoinColumn(name = "id")
public class Document extends DMEntityImpl
{
    private long folderUid;

    private Folder folder;

    private String mimeType;

    private String extension;

    private Lock lock;

    private Set<Document> relatedDocuments = new HashSet<Document>();

    private Set<Document> parentsRelatedDocuments = new HashSet<Document>();

    private List<DocumentVersion> versionList = new ArrayList<DocumentVersion>();

    public Document()
    {
        this.type = DMEntityType.DOCUMENT;
    }

    public Document(long uid, String name, String owner, String ownerSource, Date creationDate, Date updateDate,
            long folderUid, String mimeType, String extension)
    {
        this.type = DMEntityType.DOCUMENT;
        this.uid = uid;
        this.name = name;
        this.owner = owner;
        this.ownerSource = ownerSource;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
        this.folderUid = folderUid;
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public Document(String name, String owner, String ownerSource, Date creationDate, Date updateDate, long folderUid,
            String mimeType, String extension)
    {
        this.type = DMEntityType.DOCUMENT;
        this.name = name;
        this.owner = owner;
        this.ownerSource = ownerSource;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
        this.folderUid = folderUid;
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public Document(long uid, String name, String owner, String ownerSource, Date creationDate, long folderUid,
            String mimeType, String extension)
    {
        this.type = DMEntityType.DOCUMENT;
        this.uid = uid;
        this.name = name;
        this.owner = owner;
        this.ownerSource = ownerSource;
        this.creationDate = creationDate;
        this.folderUid = folderUid;
        this.mimeType = mimeType;
        this.extension = extension;
    }

    @Column(name = "extension", nullable = true)
    public String getExtension()
    {
        return extension;
    }

    public void setExtension(String extension)
    {
        this.extension = extension;
    }

    @Column(name = "mime_type", nullable = true)
    public String getMimeType()
    {
        return mimeType;
    }

    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }

    @ManyToOne(targetEntity = Folder.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id", nullable = false, insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public Folder getFolder()
    {
        return folder;
    }

    public void setFolder(Folder folder)
    {
        this.folder = folder;
        if (folder != null) {
            this.folderUid = folder.getUid();
        }
    }

    @Column(name = "folder_id", nullable = false)
    public long getFolderUid()
    {
        return folderUid;
    }

    public void setFolderUid(long folderUid)
    {
        this.folderUid = folderUid;
    }

    @Transient
    public boolean isCheckedOut() throws ConfigException, DataSourceException
    {
        FactoryInstantiator fc = FactoryInstantiator.getInstance();
        Lock lock = fc.getLockFactory().getDocumentLock(this);
        return lock != null;
    }

    @Transient
    public Lock getCheckoutLock() throws ConfigException, DataSourceException
    {
        FactoryInstantiator fc = FactoryInstantiator.getInstance();
        return fc.getLockFactory().getDocumentLock(this);
    }

    @ManyToMany(targetEntity = Document.class)
    @JoinTable(name = "related_documents",
            joinColumns = { @JoinColumn(name = "document_id") },
            inverseJoinColumns = { @JoinColumn(name = "related_document_id") })
    public Set<Document> getRelatedDocuments()
    {
        return relatedDocuments;
    }

    public void setRelatedDocuments(Set<Document> relatedDocuments)
    {
        this.relatedDocuments = relatedDocuments;
    }

    @ManyToMany(targetEntity = Document.class, mappedBy = "relatedDocuments")
    public Set<Document> getParentsRelatedDocuments()
    {
        return parentsRelatedDocuments;
    }

    public void setParentsRelatedDocuments(Set<Document> parentsRelatedDocuments)
    {
        this.parentsRelatedDocuments = parentsRelatedDocuments;
    }

    @Transient
    @Override
    public org.kimios.kernel.ws.pojo.Document toPojo() throws ConfigException, DataSourceException
    {
        return FactoryInstantiator.getInstance()
                .getDocumentFactory()
                .getDocumentPojoFromId(this.getUid());
    }

    @OneToOne(mappedBy = "document")
    public Lock getLock()
    {
        return lock;
    }

    @OneToMany(targetEntity = DocumentVersion.class, mappedBy = "document")
    public List<DocumentVersion> getVersionList()
    {
        return versionList;
    }

    public void setVersionList(List<DocumentVersion> versionList)
    {
        this.versionList = versionList;
    }

    public void setLock(Lock lock)
    {
        this.lock = lock;
    }
}

