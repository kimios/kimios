/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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

package org.kimios.kernel.bonita;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.LoginAPI;
import org.bonitasoft.engine.api.ProfileAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.exception.*;
import org.bonitasoft.engine.identity.*;
import org.bonitasoft.engine.profile.Profile;
import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.SearchResult;
import org.bonitasoft.engine.search.impl.SearchOptionsImpl;
import org.bonitasoft.engine.session.APISession;
import org.kimios.kernel.bonita.interfaces.IBonitaUsersSynchronizer;
import org.kimios.kernel.user.*;
import org.kimios.kernel.user.Group;
import org.kimios.kernel.user.User;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class BonitaUsersSynchronizer implements IBonitaUsersSynchronizer {

    private BonitaSettings bonitaCfg;

    private static Logger log = LoggerFactory.getLogger(BonitaUsersSynchronizer.class);

    @Override
    @Transactional
    public void synchronize() throws JobExecutionException {
        try {

            if(!bonitaCfg.isBonitaEnabled()){
                log.error("Bonita Link is Disabled. Sync job is now disabled until next Kimios restart.");
                throw new Exception("BonitaDisabled");

            }

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

        } catch (Exception ex) {
            log.error("error while sync with Bonita", ex);
            JobExecutionException jobExecutionException = new JobExecutionException(ex);
                 /*
                    Disable job
                 */
            jobExecutionException.setUnscheduleAllTriggers(true);
            log.error("Bonita is unavailable. Sync job is now disabled until next Kimios restart.");
            throw jobExecutionException;

        }
    }

    public BonitaSettings getBonitaCfg() {
        return bonitaCfg;
    }

    public void setBonitaCfg(BonitaSettings bonitaCfg) {
        this.bonitaCfg = bonitaCfg;
    }
}