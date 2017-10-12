package org.kimios.notifier.controller;



import org.hibernate.HibernateException;
import org.springframework.transaction.annotation.Transactional;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.IAdministrationController;
import org.kimios.kernel.controller.IDocumentController;
import org.kimios.kernel.index.controller.ISearchController;
import org.kimios.kernel.index.query.model.Criteria;
import org.kimios.kernel.index.query.model.SearchResponse;
import org.kimios.kernel.notification.model.Notification;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.ws.pojo.DMEntity;
import org.kimios.notifier.factory.NotificationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Transactional
public class NotifierController extends AKimiosController implements INotifierController {

    private static Logger logger = LoggerFactory.getLogger(NotifierController.class);

    private static String SEARCH_FIELD = "DEAD_LINE_DATE";
    private static long REMAINING_DAYS_BEFORE_NOTIFICATION = 7;

    private ISearchController searchController;
    private IDocumentController documentController;
    private IAdministrationController administrationController;

    private NotificationFactory notificationFactory;

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
            logger.info("creating notifications for document " + dm.toString());

            // find concerned users
            // ArrayList<User> users =
            Notification notification = new Notification(dm.getOwner(), dm.getUid(), dm.getOwnerSource());
            try {
                notificationFactory.saveNotification(notification);
            } catch (HibernateException he) {
                if (he.getCause().getMessage().contains("ERROR: duplicate key value violates unique constraint")) {
                    logger.info("A notification already exists on this document for this user with same status.");
                } else {
                    logger.error("Error while creating notification\n" + he.getMessage());
                }
                continue;
            }

            i++;
        }
        return i;
    }

    public ISearchController getSearchController() {
        return searchController;
    }

    public void setSearchController(ISearchController searchController) {
        this.searchController = searchController;
    }

    public IDocumentController getDocumentController() {
        return documentController;
    }

    public void setDocumentController(IDocumentController documentController) {
        this.documentController = documentController;
    }

    public IAdministrationController getAdministrationController() {
        return administrationController;
    }

    public void setAdministrationController(IAdministrationController administrationController) {
        this.administrationController = administrationController;
    }

    public NotificationFactory getNotificationFactory() {
        return notificationFactory;
    }

    public void setNotificationFactory(NotificationFactory notificationFactory) {
        this.notificationFactory = notificationFactory;
    }
}
