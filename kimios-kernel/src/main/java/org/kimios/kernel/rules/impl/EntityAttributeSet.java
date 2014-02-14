/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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

import org.kimios.kernel.dms.DMEntityImpl;
import org.kimios.kernel.dms.FactoryInstantiator;
import org.kimios.kernel.dms.extension.ValueGenerator;
import org.kimios.kernel.dms.extension.impl.DMEntityAttribute;

public class EntityAttributeSet extends RuleImpl
{
    private String attributeName;

    private Integer entityType;

    private String valueGeneratorClass;

    @Override
    public boolean isTrue()
    {
        if (entityType != null && entityType > 0) {
            return entityType == ctx.getEntity().getType();
        } else {
            return false;
        }
    }

    @Override
    public void execute() throws Exception
    {
        //generate value
        //test
        DMEntityImpl entity = null;
        Long callReturn = (Long) ctx.getParameters().get("callReturn");
        entity = (DMEntityImpl) FactoryInstantiator.getInstance().getDmEntityFactory().getEntity(callReturn);
        if (entity == null) {
            throw new NullPointerException("Entity is null ...");
        }
        ValueGenerator gen = (ValueGenerator) Class.forName(valueGeneratorClass).newInstance();
        gen.setEntity(entity);
        String value = gen.getValue();
        if (value != null) {
            DMEntityAttribute attr = new DMEntityAttribute();
            attr.setIndexed(true);
            attr.setValue(value);
            entity.getAttributes().put(attributeName, attr);
            FactoryInstantiator.getInstance().getDmEntityFactory().updateEntity(entity);
        }
    }
}

