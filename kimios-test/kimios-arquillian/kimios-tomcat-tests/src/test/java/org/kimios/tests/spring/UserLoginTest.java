package org.kimios.tests.spring;

import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.junit.Test;
import org.kimios.kernel.controller.IAdministrationController;
import org.kimios.kernel.controller.ISecurityController;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.ws.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by farf on 19/03/16.
 */
public class UserLoginTest {





    @Autowired
    private ISecurityController securityController;
    @Autowired
    private IAdministrationController administrationController;


    public void loginTest(){
        Session session = securityController.startSession("admin", "kimios", "kimios");
        Assert.assertNotNull(session);
        Assert.assertTrue(securityController.isSessionAlive(session.getUid()));

        this.securityController.endSession(session.getUid());
    }


    public void badLoginTest(){
        Session session = null;
        try {
            session = securityController.startSession("admin", "kimios", "wrong");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Assert.assertNull(session);
    }


    public void getConnectedUsersTest(){
        Session session = securityController.startSession("admin", "kimios", "kimios");
        Assert.assertNotNull(session);
        User[] connectedUsers = this.administrationController.getConnectedUsers(session);
        Assert.assertTrue(connectedUsers.length > 0);
        Assert.assertEquals(connectedUsers[0].getUid(), "admin");
        Assert.assertEquals(connectedUsers[0].getSource(), "kimios");
    }


}
