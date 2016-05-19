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

package org.kimios.templates.impl.thymeleaf;

import org.kimios.api.templates.ITemplate;
import org.kimios.api.templates.TemplateType;
import org.kimios.templates.impl.factory.TemplateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.resourceresolver.IResourceResolver;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by farf on 21/03/16.
 */
public class KimiosTemplateResolver implements IResourceResolver {


    private static Logger logger = LoggerFactory.getLogger(KimiosTemplateResolver.class);

    private TemplateFactory templateFactory;

    public KimiosTemplateResolver(TemplateFactory templateFactory){
        this.templateFactory = templateFactory;
    }


    @Override
    public String getName() {
        return "KimiosTemplateResolver";
    }

    @Override
    public InputStream getResourceAsStream(TemplateProcessingParameters templateProcessingParameters, String resourceName) {
        try{

            ITemplate kimiosTemplate = null;
            //load template from Kimios Database. Resource Name can be :
            // Template Name
            // Template Id
            // Template Type (the default will be loaded)
            long tplId = -1;
            TemplateType type = null;
            try{
                tplId = Long.parseLong(resourceName);
            } catch (Exception ex){

            }
            try{
                type = TemplateType.valueOf(resourceName);
            } catch (Exception ex){

            }

            if(tplId > 0){
                kimiosTemplate = templateFactory.loadTemplate(tplId);
            } else if(type != null) {
                kimiosTemplate = templateFactory.loadDefaultTemplate(type);
            } else {
                kimiosTemplate = templateFactory.loadTemplate(resourceName);
            }

            if( kimiosTemplate != null){
                return new ByteArrayInputStream(kimiosTemplate.getContent().getBytes());
            } else
                throw new Exception("KimiosTemplateNotFound for resourceName: " + resourceName);

        } catch (Exception ex){
            throw new TemplateProcessingException(ex.getMessage());
        }
    }
}
