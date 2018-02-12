package org.kimios.tests.kernel;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kimios.client.controller.helpers.StringTools;
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.dms.model.Folder;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.user.model.User;
import org.kimios.tests.deployments.OsgiDeployment;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * Created by tom on 29/02/16.
 */
@RunWith(Arquillian.class)
public class UserDocumentSimpleTest extends KernelTestAbstract {


    private static Logger logger = LoggerFactory.getLogger(UserDocumentSimpleTest.class);

    @ArquillianResource
    BundleContext context;

    private static String FOLDER_TEST_1 = "FOLDER TEST 1";
    private Folder folderTest1;
    private User userTest1;
    private User userTest2;
    private User userTest3;
    private Session userTest1Session;
    private Session userTest2Session;
    private Session userTest3Session;
    private long userTest1FolderUid;

    @Deployment(name="karaf")
    public static JavaArchive createDeployment() {

        JavaArchive archive =
                OsgiDeployment.createArchive( "UserDocumentSimpleTest.jar", null, UserDocumentSimpleTest.class,
                StringTools.class
                );
        archive.addAsResource("tests/launch_kimios-tests_mvn_test.sh");
        archive.addAsResource("tests/testDoc.txt");
        File exportedFile = new File("UserDocumentSimpleTest.jar");
        archive.as(ZipExporter.class).exportTo(exportedFile, true);
        return archive;
    }

    @Before
    public void setUp() {

        this.init();

        this.setAdminSession(this.securityController.startSession(ADMIN_LOGIN, USER_TEST_SOURCE, ADMIN_PWD));

        try {
            this.workspaceTest = this.workspaceController.getWorkspace(this.getAdminSession(), WORKSPACE_TEST_NAME);
        } catch (Exception e) {
            this.workspaceController.createWorkspace(this.getAdminSession(), WORKSPACE_TEST_NAME);
            this.workspaceTest = this.workspaceController.getWorkspace(this.getAdminSession(), WORKSPACE_TEST_NAME);
        }

        this.createTestUsers();
        // create folder in workspace
        long folderUid = this.folderController.createFolder(this.getAdminSession(), FOLDER_TEST_1, this.workspaceTest.getUid(), false);
        this.folderTest1 = this.folderController.getFolder(this.getAdminSession(), folderUid);
        // give access to users
        this.userTest1 = this.administrationController.getUser(this.getAdminSession(), USER_TEST_1, USER_TEST_SOURCE);
        this.userTest2 = this.administrationController.getUser(this.getAdminSession(), USER_TEST_2, USER_TEST_SOURCE);
        this.userTest3 = this.administrationController.getUser(this.getAdminSession(), USER_TEST_3, USER_TEST_SOURCE);
        this.giveAccessToEntityForUser(this.getAdminSession(), this.folderTest1, this.userTest1, true, true, false);
        this.giveAccessToEntityForUser(this.getAdminSession(), this.folderTest1, this.userTest2, true, false, false);
        this.giveAccessToEntityForUser(this.getAdminSession(), this.folderTest1, this.userTest3, true, false, false);

        // init test users' sessions
        this.userTest1Session = this.securityController.startSession(USER_TEST_1, USER_TEST_SOURCE, "test");
        this.userTest2Session = this.securityController.startSession(USER_TEST_2, USER_TEST_SOURCE, "test");
        this.userTest3Session = this.securityController.startSession(USER_TEST_3, USER_TEST_SOURCE, "test");

        // user 1 creates a subfolder
        this.userTest1FolderUid = this.folderController.createFolder(this.userTest1Session, "User_1_Folder", this.folderTest1.getUid(), true);
    }


