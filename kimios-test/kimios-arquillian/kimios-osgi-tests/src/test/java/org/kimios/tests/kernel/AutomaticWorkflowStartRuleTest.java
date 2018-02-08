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
import org.kimios.api.events.annotations.DmsEventName;
import org.kimios.api.events.annotations.DmsEventOccur;
import org.kimios.client.controller.helpers.StringTools;
import org.kimios.kernel.ws.pojo.WorkflowStatus;
import org.kimios.tests.helpers.WorkflowStatusDefinition;
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.dms.model.Folder;
import org.kimios.kernel.dms.model.Workflow;
import org.kimios.kernel.rules.model.EventBean;
import org.kimios.kernel.rules.model.RuleBean;
import org.kimios.kernel.security.model.SecurityEntityType;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.user.model.User;
import org.kimios.kernel.ws.pojo.WorkflowStatusManager;
import org.kimios.tests.deployments.OsgiDeployment;
import org.kimios.tests.helpers.XMLDescriptionGenerators;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Vector;

import static org.junit.Assert.*;

/**
 * Created by tom on 29/02/16.
 */
@RunWith(Arquillian.class)
public class AutomaticWorkflowStartRuleTest extends KernelTestAbstract {


    private static Logger logger = LoggerFactory.getLogger(AutomaticWorkflowStartRuleTest.class);

    @ArquillianResource
    BundleContext context;

    private static String FOLDER_TEST_1 = "WORKFLOW_TEST";
    private Folder folderTest1;
    private User userTest1;
    private Session userTest1Session;
    private long userTest1FolderUid;
    private Workflow workflow;
    private Long ruleId;


    @Deployment(name="karaf")
    public static JavaArchive createDeployment() {

        JavaArchive archive =
                OsgiDeployment.createArchive( AutomaticWorkflowStartRuleTest.class.getSimpleName() + ".jar", AutomaticWorkflowStartRuleTest.class,
                StringTools.class,
                WorkflowStatusDefinition.class,
                        XMLDescriptionGenerators.class
                );
        archive.addAsResource("tests/launch_kimios-tests_mvn_test.sh");
        archive.addAsResource("tests/testDoc.txt");
        File exportedFile = new File(AutomaticWorkflowStartRuleTest.class.getSimpleName() + ".jar");
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
        try {
            long workflowId = this.studioController.createWorkflow(this.adminSession, "TestWorkflow", "Workflow Test for automatic start",
                    XMLDescriptionGenerators.getWorkflowXMLDescriptor(-1, defs));

            this.workflow = this.studioController.getWorkflow(workflowId);
        }catch (Exception ex){
            ex.printStackTrace(System.err);
            logger.error("error while building sample workflow", ex);
        }

        //create rule for automatic workflow start on path
        RuleBean ruleBean = new RuleBean();
        ruleBean.setName("automaticWorkflowStartTest");
        // Dependency problem…
        // ruleBean.setJavaClass(org.kimios.kernel.rules.impl.AutomaticWorkflowStartRule.class.getName());
        ruleBean.setPath(this.folderTest1.getPath());
        ruleBean.setRecursive(true);
        ruleBean.setRuleCreationDate(new Date());
        ruleBean.setRuleOwner(this.adminSession.getUserName());
        ruleBean.setRuleOwnerSource(this.adminSession.getUserSource());
        ruleBean.setRuleUpdateDate(new Date());
        ruleBean.setEvents(new HashSet<EventBean>());
        EventBean fileUploadEvt = new EventBean();
        fileUploadEvt.setDmsEventName(DmsEventName.FILE_UPLOAD.ordinal());
        fileUploadEvt.setDmsEventStatus(DmsEventOccur.AFTER.ordinal());
        ruleBean.getEvents().add(fileUploadEvt);
        ruleBean.setParametersJson("{\"workflowName\":\"TestWorkflow\"}");

        this.ruleId = this.rulesController.createRule(this.adminSession, ruleBean);
        logger.info("Workflow {} and rule Id {} created", workflow, ruleId);
    }


    @Test
    public void testDocumentSimple() {
        Folder userTest1Folder = this.folderController.getFolder(this.userTest1Session, userTest1FolderUid);
        long docUid = -1;
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
        assertEquals("Workflow_Doc_Test", userDoc1.getName());
        assertEquals(userTest1Folder.getUid(), userDoc1.getFolderUid());

        int pendingRequestAdminCount = this.workflowController.getPendingWorkflowRequests(this.adminSession).size();
        int pendingRequestUserTest1Count = this.workflowController.getPendingWorkflowRequests(this.userTest1Session).size();
        assertTrue(pendingRequestAdminCount == 0);
        assertTrue(pendingRequestUserTest1Count == 0);
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
        if(this.ruleId != null){
            //remove rule
            this.rulesController.deleteRule(this.ruleId);
        }
        this.deleteTestUsers();
    }
}
