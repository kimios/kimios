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

package org.kimios.kernel.rules.impl;

import org.apache.commons.lang.StringUtils;
import org.kimios.kernel.dms.model.*;
import org.kimios.kernel.dms.FactoryInstantiator;
import org.kimios.kernel.security.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by farf on 08/12/15.
 */
public class TreeTemplateRule extends RuleImpl {


    private static Logger logger = LoggerFactory.getLogger(TreeTemplateRule.class);

    @Override
    public boolean isTrue() {
        return true;
    }

    @Override
    public void execute() throws Exception {
        logger.info("starting rule {} for {}", this.getClass().getName(), this.getContext().getEntity());


        String childPaths = parameters.get("childPaths");
        if(StringUtils.isEmpty( childPaths )){
            logger.info("leaving: no child paths defined for {} on {}", this.getClass().getName(),
                    this.getContext().getEntity().getPath());
            return;
        }



        List<String> paths = Arrays.asList(childPaths.split(","));


        List<DMEntitySecurity> securities =
                org.kimios.kernel.security.FactoryInstantiator.getInstance()
                        .getDMEntitySecurityFactory().getDMEntitySecurities(this.getContext().getEntity());
        for(String childPath: paths){
            long parentUid = this.getContext().getEntity().getUid();
            int parentType = this.getContext().getEntity().getType();
            DMEntityImpl parentEntity = (DMEntityImpl)this.getContext().getEntity();
            logger.info("creating child path {}", childPath);
            String[] chunks = childPath.split("/");


            for (int i = 0; i < chunks.length; i++) {
                String tmpPath = "";
                for (int j = 0; j <= i; j++) {
                    tmpPath += "/" + chunks[j];
                }
                tmpPath = parentEntity.getPath() + tmpPath;
                //a folder
                logger.info("looking for entity path {}", tmpPath);
                DMEntity dm = FactoryInstantiator.getInstance().getDmEntityFactory().getEntity(tmpPath);
                if (dm == null) {
                    //create folder
                    Date creationDate = new Date();
                    Folder f = new Folder(-1, chunks[i],
                            this.getContext().getSession().getUserName(),
                            this.getContext().getSession().getUserSource(),
                            creationDate,
                            parentUid,
                            parentType);
                    f.setUpdateDate(creationDate);
                    f.setParentUid(parentUid);
                    f.setParent(parentEntity);
                    FactoryInstantiator.getInstance().getDmEntityFactory().generatePath(f);
                    FactoryInstantiator.getInstance().getFolderFactory().saveFolder(f);

                    //look for securities

                    if(securities != null && securities.size() > 0){
                        for (DMEntitySecurity sec: securities) {
                            DMEntitySecurity des = new DMEntitySecurity(
                                    f.getUid(),
                                    f.getType(),
                                    sec.getName(),
                                    sec.getSource(),
                                    sec.getType(),
                                    sec.isRead(),
                                    sec.isWrite(),
                                    sec.isFullAccess(),
                                    f);
                            org.kimios.kernel.security.FactoryInstantiator.getInstance()
                                    .getDMEntitySecurityFactory().saveDMEntitySecurity(des, null);
                        }
                    }

                    parentUid = f.getUid();
                    parentType = DMEntityType.FOLDER;
                    parentEntity = f;
                    logger.info("saved folder {}", f.getPath());
                } else {
                    parentUid = dm.getUid();
                    parentType = dm.getType();
                    parentEntity = (DMEntityImpl)dm;
                    logger.info("already existing folder {}", dm.getPath());
                }
            }
        }


        logger.info("ending rule processing");
    }
}
