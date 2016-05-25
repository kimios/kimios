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
package org.kimios.kernel.rules;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.kimios.api.events.annotations.DmsEventName;
import org.kimios.kernel.events.model.EventContext;
import org.kimios.api.events.annotations.DmsEvent;
import org.kimios.api.events.annotations.DmsEventOccur;
import org.kimios.kernel.rules.impl.RuleImpl;
import org.kimios.kernel.rules.model.EventBean;
import org.kimios.kernel.rules.model.RuleBean;
import org.kimios.utils.context.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RuleManager
{
    private static Logger log = LoggerFactory.getLogger(RuleManager.class);

    private static String SLASH_SEPARATOR = "/";

    private ContextHolder contextHolder;

    private RuleBeanFactory ruleBeanFactory;

    public RuleBeanFactory getRuleBeanFactory()
    {
        return ruleBeanFactory;
    }

    public void setRuleBeanFactory(RuleBeanFactory ruleBeanFactory)
    {
        this.ruleBeanFactory = ruleBeanFactory;
    }

    public ContextHolder getContextHolder() {
        return contextHolder;
    }

    public void setContextHolder(ContextHolder contextHolder) {
        this.contextHolder = contextHolder;
    }

    private ObjectMapper objectMapper;

    public RuleManager(){
        objectMapper = new ObjectMapper();
    }

    public List<RuleBean> processRulesBefore(Method method, Object[] arguments) throws Throwable
    {
        DmsEvent evt = method.getAnnotation(DmsEvent.class);
        EventContext ctx = null;
        String path = null;
        List<RuleBean> beans = null;
        if (evt != null) {
            ctx = EventContext.get();
            if (ctx != null && ctx.getEntity() != null) {
                path = ctx.getEntity().getPath();
                String[] allPath = path.split(SLASH_SEPARATOR);
                String pPath = "";
                List<String> fPaths = new ArrayList<String>();
                for (String aP : allPath) {
                    aP = pPath + aP;
                    pPath = aP + SLASH_SEPARATOR;
                    if (aP.equalsIgnoreCase("")) {
                        aP = "/";
                    }
                    fPaths.add(aP);
                }

                if (log.isTraceEnabled()) {
                    for (String finalPaths : fPaths) {
                        log.trace("Fpath: " + finalPaths);
                    }
                }
                path = path.substring(0, path.lastIndexOf("/"));
                beans = ruleBeanFactory.loadConditionByEventAndPath(ctx.getEvent(), path, fPaths);
                if (beans != null && beans.size() > 0) {
                    evaluate(beans, evt.eventName()[0], DmsEventOccur.BEFORE, ctx);
                }
            }
        }
        return beans;
    }

    public void processRulesAfter(List<RuleBean> beans, DmsEventName eventName, EventContext ctx) throws Throwable
    {
        if (ctx != null && beans != null && beans.size() > 0) {
            evaluate(beans, eventName, DmsEventOccur.AFTER, ctx);
        }
    }

    private void evaluate(List<RuleBean> beans, DmsEventName eventName, DmsEventOccur occur, EventContext ctx)
    {
        EventBean currentEvent = new EventBean(eventName.ordinal(), occur.ordinal());
        if(log.isDebugEnabled()){
            log.debug(eventName.name() + "/" +eventName.ordinal() + ", Status: " + occur.name() + " / " +
                    occur.ordinal());
            log.debug("Loaded rules count: " + beans.size());
        }

        for (RuleBean each : beans) {
            if(log.isDebugEnabled()){
                log.debug(occur.name() + " " + eventName.name() + ": " + each.getName() + " Match event: " +
                        each.getEvents().contains(currentEvent));
            }
            if (each.getEvents().contains(currentEvent)) {
                try {
                    Class<?> cClass = Class.forName(each.getJavaClass());
                    cClass.asSubclass(RuleImpl.class);
                    RuleImpl liveInstance = (RuleImpl) cClass.newInstance();
                    /*
                    *  set true parameters (reflection)
                    */
                    for (String fName : each.getParameters().keySet()) {
                        if(log.isDebugEnabled()) {
                            log.debug(
                                    "Set parameter " + fName + ": " + each.getParameters().get(fName).getClass().getName());
                            log.debug("Set parameter " + fName + ": " + each.getParameters().get(fName).toString());
                        }
                        Field f = cClass.getDeclaredField(fName);
                        f.setAccessible(true);
                        if (f.getType().equals(Class.class)) {
                            f.set(liveInstance, Class.forName(each.getParameters().get(fName).toString()));
                        } else {
                            f.set(liveInstance, each.getParameters().get(fName));
                        }
                    }

                    //load others parameters from json
                    if(!StringUtils.isEmpty(each.getParametersJson())){
                        try{

                            Map<String, String> result =
                                    objectMapper.readValue(each.getParametersJson(),
                                            new TypeReference<Map<String, String>>() {
                                            });
                            each.setParametersData(result);
                            liveInstance.setParameters(result);
                        } catch (Exception ex){
                            log.error("error while parsing rule parameters from json", ex);
                        }
                    }

                    liveInstance.setConditionContext(ctx);
                    liveInstance.setContextHolder(contextHolder);
                    if (liveInstance.isTrue()) {
                        liveInstance.execute();
                    }
                } catch (Exception e) {
                    log.error("exception while handling rule", e);
                }
            }
        }
    }
}

