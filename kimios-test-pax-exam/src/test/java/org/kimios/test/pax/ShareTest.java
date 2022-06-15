package org.kimios.test.pax;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kimios.exceptions.NamingException;
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.share.model.Share;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShareTest extends KimiosTest {

    @Before
    public void createTestData() {
        super.createTestData();
    }

    @After
    public void deleteTestData() {
        super.deleteTestData();
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
        String documentPath = "/" + ACLTestUtils.WORKSPACE_TEST + "/" + ACLTestUtils.FOLDERS_TEST[1] + "/" + documentSampleTab[0];
        try {
            if (documentSampleMap.get(documentSampleName) == null
                    || this.documentController.getDocument(adminSession, documentSampleMap.get(documentSampleName)) == null) {
                documentId = this.documentController.createDocumentFromFullPathWithProperties(
                        adminSession,
                        documentPath,
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
            Document document = this.documentController.getDocument(adminSession, documentPath);
            if (document == null) {
                Assert.fail();
            } else {
                documentId = document.getUid();
            }
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

        List<Share> shareList = null;
        try {
            shareList = this.shareController.listDocumentShares(adminSession, documentId);
        } catch (Exception e) {
            Assert.fail("exception when calling listDocumentShares()");
        }
        Assert.assertEquals(1, shareList.size());
    }
}
