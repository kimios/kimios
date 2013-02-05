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
package org.kimios.kernel.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class XSDUtil
{
    private String xsdPath = "org/kimios/kernel/xml/xsd";

    private static Logger log = LoggerFactory.getLogger(XSDUtil.class);

    public void validateXmlStream(String xmlStream, String xsdFileName) throws XSDException
    {
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            Source xsdSource = new StreamSource(
                    XSDUtil.class.getClassLoader().getResourceAsStream(xsdPath + "/" + xsdFileName));
            Schema schema = schemaFactory.newSchema(xsdSource);
            InputStream xmlInputStream = new ByteArrayInputStream(xmlStream.getBytes());
            Source xmlSource = new StreamSource(xmlInputStream);
            schema.newValidator().validate(xmlSource);
        } catch (SAXException se) {
            throw new XSDException(se);
        } catch (IOException ioe) {
            throw new XSDException(ioe);
        } catch (Exception e) {
            throw new XSDException(e);
        }
    }
}

