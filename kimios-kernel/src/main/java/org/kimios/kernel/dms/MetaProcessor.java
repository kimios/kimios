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

package org.kimios.kernel.dms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.model.*;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.XMLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Created by farf on 11/13/14.
 */
public class MetaProcessor {

    private static Logger logger = LoggerFactory.getLogger(MetaProcessor.class);

    public static List<MetaValue> getMetaValuesFromXML(String xmlStream, long uid)
            throws XMLException, DataSourceException, ConfigException {
        DocumentVersion dv = FactoryInstantiator.getInstance().getDocumentVersionFactory().getDocumentVersion(uid);
        Vector<MetaValue> v = new Vector<MetaValue>();
        if (dv != null) {
            try {
                org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                        .parse(new java.io.ByteArrayInputStream(xmlStream.getBytes()));
                org.w3c.dom.Element root = doc.getDocumentElement();
                NodeList list = root.getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                    if (list.item(i).getNodeName().equalsIgnoreCase("meta")) {
                        long metaUid =
                                Long.parseLong(list.item(i).getAttributes().getNamedItem("uid").getTextContent());
                        Meta m = FactoryInstantiator.getInstance().getMetaFactory().getMeta(metaUid);
                        logger.debug("Re meta value {}", list.item(i).getTextContent());
                        MetaValue mv = toMetaValue(m.getMetaType(), dv, m, list.item(i).getTextContent());
                        if (mv != null) {
                            v.add(mv);
                        }
                    }
                }
            } catch (Exception e) {
                throw new XMLException();
            }
        }
        return v;
    }

    public static List<MetaValue> getMetaValuesFromXML(String xmlStream)
            throws XMLException, DataSourceException, ConfigException {
        List<MetaValue> v = new ArrayList<MetaValue>();
        try {
            org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new java.io.ByteArrayInputStream(xmlStream.getBytes()));
            org.w3c.dom.Element root = doc.getDocumentElement();
            NodeList list = root.getChildNodes();
            //empty document version
            DocumentVersion version = new DocumentVersion();
            for (int i = 0; i < list.getLength(); i++) {
                if (list.item(i).getNodeName().equalsIgnoreCase("meta")) {
                    long metaUid =
                            Long.parseLong(list.item(i).getAttributes().getNamedItem("uid").getTextContent());
                    Meta m = FactoryInstantiator.getInstance().getMetaFactory().getMeta(metaUid);
                    logger.debug("Re meta value {}", list.item(i).getTextContent());
                    MetaValue mv = toMetaValue(m.getMetaType(), version, m, list.item(i).getTextContent());
                    if (mv != null) {
                        v.add(mv);
                    }
                }
            }
        } catch (Exception e) {
            throw new XMLException();
        }
        return v;
    }


    public static void refresheMetas(List<MetaValue> values){
        for(MetaValue v: values){
            Meta m = FactoryInstantiator.getInstance().getMetaFactory().getMeta(v.getMetaUid());
            v.setMeta(m);
        }
    }


    public static MetaValue toMetaValue(int metaType, DocumentVersion version, Meta meta, String metaValue) {
        MetaValue metaV = null;
        switch (metaType) {
            case MetaType.BOOLEAN:
                metaV = new MetaBooleanValue(
                        version,
                        meta,
                        Boolean.parseBoolean(metaValue));
                break;
            case MetaType.DATE:
                if (Long.parseLong(metaValue) != -1) {
                    metaV = new MetaDateValue(
                            version,
                            meta,
                            new Date(Long.parseLong(metaValue)));
                } else {
                    metaV = new MetaDateValue(version, meta, null);
                }

                break;
            case MetaType.NUMBER:
                metaV = new MetaNumberValue(
                        version,
                        meta,
                        Double.parseDouble(metaValue));
                break;
            case MetaType.STRING:
                metaV = new MetaStringValue(
                        version,
                        meta,
                        metaValue);
                break;
            case MetaType.LIST:
                //TODO fix meta value parsing. Do not use JSON
                ObjectMapper mapper = new ObjectMapper();
                try {
                    List<String> list = mapper.readValue(metaValue, new TypeReference<List<String>>() {
                    });
                    metaV = new MetaListValue(
                            version,
                            meta,
                            list);
                } catch (Exception e) {
                    logger.error("error while parsing multivalued meta value", e);
                }
                break;
        }
        return metaV;
    }
}
