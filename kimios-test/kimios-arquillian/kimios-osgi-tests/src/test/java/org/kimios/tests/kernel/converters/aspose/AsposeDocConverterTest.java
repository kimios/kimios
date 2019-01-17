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

package org.kimios.tests.kernel.converters.aspose;

import org.apache.commons.io.IOUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kimios.api.InputSource;
import org.kimios.client.controller.helpers.StringTools;
import org.kimios.aspose.converters.DocxToHTML;
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.dms.model.Folder;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.user.model.User;
import org.kimios.tests.deployments.OsgiDeployment;
import org.kimios.tests.helpers.DocumentCreator;
import org.kimios.tests.helpers.WorkflowStatusDefinition;
import org.kimios.tests.helpers.XMLDescriptionGenerators;
import org.kimios.tests.kernel.KernelTestAbstract;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by tom on 29/02/16.
 */
//@RunWith(Arquillian.class)
public class AsposeDocConverterTest extends KernelTestAbstract {


    private static Logger logger = LoggerFactory.getLogger(AsposeDocConverterTest.class);

    /*@ArquillianResource
    BundleContext context;

    private static String FOLDER_TEST_1 = "ASPOSE_TEST";
    private Folder folderTest1;
    private User userTest1;
    private Session userTest1Session;
    private long userTest1FolderUid;
    private long docUid = -1;

    @Deployment(name="karaf")
    public static JavaArchive createDeployment() {

        JavaArchive archive =
                OsgiDeployment.createArchive( AsposeDocConverterTest.class.getSimpleName() + ".jar", AsposeDocConverterTest.class,
                StringTools.class,
                WorkflowStatusDefinition.class,
                        XMLDescriptionGenerators.class,
                        DocumentCreator.class
                );

        archive.addAsResource("tests/launch_kimios-tests_mvn_test.sh");
        archive.addAsResource("tests/testDoc.txt");
        archive.addAsResource("tests/converters/docx_sample.docx");
        archive.addAsResource("tests/converters/odp_sample.odp");
        archive.addAsResource("tests/converters/pptx_sample.pptx");
        archive.addAsResource("tests/converters/eml_sample.eml");
        File exportedFile = new File(AsposeDocConverterTest.class.getSimpleName() + ".jar");
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

        try {
            long folderUid = this.folderController.createFolder(this.adminSession, FOLDER_TEST_1, this.workspaceTest.getUid(), false);
            this.folderTest1 = this.folderController.getFolder(this.adminSession, folderUid);

        }catch (Exception ex){
            this.folderTest1 = this.folderController.getFolder(this.adminSession, FOLDER_TEST_1, workspaceTest.getUid(), 1);
        }
        // give access to users
        this.userTest1 = this.administrationController.getUser(this.adminSession, USER_TEST_1, USER_TEST_SOURCE);
        this.giveAccessToEntityForUser(this.adminSession, this.folderTest1, this.userTest1, true, true, false);




        // init test users' sessions
        this.userTest1Session = this.securityController.startSession(USER_TEST_1, USER_TEST_SOURCE, "test");

    }


    @Test
    public void testDocumentSimple() throws IOException {
        docUid = new DocumentCreator(this.documentController)
                .createDocument(adminSession, folderTest1,
                        this.getClass().getClassLoader(),
                        "tests/converters/docx_sample.docx",
                        "docx_sample", "docx");
        assertTrue(docUid != -1);
        this.documentController.checkinDocument(this.adminSession, docUid);
        Document userDoc1 = this.documentController.getDocument(this.adminSession, docUid);
        assertNotNull(userDoc1);
        InputSource source = converterController.convertDocument(this.adminSession, docUid, DocxToHTML.class.getName(), "html");
        String content = IOUtils.toString(source.getInputStream());
        logger.info("{}", content);
    }

    @After
    public void tearDown() {
        // init test users' sessions
        if (this.userTest1Session != null) {
            this.securityController.endSession(this.userTest1Session.getUid());
        }
        /*if (this.folderTest1 != null) {
            this.folderController.deleteFolder(this.adminSession, this.folderTest1.getUid());
        }
        this.deleteTestUsers();
    }*/
}
