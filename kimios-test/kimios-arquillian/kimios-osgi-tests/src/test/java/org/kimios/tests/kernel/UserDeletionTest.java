package org.kimios.tests.kernel;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kimios.client.controller.helpers.StringTools;
import org.kimios.kernel.dms.model.Folder;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.user.model.User;
import org.kimios.tests.deployments.OsgiDeployment;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class UserDeletionTest extends KernelTestAbstract {

    private User userTest;
    private Folder folderTest;

    @Deployment(name="karaf")
    public static JavaArchive createDeployment() {
        return OsgiDeployment.createArchive("UserDeletionTest.jar",
                null, UserDeletionTest.class,
                KernelTestAbstract.class,
                StringTools.class
        );
    }

    @Before
    public void setUp() {

        this.init();

        this.setAdminSession(this.getSecurityController().startSession(ADMIN_LOGIN, USER_TEST_SOURCE, ADMIN_PWD));

        try {
            this.workspaceTest = this.workspaceController.getWorkspace(this.getAdminSession(), WORKSPACE_TEST_NAME);
        } catch (Exception e) {
            this.workspaceController.createWorkspace(this.getAdminSession(), WORKSPACE_TEST_NAME);
            this.workspaceTest = this.workspaceController.getWorkspace(this.getAdminSession(), WORKSPACE_TEST_NAME);
        }

        this.createUsersTest();

        long folderUid = this.folderController.createFolder(this.getAdminSession(), "Awesome Test's folder", this.workspaceTest.getUid(), false);
        this.folderTest = this.folderController.getFolder(this.getAdminSession(), folderUid);
    }

    private void createUsersTest() {
        String uid = DEFAULT_USER_TEST_ID;
        String firstname = "Test";
        String lastname = "User";
        String phoneNumber = "06060606060";
        String mail = "mail";
        String password = "test";
        String authenticationSourceName = USER_TEST_SOURCE;
        boolean enabled = true;

        this.userTest = this.createUser(
                this.administrationController, this.getAdminSession(), uid, firstname, lastname, phoneNumber, mail,
                password, authenticationSourceName, enabled
        );
    }

    @Test
    public void testUserDeletion() {
        Folder folderTest = this.folderController.getFolder(this.getAdminSession(), this.folderTest.getUid());
        // grant access to user test
        this.changePermissionOnEntityForUser(this.getAdminSession(), this.userTest, folderTest, true, true, false);
        Session userTestSession = this.getSecurityController().startSession(DEFAULT_USER_TEST_ID, USER_TEST_SOURCE, "test");
        // user test can read, write and has not full access
        assertTrue(this.getSecurityController().canRead(userTestSession, this.folderTest.getUid()));
        assertTrue(this.getSecurityController().canWrite(userTestSession, this.folderTest.getUid()));
        assertFalse(this.getSecurityController().hasFullAccess(userTestSession, this.folderTest.getUid()));

        // delete the user test
        this.administrationController.deleteUser(this.getAdminSession(), this.userTest.getUid(), this.userTest.getAuthenticationSourceName());
        // create the user test again
        this.createUsersTest();
        // restart the session
        userTestSession = this.getSecurityController().startSession(DEFAULT_USER_TEST_ID, USER_TEST_SOURCE, "test");
        // user can't read, write and has not full access
        // because the security rules and ACLs have been removed
        assertFalse(this.getSecurityController().canRead(userTestSession, this.folderTest.getUid()));
        assertFalse(this.getSecurityController().canWrite(userTestSession, this.folderTest.getUid()));
        assertFalse(this.getSecurityController().hasFullAccess(userTestSession, this.folderTest.getUid()));
    }

    @After
    public void tearDown() {

        try {
            this.folderController.deleteFolder(this.getAdminSession(), this.folderTest.getUid());
            this.administrationController.deleteUser(this.getAdminSession(), this.userTest.getUid(), USER_TEST_SOURCE);
        } catch (Exception e) {
            System.out.println("Exception of type " + e.getClass().getName());
            System.out.println("Exception of type " + e.getMessage());
            System.out.println("Exception of type " + e.getCause());
        }
    }

}
