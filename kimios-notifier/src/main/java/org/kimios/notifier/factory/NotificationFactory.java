package org.kimios.notifier.factory;

import org.hibernate.HibernateException;
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.notification.model.Notification;
import org.kimios.kernel.notification.model.NotificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NotificationFactory extends HFactory {

    private static Logger logger = LoggerFactory.getLogger(NotificationFactory.class);

    public Notification saveNotification(Notification notification) throws HibernateException {

        getSession().saveOrUpdate(notification);
        getSession().flush();

        return notification;
    }

    public List<Notification> getNotifications(String userId, String userSource, long documentId, List<NotificationStatus> statuses) {
        String query = "select n from Notification n " +
                " where n.userId = :userId " +
                " and n.userSource = :userSource " +
                " and n.documentUid = :documentId " +
                " and n.status in (:statuses) ";
        return getSession()
                .createQuery(query)
                .setString("userId", userId)
                .setString("userSource", userSource)
                .setParameterList("statuses", statuses)
                .setLong("documentId", documentId)
                .list();
    }

    public List<Notification> getNotificationsToSend() {
        String query = "select n from Notification n " +
                " where n.status in (:statuses) ";
        List<NotificationStatus> statuses = new ArrayList<>();
        statuses.add(NotificationStatus.TO_BE_SENT);
        return getSession()
                .createQuery(query)
                .setParameterList("statuses", statuses)
                .list();
    }

    public Notification getNotificationById(long id) {
        String query = "select n from Notification n " +
                " where n.status in (:statuses) ";
        List<NotificationStatus> statuses = new ArrayList<>();
        statuses.add(NotificationStatus.TO_BE_SENT);
        Optional<Notification> optional = getSession()
                .createQuery(query)
                .setLong("id", id)
                .list().stream().findFirst();

        return optional.isPresent() ? optional.get() : null;
    }

    public void changeNotificationStatus(long id, NotificationStatus status) {
        Notification notification = getNotificationById(id);
        notification.setStatus(status);
        this.saveNotification(notification);
    }
}
