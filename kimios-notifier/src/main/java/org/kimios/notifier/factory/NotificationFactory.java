package org.kimios.notifier.factory;

import org.hibernate.HibernateException;
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.notification.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationFactory extends HFactory {

    private static Logger logger = LoggerFactory.getLogger(NotificationFactory.class);

    public Notification saveNotification(Notification notification) throws HibernateException {
        getSession().saveOrUpdate(notification);
        try {
            getSession().flush();
        } catch (HibernateException he) {
            throw he;
        }
        return notification;
    }
}
