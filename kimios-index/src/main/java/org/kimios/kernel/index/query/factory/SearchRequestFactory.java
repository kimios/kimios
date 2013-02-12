package org.kimios.kernel.index.query.factory;

import org.codehaus.jackson.map.ObjectMapper;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.index.query.model.SearchRequest;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: farf
 * Date: 2/7/13
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
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
