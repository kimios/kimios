package org.kimios.tests.kernel;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kimios.client.controller.helpers.StringTools;
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.dms.model.Folder;
import org.kimios.kernel.dms.model.Workspace;
import org.kimios.kernel.security.model.DMEntitySecurity;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.user.model.User;
import org.kimios.tests.deployments.OsgiDeployment;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(Arquillian.class)
public class SeveralUsersSecuritySimpleTest extends KernelTestAbstract {

    private Session adminSession;
    private User userTest1;
    private User userTest2;
    private User userTest3;
    private Session userTestSession1;
    private Session userTestSession2;
    private Session userTestSession3;
    private Workspace workspaceTest;
    private Folder totoFolder;
    private Folder totoFolder2;

    private static String USER_PASSWORD = "test";

    @Deployment(name="karaf")
    public static JavaArchive createDeployment() {
        return OsgiDeployment.createArchive(SeveralUsersSecuritySimpleTest.class.getSimpleName() + ".jar", null, SeveralUsersSecuritySimpleTest.class,
                StringTools.class);
    }

    private void giveWorkspaceAccessToUser(Session session, Workspace workspace, User user) {
        List<DMEntitySecurity> entities = this.securityController.getDMEntitySecurityies(session, workspace.getUid());

        String xmlStreamExistingEntitites = "";
        for (DMEntitySecurity entity : entities) {
            xmlStreamExistingEntitites += "\t<rule " +
                    "security-entity-type=\"" + entity.getType() + "\" " +
                    "security-entity-uid=\"" + StringTools.magicDoubleQuotes( entity.getName() ) + "\" " +
                    "security-entity-source=\"" + StringTools.magicDoubleQuotes( entity.getSource() ) + "\" " +
                    "read=\"" + Boolean.toString(entity.isRead()) + "\" " +
                    "write=\"" + Boolean.toString(entity.isWrite()) + "\" " +
                    "full=\"" + Boolean.toString(entity.isFullAccess()) + "\" />\r\n";
        }

        String xmlStream = "<security-rules dmEntityId=\"" + workspace.getUid() + "\"" +
                " dmEntityTye=\"" + workspace.getType() + "\">\r\n";

        xmlStream += xmlStreamExistingEntitites;

        xmlStream += "\t<rule " +
                "security-entity-type=\"" + user.getType() + "\" " +
                "security-entity-uid=\"" + StringTools.magicDoubleQuotes( user.getUid() ) + "\" " +
                "security-entity-source=\"" + StringTools.magicDoubleQuotes( user.getAuthenticationSourceName() ) + "\" " +
                "read=\"" + true + "\" " +
                "write=\"" + true + "\" " +
                "full=\"" + false + "\" />\r\n";
        xmlStream += "</security-rules>";

        this.securityController.updateDMEntitySecurities(session, workspace.getUid(), xmlStream, false, true);
    }

