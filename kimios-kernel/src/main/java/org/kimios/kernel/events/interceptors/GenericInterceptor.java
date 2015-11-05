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
package org.kimios.kernel.events.interceptors;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.kimios.kernel.events.ContextBuilder;
import org.kimios.kernel.events.IEventHandlerManager;
import org.kimios.kernel.events.model.EventContext;
import org.kimios.kernel.events.GenericEventHandler;
import org.kimios.kernel.events.model.annotations.DmsEvent;
import org.kimios.kernel.events.model.annotations.DmsEventOccur;
import org.kimios.kernel.rules.model.RuleBean;
import org.kimios.kernel.rules.RuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GenericInterceptor implements MethodInterceptor
{
    private static Logger log = LoggerFactory.getLogger(GenericInterceptor.class);


    public GenericInterceptor(IEventHandlerManager eventHandlerManager, RuleManager ruleManager){
        this.ruleManager = ruleManager;
        this.eventHandlerManager = eventHandlerManager;
    }

    private IEventHandlerManager eventHandlerManager;

    private RuleManager ruleManager;

    private boolean rulesManagementEnabled;


    public void init()
    {
        rulesManagementEnabled = ruleManager != null;
        log.info("Rules handler: " + (rulesManagementEnabled ? "enabled" : "disabled"));
    }

    public Object invoke(MethodInvocation arg0) throws Throwable
    {
        EventContext ctx = EventContext.get();
        DmsEvent evt = arg0.getMethod().getAnnotation(DmsEvent.class);
        log.trace(evt + " | " +
                (evt != null ? evt.eventName()[0] : " no event defined. for " + arg0.getMethod().getName()));
        if (evt != null) {
            ctx = ContextBuilder.buildContext(evt.eventName()[0], arg0.getMethod(), arg0.getArguments());
            log.trace("Set event: " + ctx.getEvent().name());
        }

        ctx.setCurrentOccur(DmsEventOccur.BEFORE);
        //process events (before state)
        for (GenericEventHandler it : eventHandlerManager.handlers()) {
            it.process(arg0.getMethod(), arg0.getArguments(),  DmsEventOccur.BEFORE, null, ctx);
        }
        //process rules before (before state)
        List<RuleBean> rulesBeans = null;
        if (rulesManagementEnabled) {
            //keep rules bean selected
            rulesBeans = ruleManager.processRulesBefore(arg0.getMethod(), arg0.getArguments());
        }
        Object ret = arg0.proceed();
        ctx.setCurrentOccur(DmsEventOccur.AFTER);
        EventContext.addParameter("callReturn", ret);
        //process rules (after state)
        if (rulesManagementEnabled) {
            //pass selected beans for the current event/path
            ruleManager.processRulesAfter(rulesBeans, ctx);
        }
        //process handler after
        for (GenericEventHandler it : eventHandlerManager.handlers()) {
            it.process(arg0.getMethod(), arg0.getArguments(), DmsEventOccur.AFTER, ret, ctx);
        }
        EventContext.clear();
        return ret;
    }
}

