package org.kimios.kernel.controller.impl;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.ISearchManagementController;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.IndexException;
import org.kimios.kernel.index.AbstractIndexManager;
import org.kimios.kernel.security.Role;
import org.kimios.kernel.security.Session;

/**
 * Search Management Controller
 */
public class SearchManagementController extends AKimiosController implements ISearchManagementController
{

    private AbstractIndexManager indexManager;

    public AbstractIndexManager getIndexManager()
    {
        return indexManager;
    }

    public void setIndexManager( AbstractIndexManager indexManager )
    {
        this.indexManager = indexManager;
    }

    /* (non-Javadoc)
        * @see org.kimios.kernel.controller.impl.IAdministrationController#reindex(org.kimios.kernel.security.Session, java.lang.String)
        */
    public void reindex(Session session, String path)
        throws AccessDeniedException, IndexException, ConfigException, DataSourceException
    {
        if (securityFactoryInstantiator.getRoleFactory()
            .getRole( Role.ADMIN, session.getUserName(), session.getUserSource()) != null)
        {
            indexManager.reindex(path);
        } else {
            throw new AccessDeniedException();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISearchManagementController#getReindexProgress(org.kimios.kernel.security.Session)
    */
    public int getReindexProgress(Session session)
        throws AccessDeniedException, IndexException, ConfigException, DataSourceException
    {
        if (securityFactoryInstantiator.getRoleFactory()
            .getRole(Role.ADMIN, session.getUserName(), session.getUserSource()) != null)
        {
            return indexManager.getReindexProgression();
        } else {
            throw new AccessDeniedException();
        }
    }
}
