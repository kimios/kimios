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
package org.kimios.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kimios.core.wrappers.DMEntity;
import org.kimios.core.wrappers.VirtualTreeEntity;
import org.kimios.kernel.index.query.model.Criteria;
import org.kimios.kernel.index.query.model.SearchRequest;
import org.kimios.kernel.index.query.model.SearchRequestSecurity;
import org.kimios.kernel.index.query.model.SearchResponse;
import org.kimios.kernel.ws.pojo.Document;
import org.kimios.utils.configuration.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        sortFieldMapping.put("lastVersionUpdateDate", "DocumentVersionUpdateDate");
        sortFieldMapping.put("uid", "DocumentUid");
        sortFieldMapping.put("extension", "DocumentExtension");
        sortFieldMapping.put("owner", "DocumentOwner");
        sortFieldMapping.put("length", "DocumentVersionLength");
        sortFieldMapping.put("documentTypeName", "DocumentTypeName");
        sortFieldMapping.put("workflowStatusName", "DocumentWorkflowStatusName");
    }



    public String execute()
            throws Exception {
        SearchResponse searchResponse = null;

        // Quick
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
        } else if (action.equalsIgnoreCase("QuickExport")) {

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

            InputStream io =
                    searchController.quickSearchExport( sessionUid, dmEntityType, dmEntityUid, parameters.get("name"), 0,
                            Integer.MAX_VALUE, sort, sortDir);

            //copy to file

            String fileName = "Kimios_Export_"
                    + new SimpleDateFormat("yyyy_MM_dd_HH_mm").format(new Date()) + ".csv";
            IOUtils.copyLarge(io, new FileOutputStream(ConfigurationManager.getValue("client","temp.directory") + "/" + fileName));
            String fullResp =
                    "[{\"fileExport\":\"" + fileName + "\"}]";

            return fullResp;
        }

        // Advanced
        else if (action.equalsIgnoreCase("Advanced")) {

            // ######### keep below for simulate quick search
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
            // ######### end of: keep below for simulate quick search

            List<Criteria> criteriaList = parseCriteriaList(parameters);

            int page = parameters.get("start") != null ? Integer.parseInt(parameters.get("start")) : -1;
            int pageSize = parameters.get("limit") != null ? Integer.parseInt(parameters.get("limit")) : -1;

            String sort = parameters.get("sort") != null ? sortFieldMapping.get(parameters.get("sort")) : null;
            String sortDir = parameters.get("dir") != null ? parameters.get("dir").toLowerCase() : null;
            String virtualPath = parameters.get("virtualPath");

            boolean autoSave = parameters.get("autoSave") != null ? Boolean.parseBoolean(parameters.get("autoSave")) : false;
            searchResponse =
                    searchController.advancedSearchDocument(sessionUid, criteriaList, page, pageSize, sort, sortDir,
                            virtualPath, -1, autoSave);
            log.debug("Advanced search in uid: " + positionUid + " [Type: " + positionType + "]: "
                    + searchResponse.getRows().size() + " results / " + searchResponse.getResults());

            return retSearchResp(searchResponse);

        } // SaveQuery
        else if (action.equalsIgnoreCase("SaveQuery")) {
            String sort = parameters.get("sort") != null ? sortFieldMapping.get(parameters.get("sort")) : null;
            String sortDir = parameters.get("dir") != null ? parameters.get("dir").toLowerCase() : null;


            String securities = parameters.get("securities") != null ? parameters.get("securities") : null;

            Long queryId =
                    parameters.get("searchQueryId") != null ? Long.parseLong(parameters.get("searchQueryId")) : null;
            String queryName = parameters.get("searchQueryName");

            List<Criteria> criteriaList = parseCriteriaList(parameters);

            boolean publicQuery = parameters.get("publicSave") != null ? Boolean.parseBoolean(parameters.get("publicSave")) : false;




            SearchRequest request = null;
            if (queryId != null) {
                //reparse
                request = searchController.getQuery(sessionUid, queryId);
            } else {
                request = new SearchRequest();
                request.setPublished(false);
                request.setPublicAccess(false);
            }



            request.setId(queryId);
            request.setName(queryName);
            request.setCriteriaList(criteriaList);
            request.setSortDir(sortDir);
            request.setSortField(sort);

            request.setPublicAccess(publicQuery);


            List<SearchRequestSecurity> searchRequestSecurities = null;
            if (StringUtils.isNotBlank(securities)) {
                searchRequestSecurities
                        = new ObjectMapper().readValue(securities, new TypeReference<List<SearchRequestSecurity>>() {
                });
                log.debug("securities object: {}", searchRequestSecurities.size());
            }
            request.setSecurities(searchRequestSecurities);
            Long newReqId = searchController.advancedSaveQuery(sessionUid, request);
            request.setId(newReqId);
            request.setCriteriasListJson(new ObjectMapper().writeValueAsString(request.getCriteriaList()));
            return new JSONSerializer().include("securities").exclude("securities.class").exclude("class").serialize(request);
        }

        // ListQueries
        else if (action.equalsIgnoreCase("ListMyQueries")) {
            List<SearchRequest> items = searchController.listMyQueries(sessionUid);
            return new JSONSerializer().exclude("class").serialize(items);
        } else if (action.equalsIgnoreCase("ListPublishedQueries")) {
            List<SearchRequest> items = searchController.listPublishedQueries(sessionUid);
            return new JSONSerializer().exclude("class").serialize(items);
        } else if (action.equalsIgnoreCase("ListPublicQueries")) {
            List<SearchRequest> items = searchController.listPublicQueries(sessionUid);
            return new JSONSerializer().exclude("class").serialize(items);
        } else if (action.equalsIgnoreCase("GetSearchRequestSecurities")) {
            Long id = Long.parseLong(parameters.get("queryId"));
            List<SearchRequestSecurity> searchRequestSecurities = searchController.getSecurities(sessionUid, id);
            return new JSONSerializer().exclude("class").serialize(searchRequestSecurities);
        } else if (action.equalsIgnoreCase("LoadQuery")) {
            Long id = Long.parseLong(parameters.get("queryId"));
            SearchRequest searchRequest = searchController.getQuery(sessionUid, id);
            log.info("" + (searchRequest.getSecurities() != null ? searchRequest.getSecurities().size() : " no securities"));
            if (searchRequest.getSecurities() != null && searchRequest.getSecurities().size() > 0) {
                for (SearchRequestSecurity s : searchRequest.getSecurities())
                    s.setSearchRequest(null);
            }
            return new JSONSerializer().include("securities").exclude("securities.class").exclude("class").serialize(searchRequest);
        }
        // DeleteQuery
        else if (action.equalsIgnoreCase("DeleteQuery")) {
            Long id = Long.parseLong(parameters.get("queryId"));
            searchController.deleteQuery(sessionUid, id);
            return "";
        }

        // ExecuteSaved
        else if (action.equalsIgnoreCase("ExecuteSaved")) {
            Long queryId = Long.parseLong(parameters.get("queryId"));
            int page = parameters.get("start") != null ? Integer.parseInt(parameters.get("start")) : -1;
            int pageSize = parameters.get("limit") != null ? Integer.parseInt(parameters.get("limit")) : -1;

            String sort = parameters.get("sort") != null ? sortFieldMapping.get(parameters.get("sort")) : null;
            String sortDir = parameters.get("dir") != null ? parameters.get("dir").toLowerCase() : null;

            String virtualPath = parameters.get("virtualPath");

            searchResponse =
                    searchController.executeSearchQuery(sessionUid, queryId, page, pageSize, sort, sortDir, virtualPath);
            log.debug("Advanced search in uid: " + searchResponse.getRows().size() + " results / "
                    + searchResponse.getResults());

            return retSearchResp(searchResponse);

        }
        /*else if ( action.equalsIgnoreCase( "SearchPath" ) )
        {
            int page = parameters.get( "start" ) != null ? Integer.parseInt( parameters.get( "start" ) ) : -1;
            int pageSize = parameters.get( "limit" ) != null ? Integer.parseInt( parameters.get( "limit" ) ) : -1;

            String sort = parameters.get( "sort" ) != null ? sortFieldMapping.get( parameters.get( "sort" ) ) : null;
            String sortDir = parameters.get( "dir" ) != null ? parameters.get( "dir" ).toLowerCase() : null;

            String virtualPath = parameters.get( "virtualPath" );
            List<Criteria> criteriaList = parseCriteriaList( parameters );
            searchResponse =
                searchController.advancedSearchDocument( sessionUid, criteriaList, page, pageSize, sort, sortDir,
                                                         virtualPath );
            log.debug( "Advanced search with path " + virtualPath + " " );

            return retSearchResp( searchResponse );

        }*/
        else if(action.equals("AdvancedExport")){

                // ######### keep below for simulate quick search
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
                // ######### end of: keep below for simulate quick search

                List<Criteria> criteriaList = parseCriteriaList(parameters);

                int page = parameters.get("start") != null ? Integer.parseInt(parameters.get("start")) : -1;
                int pageSize = parameters.get("limit") != null ? Integer.parseInt(parameters.get("limit")) : -1;

                String sort = parameters.get("sort") != null ? sortFieldMapping.get(parameters.get("sort")) : null;
                String sortDir = parameters.get("dir") != null ? parameters.get("dir").toLowerCase() : null;
                String virtualPath = parameters.get("virtualPath");

                boolean autoSave = parameters.get("autoSave") != null ? Boolean.parseBoolean(parameters.get("autoSave")) : false;
                InputStream io =
                        searchController.advancedSearchDocumentExport(sessionUid, criteriaList, 0,
                                Integer.MAX_VALUE, sort, sortDir,
                                virtualPath, -1);
                //copy to file

                String fileName = "Kimios_Export_"
                        + new SimpleDateFormat("yyyy_MM_dd_HH_mm").format(new Date()) + ".csv";
                IOUtils.copyLarge(io, new FileOutputStream(ConfigurationManager.getValue("client","temp.directory") + "/" + fileName));
                String fullResp =
                        "[{\"fileExport\":\"" + fileName + "\"}]";

                return fullResp;
        }
        else {
            return "NOACTION";
        }
    }


    private String retSearchResp(SearchResponse searchResponse) {
        Vector<DMEntity> it = new Vector<DMEntity>();
        for (org.kimios.kernel.ws.pojo.DMEntity d : searchResponse.getRows()) {
            it.add(new DMEntity(d));
        }

        List<VirtualTreeEntity> vEntities = new ArrayList<VirtualTreeEntity>();
        if (searchResponse.getFacetsData() != null && searchResponse.getFacetsData().size() > 0) {

            /*
                Create virtual entities
             */

            int vUid = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            for (Object facetInfo : searchResponse.getFacetsData().keySet()) {
                vUid++;
                String virtualPath = searchResponse.getVirtualPath() + "/" + facetInfo;
                ArrayList fInfo = (ArrayList) searchResponse.getFacetsData().get(facetInfo);
                String virtualName = fInfo.get(0).toString();
                Number count = (Number) fInfo.get(1);

                /*
                    Parse date query
                 */
                try {
                    if (facetInfo.toString().contains(" TO ")) {
                        String[] parts = facetInfo.toString().split(" TO ");
                        Date dateItem = sdf.parse(parts[0]);


                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(dateItem);

                        Date now = new Date();

                        String today = null;
                        if (DateUtils.isSameDay(dateItem, now)) {
                            today = "Today";
                        }
                        // Get unit for date facet
                        if (log.isDebugEnabled()) {
                            log.info(dateItem.toString());
                        }
                        Pattern pattern = Pattern.compile("(DAY|MONTH|YEAR|WEEK)");
                        Matcher m = pattern.matcher(parts[1]);
                        DateFormatSymbols dfs = new DateFormatSymbols(Locale.getDefault());
                        while (m.find()) {
                            String res = m.group();
                            if (res.equals("DAY")) {
                                if (today != null) {
                                    virtualName = "Today (" + DateFormatUtils.format(calendar, "dd-MM-yyyy") + ")";
                                } else
                                    virtualName = dfs.getWeekdays()[calendar.get(Calendar.DAY_OF_WEEK)] + " (" + DateFormatUtils.format(calendar, "dd-MM-yyyy") + " - Week " + calendar.get(Calendar.WEEK_OF_YEAR) + ")";
                            } else if (res.equals("MONTH")) {
                                virtualName = dfs.getMonths()[calendar.get(Calendar.MONTH)] + " " + String.valueOf(calendar.get(Calendar.YEAR));
                            } else if (res.equals("YEAR")) {
                                virtualName = String.valueOf(calendar.get(Calendar.YEAR));
                            }
                            if (log.isDebugEnabled()) {
                                log.debug(" > Translated value " + virtualName);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error(" > Exception while translating facets", e);
                }

                VirtualTreeEntity vEntity = new VirtualTreeEntity(vUid, virtualName, virtualPath, count.longValue());
                vEntities.add(vEntity);

            }
        }

        String jsonResp =

                new JSONSerializer().exclude("class")
                        .serialize(it);
        String virtualTree =
                new JSONSerializer().exclude("class")
                        .transform(new DateTransformer("MM/dd/yyyy hh:mm:ss"),
                        "creationDate").transform(
                        new DateTransformer("MM/dd/yyyy hh:mm:ss"), "checkoutDate").serialize(vEntities);
        String fullResp =
                "{\"total\":" + searchResponse.getResults() + ",\"list\":" + jsonResp + ",\"virtualTreeRows\":"
                        + virtualTree + "}";
        return fullResp;
    }


    private List<Criteria> parseCriteriaList(Map<String, String> parameters) {
        boolean dateRangeOk = false;
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
                        log.debug("Criteria (MetaData): " + c);

                    } else {
                        /* In case of meta already parsed: continue to next criteria */
                        continue;
                    }

                } else {
                    if (k.startsWith("DocumentVersionUpdateDate")) {
                        if (dateRangeOk) {
                            continue;
                        }
                        String dateFrom = parameters.get("DocumentVersionUpdateDate_from");
                        String dateTo = parameters.get("DocumentVersionUpdateDate_to");
                        c.setFieldName("DocumentVersionUpdateDate");
                        if (dateFrom != null) {
                            c.setRangeMin(dateFrom);
                        }
                        if (dateTo != null) {
                            c.setRangeMax(dateTo);
                        }
                        dateRangeOk = true;
                    } else {
                        c.setQuery(parameters.get(k));
                    }
                    criteriaList.add(c);
                    log.debug("Criteria:" + c);
                        /* No meta value... do nothing */
                    continue;
                }
            }

        }

        return criteriaList;
    }
}

