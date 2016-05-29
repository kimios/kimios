package org.kimios.tests.deployments;

import org.jboss.osgi.metadata.OSGiManifestBuilder;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.kimios.client.controller.helpers.StringTools;
import org.kimios.tests.OsgiKimiosService;
import org.kimios.tests.utils.dataset.Users;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created by farf on 19/03/16.
 */
public class OsgiDeployment {

    public static JavaArchive createArchive(String jarName, List<String> additionalDynamicImportPackages, Class... classes){
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, jarName);
        archive.addClasses(
                classes
        );
        archive.addClasses(
                OsgiKimiosService.class,
                Users.class,
                StringTools.class
        );
        archive.setManifest(new Asset() {
            public InputStream openStream() {
                OSGiManifestBuilder builder = OSGiManifestBuilder.newInstance();
                builder.addBundleSymbolicName(archive.getName());
                builder.addBundleManifestVersion(2);
                builder.addDynamicImportPackages(
                        "org.kimios.kernel.*",
                        "org.kimios.client.*",
                        "org.kimios.api.*",
                        "org.kimios.exceptions"
                );
                // additional packages
                if (additionalDynamicImportPackages != null) {
                    additionalDynamicImportPackages.stream().forEach(p -> builder.addDynamicImportPackages(p));
                }
                builder.addImportPackages(
                        "org.osgi.util.tracker",
                        "org.slf4j",
                        "org.junit.rules"
                );
                return builder.openStream();
            }
        });
        String[] resources = {
                "tests/launch_kimios-tests_mvn_test.sh",
                "tests/testDoc.txt",
                "tests/testDoc2.txt",
                "tests/testDoc3.txt"
        };
        Arrays.asList(resources).forEach(v -> archive.addAsResource(v));
        File exportedFile = new File(jarName);
        archive.as(ZipExporter.class).exportTo(exportedFile, true);


        return archive;
    }

}
