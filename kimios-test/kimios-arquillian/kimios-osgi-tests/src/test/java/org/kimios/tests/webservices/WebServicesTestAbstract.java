package org.kimios.tests.webservices;

import org.kimios.kernel.controller.ISecurityController;
import org.kimios.kernel.security.model.Session;
import org.kimios.tests.OsgiKimiosService;
import org.kimios.tests.TestAbstract;
import org.kimios.webservices.SecurityService;

/**
 * Created by tom on 11/02/16.
 */
public abstract class WebServicesTestAbstract extends TestAbstract {

    @OsgiKimiosService
    ISecurityController securityController;
    @OsgiKimiosService
    SecurityService securityService;

    Session adminSession;

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

    public void init() {
        this.initServices();
    }

    public void setSecurityController(ISecurityController securityController) {
        this.securityController = securityController;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}
