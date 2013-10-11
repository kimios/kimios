package org.kimios.kernel.bonita;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.LoginAPI;
import org.bonitasoft.engine.api.ProfileAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.exception.*;
import org.bonitasoft.engine.identity.*;
import org.bonitasoft.engine.platform.LoginException;
import org.bonitasoft.engine.profile.Profile;
import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.SearchResult;
import org.bonitasoft.engine.search.impl.SearchOptionsImpl;
import org.bonitasoft.engine.session.APISession;
import org.kimios.kernel.user.*;
import org.kimios.kernel.user.Group;
import org.kimios.kernel.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class BonitaUsersSynchronizer {

    private BonitaSettings bonitaCfg;

    private static Logger log = LoggerFactory.getLogger(BonitaUsersSynchronizer.class);

    public void synchronize() throws BonitaHomeNotSetException, ServerAPIException, UnknownAPITypeException, LoginException, CreationException, IOException, UpdateException {

        bonitaCfg.init();

        LoginAPI loginAPI = TenantAPIAccessor.getLoginAPI();
        APISession session = loginAPI.login(bonitaCfg.getBonitaUserName(), bonitaCfg.getBonitaUserPassword());
        IdentityAPI identityAPI = TenantAPIAccessor.getIdentityAPI(session);
        ProfileAPI profileAPI = TenantAPIAccessor.getProfileAPI(session);

        Role role;
        try {
            role = identityAPI.getRoleByName(bonitaCfg.getBonitaKimiosRoleName());
        } catch (RoleNotFoundException e) {
            role = identityAPI.createRole(new RoleCreator(bonitaCfg.getBonitaKimiosRoleName()));
        }

        for (String domainName : bonitaCfg.getValidDomainsToSynchronize()) {
            AuthenticationSource source = FactoryInstantiator.getInstance().getAuthenticationSourceFactory().getAuthenticationSource(domainName);

            // groups synchronisation

            log.info("Groups synchronisation...");
            GroupFactory groupFactory = source.getGroupFactory();
            List<Group> groups = groupFactory.getGroups();
            for (Group g : groups) {
                String groupName = g.getGid() + "@" + g.getAuthenticationSourceName();
                org.bonitasoft.engine.identity.Group bGroup;
                try {
                    // check existing group
                    bGroup = identityAPI.getGroupByPath("/" + groupName);
                    log.info("Group " + groupName + " already exists, updating...");
                    GroupUpdater groupUpdater = new GroupUpdater();
                    groupUpdater.updateName(groupName);
                    groupUpdater.updateDisplayName(g.getName());
                    groupUpdater.updateDescription(g.getName());
                    bGroup = identityAPI.updateGroup(bGroup.getId(), groupUpdater);

                } catch (GroupNotFoundException e) {
                    // if not exists
                    log.info("Group " + groupName + " not found, creating...");
                    GroupCreator groupCreator = new GroupCreator(groupName);
                    groupCreator.setDisplayName(g.getName());
                    groupCreator.setDescription(g.getName());
                    groupCreator.setParentPath(null);
                    bGroup = identityAPI.createGroup(groupCreator);

                }
            }

            // users synchronisation

            log.info("Users synchronisation...");
            UserFactory userFactory = source.getUserFactory();
            List<User> users = userFactory.getUsers();
            for (User u : users) {
                String userName = u.getUid() + "@" + u.getAuthenticationSourceName();
                org.bonitasoft.engine.identity.User bUser;
                try {
                    // check existing user
                    bUser = identityAPI.getUserByUserName(userName);
                    log.info("User " + userName + " already exists, updating...");
                    UserUpdater userUpdater = new UserUpdater();
                    userUpdater.setFirstName(u.getFirstName());
                    userUpdater.setLastName(u.getLastName());
                    ContactDataUpdater contact = new ContactDataUpdater();
                    contact.setEmail(u.getMail());
                    contact.setPhoneNumber(u.getPhoneNumber());
                    userUpdater.setPersonalContactData(contact);
                    userUpdater.setProfessionalContactData(contact);
                    bUser = identityAPI.updateUser(bUser.getId(), userUpdater);

                } catch (UserNotFoundException unfe) {
                    // if not exists
                    log.info("User " + userName + " not found, creating...");
                    UserCreator userCreator = new UserCreator(userName, UUID.randomUUID().toString());
                    userCreator.setFirstName(u.getFirstName());
                    userCreator.setLastName(u.getLastName());
                    ContactDataCreator contact = new ContactDataCreator();
                    contact.setEmail(u.getMail());
                    contact.setPhoneNumber(u.getPhoneNumber());
                    userCreator.setPersonalContactData(contact);
                    userCreator.setProfessionalContactData(contact);
                    bUser = identityAPI.createUser(userCreator);

                }

                // Set profile

                SearchOptions opts = new SearchOptionsImpl(Integer.MIN_VALUE, Integer.MAX_VALUE);
                try {
                    SearchResult<Profile> result = profileAPI.searchProfiles(opts);
                    List<Profile> profiles = result.getResult();
                    for (Profile profile : profiles) {

                        if (profile.getName().equals(bonitaCfg.getBonitaProfileUsers())) {
                            log.info("Creating profile member...");
                            try {
                                profileAPI.createProfileMember(profile.getId(), bUser.getId(), new Long(-1), new Long(-1));
                                log.info("Profile member created for user: " + userName);
                            } catch (AlreadyExistsException aee) {
                                log.info("Profile already exists: " + aee.getMessage());

                            } catch (CreationException ce) {
                                log.info("Profile already exists: " + ce.getMessage());
                            }
                            break;
                        }
                    }

                } catch (SearchException e) {
                    e.printStackTrace();
                }

                // groups to user

                Collection<Group> linkedGroups = source.getGroupFactory().getGroups(u.getUid());
                for (Group g : linkedGroups) {
                    String groupName = g.getGid() + "@" + g.getAuthenticationSourceName();
                    org.bonitasoft.engine.identity.Group bGroup;
                    try {
                        // check existing group
                        bGroup = identityAPI.getGroupByPath("/" + groupName);
                        log.info("Group " + groupName + " already exists, updating...");
                        GroupUpdater groupUpdater = new GroupUpdater();
                        groupUpdater.updateName(groupName);
                        groupUpdater.updateDisplayName(g.getName());
                        groupUpdater.updateDescription(g.getName());
                        bGroup = identityAPI.updateGroup(bGroup.getId(), groupUpdater);

                    } catch (GroupNotFoundException e) {
                        // if not exists
                        log.info("Group " + groupName + " not found, creating...");
                        GroupCreator groupCreator = new GroupCreator(groupName);
                        groupCreator.setDisplayName(g.getName());
                        groupCreator.setDescription(g.getName());
                        groupCreator.setParentPath(null);
                        bGroup = identityAPI.createGroup(groupCreator);
                    }


                    // set group to user by creating user membership

                    boolean needCreate = true;
                    List<UserMembership> memberships = identityAPI.getUserMemberships(bUser.getId(), Integer.MIN_VALUE, Integer.MAX_VALUE, UserMembershipCriterion.ASSIGNED_DATE_ASC);
                    for (UserMembership membership : memberships) {
                        if (membership.getUserId() == bUser.getId() && membership.getGroupId() == bGroup.getId() && membership.getRoleId() == role.getId()) {
                            log.info("User Membership already exists: " + bUser.getUserName() + " - " + bGroup.getIconName() + " - " + role.getId());
                            needCreate = false;
                            break;
                        }
                    }

                    if (needCreate) {
                        identityAPI.addUserMembership(bUser.getId(), bGroup.getId(), role.getId());
                        log.info("Add user membership: " + bUser.getUserName() + " - " + bGroup.getIconName() + " - " + role.getId());
                    }
                }
            }
        }

        log.info("Synchronisation done.");
    }

    public BonitaSettings getBonitaCfg() {
        return bonitaCfg;
    }

    public void setBonitaCfg(BonitaSettings bonitaCfg) {
        this.bonitaCfg = bonitaCfg;
    }
}