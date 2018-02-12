package org.kimios.tests.utils.dataset;

import org.kimios.kernel.controller.IAdministrationController;
import org.kimios.kernel.security.model.Session;

import java.util.Arrays;
import java.util.Date;

public class Users {
    public static String USER_TEST_1 = "userTest1";
    public static String USER_TEST_2 = "userTest2";
    public static String USER_TEST_3 = "userTest3";
    public static String USER_TEST_SOURCE = "kimios";

    public static void createUserFromPojoWithPassword(
            IAdministrationController administrationController,
            Session adminSession,
            org.kimios.kernel.ws.pojo.User user,
            String password
    ) {
        administrationController.createUser(adminSession, user.getUid(), user.getFirstName(), user.getLastName(), user.getPhoneNumber(), user.getMail(), password, user.getSource(), true);
    }

    public static void createTestUsers(
            IAdministrationController administrationController,
            Session adminSession
    ) {

        org.kimios.kernel.ws.pojo.User user = new org.kimios.kernel.ws.pojo.User(
                USER_TEST_1,
                "Test",
                "User 1",
                "06060606060",
                USER_TEST_SOURCE,
                new Date(),
                "mail"
        );
        Users.createUserFromPojoWithPassword(administrationController, adminSession, user, "test");

        user = new org.kimios.kernel.ws.pojo.User(
                USER_TEST_2,
                "Test",
                "User 2",
                "06060606060",
                USER_TEST_SOURCE,
                new Date(),
                "usertest2@mail.com"
        );
        Users.createUserFromPojoWithPassword(administrationController, adminSession, user, "test");

        user = new org.kimios.kernel.ws.pojo.User(
                USER_TEST_3,
                "Test",
                "User 3",
                "06060606060",
                USER_TEST_SOURCE,
                new Date(),
                "usertest3@coolmail.com"
        );
        Users.createUserFromPojoWithPassword(administrationController, adminSession, user, "test");
    }

    public static void deleteTestUsers(IAdministrationController administrationController, Session adminSession) {
        String[] userNames = {
                Users.USER_TEST_1,
                Users.USER_TEST_2,
                Users.USER_TEST_3
        };
        Arrays.asList(userNames).forEach(
                u -> administrationController.deleteUser(adminSession, u, Users.USER_TEST_SOURCE)
        );
    }
}
