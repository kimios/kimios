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

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity(name = "DocumentPojo")
@Table(name = "document_pojo")
public class Document implements Serializable
{
    final static long serialVersionUID = 1235489790;

    @Id
    @Column(name = "id")
    private long uid;

    @Column(name = "dm_entity_name")
    private String name;

    @Column(name = "dm_entity_owner")
    private String owner;

    @Column(name = "dm_entity_owner_source")
    private String ownerSource;

    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar creationDate;

    @Column(name = "update_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar updateDate;

    @Column(name = "version_creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar versionCreationDate;

    @Column(name = "version_update_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar versionUpdateDate;

    @Column(name = "folder_id")
    private long folderUid;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "extension")
    private String extension;

    @Column(name = "locked")
    private Boolean checkedOut = false;

    @Column(name = "lock_by")
    private String checkoutUser;

    @Column(name = "lock_source")
    private String checkoutUserSource;

    @Column(name = "lock_date")
    private Calendar checkoutDate;

    @Column(name = "file_length")
    private long length;

    @Column(name = "last_wfs")
    private Long workflowStatusUid = 0L;

    @Column(name = "status_name")
    private String workflowStatusName;

    @Column(name = "outofworkflow")
    private Boolean isOutOfWorkflow = true;

    @Column(name = "type_name")
    private String documentTypeName;

    @Column(name = "document_type_id")
    private Long documentTypeUid = 0L;

    @Column(name = "document_path")
    private String path;

    public Document()
    {
    }

    public Document(long uid, String name, String owner, String ownerSource,
            Date creationDate, Date updateDate, Date versionCreationDate, Date versionUpdateDate, long folderUid,
            String mimeType,
            String extension, long documentTypeUid, String documentTypeName,
            boolean checkedOut, String checkoutUser,
            String checkoutUserSource, Date checkoutDate, long length,
            long workflowStatusUid, String workflowStatusName,
            boolean isOutOfWorkflow, String path)
    {
        this.uid = uid;
        this.name = name;
        this.owner = owner;
        this.ownerSource = ownerSource;
        this.creationDate = Calendar.getInstance();
        this.creationDate.setTime(creationDate);
        this.updateDate = Calendar.getInstance();
        this.updateDate.setTime(updateDate);
        this.versionCreationDate = Calendar.getInstance();
        this.versionCreationDate.setTime(versionCreationDate);
        this.versionUpdateDate = Calendar.getInstance();
        this.versionUpdateDate.setTime(versionUpdateDate);
        this.folderUid = folderUid;
        this.mimeType = mimeType;
        this.extension = extension;
        this.checkedOut = checkedOut;
        this.checkoutUser = checkoutUser;
        this.checkoutUserSource = checkoutUserSource;

        this.checkoutDate = Calendar.getInstance();
        this.checkoutDate.setTime(checkoutDate);
        this.length = length;
        this.workflowStatusName = workflowStatusName;
        this.workflowStatusUid = workflowStatusUid;
        this.isOutOfWorkflow = isOutOfWorkflow;
        this.documentTypeName = documentTypeName;
        this.documentTypeUid = documentTypeUid;
        this.path = path;
    }

    public long getUid()
    {
        return uid;
    }

    public void setUid(long uid)
    {
        this.uid = uid;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public String getOwnerSource()
    {
        return ownerSource;
    }

    public void setOwnerSource(String ownerSource)
    {
        this.ownerSource = ownerSource;
    }

    public Calendar getCreationDate()
    {
        return creationDate;
    }

    public void setCreationDate(Calendar creationDate)
    {
        this.creationDate = creationDate;
    }

    public Calendar getUpdateDate()
    {
        return updateDate;
    }

    public void setUpdateDate(Calendar updateDate)
    {
        this.updateDate = updateDate;
    }

    public long getFolderUid()
    {
        return folderUid;
    }

    public void setFolderUid(long folderUid)
    {
        this.folderUid = folderUid;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public void setMimeType(String mimeType)
    {
        if (mimeType == null) {
            mimeType = "";
        }
        this.mimeType = mimeType;
    }

    public String getExtension()
    {
        return extension;
    }

    public void setExtension(String extension)
    {
        this.extension = extension;
    }

    public void setCheckedOut(Boolean checkedOut)
    {
        this.checkedOut = checkedOut;
    }

    public Boolean getCheckedOut()
    {
        return checkedOut;
    }

    public String getCheckoutUser()
    {
        return checkoutUser;
    }

    public void setCheckoutUser(String checkoutUser)
    {
        this.checkoutUser = checkoutUser;
    }

    public String getCheckoutUserSource()
    {
        return checkoutUserSource;
    }

    public void setCheckoutUserSource(String checkoutUserSource)
    {
        this.checkoutUserSource = checkoutUserSource;
    }

    public Calendar getCheckoutDate()
    {
        return checkoutDate;
    }

    public void setCheckoutDate(Calendar checkoutDate)
    {
        this.checkoutDate = checkoutDate;
    }

    public long getLength()
    {
        return length;
    }

    public void setLength(long length)
    {
        this.length = length;
    }

    public Long getWorkflowStatusUid()
    {
        return workflowStatusUid;
    }

    public void setWorkflowStatusUid(Long workflowStatusUid)
    {
        this.workflowStatusUid = workflowStatusUid;
    }

    public String getWorkflowStatusName()
    {
        return workflowStatusName;
    }

    public void setWorkflowStatusName(String workflowStatusName)
    {
        if (workflowStatusName == null) {
            workflowStatusName = "";
        }
        this.workflowStatusName = workflowStatusName;
    }

    public Boolean getOutOfWorkflow()
    {
        return isOutOfWorkflow;
    }

    public void setOutOfWorkflow(Boolean isOutOfWorkflow)
    {
        this.isOutOfWorkflow = isOutOfWorkflow;
    }

    public String getDocumentTypeName()
    {
        return documentTypeName;
    }

    public void setDocumentTypeName(String documentTypeName)
    {
        if (documentTypeName == null) {
            documentTypeName = "";
        }
        this.documentTypeName = documentTypeName;
    }

    public Long getDocumentTypeUid()
    {
        return documentTypeUid;
    }

    public void setDocumentTypeUid(Long documentTypeUid)
    {
        this.documentTypeUid = documentTypeUid;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public Calendar getVersionCreationDate()
    {
        return versionCreationDate;
    }

    public void setVersionCreationDate(Calendar versionCreationDate)
    {
        this.versionCreationDate = versionCreationDate;
    }

    public Calendar getVersionUpdateDate()
    {
        return versionUpdateDate;
    }

    public void setVersionUpdateDate(Calendar versionUpdateDate)
    {
        this.versionUpdateDate = versionUpdateDate;
    }
}
