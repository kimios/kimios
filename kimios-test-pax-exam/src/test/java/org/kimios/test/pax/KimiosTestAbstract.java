package org.kimios.test.pax;

import aQute.bnd.osgi.Constants;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.kimios.exceptions.NamingException;
import org.kimios.kernel.controller.*;
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.dms.model.DocumentVersion;
import org.kimios.kernel.security.model.DMEntitySecurity;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.share.controller.IShareController;
import org.kimios.kernel.share.model.Share;
import org.kimios.kernel.user.model.AuthenticationSource;
import org.kimios.kernel.user.model.User;
import org.kimios.utils.media.controller.IMediaUtilsController;
import org.kimios.webservices.DocumentService;
import org.kimios.webservices.exceptions.DMServiceException;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import javax.inject.Inject;
import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class KimiosTestAbstract {

    @Inject
    protected ISecurityController securityController;

    @Inject
    protected IAdministrationController administrationController;

    @Inject
    protected IWorkspaceController workspaceController;

    @Inject
    protected IFolderController folderController;

    @Inject
    protected IDocumentController documentController;

    @Inject
    protected IDocumentVersionController documentVersionController;

    @Inject
    protected DocumentService documentService;

    @Inject
    protected IMediaUtilsController mediaUtilsController;

    @Inject
    protected IShareController shareController;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @ProbeBuilder
    public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
        probe.setHeader(Constants.DYNAMICIMPORT_PACKAGE, "*,org.apache.felix.service.*;status=provisional");
        return probe;
    }

    static String[] documentSampleTab = { "sample1.pdf", "sample2.png" };
    static Map<String, Long> documentSampleMap = new HashMap<>();
    static String documentSampleResourceDir = "documents/";
    static String documentSampleTmpDir = "/tmp/";

    @Configuration
    public static Option[] configure() throws Exception {
        File serverAppCfg = resourceToFile(
                "org.kimios.server.app.cfg", "/tmp/kimios_1" + new Date().toString());
        File paxLoggingCfg = resourceToFile(
                "org.ops4j.pax.logging.cfg", "/tmp/kimios_2" + new Date().toString());

        return new Option[]{
                karafDistributionConfiguration()
                        .frameworkUrl("mvn:org.kimios/kimios-karaf-distribution/1.3-SNAPSHOT/tar.gz")
                        .karafVersion("4.0.4")
                        .useDeployFolder(false)
                        .unpackDirectory(new File("target/paxexam/unpack")),
                debugConfiguration("5005", true),
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
                replaceConfigurationFile(
                        "etc/org.kimios.server.app.cfg",
                        serverAppCfg
                ),
                replaceConfigurationFile(
                        "etc/org.ops4j.pax.logging.cfg",
                        paxLoggingCfg
                ),
                /*editConfigurationFilePut(
                        "etc/org.ops4j.pax.logging.cfg",
                        "log4j.logger.org.hibernate.SQL",
                        "debug"
                ),
                editConfigurationFilePut(
                        "etc/org.ops4j.pax.logging.cfg",
                        "log4j.logger.org.hibernate.type",
                        "trace"
                ),
                editConfigurationFilePut(
                        "etc/org.ops4j.pax.logging.cfg",
                        "log4j.logger.org.hibernate.type.descriptor.sql",
                        "trace"
                ),*/
                /*editConfigurationFilePut(
                        "etc/org.kimios.server.app.cfg",
                        "jdbc.url",
                        "jdbc:postgresql://172.92.0.1:5436/kimios_solr"
                ),
                editConfigurationFilePut(
                        "etc/org.kimios.server.app.cfg",
                        "jdbc.databasetype",
                        "postgresql"
                ),
                editConfigurationFilePut(
                        "etc/org.kimios.server.app.cfg",
                        "jdbc.driver",
                        "org.postgresql.Driver"
                ),
                editConfigurationFilePut(
                        "etc/org.kimios.server.app.cfg",
                        "jdbc.user",
                        "kimios"
                ),
                editConfigurationFilePut(
                        "etc/org.kimios.server.app.cfg",
                        "jdbc.password",
                        "kimios"
                ),
                editConfigurationFilePut(
                        "etc/org.kimios.server.app.cfg",
                        "jdbc.dialect",
                        "org.hibernate.dialect.PostgreSQLDialect"
                ),
                editConfigurationFilePut(
                        "etc/org.kimios.server.app.cfg",
                        "dms.repository.default.path",
                        "/home/tom/dev/kimios/docker/data-1.3-SNAPSHOT-solr/repository"
                ),
                editConfigurationFilePut(
                        "etc/org.kimios.server.app.cfg",
                        "dms.repository.tmp.path",
                        "/home/tom/dev/kimios/docker/data-1.3-SNAPSHOT-solr/tmp"
                ),
                editConfigurationFilePut(
                        "etc/org.kimios.server.app.cfg",
                        "dms.index.solr.home",
                        "/home/tom/dev/kimios/docker/data-1.3-SNAPSHOT-solr/index"
                ),
                editConfigurationFilePut(
                        "etc/org.kimios.server.app.cfg",
                        "etherpad.url",
                        "http://localhost:9001"
                ),
                editConfigurationFilePut(
                        "etc/org.kimios.server.app.cfg",
                        "dms.session.timeout",
                        "60"
                ),
                editConfigurationFilePut(
                        "etc/org.kimios.server.app.cfg",
                        "dms.mail.port",
                        "25"
                ),
                editConfigurationFilePut(
                        "etc/org.kimios.server.app.cfg",
                        "dms.index.solr.mode.server",
                "false"
                ),
                editConfigurationFilePut(
                        "etc/org.kimios.server.app.cfg",
                        "dms.mail.tlsauth",
                        "false"
                ),
                editConfigurationFilePut(
                        "etc/org.kimios.server.app.cfg",
                        "dms.mail.ssl",
                        "false"
                ),
                editConfigurationFilePut(
                        "etc/org.kimios.server.app.cfg",
                        "dms.mail.debug",
                        "true"
                ),
                editConfigurationFilePut(
                        "etc/org.kimios.server.app.cfg",
                        "dms.server.name",
                        "kimios-Server"
                ),
*/
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



    private static File resourceToFile(String resourcePath, String filePath) throws IOException {
        ClassLoader classloader = KimiosTestAbstract.class.getClassLoader();
        InputStream initialStream = classloader.getResourceAsStream(resourcePath);
        if (initialStream == null) {
            throw new IOException("resource not found <" + resourcePath + ">");
        }
        File targetFile = new File(filePath);
        OutputStream outStream = new FileOutputStream(targetFile);

        byte[] buffer = new byte[8 * 1024];
        int bytesRead;
        while ((bytesRead = initialStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        initialStream.close();
        outStream.close();

        return targetFile;
    }

    @Test
    public void testACL() {
        ACLTestUtils aclTestUtils = new ACLTestUtils(
                this.securityController,
                this.administrationController,
                this.workspaceController,
                this.folderController,
                this.documentController,
                this.documentVersionController
        );

        try {
            aclTestUtils.setUp();
        } catch (Exception e) {
            System.out.println("********************************");
            System.out.println(e.getMessage());
            e.printStackTrace();
            Assert.fail();
        }

        AccessRight fffAccessRight = new AccessRight(false, false, false);
        AccessRight tffAccessRight = new AccessRight(true, false, false);
        AccessRight ttfAccessRight = new AccessRight(true, true, false);
        AccessRight tttAccessRight = new AccessRight(true, true, true);

        User user1 = this.administrationController.getUser(aclTestUtils.getSession(), aclTestUtils.getArray()[0][0], "kimios");
        Session user1Session = this.securityController.startSession(user1.getUid(), "kimios");

        AccessRight user1AccessRight = new AccessRight(
                this.securityController.canRead(user1Session, aclTestUtils.getWorkspace().getUid()),
                this.securityController.canWrite(user1Session, aclTestUtils.getWorkspace().getUid()),
                this.securityController.hasFullAccess(user1Session, aclTestUtils.getWorkspace().getUid())
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
        this.securityController.updateDMEntitySecurities(
                aclTestUtils.getSession(),
                aclTestUtils.getWorkspace().getUid(),
                securityList,
                false,
                true
        );
        aclTestUtils.updateAccessRight(user1AccessRight, user1Session, aclTestUtils.getWorkspace().getUid());
        Assert.assertEquals(user1AccessRight, tffAccessRight);

        User user2 = this.administrationController.getUser(aclTestUtils.getSession(), aclTestUtils.getArray()[1][0], "kimios");
        Session user2Session = this.securityController.startSession(user2.getUid(), "kimios");
        AccessRight user2AccessRight = new AccessRight();
        aclTestUtils.updateAccessRight(user2AccessRight, user2Session, aclTestUtils.getWorkspace().getUid());
        Assert.assertEquals(user2AccessRight, fffAccessRight);

        User user3 = this.administrationController.getUser(aclTestUtils.getSession(), aclTestUtils.getArray()[2][0], "kimios");
        Session user3Session = this.securityController.startSession(user3.getUid(), "kimios");
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
        this.securityController.updateDMEntitySecurities(
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
        this.securityController.updateDMEntitySecurities(
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
        this.securityController.updateDMEntitySecurities(
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
        this.securityController.updateDMEntitySecurities(
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
        this.securityController.updateDMEntitySecurities(
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

    @Test
    public void testUploadNewDocumentVersion() throws DMServiceException {
        Assert.assertNotNull(this.documentController);

        for(String documentSample: documentSampleTab) {
            try {

                resourceToFile(documentSampleResourceDir + documentSample,
                        documentSampleTmpDir + documentSample);
            } catch (IOException e) {
                Assert.fail("failed to copy resource to file : " + documentSampleResourceDir + documentSample + " to "
                        + documentSampleTmpDir + documentSample + " (" + e.getMessage() + ")");
            }
        }
        Session adminSession = this.securityController.startSession("admin", "kimios");
        InputStream inputStream = null;
        String documentSampleName = documentSampleTab[0];
        try {
            inputStream = new FileInputStream(documentSampleTmpDir + documentSampleName);
        } catch (FileNotFoundException e) {
            Assert.fail(e.getMessage());
        }
        long documentId = -1;
        try {
            documentId = this.documentController.createDocumentFromFullPathWithProperties(
                    adminSession,
                    "/" + ACLTestUtils.WORKSPACE_TEST + "/" + ACLTestUtils.FOLDERS_TEST[0] + "/" + documentSampleName,
                    false,
                    new ArrayList<>(),
                    false,
                    -1,
                    new ArrayList<>(),
                    inputStream,
                    null,
                    null
            );
        } catch(NamingException e) {
            System.out.println("document already exists");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertNotEquals(-1, documentId);
        documentSampleMap.put(documentSampleName, documentId);

        Document document = this.documentController.getDocument(adminSession, documentId);
        Assert.assertNotNull(document);
        List< DocumentVersion> documentVersionList = this.documentVersionController.getDocumentVersions(adminSession, documentId);
        Assert.assertEquals(1, documentVersionList.size());

        try {
            this.documentService.uploadNewDocumentVersion(
                    adminSession.getUid(),
                    documentId,
                    inputStream,
                    null,
                    null,
                    documentSampleTab[0],
                    false
            );
        } catch (DMServiceException e) {
            Assert.fail(e.toString());
        }
        documentVersionList = this.documentVersionController.getDocumentVersions(adminSession, documentId);
        Assert.assertEquals(2, documentVersionList.size());

        try {
            inputStream = new FileInputStream(documentSampleTmpDir + documentSampleTab[1]);
        } catch (FileNotFoundException e) {
            Assert.fail(e.getMessage());
        }
        exceptionRule.expect(DMServiceException.class);
        this.documentService.uploadNewDocumentVersion(
                adminSession.getUid(),
                documentId,
                inputStream,
                null,
                null,
                documentSampleTab[1],
                false
        );
        documentVersionList = this.documentVersionController.getDocumentVersions(adminSession, documentId);
        Assert.assertEquals(2, documentVersionList.size());

        try {
            this.documentService.uploadNewDocumentVersion(
                    adminSession.getUid(),
                    documentId,
                    inputStream,
                    null,
                    null,
                    documentSampleTab[1],
                    true
            );
        } catch (DMServiceException e) {
            Assert.fail(e.toString());
        }
        documentVersionList = this.documentVersionController.getDocumentVersions(adminSession, documentId);
        Assert.assertEquals(3, documentVersionList.size());
    }

    @Test
    public void testMediaUtils() {
        String documentSampleResourceDir = "documents/";
        String documentSampleTmpDir = "/tmp/" + new Date().getTime() + "/";
        File file = new File(documentSampleTmpDir);
        file.mkdir();
        String[] resources = { "sample1.pdf", "sample2.png", "sample1", "sample2" };
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put(resources[0], "application/pdf");
        typeMap.put(resources[1], "image/png");
        typeMap.put(resources[2], "application/pdf");
        typeMap.put(resources[3], "image/png");

        List<MediaUtilsTestResource> mediaUtilsTestResourceList = Arrays.asList(resources).stream().map(resourceName ->
                new MediaUtilsTestResource(
                        documentSampleResourceDir + resourceName,
                        documentSampleTmpDir + resourceName,
                        typeMap.get(resourceName),
                        resourceName
                )
        ).collect(Collectors.toList());

        mediaUtilsTestResourceList.forEach(mediaUtilsTestResource ->
                {
                    try {
                        resourceToFile(mediaUtilsTestResource.getResourcePath(), mediaUtilsTestResource.getFileTmpPath());
                    } catch (IOException e) {
                        Assert.fail(e.getClass().getName() + " " + e.getMessage());
                    }
                }
        );

        mediaUtilsTestResourceList.forEach(mediaUtilsTestResource -> {
            String result = null;
            try {
                result = mediaUtilsController.detectMimeType(mediaUtilsTestResource.getFileTmpPath(), mediaUtilsTestResource.getResourceName());
            } catch (IOException e) {
                Assert.fail(e.getClass().getName() + " " + e.getMessage());
            }
            Assert.assertEquals(mediaUtilsTestResource.getMediaType(), result);
        });


    }

    @Test
    public void testShare() {
        // write resources as files on filesystem
        for(String documentSample: documentSampleTab) {
            try {

                resourceToFile(documentSampleResourceDir + documentSample,
                        documentSampleTmpDir + documentSample);
            } catch (IOException e) {
                Assert.fail("failed to copy resource to file : " + documentSampleResourceDir + documentSample + " to "
                        + documentSampleTmpDir + documentSample + " (" + e.getMessage() + ")");
            }
        }

        Session adminSession = this.securityController.startSession("admin", "kimios");
        InputStream inputStream = null;
        String documentSampleName = documentSampleTab[0];
        try {
            inputStream = new FileInputStream(documentSampleTmpDir + documentSampleTab[0]);
        } catch (FileNotFoundException e) {
            Assert.fail(e.getMessage());
        }
        long documentId = -1;
        try {
            if (documentSampleMap.get(documentSampleName) == null
            || this.documentController.getDocument(adminSession, documentSampleMap.get(documentSampleName)) == null) {
                documentId = this.documentController.createDocumentFromFullPathWithProperties(
                        adminSession,
                        "/" + ACLTestUtils.WORKSPACE_TEST + "/" + ACLTestUtils.FOLDERS_TEST[1] + "/" + documentSampleTab[0],
                        false,
                        new ArrayList<>(),
                        false,
                        -1,
                        new ArrayList<>(),
                        inputStream,
                        null,
                        null
                );
            } else {
                documentId = documentSampleMap.get(documentSampleName);
            }
        } catch(NamingException e) {
            System.out.println("document already exists");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertNotEquals(-1, documentId);
        Share share = null;
        try {
            share = this.shareController.shareEntity(
                    adminSession,
                    documentId,
                    ACLTestUtils.array[0][0],
                    "kimios",
                    true,
                    false,
                    false,
                    Date.from(Instant.now().plus(Duration.ofDays(15))),
                    false
            );
        } catch (Exception e) {
            Assert.fail("exception when creating share with shareEntity()");
        }
        
        Assert.assertNotNull(share);
    }


    @AfterClass
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
    }
}
