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

package org.kimios.utils.configuration;

import org.kimios.exceptions.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  @author Fabien Alin (Farf) <fabien.alin@gmail.com>
 *
 *      Configuration Holder Based on Hold Xml Format. Will be deprecated in the next release
 *
 */
public class XmlConfigurationHolder implements ConfigurationHolder
{
    private static Logger log = LoggerFactory.getLogger(XmlConfigurationHolder.class);

    private String configFilePath;

    private static String CONFIG_FILE_NAME = "/kimios.xml";

    private static Map<String, String> values = new HashMap<String, String>();

    public void init(String _configFilePath) throws ConfigException
    {
        if (_configFilePath == null) {
            throw new ConfigException("Config file path has not been initialized");
        }
        configFilePath = _configFilePath;
        values = new HashMap<String, String>();
        try {
            Document doc =
                    DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(configFilePath + CONFIG_FILE_NAME));
            Element root = doc.getDocumentElement();
            NodeList list = root.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {

                if (list.item(i).getNodeName().equalsIgnoreCase("key")) {
                    values.put(list.item(i).getAttributes().getNamedItem("name").getTextContent(),
                            list.item(i).getAttributes().getNamedItem("value").getTextContent());
                }
            }

            if (log.isDebugEnabled()) {
                for (String k : values.keySet()) {
                    log.debug("Key:" + k + " Value:" + values.get(k));
                }
            }
        } catch (SAXException se) {
            throw new ConfigException("Config file was unparsable : " + se.getMessage());
        } catch (ParserConfigurationException pce) {
            throw new ConfigException("Config file was unparsable : " + pce.getMessage());
        } catch (IOException io) {
            throw new ConfigException("Config file was unparsable : " + io.getMessage());
        }
    }

    public boolean exists(String keyOrPrefix)
    {
        return values.containsKey(keyOrPrefix);
    }

    public Object getValue(String key)
    {
        return values.get(key);
    }

    public String getStringValue(String key)
    {
        return values.get(key);
    }

    public List<String> getValues(String prefix)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void refresh() throws ConfigException
    {
        init(configFilePath);
    }
}
