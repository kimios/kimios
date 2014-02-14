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

package org.kimios.core.wrappers;

/**
 *
 *  @author Fabien Alin
 *
 */
public class VirtualTreeEntity
{

    private long uid;

    private String name;

    private String virtualPath;

    private String virtualDisplayPath;

    private long virtualFolderCount;

    private int type = 2;


    public VirtualTreeEntity( long uid, String name, String virtualPath, long virtualFolderCount )
    {
        this.uid = uid;
        this.name = name;
        this.virtualPath = virtualPath;
        this.virtualFolderCount = virtualFolderCount;
    }

    public String getVirtualDisplayPath() {
        return virtualDisplayPath;
    }

    public void setVirtualDisplayPath(String virtualDisplayPath) {
        this.virtualDisplayPath = virtualDisplayPath;
    }

    public long getUid()
    {
        return uid;
    }

    public void setUid( long uid )
    {
        this.uid = uid;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getVirtualPath()
    {
        return virtualPath;
    }

    public void setVirtualPath( String virtualPath )
    {
        this.virtualPath = virtualPath;
    }

    public long getVirtualFolderCount()
    {
        return virtualFolderCount;
    }

    public void setVirtualFolderCount( long virtualFolderCount )
    {
        this.virtualFolderCount = virtualFolderCount;
    }

    public int getType()
    {
        return type;
    }

    public void setType( int type )
    {
        this.type = type;
    }
}
