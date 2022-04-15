package org.kimios.itest;

import org.kimios.exceptions.AccessDeniedException;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.IAdministrationController;
import org.kimios.kernel.controller.IFolderController;
import org.kimios.kernel.controller.ISecurityController;
import org.kimios.kernel.controller.IWorkspaceController;
import org.kimios.kernel.dms.model.Folder;
import org.kimios.kernel.dms.model.Workspace;
import org.kimios.kernel.security.model.DMEntitySecurity;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ACLTest extends AKimiosController {

    private static Logger logger = LoggerFactory.getLogger(ACLTest.class);

    private static String WORKSPACE_TEST = "workspace_test";
    private static String FOLDER_TEST_1 = "folder_test_1";
    private static String FOLDER_TEST_2 = "folder_test_2";
    private static String FOLDER_TEST_3 = "folder_test_3";
    private static String[] FOLDERS_TEST = {
            FOLDER_TEST_1,
            FOLDER_TEST_2,
            FOLDER_TEST_3
    };

    private ISecurityController securityController;
    private IAdministrationController administrationController;
    private IWorkspaceController workspaceController;
    private IFolderController folderController;

    private Session session;
    private Workspace workspace;
    private List<Folder> folderList = new ArrayList<>();

    private String array[][] = {
            {"testuser_1", "test1", "user1"},
            {"testuser_2", "test2", "user2"},
            {"testuser_3", "test3", "user3"}
    };

    public ACLTest() {
        System.out.println("in");
    }

    public ISecurityController getSecurityController() {
        return securityController;
    }

    public void setSecurityController(ISecurityController securityController) {
        this.securityController = securityController;
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

    public IFolderController getFolderController() {
        return folderController;
    }

    public void setFolderController(IFolderController folderController) {
        this.folderController = folderController;
    }

    public void start() {
        logger.info(getClass().getName() + " bundle launched");
        System.out.println(getClass().getName() + " bundle launched");

        try {
            this.setUp();
        } catch (Exception e) {
            logger.error("setUp() failed");
            logger.error(e.getMessage());
            return;
        }

        boolean successful = true;
        try {
            this.testACL();
        } catch (Exception e) {
            logger.error(e.getMessage());
            successful = false;
        }

        try {
            this.tearDown();
        } catch (Exception e) {
            logger.error("tearDown() failed");
            return;
        }

        logger.info(getClass().getName() + " end of integration test");
        String message = successful ?
                " *********** SUCCESSFUL ***********" :
                "  *********** NOOOOOOOOT SUCCESSFUL ***********";
        logger.info(getClass().getName() + message);
    }

    private void testACL() throws Exception {
        int nb = 0;

        AccessRight fffAccessRight = new AccessRight(false, false, false);
        AccessRight tffAccessRight = new AccessRight(true, false, false);

        User user1 = this.administrationController.getUser(session, array[0][0], "kimios");
        Session user1Session = this.securityController.startSession(user1.getUid(), "kimios");

        AccessRight user1AccessRight = new AccessRight(
                this.securityController.canRead(user1Session, this.workspace.getUid()),
                this.securityController.canWrite(user1Session, this.workspace.getUid()),
                this.securityController.hasFullAccess(user1Session, this.workspace.getUid())
        );
        this.shouldBeEqual(user1AccessRight, fffAccessRight);
        logger.info("test " + nb++ + " ok");

        List<DMEntitySecurity> securityList = new ArrayList<>();
        securityList.add(new DMEntitySecurity(
                this.workspace.getUid(),
                this.workspace.getType(),
                user1.getUid(),
                user1.getAuthenticationSourceName(),
                user1.getType(),
                true,
                false,
                false
        ));
        this.securityController.updateDMEntitySecurities(
                session,
                this.workspace.getUid(),
                securityList,
                false,
                true
        );
        this.updateAccessRight(user1AccessRight, user1Session, this.workspace.getUid());
        this.shouldBeEqual(user1AccessRight, tffAccessRight);
        logger.info("test " + nb++ + " ok");

        User user2 = this.administrationController.getUser(session, array[1][0], "kimios");
        Session user2Session = this.securityController.startSession(user2.getUid(), "kimios");
        AccessRight user2AccessRight = new AccessRight();
        this.updateAccessRight(user2AccessRight, user2Session, this.workspace.getUid());
        this.shouldBeEqual(user2AccessRight, fffAccessRight);
        logger.info("test " + nb++ + " ok");

        User user3 = this.administrationController.getUser(session, array[2][0], "kimios");
        Session user3Session = this.securityController.startSession(user3.getUid(), "kimios");
        AccessRight user3AccessRight = new AccessRight();
        this.updateAccessRight(user3AccessRight, user3Session, this.workspace.getUid());
        this.shouldBeEqual(user3AccessRight, fffAccessRight);
        logger.info("test " + nb++ + " ok");

        securityList = new ArrayList<>();
        securityList.add(new DMEntitySecurity(
                this.workspace.getUid(),
                this.workspace.getType(),
                user2.getUid(),
                user2.getAuthenticationSourceName(),
                user2.getType(),
                true,
                false,
                false
        ));
        this.securityController.updateDMEntitySecurities(
                session,
                this.workspace.getUid(),
                securityList,
                false,
                true
        );
        this.updateAccessRight(user1AccessRight, user1Session, this.workspace.getUid());
        this.updateAccessRight(user2AccessRight, user2Session, this.workspace.getUid());
        this.updateAccessRight(user3AccessRight, user3Session, this.workspace.getUid());
        this.shouldBeEqual(user1AccessRight, tffAccessRight, "user1AccessRight");
        this.shouldBeEqual(user2AccessRight, tffAccessRight, "user2AccessRight");
        this.shouldBeEqual(user3AccessRight, fffAccessRight, "user3AccessRight");
    }

    private void setUp() throws Exception {
        session = this.securityController.startSession("admin", "kimios");

        if (session == null) {
            logger.error("session is null");
            return;
        }

        this.createUsers(array);
        if (! this.testUsersExist(array)) {
            logger.error("test users not created");
            throw new Exception("test users not created");
        }

        this.createWorkspaceAndFolders(WORKSPACE_TEST, FOLDERS_TEST);
        this.testWorkspaceAndFoldersExist(this.workspace, this.folderList);

    }

    private void tearDown() throws Exception {
        this.removeUsers(array);
        if (! this.testUsersNotExist(array)) {
            logger.error("test users not removed");
            throw new Exception("test users not removed");
        }

        this.removeWorkspaceAndFolders(this.workspace, this.folderList);
        this.testWorkspaceAndFoldersNotExist(this.workspace, this.folderList);
    }

    private void createUsers(String[][] array) {
        for (String[] user : array) {
            this.administrationController.createUser(
                    session,
                    user[0],
                    user[1],
                    user[2],
                    "030250600502",
                    "auie@auie.auie",
                    user[0],
                    "kimios",
                    true
            );
        }
    }

    private void createWorkspaceAndFolders(String workspaceName, String[] folderNameList) throws Exception {
        this.workspaceController.createWorkspace(session, workspaceName);
        this.workspace = this.workspaceController.getWorkspace(session, workspaceName);
        for (String folder: folderNameList) {
            long folderUid = this.folderController.createFolder(session, folder, this.workspace.getUid(), false);
            Folder folder1 = this.folderController.getFolder(session, folderUid);
            this.folderList.add(folder1);
            for (String folder2: folderNameList) {
                long subFolderUid = this.folderController.createFolder(session, folder2, folderUid, false);
                Folder subFolder = this.folderController.getFolder(session, subFolderUid);
                this.folderList.add(subFolder);
            }
        }
    }

    private void removeWorkspaceAndFolders(Workspace workspace, List<Folder> folderList) {
        List<Folder> folderListClone = folderList.stream().collect(Collectors.toList());
        Collections.reverse(folderListClone);
        folderListClone.forEach(folder -> this.folderController.deleteFolder(session, folder.getUid()));

        this.workspaceController.deleteWorkspace(session, workspace.getUid());
    }

    private void testWorkspaceAndFoldersExist(Workspace workspace, List<Folder> folderList) throws Exception {
        if (workspace == null) {
            String message = "workspace not created";
            logger.error(message);
            throw new Exception(message);
        }

        for (Folder folder: folderList) {
            if (folder == null) {
                String message = "folder not created";
                logger.error(message);
                throw new Exception(message);
            }
        }
    }

    private void testWorkspaceAndFoldersNotExist(Workspace workspace, List<Folder> folderList) throws Exception {
        try {
            Workspace workspace1 = this.workspaceController.getWorkspace(session, workspace.getUid());
            if (workspace1 != null) {
                String message = "workspace not removed";
                logger.error(message);
                throw new Exception(message);
            }
        } catch (AccessDeniedException e) {

        }

        for (Folder folder: folderList) {
            try {
                Folder folder1 = this.folderController.getFolder(session, folder.getUid());
                if (folder1 != null) {
                    String message = "folder not removed";
                    logger.error(message);
                    throw new Exception(message);
                }
            } catch (AccessDeniedException e) {

            }
        }
    }

    private boolean testUsersExist(String[][] array) {
        boolean ret = false;
        for (String[] userArray : array) {
            User user1 = this.administrationController.getUser(session, userArray[0], "kimios");
            ret = (user1 != null);
            if (ret == false) {
                break;
            }
        }
        return ret;
    }

    private void removeUsers(String[][] array) {
        for (String[] user : array) {
            this.administrationController.deleteUser(
                    session,
                    user[0],
                    "kimios"
            );
        }
    }

    private boolean testUsersNotExist(String[][] array) {
        boolean ret = false;
        for (String[] userArray : array) {
            User user1 = this.administrationController.getUser(session, userArray[0], "kimios");
            ret = (user1 == null);
            if (ret == false) {
                break;
            }
        }
        return ret;
    }

    private void shouldBeEqual(AccessRight accessRight1, AccessRight accessRight2) throws Exception {
        if (! accessRight1.equals(accessRight2)) {
            throw new Exception("access right should be equal "
                    + accessRight1.toString()
                    + " <> "
                    + accessRight2.toString()
            );
        }
    }

    private void shouldBeEqual(AccessRight accessRight1, AccessRight accessRight2, String message) throws Exception {
        if (! accessRight1.equals(accessRight2)) {
            throw new Exception(message
                    + " "
                    + accessRight1.toString()
                    + " <> "
                    + accessRight2.toString()
            );
        }
    }

    private void updateAccessRight(AccessRight accessRight, Session session, long dmEntityUid) {
        accessRight.setRead(this.securityController.canRead(session, dmEntityUid));
        accessRight.setWrite(this.securityController.canWrite(session, dmEntityUid));
        accessRight.setFullAccess(this.securityController.hasFullAccess(session, dmEntityUid));
    }
}
