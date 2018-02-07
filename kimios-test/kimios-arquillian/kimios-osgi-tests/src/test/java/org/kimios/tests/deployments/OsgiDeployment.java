package org.kimios.tests.deployments;

import org.jboss.osgi.metadata.OSGiManifestBuilder;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import java.io.File;
import java.io.InputStream;

/**
 * Created by farf on 19/03/16.
 */
public class OsgiDeployment {

    public static JavaArchive createArchive(String jarName, Class ... classes){
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, jarName);
        archive.addClasses(
                classes
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
                builder.addImportPackages(
                        "org.osgi.util.tracker",
                        "org.slf4j"
                );
                return builder.openStream();
            }
        });

        File exportedFile = new File(jarName);
        archive.as(ZipExporter.class).exportTo(exportedFile, true);

        return archive;
    }

}
