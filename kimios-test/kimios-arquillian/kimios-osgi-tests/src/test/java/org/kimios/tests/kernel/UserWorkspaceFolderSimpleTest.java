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
import org.kimios.kernel.dms.model.Workspace;
import org.kimios.kernel.security.model.DMEntitySecurity;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.user.model.User;
import org.kimios.tests.deployments.OsgiDeployment;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class UserWorkspaceFolderSimpleTest extends KernelTestAbstract {

    private User userTest;
    private Session userTestSession;

    private static String WORKSPACE_TEST = "workspaceTest";
    private static String USERID_TEST_2 = "userTest2";

    @Deployment(name="karaf")
    public static JavaArchive createDeployment() {

        return OsgiDeployment.createArchive("userDocumentsTest.jar",
                null, UserWorkspaceFolderSimpleTest.class,
                KernelTestAbstract.class,
                StringTools.class);
    }

    @Before
    public void setUp() {


        this.init();

        this.setAdminSession(this.getSecurityController().startSession(ADMIN_LOGIN, ADMIN_SOURCE, ADMIN_PWD));

        try {
            this.workspaceTest = this.workspaceController.getWorkspace(this.getAdminSession(), WORKSPACE_TEST);
        } catch (NullPointerException e) {
            this.workspaceController.createWorkspace(this.getAdminSession(), WORKSPACE_TEST);
            this.workspaceTest = this.workspaceController.getWorkspace(this.getAdminSession(), WORKSPACE_TEST);
        }

        this.createUsersTest();
    }

    private void createUsersTest() {
        String uid = "";
        String firstname = "Test";
        String lastname = "User";
        String phoneNumber = "06060606060";
        String mail = "mail";
        String password = "test";
        String authenticationSourceName = ADMIN_SOURCE;
        boolean enabled = true;

        uid = DEFAULT_USER_TEST_ID;
        try {
            this.userTest = this.administrationController.getUser(this.getAdminSession(), uid, authenticationSourceName);
        } catch (NullPointerException e) {
        }
        if (this.userTest == null) {
            this.administrationController.createUser(this.getAdminSession(), uid, firstname, lastname, phoneNumber, mail, password, authenticationSourceName, enabled);
            this.userTest = this.administrationController.getUser(this.getAdminSession(), uid, authenticationSourceName);
        }

        uid = USERID_TEST_2;
        User userTest2 = null;
        try {
            userTest2 = this.administrationController.getUser(this.getAdminSession(), uid, authenticationSourceName);
        } catch (NullPointerException e) {
        }
        if (userTest2 == null) {
            this.administrationController.createUser(this.getAdminSession(), uid, firstname, lastname, phoneNumber, mail, password, authenticationSourceName, enabled);
            userTest2 = this.administrationController.getUser(this.getAdminSession(), uid, authenticationSourceName);
        }
    }

    @Test
    public void testUserWorkspaces() {
        // user can see any workspace ?
        List<Workspace> workspaces = null;
        try {
            workspaces = this.workspaceController.getWorkspaces(this.userTestSession);
        } catch (NullPointerException e) {
        }
        assertNull(workspaces);

        // give the access to the userTest
        Workspace workspaceTest = this.workspaceController.getWorkspace(this.getAdminSession(), WORKSPACE_TEST);
        assertNotNull(workspaceTest);
        long wUid = workspaceTest.getUid();
        // update workspace security entities
        List<DMEntitySecurity> items = new ArrayList<DMEntitySecurity>();
        DMEntitySecurity tmp = new DMEntitySecurity();
        tmp.setDmEntityType(1);
        tmp.setDmEntityUid(wUid);
        tmp.setName(this.userTest.getUid());
        tmp.setSource(this.userTest.getAuthenticationSourceName());
        tmp.setRead(true);
        tmp.setWrite(false);
        tmp.setFullAccess(false);
        tmp.setType(userTest.getType());
        items.add(tmp);

        String xmlStream = "<security-rules dmEntityId=\"" + wUid + "\"" +
                " dmEntityTye=\"" + 1 + "\">\r\n";
        xmlStream += "\t<rule " +
                "security-entity-type=\"" + userTest.getType() + "\" " +
                "security-entity-uid=\"" + StringTools.magicDoubleQuotes( this.userTest.getUid() ) + "\" " +
                "security-entity-source=\"" + StringTools.magicDoubleQuotes( this.userTest.getAuthenticationSourceName() ) + "\" " +
                "read=\"" + true + "\" " +
                "write=\"" + true + "\" " +
                "full=\"" + false + "\" />\r\n";
        xmlStream += "</security-rules>";

        // start session userTest
        this.userTestSession = this.getSecurityController().startSession("userTest", "kimios", "test");

        // give access to the workspace
        this.getSecurityController().updateDMEntitySecurities(this.getAdminSession(), workspaceTest.getUid(), xmlStream, false, true);
        List<DMEntitySecurity> dmEntitySecurities = this.getSecurityController().getDMEntitySecurityies(this.userTestSession, wUid);
        assertNotNull(dmEntitySecurities);
        assertTrue(dmEntitySecurities.size() > 0);

        workspaces = this.workspaceController.getWorkspaces(this.userTestSession);
        assertTrue(workspaces.size() == 1);
        workspaceTest = this.workspaceController.getWorkspace(this.userTestSession, WORKSPACE_TEST);
        assertNotNull(workspaceTest);

        List<Folder> folders = null;
        try {
            folders = this.folderController.getFolders(this.userTestSession, 0);
        } catch (Exception e) {
        }
        assertNull(folders);

        // create folder with userTest session
        long folderUid = this.folderController.createFolder(this.userTestSession, "userTest folder 01", workspaceTest.getUid(), true);
        assertTrue(folderUid > 0);
        assertTrue(this.getSecurityController().canRead(this.userTestSession, folderUid));
        assertTrue(this.getSecurityController().canWrite(this.userTestSession, folderUid));

        Folder folder = this.folderController.getFolder(this.userTestSession, folderUid);
        String folderOwner = folder.getOwner();
        String folderOwnerSource = folder.getOwnerSource();
        assertEquals(userTestSession.getUserName(), folderOwner);
        assertEquals(userTestSession.getUserSource(), folderOwnerSource);

        // delete folder
        boolean folderDeleted = this.folderController.deleteFolder(this.userTestSession, folderUid);
        assertTrue(folderDeleted);
    }

    @Test
    public void testUserWorkspaceSecurity() {
        String uidUser = "userTest2";
        User user = this.administrationController.getUser(this.getAdminSession(), uidUser, ADMIN_SOURCE);
        assertNotNull(user);

        Workspace workspaceTest = this.workspaceController.getWorkspace(this.getAdminSession(), WORKSPACE_TEST);
        assertNotNull(workspaceTest);

//        List<DMEntitySecurity> items = new ArrayList<DMEntitySecurity>();
//        DMEntitySecurity tmp = new DMEntitySecurity();
//        tmp.setDmEntityType(1);
//        tmp.setDmEntityUid(workspaceTest.getUid());
//        tmp.setName(this.userTest.getName());
//        tmp.setSource(this.userTest.getAuthenticationSourceName());
//        tmp.setRead(true);
//        tmp.setWrite(true);
//        tmp.setFullAccess(false);
//        tmp.setType(userTest.getType());
//        items.add(tmp);

        String xmlStream = "<security-rules dmEntityId=\"" + workspaceTest.getUid() + "\"" +
                " dmEntityTye=\"" + workspaceTest.getType() + "\">\r\n";
        xmlStream += "\t<rule " +
                "security-entity-type=\"" + userTest.getType() + "\" " +
                "security-entity-uid=\"" + StringTools.magicDoubleQuotes( this.userTest.getUid() ) + "\" " +
                "security-entity-source=\"" + StringTools.magicDoubleQuotes( this.userTest.getAuthenticationSourceName() ) + "\" " +
                "read=\"" + true + "\" " +
                "write=\"" + true + "\" " +
                "full=\"" + false + "\" />\r\n";
        xmlStream += "</security-rules>";

        this.getSecurityController().updateDMEntitySecurities(this.getAdminSession(), workspaceTest.getUid(), xmlStream, false, true);
        List<DMEntitySecurity> dmEntitySecurities = this.getSecurityController().getDMEntitySecurityies(this.userTestSession, workspaceTest.getUid());
        assertTrue(dmEntitySecurities.size() > 0);


//        assertTrue(this.getSecurityController().canRead(this.getAdminSession(), workspaceTest.getUid()));
//        assertTrue(this.getSecurityController().canWrite(this.getAdminSession(), workspaceTest.getUid()));

    }

    @After
    public void tearDown() {
        // remove all permissions for user userTest on workspace if any
        this.removeUserPermissionsForEntity(this.getAdminSession(), this.userTest, this.workspaceTest);

        try {
            this.administrationController.deleteUser(this.getAdminSession(), this.userTest.getUid(), ADMIN_SOURCE);
        } catch (Exception e) {
            System.out.println("Exception of type " + e.getClass().getName());
            System.out.println("Exception of type " + e.getMessage());
            System.out.println("Exception of type " + e.getCause());
        }
        try {
            this.administrationController.deleteUser(this.getAdminSession(), USERID_TEST_2, ADMIN_SOURCE);
        } catch (Exception e) {
            System.out.println("Exception of type " + e.getClass().getName());
            System.out.println("Exception of type " + e.getMessage());
            System.out.println("Exception of type " + e.getCause());
        }
    }

}
