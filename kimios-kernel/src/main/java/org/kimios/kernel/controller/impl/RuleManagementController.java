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
package org.kimios.kernel.controller.impl;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.IRuleManagementController;
import org.kimios.kernel.dms.model.DMEntity;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.rules.model.EventBean;
import org.kimios.kernel.rules.model.RuleBean;
import org.kimios.kernel.rules.RuleBeanFactory;
import org.kimios.kernel.rules.impl.RuleImpl;
import org.kimios.kernel.security.SecurityAgent;
import org.kimios.kernel.security.model.Session;
import org.kimios.utils.extension.ClassFinder;
import org.kimios.kernel.utils.XmlClassSerializer;
import org.kimios.utils.extension.ExtensionRegistryManager;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

@Transactional
public class RuleManagementController extends AKimiosController implements IRuleManagementController
{
    RuleBeanFactory ruleFactory;

    public RuleBeanFactory getRuleFactory()
    {
        return ruleFactory;
    }

    public void setRuleFactory(RuleBeanFactory ruleFactory)
    {
        this.ruleFactory = ruleFactory;
    }

    public void deleteRule(long idRule) throws DataSourceException
    {
        // TODO Auto-generated method stub
    }

    public void getRule(Session session, long uid) throws DataSourceException
    {
        // TODO Auto-generated method stub

    }

    public String getRuleClassParameters(Session session, String javaClassName)
            throws DataSourceException, Exception
    {
        /*
        *  To do : right management
        *
        */
        String xml = XmlClassSerializer.getXmlDescriptor(javaClassName);
        return xml;
    }

    public void getRules(Session session) throws DataSourceException
    {
        // TODO Auto-generated method stub

    }

    public void getRulesByPath(Session session, String path)
            throws DataSourceException
    {
        // TODO Auto-generated method stub

    }

    public Collection<Class<? extends RuleImpl>> getRulesClass(Session session) throws DataSourceException
    {

        /*
        *  To do : right management
        *
        */
        Collection<Class<? extends  RuleImpl>> ruleClasses =
                ExtensionRegistryManager.itemsAsClass(RuleImpl.class);
        return ruleClasses;
    }

//  public void saveRule(Session session, String ruleJavaClass,
//      Set<EventBean> events, Map<String, Serializable> parameters,
//      String path, String ruleName) throws DataSourceException, ConfigException, AccessDeniedException {
//    DMEntity entity = dmsFactoryInstantiator.getDmEntityFactory().getEntity(path);
//    if(SecurityAgent.getInstance().isReadable(entity, session.getUserName(), session.getUserSource(), session.getGroups())){
//      RuleBean cb = new RuleBean();
//      cb.setJavaClass(ruleJavaClass);
//      /*
//       *  Generating values from xml
//       */
//      cb.setParameters(parameters);
//      cb.setEvents(events);
//      cb.setRuleOwner(session.getUserName());
//      cb.setRuleOwnerSource(session.getUserSource());
//      cb.setRuleCreationDate(new Date());
//      cb.setRuleUpdateDate(new Date());
//      cb.setPath(path);
//      cb.setName(ruleName);
//      ruleFactory.save(cb);
//    }else
//      throw new AccessDeniedException();
//  }

    public void createRule(Session session, String ruleJavaClass, String path, String ruleName, String xmlStream)
            throws DataSourceException, ConfigException, AccessDeniedException, SAXException, IOException,
            ParserConfigurationException
    {
        DMEntity entity = dmsFactoryInstantiator.getDmEntityFactory().getEntity(path);
        if (SecurityAgent.getInstance()
                .isReadable(entity, session.getUserName(), session.getUserSource(), session.getGroups()))
        {
//      new XSDUtil().validateXmlStream(xmlStream, xsdFileName);
            org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new java.io.ByteArrayInputStream(xmlStream.getBytes()));
            Element root = doc.getDocumentElement();
            NodeList nodes = root.getChildNodes();
            Set<EventBean> events = new HashSet<EventBean>();
            Map<String, Serializable> parameters = new HashMap<String, Serializable>();

            for (int i = 0; i < nodes.getLength(); ++i) {
                Node node = nodes.item(i);
                NamedNodeMap map = node.getAttributes();
                if (node.getNodeName().equalsIgnoreCase("event")) {
                    int name = Integer.parseInt(map.getNamedItem("name").getTextContent());
                    int status = Integer.parseInt(map.getNamedItem("status").getTextContent());
                    events.add(new EventBean(name, status));
                } else if (node.getNodeName().equalsIgnoreCase("parameter")) {
                    String key = map.getNamedItem("key").getTextContent();
                    String value = map.getNamedItem("value").getTextContent();
                    parameters.put(key, value);
                }
            }

            RuleBean cb = new RuleBean();
            cb.setJavaClass(ruleJavaClass);
            cb.setParameters(parameters);
            cb.setEvents(events);
            cb.setRuleOwner(session.getUserName());
            cb.setRuleOwnerSource(session.getUserSource());
            cb.setRuleCreationDate(new Date());
            cb.setRuleUpdateDate(new Date());
            cb.setPath(path);
            cb.setName(ruleName);
            ruleFactory.save(cb);
        } else {
            throw new AccessDeniedException();
        }
    }
}

