package org.kimios.tests.kernel;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kimios.kernel.dms.model.Workspace;
import org.kimios.kernel.security.model.Role;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.user.model.User;
import org.kimios.tests.deployments.OsgiDeployment;

import java.util.List;
import java.util.Vector;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class UserCreationTest extends KernelTestAbstract {

    private Session adminSession;

    private static String USER_NAME_TEST = "userTest";

    @Deployment(name="karaf")
    public static JavaArchive createDeployment() {
        return OsgiDeployment.createArchive("userCreationTest.jar", null, UserCreationTest.class);
    }

    @Before
    public void setUp() {

       this.init();
       this.adminSession = this.securityController.startSession(ADMIN_LOGIN, ADMIN_SOURCE, ADMIN_PWD);
    }

    @Test
    public void testCreateUser() {
        String uid = USER_NAME_TEST;
        String firstname = "Test";
        String lastname = "User";
        String phoneNumber = "06060606060";
        String mail = "mail";
        String password = "test";
        String authenticationSourceName = ADMIN_SOURCE;
        boolean enabled = true;

        User user = null;
        try {
            user = this.administrationController.getUser(this.adminSession, uid, ADMIN_SOURCE);
        } catch (Exception e) {
        }
        if (user == null) {
            this.administrationController.createUser(this.adminSession, uid, firstname, lastname, phoneNumber, mail, password, authenticationSourceName, enabled);
            user = this.administrationController.getUser(this.adminSession, uid, ADMIN_SOURCE);
        }

        assertNotNull(user);
        assertEquals(user.getUid(), uid);
        assertEquals(user.getFirstName(), firstname);
        assertEquals(user.getLastName(), lastname);
        assertEquals(user.getPhoneNumber(), phoneNumber);
        assertEquals(user.getMail(), mail);
        assertEquals(user.getAuthenticationSourceName(), authenticationSourceName);
        assertEquals(user.isEnabled(), enabled);

        // test user connection
        Session userTestSession = this.securityController.startSession(uid, authenticationSourceName, password);
        assertNotNull(userTestSession);
        assertTrue(userTestSession.getUid().length() > 0);

        //user workspaces
        List<Workspace> workspaces = this.workspaceController.getWorkspaces(userTestSession);
        assertNotNull(workspaces);
        assertTrue(workspaces.isEmpty());

        // can create a workspace ?
        // default is no
        boolean canCreateWorkspace = this.securityController.canCreateWorkspace(userTestSession);
        assertFalse(canCreateWorkspace);
        // set to true
//        this.administrationController.createRole(this.adminSession, Role.WORKSPACE, uid, ADMIN_USER_SOURCE);
        // is that true now ?
//        canCreateWorkspace = this.securityController.canCreateWorkspace(userTestSession);
//        assertTrue(canCreateWorkspace);

        boolean hasStudioAccess = this.securityController.hasStudioAccess(userTestSession);
        assertFalse(hasStudioAccess);

        boolean hasReportingAccess = this.securityController.hasReportingAccess(userTestSession);
        assertFalse(hasReportingAccess);

        boolean isAdmin = this.securityController.isAdmin(userTestSession);
        assertFalse(isAdmin);

        boolean isAdmin2 = this.securityController.isAdmin(uid, ADMIN_SOURCE);
        assertFalse(isAdmin2);

        boolean isSessionAlive = this.securityController.isSessionAlive(adminSession.getUid());
        assertTrue(isSessionAlive);

        Vector<Role> userRoles = this.administrationController.getRoles(this.adminSession, uid, ADMIN_SOURCE);
        assertEquals(0, userRoles.size());

        //TODO : when a user is removed, remove also the user's roles assignments
        this.administrationController.deleteUser(this.adminSession, uid, ADMIN_SOURCE);
        User userJustDeleted = null;
        try {
            userJustDeleted = this.securityController.getUser(uid, password);
        } catch (NullPointerException e) {
            System.out.println("user is null because it just has been deleted");
        }
        assertNull(userJustDeleted);
    }

}
