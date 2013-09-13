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
public class Document extends DMEntity implements Serializable
{
    final static long serialVersionUID = 1235489790;



    private Calendar versionCreationDate;

    private Calendar versionUpdateDate;


    private long folderUid;

    private String mimeType;

    private String extension;

    private Boolean checkedOut = false;

    private String checkoutUser;

    private String checkoutUserSource;

    private Calendar checkoutDate;

    private long length;

    private Long workflowStatusUid = 0L;

    private String workflowStatusName;

    private Boolean isOutOfWorkflow = true;

    private String documentTypeName;

    private Long documentTypeUid = 0L;

    private String path;

    private String addonDatas;

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
            boolean isOutOfWorkflow, String path, String addonDatas)
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
        this.addonDatas = addonDatas;
    }

    @Id
    @Column(name = "id")
    public long getUid()
    {
        return uid;
    }

    public void setUid(long uid)
    {
        this.uid = uid;
    }

    @Column(name = "dm_entity_name")
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Column(name = "dm_entity_owner")
    public String getOwner()
    {
        return owner;
    }


    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    @Column(name = "dm_entity_owner_source")
    public String getOwnerSource()
    {
        return ownerSource;
    }

    public void setOwnerSource(String ownerSource)
    {
        this.ownerSource = ownerSource;
    }

    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getCreationDate()
    {
        return creationDate;
    }

    public void setCreationDate(Calendar creationDate)
    {
        this.creationDate = creationDate;
    }

    @Column(name = "update_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getUpdateDate()
    {
        return updateDate;
    }

    public void setUpdateDate(Calendar updateDate)
    {
        this.updateDate = updateDate;
    }

    @Column(name = "folder_id")
    public long getFolderUid()
    {
        return folderUid;
    }

    public void setFolderUid(long folderUid)
    {
        this.folderUid = folderUid;
    }

    @Column(name = "mime_type")
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

    @Column(name = "extension")
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

    @Column(name = "locked")
    public Boolean getCheckedOut()
    {
        return checkedOut;
    }

    @Column(name = "lock_by")
    public String getCheckoutUser()
    {
        return checkoutUser;
    }

    public void setCheckoutUser(String checkoutUser)
    {
        this.checkoutUser = checkoutUser;
    }

    @Column(name = "lock_source")
    public String getCheckoutUserSource()
    {
        return checkoutUserSource;
    }

    public void setCheckoutUserSource(String checkoutUserSource)
    {
        this.checkoutUserSource = checkoutUserSource;
    }

    @Column(name = "lock_date")
    public Calendar getCheckoutDate()
    {
        return checkoutDate;
    }

    public void setCheckoutDate(Calendar checkoutDate)
    {
        this.checkoutDate = checkoutDate;
    }

    @Column(name = "file_length")
    public long getLength()
    {
        return length;
    }

    public void setLength(long length)
    {
        this.length = length;
    }

    @Column(name = "last_wfs")
    public Long getWorkflowStatusUid()
    {
        return workflowStatusUid;
    }

    public void setWorkflowStatusUid(Long workflowStatusUid)
    {
        this.workflowStatusUid = workflowStatusUid;
    }

    @Column(name = "status_name")
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

    @Column(name = "outofworkflow")
    public Boolean getOutOfWorkflow()
    {
        return isOutOfWorkflow;
    }

    public void setOutOfWorkflow(Boolean isOutOfWorkflow)
    {
        this.isOutOfWorkflow = isOutOfWorkflow;
    }

    @Column(name = "type_name")
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

    @Column(name = "document_type_id")
    public Long getDocumentTypeUid()
    {
        return documentTypeUid;
    }

    public void setDocumentTypeUid(Long documentTypeUid)
    {
        this.documentTypeUid = documentTypeUid;
    }

    @Column(name = "document_path")
    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }


    @Column(name = "version_creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getVersionCreationDate()
    {
        return versionCreationDate;
    }

    public void setVersionCreationDate(Calendar versionCreationDate)
    {
        this.versionCreationDate = versionCreationDate;
    }

    @Column(name = "version_update_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getVersionUpdateDate()
    {
        return versionUpdateDate;
    }

    public void setVersionUpdateDate(Calendar versionUpdateDate)
    {
        this.versionUpdateDate = versionUpdateDate;
    }


    @Column(name = "dm_entity_addon_data")
    public String getAddonDatas() {
        return addonDatas;
    }

    public void setAddonDatas(String addonDatas) {
        this.addonDatas = addonDatas;
    }

    @Override
    public int getType() {
        return 3;
    }

    @Override
    public String toString()
    {
        return "Document{" +
            "uid=" + uid +
            ", name='" + name + '\'' +
            ", owner='" + owner + '\'' +
            ", ownerSource='" + ownerSource + '\'' +
            ", creationDate=" + creationDate +
            ", updateDate=" + updateDate +
            ", versionCreationDate=" + versionCreationDate +
            ", versionUpdateDate=" + versionUpdateDate +
            ", folderUid=" + folderUid +
            ", mimeType='" + mimeType + '\'' +
            ", extension='" + extension + '\'' +
            ", checkedOut=" + checkedOut +
            ", checkoutUser='" + checkoutUser + '\'' +
            ", checkoutUserSource='" + checkoutUserSource + '\'' +
            ", checkoutDate=" + checkoutDate +
            ", length=" + length +
            ", workflowStatusUid=" + workflowStatusUid +
            ", workflowStatusName='" + workflowStatusName + '\'' +
            ", isOutOfWorkflow=" + isOutOfWorkflow +
            ", documentTypeName='" + documentTypeName + '\'' +
            ", documentTypeUid=" + documentTypeUid +
            ", path='" + path + '\'' +
                ", addonDatas='" + addonDatas + '\'' +
            '}';
    }
}
