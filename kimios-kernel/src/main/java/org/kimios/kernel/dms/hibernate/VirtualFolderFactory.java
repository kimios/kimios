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

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.kimios.kernel.dms.VirtualFolderMetaData;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;

/**
 * VirtualFolderFactory
 *
 *
 *
 *
 */
public class VirtualFolderFactory extends HFactory {

    public void saveOrUpdateMeta(VirtualFolderMetaData virtualFolderMd){
        getSession().saveOrUpdate(virtualFolderMd);
    }

    public void deleteMeta(VirtualFolderMetaData virtualFolderMd){
        getSession().delete(virtualFolderMd);
    }

}
