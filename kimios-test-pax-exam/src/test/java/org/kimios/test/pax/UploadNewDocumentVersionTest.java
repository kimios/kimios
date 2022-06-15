package org.kimios.test.pax;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kimios.exceptions.NamingException;
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.dms.model.DocumentVersion;
import org.kimios.kernel.security.model.Session;
import org.kimios.webservices.exceptions.DMServiceException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UploadNewDocumentVersionTest extends KimiosTest {

    @Before
    public void createTestData() {
        super.createTestData();
    }

    @After
    public void deleteTestData() {
        super.deleteTestData();
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
        List<DocumentVersion> documentVersionList = this.documentVersionController.getDocumentVersions(adminSession, documentId);
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
}
