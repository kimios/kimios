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

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.model.DMEntity;
import org.kimios.kernel.dms.model.DMEntityImpl;
import org.kimios.kernel.exception.DataSourceException;

import java.util.List;

public interface DMEntityFactory {
    public DMEntity getEntity(long dmEntityUid) throws ConfigException, DataSourceException;

    public DMEntity getEntity(long dmEntityUid, int dmEntityType) throws ConfigException, DataSourceException;

    public DMEntity getEntity(String path) throws ConfigException, DataSourceException;

    public List<DMEntity> getEntities(String path) throws ConfigException, DataSourceException;

    public List<DMEntityImpl> getEntitiesImpl(String path) throws ConfigException, DataSourceException;

    public List<DMEntity> getEntitiesByPathAndType(String path, int dmEntityType)
            throws ConfigException, DataSourceException;

    public Long getEntitiesByPathAndTypeCount(String path, int dmEntityType, List<Long> excludedIds,
                                              List<String> excludedExtension)
            throws ConfigException, DataSourceException;

    public Long getEntitiesByPathAndTypeCount(String path, int dmEntityType)
            throws ConfigException, DataSourceException;

    public List<DMEntity> getEntitiesByPathAndType(String path, int dmEntityType, int start, int count)
            throws ConfigException, DataSourceException;

    public List<DMEntity> getEntitiesByPathAndType(String path, int dmEntityType, int start, int count,
                                                   List<Long> excludedIds, List<String> excludedExtension)
            throws ConfigException, DataSourceException;


    public void deleteEntities(String path) throws ConfigException, DataSourceException;

    public void updateEntity(DMEntityImpl entity) throws ConfigException, DataSourceException;

    public void generatePath(DMEntity entity) throws ConfigException, DataSourceException;

    public void updatePath(DMEntity entity, String newName) throws ConfigException, DataSourceException;

    public void trash(DMEntityImpl entity) throws ConfigException, DataSourceException;

    public List<DMEntity> listTrashedEntities(Integer start, Integer count)
            throws ConfigException, DataSourceException;

    public void untrash(DMEntityImpl entity) throws ConfigException, DataSourceException;

    public List<DMEntity> getEntitiesFromIds(List<Long> listIds, int dmEntityType) throws ConfigException, DataSourceException;
}

