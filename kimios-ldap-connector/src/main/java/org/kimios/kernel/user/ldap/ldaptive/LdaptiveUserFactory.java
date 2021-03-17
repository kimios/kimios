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

import org.apache.commons.lang.StringUtils;
import org.kimios.exceptions.ConfigException;
import org.kimios.exceptions.DataSourceException;
import org.kimios.kernel.security.model.SecurityEntityType;
import org.kimios.kernel.user.ldap.LdaptiveImpl;
import org.kimios.kernel.user.model.Group;
import org.kimios.kernel.user.model.User;
import org.kimios.kernel.user.model.UserFactory;
import org.ldaptive.Connection;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

;import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class LdaptiveUserFactory extends LdaptiveFactory implements UserFactory {


    private static Logger logger = LoggerFactory.getLogger(LdaptiveUserFactory.class);

    public LdaptiveUserFactory(LdaptiveImpl ldaptive){
        this.source = ldaptive;
    }

    @Override
    public boolean authenticate(String uid, String password) throws DataSourceException, ConfigException {
        Connection cn = null;
        try {
            cn = buildConnection(uid, password);
            cn.open();
            return cn.isOpen();
        } catch (LdapException e) {
            logger.error("ldap error while authentication " + e.getMessage());
            return false;
        } finally {
            cn.close();
        }
    }

    @Override
    public User getUser(String uid) throws DataSourceException, ConfigException {
        try {
            String s =
                    "(&(objectClass=" + source.getUsersObjectClassValue() + ")(" + source.getUsersIdKey() + "=" + uid +
                            "))";
            Collection<LdapEntry> r = this.search(s, SecurityEntityType.USER);
            if (!r.isEmpty() && r.size() == 1) {
                return newUser(r.iterator().next());
            } else {
                return null;
            }
        } catch (LdapException ex) {
            throw new DataSourceException(ex, "ldap exception");
        }
    }

    @Override
    public User getUserByEmail(String emailAddress) throws DataSourceException, ConfigException {
        try{
            String s = "(&(objectClass=" + source.getUsersObjectClassValue() + ")(" + source.getUsersMailKey() + "=" +
                    emailAddress + "))";
            Collection<LdapEntry> r = this.search(s, SecurityEntityType.USER);
            if (!r.isEmpty() && r.size() == 1) {
                return newUser(r.iterator().next());
            } else {
                return null;
            }
        } catch (LdapException ex) {
            throw new DataSourceException(ex, "ldap exception");
        }
    }

    public Vector<User> getUsers() throws DataSourceException, ConfigException
    {
        try {
            Vector<User> v = new Vector<User>();
            Collection<LdapEntry> r =
                    this.search("(objectClass=" + source.getUsersObjectClassValue() + ")", SecurityEntityType.USER);
            for (LdapEntry sr : r) {
                v.add(newUser(sr));
            }
            return v;
        } catch (LdapException ex){
            throw new DataSourceException(ex, ex.getMessage());
        }
    }

    @Override
    public void saveUser(User user, String password) throws DataSourceException, ConfigException {

    }

    @Override
    public void updateUser(User user, String password) throws DataSourceException, ConfigException {

    }

    @Override
    public void deleteUser(User user) throws DataSourceException, ConfigException {

    }

    @Override
    public void addUserToGroup(User user, Group group) throws DataSourceException, ConfigException {

    }

    @Override
    public void removeUserFromGroup(User user, Group group) throws DataSourceException, ConfigException {

    }

    public String getLDAPAttribute(String userId, String attributeName)
            throws DataSourceException, ConfigException
    {
        try {
            String s = "(&(objectClass=" + source.getUsersObjectClassValue() + ")(" + source.getUsersIdKey() + "=" +
                    userId + "))";

            Collection<LdapEntry> r = this.search(s, SecurityEntityType.USER, Arrays.asList(new String[]{attributeName}));
            if (!r.isEmpty() && r.size() == 1) {
                LdapEntry e = r.iterator().next();
                String attributeValue = e.getAttribute(attributeName).getStringValue();
                return attributeValue;
            } else {
                return null;
            }
        } catch (LdapException e){
            throw new DataSourceException(e, "ldap exception");
        }
    }

    @Override
    public Object getAttribute(User user, String attributeName) throws DataSourceException, ConfigException {
        return null;
    }

    @Override
    public void setAttribute(User user, String attributeName, Object attributeValue) throws DataSourceException, ConfigException {

    }

    @Override
    public Map<String, String> getAttributes(User user) throws DataSourceException, ConfigException {
        return null;
    }

    @Override
    public User getUserByAttributeValue(String attributeName, String attributeValue) throws DataSourceException, ConfigException {
        return null;
    }

    @Override
    public void addUserEmails(String uid, List<String> emails) {

    }

    @Override
    public List<User> searchUsers(String searchText, String sourceName) {
        return null;
    }

    private User newUser(LdapEntry entry) throws LdapException
    {
        User user = new User();
        Collection<LdapAttribute> attrs = entry.getAttributes();
        if (entry.getAttribute(source.getUsersIdKey()) != null) {
            user.setUid(entry.getAttribute(source.getUsersIdKey()).getStringValue());
        }
        if (entry.getAttribute(source.getUsersDescriptionKey()) != null) {
            user.setName(entry.getAttribute(source.getUsersDescriptionKey()).getStringValue());
        }
        if (entry.getAttribute(source.getUsersMailKey()) != null) {
            user.setMail(entry.getAttribute(source.getUsersMailKey()).getStringValue());
            user.setMail(user.getMail() == null || user.getMail().equals("null") ? "" : user.getMail());
        }
        if(entry.getAttribute(source.getUserFirstNameKey()) != null){
            user.setFirstName(entry.getAttribute(source.getUserFirstNameKey()).getStringValue());
            user.setFirstName(user.getFirstName() == null || user.getFirstName().equals("null") ? "" : user.getFirstName());
        }
        if(entry.getAttribute(source.getUserLastNameKey()) != null){
            user.setLastName(entry.getAttribute(source.getUserLastNameKey()).getStringValue());
            user.setLastName(user.getLastName() == null || user.getLastName().equals("null") ? "" : user.getLastName());

        }
        if(entry.getAttribute(source.getUserPhoneKey()) != null){
            user.setPhoneNumber(entry.getAttribute(source.getUserPhoneKey()).getStringValue());
            user.setPhoneNumber(user.getPhoneNumber() == null || user.getPhoneNumber().equals("null") ? "" : user.getPhoneNumber());
        }
        if(StringUtils.isNotBlank(source.getUsersMailKey()) && entry.getAttribute(source.getUsersMailKey()) != null){

            Set<String> addonsEmail = new HashSet<String>();
            try{
                LdapAttribute ae = entry.getAttribute(source.getUsersMailKey());
                for(String value: ae.getStringValues()){
                    //load each email
                    if(StringUtils.isNotBlank(value)){
                        addonsEmail.add(value);
                    }
                }
            }   catch (Exception ex){
                logger.error("error while loading addon email from ldap", ex);
            }

            user.setEmails(addonsEmail);
            if(StringUtils.isNotBlank(user.getMail()))
                user.getEmails().add(user.getMail());

        }
        user.setAuthenticationSourceName(this.source.getName());
        return user;
    }

    @Override
    public Vector<User> getUsers(Group group) throws DataSourceException, ConfigException {
        try {
            Vector<User> v = new Vector<User>();
            Collection<LdapEntry> r = this.search(
                    "(&(objectClass=" + source.getGroupsObjectClassValue() + ")(" + source.getGroupsIdKey() + "=" +
                            group.getGid()
                            + "))", SecurityEntityType.GROUP);
            for (LdapEntry sr : r) {
                LdapAttribute attr = sr.getAttribute(source.getGroupsMemberKey());
                if (attr != null) {
                    for (String groupCn: attr.getStringValues()) {
                        String search = null;
                        if (source.isActiveDirectory()) {
                            search = "(&(objectClass=" + source.getUsersObjectClassValue() + ")(distinguishedName=" +
                                    groupCn + "))";
                        } else {

                            if (this.source.getSchemaFullCnUserGroupMatching().equals("true")) {
                                search = "(&(objectClass=" + source.getUsersObjectClassValue() + ")(" + groupCn.substring(0,
                                        (groupCn.indexOf(","))
                                ) + "))";
                            } else {
                                search = "(&(objectClass=" + source.getUsersObjectClassValue() + ")(" +
                                        source.getUsersIdKey() + "=" + groupCn + "))";
                            }
                        }
                        Collection<LdapEntry> finalUsersList = this.search(search, SecurityEntityType.USER);
                        for (LdapEntry userEntry : finalUsersList) {
                            v.add(newUser(userEntry));
                        }
                    }
                }
            }
            return v;
        } catch (LdapException e) {
            throw new DataSourceException(e, "ldap exception");
        }
    }
}
