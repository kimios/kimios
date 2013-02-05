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
package org.kimios.core.wrappers;

import org.kimios.kernel.ws.pojo.*;
import org.kimios.kernel.ws.pojo.Document;

import java.util.Date;

/**
 *
 * @author Fabien Alin
 */
public class DMEntity {
    
    public static int WORKSPACE = 1;
    public static int FOLDER = 2;
    public static int DOCUMENT = 3;
    
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
    
    public DMEntity(Workspace w){
        this.type = DMEntity.WORKSPACE;
        this.uid = w.getUid();
        this.name = w.getName();
        this.creationDate = w.getCreationDate().getTime();
        this.owner = w.getOwner();
        this.ownerSource = w.getOwnerSource();
        this.parentType = -1;
        this.parentUid = -1;
        this.path = w.getPath();
    }
    
    public DMEntity(Folder f){
        this.type = DMEntity.FOLDER;
        this.uid = f.getUid();
        this.name = f.getName();
        this.creationDate = f.getCreationDate().getTime();
        this.owner = f.getOwner();
        this.ownerSource = f.getOwnerSource();
        this.parentType = f.getParentType();
        this.parentUid = f.getParentUid();
        this.path = f.getPath();
    }
    
    public DMEntity(Document d){
        this.type = DMEntity.DOCUMENT;
        this.uid = d.getUid();
        this.name = d.getName();
        this.creationDate = d.getCreationDate().getTime();
        this.owner = d.getOwner();
        this.ownerSource = d.getOwnerSource();
        this.updateDate = d.getUpdateDate().getTime();
        this.parentUid = d.getFolderUid();
        this.extension = d.getExtension();
        this.checkedOut = d.getCheckedOut();
        this.checkoutDate = (d.getCheckoutDate() != null ? d.getCheckoutDate().getTime() : null);
        this.checkoutUser = d.getCheckoutUser();
        this.checkoutUserSource = d.getCheckoutUserSource();
        this.outOfWorkflow = d.getOutOfWorkflow();
        this.workflowStatusName = d.getWorkflowStatusName();
        this.workflowStatusUid = d.getWorkflowStatusUid() != null ? d.getWorkflowStatusUid() : 0;
        this.documentTypeName = d.getDocumentTypeName();
        this.documentTypeUid = d.getDocumentTypeUid() != null ? d.getDocumentTypeUid() : 0;
        this.length = d.getLength();
        this.path = d.getPath();
    }

    public String getOwnerSource() {
        return ownerSource;
    }

    public void setOwnerSource(String authenticationSource) {
        this.ownerSource = authenticationSource;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public boolean isCheckedOut() {
        return checkedOut;
    }

    public void setCheckedOut(boolean checkedOut) {
        this.checkedOut = checkedOut;
    }

    public Date getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(Date checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public String getCheckoutUser() {
        return checkoutUser;
    }

    public void setCheckoutUser(String checkoutUser) {
        this.checkoutUser = checkoutUser;
    }

    public String getCheckoutUserSource() {
        return checkoutUserSource;
    }

    public void setCheckoutUserSource(String checkoutUserSource) {
        this.checkoutUserSource = checkoutUserSource;
    }

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

    public boolean isOutOfWorkflow() {
        return outOfWorkflow;
    }

    public void setOutOfWorkflow(boolean outOfWorkflow) {
        this.outOfWorkflow = outOfWorkflow;
    }

    public int getParentType() {
        return parentType;
    }

    public void setParentType(int parentType) {
        this.parentType = parentType;
    }

    public long getParentUid() {
        return parentUid;
    }

    public void setParentUid(long parentUid) {
        this.parentUid = parentUid;
    }

    public long getWorkflowStatusUid() {
        return workflowStatusUid;
    }

    public void setWorkflowStatusUid(long workflowStatusUid) {
        this.workflowStatusUid = workflowStatusUid;
    }

    public String getWorkflowStatusName() {
        return workflowStatusName;
    }

    public void setWorkflowStatusName(String workflowStatusName) {
        this.workflowStatusName = workflowStatusName;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}

