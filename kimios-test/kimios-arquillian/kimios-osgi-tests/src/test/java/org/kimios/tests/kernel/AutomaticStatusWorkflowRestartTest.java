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
import org.kimios.kernel.dms.model.DocumentWorkflowStatusRequest;
import org.kimios.kernel.dms.model.Folder;
import org.kimios.kernel.dms.model.Workflow;
import org.kimios.kernel.security.model.SecurityEntityType;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.user.model.User;
import org.kimios.kernel.ws.pojo.WorkflowStatus;
import org.kimios.kernel.ws.pojo.WorkflowStatusManager;
import org.kimios.tests.deployments.OsgiDeployment;
import org.kimios.tests.helpers.WorkflowStatusDefinition;
import org.kimios.tests.helpers.XMLDescriptionGenerators;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;

import static org.junit.Assert.*;

/**
 * Created by tom on 29/02/16.
 */
@RunWith(Arquillian.class)
public class AutomaticStatusWorkflowRestartTest extends KernelTestAbstract {


    private static Logger logger = LoggerFactory.getLogger(AutomaticStatusWorkflowRestartTest.class);

    @ArquillianResource
    BundleContext context;

    private static String FOLDER_TEST_1 = "WORKFLOW_TEST";
    private Folder folderTest1;
    private User userTest1;
    private Session userTest1Session;
    private long userTest1FolderUid;
    private Workflow workflow;
    private long docUid = -1;


    @Deployment(name="karaf")
    public static JavaArchive createDeployment() {

        JavaArchive archive =
                OsgiDeployment.createArchive( AutomaticStatusWorkflowRestartTest.class.getSimpleName() + ".jar", AutomaticStatusWorkflowRestartTest.class,
                StringTools.class,
                WorkflowStatusDefinition.class,
                        XMLDescriptionGenerators.class
                );
        archive.addAsResource("tests/launch_kimios-tests_mvn_test.sh");
        archive.addAsResource("tests/testDoc.txt");
        File exportedFile = new File(AutomaticStatusWorkflowRestartTest.class.getSimpleName() + ".jar");
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
        this.giveAccessToEntityForUser(this.adminSession, this.folderTest1, this.userTest1, true, true, false);

        // init test users' sessions
        this.userTest1Session = this.securityController.startSession(USER_TEST_1, USER_TEST_SOURCE, "test");

        // user 1 creates a subfolder
        this.userTest1FolderUid = this.folderController.createFolder(this.userTest1Session, "User_1_Folder", this.folderTest1.getUid(), true);

        //create workflow

        Vector<WorkflowStatusDefinition> defs = new Vector<WorkflowStatusDefinition>();
        WorkflowStatusManager workflowStatusManager = new WorkflowStatusManager();
        workflowStatusManager.setSecurityEntitySource(this.userTest1Session.getUserSource());
        workflowStatusManager.setSecurityEntityName(this.userTest1Session.getUserName());
        workflowStatusManager.setSecurityEntityType(SecurityEntityType.USER);

        WorkflowStatusManager workflowStatusManager2 = new WorkflowStatusManager();
        workflowStatusManager2.setSecurityEntitySource(this.adminSession.getUserSource());
        workflowStatusManager2.setSecurityEntityName(this.adminSession.getUserName());
        workflowStatusManager2.setSecurityEntityType(SecurityEntityType.USER);

        WorkflowStatusDefinition def1 = new WorkflowStatusDefinition();
        WorkflowStatus status = new WorkflowStatus();
        status.setName("first status");
        status.setSuccessorUid(-1);
        def1.setWorkflowStatus(status);
        def1.setWorkflowStatusManagers(new WorkflowStatusManager[]{workflowStatusManager, workflowStatusManager2});
        defs.add(def1);

        WorkflowStatusDefinition def2 = new WorkflowStatusDefinition();
        WorkflowStatus status2 = new WorkflowStatus();
        status2.setName("second status");
        status2.setSuccessorUid(-1);
        def2.setWorkflowStatus(status2);
        def2.setWorkflowStatusManagers(new WorkflowStatusManager[]{workflowStatusManager, workflowStatusManager2});
        defs.add(def2);

        try {
            long workflowId = this.studioController.createWorkflowWithAutomaticRestartOption(
                    this.adminSession, "TestWorkflow", "Workflow Test for automatic restart on reject", true,
                    XMLDescriptionGenerators.getWorkflowXMLDescriptor(-1, defs));

            this.workflow = this.studioController.getWorkflow(workflowId);
        }catch (Exception ex){
            ex.printStackTrace(System.err);
            logger.error("error while building sample workflow", ex);
        }

        logger.info("Workflow {}  created", workflow);


        Folder userTest1Folder = this.folderController.getFolder(this.userTest1Session, userTest1FolderUid);

        try {
            InputStream docStream = this.getClass().getClassLoader().getResourceAsStream("tests/testDoc.txt");
            logger.info("test document will be created in {} - content {}", userTest1Folder, docStream);
            docUid = this.documentController.createDocumentWithProperties(
                    this.userTest1Session,
                    "Workflow_Doc_Test",
                    "txt",
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
        this.documentController.checkinDocument(this.userTest1Session, docUid);
        Document userDoc1 = this.documentController.getDocument(this.userTest1Session, docUid);
        assertNotNull(userDoc1);
    }


    @Test
    public void testDocumentSimple() {
        //start workflow
        this.workflowController.createWorkflowRequest(this.adminSession, docUid,
                this.studioController.getWorkflowStatuses(workflow.getUid()).get(0).getUid());

        //test first status state
        List<DocumentWorkflowStatusRequest> pending = this.workflowController.getPendingWorkflowRequests(this.adminSession);
        assertTrue(pending.size() == 1);
        DocumentWorkflowStatusRequest reqToValidate = pending.get(0);
        assertTrue(reqToValidate.getWorkflowStatus().getName().equals("first status"));
        this.workflowController.acceptWorkflowRequest(this.adminSession, docUid,
        reqToValidate.getUserName(), reqToValidate.getUserSource(), this.adminSession.getUserName(), this.adminSession.getUserSource(),
                reqToValidate.getWorkflowStatusUid(), reqToValidate.getDate(), "test validation");


        //test second status state
        pending = this.workflowController.getPendingWorkflowRequests(this.adminSession);
        assertTrue(pending.size() == 1);
        DocumentWorkflowStatusRequest reqToStatus2 = pending.get(0);
        assertTrue(reqToStatus2.getWorkflowStatus().getName().equals("second status"));
        this.workflowController.rejectWorkflowRequest(this.adminSession, docUid,
                reqToStatus2.getUserName(), reqToStatus2.getUserSource(), this.adminSession.getUserName(), this.adminSession.getUserSource(),
                reqToStatus2.getWorkflowStatusUid(), reqToStatus2.getDate(), "reject test validation");



        //test regenerated status
        pending = this.workflowController.getPendingWorkflowRequests(this.adminSession);
        assertTrue(pending.size() == 1);
        DocumentWorkflowStatusRequest regeneratedReq = pending.get(0);
        assertTrue(regeneratedReq.getWorkflowStatus().getName().equals("first status"));
    }

    @After
    public void tearDown() {
        // init test users' sessions
        if (this.userTest1Session != null) {
            this.securityController.endSession(this.userTest1Session.getUid());
        }
        if (this.folderTest1 != null) {
            this.folderController.deleteFolder(this.adminSession, this.folderTest1.getUid());
        }
        if(this.workflow != null){
            this.studioController.deleteWorkflow(this.adminSession, this.workflow.getUid());
        }

        this.deleteTestUsers();
    }
}
