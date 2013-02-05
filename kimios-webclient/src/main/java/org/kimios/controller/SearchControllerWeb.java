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
package org.kimios.controller;

import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;
import org.kimios.core.wrappers.DMEntity;
import org.kimios.kernel.ws.pojo.Document;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Vector;

/**
 * @author Fabien Alin
 */
public class SearchControllerWeb extends Controller {

    public SearchControllerWeb(Map<String, String> parameters) {
        super(parameters);
    }

    public String execute() throws Exception {
        Document[] res = new Document[0];
        if (action.equalsIgnoreCase("Quick")) {

            long dmEntityUid = -1;
            try {
                dmEntityUid = Long.parseLong(parameters.get("dmEntityUid"));
            } catch (Exception e) {
            }
            int dmEntityType = -1;
            try {
                dmEntityType = Integer.parseInt(parameters.get("dmEntityType"));
            } catch (Exception e) {
                dmEntityType = -1;
            }
            if (dmEntityUid <= 0) {
                dmEntityUid = -1;
                dmEntityType = -1;
            }

            res = searchController
                    .quickSearch(sessionUid, dmEntityType, dmEntityUid, parameters.get("name"));
            log.debug("Quick search in uid: " + dmEntityUid + " [Type: " + dmEntityType + "]: " + res.length + " results");
        } else if (action.equalsIgnoreCase("Advanced")) {
            String docName = parameters.get("name");
            String docUidS = parameters.get("uid");
            String text = parameters.get("text");
            String positionUidS = parameters.get("dmEntityUid");
            String positionTypeS = parameters.get("dmEntityType");
            String documentType = parameters.get("documentType");
            long docUid = -1;
            try {
                docUid = Long.parseLong(docUidS);
            } catch (Exception e) {
            }
            long docTypeUid = -1;
            try {
                docTypeUid = Long.parseLong(documentType);
            } catch (Exception e) {
            }
            long positionUid = -1;
            try {
                positionUid = Long.parseLong(positionUidS);
            } catch (Exception e) {
            }
            int positionType = -1;
            try {
                positionType = Integer.parseInt(positionTypeS);
            } catch (Exception e) {
            }

            LinkedHashSet<String> hashset = new LinkedHashSet<String>();
            hashset.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?><search>\n");
            if (docName != null && !docName.equals(""))
                hashset.add("<document-name><![CDATA[" + docName + "]]></document-name>\n");
            if (text != null && !text.equals(""))
                hashset.add("<text><![CDATA[" + text + "]]></text>\n");
            if (docUid != -1)
                hashset.add("<document-uid>" + docUid + "</document-uid>\n");
            if (docTypeUid != -1)
                hashset.add("<document-type-uid>" + docTypeUid + "</document-type-uid>\n");

            for (String paramKey : parameters.keySet()) {
                if (paramKey.startsWith("meta_value_") && parameters.get(paramKey) != null && !parameters.get(paramKey).equals("")) {
                    String metaUid = paramKey.split("_")[2];
                    int metaType = Integer.parseInt(paramKey.split("_")[3]);

                    switch (metaType) {
                        case 1:
                            //text?
                            hashset.add("<meta-value uid=\"" + metaUid + "\"><![CDATA[" + parameters.get(paramKey) + "]]></meta-value>\n");
                            break;
                        case 2:
                            //number
                            hashset.add("<meta-value uid=\"" + metaUid + "\" number-from=\"" + parameters.get("meta_value_" + metaUid + "_2_nbfrom") + "\" number-to=\"" + parameters.get("meta_value_" + metaUid + "_2_nbto") + "\" />\n");
                            break;
                        case 3:
                            //date
                            long dFrom = -1;
                            long dTo = -1;
                            try {
                                dFrom = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(parameters.get("meta_value_" + metaUid + "_3_dfrom")).getTime();
                            } catch (Exception e) {
                                dFrom = -1;
                            }
                            try {
                                dTo = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(parameters.get("meta_value_" + metaUid + "_3_dto")).getTime();
                            } catch (Exception e) {
                                dTo = -1;
                            }
                            hashset.add("<meta-value uid=\"" + metaUid + "\" date-from=\"" + (dFrom != -1 ? dFrom : "") + "\" date-to=\"" + (dTo != -1 ? dTo : "") + "\" />\n");
                            break;
                        case 4:
                            //boolean
                            hashset.add("<meta-value uid=\"" + metaUid + "\" boolean-value=\"" + parameters.get(paramKey) + "\" />\n");
                            break;
                    }
                }
            }

            hashset.add("</search>");
            StringBuffer xml = new StringBuffer();
            Iterator<String> it = hashset.iterator();
            while (it.hasNext()) {
                xml.append(it.next());
            }

            res = searchController.advancedSearch(sessionUid, xml.toString(), positionUid, positionType);
            log.debug("Advanced search in uid: " + positionUid + " [Type: " + positionType + "]: " + res.length + " results");
        }
        Vector<DMEntity> it = new Vector<DMEntity>();
        for (Document d : res) {
            it.add(new DMEntity(d));
        }
        String jsonResp = new JSONSerializer().exclude("class")
                .transform(new DateTransformer("MM/dd/yyyy hh:mm:ss"), "creationDate")
                .transform(new DateTransformer("MM/dd/yyyy hh:mm:ss"), "checkoutDate")
                .serialize(it);

//            String fullResp = "{\"success\":true,\"msg\":{\"count\":" + it.size() + ",\"results\":" + jsonResp + "}}";
        String fullResp = "{list:" + jsonResp + "}";
        return fullResp;
    }
}

