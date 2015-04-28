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

package org.kimios.kernel.index.query.factory;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.hibernate.SessionFactory;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.index.query.model.Criteria;
import org.kimios.kernel.index.query.model.SearchRequest;
import org.kimios.kernel.security.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.util.List;

/**
 *
 *  @author Fabien Alin
 *  @version 1.0
 *
 */
public class SearchRequestFactory extends HFactory {

    private static Logger logger = LoggerFactory.getLogger(SearchRequestFactory.class);


    public Long save(SearchRequest searchRequest) throws DataSourceException {

        /*
            Convert Criteria list to json prior to save
         */

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String criteriasList = objectMapper.writeValueAsString(searchRequest.getCriteriaList());
            searchRequest.setCriteriasListJson(criteriasList);
            getSession().saveOrUpdate(searchRequest);
            getSession().flush();
            return searchRequest.getId();
        } catch (Exception e) {
            throw new DataSourceException(e);
        }
    }


    public List<SearchRequest> loadAllSearchRequests(){

        return getSession().createQuery("from SearchRequest fetch all properties order by name")
                .list();
    }

    public List<SearchRequest> loadSearchRequest(String userId, String userSource) {
        String query = "from SearchRequest fetch all properties where temporary is false " +
                "and (publicAccess is false and published is true) order by name";
        List<SearchRequest> requests =  getSession().createQuery(query)
                .list();
        return requests;
    }

    public List<SearchRequest> listPublicSearchRequest() {
        String query = "from SearchRequest fetch all properties where temporary is false " +
                "and (publicAccess is true) order by name";
        List<SearchRequest> requests =  getSession().createQuery(query)
                .list();
        return requests;
    }

    public List<SearchRequest> loadMySearchRequestNotPublished(Session session) {
        String query = "from SearchRequest fetch all properties where " +
                "publicAccess is false and ((searchSessionId = :searchSessionId and temporary is true) " +
                " or (owner = :userId and ownerSource = :userSource and published is false))  " +
                " order by creationDate asc";
        List<SearchRequest> requests =  getSession().createQuery(query)
                .setString("searchSessionId", session.getUid())
                .setString("userId", session.getUserName())
                .setString("userSource", session.getUserSource())
                .list();

        return requests;
    }

    public SearchRequest loadById(Long id) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();


            String query = "from SearchRequest fetch all properties where id = :id";
            SearchRequest searchRequest = (SearchRequest) getSession()
                    .createQuery(query)
                    .setLong("id", id)
                    .uniqueResult();


            if(searchRequest != null && searchRequest.getCriteriasListJson() != null){
                List<Criteria> criteriaList = objectMapper.readValue(
                        searchRequest.getCriteriasListJson(),
                        new TypeReference<List<Criteria>>(){});
                searchRequest.setCriteriaList(criteriaList);
            }

            return searchRequest;
        }catch ( Exception ex ){
            throw new DataSourceException( ex );
        }
    }

    public void deleteSearchRequest(long id){
        getSession().delete( loadById( id ) );
    }

    public void deleteSearchRequestBySession(String id){
        String query = "from SearchRequest fetch all properties where " +
                "publicAccess is false and (searchSessionId = :searchSessionId and temporary is true) ";

        List<SearchRequest> requests =  getSession().createQuery(query)
                .setString("searchSessionId", id)
                .list();


        for(SearchRequest req: requests){
            getSession().delete( req );
        }

    }


}
