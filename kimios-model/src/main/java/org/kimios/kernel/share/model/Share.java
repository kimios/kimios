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

package org.kimios.kernel.share.model;

import org.kimios.kernel.dms.model.DMEntity;
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.filetransfer.model.DataTransfer;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by farf on 13/02/16.
 */

@Entity
@Table(name = "dm_entity_share")
@SequenceGenerator(allocationSize = 1, name = "seq", sequenceName = "share_id_seq")
public class Share {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator =  "seq", strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(targetEntity = Document .class)
    @JoinColumn(name = "dm_entity_id", nullable = false)
    private DMEntity entity;

    @Column(name =  "creation_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate = new Date();

    @Column(name =  "update_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate = new Date();


    @Column(name = "creator_id", nullable = false)
    private String creatorId;

    @Column(name = "creator_source", nullable = false)
    private String creatorSource;


    @Column(name = "validator_id", nullable = true)
    private String validatorId;

    @Column(name = "validator_source", nullable = true)
    private String validatorSource;

    @Column(name = "share_user_id", nullable = true)
    private String targetUserId;

    @Column(name = "share_user_source", nullable = true)
    private String targetUserSource;


    @Column(name = "share_type")
    @Enumerated(EnumType.STRING)
    private ShareType type;

    @Column(name = "share_notify", nullable = false)
    private boolean notify = false;

    @Column(name = "share_is_read", nullable = false)
    private boolean read = false;

    @Column(name = "share_is_write", nullable = false)
    private boolean write = false;

    @Column(name = "share_is_fullaccess", nullable = false)
    private boolean fullAccess = false;


    @Column(name = "share_download_token", nullable = true)
    private String downloadToken;

    @Column(name = "share_download_count", nullable = true)
    private int downloadCount = 0;

    @Column(name = "expiration_date", nullable = true)
    private Date expirationDate;

    @Column(name = "share_status", nullable = false)
    private ShareStatus shareStatus;


    @OneToMany(mappedBy="share", fetch = FetchType.LAZY)
    private Set<DataTransfer> dataTransferSet;

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

    public Set<DataTransfer> getDataTransferSet() {
        return dataTransferSet;
    }

    public void setDataTransferSet(Set<DataTransfer> dataTransferSet) {
        this.dataTransferSet = dataTransferSet;
    }

    @Transient
    public org.kimios.kernel.ws.pojo.Share toPojo(){
        return new org.kimios.kernel.ws.pojo.Share(this);
    }
}
