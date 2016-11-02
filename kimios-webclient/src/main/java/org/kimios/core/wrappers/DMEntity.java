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
package org.kimios.core.wrappers;

import org.kimios.kernel.ws.pojo.*;

import java.util.Date;
import java.util.Map;

/**
 * @author Fabien Alin
 */
public class DMEntity {

    public static int WORKSPACE = 1;

    public static int FOLDER = 2;

    public static int DOCUMENT = 3;

    public static int SYMBOLIC_LINK = 7;

    private long uid;

    private int type;

    private String name;

    private String owner;

    private String ownerSource;

    private Date creationDate;

    private long parentUid;

    private int parentType;

    private String extension;

    private Date checkoutDate;

    private boolean checkedOut;

    private String checkoutUser;

    private String checkoutUserSource;

    private boolean outOfWorkflow;

    private String workflowStatusName;

    private long workflowStatusUid;

    private String documentTypeName;

    private long documentTypeUid;

    private long length;

    private String path;

    private Date updateDate;

    private long lastVersionId;

    private Date lastVersionCreationDate;
    private Date lastVersionUpdateDate;
    private String dmEntityAddonData;
    private String customVersion;
    private String customVersionPending;
    private String lastUpdateAuthor;
    private String lastUpdateAuthorSource;
    private String validatorUserName;
    private String validatorUserSource;

    private Map<String, MetaValue> metaDatas;

    private DMEntity targetEntity;


    private long virtualFolderCount;


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

    public DMEntity(org.kimios.kernel.ws.pojo.DMEntity entity){


        this.type = entity.getType();
        this.uid = entity.getUid();
        this.name = entity.getName();
        this.creationDate = entity.getCreationDate().getTime();
        this.owner = entity.getOwner();
        this.ownerSource = entity.getOwnerSource();
        this.path = entity.getPath();

        if(entity instanceof Workspace){
            this.parentType = -1;
            this.parentUid = -1;
        }
        if(entity instanceof Folder){
            this.parentType = ((Folder) entity).getParentType();
            this.parentUid = ((Folder) entity).getParentUid();
        }
        if(entity instanceof org.kimios.kernel.ws.pojo.Document){
            this.parentUid = ((org.kimios.kernel.ws.pojo.Document)entity).getFolderUid();
            this.extension = ((org.kimios.kernel.ws.pojo.Document)entity).getExtension();
            this.checkedOut = ((org.kimios.kernel.ws.pojo.Document)entity).getCheckedOut();
            this.checkoutDate = ( ((org.kimios.kernel.ws.pojo.Document)entity).getCheckoutDate() != null ? ((org.kimios.kernel.ws.pojo.Document)entity).getCheckoutDate().getTime() : null );
            this.checkoutUser = ((org.kimios.kernel.ws.pojo.Document)entity).getCheckoutUser();
            this.checkoutUserSource = ((org.kimios.kernel.ws.pojo.Document)entity).getCheckoutUserSource();
            this.outOfWorkflow = ((org.kimios.kernel.ws.pojo.Document)entity).getOutOfWorkflow() != null ? ((org.kimios.kernel.ws.pojo.Document)entity).getOutOfWorkflow() : true;
            this.workflowStatusName = ((org.kimios.kernel.ws.pojo.Document)entity).getWorkflowStatusName();
            this.workflowStatusUid = ((org.kimios.kernel.ws.pojo.Document)entity).getWorkflowStatusUid() != null ? ((org.kimios.kernel.ws.pojo.Document)entity).getWorkflowStatusUid() : 0;
            this.documentTypeName = ((org.kimios.kernel.ws.pojo.Document)entity).getDocumentTypeName();
            this.documentTypeUid = ((org.kimios.kernel.ws.pojo.Document)entity).getDocumentTypeUid() != null ? ((org.kimios.kernel.ws.pojo.Document)entity).getDocumentTypeUid() : 0;
            this.length = ((org.kimios.kernel.ws.pojo.Document)entity).getLength();
            this.path = ((org.kimios.kernel.ws.pojo.Document)entity).getPath();

            this.lastVersionCreationDate = ((org.kimios.kernel.ws.pojo.Document)entity).getVersionCreationDate().getTime();
            this.lastVersionUpdateDate = ((org.kimios.kernel.ws.pojo.Document)entity).getVersionUpdateDate().getTime();

            this.dmEntityAddonData = ((org.kimios.kernel.ws.pojo.Document)entity).getAddonDatas();

            this.metaDatas = ((org.kimios.kernel.ws.pojo.Document)entity).getMetaDatas();

            this.lastVersionId = ((org.kimios.kernel.ws.pojo.Document)entity).getLastVersionId();
            this.customVersion = ((org.kimios.kernel.ws.pojo.Document)entity).getCustomVersion();
            this.customVersionPending = ((org.kimios.kernel.ws.pojo.Document)entity).getCustomVersionPending();
            this.lastUpdateAuthor = ((org.kimios.kernel.ws.pojo.Document)entity).getLastUpdateAuthor();
            this.lastUpdateAuthorSource = ((org.kimios.kernel.ws.pojo.Document)entity).getLastUpdateAuthorSource();
            this.validatorUserName = ((org.kimios.kernel.ws.pojo.Document)entity).getValidatorUserName();
            this.validatorUserSource = ((org.kimios.kernel.ws.pojo.Document)entity).getValidatorUserSource();
        }
        if(entity instanceof SymbolicLink){
            this.parentUid = ((SymbolicLink) entity).getParentUid();
            this.parentType = ((SymbolicLink) entity).getParentType();
            this.targetEntity = new DMEntity(((SymbolicLink) entity).getTarget());
        }
    }

