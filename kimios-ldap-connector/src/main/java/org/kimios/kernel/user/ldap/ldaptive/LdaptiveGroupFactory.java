/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2017  DevLib'
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

package org.kimios.kernel.user.ldap.ldaptive;

import org.kimios.exceptions.ConfigException;
import org.kimios.exceptions.DataSourceException;
import org.kimios.kernel.security.model.SecurityEntityType;
import org.kimios.kernel.user.ldap.LdaptiveImpl;
import org.kimios.kernel.user.model.Group;
import org.kimios.kernel.user.model.GroupFactory;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class LdaptiveGroupFactory extends LdaptiveFactory implements GroupFactory {

    private static Logger logger = LoggerFactory.getLogger(LdaptiveGroupFactory.class);

    public LdaptiveGroupFactory(LdaptiveImpl ldaptive) {
        this.source = ldaptive;
    }

    @Override
    public Group getGroup(String gid) throws DataSourceException, ConfigException {
        try {
            Collection<LdapEntry> r = this.search(
                    "(&(objectClass=" + source.getGroupsObjectClassValue() + ")(" + source.getGroupsIdKey() + "=" +
                            gid + "))",
                    SecurityEntityType.GROUP);
            if (!r.isEmpty() && r.size() == 1) {
                LdapEntry entry = r.iterator().next();
                return new Group(gid, entry.getAttribute(source.getGroupsDescriptionKeyn()).getStringValue(), source.getName());
            } else {
                return null;
            }
        } catch (LdapException e){
            throw new DataSourceException(e, "ldap exception");
        }
    }

    public Vector<Group> getGroups() throws ConfigException, DataSourceException
    {
        try {
            Vector<Group> v = new Vector<Group>();
            Collection<LdapEntry> r = this.search("(&(objectClass=" + source.getGroupsObjectClassValue() + "))",
                    SecurityEntityType.GROUP);
            for (LdapEntry sr : r) {
                v.add(buildGroupInstance(sr));
            }
            return v;
        } catch (LdapException e) {
            throw new DataSourceException(e, "LDAP connection failed, please check your settings");
        }
    }

    protected Group buildGroupInstance(LdapEntry entry){
        return new Group(entry.getAttribute(source.getGroupsIdKey()).getStringValue(),
                entry.getAttribute(source.getGroupsDescriptionKeyn()).getStringValue(),
                source.getName());
    }


    @Override
    public Vector<Group> getGroups(String userUid) throws DataSourceException, ConfigException {
        try {
            Vector<Group> v = new Vector<Group>();
            Collection<LdapEntry> r = null;
            List<String> nodes = null;
            if (source.isActiveDirectory()) {
                /*

                    Load user
                 */
                LdaptiveUserFactory userFactory = (LdaptiveUserFactory) this.source.getUserFactory();
                String distinguishedName = userFactory.getLDAPAttribute(userUid, "distinguishedName");
                if (source.getGroupsDn() != null) {
                    if (source.getGroupsDn().indexOf(':') != -1) {
                        String[] ns = source.getGroupsDn().split(":");
                        nodes = new ArrayList<String>();
                        for (int i = 0; i < ns.length; i++) {
                            nodes.add(ns[i]);
                        }
                    }
                }
                logger.debug("Will get throug group query with nodes ? " + nodes);
                if (nodes == null) {
                    if (this.source.getUsersDn() == null || this.source.getUsersDn().length() == 0) {
                        logger.debug("Old ad query mode... because of " + this.source.getUsersDn());
                        v = getGroupsAD(userUid);
                    } else {

                        logger.debug("looking for Active Directory group for user " + distinguishedName );
                        r = this.search("(&(objectClass=" + source.getGroupsObjectClassValue() + ")(" +
                                source.getGroupsMemberKey() + "=" + distinguishedName + "))", SecurityEntityType.GROUP);
                        logger.debug(" search result count " + r.size() );
                        for (LdapEntry entry : r) {
                            v.add(new Group(entry.getAttribute(source.getGroupsIdKey()).getStringValue(),
                                    entry.getAttribute(source.getGroupsDescriptionKeyn()).getStringValue(), source.getName()));
                        }
                    }
                } else {
                    /*
                         Load use
                     */
                    logger.debug(" global mode ");
                    String groupQuery =  "(&(objectClass=" + source.getGroupsObjectClassValue() + ")(" +
                            source.getGroupsMemberKey()
                            + "=" + distinguishedName + "))";


                    logger.debug("looking for group for " + distinguishedName + " with query: " + groupQuery);
                    Collection<LdapEntry> lst = this.search(
                            groupQuery , SecurityEntityType.GROUP);

                    logger.debug("found result for group for " + distinguishedName + " " + lst.size());
                    for (LdapEntry sr : lst) {
                        v.add(new Group(sr.getAttribute(source.getGroupsIdKey()).getStringValue(),
                                sr.getAttribute(source.getGroupsDescriptionKeyn()).getStringValue(), source.getName()));
                    }
                }
            } else {
                if (this.source.getSchemaFullCnUserGroupMatching().equals("true")) {

                    String searchQuery =
                            this.source.getUsersIdKey() + "=" + userUid + "," + this.source.getUsersDn();
                    r = this.search("(&(objectClass=" + source.getGroupsObjectClassValue() + ")(" +
                                    source.getGroupsMemberKey() + "=" + searchQuery + "))",
                            SecurityEntityType.GROUP);
                } else {
                    r = this.search("(&(objectClass=" + source.getGroupsObjectClassValue() + ")(" +
                                    source.getGroupsMemberKey() + "=" + userUid + "))",
                            SecurityEntityType.GROUP);
                }
                for (LdapEntry sr : r) {
                    v.add(new Group(sr.getAttribute(source.getGroupsIdKey()).getStringValue(),
                            sr.getAttribute(source.getGroupsDescriptionKeyn()).getStringValue(), source.getName()));
                }
            }

            return v;
        } catch (LdapException e) {
            throw new DataSourceException(e, "ldap exception");
        }
    }

    private Vector<Group> getGroupsAD(String userId) throws LdapException
    {

        Vector<Group> v = new Vector<Group>();

        Collection<LdapEntry> r = this.search("(&(objectClass=" + this.source.getUsersObjectClassValue() + ")("
                + this.source.getUsersIdKey() + "=" + userId + "))", SecurityEntityType.USER, Arrays.asList("distinguishedName"));

        LdapEntry rUser = r.iterator().next();

        Collection<LdapEntry> rGroups = this.search("(&(objectClass=" + this.source.getGroupsObjectClassValue() + ")("
                + this.source.getGroupsMemberKey() + "=" +
                rUser.getAttribute("distinguishedName").getStringValue() + "))", SecurityEntityType.GROUP);

        for (LdapEntry sr : rGroups) {
            v.add(new Group(sr.getAttribute(this.source.getGroupsIdKey()).getStringValue(),
                    sr.getAttribute(this.source.getGroupsDescriptionKeyn()).getStringValue(), source.getName()));
        }
        return v;
    }

    @Override
    public void saveGroup(Group group) throws DataSourceException, ConfigException {

    }

    @Override
    public void updateGroup(Group group) throws DataSourceException, ConfigException {

    }

    @Override
    public void deleteGroup(Group group) throws DataSourceException, ConfigException {

    }

    @Override
    public List<Group> searchGroups(String searchText, String sourceName) throws DataSourceException, ConfigException {
        return null;
    }
}
