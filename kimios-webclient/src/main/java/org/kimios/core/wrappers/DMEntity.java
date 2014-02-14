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

import org.kimios.kernel.ws.pojo.Document;
import org.kimios.kernel.ws.pojo.Folder;
import org.kimios.kernel.ws.pojo.SymbolicLink;
import org.kimios.kernel.ws.pojo.Workspace;

import java.util.Date;

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

    private Date lastVersionCreationDate;
    private Date lastVersionUpdateDate;
    private String dmEntityAddonData;

    private DMEntity targetEntity;


    private long virtualFolderCount;


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
        if(entity instanceof Document){
            this.parentUid = ((Document)entity).getFolderUid();
            this.extension = ((Document)entity).getExtension();
            this.checkedOut = ((Document)entity).getCheckedOut();
            this.checkoutDate = ( ((Document)entity).getCheckoutDate() != null ? ((Document)entity).getCheckoutDate().getTime() : null );
            this.checkoutUser = ((Document)entity).getCheckoutUser();
            this.checkoutUserSource = ((Document)entity).getCheckoutUserSource();
            this.outOfWorkflow = ((Document)entity).getOutOfWorkflow() != null ? ((Document)entity).getOutOfWorkflow() : true;
            this.workflowStatusName = ((Document)entity).getWorkflowStatusName();
            this.workflowStatusUid = ((Document)entity).getWorkflowStatusUid() != null ? ((Document)entity).getWorkflowStatusUid() : 0;
            this.documentTypeName = ((Document)entity).getDocumentTypeName();
            this.documentTypeUid = ((Document)entity).getDocumentTypeUid() != null ? ((Document)entity).getDocumentTypeUid() : 0;
            this.length = ((Document)entity).getLength();
            this.path = ((Document)entity).getPath();

            this.lastVersionCreationDate = ((Document)entity).getVersionCreationDate().getTime();
            this.lastVersionUpdateDate = ((Document)entity).getVersionUpdateDate().getTime();

            this.dmEntityAddonData = ((Document)entity).getAddonDatas();
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
}

