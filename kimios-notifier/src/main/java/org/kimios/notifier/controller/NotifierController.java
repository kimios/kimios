package org.kimios.notifier.controller;

import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.IAdministrationController;
import org.kimios.kernel.controller.IDocumentController;
import org.kimios.kernel.index.controller.ISearchController;
import org.kimios.kernel.index.query.model.Criteria;
import org.kimios.kernel.index.query.model.SearchResponse;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.ws.pojo.DMEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotifierController extends AKimiosController implements INotifierController {

    private static Logger logger = LoggerFactory.getLogger(NotifierController.class);

    private static String SEARCH_FIELD = "DEAD_LINE_DATE";
    private static long REMAINING_DAYS_BEFORE_NOTIFICATION = 7;

    private ISearchController searchController;
    private IDocumentController documentController;
    private IAdministrationController administrationController;

    public SearchResponse searchDocuments(Session session) throws Exception {
        return this.searchController.advancedSearchDocuments(session, prepareCriteriaList(), -1, -1, null, null,
                null, null, false);
    }

    public List<Criteria> prepareCriteriaList() {
        Criteria c = new Criteria();
        c.setFieldName(SEARCH_FIELD);
        c.setLevel(0);
        c.setPosition(0);

        LocalDateTime dateTimeCriteria = LocalDateTime.now().minusDays(REMAINING_DAYS_BEFORE_NOTIFICATION);
        c.setRangeMin(dateTimeCriteria.toString());

        ArrayList<Criteria> criteriaList = new ArrayList<Criteria>();
        criteriaList.add(c);

        return criteriaList;
    }

    public Integer createNotifications(Session session) throws Exception {
        SearchResponse searchResponse = searchDocuments(session);
        logger.info("creating notifications now…");
        Integer i = 0;
        for (DMEntity dm: searchResponse.getRows()) {
            // filtering only documents
            if (dm.getType() != 3) {
                continue;
            }
            logger.info("creating notification for document " + dm.toString());
            i++;
        }
        return i;
    }
}
