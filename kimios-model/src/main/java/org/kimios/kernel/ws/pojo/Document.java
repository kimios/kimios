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
package org.kimios.kernel.ws.pojo;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Document extends DMEntity implements Serializable
{
    final static long serialVersionUID = 1235489790;


    private Long lastVersionId;

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

    private String customVersion;

    private String customVersionPending;

    private String lastUpdateAuthor;

    private String lastUpdateAuthorSource;

    private Long workflowStatusUid = 0L;

    private String workflowStatusName;

    private String validatorUserName;

    private String validatorUserSource;

    private String workflowName;

    private Boolean isOutOfWorkflow = true;

    private String documentTypeName;

    private Long documentTypeUid = 0L;

    private Float indexScore;

    public Document()
    {
        this.type = 3;
    }

    public Document(long uid, String name, String owner, String ownerSource,
            Date creationDate, Date updateDate, long lastVersionId, Date versionCreationDate, Date versionUpdateDate, long folderUid,
            String mimeType,
            String extension, long documentTypeUid, String documentTypeName,
            boolean checkedOut, String checkoutUser,
            String checkoutUserSource, Date checkoutDate, long length,
            long workflowStatusUid, String workflowStatusName,
            boolean isOutOfWorkflow, String path, String addonDatas,
                    String customVersion, String customVersionPending,
                    String lastUpdateAuthor, String lastUpdateAuthorSource,
                    String validatorUserName, String validatorUserSource,
                    String workflowName)
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
        this.lastVersionId = lastVersionId;
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

        this.customVersion = customVersion;
        this.customVersionPending = customVersionPending;

        this.lastUpdateAuthor = lastUpdateAuthor;
        this.lastUpdateAuthorSource = lastUpdateAuthorSource;

        this.validatorUserName = validatorUserName;
        this.validatorUserSource = validatorUserSource;

        this.workflowName = workflowName;

        this.type = 3;
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

    public Long getLastVersionId() {
        return lastVersionId;
    }

    public void setLastVersionId(Long lastVersionId) {
        this.lastVersionId = lastVersionId;
    }

    public Float getIndexScore() {
        return indexScore;
    }

    public void setIndexScore(Float indexScore) {
        this.indexScore = indexScore;
    }

    public String getCustomVersion() {
        return customVersion;
    }

    public void setCustomVersion(String customVersion) {
        this.customVersion = customVersion;
    }

    public String getCustomVersionPending() {
        return customVersionPending;
    }

    public void setCustomVersionPending(String customVersionPending) {
        this.customVersionPending = customVersionPending;
    }

    @Override
    public String toString() {
        return "Document{" +
                "lastVersionId=" + lastVersionId +
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
                ", customVersion='" + customVersion + '\'' +
                ", customVersionPending='" + customVersionPending + '\'' +
                ", lastUpdateAuthor='" + lastUpdateAuthor + '\'' +
                ", lastUpdateAuthorSource='" + lastUpdateAuthorSource + '\'' +
                ", workflowStatusUid=" + workflowStatusUid +
                ", workflowStatusName='" + workflowStatusName + '\'' +
                ", validatorUserName='" + validatorUserName + '\'' +
                ", validatorUserSource='" + validatorUserSource + '\'' +
                ", isOutOfWorkflow=" + isOutOfWorkflow +
                ", documentTypeName='" + documentTypeName + '\'' +
                ", documentTypeUid=" + documentTypeUid +
                ", indexScore=" + indexScore +
                '}';
    }

    public String getLastUpdateAuthor() {
        return lastUpdateAuthor;
    }

    public void setLastUpdateAuthor(String lastUpdateAuthor) {
        this.lastUpdateAuthor = lastUpdateAuthor;
    }

    public String getLastUpdateAuthorSource() {
        return lastUpdateAuthorSource;
    }

    public void setLastUpdateAuthorSource(String lastUpdateAuthorSource) {
        this.lastUpdateAuthorSource = lastUpdateAuthorSource;
    }

    public String getValidatorUserName() {
        return validatorUserName;
    }

    public void setValidatorUserName(String validatorUserName) {
        this.validatorUserName = validatorUserName;
    }

    public String getValidatorUserSource() {
        return validatorUserSource;
    }

    public void setValidatorUserSource(String validatorUserSource) {
        this.validatorUserSource = validatorUserSource;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }
}
