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

package org.kimios.templates.impl.factory;

import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.templates.model.Template;
import org.kimios.api.templates.ITemplate;
import org.kimios.api.templates.TemplateType;

/**
 * Created by farf on 21/03/16.
 */
public class TemplateFactory extends HFactory {


    public ITemplate loadDefaultTemplate(TemplateType templateType){

        String q = "from Template t where t.type = :templateType and t.defaultTemplate is true";
        return (Template) getSession().createQuery(q)
                .setParameter("templateType", templateType)
                .uniqueResult();

    }

    public ITemplate loadTemplate(long id){
        String q = "from Template t where t.id = :templateId";
        return (Template)getSession().createQuery(q)
                .setParameter("templateId", id)
                .uniqueResult();
    }

    public ITemplate loadTemplate(String templateName){
        String q = "from Template t where t.name = :templateName";
        return (Template)getSession().createQuery(q)
                .setParameter("templateName", templateName)
                .uniqueResult();
    }

    public void save(ITemplate template){
        getSession().saveOrUpdate(template);
        getSession().flush();
    }
}
