/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
public class UserDocumentWithPropertiesTest extends KernelTestAbstract {


    private static Logger logger = LoggerFactory.getLogger(UserDocumentWithPropertiesTest.class);

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
                OsgiDeployment.createArchive( UserDocumentWithPropertiesTest.class.getSimpleName() + ".jar", null, UserDocumentWithPropertiesTest.class,
                StringTools.class
                );
        archive.addAsResource("tests/launch_kimios-tests_mvn_test.sh");
        archive.addAsResource("tests/testDoc.txt");
        File exportedFile = new File(UserDocumentWithPropertiesTest.class.getSimpleName() + ".jar");
        archive.as(ZipExporter.class).exportTo(exportedFile, true);
        return archive;
    }

    @Before
    public void setUp() {

        this.init();

        this.adminSession = this.securityController.startSession(ADMIN_LOGIN, USER_TEST_SOURCE, ADMIN_PWD);

        try {
            this.workspaceTest = this.workspaceController.getWorkspace(this.adminSession, WORKSPACE_TEST_NAME);
        } catch (Exception e) {
            this.workspaceController.createWorkspace(this.adminSession, WORKSPACE_TEST_NAME);
            this.workspaceTest = this.workspaceController.getWorkspace(this.adminSession, WORKSPACE_TEST_NAME);
        }

        this.createTestUsers();
        // create folder in workspace
        long folderUid = this.folderController.createFolder(this.adminSession, FOLDER_TEST_1, this.workspaceTest.getUid(), false);
        this.folderTest1 = this.folderController.getFolder(this.adminSession, folderUid);
        // give access to users
        this.userTest1 = this.administrationController.getUser(this.adminSession, USER_TEST_1, USER_TEST_SOURCE);
        this.userTest2 = this.administrationController.getUser(this.adminSession, USER_TEST_2, USER_TEST_SOURCE);
        this.userTest3 = this.administrationController.getUser(this.adminSession, USER_TEST_3, USER_TEST_SOURCE);
        this.giveAccessToEntityForUser(this.adminSession, this.folderTest1, this.userTest1, true, true, false);
        this.giveAccessToEntityForUser(this.adminSession, this.folderTest1, this.userTest2, true, false, false);
        this.giveAccessToEntityForUser(this.adminSession, this.folderTest1, this.userTest3, true, false, false);

        // init test users' sessions
        this.userTest1Session = this.securityController.startSession(USER_TEST_1, USER_TEST_SOURCE, "test");
        this.userTest2Session = this.securityController.startSession(USER_TEST_2, USER_TEST_SOURCE, "test");
        this.userTest3Session = this.securityController.startSession(USER_TEST_3, USER_TEST_SOURCE, "test");

        // user 1 creates a subfolder
        this.userTest1FolderUid = this.folderController.createFolder(this.adminSession, "User_1_Folder", this.folderTest1.getUid(), true);
    }


    @Test
    public void testDocumentSimple() {
        Folder userTest1Folder = this.folderController.getFolder(this.adminSession, userTest1FolderUid);
        long docUid = -1;
        try {
            InputStream docStream = this.getClass().getClassLoader().getResourceAsStream("tests/testDoc.txt");
            logger.info("test document will be created in {} - content {}", userTest1Folder, docStream);
            docUid = this.documentController.createDocumentWithProperties(
                    this.adminSession,
                    "User Test 1 doc 1",
                    "log",
                    "text/plain",
                    userTest1Folder.getUid(),
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
            logger.error("error while document creation", e);
        }
        logger.info("Created document Id {}", docUid);
        assertFalse(docUid == -1);
        this.documentController.checkinDocument(this.adminSession, docUid);
        Document userDoc1 = this.documentController.getDocument(this.adminSession, docUid);
        assertNotNull(userDoc1);
        assertEquals("User Test 1 doc 1", userDoc1.getName());
        assertEquals(userTest1Folder.getUid(), userDoc1.getFolderUid());

        assertTrue(this.securityController.canRead(this.adminSession, userDoc1.getUid()));
        assertTrue(this.securityController.canRead(this.adminSession, userDoc1.getUid()));
        assertTrue(this.securityController.canRead(this.adminSession, userDoc1.getUid()));
        assertTrue(this.securityController.canWrite(this.adminSession, userDoc1.getUid()));


    }

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
            this.folderController.deleteFolder(this.adminSession, this.folderTest1.getUid());
        }
        this.deleteTestUsers();
    }
}
