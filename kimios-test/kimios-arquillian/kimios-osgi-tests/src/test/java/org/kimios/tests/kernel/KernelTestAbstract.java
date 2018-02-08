package org.kimios.tests.kernel;

import org.jboss.arquillian.test.api.ArquillianResource;
import org.kimios.tests.OsgiKimiosService;
import org.kimios.tests.TestAbstract;
import org.kimios.client.controller.helpers.StringTools;
import org.kimios.kernel.controller.*;
import org.kimios.kernel.dms.model.DMEntity;
import org.kimios.kernel.dms.model.Workspace;
import org.kimios.kernel.security.model.DMEntitySecurity;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.user.model.User;
import org.osgi.framework.BundleContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tom on 11/02/16.
 */
public abstract class KernelTestAbstract extends TestAbstract {

    @ArquillianResource
    BundleContext context;

    @OsgiKimiosService
    protected IAdministrationController administrationController;
    @OsgiKimiosService
    protected ISecurityController securityController;
    @OsgiKimiosService
    protected IWorkspaceController workspaceController;
    @OsgiKimiosService
    protected IFolderController folderController;
    @OsgiKimiosService
    protected IDocumentController documentController;
    @OsgiKimiosService
    protected IWorkflowController workflowController;
    @OsgiKimiosService
    protected IStudioController studioController;
    @OsgiKimiosService
    protected IRuleManagementController rulesController;

    protected Session adminSession;
    protected Workspace workspaceTest;

    public static String ADMIN_LOGIN = "admin";
    public static String ADMIN_PWD = "kimios";
    public static String USER_TEST_SOURCE = "kimios";
    public static String USER_TEST_SOURCE_2 = "kimios2";
    public static String DEFAULT_USER_TEST_ID = "userTest";
    public static String DEFAULT_USER_TEST_PASS = "test";
    public static String WORKSPACE_TEST_NAME = "workspaceTest";

    public static String USER_TEST_1 = "userTest1";
    public static String USER_TEST_2 = "userTest2";
    public static String USER_TEST_3 = "userTest3";
    public static String USER_TEST_4 = "userTest4";

    public void setAdministrationController(IAdministrationController administrationController) {
        this.administrationController = administrationController;
    }

    public void setSecurityController(ISecurityController securityController) {
        this.securityController = securityController;
    }

    public void setWorkspaceController(IWorkspaceController workspaceController) {
        this.workspaceController = workspaceController;
    }

    public void setFolderController(IFolderController folderController) {
        this.folderController = folderController;
    }

    public void setDocumentController(IDocumentController documentController) {
        this.documentController = documentController;
    }

    public IRuleManagementController getRulesController() {
        return rulesController;
    }

    public void setRulesController(IRuleManagementController rulesController) {
        this.rulesController = rulesController;
    }

    public IWorkflowController getWorkflowController() {
        return workflowController;
    }

    public void setWorkflowController(IWorkflowController workflowController) {
        this.workflowController = workflowController;
    }

    public IStudioController getStudioController() {
        return studioController;
    }

    public void setStudioController(IStudioController studioController) {
        this.studioController = studioController;
    }

    public void init() {
        this.initServices();
    }

    public void removeUserPermissionsForEntity(Session session, User user, DMEntity entity) {
        this.changePermissionOnEntityForUser(session, user, entity, false, false, false);
    }

    public void changePermissionOnEntityForUser (Session session, User user, DMEntity entity, boolean read, boolean write, boolean full) {
        List<DMEntitySecurity> entities = this.securityController.getDMEntitySecurityies(session, entity.getUid());

        String xmlStreamExistingEntitites = "";

        // insert new entitySecurity only if a permission is set at true
        // or only if it already exists
        boolean appendEntitySecurity = read || write|| full;
        boolean secAppended = false;
        for (DMEntitySecurity entitySecurity : entities) {
            // if the entity has a rule for this user
            if (user.getUid().equals(entitySecurity.getName()) && user.getAuthenticationSourceName().equals(entitySecurity.getSource())) {
                // change rules
                entitySecurity.setRead(read);
                entitySecurity.setWrite(write);
                entitySecurity.setFullAccess(full);

                secAppended = true;
            }

            // add to xml stream
            xmlStreamExistingEntitites += dmEntitySecurityToXmlStream(entitySecurity);
        }

        // if it hasn't been appended and it has to be appended, we append it.
        if (!secAppended && appendEntitySecurity) {
            DMEntitySecurity newEntSec = new DMEntitySecurity(entity.getUid(), entity.getType(), user.getUid(), user.getAuthenticationSourceName(), user.getType(), read, write, full);
            xmlStreamExistingEntitites += dmEntitySecurityToXmlStream(newEntSec);
        }

        String xmlStream = "<security-rules dmEntityId=\"" + entity.getUid() + "\"" +
                " dmEntityTye=\"" + entity.getType() + "\">\r\n";
        xmlStream += xmlStreamExistingEntitites;
        xmlStream += "</security-rules>";

        this.securityController.updateDMEntitySecurities(session, entity.getUid(), xmlStream, false, true);
    }

