/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
 *
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.webservices.impl;

import org.kimios.kernel.rules.impl.RuleImpl;
import org.kimios.kernel.security.Session;
import org.kimios.kernel.ws.pojo.Rule;
import org.kimios.kernel.ws.pojo.RuleBean;
import org.kimios.kernel.ws.pojo.RuleImplP;
import org.kimios.webservices.CoreService;
import org.kimios.webservices.DMServiceException;
import org.kimios.webservices.RuleService;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.Collection;

@WebService(targetNamespace = "http://kimios.org", serviceName = "RuleService", name = "RuleService")
public class RuleServiceImpl extends CoreService implements RuleService
{
    public String[] getAvailablesRules(String sessionId) throws DMServiceException
    {
        try {
            Session session = getHelper().getSession(sessionId);
            Collection<Class<? extends RuleImpl>> c = ruleController.getRulesClass(session);
            String[] _classes = new String[c.size()];
            int u = 0;
            for (Class<?> item : c) {
                _classes[u++] = item.getName();
            }
            return _classes;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public String getRuleParam(String sessionId, String javaClassName) throws DMServiceException
    {
        try {
            Session session = getHelper().getSession(sessionId);
            return ruleController.getRuleClassParameters(session, javaClassName);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public void createRule(String sessionId, String conditionJavaClass,
            String path, String ruleName, String xmlStream) throws DMServiceException
    {
        try {
            Session session = getHelper().getSession(sessionId);
            ruleController.createRule(session, conditionJavaClass, path, ruleName, xmlStream);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public RuleBean[] getBeans() throws DMServiceException
    {

        ArrayList<RuleBean> beans = new ArrayList<RuleBean>();

        RuleBean b1 = new RuleBean();
        b1.setJavaClass("test");
        b1.setName("toto");
        b1.setPath("/MyWorkspace");
        RuleBean b2 = new RuleBean();
        b2.setJavaClass("test2");
        b2.setName("toto2");
        b2.setPath("/MyWorkspace2");
        beans.add(b1);
        beans.add(b2);

        RuleBean[] totos = new RuleBean[beans.size()];
        int ind = 0;
        for (RuleBean t : beans) {
            totos[ind++] = t;
        }

        return totos;
    }

    public void sendList(RuleBean[] beans) throws DMServiceException
    {
        for (RuleBean item : beans) {
            System.out.println(item.getPath());
        }
    }

    public Rule[] getRuleItems(Rule[] rules) throws DMServiceException
    {

        /*
        *  test
        */

        for (Rule items : rules) {
            System.out.println(items.getJavaClassName());
            for (RuleImplP jk : items.getBeans()) {
                System.out.println(jk.getDmsEvent() + " " + jk.getDmsEventStatus());
            }
        }

        Rule[] tabs = new Rule[2];
        Rule i1 = new Rule();
        i1.setJavaClassName("test");

        RuleImplP[] impls = new RuleImplP[2];
        RuleImplP p1 = new RuleImplP();
        p1.setDmsEvent(1);
        p1.setDmsEventStatus(2);
        RuleImplP p2 = new RuleImplP();
        p2.setDmsEvent(3);
        p2.setDmsEventStatus(4);

        RuleImplP[] impls2 = new RuleImplP[2];
        RuleImplP p3 = new RuleImplP();
        p3.setDmsEvent(1);
        p3.setDmsEventStatus(2);
        RuleImplP p4 = new RuleImplP();
        p4.setDmsEvent(3);
        p4.setDmsEventStatus(4);

        impls[0] = p1;
        impls[1] = p2;

        impls[0] = p3;
        impls[1] = p4;

        i1.setBeans(impls);

        Rule i2 = new Rule();
        i2.setJavaClassName("test2");

        i2.setBeans(impls2);

        tabs[0] = i1;
        tabs[1] = i2;

        return tabs;
    }
}

