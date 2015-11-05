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

import org.kimios.kernel.dms.model.PathTemplate;
import org.kimios.kernel.dms.utils.PathElement;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.hibernate.HFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by farf on 10/04/15.
 */
public class HPathTemplateFactory extends HFactory implements org.kimios.kernel.dms.PathTemplateFactory {

    private static Logger log = LoggerFactory.getLogger(HPathTemplateFactory.class);


    @Override
    public PathTemplate loadById(Long pathTemplateId){
        String q = "from PathTemplate p where p.id = :id";
        PathTemplate p = (PathTemplate)getSession().createQuery(q)
                .setLong("id", pathTemplateId)
                .uniqueResult();
        try{
            p.setPathElements(PathElement.parseElementsFromStructure(p.getPathTemplateContent()));
        }
        catch (Exception ex){
            log.error("incorrect path model", ex);
            throw new AccessDeniedException();
        }

        return p;
    }

    @Override
    public PathTemplate getDefaultPathTemplate(){

        try {
            String q = "from PathTemplate p where p.defaultPathTemplate = true";
            PathTemplate p = (PathTemplate) getSession().createQuery(q)
                    .uniqueResult();
            p.setPathElements(PathElement.parseElementsFromStructure(p.getPathTemplateContent()));
            return p;
        }catch (Exception ex){
            log.error("default path template not found.", ex);
            return null;
        }
    }


    @Override
    public Long save(PathTemplate pathTemplate){

        try {
            pathTemplate.setPathTemplateContent(PathElement.convertStructureToString(pathTemplate.getPathElements()));
            getSession().saveOrUpdate(pathTemplate);
            getSession().flush();
            return pathTemplate.getId();
        }catch (Exception ex){
            return null;
        }
    }

}