    public static String dmEntitySecurityToXmlStream (DMEntitySecurity entSec) {
       String xmlStream =  "\t<rule " +
                "security-entity-type=\"" + entSec.getType() + "\" " +
                "security-entity-uid=\"" + StringTools.magicDoubleQuotes(entSec.getName()) + "\" " +
                "security-entity-source=\"" + StringTools.magicDoubleQuotes(entSec.getSource()) + "\" " +
                "read=\"" + Boolean.toString(entSec.isRead()) + "\" " +
                "write=\"" + Boolean.toString(entSec.isWrite()) + "\" " +
                "full=\"" + Boolean.toString(entSec.isFullAccess()) + "\" />\r\n";

        return xmlStream;
    }

    public void createWorkspaceTestIfNotExists() {
        Workspace workspaceTest = null;
        try {
            workspaceTest = this.workspaceController.getWorkspace(this.adminSession, WORKSPACE_TEST_NAME);
        } catch (Exception e) {
        }
        if (workspaceTest == null) {
            // creation
            this.workspaceController.createWorkspace(this.adminSession, WORKSPACE_TEST_NAME);
        }
    }

    public void createUserTestIfNotExists(String userId) {
        User userTest = null;
        try {
            userTest = this.administrationController.getUser(this.adminSession, userId, USER_TEST_SOURCE);
        } catch (Exception e) {

        }
        if (userTest == null) {
            this.createDefaultUserTest(userId);
        }
    }

    public void createDefaultUserTest(String uid) {
        String firstname = "Test";
        String lastname = "User";
        String phoneNumber = "06060606060";
        String mail = "mail";
        String password = "test";
        String authenticationSourceName = USER_TEST_SOURCE;
        boolean enabled = true;

        this.administrationController.createUser(this.adminSession, uid, firstname, lastname, phoneNumber, mail, password, authenticationSourceName, enabled);
    }

    public void createDefaultUserTest2(String uid) {
        String firstname = "Test";
        String lastname = "User 2";
        String phoneNumber = "06060606060";
        String mail = "usertest2@mail.com";
        String password = "test";
        String authenticationSourceName = USER_TEST_SOURCE;
        boolean enabled = true;

        this.administrationController.createUser(this.adminSession, uid, firstname, lastname, phoneNumber, mail, password, authenticationSourceName, enabled);
    }

    public void createTestUsers() {

        org.kimios.kernel.ws.pojo.User user = new org.kimios.kernel.ws.pojo.User(
                USER_TEST_1,
                "Test",
                "User 1",
                "06060606060",
                USER_TEST_SOURCE,
                new Date(),
                "mail"
        );
        this.createUserFromPojoWithPassword(user, "test");

        user = new org.kimios.kernel.ws.pojo.User(
                USER_TEST_2,
                "Test",
                "User 2",
                "06060606060",
                USER_TEST_SOURCE,
                new Date(),
                "usertest2@mail.com"
        );
        this.createUserFromPojoWithPassword(user, "test");

        user = new org.kimios.kernel.ws.pojo.User(
                USER_TEST_3,
                "Test",
                "User 3",
                "06060606060",
                USER_TEST_SOURCE,
                new Date(),
                "usertest3@coolmail.com"
        );
        this.createUserFromPojoWithPassword(user, "test");
    }

    public void deleteTestUsers() {
        String[] userNames = {
                USER_TEST_1,
                USER_TEST_2,
                USER_TEST_3
        };
        for (String userName : userNames) {
            this.administrationController.deleteUser(this.adminSession, userName, USER_TEST_SOURCE);
        }
    }

    public void createUserFromPojoWithPassword(org.kimios.kernel.ws.pojo.User user, String password) {
        this.administrationController.createUser(this.adminSession, user.getUid(), user.getFirstName(), user.getLastName(), user.getPhoneNumber(), user.getMail(), password, user.getSource(), true);
    }

    public void deleteUserTest() {
        this.administrationController.deleteUser(this.adminSession, DEFAULT_USER_TEST_ID, USER_TEST_SOURCE);
    }

    public void giveAccessToEntityForUser(Session session, DMEntity ent, User user, boolean read, boolean write, boolean full) {
        List<DMEntitySecurity> entities = this.securityController.getDMEntitySecurityies(session, ent.getUid());

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

        this.securityController.updateDMEntitySecurities(session, ent.getUid(), xmlStream, false, true);
    }
}
