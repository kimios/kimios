package org.kimios.test.pax;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kimios.exceptions.AccessDeniedException;
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.dms.model.Folder;
import org.kimios.kernel.dms.model.Workspace;
import org.kimios.kernel.security.model.Session;
import org.kimios.webservices.exceptions.DMServiceException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DocumentTest extends KimiosTest {

    @Before
    public void createTestData() {
        super.createTestData();
    }

    @After
    public void deleteTestData() {
        super.deleteTestData();
    }

    @Test
    public void testCreate() {
        FileInputStream documentSampleInputStream = null;
        String documentResourceName = "sample.odt";
        String documentResourcePath = "documents/" + documentResourceName;
        try {
            File documentSample = resourceToFile(documentResourcePath, "/tmp/sample.odt");
            documentSampleInputStream = new FileInputStream(documentSample);
        } catch (IOException e) {
            Assert.fail("can't create file from resource : " + documentResourcePath);
        }

        Session adminSession = securityController.startSession("admin", "kimios");

        Workspace workspace = this.workspaceController.getWorkspace(adminSession, ACLTestUtils.WORKSPACE_TEST);
        long workspaceUid;
        if (workspace == null) {
            try {
                workspaceUid = this.workspaceController.createWorkspace(adminSession, ACLTestUtils.WORKSPACE_TEST);
            } catch (Exception e) {
                Assert.fail("can't create workspace : " + ACLTestUtils.WORKSPACE_TEST);
            }
        }
        workspaceUid = workspace.getUid();

        List<Folder> folderList = this.folderController.getFolders(adminSession, workspaceUid);
        if (
                folderList.size() == 0
                        || folderList.stream()
                        .filter(folder -> folder.getName().equals(ACLTestUtils.FOLDER_TEST_3))
                        .collect(Collectors.toList())
                        .size() == 0
        ) {
            try {
                long folderUid = this.folderController.createFolder(adminSession, ACLTestUtils.FOLDER_TEST_3, workspaceUid, false);
            } catch (Exception e) {
                Assert.fail("can't create folder : " + ACLTestUtils.FOLDER_TEST_3);
            }
        }

        long documentUid = this.documentController.createDocumentFromFullPathWithProperties(
                adminSession,
                "/" + ACLTestUtils.WORKSPACE_TEST + "/" + ACLTestUtils.FOLDER_TEST_3 + "/" + documentResourceName,
                false,
                new ArrayList<>(),
                false,
                -1,
                new ArrayList<>(),
                documentSampleInputStream,
                null,
                null
        );
        Assert.assertNotNull(documentUid);
        Assert.assertTrue(documentUid > 0);

        Document document = this.documentController.getDocument(adminSession, documentUid);
        long documentFolderUid = document.getFolderUid();
        Assert.assertNotNull(document);

        List<Document> documentList = this.documentController.getDocuments(adminSession, documentFolderUid);
        Assert.assertTrue(
                documentList.stream()
                        .filter(document1 -> document1.getUid() == documentUid)
                        .collect(Collectors.toList())
                        .size() == 1
        );

        document = this.documentController.getDocument(adminSession, document.getPath());
        Assert.assertNotNull(document);

        this.documentController.deleteDocument(adminSession, documentUid, false);
        documentList = this.documentController.getDocuments(adminSession, documentFolderUid);
        Assert.assertTrue(
                documentList.stream()
                        .filter(document1 -> document1.getUid() == documentUid)
                        .collect(Collectors.toList())
                        .size() == 0
        );

        exceptionRule.expect(AccessDeniedException.class);
        this.documentController.getDocument(adminSession, document.getPath());

        exceptionRule.expect(AccessDeniedException.class);
        this.documentController.getDocument(adminSession, document.getUid());
    }
}
