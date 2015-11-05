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

package org.kimios.kernel.dms.hibernate;

import org.kimios.kernel.dms.model.Folder;
import org.kimios.kernel.dms.model.VirtualFolderMetaData;
import org.kimios.kernel.hibernate.HFactory;

import java.util.List;

/**
 * VirtualFolderFactory
 *
 *
 *
 *
 */
public class HVirtualFolderFactory extends HFactory implements org.kimios.kernel.dms.VirtualFolderFactory {

    @Override
    public void saveOrUpdateMeta(VirtualFolderMetaData virtualFolderMd){

        getSession().saveOrUpdate(virtualFolderMd);
        flush();
    }

    @Override
    public void deleteMeta(VirtualFolderMetaData virtualFolderMd){

        getSession().delete(virtualFolderMd);
        flush();
    }


    @Override
    public List<VirtualFolderMetaData> virtualFolderMetaDataList(Folder folder){
        String query = "select m from VirtualFolderMetaData m where m.virtualFolder = :folder";
        return getSession()
                .createQuery(query)
                .setParameter("folder", folder)
                .list();

    }

}
