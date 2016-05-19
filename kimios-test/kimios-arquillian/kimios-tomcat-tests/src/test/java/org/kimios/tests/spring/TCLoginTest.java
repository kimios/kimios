package org.kimios.tests.spring;

import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.spring.integration.test.annotation.SpringConfiguration;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kimios.tests.deployments.Deployments;

/**
 * Created by farf on 23/04/16.
 */


@ArquillianSuiteDeployment
@RunWith(Arquillian.class)
@SpringConfiguration("spring/ctx-kimios.xml")
public class TCLoginTest extends UserLoginTest {

    @Deployment(name = "tomcat")
    @TargetsContainer("tomcat")
    public static WebArchive createDeployment() {
        String sourceArchive = "/kimios-server.war";
        String targetArchive = "kimios-11-test.war";
        return ShrinkWrap.create(ZipImporter.class,
                targetArchive)
                .importFrom(Deployments.class.getResourceAsStream(sourceArchive))
                .as(WebArchive.class)
                //Adding test classes
                .addPackage(TCLoginTest.class.getPackage())
                .addAsResource("spring/ctx-kimios.xml")
                .addAsResource("spring/ctx-kimios-test.xml")
                .addAsWebInfResource("spring/web.xml", "web.xml");
    }



    @Test
    @OperateOnDeployment(value = "tomcat")
    @Override
    public void loginTest() {
        super.loginTest();
    }


    @Test
//            (expected = DmsKernelException.class)
    @OperateOnDeployment(value = "tomcat")
    @Override
    public void badLoginTest() {
        super.badLoginTest();
    }

    @Test
//            (expected = DmsKernelException.class)
    @OperateOnDeployment(value = "tomcat")
    @Override
    public void getConnectedUsersTest() {
        super.getConnectedUsersTest();
    }
}