    @Test
    public void testDocumentSimple() {
        // test users accesses
        assertTrue(this.securityController.canRead(this.userTest1Session, this.folderTest1.getUid()));
        assertTrue(this.securityController.canRead(this.userTest2Session, this.folderTest1.getUid()));
        assertTrue(this.securityController.canRead(this.userTest3Session, this.folderTest1.getUid()));
        assertTrue(this.securityController.canWrite(this.userTest1Session, this.folderTest1.getUid()));
        assertFalse(this.securityController.canWrite(this.userTest2Session, this.folderTest1.getUid()));
        assertFalse(this.securityController.canWrite(this.userTest3Session, this.folderTest1.getUid()));

        Folder userTest1Folder = this.folderController.getFolder(this.userTest1Session, userTest1FolderUid);

        assertTrue(this.securityController.canRead(this.userTest1Session, userTest1FolderUid));
        assertTrue(this.securityController.canRead(this.userTest2Session, userTest1FolderUid));
        assertTrue(this.securityController.canRead(this.userTest3Session, userTest1FolderUid));
        assertTrue(this.securityController.canWrite(this.userTest1Session, userTest1FolderUid));
        assertFalse(this.securityController.canWrite(this.userTest2Session, userTest1FolderUid));
        assertFalse(this.securityController.canWrite(this.userTest3Session, userTest1FolderUid));

        // user 1 import a document in the DMS
        long docUid = -1;
        try {
            InputStream docStream = this.getClass().getClassLoader().getResourceAsStream("tests/launch_kimios-tests_mvn_test.sh");
//            InputStream docStream = new FileInputStream(new File("/home/tom/IdeaProjects/kimios-tests/launch_kimios-tests_mvn_test.sh"));
            docUid = this.documentController.createDocumentWithProperties(
                    this.userTest1Session,
                    "User Test 1 doc 1",
                    "log",
                    "text/x-log",

                    userTest1Folder.getUid(),
                    true,
                    "<security-rules dmEntityId=\"-1\" dmEntityTye=\"3\"></security-rules>",
                    false,
                    -1,
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?><document-meta></document-meta>",
                    docStream,
                    "",
                    ""
            );
        } catch (Exception e) {
            System.out.println("Exception of type " + e.getClass().getName());
            System.out.println("cause : " + e.getCause());
            System.out.println("Message : " + e.getMessage());
            logger.error("error while document creation", e);
        }
        logger.info("Created document Id {}", docUid);
        assertFalse(docUid == -1);
        this.documentController.checkinDocument(this.userTest1Session, docUid);
        Document userDoc1 = this.documentController.getDocument(this.userTest1Session, docUid);
        assertNotNull(userDoc1);
        assertEquals("User Test 1 doc 1", userDoc1.getName());
        assertEquals(userTest1Folder.getUid(), userDoc1.getFolderUid());

        assertTrue(this.securityController.canRead(this.userTest1Session, userDoc1.getUid()));
        assertTrue(this.securityController.canRead(this.userTest2Session, userDoc1.getUid()));
        assertTrue(this.securityController.canRead(this.userTest3Session, userDoc1.getUid()));
        assertTrue(this.securityController.canWrite(this.userTest1Session, userDoc1.getUid()));
        assertFalse(this.securityController.canWrite(this.userTest2Session, userDoc1.getUid()));
        assertFalse(this.securityController.canWrite(this.userTest3Session, userDoc1.getUid()));


        // user 1 import a document in the DMS
        docUid = -1;
        try {
            InputStream docStream = this.getClass().getClassLoader().getResourceAsStream("tests/testDoc.txt");
            docUid = this.documentController.createDocumentWithProperties(
                    this.userTest1Session,
                    "User Test 1 doc 2",
                    "pom",
                    "text/xml",
                    userTest1Folder.getUid(),
                    true,
                    "<security-rules dmEntityId=\"-1\" dmEntityTye=\"3\"></security-rules>",
                    false,
                    -1,
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?><document-meta></document-meta>",
                    docStream,
                    "",
                    ""
            );
        } catch (Exception e) {
            System.out.println("Exception of type " + e.getClass().getName());
            System.out.println("cause : " + e.getCause());
            System.out.println("Message : " + e.getMessage());
        }
        assertFalse(docUid == -1);
        this.documentController.checkinDocument(this.userTest1Session, docUid);
        Document userDoc2 = this.documentController.getDocument(this.userTest1Session, docUid);
        assertNotNull(userDoc1);
        assertEquals("User Test 1 doc 1", userDoc1.getName());
        assertEquals(userTest1Folder.getUid(), userDoc1.getFolderUid());

        assertTrue(this.securityController.canRead(this.userTest1Session, userDoc1.getUid()));
        assertTrue(this.securityController.canRead(this.userTest2Session, userDoc1.getUid()));
        assertTrue(this.securityController.canRead(this.userTest3Session, userDoc1.getUid()));
        assertTrue(this.securityController.canWrite(this.userTest1Session, userDoc1.getUid()));
        assertFalse(this.securityController.canWrite(this.userTest2Session, userDoc1.getUid()));
        assertFalse(this.securityController.canWrite(this.userTest3Session, userDoc1.getUid()));

    }

//    @Test
//    public void testDocumentSimple2() {
//        Folder userTest1Folder = this.folderController.getFolder(this.userTest1Session, this.userTest1FolderUid);
//
//        // user 1 import an other document in the DMS
//        // this one with parameter 'isSecurityInherited' at false
//        long docUid2 = -1;
//        try {
//            InputStream docStream = new FileInputStream(new File("/home/tom/IdeaProjects/kimios-arquillian-tests/launch_kimios-tests_mvn_test.sh"));
//            long docUidTest = this.documentController.createDocumentWithProperties(
//                    this.userTest1Session,
//                    "User Test 1 doc 2",
//                    "sh",
//                    "application/x-shellscript",
//                    userTest1Folder.getUid(),
//                    false,
//                    "<security-rules dmEntityId=\"-1\" dmEntityTye=\"3\"></security-rules>",
//                    false,
//                    -1,
//                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?><document-meta></document-meta>",
//                    docStream,
//                    "",
//                    ""
//            );
//            System.out.println("This is returned docUid : " + docUidTest);
//        } catch (Exception e) {
//            System.out.println("Exception of type " + e.getClass().getName());
//            System.out.println("cause : " + e.getCause());
//            System.out.println("Message : " + e.getMessage());
//        }
//        assertFalse(docUid2 == -1);
//        this.documentController.checkinDocument(this.userTest1Session, docUid2);
//        Document userDoc2 = this.documentController.getDocument(this.userTest1Session, docUid2);
//        assertNotNull(userDoc2);
//        assertEquals("User Test 1 doc 2", userDoc2.getName());
//        assertEquals(userTest1Folder.getUid(), userDoc2.getFolderUid());
//
//        assertTrue(this.securityController.canRead(this.userTest1Session, userDoc2.getUid()));
//        assertFalse(this.securityController.canRead(this.userTest2Session, userDoc2.getUid()));
//        assertFalse(this.securityController.canRead(this.userTest3Session, userDoc2.getUid()));
//        assertTrue(this.securityController.canWrite(this.userTest1Session, userDoc2.getUid()));
//        assertFalse(this.securityController.canWrite(this.userTest2Session, userDoc2.getUid()));
//        assertFalse(this.securityController.canWrite(this.userTest3Session, userDoc2.getUid()));
//
//    }

    @After
    public void tearDown() {
        // init test users' sessions
        if (this.userTest1Session != null) {
            this.securityController.endSession(this.userTest1Session.getUid());
        }
        if (this.userTest2Session != null) {
            this.securityController.endSession(this.userTest2Session.getUid());
        }
        if (this.userTest3Session != null) {
            this.securityController.endSession(this.userTest3Session.getUid());
        }

        if (this.folderTest1 != null) {
            this.folderController.deleteFolder(this.getAdminSession(), this.folderTest1.getUid());
        }
        this.deleteTestUsers();
    }
}
