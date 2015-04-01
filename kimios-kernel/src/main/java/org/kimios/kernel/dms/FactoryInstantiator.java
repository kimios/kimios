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

public class FactoryInstantiator implements IDmsFactoryInstantiator {
    private static FactoryInstantiator instance;

    private FactoryInstantiator()
    {
    }

    synchronized public static FactoryInstantiator getInstance()
    {
        if (instance == null) {
            instance = new FactoryInstantiator();
        }
        return instance;
    }

    DMEntityFactory dmEntityFactory;

    DocumentFactory documentFactory;

    FolderFactory folderFactory;

    DocumentVersionFactory documentVersionFactory;

    DocumentTypeFactory documentTypeFactory;

    WorkspaceFactory workspaceFactory;

    MetaFactory metaFactory;

    MetaFeedFactory metaFeedFactory;

    MetaValueFactory metaValueFactory;

    WorkflowFactory workflowFactory;

    WorkflowStatusFactory workflowStatusFactory;

    EnumerationValueFactory enumerationValueFactory;

    BookmarkFactory bookmarkFactory;

    RecentItemsFactory recentItemFactory;

    LockFactory lockFactory;

    WorkflowStatusManagerFactory workflowStatusManagerFactory;

    DocumentWorkflowStatusFactory documentWorkflowStatusFactory;

    DocumentWorkflowStatusRequestFactory documentWorkflowStatusRequestFactory;

    DocumentCommentFactory documentCommentFactory;

    SymbolicLinkFactory symbolicLinkFactory;

    @Override
    public DocumentTypeFactory getDocumentTypeFactory()
    {
        return documentTypeFactory;
    }

    public void setDocumentTypeFactory(DocumentTypeFactory documentTypeFactory)
    {
        this.documentTypeFactory = documentTypeFactory;
    }

    @Override
    public DMEntityFactory getDmEntityFactory()
    {
        return dmEntityFactory;
    }

    public void setDmEntityFactory(DMEntityFactory dmEntityFactory)
    {
        this.dmEntityFactory = dmEntityFactory;
    }

    @Override
    public DocumentFactory getDocumentFactory()
    {
        return documentFactory;
    }

    public void setDocumentFactory(DocumentFactory documentFactory)
    {
        this.documentFactory = documentFactory;
    }

    @Override
    public FolderFactory getFolderFactory()
    {
        return folderFactory;
    }

    public void setFolderFactory(FolderFactory folderFactory)
    {
        this.folderFactory = folderFactory;
    }

    @Override
    public DocumentVersionFactory getDocumentVersionFactory()
    {
        return documentVersionFactory;
    }

    public void setDocumentVersionFactory(
            DocumentVersionFactory documentVersionFactory)
    {
        this.documentVersionFactory = documentVersionFactory;
    }

    @Override
    public WorkspaceFactory getWorkspaceFactory()
    {
        return workspaceFactory;
    }

    public void setWorkspaceFactory(WorkspaceFactory workspaceFactory)
    {
        this.workspaceFactory = workspaceFactory;
    }

    @Override
    public MetaFactory getMetaFactory()
    {
        return metaFactory;
    }

    public void setMetaFactory(MetaFactory metaFactory)
    {
        this.metaFactory = metaFactory;
    }

    @Override
    public MetaFeedFactory getMetaFeedFactory()
    {
        return metaFeedFactory;
    }

    public void setMetaFeedFactory(MetaFeedFactory metaFeedFactory)
    {
        this.metaFeedFactory = metaFeedFactory;
    }

    @Override
    public MetaValueFactory getMetaValueFactory()
    {
        return metaValueFactory;
    }

    public void setMetaValueFactory(MetaValueFactory metaValueFactory)
    {
        this.metaValueFactory = metaValueFactory;
    }

    @Override
    public WorkflowFactory getWorkflowFactory()
    {
        return workflowFactory;
    }

    public void setWorkflowFactory(WorkflowFactory workflowFactory)
    {
        this.workflowFactory = workflowFactory;
    }

    @Override
    public WorkflowStatusFactory getWorkflowStatusFactory()
    {
        return workflowStatusFactory;
    }

    public void setWorkflowStatusFactory(WorkflowStatusFactory workflowStatusFactory)
    {
        this.workflowStatusFactory = workflowStatusFactory;
    }

    @Override
    public EnumerationValueFactory getEnumerationValueFactory()
    {
        return enumerationValueFactory;
    }

    public void setEnumerationValueFactory(
            EnumerationValueFactory enumerationValueFactory)
    {
        this.enumerationValueFactory = enumerationValueFactory;
    }

    @Override
    public BookmarkFactory getBookmarkFactory()
    {
        return bookmarkFactory;
    }

    public void setBookmarkFactory(BookmarkFactory bookmarkFactory)
    {
        this.bookmarkFactory = bookmarkFactory;
    }

    @Override
    public RecentItemsFactory getRecentItemFactory()
    {
        return recentItemFactory;
    }

    public void setRecentItemFactory(RecentItemsFactory recentItemFactory)
    {
        this.recentItemFactory = recentItemFactory;
    }

    @Override
    public LockFactory getLockFactory()
    {
        return lockFactory;
    }

    public void setLockFactory(LockFactory lockFactory)
    {
        this.lockFactory = lockFactory;
    }

    @Override
    public WorkflowStatusManagerFactory getWorkflowStatusManagerFactory()
    {
        return workflowStatusManagerFactory;
    }

    public void setWorkflowStatusManagerFactory(
            WorkflowStatusManagerFactory workflowStatusManagerFactory)
    {
        this.workflowStatusManagerFactory = workflowStatusManagerFactory;
    }

    @Override
    public DocumentWorkflowStatusFactory getDocumentWorkflowStatusFactory()
    {
        return documentWorkflowStatusFactory;
    }

    public void setDocumentWorkflowStatusFactory(
            DocumentWorkflowStatusFactory documentWorkflowStatusFactory)
    {
        this.documentWorkflowStatusFactory = documentWorkflowStatusFactory;
    }

    @Override
    public DocumentWorkflowStatusRequestFactory getDocumentWorkflowStatusRequestFactory()
    {
        return documentWorkflowStatusRequestFactory;
    }

    public void setDocumentWorkflowStatusRequestFactory(
            DocumentWorkflowStatusRequestFactory documentWorkflowStatusRequestFactory)
    {
        this.documentWorkflowStatusRequestFactory = documentWorkflowStatusRequestFactory;
    }

    @Override
    public DocumentCommentFactory getDocumentCommentFactory()
    {
        return documentCommentFactory;
    }

    public void setDocumentCommentFactory(
            DocumentCommentFactory documentCommentFactory)
    {
        this.documentCommentFactory = documentCommentFactory;
    }

    @Override
    public SymbolicLinkFactory getSymbolicLinkFactory()
    {
        return symbolicLinkFactory;
    }

    public void setSymbolicLinkFactory(SymbolicLinkFactory symbolicLinkFactory)
    {
        this.symbolicLinkFactory = symbolicLinkFactory;
    }
}

