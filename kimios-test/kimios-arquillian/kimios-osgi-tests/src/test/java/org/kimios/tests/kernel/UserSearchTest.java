package org.kimios.tests.kernel;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kimios.kernel.security.model.SecurityEntity;
import org.kimios.kernel.security.model.SecurityEntityType;
import org.kimios.kernel.user.model.Group;
import org.kimios.kernel.user.model.User;
import org.kimios.tests.deployments.OsgiDeployment;
import org.kimios.tests.utils.dataset.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by tom on 26/02/16.
 */
@RunWith(Arquillian.class)
public class UserSearchTest  extends KernelTestAbstract {

    public static final String GROUP_TEST_1 = "the Johnnys";
    public static final String GROUP_TEST_2 = "the teclibians";
    public static final String GROUP_TEST_3 = "Henry and Funkyman";
    public static final String GROUP_TEST_4 = "the John with funky spirit";

    public static final String GROUP_TEST_2_1 = "the jazzy Johnnys";
    public static final String GROUP_TEST_2_2 = "the jazzy teclibians";
    public static final String GROUP_TEST_2_3 = "Henry and metal man";
    public static final String GROUP_TEST_2_4 = "the John with metal spirit";

    private static Logger logger = LoggerFactory.getLogger(UserSearchTest.class);

    @Deployment(name="karaf")
    public static JavaArchive createDeployment() {
        return OsgiDeployment.createArchive("UserSearchTest.jar", null, UserSearchTest.class);
    }

