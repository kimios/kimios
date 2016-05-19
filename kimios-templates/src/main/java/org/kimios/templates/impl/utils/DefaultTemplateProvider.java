/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
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

package org.kimios.templates.impl.utils;

import org.apache.commons.io.IOUtils;
import org.kimios.kernel.templates.model.Template;
import org.kimios.api.templates.TemplateType;

/**
 * Created by farf on 21/03/16.
 */
public class DefaultTemplateProvider {

    /***
     *  Utility class to provide default templates, by type, bundled with Kimios
     *  Templates are defined in static files
     */


    public Template loadStaticDefaultTemplate(TemplateType templateType) throws Exception {

        Template t = new Template();
        String templateResourceName = null;
        switch (templateType){
            case MAIL:
                templateResourceName = "default-email-template.html";
                break;

            case SHARE_MAIL:
                templateResourceName = "default-share-email-template.html";
                break;

            case SYSTEM:
                templateResourceName = "default-system-template.html";
                break;
            case CUSTOM:
                // load usging template name minified
                templateResourceName = "default-" + templateType.name().toLowerCase() + ".html";

        }
        if(templateResourceName != null){
            t.setContent(IOUtils.toString(DefaultTemplateProvider.class.getResourceAsStream("templates/" + templateResourceName )));
        } else
            throw new Exception("DefaultTemplateNotFound for " + templateType);


        return t;
    }

}
