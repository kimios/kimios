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

package org.kimios.kernel.dms;

/**
 * Created by farf on 01/04/15.
 */
public interface IDmsFactoryInstantiator {
    DocumentTypeFactory getDocumentTypeFactory();

    DMEntityFactory getDmEntityFactory();

    DocumentFactory getDocumentFactory();

    FolderFactory getFolderFactory();

    DocumentVersionFactory getDocumentVersionFactory();

    WorkspaceFactory getWorkspaceFactory();

    MetaFactory getMetaFactory();

    MetaFeedFactory getMetaFeedFactory();

    MetaValueFactory getMetaValueFactory();

    WorkflowFactory getWorkflowFactory();

    WorkflowStatusFactory getWorkflowStatusFactory();

    EnumerationValueFactory getEnumerationValueFactory();

    BookmarkFactory getBookmarkFactory();

    RecentItemsFactory getRecentItemFactory();

    LockFactory getLockFactory();

    WorkflowStatusManagerFactory getWorkflowStatusManagerFactory();

    DocumentWorkflowStatusFactory getDocumentWorkflowStatusFactory();

    DocumentWorkflowStatusRequestFactory getDocumentWorkflowStatusRequestFactory();

    DocumentCommentFactory getDocumentCommentFactory();

    SymbolicLinkFactory getSymbolicLinkFactory();

    PathTemplateFactory getPathTemplateFactory();

    VirtualFolderFactory getVirtualFolderFactory();
}
