package org.kimios.tests.share;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kimios.exceptions.AccessDeniedException;
import org.kimios.kernel.controller.IAdministrationController;
import org.kimios.kernel.controller.IDocumentController;
import org.kimios.kernel.controller.IFolderController;
import org.kimios.kernel.controller.IWorkspaceController;
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.dms.model.Folder;
import org.kimios.kernel.dms.model.Workspace;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.share.controller.IMailShareController;
import org.kimios.kernel.share.controller.IShareController;
import org.kimios.kernel.share.controller.IShareTransferController;
import org.kimios.kernel.share.model.Share;
import org.kimios.tests.OsgiKimiosService;
import org.kimios.tests.TestAbstract;
import org.kimios.tests.deployments.OsgiDeployment;
import org.kimios.tests.utils.dataset.Users;
import org.kimios.webservices.DocumentService;
import org.kimios.webservices.ExtensionService;
import org.kimios.webservices.exceptions.DMServiceException;
import org.osgi.framework.BundleContext;

import java.io.InputStream;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ShareTest extends TestAbstract {

    @ArquillianResource
    BundleContext context;

    @OsgiKimiosService
    protected IShareController shareController;
    @OsgiKimiosService
    protected IMailShareController mailShareController;
    @OsgiKimiosService
    protected IShareTransferController shareTransferController;
    @OsgiKimiosService
    protected IAdministrationController administrationController;
    @OsgiKimiosService
    protected IWorkspaceController workspaceController;
    @OsgiKimiosService
    protected IDocumentController documentController;
    @OsgiKimiosService
    protected IFolderController folderController;
    @OsgiKimiosService
    protected DocumentService documentService;
    @OsgiKimiosService
    protected ExtensionService extensionService;

    private final String workspaceTestName = "workspaceShareTest";
    private Workspace workspaceTest;
    private final String folderTestName = "folderShareTest";
    private Folder folderTest;
    private Share shareTest;
    private List<Share> shares = new ArrayList<>();
    private Map<String, ArrayList<Long>> sharedDocuments = new HashMap<>();

    private HashMap<String, Session> sessions = new HashMap<>();

    public IShareController getShareController() {
        return shareController;
    }

    public void setShareController(IShareController shareController) {
        this.shareController = shareController;
    }

    public IMailShareController getMailShareController() {
        return mailShareController;
    }

    public void setMailShareController(IMailShareController mailShareController) {
        this.mailShareController = mailShareController;
    }

    public IShareTransferController getShareTransferController() {
        return shareTransferController;
    }

    public void setShareTransferController(IShareTransferController shareTransferController) {
        this.shareTransferController = shareTransferController;
    }

    public IAdministrationController getAdministrationController() {
        return administrationController;
    }

    public void setAdministrationController(IAdministrationController administrationController) {
        this.administrationController = administrationController;
    }

    public IWorkspaceController getWorkspaceController() {
        return workspaceController;
    }

    public void setWorkspaceController(IWorkspaceController workspaceController) {
        this.workspaceController = workspaceController;
    }

    public IDocumentController getDocumentController() {
        return documentController;
    }

    public void setDocumentController(IDocumentController documentController) {
        this.documentController = documentController;
    }

    public IFolderController getFolderController() {
        return folderController;
    }

    public void setFolderController(IFolderController folderController) {
        this.folderController = folderController;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public ExtensionService getExtensionService() {
        return extensionService;
    }

    public void setExtensionService(ExtensionService extensionService) {
        this.extensionService = extensionService;
    }

    @Deployment(name="karaf")
    public static JavaArchive createDeployment() {
        String jarName = ShareTest.class.getSimpleName() + ".jar";
        List<String> additionalPackages = new ArrayList<>();
        additionalPackages.add("org.kimios.webservices");
        additionalPackages.add("org.kimios.webservices.exceptions");
        return OsgiDeployment.createArchive(
                jarName,
                additionalPackages,
                ShareTest.class
        );
    }

    @Before
    public void setUp() {
        this.initServices();

        this.setAdminSession(this.getSecurityController().startSession(ADMIN_LOGIN, ADMIN_SOURCE, ADMIN_PWD));

        Users.createTestUsers(this.getAdministrationController(), this.getAdminSession());

        try {
            this.workspaceController.createWorkspace(this.getAdminSession(), this.workspaceTestName);
        } catch (Exception e) {
        }
        this.workspaceTest = this.workspaceController.getWorkspace(this.getAdminSession(), this.workspaceTestName);
        // create folder in workspace
        long folderUid = -1;
        try {
            folderUid = this.folderController.createFolder(
                    this.getAdminSession(),
                    this.folderTestName,
                    this.workspaceTest.getUid(),
                    false
            );
        } catch (Exception e) {

        }
        if (folderUid == -1) {
            this.folderTest = this.getFolderController().getFolder(
                    this.getAdminSession(),
                    this.folderTestName,
                    this.workspaceTest.getUid(),
                    this.workspaceTest.getType()
            );
        } else {
            this.folderTest = this.folderController.getFolder(this.getAdminSession(), folderUid);
        }
        if (this.folderTest == null) {
            fail(this.folderTestName + " folder does not exist");
        }
        String[] usersTest = {
                Users.USER_TEST_1,
                Users.USER_TEST_2,
                Users.USER_TEST_3
        };

        Users.giveAccessToEntityForUser(
                this.getAdminSession(),
                this.getSecurityController(),
                this.folderTest,
                this.getAdministrationController().getUser(this.getAdminSession(), Users.USER_TEST_1, Users.USER_TEST_SOURCE),
                true,
                true,
                false
        );

        // start connections
        Arrays.asList(usersTest).forEach(uid ->
                sessions.put(uid, this.getSecurityController().startSession(uid, Users.USER_TEST_SOURCE))
        );

        // user test 1 creates a document
        long shareTestDoc = -1;
        String docName = "Test doc created by user" + Users.USER_TEST_1;
        try {
            InputStream docStream = this.getClass().getClassLoader().getResourceAsStream("tests/testDoc2.txt");
            shareTestDoc = this.documentController.createDocumentWithProperties(
                    sessions.get(Users.USER_TEST_1),
                    docName,
                    "txt",
                    "text/plain",
                    this.folderTest.getUid(),
                    false,
                    "<security-rules dmEntityId=\"-1\" dmEntityTye=\"3\"></security-rules>",
                    false,
                    -1,
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?><document-meta></document-meta>",
                    docStream,
                    "",
                    ""
            );
        } catch (Exception e) {
            System.out.println("Exception of type : " + e.getClass().getName());
            System.out.println("Message : " + e.getMessage());
            System.out.println("Cause : " + e.getCause());
            fail("Test can be done. User 1 can't create a document.");
        }
        ArrayList<Long> ids = new ArrayList<>();
        ids.add(shareTestDoc);
        this.sharedDocuments.put(Users.USER_TEST_1, ids);
    }

    @Test
    public void testSimpleShare() {
        assertNotNull(this.getShareController());

        // user 1 creates one document
        Session user1Session = sessions.get(Users.USER_TEST_1);
        String path = this.workspaceTest.getPath() + "/" + "shareTestDoc1";
        long shareTestDoc1Uid = -1;
        String docName = "Test doc 2";
        try {
            InputStream docStream = this.getClass().getClassLoader().getResourceAsStream("tests/testDoc2.txt");
            shareTestDoc1Uid = this.documentController.createDocumentWithProperties(
                    sessions.get(Users.USER_TEST_1),
                    docName,
                    "txt",
                    "text/plain",
                    this.folderTest.getUid(),
                    false,
                    "<security-rules dmEntityId=\"-1\" dmEntityTye=\"3\"></security-rules>",
                    false,
                    -1,
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?><document-meta></document-meta>",
                    docStream,
                    "",
                    ""
            );
        } catch (Exception e) {
            System.out.println("Exception of type : " + e.getClass().getName());
            System.out.println("Message : " + e.getMessage());
            System.out.println("Cause : " + e.getCause());

            shareTestDoc1Uid = this.documentController.getDocument(this.getAdminSession(), docName,
                    "txt", this.folderTest.getUid()).getUid();
        }
        Document doc = this.getDocumentController().getDocument(sessions.get(Users.USER_TEST_1), shareTestDoc1Uid);
        assertNotNull(doc);
        // User test 2 cannot read that doc
        assertFalse("User test 2 cannot read that doc", this.getSecurityController().canRead(sessions.get(Users.USER_TEST_2), shareTestDoc1Uid));
        // User test 3 too cannot read that doc
        assertFalse("User test 3 cannot read that doc", this.getSecurityController().canRead(sessions.get(Users.USER_TEST_3), shareTestDoc1Uid));

        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.DATE, 7);
        Date date = cal.getTime();
        try {
            shareTest = this.getShareController().shareEntity(user1Session, shareTestDoc1Uid, Users.USER_TEST_2, Users.USER_TEST_SOURCE,
                    true, false, false, date, false);
        } catch (Exception e) {
            System.out.println("Exception of type : " + e.getClass().getName());
            System.out.println("Message : " + e.getMessage());
            System.out.println("Cause : " + e.getCause());
            fail("Exception while sharing entity");
        }
        List<Share> entitiesSharedByMe = null;
        try {
            entitiesSharedByMe = this.getShareController().listEntitiesSharedByMe(this.sessions.get(Users.USER_TEST_1));
        } catch (Exception e) {
            System.out.println("Exception while retrieving entities shared by me (" + Users.USER_TEST_1 + ")");
        }
        if (entitiesSharedByMe == null) {
            fail("entitiesSharedByMe should not be null");
        }
        assertEquals(1, entitiesSharedByMe.size());

        assertTrue(this.getSecurityController().canRead(this.sessions.get(Users.USER_TEST_2), shareTestDoc1Uid));

        try {
            this.getShareController().removeShare(user1Session, shareTest.getId());
        } catch (Exception e) {
            System.out.println("Share cannot be removed");
        }
        try {
            entitiesSharedByMe = this.getShareController().listEntitiesSharedByMe(this.sessions.get(Users.USER_TEST_1));
        } catch (Exception e) {
            System.out.println("Exception while retrieving entities shared by me (" + Users.USER_TEST_1 + ")");
        }
        if (entitiesSharedByMe == null) {
            fail("entitiesSharedByMe should not be null");
        }
        assertEquals(0, entitiesSharedByMe.size());
        assertFalse(this.getSecurityController().canRead(this.sessions.get(Users.USER_TEST_2), shareTestDoc1Uid));

        try {
            // users have not shared anything yet
            assertEquals(0, this.getShareController().listEntitiesSharedByMe(this.sessions.get(Users.USER_TEST_1)).size());
            assertEquals(0, this.getShareController().listEntitiesSharedByMe(this.sessions.get(Users.USER_TEST_2)).size());
            assertEquals(0, this.getShareController().listEntitiesSharedByMe(this.sessions.get(Users.USER_TEST_3)).size());

            assertEquals(0, this.getShareController().listEntitiesSharedWithMe(this.sessions.get(Users.USER_TEST_1)).size());
            assertEquals(0, this.getShareController().listEntitiesSharedWithMe(this.sessions.get(Users.USER_TEST_2)).size());
            assertEquals(0, this.getShareController().listEntitiesSharedWithMe(this.sessions.get(Users.USER_TEST_3)).size());

            Share share = this.getShareController().shareEntity(this.sessions.get(Users.USER_TEST_1), shareTestDoc1Uid, Users.USER_TEST_2, Users.USER_TEST_SOURCE,
                    true, false, false, date, false);
            this.shares.add(share);
            assertTrue(this.getSecurityController().canRead(this.sessions.get(Users.USER_TEST_2), shareTestDoc1Uid));
            assertFalse(this.getSecurityController().canRead(this.sessions.get(Users.USER_TEST_3), shareTestDoc1Uid));

            this.shareController.disableShare(this.sessions.get(Users.USER_TEST_1), shareTest.getId());
            assertFalse(this.getSecurityController().canRead(this.sessions.get(Users.USER_TEST_2), shareTestDoc1Uid));
            assertFalse(this.getSecurityController().canRead(this.sessions.get(Users.USER_TEST_3), shareTestDoc1Uid));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Test
    public void testDeleteSharedDocument() {
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.DATE, 7);
        Date date = cal.getTime();
        Share share;
        try {
            share = this.getShareController().shareEntity(
                    sessions.get(Users.USER_TEST_1),
                    this.sharedDocuments.get(Users.USER_TEST_1).get(0),
                    Users.USER_TEST_3, Users.USER_TEST_SOURCE,
                    true, false, false, date, false
            );
            this.shares.add(share);

            GregorianCalendar.getInstance();
            cal.add(Calendar.DATE, -10);
            date = cal.getTime();
            share = this.getShareController().shareEntity(
                    sessions.get(Users.USER_TEST_1),
                    this.sharedDocuments.get(Users.USER_TEST_1).get(0),
                    Users.USER_TEST_3,
                    Users.USER_TEST_SOURCE,
                    true,
                    false,
                    false,
                    date,
                    false
            );
            this.shares.add(share);
        } catch (Exception e) {
            System.out.println("Exception of type : " + e.getClass().getName());
            System.out.println("Message : " + e.getMessage());
            System.out.println("Cause : " + e.getCause());
            fail("Exception while sharing entity");
        }
        try {
            Document document = this.documentController.getDocumentWithShares(
                    sessions.get(Users.USER_TEST_1),
                    this.sharedDocuments.get(Users.USER_TEST_1).get(0)
            );
            assertNotNull(document.getShareSet());
            assertTrue(document.getShareSet().size() == 1);
        } catch (Exception e) {

        }

        try {
            this.documentService.deleteDocument(sessions.get(Users.USER_TEST_1).getUid(), this.sharedDocuments.get(Users.USER_TEST_1).get(0), false);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof DMServiceException);
            assertTrue(((DMServiceException) e).getCode() == 17);
        }

        try {
            this.documentService.deleteDocument(sessions.get(Users.USER_TEST_1).getUid(), this.sharedDocuments.get(Users.USER_TEST_1).get(0), true);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof DMServiceException);
            assertTrue(((DMServiceException) e).getCode() == 18);
        }

        try {
            // should raise exception because document does not exist any more (just been deleted)
            Document document = this.documentController.getDocument(sessions.get(Users.USER_TEST_1), this.sharedDocuments.get(Users.USER_TEST_1).get(0));
            assertNull(document);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof AccessDeniedException);
        }

    }

    @Test
    public void testTrashSharedDocument() {
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.DATE, 7);
        Date date = cal.getTime();
        Share share;
        try {
            share = this.getShareController().shareEntity(
                    sessions.get(Users.USER_TEST_1),
                    this.sharedDocuments.get(Users.USER_TEST_1).get(0),
                    Users.USER_TEST_3, Users.USER_TEST_SOURCE,
                    true, false, false, date, false);
            this.shares.add(share);

            GregorianCalendar.getInstance();
            cal.add(Calendar.DATE, -10);
            date = cal.getTime();
            share = this.getShareController().shareEntity(
                    sessions.get(Users.USER_TEST_1),
                    this.sharedDocuments.get(Users.USER_TEST_1).get(0),
                    Users.USER_TEST_3,
                    Users.USER_TEST_SOURCE,
                    true,
                    false,
                    false,
                    date,
                    false
            );
            this.shares.add(share);

            GregorianCalendar.getInstance();
            cal.add(Calendar.DATE, +20);
            date = cal.getTime();
            share = this.getShareController().shareEntity(
                    sessions.get(Users.USER_TEST_1),
                    this.sharedDocuments.get(Users.USER_TEST_1).get(0),
                    Users.USER_TEST_3,
                    Users.USER_TEST_SOURCE,
                    true,
                    false,
                    false,
                    date,
                    false
            );
            this.shares.add(share);
        } catch (Exception e) {
            System.out.println("Exception of type : " + e.getClass().getName());
            System.out.println("Message : " + e.getMessage());
            System.out.println("Cause : " + e.getCause());
            fail("Exception while sharing entity");
        }
        try {
            Document document = this.documentController.getDocumentWithShares(
                    sessions.get(Users.USER_TEST_1),
                    this.sharedDocuments.get(Users.USER_TEST_1).get(0)
            );
            assertNotNull(document.getShareSet());
            assertTrue(document.getShareSet().size() == 2);
        } catch (Exception e) {

        }

        try {
            this.extensionService.trashEntityForce(sessions.get(Users.USER_TEST_1).getUid(), this.sharedDocuments.get(Users.USER_TEST_1).get(0), false);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof DMServiceException);
            assertTrue(((DMServiceException) e).getCode() == 17);
        }

        try {
            this.extensionService.trashEntityForce(sessions.get(Users.USER_TEST_1).getUid(), this.sharedDocuments.get(Users.USER_TEST_1).get(0), true);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof DMServiceException);
            assertTrue(((DMServiceException) e).getCode() == 18);
        }

        try {
            Document document = this.documentController.getDocument(sessions.get(Users.USER_TEST_1), this.sharedDocuments.get(Users.USER_TEST_1).get(0));
            assertTrue(document.getTrashed());
        } catch (Exception e) {
        }

    }

    @After
    public void tearDown() {
        // remove shares
        // end test users' sessions
        this.shares.forEach(s -> {
            try {
                this.shareController.removeShare(this.getAdminSession(), s.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        // delete documents
        this.sharedDocuments.forEach((k, v) -> {
            try {
                v.forEach(id -> {
                    this.documentController.deleteDocument(this.sessions.get(k), id, false);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        this.sessions.forEach((k, v) -> this.getSecurityController().endSession(v.getUid()));
        this.getFolderController().deleteFolder(this.getAdminSession(), this.folderTest.getUid());
        this.getWorkspaceController().deleteWorkspace(this.getAdminSession(), this.workspaceTest.getUid());
        Users.deleteTestUsers(this.getAdministrationController(), this.getAdminSession());
    }
}
