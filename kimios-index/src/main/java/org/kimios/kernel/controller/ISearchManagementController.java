package org.kimios.kernel.controller;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.IndexException;
import org.kimios.kernel.security.Session;

/**
 * Created with IntelliJ IDEA.
 * User: farf
 * Date: 7/8/13
 * Time: 12:37 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ISearchManagementController
{


    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISearchManagementController#reindex(org.kimios.kernel.security.Session, java.lang.String)
    */
    public void reindex(Session session, String path)
        throws AccessDeniedException, IndexException, ConfigException, DataSourceException;

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISearchManagementController#getReindexProgress(org.kimios.kernel.security.Session)
    */
    public int getReindexProgress(Session session)
        throws AccessDeniedException, IndexException, ConfigException, DataSourceException;
}
