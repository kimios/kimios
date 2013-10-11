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
package org.kimios.kernel.user.impl.factory.hibernate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.criterion.Restrictions;
import org.kimios.kernel.exception.AuthenticationSourceException;
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.user.AuthenticationSourceBean;
import org.kimios.kernel.user.AuthenticationSourceParamsFactory;
import org.kimios.kernel.xml.XSDException;
import org.kimios.kernel.xml.XSDUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The Hibernate authentication sources parameters factory
 */
public class HAuthenticationSourceParamsFactory extends HFactory implements AuthenticationSourceParamsFactory
{
    /**
     * Get a XML stream containing authentication source parameters for a given authentication source name
     */
    public final String getParams(String name)
    {
        AuthenticationSourceBean beanAuth =
                (AuthenticationSourceBean) getSession().createCriteria(AuthenticationSourceBean.class)
                        .add(Restrictions.eq("name", name))
                        .uniqueResult();

        Map<String, String> params = beanAuth.getParameters();

        /*
            list class params, to add them if not already created inside database
         */

        List<String> missingParams = new ArrayList<String>();
        try{

            Class<?> c = Class.forName(beanAuth.getJavaClass());
            for (Field f : c.getDeclaredFields()) {
               missingParams.add(f.getName());
            }
        }catch (Exception e){

        }





        StringBuffer xml = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<authentication-source name=\"" + name + "\">\n");
        for (String fieldName : params.keySet()) {
            xml.append("<field name=\"");
            xml.append(fieldName);
            xml.append("\" value=\"");
            xml.append(params.get(fieldName));
            xml.append("\" />\n");
            missingParams.remove(fieldName);
        }
        for(String fieldName: missingParams){
            xml.append("<field name=\"");
            xml.append(fieldName);
            xml.append("\" value=\"");
            xml.append("\" />\n");
        }

        xml.append("</authentication-source>\n");
        return xml.toString();
    }

    /**
     * Create authentication source parameters with XML parameters for a new given authentication source name
     */
    public final void createParams(String sourceName, String xml) throws AuthenticationSourceException, XSDException
    {
        try {
            new XSDUtil().validateXmlStream(xml, "authentication-source.xsd");
            NodeList fields = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                    new ByteArrayInputStream(xml.getBytes())).getDocumentElement().getElementsByTagName("field");

            AuthenticationSourceBean beanAuth =
                    (AuthenticationSourceBean) getSession().createCriteria(AuthenticationSourceBean.class)
                            .add(Restrictions.eq("name", sourceName))
                            .uniqueResult();

            for (int i = 0; i < fields.getLength(); i++) {
                NamedNodeMap map = fields.item(i).getAttributes();
                beanAuth.getParameters()
                        .put(map.getNamedItem("name").getNodeValue(), map.getNamedItem("value").getNodeValue());
            }
            getSession().update(beanAuth);
            getSession().flush();
        } catch (ParserConfigurationException e) {
            throw new AuthenticationSourceException(e, e.getMessage());
        } catch (SAXException e) {
            throw new AuthenticationSourceException(e, e.getMessage());
        } catch (IOException e) {
            throw new AuthenticationSourceException(e, e.getMessage());
        } catch (XSDException e) {
            throw new XSDException(e);
        }
    }

    /**
     * Update authentication source parameters with XML parameters for a given authentication source name
     */
    public void updateParams(String sourceName, String xml, boolean enableSso, boolean enableMailCheck) throws AuthenticationSourceException, XSDException
    {
        try {
            new XSDUtil().validateXmlStream(xml, "authentication-source.xsd");
            Element root = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                    new ByteArrayInputStream(xml.getBytes())).getDocumentElement();
            NodeList fields = root.getElementsByTagName("field");

            NamedNodeMap m = root.getAttributes();
            AuthenticationSourceBean beanAuth =
                    (AuthenticationSourceBean) getSession().createCriteria(AuthenticationSourceBean.class)
                            .add(Restrictions.eq("name", sourceName))
                            .uniqueResult();

            for (int i = 0; i < fields.getLength(); i++) {
                NamedNodeMap map = fields.item(i).getAttributes();
                beanAuth.getParameters()
                        .put(map.getNamedItem("name").getNodeValue(), map.getNamedItem("value").getNodeValue());
            }

            beanAuth.setEnableSso(enableSso);
            beanAuth.setEnableMailCheck(enableMailCheck);
            getSession().update(beanAuth);
            getSession().flush();
        } catch (ParserConfigurationException e) {
            throw new AuthenticationSourceException(e.getMessage());
        } catch (SAXException e) {
            throw new AuthenticationSourceException(e.getMessage());
        } catch (IOException e) {
            throw new AuthenticationSourceException(e.getMessage());
        } catch (XSDException e) {
            throw new XSDException(e);
        }
    }
}

