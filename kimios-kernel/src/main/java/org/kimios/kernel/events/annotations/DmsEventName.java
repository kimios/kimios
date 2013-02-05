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
package org.kimios.kernel.events.annotations;

public enum DmsEventName
{
    APPLICATION_START,
    APPLICATION_STOP,
    SESSION_START,
    SESSION_STOP,
    DOCUMENT_CREATE,
    DOCUMENT_UPDATE,
    DOCUMENT_DELETE,
    DOCUMENT_READ,
    DOCUMENT_CHECKOUT,
    DOCUMENT_CHECKIN,
    DOCUMENT_ADD_RELATED,
    DOCUMENT_REMOVE_RELATED,
    DOCUMENT_VERSION_CREATE,
    DOCUMENT_VERSION_CREATE_FROM_LATEST, /* copy of previous version, for meta data update*/
    DOCUMENT_VERSION_UPDATE,
    DOCUMENT_VERSION_READ,
    META_VALUE_UPDATE,
    DOCUMENT_VERSION_COMMENT_CREATE,
    DOCUMENT_VERSION_COMMENT_UPDATE,
    DOCUMENT_VERSION_COMMENT_DELETE,
    FILE_UPLOAD,
    FILE_DOWNLOAD,
    FOLDER_READ,
    FOLDER_CREATE,
    FOLDER_UPDATE,
    FOLDER_DELETE,
    WORKSPACE_READ,
    WORKSPACE_CREATE,
    WORKSPACE_UPDATE,
    WORKSPACE_DELETE,
    WORKFLOW_CREATE,
    WORKFLOW_UPDATE,
    WORKFLOW_DELETE,
    WORKFLOW_STATUS_REQUEST_CREATE,
    WORKFLOW_STATUS_REQUEST_COMMENT,
    WORKFLOW_STATUS_REQUEST_ACCEPT,
    WORKFLOW_STATUS_REQUEST_REJECT,
    WORKFLOW_CANCEL,
    USER_CREATE,
    USER_DELETE,
    USER_ATTRIBUTE_SET,
    GROUP_CREATE,
    GROUP_DELETE,
    USER_GROUP_ADD,
    DOCUMENT_TYPE_CREATE,
    DOCUMENT_TYPE_UPDATE,
    DOCUMENT_TYPE_DELETE,
    META_FEED_CREATE,
    META_FEED_READ,
    META_FEED_UPDATE,
    META_FEED_DELETE,
    ENTITY_ACL_UPDATE;
}

