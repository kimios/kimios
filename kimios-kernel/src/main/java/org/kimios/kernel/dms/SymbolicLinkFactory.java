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

/**
 * @author Fabien Alin
 */
public interface SymbolicLinkFactory
{
    /**
     * @throws org.kimios.kernel.exception.DataSourceException return all of the SymbolicLink linked to a given
     * DMEntity(Document or Folder)
     */
    public List<SymbolicLink> getSymbolicLinks(DMEntity dme) throws ConfigException, DataSourceException;

    /**
     * @return Vector<SymbolicLinks>
     * @throws DataSourceException return all of the Child SymbolicLink for a given DMEntity(Workspace or Folder)
     */
    public List<SymbolicLink> getChildSymbolicLinks(DMEntity d) throws ConfigException, DataSourceException;

    /**
     * @throws DataSourceException save a symbolic link
     */
    public void addSymbolicLink(SymbolicLink sl) throws ConfigException, DataSourceException;

    /**
     * @throws DataSourceException remove a symbolic link
     */
    public void removeSymbolicLink(SymbolicLink sl) throws ConfigException, DataSourceException;

    /**
     * @throws DataSourceException remove a symbolic link
     */
    public void removeSymbolicLink(long symbolicLinkId) throws ConfigException, DataSourceException;

    /**
     * @param dmEntityUid
     * @param dmEntityType
     * @param parentUid
     * @param parentType
     * @return
     * @throws ConfigException
     * @throws DataSourceException
     */
    public SymbolicLink getSymbolicLink(long dmEntityUid, int dmEntityType, long parentUid, int parentType)
            throws ConfigException, DataSourceException;


    /**
     * @param symbolicLinkId
     * @return
     * @throws ConfigException
     * @throws DataSourceException
     */
    public SymbolicLink getSymbolicLink(long symbolicLinkId)
            throws DataSourceException;

    /**
     * @param sl
     * @throws ConfigException
     * @throws DataSourceException
     */
    public void updateSymbolicLink(SymbolicLink sl) throws ConfigException, DataSourceException;
}

