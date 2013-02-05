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
package org.kimios.kernel.security;

import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.exception.XMLException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DMEntitySecurityUtil
{
    /**
     * Convenience method Generate securities matrix from a given xml descriptor
     */
    public static Vector<DMEntitySecurity> getDMentitySecuritesFromXml(String xmlStream, DMEntity it)
            throws XMLException
    {
        Vector<DMEntitySecurity> v = new Vector<DMEntitySecurity>();
        try {
            org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new java.io.ByteArrayInputStream(xmlStream.getBytes()));
            Element root = doc.getDocumentElement();
            NodeList list = root.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                if (list.item(i).getNodeName().equalsIgnoreCase("rule")) {
                    v.add(new DMEntitySecurity(
                            it.getUid(),
                            it.getType(),
                            list.item(i).getAttributes().getNamedItem("security-entity-uid").getTextContent(),
                            list.item(i).getAttributes().getNamedItem("security-entity-source").getTextContent(),
                            Integer.parseInt(
                                    list.item(i).getAttributes().getNamedItem("security-entity-type").getTextContent()),
                            Boolean.parseBoolean(list.item(i).getAttributes().getNamedItem("read").getTextContent()),
                            Boolean.parseBoolean(list.item(i).getAttributes().getNamedItem("write").getTextContent()),
                            Boolean.parseBoolean(list.item(i).getAttributes().getNamedItem("full").getTextContent()),
                            it));
                }
            }
        } catch (ParserConfigurationException e) {
            throw new XMLException();
        } catch (SAXException e) {
            throw new XMLException();
        } catch (IOException e) {
            throw new XMLException();
        }
        return v;
    }
}

