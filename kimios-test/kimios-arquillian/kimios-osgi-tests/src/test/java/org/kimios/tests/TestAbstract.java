package org.kimios.tests;

import org.jboss.arquillian.test.api.ArquillianResource;
import org.kimios.kernel.controller.IAdministrationController;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.user.model.User;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by tom on 11/02/16.
 */
public abstract class TestAbstract {

    @ArquillianResource
    BundleContext context;

    /**
     * Get a diff between two dates
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);

    }

    public <S> ServiceReference<S> waitForserviceReference(Class<S> clazz, long timeout) {
        ServiceReference<S> sRef = this.context.getServiceReference(clazz);

        Date date1 = new Date();
        while (sRef == null && getDateDiff(date1, new Date(), TimeUnit.SECONDS) < timeout) {
            sRef = this.context.getServiceReference(clazz);
        }

        return sRef;
    }

    public <S> S obtainService(Class<S> clazz, long timeout) {
        ServiceReference<S> sRef = waitForserviceReference(clazz, timeout);

        return this.context.getService(sRef);
    }

    public void initServices(List<String> controllerNames) {
        Class<?> c =  this.getClass();
        for (String cName : controllerNames) {
            try {
                Field f = c.getSuperclass().getDeclaredField(cName);
//                f.set(this, this.obtainService(f.getType(), 120));

                String fieldName = f.getName();
                String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                Method setter = c.getSuperclass().getDeclaredMethod(methodName, f.getType());
                setter.invoke(this, this.obtainService(f.getType(), 120));
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public User createUser (
            IAdministrationController administrationController,
            Session session, String uid, String firstname, String lastname, String phoneNumber,
            String mail, String password, String authenticationSourceName, boolean enabled
    ) {
        User user = null;
        try {
            user = administrationController.getUser(session, uid, authenticationSourceName);
            if (user == null) {
                administrationController.createUser(session, uid, firstname, lastname, phoneNumber, mail, password, authenticationSourceName, enabled);
                user = administrationController.getUser(session, uid, authenticationSourceName);
            }
        } catch (NullPointerException e) {
        }
        return user;
    }
}
