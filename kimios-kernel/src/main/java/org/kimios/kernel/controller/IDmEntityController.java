package org.kimios.kernel.controller;

import org.kimios.exceptions.AccessDeniedException;
import org.kimios.exceptions.ConfigException;
import org.kimios.exceptions.DataSourceException;
import org.kimios.kernel.dms.model.DMEntityImpl;
import org.kimios.kernel.security.model.Session;

public interface IDmEntityController {
    public DMEntityImpl getEntity(Session session, long uid) throws DataSourceException, ConfigException, AccessDeniedException;
}
