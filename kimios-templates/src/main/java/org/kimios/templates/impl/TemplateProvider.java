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

import org.kimios.kernel.controller.AKimiosController;
import org.kimios.api.templates.ITemplateProvider;
import org.kimios.api.templates.ITemplate;
import org.kimios.api.templates.TemplateType;
import org.kimios.templates.impl.factory.TemplateFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by farf on 04/03/16.
 */
@Transactional
public class TemplateProvider extends AKimiosController implements ITemplateProvider {


    private TemplateFactory templateFactory;

    public TemplateProvider(TemplateFactory templateFactory){
        this.templateFactory = templateFactory;
    }

    @Override
    public ITemplate loadTemplate(long templateId) throws Exception {
        ITemplate t = templateFactory.loadTemplate(templateId);
        return t;
    }

    @Override
    public ITemplate loadTemplate(String templateName) throws Exception {
        ITemplate t = templateFactory.loadTemplate(templateName);
        return t;
    }


    @Override
    public ITemplate getDefaultTemplate(TemplateType templateType) throws Exception{
        return templateFactory.loadDefaultTemplate(templateType);
    }

    @Override
    public ITemplate saveTemplate(ITemplate template) throws Exception{
        templateFactory.save(template);
        return template;
    }

}
