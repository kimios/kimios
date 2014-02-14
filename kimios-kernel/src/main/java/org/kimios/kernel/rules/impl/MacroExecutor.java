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

import org.kimios.kernel.macro.MacroImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;

public class MacroExecutor extends RuleImpl
{
    private static Logger log = LoggerFactory.getLogger(MacroExecutor.class);

    private Class<? extends MacroImpl> macroClass;

    private Map<String, Object> macroParameters;

    @Override
    public boolean isTrue()
    {
        return true;
    }

    @Override
    public void execute()
    {
        try {
            MacroImpl impl = macroClass.newInstance();

            /*
            *  setting session && context
            */
            impl.setSession(this.getContext().getSession());
            impl.setContext(this.getContext());
            for (Field f : macroClass.getDeclaredFields()) {
                f.setAccessible(true);
                if (macroParameters.containsKey(f.getName())) {
                    f.set(impl, macroParameters.get(f.getName()));
                    if (log.isDebugEnabled()) {
                        log.debug("Set parameter " + f.getName() + " to value " + macroParameters.get(f.getName()));
                    }
                }
            }
            impl.execute();
        } catch (Exception e) {
            log.error("Exception while rule execution " + e.getMessage(), e);
        }
    }
}

