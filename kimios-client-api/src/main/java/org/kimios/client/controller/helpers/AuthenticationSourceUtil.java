/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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
package org.kimios.client.controller.helpers;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class allowing to generate XML authentication source fields and
 * create map from XML fields
 *
 * @author jludmann
 */
public class AuthenticationSourceUtil
{

    private String sourceName;

    private String newSourceName;

    private Map<String, String> fields = new HashMap<String, String>();

    public AuthenticationSourceUtil( String sourceName )
    {
        this.sourceName = sourceName;
    }

    /**
     * Get the authentication source name
     */
    public String getName()
    {
        return this.sourceName;
    }

    /**
     * Specify a new authentication source name (for update only)
     */
    public void changeName( String newSourceName )
    {
        this.newSourceName = newSourceName;
    }

    /**
     * Add new field
     */
    public void addField( String key, String value )
    {
        if ( key != null )
        {
            fields.put( key, value );
        }
    }

    /**
     * Set fields
     */
    public AuthenticationSourceUtil setFields( Map<String, String> parameters )
    {
        for ( String key : parameters.keySet() )
        {
            addField( key, parameters.get( key ) );
        }
        return this;
    }

    /**
     * Convert the authentication source to XML
     */
    public String generateXml()
    {
        StringBuffer xml = new StringBuffer( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
        xml.append( "<authentication-source name=\"" );
        xml.append( sourceName );
        xml.append( "\"" );
        if ( newSourceName != null )
        {
            xml.append( " newName=\"" );
            xml.append( newSourceName );
            xml.append( "\" " );
        }
        xml.append( ">\n" );
        for ( String key : fields.keySet() )
        {
            xml.append( "<field name=\"" );
            xml.append( key );
            xml.append( "\" value=\"" );
            xml.append( fields.get( key ) );
            xml.append( "\" />\n" );
        }
        xml.append( "</authentication-source>\n" );
        return xml.toString();
    }

    /**
     * Get a authentication source map from XML fields
     */
    public static Map<String, String> getFields( String xml )
        throws ParserConfigurationException, SAXException, IOException
    {
        Map<String, String> fields = new HashMap<String, String>();
        ByteArrayInputStream in = new ByteArrayInputStream( xml.getBytes() );
        Document d = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( in );
        NodeList nl = d.getDocumentElement().getElementsByTagName( "field" );
        for ( int i = 0; i < nl.getLength(); i++ )
        {
            String name = nl.item( i ).getAttributes().getNamedItem( "name" ).getNodeValue();
            String value = nl.item( i ).getAttributes().getNamedItem( "value" ).getNodeValue();
            fields.put( name, value );
        }
        return fields;
    }

    /**
     * Get a class name list of all implemented authentication sources
     */
    public static List<String> getAvailable( String xml )
        throws ParserConfigurationException, SAXException, IOException
    {
        List<String> availableClassName = new ArrayList<String>();
        ByteArrayInputStream in = new ByteArrayInputStream( xml.getBytes() );
        Document d = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( in );
        NodeList nl = d.getDocumentElement().getElementsByTagName( "authentication-source" );
        for ( int i = 0; i < nl.getLength(); i++ )
        {
            availableClassName.add( nl.item( i ).getAttributes().getNamedItem( "class-name" ).getNodeValue() );
        }
        return availableClassName;
    }

    public static List<String> getAvailableParams( String xml )
        throws ParserConfigurationException, SAXException, IOException
    {
        List<String> availableParams = new ArrayList<String>();
        ByteArrayInputStream in = new ByteArrayInputStream( xml.getBytes() );
        Document d = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( in );
        NodeList nl = d.getDocumentElement().getElementsByTagName( "field" );
        for ( int i = 0; i < nl.getLength(); i++ )
        {
            availableParams.add( nl.item( i ).getAttributes().getNamedItem( "name" ).getNodeValue() );
        }
        return availableParams;
    }
}

