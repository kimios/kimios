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
package org.kimios.kernel.events;

import org.kimios.api.events.IEventContext;
import org.kimios.kernel.events.model.EventContext;
import org.kimios.api.events.annotations.DmsEvent;
import org.kimios.api.events.annotations.DmsEventName;
import org.kimios.api.events.annotations.DmsEventOccur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public abstract class GenericEventHandler implements org.kimios.api.EventHandler {

    private static Logger logger = LoggerFactory.getLogger(GenericEventHandler.class);

    protected Set<Method> methods;

    public Set<Method> getMethods()
    {
        return this.methods;
    }

    @Override
    final public IEventContext process(Method method, Object[] arguments, DmsEventOccur _when, Object methodReturn,
                                       IEventContext iContext) throws Throwable
    {

        EventContext ctx = (EventContext)iContext;
        DmsEvent evt = method.getAnnotation(DmsEvent.class);
        if (evt != null) {
            for (Method it : this.getClass().getDeclaredMethods()) {
                DmsEvent st = null;
                st = it.getAnnotation(DmsEvent.class);
                if (st != null) {

                    DmsEventName[] names = st.eventName();
                    List<DmsEventName> nameList = Arrays.asList(names);
                    for (DmsEventName nCurrent : evt.eventName()) {

                        if (nameList.contains(nCurrent) && st.when().equals(_when)) {

                            if(logger.isTraceEnabled()){
                                logger.trace(" will call " + it.getName() + " on " + this.getClass().getName() + " with "
                                        + ctx + " ( " + ctx.getEntity() + ") on event " + nCurrent.name() + ". accepted ? " + nameList.contains(nCurrent) + " / " + _when.name());
                            }

                            /*
                            *  Generate the before context
                            */
                            Object[] tab = new Object[1];
                            tab[0] = arguments;
                            try {
                                it.invoke(this, tab[0], methodReturn, ctx);
                            } catch (InvocationTargetException ex) {
                                /*
                                   Get thrown exception
                                */
                                throw ex.getCause();
                            }
                        }
                    }
                }
            }
        }
        return ctx;
    }


    final public boolean isJoined(Method mt)
    {
        return methods.contains(mt);
    }
}

