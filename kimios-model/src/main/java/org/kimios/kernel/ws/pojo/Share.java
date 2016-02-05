/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
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

import org.kimios.kernel.share.model.ShareStatus;
import org.kimios.kernel.share.model.ShareType;

import java.util.Date;

public class Share {


    public Long id;
    public Date creationDate;
    public Date updateDate;
    public String creatorId;
    public String creatorSource;
    public String validatorId;
    public String validatorSource;
    public String targetUserId;
    public String targetUserSource;
    public ShareType type;
    public boolean notify = false;
    public boolean read = false;
    public boolean write = false;
    public boolean fullAccess = false;
    public String downloadToken;
    public int downloadCount = 0;
    public Date expirationDate;
    public ShareStatus shareStatus;
    public DMEntity entity;


    public Share(){}
    public Share(org.kimios.kernel.share.model.Share share) {

        this.id = share.getId();
        this.creationDate = share.getCreationDate();
        this.updateDate = share.getUpdateDate();
        this.creatorSource = share.getCreatorSource();
        this.creatorId = share.getCreatorId();
        this.targetUserId = share.getTargetUserId();
        this.targetUserSource = share.getTargetUserSource();
        this.read = share.isRead();
        this.write = share.isWrite();
        this.fullAccess = share.isFullAccess();
        this.notify = share.isNotify();
        this.shareStatus = share.getShareStatus();
        this.type = share.getType();
        this.validatorId = share.getValidatorId();
        this.validatorSource = share.getValidatorSource();
        this.expirationDate = share.getExpirationDate();


        this.entity = share.getEntity().toPojo();


    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorSource() {
        return creatorSource;
    }

    public void setCreatorSource(String creatorSource) {
        this.creatorSource = creatorSource;
    }

    public String getValidatorId() {
        return validatorId;
    }

    public void setValidatorId(String validatorId) {
        this.validatorId = validatorId;
    }

    public String getValidatorSource() {
        return validatorSource;
    }

    public void setValidatorSource(String validatorSource) {
        this.validatorSource = validatorSource;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getTargetUserSource() {
        return targetUserSource;
    }

    public void setTargetUserSource(String targetUserSource) {
        this.targetUserSource = targetUserSource;
    }

    public ShareType getType() {
        return type;
    }

    public void setType(ShareType type) {
        this.type = type;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public boolean isFullAccess() {
        return fullAccess;
    }

    public void setFullAccess(boolean fullAccess) {
        this.fullAccess = fullAccess;
    }

    public String getDownloadToken() {
        return downloadToken;
    }

    public void setDownloadToken(String downloadToken) {
        this.downloadToken = downloadToken;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public ShareStatus getShareStatus() {
        return shareStatus;
    }

    public void setShareStatus(ShareStatus shareStatus) {
        this.shareStatus = shareStatus;
    }

    public DMEntity getEntity() {
        return entity;
    }

    public void setEntity(DMEntity entity) {
        this.entity = entity;
    }
}