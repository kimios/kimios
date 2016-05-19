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

package org.kimios.templates.impl;

import org.apache.commons.io.IOUtils;
import org.kimios.api.templates.ITemplateProcessor;
import org.kimios.api.templates.ITemplate;
import org.kimios.kernel.templates.model.Template;
import org.kimios.templates.impl.thymeleaf.KimiosTemplateResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Created by farf on 04/03/16.
 */
@Transactional
public class TemplateProcessor implements ITemplateProcessor {


    private static final Logger logger = LoggerFactory.getLogger(TemplateProcessor.class);

    private TemplateEngine defaultTplEngine;

    private IResourceResolver kimiosTemplateResolver;

    public IContext initContext(){
        return new Context();
    }

    public TemplateProcessor(KimiosTemplateResolver kimiosTemplateResolver){
        this.kimiosTemplateResolver = kimiosTemplateResolver;

        defaultTplEngine = new TemplateEngine();
        TemplateResolver templateResolver = new TemplateResolver();
        templateResolver.setResourceResolver(kimiosTemplateResolver);
        defaultTplEngine.setTemplateResolver(templateResolver);
    }

    private String generate(TemplateEngine templateEngine,
                            String template, Map<String, Object> datas){

        if(templateEngine == null)
            throw new TemplateProcessingException("template processor not initialized");

        IContext currentContext = new Context();
        currentContext.getVariables().putAll(datas);

        return templateEngine.process(template, currentContext);

    }


    @Override
    public String processStringTemplateToString(final String template, Map<String, Object> context) {
        //Tempalte is actually template content.
        //So we dynamically build a templateEngine
        TemplateEngine tmpEngine = new TemplateEngine();
        TemplateResolver templateResolver = new TemplateResolver();
        templateResolver.setResourceResolver(new IResourceResolver() {

            @Override
            public String getName() {
                return "temp-template";
            }

            @Override
            public InputStream getResourceAsStream(TemplateProcessingParameters templateProcessingParameters, String resourceName) {
                try {
                    return new ByteArrayInputStream(template.getBytes());
                } catch (Throwable ex1) {
                    System.out.println(ex1.getMessage());
                    ex1.printStackTrace(System.err);
                    throw new TemplateProcessingException(ex1.getMessage());
                }
            }
        });
        tmpEngine.setTemplateResolver(templateResolver);
        return generate(tmpEngine, template, context);
    }

    @Override
    public String processTemplateToString(ITemplate template, Map<String, Object> context)
        throws Exception {
        return generate(defaultTplEngine, template.getName(), context);
    }



    @Override
    public void processContentToOutputStream(ITemplate template, Map<String, Object> context, OutputStream out)
        throws Exception {
        String contents = generate(defaultTplEngine, template.getName(), context);
        IOUtils.write(contents, out);
    }
}
