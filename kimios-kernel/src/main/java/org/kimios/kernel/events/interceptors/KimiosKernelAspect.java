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
import org.aspectj.lang.annotation.DeclarePrecedence;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.dms.model.Folder;
import org.kimios.kernel.dms.model.Workspace;
import org.kimios.kernel.events.ContextBuilder;
import org.kimios.kernel.events.IEventHandlerManager;
import org.kimios.kernel.events.model.EventContext;
import org.kimios.kernel.events.GenericEventHandler;
import org.kimios.api.events.annotations.DmsEvent;
import org.kimios.api.events.annotations.DmsEventOccur;
import org.kimios.kernel.rules.model.RuleBean;
import org.kimios.kernel.rules.RuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.lang.reflect.Method;
import java.util.List;

@Aspect
@DeclarePrecedence("AnnotationTransactionAspect")
public class KimiosKernelAspect {


    private static Logger log = LoggerFactory.getLogger(KimiosKernelAspect.class);

    private RuleManager ruleManager;

    private IEventHandlerManager eventHandlerManager;

    private boolean rulesManagementEnabled;

    public RuleManager getRuleManager()
    {
        return ruleManager;
    }

    public void setRuleManager(RuleManager ruleManager)
    {

        this.ruleManager = ruleManager;
        log.debug("====> RULE MANAGER DEFINED TO " + ruleManager);

    }

    public IEventHandlerManager getEventHandlerManager() {
        return eventHandlerManager;
    }

    public void setEventHandlerManager(IEventHandlerManager eventHandlerManager) {
        this.eventHandlerManager = eventHandlerManager;
        log.debug("====> EVENT HANDLER MANAGER DEFINED TO " + eventHandlerManager);
    }

    public void init()
    {
        rulesManagementEnabled = ruleManager != null;
        log.info("Rules handler: " + (rulesManagementEnabled ? "enabled" : "disabled"));
    }

    @Around("eventMethodForControllersOnly()")
    public Object wrap(ProceedingJoinPoint pjp) throws Throwable {


        rulesManagementEnabled = ruleManager != null;

        Method method = null;
        if(pjp.getSignature() instanceof MethodSignature){
            method = ((MethodSignature) pjp.getSignature()).getMethod();
        }


        if(method != null){

            if(log.isTraceEnabled()){
                log.trace("Rule Management Enabled: {}. ManagerRef: {}", rulesManagementEnabled, ruleManager);
            }
            EventContext ctx = EventContext.get();
            DmsEvent evt = method.getAnnotation(DmsEvent.class);
            if(log.isTraceEnabled()) {
                log.trace("{} ", evt, method.getName());
            }
            if (evt != null) {
                ctx = ContextBuilder.buildContext(evt.eventName()[0], method, pjp.getArgs());
                if(log.isTraceEnabled()) {
                    log.trace("on method {}, defined event is: {}. Found entity is {}",
                            method.getName(),
                            ctx.getEvent().name(),
                            ctx.getEntity());
                }
            }

            ctx.setCurrentOccur(DmsEventOccur.BEFORE);
            //process events (before state)

            List<RuleBean> rulesBeans = null;
            if(evt != null){
                if(log.isTraceEnabled()){
                    log.trace("event entity context {} ", ctx.getEntity());
                }
                for (GenericEventHandler it : eventHandlerManager.handlers()) {
                    if(log.isTraceEnabled()) {
                        log.trace("BEFORE {} processing event handler {}", evt.eventName(), it.getClass().getName());
                    }
                    it.process(method, pjp.getArgs(), DmsEventOccur.BEFORE, null, ctx);
                }
                //process rules before (before state)
                if (rulesManagementEnabled) {
                    //keep rules bean selected
                    rulesBeans = ruleManager.processRulesBefore(method, pjp.getArgs());
                    if(log.isTraceEnabled()) {
                        log.trace("BEFORE {} following processing rules, rules beans count is {}", evt.eventName(),
                                rulesBeans != null ? rulesBeans.size() : 0);
                    }
                }
            }

            Object ret = pjp.proceed();
            ctx.setCurrentOccur(DmsEventOccur.AFTER);
            if(evt != null){
                //IMPORTANT: reset event. Should reset Context Parameters ?
                ctx.setEvent(evt.eventName()[0]);
            }
            EventContext.addParameter("callReturn", ret);
            //Reset Entity with generated one if exists
            //TODO: Move processed entity to key "entity", and generalize in all Kernel Controllers
            if(EventContext.getParameters().get("document") != null){
                ctx.setEntity((Document)EventContext.getParameters().get("document"));
            } else if(EventContext.getParameters().get("workspace") != null){
                ctx.setEntity((Workspace)EventContext.getParameters().get("workspace"));
            } else if(EventContext.getParameters().get("folder") != null){
                ctx.setEntity((Folder)EventContext.getParameters().get("folder"));
            }
            if(evt != null){
                //process rules (after state)
                if (rulesManagementEnabled && rulesBeans != null && rulesBeans.size() > 0) {
                    //pass selected beans for the current event/path
                    ruleManager.processRulesAfter(rulesBeans, evt.eventName()[0], ctx);
                }
                //process handler after
                for (GenericEventHandler it : eventHandlerManager.handlers()) {
                    if(log.isTraceEnabled()) {
                        log.trace("AFTER {} processing event handler {}", evt.eventName(), it.getClass().getName());
                    }
                    it.process(method, pjp.getArgs(), DmsEventOccur.AFTER, ret, ctx);
                }
            }

            EventContext.clear();
            return ret;
        }  else {
            return pjp.proceed();
        }

    }

    @Pointcut("execution(@org.kimios.api.events.annotations.DmsEvent * *(..))")
    public void eventMethod(){}

    @Pointcut("execution(* org.kimios.kernel.events.GenericEventHandler+.*(..))")
    public void handlerPoincut() {}


    @Pointcut("eventMethod() && !handlerPoincut()")
    public void eventMethodForControllersOnly(){}

}
