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

package org.kimios.kernel.index.query.factory;

import org.codehaus.jackson.map.ObjectMapper;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.index.query.model.SearchRequest;

import java.util.List;

/**
 *
 *  @author Fabien Alin
 *  @version 1.0
 *
 */
public class SearchRequestFactory extends HFactory {


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


    public List<SearchRequest> loadSearchRequest(String userId, String userSource) {
        String query = "from SearchRequest fetch all properties where owner = :userId and ownerSource = :userSource order by name";
        return getSession().createQuery(query)
                .setParameter("userId", userId)
                .setParameter("userSource", userSource)
                .list();
    }

    public SearchRequest loadById(Long id) {
        String query = "from SearchRequest fetch all properties where id = :id";
        return (SearchRequest) getSession()
                .createQuery(query)
                .setLong("id", id)
                .uniqueResult();
    }


}
