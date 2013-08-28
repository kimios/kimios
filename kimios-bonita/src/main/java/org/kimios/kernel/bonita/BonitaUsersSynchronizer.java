package org.kimios.kernel.bonita;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.LoginAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.exception.*;
import org.bonitasoft.engine.identity.*;
import org.bonitasoft.engine.platform.LoginException;
import org.bonitasoft.engine.session.APISession;
import org.kimios.kernel.user.*;
import org.kimios.kernel.user.Group;
import org.kimios.kernel.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class BonitaUsersSynchronizer {

    private String bonitaUserName;
    private String bonitaUserPassword;
    private String bonitaHome;
    private String bonitaApplicationName;
    private String bonitaServerUrl;
    private List<String> validDomainsToSynchronize;

    private static Logger log = LoggerFactory.getLogger(BonitaUsersSynchronizer.class);

    public void synchronize() throws BonitaHomeNotSetException, ServerAPIException, UnknownAPITypeException, LoginException, CreationException, IOException, UpdateException {
        initializeBonitaClientConfiguration();
        LoginAPI loginAPI = TenantAPIAccessor.getLoginAPI();
        APISession session = loginAPI.login(bonitaUserName, bonitaUserPassword);
        IdentityAPI identityAPI = TenantAPIAccessor.getIdentityAPI(session);

        for (String domainName : validDomainsToSynchronize) {

            AuthenticationSource source = FactoryInstantiator.getInstance().getAuthenticationSourceFactory().getAuthenticationSource(domainName);

            // for each users
            log.info("Users synchronisation...");
            UserFactory userFactory = source.getUserFactory();
            List<User> users = userFactory.getUsers();
            for (User u : users) {
                String userName = u.getUid() + "@" + u.getAuthenticationSourceName();

                try {
                    // check existing user
                    org.bonitasoft.engine.identity.User bUser = identityAPI.getUserByUserName(userName);
                    log.info("User " + userName + " already exists, update...");
                    UserUpdater userUpdater = new UserUpdater();
                    userUpdater.setFirstName(u.getName());
                    userUpdater.setLastName(u.getName());
                    ContactDataUpdater contact = new ContactDataUpdater();
                    contact.setEmail(u.getMail());
                    userUpdater.setPersonalContactData(contact);
                    identityAPI.updateUser(bUser.getId(), userUpdater);

                } catch (UserNotFoundException unfe) {
                    // if not exists
                    log.info("User " + userName + " not found, create...");
                    identityAPI.createUser(userName, u.getPassword(), u.getName(), u.getName());

                }
            }

            // for each groups
            log.info("Groups synchronisation...");
            GroupFactory groupFactory = source.getGroupFactory();
            List<Group> groups = groupFactory.getGroups();
            for (Group g : groups) {
                String groupName = g.getGid() + "@" + g.getAuthenticationSourceName();

                try {
                    // check existing group
                    org.bonitasoft.engine.identity.Group bGroup = identityAPI.getGroupByPath("/" + groupName);
                    log.info("Group " + groupName + " already exists, ignored");
                    GroupUpdater groupUpdater = new GroupUpdater();
                    groupUpdater.updateName(groupName);
                    groupUpdater.updateDisplayName(groupName);
                    groupUpdater.updateDescription(g.getName());
                    identityAPI.updateGroup(bGroup.getId(), groupUpdater);

                } catch (GroupNotFoundException e) {
                    // if not exists
                    log.info("Group " + groupName + " not found, it will be created...");
                    identityAPI.createGroup(groupName, "/");
                }
            }
        }

        log.info("Synchronisation done.");
    }

    private void initializeBonitaClientConfiguration() throws IOException {
        File homeFolder = null;
        if (System.getProperty("bonita.home") == null) {
            // create a bonita home that is for application 'myClientExample' and on localhost:8080
            homeFolder = new File(bonitaHome);
            homeFolder.mkdirs();
            File file = new File(homeFolder, "client");
            file.mkdir();
            file = new File(file, "conf");
            file.mkdir();
            file = new File(file, "bonita-client.properties");
            file.createNewFile();
            final Properties properties = new Properties();
            properties.put("application.name", bonitaApplicationName);
            properties.put("org.bonitasoft.engine.api-type", "HTTP");
            properties.put("server.url", bonitaServerUrl);
            properties.put("org.bonitasoft.engine.api-type.parameters", "server.url,application.name");

            final FileWriter writer = new FileWriter(file);
            try {
                properties.store(writer, "Server configuration");
            } finally {
                writer.close();
            }
            System.out.println("Using server configuration " + properties);
            System.setProperty("bonita.home", homeFolder.getAbsolutePath());
        }
    }

    public String getBonitaUserName() {
        return bonitaUserName;
    }

    public void setBonitaUserName(String bonitaUserName) {
        this.bonitaUserName = bonitaUserName;
    }

    public String getBonitaUserPassword() {
        return bonitaUserPassword;
    }

    public void setBonitaUserPassword(String bonitaUserPassword) {
        this.bonitaUserPassword = bonitaUserPassword;
    }

    public String getBonitaHome() {
        return bonitaHome;
    }

    public void setBonitaHome(String bonitaHome) {
        this.bonitaHome = bonitaHome;
    }

    public String getBonitaApplicationName() {
        return bonitaApplicationName;
    }

    public void setBonitaApplicationName(String bonitaApplicationName) {
        this.bonitaApplicationName = bonitaApplicationName;
    }

    public String getBonitaServerUrl() {
        return bonitaServerUrl;
    }

    public void setBonitaServerUrl(String bonitaServerUrl) {
        this.bonitaServerUrl = bonitaServerUrl;
    }

    public List<String> getValidDomainsToSynchronize() {
        return validDomainsToSynchronize;
    }

    public void setValidDomainsToSynchronize(List<String> domainsToSynchronize) {
        this.validDomainsToSynchronize = domainsToSynchronize;
    }

}