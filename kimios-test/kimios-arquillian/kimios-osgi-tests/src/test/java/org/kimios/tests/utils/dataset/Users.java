package org.kimios.tests.utils.dataset;

import org.kimios.client.controller.helpers.StringTools;
import org.kimios.kernel.controller.IAdministrationController;
import org.kimios.kernel.controller.ISecurityController;
import org.kimios.kernel.dms.model.DMEntity;
import org.kimios.kernel.security.model.DMEntitySecurity;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.user.model.User;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
        try {
            administrationController.createUser(adminSession, user.getUid(), user.getFirstName(), user.getLastName(), user.getPhoneNumber(), user.getMail(), password, user.getSource(), true);
        } catch (Exception e) {
        }
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

    public static void giveAccessToEntityForUser(Session session, ISecurityController securityController, DMEntity ent, User user, boolean read, boolean write, boolean full) {
        List<DMEntitySecurity> entities = securityController.getDMEntitySecurityies(session, ent.getUid());

        String xmlStreamExistingEntitites = "";
        for (DMEntitySecurity entity : entities) {
            if (!user.getUid().equals(entity.getName()) || !user.getAuthenticationSourceName().equals(entity.getSource())) {
                xmlStreamExistingEntitites += "\t<rule " +
                        "security-entity-type=\"" + entity.getType() + "\" " +
                        "security-entity-uid=\"" + StringTools.magicDoubleQuotes(entity.getName()) + "\" " +
                        "security-entity-source=\"" + StringTools.magicDoubleQuotes(entity.getSource()) + "\" " +
                        "read=\"" + Boolean.toString(entity.isRead()) + "\" " +
                        "write=\"" + Boolean.toString(entity.isWrite()) + "\" " +
                        "full=\"" + Boolean.toString(entity.isFullAccess()) + "\" />\r\n";
            }
        }

        String xmlStream = "<security-rules dmEntityId=\"" + ent.getUid() + "\"" +
                " dmEntityTye=\"" + ent.getType() + "\">\r\n";

        xmlStream += xmlStreamExistingEntitites;

        xmlStream += "\t<rule " +
                "security-entity-type=\"" + user.getType() + "\" " +
                "security-entity-uid=\"" + StringTools.magicDoubleQuotes( user.getUid() ) + "\" " +
                "security-entity-source=\"" + StringTools.magicDoubleQuotes( user.getAuthenticationSourceName() ) + "\" " +
                "read=\"" + Boolean.toString(read) + "\" " +
                "write=\"" + Boolean.toString(write) + "\" " +
                "full=\"" + Boolean.toString(full) + "\" />\r\n";
        xmlStream += "</security-rules>";

        securityController.updateDMEntitySecurities(session, ent.getUid(), xmlStream, false, true);
    }
}
