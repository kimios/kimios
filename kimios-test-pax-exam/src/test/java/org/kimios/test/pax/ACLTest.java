package org.kimios.test.pax;

import org.junit.*;
import org.kimios.kernel.security.model.DMEntitySecurity;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class ACLTest extends KimiosTest {

    @Before
    public void createTestData() {
        super.createTestData();
    }

    @After
    public void deleteTestData() {
        super.deleteTestData();
    }

    @Test
    public void testACL() {
        if (ACLTestUtils.getInstance() == null) {
            ACLTestUtils.createInstance(
                    securityController,
                    administrationController,
                    workspaceController,
                    folderController,
                    documentController,
                    documentVersionController
            );
        }
        ACLTestUtils aclTestUtils = ACLTestUtils.getInstance();

        /*try {
            aclTestUtils.setUp();
        } catch (Exception e) {
            System.out.println("********************************");
            System.out.println(e.getMessage());
            e.printStackTrace();
            Assert.fail();
        }*/

        AccessRight fffAccessRight = new AccessRight(false, false, false);
        AccessRight tffAccessRight = new AccessRight(true, false, false);
        AccessRight ttfAccessRight = new AccessRight(true, true, false);
        AccessRight tttAccessRight = new AccessRight(true, true, true);

        User user1 = administrationController.getUser(aclTestUtils.getSession(), aclTestUtils.getArray()[0][0], "kimios");
        Session user1Session = securityController.startSession(user1.getUid(), "kimios");

        AccessRight user1AccessRight = new AccessRight(
                securityController.canRead(user1Session, aclTestUtils.getWorkspace().getUid()),
                securityController.canWrite(user1Session, aclTestUtils.getWorkspace().getUid()),
                securityController.hasFullAccess(user1Session, aclTestUtils.getWorkspace().getUid())
        );
        Assert.assertEquals(user1AccessRight, fffAccessRight);

        List<DMEntitySecurity> securityList = new ArrayList<>();
        securityList.add(new DMEntitySecurity(
                aclTestUtils.getWorkspace().getUid(),
                aclTestUtils.getWorkspace().getType(),
                user1.getUid(),
                user1.getAuthenticationSourceName(),
                user1.getType(),
                true,
                false,
                false
        ));
        securityController.updateDMEntitySecurities(
                aclTestUtils.getSession(),
                aclTestUtils.getWorkspace().getUid(),
                securityList,
                false,
                true
        );
        aclTestUtils.updateAccessRight(user1AccessRight, user1Session, aclTestUtils.getWorkspace().getUid());
        Assert.assertEquals(user1AccessRight, tffAccessRight);

        User user2 = administrationController.getUser(aclTestUtils.getSession(), aclTestUtils.getArray()[1][0], "kimios");
        Session user2Session = securityController.startSession(user2.getUid(), "kimios");
        AccessRight user2AccessRight = new AccessRight();
        aclTestUtils.updateAccessRight(user2AccessRight, user2Session, aclTestUtils.getWorkspace().getUid());
        Assert.assertEquals(user2AccessRight, fffAccessRight);

        User user3 = administrationController.getUser(aclTestUtils.getSession(), aclTestUtils.getArray()[2][0], "kimios");
        Session user3Session = securityController.startSession(user3.getUid(), "kimios");
        AccessRight user3AccessRight = new AccessRight();
        aclTestUtils.updateAccessRight(user3AccessRight, user3Session, aclTestUtils.getWorkspace().getUid());
        Assert.assertEquals(user3AccessRight, fffAccessRight);

        // add write right to user1 for workspace
        securityList = new ArrayList<>();
        securityList.add(new DMEntitySecurity(
                aclTestUtils.getWorkspace().getUid(),
                aclTestUtils.getWorkspace().getType(),
                user1.getUid(),
                user1.getAuthenticationSourceName(),
                user1.getType(),
                false,
                true,
                false
        ));
        securityController.updateDMEntitySecurities(
                aclTestUtils.getSession(),
                aclTestUtils.getWorkspace().getUid(),
                securityList,
                false,
                true
        );
        aclTestUtils.updateAccessRight(user1AccessRight, user1Session, aclTestUtils.getWorkspace().getUid());
        Assert.assertEquals(user1AccessRight, ttfAccessRight);

        securityList = new ArrayList<>();
        securityList.add(new DMEntitySecurity(
                aclTestUtils.getWorkspace().getUid(),
                aclTestUtils.getWorkspace().getType(),
                user1.getUid(),
                user1.getAuthenticationSourceName(),
                user1.getType(),
                true,
                false,
                false
        ));
        securityController.updateDMEntitySecurities(
                aclTestUtils.getSession(),
                aclTestUtils.getWorkspace().getUid(),
                securityList,
                false,
                true
        );
        aclTestUtils.updateAccessRight(user1AccessRight, user1Session, aclTestUtils.getWorkspace().getUid());
        Assert.assertEquals(user1AccessRight, tffAccessRight);

        // add read right to user2 for workspace
        securityList = new ArrayList<>();
        securityList.add(new DMEntitySecurity(
                aclTestUtils.getWorkspace().getUid(),
                aclTestUtils.getWorkspace().getType(),
                user2.getUid(),
                user2.getAuthenticationSourceName(),
                user2.getType(),
                true,
                false,
                false
        ));
        securityController.updateDMEntitySecurities(
                aclTestUtils.getSession(),
                aclTestUtils.getWorkspace().getUid(),
                securityList,
                false,
                true
        );
        aclTestUtils.updateAccessRight(user1AccessRight, user1Session, aclTestUtils.getWorkspace().getUid());
        aclTestUtils.updateAccessRight(user2AccessRight, user2Session, aclTestUtils.getWorkspace().getUid());
        aclTestUtils.updateAccessRight(user3AccessRight, user3Session, aclTestUtils.getWorkspace().getUid());
        Assert.assertEquals(fffAccessRight, user1AccessRight);
        Assert.assertEquals(tffAccessRight, user2AccessRight);
        Assert.assertEquals(fffAccessRight, user3AccessRight);

        // add read right to user3 and write for user2 for workspace
        securityList = new ArrayList<>();
        securityList.add(new DMEntitySecurity(
                aclTestUtils.getWorkspace().getUid(),
                aclTestUtils.getWorkspace().getType(),
                user3.getUid(),
                user3.getAuthenticationSourceName(),
                user3.getType(),
                true,
                false,
                false
        ));
        securityList.add(new DMEntitySecurity(
                aclTestUtils.getWorkspace().getUid(),
                aclTestUtils.getWorkspace().getType(),
                user2.getUid(),
                user2.getAuthenticationSourceName(),
                user2.getType(),
                true,
                true,
                false
        ));
        securityController.updateDMEntitySecurities(
                aclTestUtils.getSession(),
                aclTestUtils.getWorkspace().getUid(),
                securityList,
                false,
                true
        );
        aclTestUtils.updateAccessRight(user1AccessRight, user1Session, aclTestUtils.getWorkspace().getUid());
        aclTestUtils.updateAccessRight(user2AccessRight, user2Session, aclTestUtils.getWorkspace().getUid());
        aclTestUtils.updateAccessRight(user3AccessRight, user3Session, aclTestUtils.getWorkspace().getUid());
        Assert.assertEquals(fffAccessRight, user1AccessRight);
        Assert.assertEquals(ttfAccessRight, user2AccessRight);
        Assert.assertEquals(tffAccessRight, user3AccessRight);

        // add right with appendMode
        securityList = new ArrayList<>();
        securityList.add(new DMEntitySecurity(
                aclTestUtils.getWorkspace().getUid(),
                aclTestUtils.getWorkspace().getType(),
                user1.getUid(),
                user1.getAuthenticationSourceName(),
                user1.getType(),
                true,
                false,
                false
        ));
        securityList.add(new DMEntitySecurity(
                aclTestUtils.getWorkspace().getUid(),
                aclTestUtils.getWorkspace().getType(),
                user3.getUid(),
                user3.getAuthenticationSourceName(),
                user3.getType(),
                true,
                false,
                true
        ));
        securityController.updateDMEntitySecurities(
                aclTestUtils.getSession(),
                aclTestUtils.getWorkspace().getUid(),
                securityList,
                false,
                false
        );
        aclTestUtils.updateAccessRight(user1AccessRight, user1Session, aclTestUtils.getWorkspace().getUid());
        aclTestUtils.updateAccessRight(user2AccessRight, user2Session, aclTestUtils.getWorkspace().getUid());
        aclTestUtils.updateAccessRight(user3AccessRight, user3Session, aclTestUtils.getWorkspace().getUid());
        Assert.assertEquals(tffAccessRight, user1AccessRight);
        Assert.assertEquals(fffAccessRight, user2AccessRight);
        Assert.assertEquals(tttAccessRight, user3AccessRight);


        try {
            aclTestUtils.tearDown();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

        Assert.assertTrue(true);
    }

    /*@AfterClass
    public static void deleteTestData() {
        System.out.println("*********************");
        System.out.println("in deleteTestData()");
        if (ACLTestUtils.instance != null) {
            ACLTestUtils aclTestUtils = ACLTestUtils.instance;
            try {
                System.out.println("tearDown()");
                aclTestUtils.tearDown();
            } catch (Exception e) {
                System.out.println("********************************");
                System.out.println(e.getMessage());
            }
        }
    }*/
}
