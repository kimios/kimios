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
/**
 * DM Entity POJO
 */
kimios.DMEntityPojo = function(entity) {
  
  this.uid           = entity.uid;
  this.type           = entity.type;
  this.name           = entity.name;
  this.path           = entity.path;
  this.owner           = entity.owner;
  this.ownerSource       = entity.ownerSource;
  this.creationDate       = entity.creationDate;
  
  this.length          = entity.length;
  this.extension         = entity.extension;
  
  this.parentUid         = entity.parentUid;
  this.parentType       = entity.parentType;
  
  this.documentTypeUid    = entity.documentTypeUid;
  this.documentTypeName    = entity.documentTypeName;
  
  this.workflowStatusUid    = entity.workflowStatusUid;
  this.workflowStatusName    = entity.workflowStatusName;
  this.statusUserName      = entity.statusUserName;
  this.statusUserSource    = entity.statusUserSource;
  this.statusDate        = entity.statusDate;
  this.status          = entity.status;
  this.outOfWorkflow      = entity.outOfWorkflow;
  
  this.checkedOut        = entity.checkedOut;
  this.checkoutDate      = entity.checkoutDate;
  this.checkoutUser      = entity.checkoutUser;
  this.checkoutUserSource    = entity.checkoutUserSource;
  
  this.author          = entity.author;
  this.authorSource      = entity.authorSource;
  
  this.versionUid        = entity.versionUid;
  
};
