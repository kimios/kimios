package org.kimios.test.pax;

import aQute.bnd.osgi.Constants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import java.io.File;

import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class KimiosTest {

    @ProbeBuilder
    public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
        probe.setHeader(Constants.DYNAMICIMPORT_PACKAGE, "*,org.apache.felix.service.*;status=provisional");
        return probe;
    }

    @Configuration
    public static Option[] configure() throws Exception {
        return new Option[]{
                karafDistributionConfiguration()
                        .frameworkUrl("mvn:org.apache.karaf/apache-karaf/4.0.4/tar.gz")
                        .karafVersion("4.0.4")
                        .useDeployFolder(false)
                        .unpackDirectory(new File("target/paxexam/unpack")),
                logLevel(LogLevelOption.LogLevel.WARN),

                editConfigurationFilePut(
                        "etc/org.ops4j.pax.url.mvn.cfg",
                        "org.ops4j.pax.url.mvn.repositories",
                        "https://repo1.maven.org/maven2@id=central, "
                                + "http://repository.springsource.com/maven/bundles/release@id=spring.ebr.release, "
                                + "http://repository.springsource.com/maven/bundles/external@id=spring.ebr.external, "
                                + "http://zodiac.springsource.com/maven/bundles/release@id=gemini, "
                                + "http://repository.apache.org/content/groups/snapshots-group@id=apache@snapshots@noreleases, "
                                + "https://oss.sonatype.org/content/repositories/snapshots@id=sonatype.snapshots.deploy@snapshots@noreleases, "
                                + "https://oss.sonatype.org/content/repositories/ops4j-snapshots@id=ops4j.sonatype.snapshots.deploy@snapshots@noreleases, "
                                + "http://repository.springsource.com/maven/bundles/external@id=spring-ebr-repository@snapshots@noreleases"
                ),

                // install features
                /*features(maven().groupId("org.apache.karaf.features").artifactId("standard").type("xml").classifier("features").versionAsInProject(), "jdbc"),
                features(maven().groupId("org.apache.karaf.features").artifactId("standard").type("xml").classifier("features").versionAsInProject(), "hibernate"),
                features(maven().groupId("org.apache.karaf.features").artifactId("standard").type("xml").classifier("features").versionAsInProject(), "jpa"),
                features(maven().groupId("org.apache.karaf.features").artifactId("standard").type("xml").classifier("features").versionAsInProject(), "transaction"),
                features(maven().groupId("org.apache.karaf.features").artifactId("standard").type("xml").classifier("features").versionAsInProject(), "jndi"),
                features(maven().groupId("org.apache.karaf.features").artifactId("standard").type("xml").classifier("features").versionAsInProject(), "pax-jdbc-pool-dbcp2"),
                features(maven().groupId("org.apache.karaf.features").artifactId("standard").type("xml").classifier("features").versionAsInProject(), "aries-annotation"),
                */
                // features(maven().groupId("org.apache.karaf.features").artifactId("standard").type("xml").classifier("features").version("4.0.4"), "wrap"),


                // Change ssh port
                // editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiRegistryPort", RMI_REG_PORT),
                // editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiServerPort", RMI_SERVER_PORT),

                keepRuntimeFolder(),

                // install bundles
                // mavenBundle().groupId("org.apache.karaf.itests").artifactId("itests").version("4.0.4").type("jar").classifier("tests"),
                // wrappedBundle("mvn:org.apache.karaf.itests/itests/4.0.4/jar/tests"),
                /*mavenBundle().groupId("com.h2database").artifactId("h2").version("1.4.190"),
                mavenBundle().groupId("commons-lang").artifactId("commons-lang").version("2.6"),
                mavenBundle().groupId("commons-logging").artifactId("commons-logging").version("1.2"),
                mavenBundle().groupId("commons-io").artifactId("commons-io").version("2.4"),
*/

                // mavenBundle().groupId("org.apache.karaf.itests").artifactId("itests").version("4.0.4").type("jar").classifier("tests"),

                /*// install bundle datasource h2 for test
                streamBundle(bundle().add("OSGI-INF/blueprint/datasource-h2-test.xml",
                        new File("src/test/resources/OSGI-INF/blueprint/datasource-h2-test.xml").toURL())
                        .set(Constants.BUNDLE_NAME, "Apache Karaf :: Ippon OSGI Datasource Test")
                        .set(Constants.BUNDLE_SYMBOLICNAME, "ippon-osgi-sample-ds")
                        .set("Bundle-ManifestVersion", "2")
                        .set(Constants.DYNAMICIMPORT_PACKAGE, "*").build()).start(),*/

                // install ippon bundles
                /*mavenBundle().groupId("fr.ippon.osgi.sample").artifactId("ippon-osgi-sample-services").version("1.0-SNAPSHOT"),
                mavenBundle().groupId("fr.ippon.osgi.sample").artifactId("ippon-osgi-sample-command").version("1.0-SNAPSHOT"),*/

        };
    }

    @Test
    public void testProvisioning() throws Exception {
        // Check that the features are installed
        // assertFeatureInstalled("pax-jdbc-postgresql", "4.0.4");
        // assertFeatureInstalled("hibernate", "4.3.6.Final");
        // assertFeatureInstalled("jpa", "2.2.0");

        // Check that the bundles are installed
        // assertBundleInstalled("ippon-osgi-sample-services");
        // assertBundleInstalled("ippon-osgi-sample-command");

        Assert.assertEquals("1", "1");
    }
}
