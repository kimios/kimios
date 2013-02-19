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
import org.kimios.kernel.index.query.model.Criteria;
import org.kimios.kernel.index.query.model.SearchRequest;
import org.kimios.kernel.index.query.model.SearchResponse;
import org.kimios.kernel.ws.pojo.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Fabien Alin
 */
public class SearchControllerWeb
        extends Controller {

    private static Logger log = LoggerFactory.getLogger(SearchControllerWeb.class);

    public SearchControllerWeb(Map<String, String> parameters) {
        super(parameters);
    }


    private static Map<String, String> sortFieldMapping = new HashMap<String, String>();

    static {
        sortFieldMapping.put("name", "DocumentName");
        sortFieldMapping.put("creationDate", "DocumentCreationDate");
        sortFieldMapping.put("updateDate", "DocumentVersionUpdateDate");
        sortFieldMapping.put("uid", "DocumentUid");
        sortFieldMapping.put("extension", "DocumentExtension");
        sortFieldMapping.put("owner", "DocumentOwner");
        sortFieldMapping.put("length", "DocumentVersionLength");
        sortFieldMapping.put("documentTypeName", "DocumentTypeName");
    }


    public String execute()
            throws Exception {
        SearchResponse searchResponse = null;
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

            int page = parameters.get("start") != null ? Integer.parseInt(parameters.get("start")) : -1;
            int pageSize = parameters.get("limit") != null ? Integer.parseInt(parameters.get("limit")) : -1;

            String sort = parameters.get("sort") != null ? sortFieldMapping.get(parameters.get("sort")) : null;
            String sortDir = parameters.get("dir") != null ? parameters.get("dir").toLowerCase() : null;

            searchResponse =
                    searchController.quickSearch(sessionUid, dmEntityType, dmEntityUid, parameters.get("name"), page,
                            pageSize, sort, sortDir);
            log.debug("Quick search in uid: " + dmEntityUid + " [Type: " + dmEntityType + "]: "
                    + searchResponse.getRows().size() + " results / " + searchResponse.getResults());

            return retSearchResp(searchResponse);
        } else if (action.equalsIgnoreCase("Advanced")) {
            String positionUidS = parameters.get("dmEntityUid");
            String positionTypeS = parameters.get("dmEntityType");
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

            List<Criteria> criteriaList = parseCriteriaList(parameters);

            int page = parameters.get("start") != null ? Integer.parseInt(parameters.get("start")) : -1;
            int pageSize = parameters.get("limit") != null ? Integer.parseInt(parameters.get("limit")) : -1;

            String sort = parameters.get("sort") != null ? sortFieldMapping.get(parameters.get("sort")) : null;
            String sortDir = parameters.get("dir") != null ? parameters.get("dir").toLowerCase() : null;

            searchResponse =
                    searchController.advancedSearchDocument(sessionUid, criteriaList, page,
                            pageSize, sort, sortDir);
            log.debug("Advanced search in uid: " + positionUid + " [Type: " + positionType + "]: "
                    + searchResponse.getRows().size() + " results / " + searchResponse.getResults());

            return retSearchResp(searchResponse);

        } else if (action.equalsIgnoreCase("SaveQuery")) {
            String sort = parameters.get("sort") != null ? sortFieldMapping.get(parameters.get("sort")) : null;
            String sortDir = parameters.get("dir") != null ? parameters.get("dir").toLowerCase() : null;

            Long queryId = parameters.get("searchQueryId") != null ? Long.parseLong(parameters.get("searchQueryId")) : null;
            String queryName = parameters.get("searchQueryName");

            List<Criteria> criteriaList = parseCriteriaList(parameters);
            searchController.saveQuery(sessionUid, queryId, queryName, criteriaList, sort, sortDir);
            return "";
        } else if (action.equalsIgnoreCase("ListQueries")) {
            List<SearchRequest> items = searchController.listQueries(sessionUid);
            return new JSONSerializer().exclude("class").serialize(items);
        } else if (action.equalsIgnoreCase("DeleteQuery")) {

            Long id = Long.parseLong(parameters.get("queryId"));
            searchController.deleteQuery(sessionUid, id);
            return "";
        } else if (action.equalsIgnoreCase("ExecuteSaved")) {


            Long queryId = Long.parseLong(parameters.get("queryId"));
            int page = parameters.get("start") != null ? Integer.parseInt(parameters.get("start")) : -1;
            int pageSize = parameters.get("limit") != null ? Integer.parseInt(parameters.get("limit")) : -1;

            String sort = parameters.get("sort") != null ? sortFieldMapping.get(parameters.get("sort")) : null;
            String sortDir = parameters.get("dir") != null ? parameters.get("dir").toLowerCase() : null;

            searchResponse =
                    searchController.executeSearchQuery(sessionUid, queryId, page,
                            pageSize, sort, sortDir);
            log.debug("Advanced search in uid: "
                    + searchResponse.getRows().size() + " results / " + searchResponse.getResults());

            return retSearchResp(searchResponse);

        } else {
            return "NOACTION";
        }
    }


    private String retSearchResp(SearchResponse searchResponse) {
        Vector<DMEntity> it = new Vector<DMEntity>();
        for (Document d : searchResponse.getRows()) {
            it.add(new DMEntity(d));
        }
        String jsonResp =
                new JSONSerializer().exclude("class").transform(new DateTransformer("MM/dd/yyyy hh:mm:ss"),
                        "creationDate").transform(
                        new DateTransformer("MM/dd/yyyy hh:mm:ss"), "checkoutDate").serialize(it);
        String fullResp = "{\"total\":" + searchResponse.getResults() + ",\"list\":" + jsonResp + "}";
        return fullResp;
    }


    private List<Criteria> parseCriteriaList(Map<String, String> parameters) {
        List<Long> alreayParsedMeta = new ArrayList<Long>();
        List<Criteria> criteriaList = new ArrayList<Criteria>();
        for (String k : parameters.keySet()) {
            if (parameters.get(k) != null && parameters.get(k).trim().length() > 0 && (k.startsWith("MetaData")
                    || k.startsWith("Document"))) {
                Criteria c = new Criteria();
                c.setFieldName(k);
                c.setLevel(0);
                c.setPosition(0);
                if (k.startsWith("MetaData")) {

                    String metaUid = k.split("_")[1];
                    if (!alreayParsedMeta.contains(Long.parseLong(metaUid))) {
                        c.setMetaId(Long.parseLong(metaUid));
                        if (k.contains("String")) {
                            c.setMetaType(new Long(1));
                        }
                        if (k.contains("Number")) {
                            c.setMetaType(new Long(2));
                            c.setFieldName("MetaDataNumber_" + metaUid);
                        }
                        if (k.contains("Date")) {
                            c.setMetaType(new Long(3));
                            c.setFieldName("MetaDataDate_" + metaUid);
                        }
                        if (k.contains("Boolean")) {
                            c.setMetaType(new Long(4));
                        }

                        if (k.contains("Date") || k.contains("Number")) {

                            String fromKey = k.split("_")[0] + "_" + metaUid + "_from";
                            String toKey = k.split("_")[0] + "_" + metaUid + "_to";
                            if (parameters.get(fromKey) != null) {
                                c.setRangeMin(parameters.get(fromKey));
                            }
                            if (parameters.get(toKey) != null) {
                                c.setRangeMax(parameters.get(toKey));
                            }
                        } else {
                            c.setQuery(parameters.get(k));
                        }
                        criteriaList.add(c);
                        alreayParsedMeta.add(Long.parseLong(metaUid));
                        System.out.println(" >>> " + c);

                    } else {
                        /* In case of meta already parsed: continue to next criteria */
                        continue;
                    }
                } else {
                    c.setQuery(parameters.get(k));
                    criteriaList.add(c);
                    log.debug("Search Criteria:" + c);
                        /* No meta value... do nothing */
                    continue;
                }
            }

        }

        return criteriaList;
    }
}

