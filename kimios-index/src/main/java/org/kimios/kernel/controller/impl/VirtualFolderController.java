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
import org.kimios.kernel.dms.MetaType;
import org.kimios.kernel.dms.MetaValue;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.index.query.factory.VirtualFolderFactory;
import org.kimios.kernel.index.query.model.VirtualFolder;
import org.kimios.kernel.index.query.model.VirtualFolderMetaData;
import org.kimios.kernel.security.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * VirtualFolderController
 */
@Transactional
public class VirtualFolderController {


    private static Logger logger = LoggerFactory.getLogger(VirtualFolderController.class);


    private VirtualFolderFactory virtualFolderFactory;

    public VirtualFolderController(VirtualFolderFactory virtualFolderFactory) {
        this.virtualFolderFactory = virtualFolderFactory;
    }


    public VirtualFolder addVirtualFolder(Session session, Long id, String name, List<MetaValue> metaValues)
            throws ConfigException, AccessDeniedException {
        VirtualFolder virtualFolder = null;
        if (id != null) {
            virtualFolder = virtualFolderFactory.loadById(id);
        } else {
            virtualFolder = new VirtualFolder();
        }
        virtualFolder.setName(name);
        virtualFolder.setOwner(session.getUserName());
        virtualFolder.setOwnerSource(session.getUserSource());

        virtualFolderFactory.save(virtualFolder);
        virtualFolderFactory.flush();

        for (MetaValue metaValue : metaValues) {
            VirtualFolderMetaData virtualFolderMetaData = new VirtualFolderMetaData();
            virtualFolderMetaData.setVirtualFolderId(virtualFolder.getId());
            virtualFolderMetaData.setMetaId(metaValue.getMetaUid());
            switch (virtualFolderMetaData.getMeta().getMetaType()) {
                case MetaType.STRING:
                    virtualFolderMetaData.setStringValue(metaValue.getValue().toString());
                    break;
                case MetaType.DATE:
                    virtualFolderMetaData.setDateValue((Date) metaValue.getValue());
                    break;
            }
            virtualFolderFactory.save(virtualFolderMetaData);
        }
        return virtualFolder;
    }

}
