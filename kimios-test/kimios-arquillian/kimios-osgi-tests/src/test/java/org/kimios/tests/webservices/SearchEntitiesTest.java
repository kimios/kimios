package org.kimios.tests.webservices;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.osgi.metadata.OSGiManifestBuilder;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kimios.kernel.ws.pojo.SecurityEntity;
import org.kimios.kernel.ws.pojo.User;
import org.kimios.tests.OsgiKimiosService;
import org.kimios.tests.deployments.OsgiDeployment;
import org.osgi.framework.BundleContext;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class SearchEntitiesTest extends WebServicesTestAbstract {

    @ArquillianResource
    BundleContext context;

    private static String ADMIN_LOGIN = "admin";
    private static String ADMIN_PWD= "kimios";
    private static String ADMIN_USER_SOURCE = "kimios";

    @Deployment(name = "karaf")
    public static JavaArchive createDeployment() {
        String jarName = "SearchEntitiesTest.jar";
        ArrayList<String> additionalImportPackages = new ArrayList<>();
        additionalImportPackages.add("org.kimios.webservices");
        return OsgiDeployment.createArchive(jarName, additionalImportPackages, SearchEntitiesTest.class);
    }

    @Before
    public void setUp() {
        this.init();
//        ServiceReference<SecurityService> sref = context.getServiceReference(SecurityService.class);
//        this.securityService = context.getService(sref);
//
//        ServiceReference<ISecurityController> srefAdminController = context.getServiceReference(ISecurityController.class);
//        this.securityController = context.getService(srefAdminController);

        this.adminSession = this.securityController.startSession("admin", "kimios", "kimios");
    }

    @Test
    public void testSearchEntities(){
        assertNotNull(this.securityService);

        SecurityEntity[] secEntities = null;
        try {
            secEntities = this.securityService.searchSecurityEntities(this.adminSession.getUid(), "admin", "", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(secEntities);
        assertEquals(1, secEntities.length);
    }

    @Test
    public void testGetUser() {
        User userGot = null;
        try {
            userGot = this.securityService.getUser(this.adminSession.getUid());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(userGot);
    }

    @Test
    public void testGetUsers() {
        User[] usersGot = null;
        try {
            usersGot = this.securityService.getUsers(this.adminSession.getUid(), "kimios");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(usersGot);
    }
}