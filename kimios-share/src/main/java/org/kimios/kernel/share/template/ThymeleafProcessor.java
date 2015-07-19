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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.templateresolver.TemplateResolver;

import java.util.Map;


public class ThymeleafProcessor {


    private TemplateEngine tplEngine;

    public IContext initContext(){
        return new Context();
    }


    public ThymeleafProcessor(){
        tplEngine = new TemplateEngine();
        TemplateResolver templateResolver = new TemplateResolver();
        templateResolver.setResourceResolver(new DummyTemplateResolver());
        tplEngine.setTemplateResolver(templateResolver);
    }


    private static Logger logger = LoggerFactory.getLogger(ThymeleafProcessor.class);

    public String generate(String template, Map<String, Object> datas){

        if(tplEngine == null)
            throw new TemplateProcessingException("template processor not initialized");

        IContext currentContext = new Context();
        currentContext.getVariables().putAll(datas);

        return tplEngine.process(template, currentContext);

    }
}

