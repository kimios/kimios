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
package org.kimios.kernel.controller.impl;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.IPathController;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.PathException;
import org.kimios.kernel.security.SecurityAgent;
import org.kimios.kernel.security.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(noRollbackFor = PathException.class)
public class PathController extends AKimiosController implements IPathController
{

    private static Logger logger = LoggerFactory.getLogger(IPathController.class);

    public org.kimios.kernel.ws.pojo.DMEntity getDMEntityPojoFromPath(Session session, String path) throws PathException, ConfigException, DataSourceException, AccessDeniedException {
        DMEntity entity = this.getDMEntityFromPath(session, path);
        return entity.toPojo();
    }

    /* (non-Javadoc)
        * @see org.kimios.kernel.controller.impl.IPathController#getDMEntityFromPath(org.kimios.kernel.security.Session, java.lang.String)
        */
    public DMEntity getDMEntityFromPath(Session session, String path) throws PathException, ConfigException,
            DataSourceException, AccessDeniedException
    {

        if (path == null) {
            throw new PathException("Supplied path was incorrect");
        }
        if (path.endsWith("/") && path.length() > 1) {
            path = path.substring(0, path.length() - 1);
        }
        String[] p = path.split("/");
        if (p.length == 0) {
            throw new PathException("Supplied path was incorrect");
        }
        if (!p[0].equals("")) {
            throw new PathException("Supplied path was incorrect");
        }

        DMEntity dm = null;
        try {
            dm = dmsFactoryInstantiator.getDmEntityFactory().getEntity(path);
        } catch (Exception ex) {
            logger.error("An error occured while retrieving path " + path, ex);
        }
        if (dm != null) {
            if (!SecurityAgent.getInstance()
                    .isReadable(dm, session.getUserName(), session.getUserSource(), session.getGroups()))
            {
                throw new AccessDeniedException();
            }
            return dm;
        } else {
            logger.error("No entry found at the given path : " + path);
            throw new PathException("No entry found at the given path : " + path);
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IPathController#getPathFromDMEntity(org.kimios.kernel.security.Session, long, int)
    */
    public String getPathFromDMEntity(Session session, long dmEntityUid)
            throws ConfigException, DataSourceException, AccessDeniedException
    {
        DMEntity d = null;
        d = dmsFactoryInstantiator.getDmEntityFactory().getEntity(dmEntityUid);
        if (d != null && SecurityAgent.getInstance()
                .isReadable(d, session.getUserName(), session.getUserSource(), session.getGroups()))
        {
            return d.getPath();
        } else {
            throw new AccessDeniedException();
        }
    }

    public List<DMEntity> getDMEntitiesByPathAndType(String path, int dmEntityType)
            throws ConfigException, DataSourceException, AccessDeniedException
    {

        return dmsFactoryInstantiator.getDmEntityFactory().getEntitiesByPathAndType(path, dmEntityType);
    }

    public List<DMEntity> getDMEntitiesByPath(String path)
            throws ConfigException, DataSourceException, AccessDeniedException
    {
        return dmsFactoryInstantiator.getDmEntityFactory().getEntities(path);
    }
}

