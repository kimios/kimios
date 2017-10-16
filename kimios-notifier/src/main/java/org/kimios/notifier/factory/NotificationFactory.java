package org.kimios.notifier.factory;

import org.hibernate.HibernateException;
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.notification.model.Notification;
import org.kimios.kernel.notification.model.NotificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
}
