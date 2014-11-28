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

package org.kimios.kernel.controller.impl;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.ISearchManagementController;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.IndexException;
import org.kimios.kernel.index.AbstractIndexManager;
import org.kimios.kernel.index.SolrIndexManager;
import org.kimios.kernel.security.Role;
import org.kimios.kernel.security.Session;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Search Management Controller
 */
@Transactional
public class SearchManagementController extends AKimiosController implements ISearchManagementController {

    private AbstractIndexManager indexManager;

    public AbstractIndexManager getIndexManager() {
        return indexManager;
    }

    public void setIndexManager(AbstractIndexManager indexManager) {
        this.indexManager = indexManager;
    }

    /* (non-Javadoc)
        * @see org.kimios.kernel.controller.impl.IAdministrationController#reindex(org.kimios.kernel.security.Session, java.lang.String)
        */
    public void reindex(Session session, String path)
            throws AccessDeniedException, IndexException, ConfigException, DataSourceException {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.ADMIN, session.getUserName(), session.getUserSource()) != null) {
            indexManager.reindex(path);
        } else {
            throw new AccessDeniedException();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISearchManagementController#getReindexProgress(org.kimios.kernel.security.Session)
    */
    public int getReindexProgress(Session session)
            throws AccessDeniedException, IndexException, ConfigException, DataSourceException {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.ADMIN, session.getUserName(), session.getUserSource()) != null) {
            return indexManager.getReindexProgression();
        } else {
            throw new AccessDeniedException();
        }
    }

    @Override
    public List<String> listDocumentAvailableFields(Session session) throws AccessDeniedException, IndexException, ConfigException, DataSourceException {
        if (indexManager instanceof SolrIndexManager) {
            return ((SolrIndexManager) indexManager).filterFields();
        } else
            throw new AccessDeniedException();
    }
}
