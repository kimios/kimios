/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2015  DevLib'
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
package org.kimios.kernel.user.impl.factory.genericldaplayer;

import org.apache.commons.lang.NotImplementedException;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.security.SecurityEntityType;
import org.kimios.kernel.user.Group;
import org.kimios.kernel.user.User;
import org.kimios.kernel.user.UserFactory;
import org.kimios.kernel.user.impl.GenericLDAPImpl;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class GenericLDAPUserFactory extends GenericLDAPFactory implements UserFactory
{


    public GenericLDAPUserFactory(GenericLDAPImpl source)
    {
        this.source = source;
    }

    public boolean authenticate(String uid, String passwd) throws DataSourceException, ConfigException
    {
        try {
            String providerUrl = source.getProviderUrl() + "/" + source.getBaseDn();
            DirContext ctxtDir = null;
            if (!source.isActiveDirectory()) {
                String dn = source.getUsersDn();
                if (dn != null && dn.indexOf(':') == -1) {
                    // single CN
                    ctxtDir = this.getContext(providerUrl, source.getAuthenticationMode(), source.getReferralMode(),
                            getUserString(uid), passwd);
                } else {
                    // multiple CN
                    String[] ns = dn.split(":");
                    for (int i = 0; i < ns.length; i++) {
                        try {
                            ctxtDir = this.getContext(providerUrl, source.getAuthenticationMode(),
                                    source.getReferralMode(), source.getUsersIdKey() + "=" + uid
                                    + "," + ns[i], passwd);
                            break;
                        } catch (javax.naming.AuthenticationException ae) {
                            continue;
                        }
                    }
                }
            } else {
                ctxtDir = this.getContext(providerUrl, source.getAuthenticationMode(), source.getReferralMode(),
                        getUserString(uid), passwd);
            }
            if (ctxtDir != null) {
                ctxtDir.close();
            }
            return true;
        } catch (javax.naming.AuthenticationException e) {
            return false;
        } catch (NamingException e) {
            throw new DataSourceException(e, "LDAP Exception : " + e.getMessage());
        }
    }

    public Vector<User> getUsers() throws DataSourceException, ConfigException
    {
        try {
            Vector<User> v = new Vector<User>();
            List<SearchResult> r =
                    this.search("(objectClass=" + source.getUsersObjectClassValue() + ")", SecurityEntityType.USER);
            for (SearchResult sr : r) {
                Attributes attrs = sr.getAttributes();
                v.add(newUser(attrs));
            }
            return v;
        } catch (javax.naming.AuthenticationException e) {
            throw new ConfigException(e, "LDAP connection failed, please check your settings");
        } catch (NamingException e) {
            throw new DataSourceException(e, "LDAP Exception : " + e.getMessage());
        }
    }

    public User getUser(String uid) throws DataSourceException, ConfigException
    {
        try {
            String s =
                    "(&(objectClass=" + source.getUsersObjectClassValue() + ")(" + source.getUsersIdKey() + "=" + uid +
                            "))";
            List<SearchResult> r = this.search(s, SecurityEntityType.USER);
            if (!r.isEmpty()) {
                return newUser(r.get(0).getAttributes());
            } else {
                return null;
            }
        } catch (javax.naming.AuthenticationException e) {
            e.printStackTrace();
            throw new ConfigException(e, "LDAP connection failed, please check your settings");
        } catch (NamingException e) {
            throw new DataSourceException(e, "LDAP Exception : " + e.getMessage());
        }
    }

    public Vector<User> getUsers(Group group) throws DataSourceException, ConfigException
    {
        try {
            Vector<User> v = new Vector<User>();
            List<SearchResult> r = this.search(
                    "(&(objectClass=" + source.getGroupsObjectClassValue() + ")(" + source.getGroupsIdKey() + "=" +
                            group.getGid()
                            + "))", SecurityEntityType.GROUP);
            for (SearchResult sr : r) {
                Attributes attrs = sr.getAttributes();
                Attribute attr = attrs.get(source.getGroupsMemberKey());
                if (attr != null) {
                    for (int i = 0; i < attr.size(); i++) {
                        String cn = attr.get(i).toString();
                        String search = null;
                        if (source.isActiveDirectory()) {
                            search = "(&(objectClass=" + source.getUsersObjectClassValue() + ")(distinguishedName=" +
                                    cn + "))";
                        } else {

                            if (this.source.getSchemaFullCnUserGroupMatching().equals("true")) {
                                search = "(&(objectClass=" + source.getUsersObjectClassValue() + ")(" + cn.substring(0,
                                                                                                                     (cn.indexOf(","))
                                ) + "))";
                            } else {
                                search = "(&(objectClass=" + source.getUsersObjectClassValue() + ")(" +
                                    source.getUsersIdKey() + "=" + cn + "))";
                            }
                        }
                        List<SearchResult> r2 = this.search(search, SecurityEntityType.USER);
                        for (SearchResult sr2 : r2) {
                            v.add(newUser(sr2.getAttributes()));
                        }
                    }
                }
            }
            return v;
        } catch (javax.naming.AuthenticationException e) {
            throw new ConfigException(e, "LDAP connection failed, please check your settings");
        } catch (NamingException e) {
            throw new DataSourceException(e, "LDAP Exception : " + e.getMessage());
        }
    }

    public void addUserToGroup(User user, Group group) throws DataSourceException, ConfigException
    {

    }

    public void deleteUser(User user) throws DataSourceException, ConfigException
    {

    }

    public void removeUserFromGroup(User user, Group group) throws DataSourceException, ConfigException
    {

    }

    public void saveUser(User user, String password) throws DataSourceException, ConfigException
    {

    }

    public void updateUser(User user, String password) throws DataSourceException, ConfigException
    {

    }

    /**
     * @param attrs
     * @return
     * @throws javax.naming.NamingException
     */
    private User newUser(Attributes attrs) throws NamingException
    {
        User user = new User();
        if (attrs.get(source.getUsersIdKey()) != null) {
            user.setUid(attrs.get(source.getUsersIdKey()).get().toString());
        }
        if (attrs.get(source.getUsersDescriptionKey()) != null) {
            user.setName(attrs.get(source.getUsersDescriptionKey()).get().toString());
        }
        if (attrs.get(source.getUsersMailKey()) != null) {
            user.setMail(attrs.get(source.getUsersMailKey()).get().toString());
            user.setMail(user.getMail() == null || user.getMail().equals("null") ? "" : user.getMail());
        }
        if(attrs.get(source.getUserFirstNameKey()) != null){
            user.setFirstName(attrs.get(source.getUserFirstNameKey()).get().toString());
            user.setFirstName(user.getFirstName() == null || user.getFirstName().equals("null") ? "" : user.getFirstName());
        }
        if(attrs.get(source.getUserLastNameKey()) != null){
            user.setLastName(attrs.get(source.getUserLastNameKey()).get().toString());
            user.setLastName(user.getLastName() == null || user.getLastName().equals("null") ? "" : user.getLastName());

        }
        if(attrs.get(source.getUserPhoneKey()) != null){
            user.setPhoneNumber(attrs.get(source.getUserPhoneKey()).get().toString());
            user.setPhoneNumber(user.getPhoneNumber() == null || user.getPhoneNumber().equals("null") ? "" : user.getPhoneNumber());
        }
        user.setAuthenticationSourceName(this.source.getName());
        return user;
    }

    public Object getAttribute(User user, String attributeName)
            throws DataSourceException, ConfigException
    {
        try {
            String s = "(&(objectClass=" + source.getUsersObjectClassValue() + ")(" + source.getUsersIdKey() + "=" +
                    user.getUid() + "))";
            List<SearchResult> r = this.search(s, SecurityEntityType.USER);
            if (!r.isEmpty()) {
                Attributes attrs = r.get(0).getAttributes();
                return attrs.get(attributeName).get();
            } else {
                return null;
            }
        } catch (javax.naming.AuthenticationException e) {
            throw new ConfigException(e, "LDAP connection failed, please check your settings");
        } catch (NamingException e) {
            throw new DataSourceException(e, "LDAP Exception : " + e.getMessage());
        }
    }

    public String getLDAPAttribute(String userId, String attributeName)
            throws DataSourceException, ConfigException
    {
        try {
            String s = "(&(objectClass=" + source.getUsersObjectClassValue() + ")(" + source.getUsersIdKey() + "=" +
                    userId + "))";
            List<SearchResult> r = this.search(s, SecurityEntityType.USER);
            if (!r.isEmpty()) {
                Attributes attrs = r.get(0).getAttributes();
                Object attributeValue = attrs.get(attributeName).get();

                return attributeValue != null ? attributeValue.toString() : null;
            } else {
                return null;
            }
        } catch (javax.naming.AuthenticationException e) {
            throw new ConfigException(e, "LDAP connection failed, please check your settings");
        } catch (NamingException e) {
            throw new DataSourceException(e, "LDAP Exception : " + e.getMessage());
        }
    }

    public void setAttribute(User user, String attributeName,
            Object attributeValue) throws DataSourceException, ConfigException
    {
        throw new ConfigException("Not Implemented Yet");
    }

    public Map<String, String> getAttributes(User user)
            throws DataSourceException, ConfigException
    {
        try {
            String s = "(&(objectClass=" + source.getUsersObjectClassValue() + ")(" + source.getUsersIdKey() + "=" +
                    user.getUid() + "))";
            List<SearchResult> r = this.search(s, SecurityEntityType.USER);
            if (!r.isEmpty()) {
                Attributes attrs = r.get(0).getAttributes();
                Map<String, String> items = new HashMap<String, String>();
                NamingEnumeration<? extends Attribute> lst = attrs.getAll();
                while (lst.hasMoreElements()) {
                    Attribute attr = lst.nextElement();
                    if (attr.get() != null) {
                        items.put(attr.getID(), attr.get().toString());
                    }
                }

                return items;
            } else {
                return null;
            }
        } catch (javax.naming.AuthenticationException e) {
            throw new ConfigException(e, "LDAP connection failed, please check your settings");
        } catch (NamingException e) {
            throw new DataSourceException(e, "LDAP Exception : " + e.getMessage());
        }
    }

    public User getUserByAttributeValue(String attributeName,
            String attributeValue) throws DataSourceException, ConfigException
    {
        throw new ConfigException("Not Implemented Yet");
    }

    public User getUserByEmail(String emailAddress) throws DataSourceException, ConfigException {
        try{
            String s = "(&(objectClass=" + source.getUsersObjectClassValue() + ")(" + source.getUsersMailKey() + "=" +
                    emailAddress + "))";
            List<SearchResult> r = this.search(s, SecurityEntityType.USER);
            if (!r.isEmpty()) {
                return newUser(r.get(0).getAttributes());
            } else {
                return null;
            }
        }  catch (javax.naming.AuthenticationException e) {
            throw new ConfigException(e, "LDAP connection failed, please check your settings");
        } catch (NamingException e) {
            throw new DataSourceException(e, "LDAP Exception : " + e.getMessage());
        }
    }

    public void addUserEmails(String uid, List<String> emails) {
        throw new NotImplementedException();
    }
}

