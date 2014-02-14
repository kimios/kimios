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
package org.kimios.kernel.index;

import org.kimios.exceptions.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class FiltersMapper
{
    private static Logger log = LoggerFactory.getLogger(FiltersMapper.class);

    private static Map<String, IndexFilter> filters = new HashMap<String, IndexFilter>();

    private static FiltersMapper instance;

    private FiltersMapper()
    {
    }

    synchronized public static FiltersMapper getInstance()
    {
        if (instance == null) {
            instance = new FiltersMapper();
        }
        return instance;
    }

    public IndexFilter getFiltersFor(String extension)
    {
        if (extension != null) {
            return filters.get(extension.toLowerCase());
        } else {
            return null;
        }
    }

    public void loadFiltersMapping() throws ConfigException
    {
        loadFiltersMapping(null);
    }

    public void loadFiltersMapping(InputStream xml) throws ConfigException
    {
        if (xml == null) {
            xml = FiltersMapper.class.getClassLoader().getResourceAsStream(
                    "index-filter-mapping.xml");
        }

        log.info("[kimios Indexer] - Loading filters configuration ...");
        try {
            Document doc = null;
            try {
                DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
                DocumentBuilder constructeur = fabrique.newDocumentBuilder();
                doc = constructeur.parse(xml);
            } catch (Exception e) {
                log.error("*** Kernel*** : error while loading filters file", e);
            }
            if (doc != null) {
                Element elem = doc.getDocumentElement();
                NodeList list = elem.getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                    NodeList list2 = list.item(i).getChildNodes();
                    Vector<String> extensionName = new Vector<String>();
                    String className = "";
                    for (int j = 0; j < list2.getLength(); j++) {
                        if (list2.item(j).getNodeName().equals("filter-class")) {
                            className = list2.item(j).getTextContent();
                        }
                        if (list2.item(j).getNodeName().equals("file-extension")) {
                            extensionName.add(list2.item(j).getTextContent().toLowerCase());
                        }
                    }
                    //loading class :
                    try {
                        for (String ex : extensionName) {
                            filters.put(ex, (IndexFilter) Class.forName(className).newInstance());
                        }
                    } catch (ClassNotFoundException cnfe) {
                        log.error("[kimios Indexer] - ERROR : unable to load " + className);
                    } catch (ClassCastException cce) {
                        log.error("[kimios Indexer] - ERROR : " + className + " isn't an Index Filter...");
                    }
                }
            } else {
                log.error("[kimios Indexer] - ERROR : unable to open cconf stream ");
            }
        } catch (Exception e) {
            log.error("[kimios Indexer] - ERROR : unable to read  conf stream ");
        }
        log.info("[kimios Indexer] - Index filters Loaded");
        for (String ex : filters.keySet()) {
            log.info("[kimios Indexer] - Extension : [" + ex + "] --> [" + filters.get(ex).getClass().getName() + "]");
        }
    }
}

