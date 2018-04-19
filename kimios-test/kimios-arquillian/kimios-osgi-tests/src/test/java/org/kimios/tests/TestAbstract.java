package org.kimios.tests;

import org.jboss.arquillian.test.api.ArquillianResource;
import org.kimios.kernel.controller.IAdministrationController;
import org.kimios.kernel.controller.ISecurityController;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.user.model.User;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by tom on 11/02/16.
 */
public abstract class TestAbstract {

    public static String ADMIN_LOGIN = "admin";
    public static String ADMIN_PWD = "kimios";
    public static String ADMIN_SOURCE = "kimios";

    private Session adminSession;

    @OsgiKimiosService
    private ISecurityController securityController;

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

    public void initServices() {
        Class<?> c =  this.getClass();
        for (Field f : this.retrieveAllDeclaredFields()
                .stream()
                .filter(f -> f.isAnnotationPresent(OsgiKimiosService.class))
                .collect(Collectors.toList())) {
            try {
                String fieldName = f.getName();
                String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                Method setter = c.getMethod(methodName, f.getType());
                setter.invoke(this, this.obtainService(f.getType(), 120));
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

    public List<Field> retrieveAllDeclaredFields() {
        ArrayList<Field> list = new ArrayList<>();
        Class<?> c =  this.getClass();
        while (c != null) {
            Collections.addAll(list, c.getDeclaredFields());
            c = c.getSuperclass();
        }
        return list;
    }

    public Session getAdminSession() {
        return adminSession;
    }

    public void setAdminSession(Session adminSession) {
        this.adminSession = adminSession;
    }

    public void setSecurityController(ISecurityController securityController) {
        this.securityController = securityController;
    }

    public ISecurityController getSecurityController() {
        return securityController;
    }
}