    @Before
    public void setUp() {

        logger.info("in the setUp method !");

        this.init();

        this.setAdminSession(this.getSecurityController().startSession(ADMIN_LOGIN, Users.USER_TEST_SOURCE, ADMIN_PWD));

        // populating domain 1
        // create several users
        this.administrationController.createUser(this.getAdminSession(),
                "userTest1", "John", "Smith", "07", "john.smith@teclib.com", "mailTest1", Users.USER_TEST_SOURCE, true);
        this.administrationController.createUser(this.getAdminSession(),
                "userTest2", "Johnny", "Smooth", "070707", "johnny.smooth@teclib.com", "mailTest2", Users.USER_TEST_SOURCE, true);
        this.administrationController.createUser(this.getAdminSession(),
                "userTest3", "Johnny", "Cash", "070707", "johnny@ca.sh", "mailTest3", Users.USER_TEST_SOURCE, true);
        this.administrationController.createUser(this.getAdminSession(),
                "userTest4", "James", "Brown", "070707", "james.brown@funky.man", "mailTest4", Users.USER_TEST_SOURCE, true);
        this.administrationController.createUser(this.getAdminSession(),
                "userTest5", "Henry", "II", "070707", "henryii@caramail.com", "mailTest5", Users.USER_TEST_SOURCE, true);

        //create several groups
        this.administrationController.createGroup(this.getAdminSession(),
                "groupTest1", GROUP_TEST_1, Users.USER_TEST_SOURCE);
        this.administrationController.createGroup(this.getAdminSession(),
                "groupTest2", GROUP_TEST_2, Users.USER_TEST_SOURCE);
        this.administrationController.createGroup(this.getAdminSession(),
                "groupTest3", GROUP_TEST_3, Users.USER_TEST_SOURCE);
        this.administrationController.createGroup(this.getAdminSession(),
                "groupTest4", GROUP_TEST_4, Users.USER_TEST_SOURCE);

        // creation of second domain
        try {
            this.administrationController.createAuthenticationSource(
                    this.getAdminSession(), USER_TEST_SOURCE_2,
                    "org.kimios.kernel.user.impl.HAuthenticationSource",
                    true,
                    false,
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <authentication-source name=\"domainForDebug\"> </authentication-source>"
            );
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        // populating domain 2
        // create several users
        this.administrationController.createUser(this.getAdminSession(),
                "userTest1", "Miles", "Davis", "07", "miles@teclib.com", "mailTest1", USER_TEST_SOURCE_2, true);
        this.administrationController.createUser(this.getAdminSession(),
                "userTest2", "Johnny", "Hodges", "070707", "johnny.hodges@teclib.com", "mailTest2", USER_TEST_SOURCE_2, true);
        this.administrationController.createUser(this.getAdminSession(),
                "userTest3", "John", "Coltrane", "070707", "john@coltra.ne", "mailTest3", USER_TEST_SOURCE_2, true);
        this.administrationController.createUser(this.getAdminSession(),
                "userTest4", "James", "Hetfield", "070707", "james.hetfield@metal.lica", "mailTest4", USER_TEST_SOURCE_2, true);
        this.administrationController.createUser(this.getAdminSession(),
                "userTest5", "Henry", "IV", "070707090", "henryiv@libertysurf.net", "mailTest5", USER_TEST_SOURCE_2, true);

        //create several groups
        this.administrationController.createGroup(this.getAdminSession(),
                "groupTest1", GROUP_TEST_2_1, USER_TEST_SOURCE_2);
        this.administrationController.createGroup(this.getAdminSession(),
                "groupTest2", GROUP_TEST_2_2, USER_TEST_SOURCE_2);
        this.administrationController.createGroup(this.getAdminSession(),
                "groupTest3", GROUP_TEST_2_3, USER_TEST_SOURCE_2);
        this.administrationController.createGroup(this.getAdminSession(),
                "groupTest4", GROUP_TEST_2_4, USER_TEST_SOURCE_2);
    }

    @Test
    public void testSearchUsers() {
        String searchText = "John";
        List<SecurityEntity> secEntities = null;
        try {
            secEntities = this.administrationController.searchSecurityEntities(searchText, Users.USER_TEST_SOURCE, SecurityEntityType.USER);
        } catch (Exception e) {
            System.out.println("Exception of type " + e.getClass().getName());
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        }
        assertNotNull(secEntities);
        assertEquals(3, secEntities.size());

        // let's see the users in the list
        HashMap<String, SecurityEntity> hashSecEntities = new HashMap<String, SecurityEntity>();
        for (SecurityEntity secEntity : secEntities) {
            hashSecEntities.put(secEntity.getID(), secEntity);
        }
        User userTest1 = (User)hashSecEntities.get("userTest1");
        User userTest2 = (User)hashSecEntities.get("userTest2");
        User userTest3 = (User)hashSecEntities.get("userTest3");
        assertNotNull(userTest1);
        assertEquals("John", userTest1.getFirstName());
        assertNotNull(userTest2);
        assertEquals("Johnny", userTest2.getFirstName());
        assertNotNull(userTest3);
        assertEquals("Johnny", userTest3.getFirstName());

        searchText = "johnny";
        // reset
        secEntities = null;
        try {
            secEntities = this.administrationController.searchSecurityEntities(searchText, Users.USER_TEST_SOURCE, SecurityEntityType.USER);
        } catch (Exception e) {
            System.out.println("Exception of type " + e.getClass().getName());
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        }
        assertNotNull(secEntities);
        assertEquals(2, secEntities.size());
        // let's see the users in the list
        hashSecEntities = new HashMap<String, SecurityEntity>();
        for (SecurityEntity secEntity : secEntities) {
            hashSecEntities.put(secEntity.getID(), secEntity);
        }

        userTest2 = (User)hashSecEntities.get("userTest2");
        userTest3 = (User)hashSecEntities.get("userTest3");
        assertNotNull(userTest2);
        assertEquals("Johnny", userTest2.getFirstName());
        assertEquals("Smooth", userTest2.getLastName());
        assertNotNull(userTest3);
        assertEquals("Johnny", userTest3.getFirstName());
        assertEquals("Cash", userTest3.getLastName());


        searchText = "teclib";
        // reset
        secEntities = null;
        try {
            // filtering
            secEntities = this
                    .administrationController
                    .searchSecurityEntities(searchText, Users.USER_TEST_SOURCE, SecurityEntityType.USER);
            secEntities = secEntities.stream()
                    .filter(s -> s instanceof User
                            && ((User)s).getUid().startsWith("userTest"))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Exception of type " + e.getClass().getName());
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        }
        assertNotNull(secEntities);
        assertEquals(2, secEntities.size());
        // let's see the users in the list
        hashSecEntities = new HashMap<String, SecurityEntity>();
        for (SecurityEntity secEntity : secEntities) {
            hashSecEntities.put(secEntity.getID(), secEntity);
        }

        userTest1 = (User)hashSecEntities.get("userTest1");
        userTest2 = (User)hashSecEntities.get("userTest2");
        assertNotNull(userTest1);
        assertEquals("John", userTest1.getFirstName());
        assertEquals("Smith", userTest1.getLastName());
        assertNotNull(userTest2);
        assertEquals("Johnny", userTest2.getFirstName());
        assertEquals("Smooth", userTest2.getLastName());


        searchText = "cash";
        // reset
        secEntities = null;
        try {
            secEntities = this.administrationController.searchSecurityEntities(searchText, Users.USER_TEST_SOURCE, SecurityEntityType.USER);
        } catch (Exception e) {
            System.out.println("Exception of type " + e.getClass().getName());
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        }
        assertNotNull(secEntities);
        assertEquals(1, secEntities.size());
        // let's see the users in the list
        hashSecEntities = new HashMap<String, SecurityEntity>();
        for (SecurityEntity secEntity : secEntities) {
            hashSecEntities.put(secEntity.getID(), secEntity);
        }

        userTest3 = (User)hashSecEntities.get("userTest3");
        assertNotNull(userTest3);
        assertEquals("Johnny", userTest3.getFirstName());
        assertEquals("Cash", userTest3.getLastName());


        searchText = "test";
        // reset
        secEntities = null;
        try {
            secEntities = this.administrationController.searchSecurityEntities(searchText, Users.USER_TEST_SOURCE, SecurityEntityType.USER);
        } catch (Exception e) {
            System.out.println("Exception of type " + e.getClass().getName());
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        }
        assertNotNull(secEntities);
        assertEquals(5, secEntities.size());
        // let's see the users in the list
        hashSecEntities = new HashMap<String, SecurityEntity>();
        for (SecurityEntity secEntity : secEntities) {
            hashSecEntities.put(secEntity.getID(), secEntity);
        }
        userTest1 = (User)hashSecEntities.get("userTest1");
        userTest2 = (User)hashSecEntities.get("userTest2");
        userTest3 = (User)hashSecEntities.get("userTest3");
        User userTest4 = (User)hashSecEntities.get("userTest4");
        User userTest5 = (User)hashSecEntities.get("userTest5");
        assertNotNull(userTest1);
        assertNotNull(userTest2);
        assertNotNull(userTest3);
        assertNotNull(userTest4);
        assertNotNull(userTest5);
    }

    @Test
    public void testSearchGroups() {
        String searchText = "John";
        List<SecurityEntity> secEntities = null;
        try {
            secEntities = this.administrationController
                    .searchSecurityEntities(searchText, Users.USER_TEST_SOURCE, SecurityEntityType.GROUP)
                    .stream()
                    .filter(s -> (s instanceof User
                            && ((User)s).getUid().startsWith("userTest"))
                            || (s instanceof Group
                            && ((Group)s).getGid().startsWith("groupTest")))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Exception of type " + e.getClass().getName());
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        }
        assertNotNull(secEntities);
        assertEquals(2, secEntities.size());
        // let's see the groups in the list
        HashMap<String, SecurityEntity> hashSecEntities = new HashMap<String, SecurityEntity>();
        for (SecurityEntity secEntity : secEntities) {
            hashSecEntities.put(secEntity.getID(), secEntity);
        }
        Group groupTest1 = (Group)hashSecEntities.get("groupTest1");
        Group groupTest4 = (Group)hashSecEntities.get("groupTest4");
        assertNotNull(groupTest1);
        assertEquals(GROUP_TEST_1, groupTest1.getName());
        assertNotNull(groupTest4);
        assertEquals(GROUP_TEST_4, groupTest4.getName());


        searchText = "johnny";
        // reset
        secEntities = null;
        try {
            secEntities = this.administrationController
                    .searchSecurityEntities(searchText, Users.USER_TEST_SOURCE, SecurityEntityType.GROUP)
                    .stream()
                    .filter(s -> (s instanceof User
                            && ((User)s).getUid().startsWith("userTest"))
                            || (s instanceof Group
                            && ((Group)s).getGid().startsWith("groupTest")))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Exception of type " + e.getClass().getName());
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        }
        assertNotNull(secEntities);
        assertEquals(1, secEntities.size());
        // let's see the users in the list
        hashSecEntities = new HashMap<String, SecurityEntity>();
        for (SecurityEntity secEntity : secEntities) {
            hashSecEntities.put(secEntity.getID(), secEntity);
        }
        groupTest1 = (Group)hashSecEntities.get("groupTest1");
        assertNotNull(groupTest1);
        assertEquals(GROUP_TEST_1, groupTest1.getName());


        searchText = "teclib";
        // reset
        secEntities = null;
        try {
            secEntities = this.administrationController
                    .searchSecurityEntities(searchText, Users.USER_TEST_SOURCE, SecurityEntityType.GROUP)
                    .stream()
                    .filter(s -> (s instanceof User
                            && ((User)s).getUid().startsWith("userTest"))
                            || (s instanceof Group
                            && ((Group)s).getGid().startsWith("groupTest")))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Exception of type " + e.getClass().getName());
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        }
        assertNotNull(secEntities);
        assertEquals(1, secEntities.size());
        // let's see the users in the list
        hashSecEntities = new HashMap<String, SecurityEntity>();
        for (SecurityEntity secEntity : secEntities) {
            hashSecEntities.put(secEntity.getID(), secEntity);
        }

        Group groupTest2 = (Group)hashSecEntities.get("groupTest2");
        assertNotNull(groupTest2);
        assertEquals(GROUP_TEST_2, groupTest2.getName());


        searchText = "Funky";
        // reset
        secEntities = null;
        try {
            secEntities = this.administrationController
                    .searchSecurityEntities(searchText, Users.USER_TEST_SOURCE, SecurityEntityType.GROUP)
                    .stream()
                    .filter(s -> (s instanceof User
                            && ((User)s).getUid().startsWith("userTest"))
                            || (s instanceof Group
                            && ((Group)s).getGid().startsWith("groupTest")))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Exception of type " + e.getClass().getName());
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        }
        assertNotNull(secEntities);
        assertEquals(2, secEntities.size());
        // let's see the users in the list
        hashSecEntities = new HashMap<String, SecurityEntity>();
        for (SecurityEntity secEntity : secEntities) {
            hashSecEntities.put(secEntity.getID(), secEntity);
        }
        Group groupTest3 = (Group)hashSecEntities.get("groupTest3");
        groupTest4 = (Group)hashSecEntities.get("groupTest4");
        assertNotNull(groupTest3);
        assertEquals(GROUP_TEST_3, groupTest3.getName());
        assertNotNull(groupTest4);
        assertEquals(GROUP_TEST_4, groupTest4.getName());


        searchText = "test";
        // reset
        secEntities = null;
        try {
            secEntities = this.administrationController
                    .searchSecurityEntities(searchText, Users.USER_TEST_SOURCE, SecurityEntityType.GROUP)
                    .stream()
                    .filter(s -> (s instanceof User
                            && ((User)s).getUid().startsWith("userTest"))
                            || (s instanceof Group
                            && ((Group)s).getGid().startsWith("groupTest")))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Exception of type " + e.getClass().getName());
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        }
        assertNotNull(secEntities);
        assertEquals(4, secEntities.size());
        // let's see the users in the list
        hashSecEntities = new HashMap<String, SecurityEntity>();
        for (SecurityEntity secEntity : secEntities) {
            hashSecEntities.put(secEntity.getID(), secEntity);
        }
        groupTest1 = (Group)hashSecEntities.get("groupTest1");
        groupTest2 = (Group)hashSecEntities.get("groupTest2");
        groupTest3 = (Group)hashSecEntities.get("groupTest3");
        groupTest4 = (Group)hashSecEntities.get("groupTest4");
        assertNotNull(groupTest1);
        assertEquals(GROUP_TEST_1, groupTest1.getName());
        assertNotNull(groupTest2);
        assertEquals(GROUP_TEST_2, groupTest2.getName());
        assertNotNull(groupTest3);
        assertEquals(GROUP_TEST_3, groupTest3.getName());
        assertNotNull(groupTest4);
        assertEquals(GROUP_TEST_4, groupTest4.getName());
    }

    @Test
    public void testSearchSecurityEntities() {
        String searchText = "John";
        List<SecurityEntity> secEntities = null;
        try {
            secEntities = this.administrationController.searchSecurityEntities(searchText, Users.USER_TEST_SOURCE, 0);
        } catch (Exception e) {
            System.out.println("Exception of type " + e.getClass().getName());
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        }
        assertNotNull(secEntities);
        assertEquals(5, secEntities.size());

        // let's see what's in the list
        HashMap<String, SecurityEntity> hashSecEntities = new HashMap<String, SecurityEntity>();
        for (SecurityEntity secEntity : secEntities) {
            hashSecEntities.put(secEntity.getID(), secEntity);
        }
        User userTest1 = (User)hashSecEntities.get("userTest1");
        User userTest2 = (User)hashSecEntities.get("userTest2");
        User userTest3 = (User)hashSecEntities.get("userTest3");
        assertNotNull(userTest1);
        assertEquals("John", userTest1.getFirstName());
        assertNotNull(userTest2);
        assertEquals("Johnny", userTest2.getFirstName());
        assertNotNull(userTest3);
        assertEquals("Johnny", userTest3.getFirstName());
        Group groupTest1 = (Group)hashSecEntities.get("groupTest1");
        Group groupTest4 = (Group)hashSecEntities.get("groupTest4");
        assertNotNull(groupTest1);
        assertEquals(GROUP_TEST_1, groupTest1.getName());
        assertNotNull(groupTest4);
        assertEquals(GROUP_TEST_4, groupTest4.getName());

        searchText = "johnny";
        // reset
        secEntities = null;
        try {
            secEntities = this.administrationController.searchSecurityEntities(searchText, Users.USER_TEST_SOURCE, 0);
        } catch (Exception e) {
            System.out.println("Exception of type " + e.getClass().getName());
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        }
        assertNotNull(secEntities);
        assertEquals(3, secEntities.size());
        // let's see the users in the list
        hashSecEntities = new HashMap<String, SecurityEntity>();
        for (SecurityEntity secEntity : secEntities) {
            hashSecEntities.put(secEntity.getID(), secEntity);
        }

        userTest2 = (User)hashSecEntities.get("userTest2");
        userTest3 = (User)hashSecEntities.get("userTest3");
        assertNotNull(userTest2);
        assertEquals("Johnny", userTest2.getFirstName());
        assertEquals("Smooth", userTest2.getLastName());
        assertNotNull(userTest3);
        assertEquals("Johnny", userTest3.getFirstName());
        assertEquals("Cash", userTest3.getLastName());
        groupTest1 = (Group)hashSecEntities.get("groupTest1");
        assertNotNull(groupTest1);
        assertEquals(GROUP_TEST_1, groupTest1.getName());


        searchText = "teclib";
        // reset
        secEntities = null;
        try {
            secEntities = this.administrationController
                    .searchSecurityEntities(searchText, Users.USER_TEST_SOURCE, 0)
                    .stream()
                    .filter(s -> (s instanceof User
                            && ((User)s).getUid().startsWith("userTest"))
                            || (s instanceof Group
                            && ((Group)s).getGid().startsWith("groupTest")))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Exception of type " + e.getClass().getName());
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        }
        assertNotNull(secEntities);
        assertEquals(3, secEntities.size());
        // let's see the users in the list
        hashSecEntities = new HashMap<String, SecurityEntity>();
        for (SecurityEntity secEntity : secEntities) {
            hashSecEntities.put(secEntity.getID(), secEntity);
        }

        userTest1 = (User)hashSecEntities.get("userTest1");
        userTest2 = (User)hashSecEntities.get("userTest2");
        assertNotNull(userTest1);
        assertEquals("John", userTest1.getFirstName());
        assertEquals("Smith", userTest1.getLastName());
        assertNotNull(userTest2);
        assertEquals("Johnny", userTest2.getFirstName());
        assertEquals("Smooth", userTest2.getLastName());
        Group groupTest2 = (Group)hashSecEntities.get("groupTest2");
        assertNotNull(groupTest2);
        assertEquals(GROUP_TEST_2, groupTest2.getName());


        searchText = "cash";
        // reset
        secEntities = null;
        try {
            secEntities = this.administrationController
                    .searchSecurityEntities(searchText, Users.USER_TEST_SOURCE, 0)
                    .stream()
                    .filter(s -> (s instanceof User
                            && ((User)s).getUid().startsWith("userTest"))
                            || (s instanceof Group
                            && ((Group)s).getGid().startsWith("groupTest")))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Exception of type " + e.getClass().getName());
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        }
        assertNotNull(secEntities);
        assertEquals(1, secEntities.size());
        // let's see the users in the list
        hashSecEntities = new HashMap<String, SecurityEntity>();
        for (SecurityEntity secEntity : secEntities) {
            hashSecEntities.put(secEntity.getID(), secEntity);
        }

        userTest3 = (User)hashSecEntities.get("userTest3");
        assertNotNull(userTest3);
        assertEquals("Johnny", userTest3.getFirstName());
        assertEquals("Cash", userTest3.getLastName());


        searchText = "test";
        // reset
        secEntities = null;
        try {
            secEntities = this.administrationController
                    .searchSecurityEntities(searchText, Users.USER_TEST_SOURCE, 0)
                    .stream()
                    .filter(s -> (s instanceof User
                            && ((User)s).getUid().startsWith("userTest"))
                            || (s instanceof Group
                            && ((Group)s).getGid().startsWith("groupTest")))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Exception of type " + e.getClass().getName());
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        }
        assertNotNull(secEntities);
        assertEquals(9, secEntities.size());
        // let's see the users in the list
        hashSecEntities = new HashMap<String, SecurityEntity>();
        for (SecurityEntity secEntity : secEntities) {
            hashSecEntities.put(secEntity.getID(), secEntity);
        }
        userTest1 = (User)hashSecEntities.get("userTest1");
        userTest2 = (User)hashSecEntities.get("userTest2");
        userTest3 = (User)hashSecEntities.get("userTest3");
        User userTest4 = (User)hashSecEntities.get("userTest4");
        User userTest5 = (User)hashSecEntities.get("userTest5");
        assertNotNull(userTest1);
        assertNotNull(userTest2);
        assertNotNull(userTest3);
        assertNotNull(userTest4);
        assertNotNull(userTest5);
        groupTest1 = (Group)hashSecEntities.get("groupTest1");
        groupTest2 = (Group)hashSecEntities.get("groupTest2");
        Group groupTest3 = (Group)hashSecEntities.get("groupTest3");
        groupTest4 = (Group)hashSecEntities.get("groupTest4");
        assertNotNull(groupTest1);
        assertEquals(GROUP_TEST_1, groupTest1.getName());
        assertNotNull(groupTest2);
        assertEquals(GROUP_TEST_2, groupTest2.getName());
        assertNotNull(groupTest3);
        assertEquals(GROUP_TEST_3, groupTest3.getName());
        assertNotNull(groupTest4);
        assertEquals(GROUP_TEST_4, groupTest4.getName());
    }

    @Test
    public void testSearchSecurityEntitiesInAllDomains() {
        String searchText = "John";
        List<SecurityEntity> secEntities = null;
        try {
            secEntities = this.administrationController.searchSecurityEntities(searchText, null, 0);
        } catch (Exception e) {
            System.out.println("Exception of type " + e.getClass().getName());
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        }
        assertNotNull(secEntities);
        assertEquals(9, secEntities.size());

        // let's see what's in the list
        HashMap<String, HashMap<String, SecurityEntity>> hashSecEntities = new HashMap<String, HashMap<String, SecurityEntity>>();
        for (SecurityEntity secEntity : secEntities) {
            if (hashSecEntities.get(secEntity.getAuthenticationSourceName()) == null) {
                hashSecEntities.put(secEntity.getAuthenticationSourceName(), new HashMap<String, SecurityEntity>());
            }
            hashSecEntities.get(secEntity.getAuthenticationSourceName()).put(secEntity.getID(), secEntity);
        }
        User userTest1 = (User)(hashSecEntities.get(Users.USER_TEST_SOURCE).get("userTest1"));
        User userTest2 = (User)(hashSecEntities.get(Users.USER_TEST_SOURCE).get("userTest2"));
        User userTest3 = (User)(hashSecEntities.get(Users.USER_TEST_SOURCE).get("userTest3"));
        assertNotNull(userTest1);
        assertEquals("John", userTest1.getFirstName());
        assertNotNull(userTest2);
        assertEquals("Johnny", userTest2.getFirstName());
        assertNotNull(userTest3);
        assertEquals("Johnny", userTest3.getFirstName());
        Group groupTest1 = (Group)(hashSecEntities.get(Users.USER_TEST_SOURCE).get("groupTest1"));
        Group groupTest4 = (Group)(hashSecEntities.get(Users.USER_TEST_SOURCE).get("groupTest4"));
        assertNotNull(groupTest1);
        assertEquals(GROUP_TEST_1, groupTest1.getName());
        assertNotNull(groupTest4);
        assertEquals(GROUP_TEST_4, groupTest4.getName());



        searchText = "johnny";
        // reset
        secEntities = null;
        try {
            secEntities = this.administrationController
                    .searchSecurityEntities(searchText, null, 0)
                    .stream()
                    .filter(s -> (s instanceof User
                            && ((User)s).getUid().startsWith("userTest"))
                            || (s instanceof Group
                            && ((Group)s).getGid().startsWith("groupTest")))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Exception of type " + e.getClass().getName());
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        }
        assertNotNull(secEntities);
        assertEquals(5, secEntities.size());
        // let's see the users in the list
        hashSecEntities = new HashMap<String, HashMap<String, SecurityEntity>>();
        for (SecurityEntity secEntity : secEntities) {
            if (hashSecEntities.get(secEntity.getAuthenticationSourceName()) == null) {
                hashSecEntities.put(secEntity.getAuthenticationSourceName(), new HashMap<String, SecurityEntity>());
            }
            hashSecEntities.get(secEntity.getAuthenticationSourceName()).put(secEntity.getID(), secEntity);
        }

        userTest2 = (User)(hashSecEntities.get(Users.USER_TEST_SOURCE).get("userTest2"));
        userTest3 = (User)(hashSecEntities.get(Users.USER_TEST_SOURCE).get("userTest3"));
        assertNotNull(userTest2);
        assertEquals("Johnny", userTest2.getFirstName());
        assertEquals("Smooth", userTest2.getLastName());
        assertNotNull(userTest3);
        assertEquals("Johnny", userTest3.getFirstName());
        assertEquals("Cash", userTest3.getLastName());
        groupTest1 = (Group)(hashSecEntities.get(Users.USER_TEST_SOURCE).get("groupTest1"));
        assertNotNull(groupTest1);
        assertEquals(GROUP_TEST_1, groupTest1.getName());


        searchText = "teclib";
        // reset
        secEntities = null;
        try {
            secEntities = this.administrationController
                    .searchSecurityEntities(searchText, null, 0)
                    .stream()
                    .filter(s -> (s instanceof User
                            && ((User)s).getUid().startsWith("userTest"))
                            || (s instanceof Group
                            && ((Group)s).getGid().startsWith("groupTest")))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Exception of type " + e.getClass().getName());
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        }
        assertNotNull(secEntities);
        assertEquals(6, secEntities.size());
        // let's see the users in the list
        hashSecEntities = new HashMap<String, HashMap<String, SecurityEntity>>();
        for (SecurityEntity secEntity : secEntities) {
            if (hashSecEntities.get(secEntity.getAuthenticationSourceName()) == null) {
                hashSecEntities.put(secEntity.getAuthenticationSourceName(), new HashMap<String, SecurityEntity>());
            }
            hashSecEntities.get(secEntity.getAuthenticationSourceName()).put(secEntity.getID(), secEntity);
        }

        userTest1 = (User)(hashSecEntities.get(Users.USER_TEST_SOURCE).get("userTest1"));
        userTest2 = (User)(hashSecEntities.get(Users.USER_TEST_SOURCE).get("userTest2"));
        assertNotNull(userTest1);
        assertEquals("John", userTest1.getFirstName());
        assertEquals("Smith", userTest1.getLastName());
        assertNotNull(userTest2);
        assertEquals("Johnny", userTest2.getFirstName());
        assertEquals("Smooth", userTest2.getLastName());
        Group groupTest2 = (Group)(hashSecEntities.get(Users.USER_TEST_SOURCE).get("groupTest2"));
        assertNotNull(groupTest2);
        assertEquals(GROUP_TEST_2, groupTest2.getName());


        searchText = "cash";
        // reset
        secEntities = null;
        try {
            secEntities = this.administrationController.searchSecurityEntities(searchText, null, 0);
        } catch (Exception e) {
            System.out.println("Exception of type " + e.getClass().getName());
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        }
        assertNotNull(secEntities);
        assertEquals(1, secEntities.size());
        // let's see the users in the list
        hashSecEntities = new HashMap<String, HashMap<String, SecurityEntity>>();
        for (SecurityEntity secEntity : secEntities) {
            if (hashSecEntities.get(secEntity.getAuthenticationSourceName()) == null) {
                hashSecEntities.put(secEntity.getAuthenticationSourceName(), new HashMap<String, SecurityEntity>());
            }
            hashSecEntities.get(secEntity.getAuthenticationSourceName()).put(secEntity.getID(), secEntity);
        }

        userTest3 = (User)(hashSecEntities.get(Users.USER_TEST_SOURCE).get("userTest3"));
        assertNotNull(userTest3);
        assertEquals("Johnny", userTest3.getFirstName());
        assertEquals("Cash", userTest3.getLastName());


        searchText = "test";
        // reset
        secEntities = null;
        try {
            secEntities = this.administrationController.searchSecurityEntities(searchText, null, 0);
        } catch (Exception e) {
            System.out.println("Exception of type " + e.getClass().getName());
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        }
        assertNotNull(secEntities);
        assertEquals(18, secEntities.size());
        // let's see the users in the list
        hashSecEntities = new HashMap<String, HashMap<String, SecurityEntity>>();
        for (SecurityEntity secEntity : secEntities) {
            if (hashSecEntities.get(secEntity.getAuthenticationSourceName()) == null) {
                hashSecEntities.put(secEntity.getAuthenticationSourceName(), new HashMap<String, SecurityEntity>());
            }
            hashSecEntities.get(secEntity.getAuthenticationSourceName()).put(secEntity.getID(), secEntity);
        }
        userTest1 = (User)(hashSecEntities.get(Users.USER_TEST_SOURCE).get("userTest1"));
        userTest2 = (User)(hashSecEntities.get(Users.USER_TEST_SOURCE).get("userTest2"));
        userTest3 = (User)(hashSecEntities.get(Users.USER_TEST_SOURCE).get("userTest3"));
        User userTest4 = (User)(hashSecEntities.get(Users.USER_TEST_SOURCE).get("userTest4"));
        User userTest5 = (User)(hashSecEntities.get(Users.USER_TEST_SOURCE).get("userTest5"));
        assertNotNull(userTest1);
        assertNotNull(userTest2);
        assertNotNull(userTest3);
        assertNotNull(userTest4);
        assertNotNull(userTest5);
        groupTest1 = (Group)(hashSecEntities.get(Users.USER_TEST_SOURCE).get("groupTest1"));
        groupTest2 = (Group)(hashSecEntities.get(Users.USER_TEST_SOURCE).get("groupTest2"));
        Group groupTest3 = (Group)(hashSecEntities.get(Users.USER_TEST_SOURCE).get("groupTest3"));
        groupTest4 = (Group)(hashSecEntities.get(Users.USER_TEST_SOURCE).get("groupTest4"));
        assertNotNull(groupTest1);
        assertEquals(GROUP_TEST_1, groupTest1.getName());
        assertNotNull(groupTest2);
        assertEquals(GROUP_TEST_2, groupTest2.getName());
        assertNotNull(groupTest3);
        assertEquals(GROUP_TEST_3, groupTest3.getName());
        assertNotNull(groupTest4);
        assertEquals(GROUP_TEST_4, groupTest4.getName());
    }

    @After
    public void tearDown() {
        if (this.getAdminSession() != null) {
            String[] usersIdsToDelete = {
                    "userTest1",
                    "userTest2",
                    "userTest3",
                    "userTest4",
                    "userTest5"
            };
            for (String userId : usersIdsToDelete) {
                this.administrationController.deleteUser(this.getAdminSession(), userId, Users.USER_TEST_SOURCE);
                this.administrationController.deleteUser(this.getAdminSession(), userId, USER_TEST_SOURCE_2);
            }

            String[] groupsIdsToDelete = {
                    "groupTest1",
                    "groupTest2",
                    "groupTest3",
                    "groupTest4"
            };
            for (String groupId : groupsIdsToDelete) {
                this.administrationController.deleteGroup(this.getAdminSession(), groupId, Users.USER_TEST_SOURCE);
                this.administrationController.deleteGroup(this.getAdminSession(), groupId, USER_TEST_SOURCE_2);
            }

            this.administrationController.deleteAuthenticationSource(this.getAdminSession(), USER_TEST_SOURCE_2);
        }
    }
}
