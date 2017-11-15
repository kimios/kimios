package org.kimios.notifier.controller;


import org.apache.commons.io.FileUtils;
import org.kimios.kernel.controller.*;
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.dms.model.DocumentType;
import org.kimios.kernel.dms.model.Meta;
import org.kimios.kernel.dms.model.MetaType;
import org.kimios.kernel.index.controller.ISearchController;
import org.kimios.kernel.index.query.model.Criteria;
import org.kimios.kernel.index.query.model.SearchResponse;
import org.kimios.kernel.notification.model.Notification;
import org.kimios.kernel.notification.model.NotificationStatus;
import org.kimios.kernel.security.model.DMEntitySecurity;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.share.controller.IMailShareController;
import org.kimios.kernel.share.mail.MailDescriptor;
import org.kimios.kernel.user.model.User;
import org.kimios.kernel.ws.pojo.DMEntity;
import org.kimios.notifier.factory.NotificationFactory;
import org.kimios.utils.configuration.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

@Transactional
public class NotifierController extends AKimiosController implements INotifierController {

    private static Logger logger = LoggerFactory.getLogger(NotifierController.class);

    private String mailerSender = "Kimios";
    private String mailerSenderMail = "kimios@kimios.org";

    private ISearchController searchController;
    private IDocumentController documentController;
    private IAdministrationController administrationController;
    private ISecurityController securityController;
    private IMailShareController mailShareController;
    private IDocumentVersionController documentVersionController;
    private IStudioController studioController;
    private String documentTypeName = "SampleDoc";
    private String metaDateName = "DeadLine";
    private String templateUrl = "classpath:/default-template.html";
    private int remainingDaysBeforeNotification = 7;

    private NotificationFactory notificationFactory;

    public NotifierController(ISearchController searchController,
                              IDocumentController documentController,
                              IAdministrationController administrationController,
                              NotificationFactory notificationFactory,
                              ISecurityController securityController,
                              IMailShareController mailShareController,
                              IDocumentVersionController documentVersionController,
                              IStudioController studioController) {
        this.searchController = searchController;
        this.documentController = documentController;
        this.documentVersionController = documentVersionController;
        this.administrationController = administrationController;
        this.securityController = securityController;
        this.notificationFactory = notificationFactory;
        this.mailShareController = mailShareController;
        this.studioController = studioController;

        documentTypeName = ConfigurationManager.getValue("dms.notifier.type.name") != null ?
                ConfigurationManager.getValue("dms.notifier.type.name") : documentTypeName;
        metaDateName = ConfigurationManager.getValue("dms.notifier.type.metaname") != null ?
                ConfigurationManager.getValue("dms.notifier.type.metaname") : metaDateName;

        templateUrl = ConfigurationManager.getValue("dms.notifier.reminder.templateurl") != null ?
                ConfigurationManager.getValue("dms.notifier.reminder.templateurl") : templateUrl;

        mailerSender = ConfigurationManager.getValue("dms.mail.sendername") != null
                && !ConfigurationManager.getValue("dms.mail.sendername").isEmpty() ?
                ConfigurationManager.getValue("dms.mail.sendername") : mailerSender;

        mailerSenderMail = ConfigurationManager.getValue("dms.mail.sendermail") != null
                && !ConfigurationManager.getValue("dms.mail.sendermail").isEmpty() ?
                ConfigurationManager.getValue("dms.mail.sendermail") : mailerSenderMail;

        try {
            remainingDaysBeforeNotification = ConfigurationManager.getValue("dms.notifier.reminderdelay") != null ?
                    Integer.parseInt(ConfigurationManager.getValue("dms.notifier.reminderdelay")) : remainingDaysBeforeNotification;
        } catch (Exception ex){
            logger.error("invalid notification delay. defaulted to 7 days");
        }
    }

    public SearchResponse searchDocuments(Session session) throws Exception {

        List<DocumentType> documentTypes = this.studioController.getDocumentTypes();
        DocumentType eligibleType = null;
        for (DocumentType type : documentTypes) {
            if (type.getName().equals(documentTypeName)) {
                eligibleType = type;
                break;
            }
        }
        if (eligibleType != null) {
            List<Meta> metas = this.documentVersionController.getMetas(session, eligibleType.getUid());

            Meta eligibleMeta = null;
            for (Meta meta : metas) {
                if (meta.getName().equals(metaDateName) && meta.getMetaType() == MetaType.DATE) {
                    eligibleMeta = meta;
                    break;
                }
            }
            if (eligibleMeta != null) {
                return this.searchController.advancedSearchDocuments(session, prepareCriteriaList(eligibleMeta), -1, -1, null, null,
                        null, null, false);
            } else {
                logger.warn("meta {} for notification not found", metaDateName);
            }

        } else {
            logger.warn("document type {} for notification not found", documentTypeName);
        }
        return null;
    }