    public DMEntity( long virtualEntityCount, String virtualPath, String virtualEntityName, long uid )
    {
        this.virtualFolderCount = virtualEntityCount;
        this.path = virtualPath;
        this.name = virtualEntityName;

        this.type = 2;
        this.owner = "search";
        this.ownerSource = "search";
        this.updateDate = new Date();
        this.creationDate = this.updateDate;
        this.uid = uid;
    }


    public String getOwnerSource()
    {
        return ownerSource;
    }

    public void setOwnerSource( String authenticationSource )
    {
        this.ownerSource = authenticationSource;
    }

    public Date getCreationDate()
    {
        return creationDate;
    }

    public void setCreationDate( Date creationDate )
    {
        this.creationDate = creationDate;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getOwner()
    {
        return owner;
    }

    public void setOwner( String owner )
    {
        this.owner = owner;
    }

    public int getType()
    {
        return type;
    }

    public void setType( int type )
    {
        this.type = type;
    }

    public long getUid()
    {
        return uid;
    }

    public void setUid( long uid )
    {
        this.uid = uid;
    }

    public String getExtension()
    {
        return extension;
    }

    public void setExtension( String extension )
    {
        this.extension = extension;
    }

    public boolean isCheckedOut()
    {
        return checkedOut;
    }

    public void setCheckedOut( boolean checkedOut )
    {
        this.checkedOut = checkedOut;
    }

    public Date getCheckoutDate()
    {
        return checkoutDate;
    }

    public void setCheckoutDate( Date checkoutDate )
    {
        this.checkoutDate = checkoutDate;
    }

    public String getCheckoutUser()
    {
        return checkoutUser;
    }

    public void setCheckoutUser( String checkoutUser )
    {
        this.checkoutUser = checkoutUser;
    }

    public String getCheckoutUserSource()
    {
        return checkoutUserSource;
    }

    public void setCheckoutUserSource( String checkoutUserSource )
    {
        this.checkoutUserSource = checkoutUserSource;
    }

    public String getDocumentTypeName()
    {
        return documentTypeName;
    }

    public void setDocumentTypeName( String documentTypeName )
    {
        this.documentTypeName = documentTypeName;
    }

    public long getDocumentTypeUid()
    {
        return documentTypeUid;
    }

    public void setDocumentTypeUid( long documentTypeUid )
    {
        this.documentTypeUid = documentTypeUid;
    }

    public boolean isOutOfWorkflow()
    {
        return outOfWorkflow;
    }

    public void setOutOfWorkflow( boolean outOfWorkflow )
    {
        this.outOfWorkflow = outOfWorkflow;
    }

    public int getParentType()
    {
        return parentType;
    }

    public void setParentType( int parentType )
    {
        this.parentType = parentType;
    }

    public long getParentUid()
    {
        return parentUid;
    }

    public void setParentUid( long parentUid )
    {
        this.parentUid = parentUid;
    }

    public long getWorkflowStatusUid()
    {
        return workflowStatusUid;
    }

    public void setWorkflowStatusUid( long workflowStatusUid )
    {
        this.workflowStatusUid = workflowStatusUid;
    }

    public String getWorkflowStatusName()
    {
        return workflowStatusName;
    }

    public void setWorkflowStatusName( String workflowStatusName )
    {
        this.workflowStatusName = workflowStatusName;
    }

    public long getLength()
    {
        return length;
    }

    public void setLength( long length )
    {
        this.length = length;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath( String path )
    {
        this.path = path;
    }

    public Date getUpdateDate()
    {
        return updateDate;
    }

    public void setUpdateDate( Date updateDate )
    {
        this.updateDate = updateDate;
    }

    public Date getLastVersionCreationDate() {
        return lastVersionCreationDate;
    }

    public void setLastVersionCreationDate(Date lastVersionCreationDate) {
        this.lastVersionCreationDate = lastVersionCreationDate;
    }

    public Date getLastVersionUpdateDate() {
        return lastVersionUpdateDate;
    }

    public void setLastVersionUpdateDate(Date lastVersionUpdateDate) {
        this.lastVersionUpdateDate = lastVersionUpdateDate;
    }

    public String getDmEntityAddonData() {
        return dmEntityAddonData;
    }

    public void setDmEntityAddonData(String dmEntityAddonData) {
        this.dmEntityAddonData = dmEntityAddonData;
    }

    public DMEntity getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(DMEntity targetEntity) {
        this.targetEntity = targetEntity;
    }

    public Map<String, MetaValue> getMetaDatas() {
        return metaDatas;
    }

    public void setMetaDatas(Map<String, MetaValue> metaDatas) {
        this.metaDatas = metaDatas;
    }

    public long getLastVersionId() {
        return lastVersionId;
    }

    public void setLastVersionId(long lastVersionId) {
        this.lastVersionId = lastVersionId;
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
}