    @Before
    public void setUp() {

        this.init();

        this.adminSession = this.securityController.startSession(ADMIN_LOGIN, USER_TEST_SOURCE, ADMIN_PWD);

        try {
            // get test workspace
            this.workspaceTest = this.workspaceController.getWorkspace(this.adminSession, WORKSPACE_TEST_NAME);
        } catch (NullPointerException e) {
            // create workspace
            this.workspaceController.createWorkspace(this.adminSession, WORKSPACE_TEST_NAME);
            this.workspaceTest = this.workspaceController.getWorkspace(this.adminSession, WORKSPACE_TEST_NAME);
        }

        // create users
        // users data
        String[] uids = {"toto", "titi", "tutu"};
        String[] firstnames = {"user 1", "user 2", "user 3"};
        String[] lastnames = {"test", "test", "test"};
        String phoneNumber = "06060606060";
        String[] mails = {"toto@teclib.mail", "titi@teclib.mail", "tutu@teclib.mail"};
        String password = USER_PASSWORD;
        String authenticationSourceName = USER_TEST_SOURCE;
        boolean enabled = true;

        // user toto
        int i = 0;
        this.administrationController.createUser(this.adminSession, uids[i], firstnames[i], lastnames[i], phoneNumber, mails[i], password, authenticationSourceName, enabled);
        this.userTest1 = this.administrationController.getUser(this.adminSession, uids[i], authenticationSourceName);
        // user titi
        i = 1;
        this.administrationController.createUser(this.adminSession, uids[i], firstnames[i], lastnames[i], phoneNumber, mails[i], password, authenticationSourceName, enabled);
        this.userTest2 = this.administrationController.getUser(this.adminSession, uids[i], authenticationSourceName);
        // user tutu
        i = 2;
        this.administrationController.createUser(this.adminSession, uids[i], firstnames[i], lastnames[i], phoneNumber, mails[i], password, authenticationSourceName, enabled);
        this.userTest3 = this.administrationController.getUser(this.adminSession, uids[i], authenticationSourceName);

        this.userTestSession1 = this.securityController.startSession(this.userTest1.getUid(), this.userTest1.getAuthenticationSourceName(), USER_PASSWORD);
        this.userTestSession2 = this.securityController.startSession(this.userTest2.getUid(), this.userTest2.getAuthenticationSourceName(), USER_PASSWORD);
        this.userTestSession3 = this.securityController.startSession(this.userTest3.getUid(), this.userTest3.getAuthenticationSourceName(), USER_PASSWORD);

        i = 0;
        this.userTest1 = this.administrationController.getUser(this.adminSession, uids[i], authenticationSourceName);
        i = 1;
        this.userTest2 = this.administrationController.getUser(this.adminSession, uids[i], authenticationSourceName);
        i = 2;
        this.userTest3 = this.administrationController.getUser(this.adminSession, uids[i], authenticationSourceName);
    }

    @After
    public void tearDown() {
        List<DMEntitySecurity> emptyList = new ArrayList<DMEntitySecurity>();

        // remove permission on folders
        this.securityController.updateDMEntitySecurities(this.adminSession, this.totoFolder.getUid(), emptyList, true, true);
        this.securityController.updateDMEntitySecurities(this.adminSession, this.totoFolder2.getUid(), emptyList, true, true);
        // remove folders
        this.folderController.deleteFolder(this.adminSession, this.totoFolder.getUid());
        this.folderController.deleteFolder(this.adminSession, this.totoFolder2.getUid());

        // remove permissions on workspace
        this.securityController.updateDMEntitySecurities(this.adminSession, this.workspaceTest.getUid(), emptyList, true, true);

        // remove users
        this.administrationController.deleteUser(this.adminSession, this.userTest1.getUid(), this.userTest1.getAuthenticationSourceName());
        this.administrationController.deleteUser(this.adminSession, this.userTest2.getUid(), this.userTest2.getAuthenticationSourceName());
        this.administrationController.deleteUser(this.adminSession, this.userTest3.getUid(), this.userTest3.getAuthenticationSourceName());
    }

