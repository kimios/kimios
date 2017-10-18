package org.kimios.notifier.controller;


import org.apache.commons.mail.MultiPartEmail;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.IAdministrationController;
import org.kimios.kernel.controller.IDocumentController;
import org.kimios.kernel.controller.ISecurityController;
import org.kimios.kernel.index.controller.ISearchController;
import org.kimios.kernel.index.query.model.Criteria;
import org.kimios.kernel.index.query.model.SearchResponse;
import org.kimios.kernel.notification.model.Notification;
import org.kimios.kernel.notification.model.NotificationStatus;
import org.kimios.kernel.security.model.DMEntitySecurity;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.share.mail.IEmailFactory;
import org.kimios.kernel.user.model.User;
import org.kimios.kernel.ws.pojo.DMEntity;
import org.kimios.notifier.factory.NotificationFactory;
import org.kimios.notifier.jobs.NotificationMailRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Transactional
public class NotifierController extends AKimiosController implements INotifierController {

    private static Logger logger = LoggerFactory.getLogger(NotifierController.class);

    private static String SEARCH_FIELD = "DEAD_LINE_DATE";
    private static long REMAINING_DAYS_BEFORE_NOTIFICATION = 7;

    private String mailerSender = "Kimios'";
    private String mailerSenderMail = "kimios@kimios.org";

    private ISearchController searchController;
    private IDocumentController documentController;
    private IAdministrationController administrationController;
    private ISecurityController securityController;

    private NotificationFactory notificationFactory;
    private IEmailFactory emailFactory;

    private ScheduledExecutorService scheduledExecutorService;

    public NotifierController(ISearchController searchController,
                              IDocumentController documentController,
                              IAdministrationController administrationController,
                              NotificationFactory notificationFactory,
                              ISecurityController securityController,
                              IEmailFactory emailFactory) {
        this.searchController = searchController;
        this.documentController = documentController;
        this.administrationController = administrationController;
        this.securityController = securityController;
        this.notificationFactory = notificationFactory;
        this.scheduledExecutorService = Executors.newScheduledThreadPool(8);
        this.emailFactory = emailFactory;
    }

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
            Set<UserKey> userKeys = new HashSet<UserKey>();
            // document's owner
            userKeys.add(new UserKey(dm.getOwner(), dm.getOwnerSource()));

            // users who can see the document
            try {
                List<DMEntitySecurity> securities = this.securityController.getDMEntitySecurityies(session, dm.getUid());
                for (DMEntitySecurity security: securities) {
                    if (security.isRead()
                            || security.isWrite()
                            || security.isFullAccess()) {
                        userKeys.add(new UserKey(security.getName(), security.getSource()));
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }

            for (UserKey userKey: userKeys) {
                Notification notification = new Notification(userKey.getUserId(), userKey.getUserSource(), dm.getUid());
                try {
                    List<NotificationStatus> statuses = new ArrayList<>();
                    statuses.add(notification.getStatus());
                    if (notificationFactory.getNotifications(
                            notification.getUserId(),
                            notification.getUserSource(),
                            notification.getDocumentUid(),
                            statuses).size() == 0) {
                        notificationFactory.saveNotification(notification);
                    }
                } catch (Exception e) {
                    logger.error("Error while creating notification\n" + e.getMessage());
                    continue;
                }

                i++;
            }
        }
        return i;
    }

    @Override
    public void sendNotifications(Session session) throws Exception {
        List<Notification> notifications = this.notificationFactory.getNotificationsToSend();

        for (Notification notification: notifications) {
            MultiPartEmail email = emailFactory.getMultipartEmailObject();
            email.setSubject("Kimios Notification");
            email.setMsg("Notification about document " + notification.getDocumentUid());
            User user = this.administrationController.getUser(session, notification.getUserId(), notification.getUserSource());
            // get user mail
            String emailAddress = user.getMail();
            email.addTo(emailAddress, user.getFirstName() + " " + user.getLastName());
            email.setFrom(mailerSenderMail, mailerSender);

            scheduledExecutorService.schedule(new NotificationMailRunnable(email, notificationFactory,
                    notification.getId()), 1000, TimeUnit.MILLISECONDS);
        }
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

    public ISecurityController getSecurityController() {
        return securityController;
    }

    public void setSecurityController(ISecurityController securityController) {
        this.securityController = securityController;
    }

    public IEmailFactory getEmailFactory() {
        return emailFactory;
    }

    public void setEmailFactory(IEmailFactory emailFactory) {
        this.emailFactory = emailFactory;
    }

    private class UserKey implements Comparable {

        private String userId;
        private String userSource;

        public UserKey(String userId, String userSource) {
            this.userId = userId;
            this.userSource = userSource;
        }

        public int compareTo(UserKey uk) {
            return userId.compareTo(uk.getUserId()) == 0 ? userSource.compareTo(uk.getUserSource()) :
                    userId.compareTo(uk.getUserId());
        }

        public String getUserId() {
            return userId;
        }

        public String getUserSource() {
            return userSource;
        }

        @Override
        public int compareTo(Object o) {
            return (o instanceof UserKey) ? this.compareTo((UserKey)o) : 0;
        }
    }
}
