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

package org.kimios.kernel.share.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.resourceresolver.IResourceResolver;

import java.io.InputStream;

/**
 * Created by farf on 3/12/14.
 */
public class DummyTemplateResolver implements IResourceResolver {

    private static Logger logger = LoggerFactory.getLogger(DummyTemplateResolver.class);



    @Override
    public String getName() {
        return DummyTemplateResolver.class.getName();
    }

    @Override
    public InputStream getResourceAsStream(TemplateProcessingParameters arg0,
                                           String arg1) {

        try{
            InputStream stream = this.getClass().getResourceAsStream("/" + arg1);
            return stream;
        } catch(Exception ex){
            logger.error("error while loading defined template " + arg1, ex);
            throw new TemplateProcessingException(ex.getMessage());
        }
    }

}
