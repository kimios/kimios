package org.kimios.kernel.controller.impl;

import org.kimios.exceptions.AccessDeniedException;
import org.kimios.exceptions.ConfigException;
import org.kimios.exceptions.DataSourceException;
import org.kimios.kernel.controller.IDmEntityController;
import org.kimios.kernel.controller.IDocumentController;
import org.kimios.kernel.controller.IFolderController;
import org.kimios.kernel.controller.IWorkspaceController;
import org.kimios.kernel.dms.model.DMEntityImpl;
import org.kimios.kernel.security.model.Session;

public class DmEntityController implements IDmEntityController {

    IWorkspaceController workspaceController;
    IFolderController folderController;
    IDocumentController documentController;

    @Override
    public DMEntityImpl getEntity(Session session, long uid) throws DataSourceException, ConfigException, AccessDeniedException {
        DMEntityImpl entity = null;
        try {
            entity = this.documentController.getDocument(session, uid);
        } catch (AccessDeniedException ade) {
            // access denied OR entity is not a document
        }
        if (entity == null) {
            try {
                entity = this.folderController.getFolder(session, uid);
            } catch (AccessDeniedException ade) {
                // access denied OR entity is not a folder
            }
        }
        if (entity == null) {
            try {
                entity = this.workspaceController.getWorkspace(session, uid);
            } catch (AccessDeniedException ade) {
                // access denied OR entity is not a folder
            }
        }
        return entity;
    }

    public IWorkspaceController getWorkspaceController() {
        return workspaceController;
    }

    public void setWorkspaceController(IWorkspaceController workspaceController) {
        this.workspaceController = workspaceController;
    }

    public IFolderController getFolderController() {
        return folderController;
    }

    public void setFolderController(IFolderController folderController) {
        this.folderController = folderController;
    }

    public IDocumentController getDocumentController() {
        return documentController;
    }

    public void setDocumentController(IDocumentController documentController) {
        this.documentController = documentController;
    }
}
