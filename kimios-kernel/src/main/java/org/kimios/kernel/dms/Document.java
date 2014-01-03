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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.security.SecurityAgent;
import org.kimios.kernel.ws.pojo.*;

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
        FactoryInstantiator fc = FactoryInstantiator.getInstance();
        DocumentVersion version = fc.getDocumentVersionFactory().getLastDocumentVersion(this);
        DocumentType docType = (version != null ? version.getDocumentType() : null);
        String docTypeName = "";
        long docTypeUid = -1;
        if (docType != null) {
            docTypeName = docType.getName();
            docTypeUid = docType.getUid();
        }
        Lock lock = fc.getLockFactory().getDocumentLock(this);
        String checkoutUser = "";
        String checkoutUserSource = "";
        Date checkOutDate = new Date();
        boolean isCheckedOut = false;
        if (lock != null) {
            checkoutUser = lock.getUser();
            checkoutUserSource = lock.getUserSource();
            checkOutDate = lock.getDate();
            isCheckedOut = true;
        }
        DocumentWorkflowStatus dws = fc.getDocumentWorkflowStatusFactory().getLastDocumentWorkflowStatus(this.getUid());
        long wsUid = -1;
        String wsName = "";
        boolean isOutOfWorkflow = SecurityAgent.getInstance().isDocumentOutOfWorkflow(this);
        if (dws != null) {
            wsUid = dws.getWorkflowStatusUid();
            wsName = fc.getWorkflowStatusFactory().getWorkflowStatus(dws.getWorkflowStatusUid()).getName();
        }
        return new org.kimios.kernel.ws.pojo.Document(this.uid, this.name, this.owner,
                this.ownerSource, this.creationDate, this.updateDate,version.getUid(), version.getCreationDate(),
                version.getModificationDate(), this.folderUid, this.mimeType,
                this.extension, docTypeUid, docTypeName, isCheckedOut, checkoutUser,
                checkoutUserSource, checkOutDate, version.getLength(), wsUid, wsName, isOutOfWorkflow, this.path, this.addOnDatas);
    }

    @OneToOne(mappedBy = "document")
    public Lock getLock()
    {
        return lock;
    }

    @OneToMany(targetEntity = DocumentVersion.class, mappedBy = "document", cascade = CascadeType.ALL)
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

