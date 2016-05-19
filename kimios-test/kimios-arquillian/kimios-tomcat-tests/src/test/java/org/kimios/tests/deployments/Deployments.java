package org.kimios.tests.deployments;

import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.kimios.tests.spring.TCLoginTest;

/**
 * Created by farf on 19/03/16.
 */




public class Deployments {

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

}
