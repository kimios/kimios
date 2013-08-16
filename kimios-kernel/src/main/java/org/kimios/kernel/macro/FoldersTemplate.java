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
package org.kimios.kernel.macro;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.kimios.kernel.controller.IFolderController;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.dms.DMEntityType;
import org.kimios.kernel.dms.FactoryInstantiator;
import org.kimios.kernel.exception.MacroException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create a template with defined folders from folders-template.conf
 *
 * @author jludmann
 */
public class FoldersTemplate extends MacroImpl
{
    private Logger log = LoggerFactory.getLogger(FoldersTemplate.class);

    private DMEntity entity;

    private String templateSetting;

    public String getTemplateSetting()
    {
        return templateSetting;
    }

    public void setTemplateSetting(String templateSetting)
    {
        this.templateSetting = templateSetting;
    }

    private IFolderController folderController;

    public IFolderController getFolderController()
    {
        return folderController;
    }

    public void setFolderController(IFolderController folderController)
    {
        this.folderController = folderController;
    }

    public void execute() throws MacroException
    {
        try {
            if (entity == null) {
                entity = context.getEntity();
            }
            if (entity == null) {
                throw new NullPointerException("The target entity for folder creation is null!");
            }
            if (entity.getType() != DMEntityType.WORKSPACE && entity.getType() != DMEntityType.FOLDER) {
                throw new MacroException("The given DMEntity must be a workspace or folder only");
            }

            if (entity.getUid() <= 0) {
                //load from db
                log.debug("Found return Id: " + String.valueOf(context.getParameters().get("callReturn")));
                Long callReturn = (Long) context.getParameters().get("callReturn");
                DMEntity repEntity = FactoryInstantiator.getInstance()
                        .getDmEntityFactory()
                        .getEntity(callReturn);

                if (repEntity != null) {
                    log.debug("Rep entity: " + repEntity.getUid() + " " + repEntity.getPath());
                    entity = repEntity;
                } else {
                    throw new NullPointerException("The target entity for folder creation is null!");
                }
            }
            log.debug("Entity Template apply: " + entity + " " +
                    (entity != null ? entity.getUid() + " " + entity.getType() : ""));
            List<String> paths = new ArrayList<String>();
            if (templateSetting != null) {
                InputStream in = new FileInputStream(templateSetting);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                while (true) {
                    String line = reader.readLine();
                    // end of file
                    if (line == null) {
                        break;
                    }
                    // skip comment
                    if (!line.startsWith("#")) {
                        paths.add(line.trim());
                    }
                }
                reader.close();
                for (String path : paths) {
                    folderController.createFolder(session, path, entity.getUid(), true);
                }
            }
        } catch (Exception ex) {
            throw new MacroException(ex);
        }
    }
}

