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
package org.kimios.kernel.dms;

import java.util.List;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;

public interface DMEntityFactory
{
    public DMEntity getEntity(long dmEntityUid) throws ConfigException, DataSourceException;

    public DMEntity getEntity(long dmEntityUid, int dmEntityType) throws ConfigException, DataSourceException;

    public DMEntity getEntity(String path) throws ConfigException, DataSourceException;

    public List<DMEntity> getEntities(String path) throws ConfigException, DataSourceException;

    public List<DMEntityImpl> getEntitiesImpl(String path) throws ConfigException, DataSourceException;

    public List<DMEntity> getEntitiesByPathAndType(String path, int dmEntityType)
            throws ConfigException, DataSourceException;

    public void deteteEntities(String path) throws ConfigException, DataSourceException;

    public void updateEntity(DMEntityImpl entity) throws ConfigException, DataSourceException;

    public void generatePath(DMEntity entity) throws ConfigException, DataSourceException;

    public void updatePath(DMEntity entity, String newName) throws ConfigException, DataSourceException;
}