    @Test
    public void testGiveAccessToUsers() {
        this.giveWorkspaceAccessToUser(this.adminSession, this.workspaceTest, this.userTest1);
        List<DMEntitySecurity> securityList = this.securityController.getDMEntitySecurityies(this.userTestSession1, this.workspaceTest.getUid());

        assertTrue(this.securityController.canRead(this.userTestSession1, this.workspaceTest.getUid()));
        assertFalse(this.securityController.canRead(this.userTestSession2, this.workspaceTest.getUid()));
        assertFalse(this.securityController.canRead(this.userTestSession3, this.workspaceTest.getUid()));

        assertTrue(this.securityController.canWrite(this.userTestSession1, this.workspaceTest.getUid()));
        assertFalse(this.securityController.canWrite(this.userTestSession2, this.workspaceTest.getUid()));
        assertFalse(this.securityController.canWrite(this.userTestSession3, this.workspaceTest.getUid()));

        this.giveWorkspaceAccessToUser(this.adminSession, this.workspaceTest, this.userTest2);
        securityList = this.securityController.getDMEntitySecurityies(this.userTestSession2, this.workspaceTest.getUid());

        assertTrue(this.securityController.canRead(this.userTestSession1, this.workspaceTest.getUid()));
        assertTrue(this.securityController.canRead(this.userTestSession2, this.workspaceTest.getUid()));
        assertFalse(this.securityController.canRead(this.userTestSession3, this.workspaceTest.getUid()));

        assertTrue(this.securityController.canWrite(this.userTestSession1, this.workspaceTest.getUid()));
        assertTrue(this.securityController.canWrite(this.userTestSession2, this.workspaceTest.getUid()));
        assertFalse(this.securityController.canWrite(this.userTestSession3, this.workspaceTest.getUid()));

        this.giveWorkspaceAccessToUser(this.adminSession, this.workspaceTest, this.userTest3);
        securityList = this.securityController.getDMEntitySecurityies(this.userTestSession3, this.workspaceTest.getUid());

        assertTrue(this.securityController.canRead(this.userTestSession1, this.workspaceTest.getUid()));
        assertTrue(this.securityController.canRead(this.userTestSession2, this.workspaceTest.getUid()));
        assertTrue(this.securityController.canRead(this.userTestSession3, this.workspaceTest.getUid()));

        assertTrue(this.securityController.canWrite(this.userTestSession1, this.workspaceTest.getUid()));
        assertTrue(this.securityController.canWrite(this.userTestSession2, this.workspaceTest.getUid()));
        assertTrue(this.securityController.canWrite(this.userTestSession3, this.workspaceTest.getUid()));

        // create folder WITH security inherited
        long folderUid = this.folderController.createFolder(this.userTestSession1, "toto's folder", workspaceTest.getUid(), true);
        this.totoFolder = this.folderController.getFolder(this.userTestSession1, folderUid);
        assertEquals(this.userTestSession1.getUserName(), this.totoFolder.getOwner());
        assertTrue(this.securityController.canRead(this.userTestSession1, this.totoFolder.getUid()));
        assertTrue(this.securityController.canRead(this.userTestSession2, this.totoFolder.getUid()));
        assertTrue(this.securityController.canRead(this.userTestSession3, this.totoFolder.getUid()));
        assertTrue(this.securityController.canWrite(this.userTestSession1, this.totoFolder.getUid()));
        assertTrue(this.securityController.canWrite(this.userTestSession2, this.totoFolder.getUid()));
        assertTrue(this.securityController.canWrite(this.userTestSession3, this.totoFolder.getUid()));

        // create folder WITHOUT security inherited
        long folder2Uid = this.folderController.createFolder(this.userTestSession1, "toto's second folder", workspaceTest.getUid(), false);
        this.totoFolder2 = this.folderController.getFolder(this.userTestSession1, folder2Uid);
        assertTrue(this.securityController.canRead(this.userTestSession1, this.totoFolder2.getUid()));
        assertFalse(this.securityController.canRead(this.userTestSession2, this.totoFolder2.getUid()));
        assertFalse(this.securityController.canRead(this.userTestSession3, this.totoFolder2.getUid()));
        assertTrue(this.securityController.canWrite(this.userTestSession1, this.totoFolder2.getUid()));
        assertFalse(this.securityController.canWrite(this.userTestSession2, this.totoFolder2.getUid()));
        assertFalse(this.securityController.canWrite(this.userTestSession3, this.totoFolder2.getUid()));


        // user 1 gives access to user 2 for folder
        this.giveAccessToEntityForUser(this.userTestSession1, this.totoFolder2, this.userTest2, true, false, false);
        assertTrue(this.securityController.canRead(this.userTestSession2, this.totoFolder2.getUid()));
        assertFalse(this.securityController.canWrite(this.userTestSession2, this.totoFolder2.getUid()));

        // user 1 gives access to user 3 for folder
        this.giveAccessToEntityForUser(this.userTestSession1, this.totoFolder2, this.userTest3, true, true, false);
        assertTrue(this.securityController.canRead(this.userTestSession3, this.totoFolder2.getUid()));
        assertTrue(this.securityController.canWrite(this.userTestSession3, this.totoFolder2.getUid()));

        // user 1 removes access to user 2 for folder
        this.giveAccessToEntityForUser(this.userTestSession1, this.totoFolder2, this.userTest2, false, false, false);
        assertFalse(this.securityController.canRead(this.userTestSession2, this.totoFolder2.getUid()));
        assertFalse(this.securityController.canWrite(this.userTestSession2, this.totoFolder2.getUid()));
        // can user 2 get the folder ?
        // must be no
        Folder totoFolder2WithUser2 = null;
        try {
            totoFolder2WithUser2 = this.folderController.getFolder(this.userTestSession2, folder2Uid);
        } catch (Exception e) {

        }
        assertNull(totoFolder2WithUser2);

        // can user 3 get the folder ?
        // must be yes
        Folder totoFolder2WithUser3 = null;
        try {
            totoFolder2WithUser3 = this.folderController.getFolder(this.userTestSession3, folder2Uid);
        } catch (Exception e) {

        }
        assertNotNull(totoFolder2WithUser3);

        // now let's create a document
        String path = this.totoFolder2.getPath() + "/" + "totoDoc1";
        long totoDoc1Uid = -1;
        try {
            totoDoc1Uid = this.documentController.createDocument(this.userTestSession1, path, true);
        } catch (Exception e) {
            System.out.println("Exception of type : " + e.getClass().getName());
            System.out.println("Message : " + e.getMessage());
            System.out.println("Cause : " + e.getCause());
        }
        assertTrue(totoDoc1Uid > 0);
        assertTrue(this.securityController.hasFullAccess(this.userTestSession1, totoDoc1Uid));
        assertFalse(this.securityController.hasFullAccess(this.userTestSession2, totoDoc1Uid));
        assertFalse(this.securityController.hasFullAccess(this.userTestSession3, totoDoc1Uid));
        assertTrue(this.securityController.canRead(this.userTestSession1, totoDoc1Uid));
        assertFalse(this.securityController.canRead(this.userTestSession2, totoDoc1Uid));
        assertTrue(this.securityController.canRead(this.userTestSession3, totoDoc1Uid));
        assertTrue(this.securityController.canWrite(this.userTestSession1, totoDoc1Uid));
        assertFalse(this.securityController.canWrite(this.userTestSession2, totoDoc1Uid));
        assertTrue(this.securityController.canWrite(this.userTestSession3, totoDoc1Uid));

        // now let's create an toher document (WITHOUT security inherited
        path = this.totoFolder2.getPath() + "/" + "totoDoc2";
        long totoDoc2Uid = -1;
        try {
            totoDoc2Uid = this.documentController.createDocument(this.userTestSession1, path, false);
        } catch (Exception e) {
            System.out.println("Exception of type : " + e.getClass().getName());
            System.out.println("Message : " + e.getMessage());
            System.out.println("Cause : " + e.getCause());
        }
        assertTrue(totoDoc2Uid > 0);
        assertTrue(this.securityController.hasFullAccess(this.userTestSession1, totoDoc2Uid));
        assertFalse(this.securityController.hasFullAccess(this.userTestSession2, totoDoc2Uid));
        assertFalse(this.securityController.hasFullAccess(this.userTestSession3, totoDoc2Uid));
        assertTrue(this.securityController.canRead(this.userTestSession1, totoDoc2Uid));
        assertFalse(this.securityController.canRead(this.userTestSession2, totoDoc2Uid));
        assertFalse(this.securityController.canRead(this.userTestSession3, totoDoc2Uid));
        assertTrue(this.securityController.canWrite(this.userTestSession1, totoDoc2Uid));
        assertFalse(this.securityController.canWrite(this.userTestSession2, totoDoc2Uid));
        assertFalse(this.securityController.canWrite(this.userTestSession3, totoDoc2Uid));

        Document totoDoc1 = this.documentController.getDocument(this.userTestSession1, totoDoc1Uid);

        // Can user 2 change permissions on totoDoc1 for user 1 ?
        // must be no
        boolean user2CanChangeTotoDoc1PermissionsForUser1 = false;
        try {
            this.giveAccessToEntityForUser(this.userTestSession2, totoDoc1, userTest1, true, false, false);
        } catch (Exception e) {
            assertEquals("AccessDeniedException", e.getClass().getSimpleName());
        }
        assertFalse(user2CanChangeTotoDoc1PermissionsForUser1);

        // Can user 2 change permissions on totoDoc1 for user 2 ?
        // must be no
        boolean user2CanChangeTotoDoc1Permissions = false;
        try {
            this.giveAccessToEntityForUser(this.userTestSession2, totoDoc1, userTest2, true, false, false);
        } catch (Exception e) {
            assertEquals("AccessDeniedException", e.getClass().getSimpleName());
        }
        assertFalse(user2CanChangeTotoDoc1Permissions);

        // Can user 3 change permissions on totoDoc1 for user 2 ?
        // must be no
        boolean user3CanChangeTotoDoc1Permissions = false;
        try {
            this.giveAccessToEntityForUser(this.userTestSession3, totoDoc1, userTest2, true, false, false);
        } catch (Exception e) {
            assertEquals("AccessDeniedException", e.getClass().getSimpleName());
        }
        assertFalse(user3CanChangeTotoDoc1Permissions);

        this.giveAccessToEntityForUser(this.userTestSession1, totoDoc1, userTest2, true, false, false);
        this.giveAccessToEntityForUser(this.userTestSession1, totoDoc1, userTest3, true, false, false);
        assertTrue(this.securityController.canRead(this.userTestSession2, totoDoc1Uid));
        assertFalse(this.securityController.canWrite(this.userTestSession2, totoDoc1Uid));
        assertTrue(this.securityController.canRead(this.userTestSession3, totoDoc1Uid));
        assertFalse(this.securityController.canWrite(this.userTestSession3, totoDoc1Uid));

        this.giveAccessToEntityForUser(this.userTestSession1, totoDoc1, userTest2, true, true, true);
        assertTrue(this.securityController.hasFullAccess(this.userTestSession2, totoDoc1Uid));
        this.giveAccessToEntityForUser(this.userTestSession2, totoDoc1, userTest1, true, false, false);
        assertTrue(this.securityController.canRead(this.userTestSession1, totoDoc1Uid));
        assertTrue(this.securityController.canWrite(this.userTestSession1, totoDoc1Uid));
        assertTrue(this.securityController.hasFullAccess(this.userTestSession1, totoDoc1Uid));
        assertEquals(totoDoc1.getOwner(), this.userTest1.getUid());

        this.giveAccessToEntityForUser(this.userTestSession1, totoDoc1, userTest2, true, false, false);
        assertTrue(this.securityController.canRead(this.userTestSession2, totoDoc1Uid));
        assertFalse(this.securityController.canWrite(this.userTestSession2, totoDoc1Uid));
        assertFalse(this.securityController.hasFullAccess(this.userTestSession2, totoDoc1Uid));

        this.administrationController.changeOwnership(this.adminSession, totoDoc1Uid, userTest2.getUid(), userTest2.getAuthenticationSourceName());
        totoDoc1 = this.documentController.getDocument(this.userTestSession1, totoDoc1Uid);
        assertEquals(totoDoc1.getOwner(), this.userTest2.getUid());
        assertTrue(this.securityController.canRead(this.userTestSession2, totoDoc1Uid));
        assertTrue(this.securityController.canWrite(this.userTestSession2, totoDoc1Uid));
        assertTrue(this.securityController.hasFullAccess(this.userTestSession2, totoDoc1Uid));

    }
}
