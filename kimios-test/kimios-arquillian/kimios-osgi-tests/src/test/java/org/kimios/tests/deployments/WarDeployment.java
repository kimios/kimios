package org.kimios.tests.deployments;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.io.File;

/**
 * Created by farf on 19/03/16.
 */
public class WarDeployment {


    private static String sourceArchive;
    private static String targetArchive;

    public static WebArchive createDeployment(String sourceArchive, String targetArchive) {
        return ShrinkWrap.create(ZipImporter.class,
                targetArchive)
                .importFrom(new File(sourceArchive))
                .as(WebArchive.class)
                .addAsResource("spring/ctx-kimios.xml")
                .addAsResource("spring/ctx-kimios-test.xml")
                .addAsWebInfResource("spring/web.xml", "web.xml");
    }
}
