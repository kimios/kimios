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

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.kimios.kernel.events.ContextBuilder;
import org.kimios.kernel.events.EventContext;
import org.kimios.kernel.events.EventHandlerManager;
import org.kimios.kernel.events.GenericEventHandler;
import org.kimios.kernel.events.annotations.DmsEvent;
import org.kimios.kernel.events.annotations.DmsEventOccur;
import org.kimios.kernel.rules.RuleBean;
import org.kimios.kernel.rules.RuleManager;
import org.kimios.utils.spring.ApplicationContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

@Aspect
public class KimiosKernelAspect {


    private static Logger log = LoggerFactory.getLogger(KimiosKernelAspect.class);

    private RuleManager ruleManager;

    private boolean rulesManagementEnabled;

    public RuleManager getRuleManager()
    {
        return ruleManager;
    }

    public void setRuleManager(RuleManager ruleManager)
    {
        this.ruleManager = ruleManager;
    }

    public void init()
    {
        rulesManagementEnabled = ruleManager != null;
        log.info("Rules handler: " + (rulesManagementEnabled ? "enabled" : "disabled"));
    }

    @Around("eventMethodForControllersOnly()")
    public Object wrap(ProceedingJoinPoint pjp) throws Throwable {


        ruleManager = ApplicationContextProvider.loadBean(RuleManager.class);
        rulesManagementEnabled = ruleManager != null;

        Method method = null;
        if(pjp.getSignature() instanceof MethodSignature){
            method = ((MethodSignature) pjp.getSignature()).getMethod();
        }


        if(method != null){

            log.trace(" Rule Manager  " + ruleManager + " | Rule Managerment Enabled " + rulesManagementEnabled);

            EventContext ctx = EventContext.get();
            DmsEvent evt = method.getAnnotation(DmsEvent.class);
            log.trace(evt + " | " +
                    (evt != null ? evt.eventName()[0] : " no event defined. for " + method.getName()));
            if (evt != null) {
                ctx = ContextBuilder.buildContext(evt.eventName()[0], method, pjp.getArgs());
                log.trace("Set event: " + ctx.getEvent().name() + " | " + ctx.getEntity());
            }

            ctx.setCurrentOccur(DmsEventOccur.BEFORE);
            //process events (before state)
            for (GenericEventHandler it : EventHandlerManager.getInstance().handlers()) {
                log.trace("Event Before Context: " + ctx.getEntity());
                it.process(method, pjp.getArgs(), DmsEventOccur.BEFORE, null, ctx);
            }
            //process rules before (before state)
            List<RuleBean> rulesBeans = null;
            if (rulesManagementEnabled) {
                //keep rules bean selected
                rulesBeans = ruleManager.processRulesBefore(method, pjp.getArgs());
            }
            Object ret = pjp.proceed();
            ctx.setCurrentOccur(DmsEventOccur.AFTER);
            EventContext.addParameter("callReturn", ret);
            //process rules (after state)
            if (rulesManagementEnabled) {
                //pass selected beans for the current event/path
                ruleManager.processRulesAfter(rulesBeans, ctx);
            }
            //process handler after
            for (GenericEventHandler it : EventHandlerManager.getInstance().handlers()) {
                log.trace("Event After Context: " + ctx.getEntity());
                it.process(method, pjp.getArgs(), DmsEventOccur.AFTER, ret, ctx);
            }
            EventContext.clear();
            return ret;
        }  else {
            return pjp.proceed();
        }

    }

    @Pointcut("execution(@org.kimios.kernel.events.annotations.DmsEvent * *(..))")
    public void eventMethod(){}

    @Pointcut("execution(* org.kimios.kernel.events.GenericEventHandler+.*(..))")
    public void handlerPoincut() {}


    @Pointcut("eventMethod() && !handlerPoincut()")
    public void eventMethodForControllersOnly(){}

}