    public List<Criteria> prepareCriteriaList(Meta meta) {
        //get specific metat date
        String searchField = "MetaDataDate_" + meta.getUid();
        Criteria c = new Criteria();
        c.setFieldName(searchField);
        c.setMetaId(meta.getUid());
        c.setLevel(0);
        c.setPosition(0);
        LocalDateTime dateTimeCriteria = LocalDateTime.now().minusDays(remainingDaysBeforeNotification);
        c.setRangeMin(dateTimeCriteria.toString());
        logger.debug("looking for document with meta {} on date {}", meta.getName(), dateTimeCriteria);
        ArrayList<Criteria> criteriaList = new ArrayList<Criteria>();
        criteriaList.add(c);
        return criteriaList;
    }

    public Integer createNotifications(Session session) throws Exception {
        SearchResponse searchResponse = searchDocuments(session);
        logger.info("creating notifications now…");
        Integer i = 0;
        if (searchResponse != null) {
            for (DMEntity dm : searchResponse.getRows()) {
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
                    for (DMEntitySecurity security : securities) {
                        if (security.isRead()
                                || security.isWrite()
                                || security.isFullAccess()) {
                            userKeys.add(new UserKey(security.getName(), security.getSource()));
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }

                for (UserKey userKey : userKeys) {
                    Notification notification = new Notification(userKey.getUserId(), userKey.getUserSource(), dm.getUid());
                    try {
                        List<NotificationStatus> statuses = new ArrayList<>();
                        statuses.add(NotificationStatus.SENT);
                        statuses.add(NotificationStatus.TO_BE_SENT);
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
        }
        return i;
    }

    @Override
    public void sendNotifications(Session session) throws Exception {
        List<Notification> notifications = this.notificationFactory.getNotificationsToSend();

        for (Notification notification : notifications) {
            /*MultiPartEmail email = emailFactory.getMultipartEmailObject();
            email.setSubject("Kimios Notification");
            email.setMsg("Notification about document " + notification.getDocumentUid());
            User user = this.administrationController.getUser(session, notification.getUserId(), notification.getUserSource());
            // get user mail
            String emailAddress = user.getMail();
            email.addTo(emailAddress, user.getFirstName() + " " + user.getLastName());
            email.setFrom(mailerSenderMail, mailerSender);

            scheduledExecutorService.schedule(new NotificationMailRunnable(email, notificationFactory,
                    notification.getId()), 1000, TimeUnit.MILLISECONDS);*/

            MailDescriptor mailDescriptor = new MailDescriptor();
            mailDescriptor.setSubject("Kimios Notification");
            mailDescriptor.setFrom(mailerSenderMail);
            mailDescriptor.setFromName(mailerSender);

            String mailContent = "Notification about document " + notification.getDocumentUid();
            if(templateUrl != null){
                try {
                    File f = new File(URI.create(templateUrl));
                    mailContent = FileUtils.readFileToString(f);
                } catch (Exception ex){
                    logger.warn("exception while loading default template", ex);
                }
            }

            mailDescriptor.setMailContent(mailContent);
            User user = this.administrationController.getUser(session, notification.getUserId(), notification.getUserSource());
            Document document = this.documentController.getDocument(session, notification.getDocumentUid());
            // get user mail
            String emailAddress = user.getMail();
            mailDescriptor.setRecipients(Arrays.asList(emailAddress));
            mailDescriptor.getDatas().put("user", user);
            mailDescriptor.getDatas().put("document", document);
            mailShareController.scheduleMailSend(mailDescriptor);
            notification.setStatus(NotificationStatus.SENT);
        }

        for(Notification s: notifications)
            notificationFactory.saveNotification(s);
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
            return (o instanceof UserKey) ? this.compareTo((UserKey) o) : 0;
        }
    }


    public String getMailerSender() {
        return mailerSender;
    }

    public void setMailerSender(String mailerSender) {
        this.mailerSender = mailerSender;
    }

    public String getMailerSenderMail() {
        return mailerSenderMail;
    }

    public void setMailerSenderMail(String mailerSenderMail) {
        this.mailerSenderMail = mailerSenderMail;
    }


}
